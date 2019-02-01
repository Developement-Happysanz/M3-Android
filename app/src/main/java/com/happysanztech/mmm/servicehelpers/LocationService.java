package com.happysanztech.mmm.servicehelpers;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


import com.happysanztech.mmm.activity.MainActivity;
import com.happysanztech.mmm.bean.database.SQLiteHelper;
import com.happysanztech.mmm.helper.AlertDialogHelper;
import com.happysanztech.mmm.serviceinterfaces.IServiceListener;
import com.happysanztech.mmm.utils.MobilizerConstants;
import com.happysanztech.mmm.utils.PreferenceStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.util.Log.d;


/**
 * Created by Admin on 07-01-2018.
 */

public class LocationService extends Service implements IServiceListener {

    public static final String BROADCAST_ACTION = "Hello World";
    private static final int ONE_MINUTES = 500 * 5 * 2;
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLoc = null;
    private ServiceHelper serviceHelper;
    private boolean isFirstTimePreviousBest = true;
    private boolean isFirstTimeRecordUpdateToServer = true;
    private boolean isGPSEnabled = true;
    SQLiteHelper database;

    Intent intent;
    int counter = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        database = new SQLiteHelper(getApplicationContext());
    }

    @Override
    public void onStart(Intent intent, int startId) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, listener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }*/

    protected boolean isBetterLocation(Location currentBestlocation, Location previousBestLocation) {
        if (previousBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        String checkUserId = PreferenceStorage.getUserId(getApplicationContext());
        double currentBestLat = 0.00;
        double currentBestLong = 0.00;
        double previousBestLat = 0.00;
        double previousBestLong = 0.00;

        Cursor c = database.getCurrentBestLocationTopValue();
        if (c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {

                    currentBestLat = Double.parseDouble(c.getString(1));
                    currentBestLong = Double.parseDouble(c.getString(2));

                } while (c.moveToNext());
            }
        }

        Cursor c1 = database.getPreviousBestLocationTopValue();
        if (c1.getCount() > 0) {
            if (c1.moveToFirst()) {
                do {

                    previousBestLat = Double.parseDouble(c1.getString(1));
                    previousBestLong = Double.parseDouble(c1.getString(2));

                } while (c1.moveToNext());
            }
        }
        double distance = 0.00;
        if (currentBestLat != 0.00 || currentBestLong != 0.00 || previousBestLat != 0.00 ||
                previousBestLong != 0.00) {

            /*Location loc1 = new Location("");
            loc1.setLatitude(previousBestLat);
            loc1.setLongitude(previousBestLong);

            Location loc2 = new Location("");
            loc2.setLatitude(currentBestLat);
            loc2.setLongitude(currentBestLong);

            float distanceInMeters = loc1.distanceTo(loc2);*/

            distance = distance(previousBestLat, previousBestLong, currentBestLat, currentBestLong);
//            distance = distanceInMeters;
        }
//        Toast.makeText(getApplicationContext(), "Latitude : " + currentLatitude + " " + "Longitude : " + currentLongitude, Toast.LENGTH_SHORT).show();

        // Check whether the new location fix is newer or older
        long timeDelta = currentBestlocation.getTime() - previousBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > ONE_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -ONE_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            if (isGPSEnabled) {
                if (distance > 0.01) {
                    if (!checkUserId.equalsIgnoreCase("") || checkUserId != null) {
                        Toast.makeText(this, "Location sent", Toast.LENGTH_LONG).show();
                        isFirstTimePreviousBest = false;
                        database.deleteAllPreviousBestLocation();
                        long l = database.previous_best_location_insert("" + currentBestlocation.getLatitude(), "" + currentBestlocation.getLongitude());
                        previousBestLoc = currentBestlocation;
                        callService(distance, currentBestlocation);
                    }
                } else {
//                    if (distance == 0.00) {
                        if (!checkUserId.equalsIgnoreCase("") || checkUserId != null) {
                            if (isFirstTimeRecordUpdateToServer) {
                                Toast.makeText(this, "Location sent", Toast.LENGTH_LONG).show();
                                isFirstTimeRecordUpdateToServer = false;
                                database.deleteAllPreviousBestLocation();
                                long l = database.previous_best_location_insert("" + currentBestlocation.getLatitude(), "" + currentBestlocation.getLongitude());
//                            previousBestLoc = currentBestlocation;
                                callService(distance, currentBestlocation);
                            }
//                        }
                    }
                }
                return true;

                // If the new location is more than two minutes older, it must be worse
            }
        } else if (isSignificantlyOlder) {
            if (isGPSEnabled) {
//                if (distance == 0.00) {
                    if (!checkUserId.equalsIgnoreCase("") || checkUserId != null) {
                        if (isFirstTimeRecordUpdateToServer) {
                            Toast.makeText(this, "Location sent", Toast.LENGTH_LONG).show();
                            isFirstTimeRecordUpdateToServer = false;
                            database.deleteAllPreviousBestLocation();
                            long l = database.previous_best_location_insert("" + currentBestlocation.getLatitude(), "" + currentBestlocation.getLongitude());
                            previousBestLoc = currentBestlocation;
                            callService(distance, currentBestlocation);
                        }
//                    }
                }
                return false;
            }
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (currentBestlocation.getAccuracy() - previousBestLoc.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(currentBestlocation.getProvider(),
                previousBestLoc.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }


    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");
//        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        locationManager.removeUpdates(listener);
//        startService(new Intent(this, LocationService.class));
    }

    public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }

    public class MyLocationListener implements LocationListener {

        public void onLocationChanged(final Location loc) {
            Log.i("***********************", "Location changed");
            if (isBetterLocation(loc, previousBestLoc)) {
                loc.getLatitude();
                loc.getLongitude();
                String latitude = "";
                String longitude = "";
                latitude = String.valueOf(loc.getLatitude());
                longitude = String.valueOf(loc.getLongitude());

                database.deleteAllCurrentBestLocation();

                long l = database.current_best_location_insert(latitude, longitude);
                //If everything went fine lets get latitude and longitude
                intent.putExtra("Latitude", loc.getLatitude());
                intent.putExtra("Longitude", loc.getLongitude());
                intent.putExtra("Provider", loc.getProvider());
                sendBroadcast(intent);
                if (isFirstTimePreviousBest) {
                    database.deleteAllPreviousBestLocation();
                    long l1 = database.previous_best_location_insert(latitude, longitude);
                    previousBestLoc = loc;
                    isFirstTimePreviousBest = false;
                }
            }
        }

        public void onProviderDisabled(String provider) {

            Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
            isGPSEnabled = false;

        }

        public void onProviderEnabled(String provider) {

            Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
            isGPSEnabled = true;
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }

    @Override
    public void onResponse(JSONObject response) {
        if (validateSignInResponse(response)) {

        }
    }

    @Override
    public void onError(String error) {

    }

    private boolean validateSignInResponse(JSONObject response) {
        boolean signInSuccess = false;
        if ((response != null)) {
            try {
                String status = response.getString("status");
                String msg = response.getString(MobilizerConstants.PARAM_MESSAGE);
                d("HI", "status val" + status + "msg" + msg);

                if ((status != null)) {
                    if (((status.equalsIgnoreCase("activationError")) || (status.equalsIgnoreCase("alreadyRegistered")) ||
                            (status.equalsIgnoreCase("notRegistered")) || (status.equalsIgnoreCase("error")))) {
                        signInSuccess = false;
                        d("HI", "Show error dialog");
                        AlertDialogHelper.showSimpleAlertDialog(getApplicationContext(), msg);

                    } else {
                        signInSuccess = true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return signInSuccess;
    }

    private void callService(double distance, Location location) {
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        String locationAddress = getCompleteAddressString(currentLatitude, currentLongitude);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(MobilizerConstants.KEY_USER_ID, PreferenceStorage.getUserId(getApplicationContext()));
            jsonObject.put(MobilizerConstants.PARAMS_LATITUDE, currentLatitude);
            jsonObject.put(MobilizerConstants.PARAMS_LONGITUDE, currentLongitude);
            jsonObject.put(MobilizerConstants.PARAMS_DATETIME, currentDateandTime);
            jsonObject.put(MobilizerConstants.PARAMS_LOCATION, locationAddress);
            jsonObject.put(MobilizerConstants.PARAMS_DISTANCE, distance);
            jsonObject.put(MobilizerConstants.PARAMS_PIA_ID, PreferenceStorage.getPIAId(getApplicationContext()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = MobilizerConstants.BUILD_URL + MobilizerConstants.MTS;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append(" ");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("MyCurrentloctionaddress", strReturnedAddress.toString());
            } else {
                Log.w("MyCurrentloctionaddress", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("MyCurrentloctionaddress", "Canont get Address!");
        }
        return strAdd;
    }

    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 6371; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double dist = earthRadius * c;

        double roundDist = round(dist, 6);

        return roundDist;  // output distance, in MILES
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
