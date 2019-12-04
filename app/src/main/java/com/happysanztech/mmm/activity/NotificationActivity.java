package com.happysanztech.mmm.activity;

import android.os.Bundle;
import android.os.Handler;

import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.happysanztech.mmm.R;
import com.happysanztech.mmm.adapter.NotificationListAdapter;
import com.happysanztech.mmm.bean.support.NotificationData;
import com.happysanztech.mmm.bean.support.NotificationDataList;
import com.happysanztech.mmm.helper.AlertDialogHelper;
import com.happysanztech.mmm.helper.ProgressDialogHelper;
import com.happysanztech.mmm.interfaces.DialogClickListener;
import com.happysanztech.mmm.servicehelpers.ServiceHelper;
import com.happysanztech.mmm.serviceinterfaces.IServiceListener;
import com.happysanztech.mmm.utils.CommonUtils;
import com.happysanztech.mmm.utils.MobilizerConstants;
import com.happysanztech.mmm.utils.PreferenceStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.util.Log.d;


/**
 * Created by Admin on 04-01-2018.
 */

public class NotificationActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener {

    private static final String TAG = BatchDetailsActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    //    private TradeData tradeData;
    ListView loadMoreListView;
    NotificationListAdapter notificationListAdapter;
    ArrayList<NotificationData> notificationArrayList;
    protected boolean isLoadingForFirstTime = true;
    Handler mHandler = new Handler();
    int pageNumber = 0, totalCount = 0;
    private ImageView imBack;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        loadMoreListView = findViewById(R.id.listView_batch);
        notificationArrayList = new ArrayList<>();

        imBack = findViewById(R.id.back_tic_his);
        imBack.setOnClickListener(this);

        populateData();
    }

    private void populateData() {

        if (CommonUtils.isNetworkAvailable(this)) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(MobilizerConstants.KEY_USER_ID, PreferenceStorage.getUserId(getApplicationContext()));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            String url = MobilizerConstants.BUILD_URL + MobilizerConstants.NOTIFICATION_LIST;
            serviceHelper.makeGetServiceCall(jsonObject.toString(), url);


        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection");
        }
    }

    @Override
    public void onClick(View v) {
        if (v == imBack) {
            finish();
        }
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    private boolean validateSignInResponse(JSONObject response) {
        boolean signInSuccess = false;
        if ((response != null)) {
            try {
                String status = response.getString("status");
                String msg = response.getString(MobilizerConstants.PARAM_MESSAGE);
                d(TAG, "status val" + status + "msg" + msg);

                if ((status != null)) {
                    if (((status.equalsIgnoreCase("activationError")) || (status.equalsIgnoreCase("alreadyRegistered")) ||
                            (status.equalsIgnoreCase("notRegistered")) || (status.equalsIgnoreCase("error")))) {
                        signInSuccess = false;
                        d(TAG, "Show error dialog");
                        AlertDialogHelper.showSimpleAlertDialog(this, msg);

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
    public void onResponse(final JSONObject response) {
        progressDialogHelper.hideProgressDialog();

        if (validateSignInResponse(response)) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
//                    progressDialogHelper.hideProgressDialog();

                    Gson gson = new Gson();
                    NotificationDataList notificationDataList = gson.fromJson(response.toString(), NotificationDataList.class);
                    if (notificationDataList.getNotificationData() != null && notificationDataList.getNotificationData().size() > 0) {
                        totalCount = notificationDataList.getCount();
                        isLoadingForFirstTime = false;
                        updateListAdapter(notificationDataList.getNotificationData());
                    }
                }
            });
        }
    }

    @Override
    public void onError(final String error) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                progressDialogHelper.hideProgressDialog();
                AlertDialogHelper.showSimpleAlertDialog(NotificationActivity.this, error);
            }
        });
    }

    protected void updateListAdapter(ArrayList<NotificationData> notificationArrayList) {
        this.notificationArrayList.addAll(notificationArrayList);
        if (notificationListAdapter == null) {
            notificationListAdapter = new NotificationListAdapter(this, this.notificationArrayList);
            loadMoreListView.setAdapter(notificationListAdapter);
        } else {
            notificationListAdapter.notifyDataSetChanged();
        }
    }
}
