package com.happysanztech.mmm.servicehelpers;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.happysanztech.mmm.R;
import com.happysanztech.mmm.activity.MainActivity;
import com.happysanztech.mmm.bean.database.SQLiteHelper;
import com.happysanztech.mmm.utils.PreferenceStorage;
import com.happysanztech.mmm.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A bound and started service that is promoted to a foreground service when location updates have
 * been requested and all clients unbind.
 * <p>
 * For apps running in the background on "O" devices, location is computed only once every 10
 * minutes and delivered batched every 30 minutes. This restriction applies even to apps
 * targeting "N" or lower which are run on "O" devices.
 * <p>
 * This sample show how to use a long-running service for location updates. When an activity is
 * bound to this service, frequent location updates are permitted. When the activity is removed
 * from the foreground, the service promotes itself to a foreground service, and location updates
 * continue. When the activity comes back to the foreground, the foreground service stops, and the
 * notification assocaited with that service is removed.
 */
public class LocationUpdatesService extends Service {

    private static final String PACKAGE_NAME =
            "com.happysanztech.mmm.servicehelpers";

    private static final String TAG = LocationUpdatesService.class.getSimpleName();

    /**
     * The name of the channel for notifications.
     */
    private static final String CHANNEL_ID = "channel_01";

    public static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";

    public static final String EXTRA_LOCATION = PACKAGE_NAME + ".location";
    private static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME +
            ".started_from_notification";

    private final IBinder mBinder = new LocalBinder();

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    /**
     * The identifier for the notification displayed for the foreground service.
     */
    private static final int NOTIFICATION_ID = 12345678;

    /**
     * Used to check whether the bound activity has really gone away and not unbound as part of an
     * orientation change. We create a foreground service notification only if the former takes
     * place.
     */
    private boolean mChangingConfiguration = false;

    private NotificationManager mNotificationManager;

    /**
     * Contains parameters used by {@link com.google.android.gms.location.FusedLocationProviderApi}.
     */
    private LocationRequest mLocationRequest;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Callback for changes in location.
     */
    private LocationCallback mLocationCallback;

    private Handler mServiceHandler;

    /**
     * The current location.
     */
    private Location mLocation;

    public LocationUpdatesService() {
    }

    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    public Location previousBestLoc = null;
    SQLiteHelper database;
    private static final int ONE_MINUTES = 1000;
    private boolean isFirstTimePreviousBest = true;
    private boolean isFirstTimeRecordUpdateToServer = true;
    private String gpsStatus = "N";
    double latitude, longitude;
    LocationManager locationManager;
    Location location;
    double globeDistance = 0.00;
    Location globeCurrentBestlocation;


    @Override
    public void onCreate() {
        database = new SQLiteHelper(getApplicationContext());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };

        createLocationRequest();
        getLastLocation();

        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service started");
        boolean startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION,
                false);

        // We got here because the user decided to remove location updates from the notification.
        if (startedFromNotification) {
            removeLocationUpdates();
            stopSelf();
        }
        // Tells the system to not try to recreate the service after it has been killed.
        return START_NOT_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) comes to the foreground
        // and binds with this service. The service should cease to be a foreground service
        // when that happens.
        Log.i(TAG, "in onBind()");
        stopForeground(true);
        mChangingConfiguration = false;
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) returns to the foreground
        // and binds once again with this service. The service should cease to be a foreground
        // service when that happens.
        Log.i(TAG, "in onRebind()");
        stopForeground(true);
        mChangingConfiguration = false;
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Last client unbound from service");

        // Called when the last client (MainActivity in case of this sample) unbinds from this
        // service. If this method is called due to a configuration change in MainActivity, we
        // do nothing. Otherwise, we make this service a foreground service.
        if (!mChangingConfiguration && Utils.requestingLocationUpdates(this)) {
            Log.i(TAG, "Starting foreground service");

            startForeground(NOTIFICATION_ID, getNotification());
        }
        return true; // Ensures onRebind() is called when a client re-binds.
    }

    @Override
    public void onDestroy() {
        mServiceHandler.removeCallbacksAndMessages(null);
    }

    /**
     * Makes a request for location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    public void requestLocationUpdates() {
        Log.i(TAG, "Requesting location updates");
        Utils.setRequestingLocationUpdates(this, true);
        startService(new Intent(getApplicationContext(), LocationUpdatesService.class));
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.myLooper());
        } catch (SecurityException unlikely) {
            Utils.setRequestingLocationUpdates(this, false);
            Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
        }
    }

    /**
     * Removes location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    public void removeLocationUpdates() {
        Log.i(TAG, "Removing location updates");
        callService(globeDistance, globeCurrentBestlocation);
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            Utils.setRequestingLocationUpdates(this, false);
            stopSelf();
        } catch (SecurityException unlikely) {
            Utils.setRequestingLocationUpdates(this, true);
            Log.e(TAG, "Lost location permission. Could not remove updates. " + unlikely);
        }
    }

    /**
     * Returns the {@link NotificationCompat} used as part of the foreground service.
     */
    private Notification getNotification() {
        Intent intent = new Intent(this, LocationUpdatesService.class);

        CharSequence text = Utils.getLocationText(mLocation);

        // Extra to help us figure out if we arrived in onStartCommand via the notification or not.
        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);

        // The PendingIntent that leads to a call to onStartCommand() in this service.
        PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // The PendingIntent to launch activity.
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .addAction(R.drawable.ic_launch, getString(R.string.launch_activity),
                        activityPendingIntent)
                .addAction(R.drawable.ic_cancel, getString(R.string.remove_location_updates),
                        servicePendingIntent)
                .setContentText(text)
                .setContentTitle(Utils.getLocationTitle(this))
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(text)
                .setWhen(System.currentTimeMillis());

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }

        return builder.build();
    }

    private void getLastLocation() {
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                mLocation = task.getResult();
                            } else {
                                Log.w(TAG, "Failed to get location.");
                            }
                        }
                    });
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission." + unlikely);
        }
    }

    private void onNewLocation(Location location) {
        Log.i(TAG, "New location: " + location);

        mLocation = location;
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnable && !isNetworkEnable) {

            gpsStatus = "N";

        } else {

            gpsStatus = "Y";
        }
        // Notify anyone listening for broadcasts about the new location.
        Intent intent = new Intent(ACTION_BROADCAST);
        intent.putExtra(EXTRA_LOCATION, location);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

        // Update notification content if running as a foreground service.
        if (serviceIsRunningInForeground(this)) {
            mNotificationManager.notify(NOTIFICATION_ID, getNotification());
        }

        if (isBetterLocation(location, previousBestLoc)) {
            location.getLatitude();
            location.getLongitude();
            String latitude = "";
            String longitude = "";
            latitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());

            database.deleteAllCurrentBestLocation();

            long l = database.current_best_location_insert(latitude, longitude);
            //If everything went fine lets get latitude and longitude
            intent.putExtra("Latitude", location.getLatitude());
            intent.putExtra("Longitude", location.getLongitude());
            intent.putExtra("Provider", location.getProvider());
            sendBroadcast(intent);
            if (isFirstTimePreviousBest) {
                database.deleteAllPreviousBestLocation();
                long l1 = database.previous_best_location_insert(latitude, longitude);
                previousBestLoc = location;
                isFirstTimePreviousBest = false;
            }
        }


    }

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
            if (isGPSEnable) {
                if (distance >= 0.01) {
                    if (!checkUserId.equalsIgnoreCase("") || checkUserId != null) {
//                        Toast.makeText(this, "Location sent", Toast.LENGTH_LONG).show();
                        isFirstTimePreviousBest = false;
                        database.deleteAllPreviousBestLocation();
                        long l = database.previous_best_location_insert("" + currentBestlocation.getLatitude(), "" + currentBestlocation.getLongitude());
                        previousBestLoc = currentBestlocation;
                        callService(distance, currentBestlocation);
                        globeDistance = distance;
                        globeCurrentBestlocation = currentBestlocation;
                    }
                } else {
//                    if (distance == 0.00) {
                    if (!checkUserId.equalsIgnoreCase("") || checkUserId != null) {
                        if (isFirstTimeRecordUpdateToServer) {
//                            Toast.makeText(this, "Location sent", Toast.LENGTH_LONG).show();
                            isFirstTimeRecordUpdateToServer = false;
                            database.deleteAllPreviousBestLocation();
                            long l = database.previous_best_location_insert("" + currentBestlocation.getLatitude(), "" + currentBestlocation.getLongitude());
//                            previousBestLoc = currentBestlocation;
                            callService(distance, currentBestlocation);
                            globeDistance = distance;
                            globeCurrentBestlocation = currentBestlocation;
                        }
//                        }
                    }
                }
                return true;

                // If the new location is more than two minutes older, it must be worse
            }
        } else if (isSignificantlyOlder) {
            if (isGPSEnable) {
//                if (distance == 0.00) {
                if (!checkUserId.equalsIgnoreCase("") || checkUserId != null) {
                    if (isFirstTimeRecordUpdateToServer) {
//                        Toast.makeText(this, "Location sent", Toast.LENGTH_LONG).show();
                        isFirstTimeRecordUpdateToServer = false;
                        database.deleteAllPreviousBestLocation();
                        long l = database.previous_best_location_insert("" + currentBestlocation.getLatitude(), "" + currentBestlocation.getLongitude());
                        previousBestLoc = currentBestlocation;
                        callService(distance, currentBestlocation);
                        globeDistance = distance;
                        globeCurrentBestlocation = currentBestlocation;
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

    private void callService(double distance, Location location) {
        if (location != null) {
            double currentLatitude = location.getLatitude();
            String lat = Double.toString(currentLatitude);
            double currentLongitude = location.getLongitude();
            String lon = Double.toString(currentLongitude);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateandTime = sdf.format(new Date());
            String locationAddress = getCompleteAddressString(currentLatitude, currentLongitude);
            String dist = Double.toString(distance);
            String TrackingStatus = PreferenceStorage.getTrackStatus(getApplicationContext());

            long x = database.store_location_data_insert(PreferenceStorage.getUserId(getApplicationContext()), lat, lon, locationAddress,
                    currentDateandTime, dist, PreferenceStorage.getPIAId(getApplicationContext()), gpsStatus, TrackingStatus);

            System.out.println("Stored Id : " + x);
            PreferenceStorage.saveTrackStatus(getApplicationContext(), "");
        }
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


    /**
     * Sets the location request parameters.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public LocationUpdatesService getService() {
            return LocationUpdatesService.this;
        }
    }

    /**
     * Returns true if this is a foreground service.
     *
     * @param context The {@link Context}.
     */
    public boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            if (getClass().getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }
            }
        }
        return false;
    }
}