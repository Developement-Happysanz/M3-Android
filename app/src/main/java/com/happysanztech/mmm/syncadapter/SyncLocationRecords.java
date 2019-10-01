package com.happysanztech.mmm.syncadapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.happysanztech.mmm.activity.LoginActivity;
import com.happysanztech.mmm.activity.MainActivity;
import com.happysanztech.mmm.activity.SplashScreenActivity;
import com.happysanztech.mmm.bean.database.SQLiteHelper;
import com.happysanztech.mmm.helper.AlertDialogHelper;
import com.happysanztech.mmm.servicehelpers.ServiceHelper;
import com.happysanztech.mmm.serviceinterfaces.IServiceListener;
import com.happysanztech.mmm.utils.MobilizerConstants;

import org.json.JSONException;
import org.json.JSONObject;

import static android.util.Log.d;

public class SyncLocationRecords implements IServiceListener {

    private ServiceHelper serviceHelper;
    private Context context;
    private SQLiteHelper database;
    private String _id = "";

    public SyncLocationRecords(Context context) {
        this.context = context;
        serviceHelper = new ServiceHelper(context);
        serviceHelper.setServiceListener(this);
    }

    public void SyncToServer() {
        callService();
    }

    private void callService() {

        database = new SQLiteHelper(context);

        String userId = "";
        String currentLatitude = "";
        String currentLongitude = "";
        String currentDateAndTime = "";
        String locationAddress = "";
        String dist = "";
        String piaId = "";

        Cursor c = database.getStoredLocationData();
        if (c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    _id = c.getString(0);
                    userId = c.getString(1);
                    currentLatitude = c.getString(2);
                    currentLongitude = c.getString(3);
                    locationAddress = c.getString(4);
                    currentDateAndTime = c.getString(5);
                    dist = c.getString(6);
                    piaId = c.getString(7);

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(MobilizerConstants.KEY_USER_ID, userId);
                        jsonObject.put(MobilizerConstants.PARAMS_LATITUDE, currentLatitude);
                        jsonObject.put(MobilizerConstants.PARAMS_LONGITUDE, currentLongitude);
                        jsonObject.put(MobilizerConstants.PARAMS_DATETIME, currentDateAndTime);
                        jsonObject.put(MobilizerConstants.PARAMS_LOCATION, locationAddress);
                        jsonObject.put(MobilizerConstants.PARAMS_DISTANCE, dist);
                        jsonObject.put(MobilizerConstants.PARAMS_PIA_ID, piaId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String url = MobilizerConstants.BUILD_URL + MobilizerConstants.MTS;
                    serviceHelper.makeGetServiceCall(jsonObject.toString(), url);

                } while (c.moveToNext());
            }
        }
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
                        AlertDialogHelper.showSimpleAlertDialog(context, msg);

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


    @Override
    public void onResponse(JSONObject response) {
        if (validateSignInResponse(response)) {
            database.updateLocationSyncStatus(_id);
            Cursor c = database.getStoredLocationData();
            if (c.getCount() > 0) {
                SyncToServer();
            } else {
                Toast.makeText(context, "All records synced", Toast.LENGTH_LONG).show();
                Intent i = new Intent(context, MainActivity.class);
                context.startActivity(i);
            }
        }
    }

    @Override
    public void onError(String error) {

    }
}
