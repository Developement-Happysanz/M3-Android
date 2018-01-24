package com.happysanztech.mmm.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.happysanztech.mmm.R;
import com.happysanztech.mmm.adapter.BatchDataListAdapter;
import com.happysanztech.mmm.bean.support.BatchData;
import com.happysanztech.mmm.bean.support.BatchDataList;
import com.happysanztech.mmm.bean.support.TradeData;
import com.happysanztech.mmm.helper.AlertDialogHelper;
import com.happysanztech.mmm.helper.ProgressDialogHelper;
import com.happysanztech.mmm.interfaces.DialogClickListener;
import com.happysanztech.mmm.servicehelpers.ServiceHelper;
import com.happysanztech.mmm.serviceinterfaces.IServiceListener;
import com.happysanztech.mmm.utils.CommonUtils;
import com.happysanztech.mmm.utils.MobilizerConstants;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.util.Log.d;

/**
 * Created by Admin on 08-01-2018.
 */

public class BatchDetailsActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = BatchDetailsActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private TradeData tradeData;
    ListView loadMoreListView;
    BatchDataListAdapter batchDataListAdapter;
    ArrayList<BatchData> batchDataArrayList;
    protected boolean isLoadingForFirstTime = true;
    Handler mHandler = new Handler();
    int pageNumber = 0, totalCount = 0;
    private ImageView imBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_details);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        tradeData = (TradeData) getIntent().getSerializableExtra("eventObj");
        loadMoreListView = findViewById(R.id.listView_batch);
        loadMoreListView.setOnItemClickListener(this);
        batchDataArrayList = new ArrayList<>();
        imBack = findViewById(R.id.back_tic_his);
        imBack.setOnClickListener(this);

        populateData();
    }

    private void populateData() {

        if (CommonUtils.isNetworkAvailable(this)) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(MobilizerConstants.PARAMS_TRADE_ID, tradeData.getId());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            String url = MobilizerConstants.BUILD_URL + MobilizerConstants.BATCH;
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
                    BatchDataList batchDataList = gson.fromJson(response.toString(), BatchDataList.class);
                    if (batchDataList.getBatchData() != null && batchDataList.getBatchData().size() > 0) {
                        totalCount = batchDataList.getCount();
                        isLoadingForFirstTime = false;
                        updateListAdapter(batchDataList.getBatchData());
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
                AlertDialogHelper.showSimpleAlertDialog(BatchDetailsActivity.this, error);
            }
        });
    }

    protected void updateListAdapter(ArrayList<BatchData> batchDataArrayList) {
        this.batchDataArrayList.addAll(batchDataArrayList);
        if (batchDataListAdapter == null) {
            batchDataListAdapter = new BatchDataListAdapter(this, this.batchDataArrayList);
            loadMoreListView.setAdapter(batchDataListAdapter);
        } else {
            batchDataListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
