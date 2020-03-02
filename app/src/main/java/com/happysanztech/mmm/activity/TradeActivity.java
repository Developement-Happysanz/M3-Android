package com.happysanztech.mmm.activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.happysanztech.mmm.R;
import com.happysanztech.mmm.adapter.TradeDataListAdapter;
import com.happysanztech.mmm.bean.database.SQLiteHelper;
import com.happysanztech.mmm.bean.support.TradeData;
import com.happysanztech.mmm.bean.support.TradeDataList;
import com.happysanztech.mmm.helper.AlertDialogHelper;
import com.happysanztech.mmm.helper.ProgressDialogHelper;
import com.happysanztech.mmm.interfaces.DialogClickListener;
import com.happysanztech.mmm.servicehelpers.ServiceHelper;
import com.happysanztech.mmm.serviceinterfaces.IServiceListener;
import com.happysanztech.mmm.utils.MobilizerConstants;
import com.happysanztech.mmm.utils.PreferenceStorage;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.util.Log.d;


/**
 * Created by Admin on 03-01-2018.
 */

public class TradeActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = "TradeActivity";
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    ListView loadMoreListView;
    TradeDataListAdapter tradeDataListAdapter;
    ArrayList<TradeData> tradeDataArrayList;
    protected boolean isLoadingForFirstTime = true;
    Handler mHandler = new Handler();
    int pageNumber = 0, totalCount = 0;
    SQLiteHelper database;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_trades);
        database = new SQLiteHelper(getApplicationContext());
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        loadMoreListView = findViewById(R.id.listView_trades);
        loadMoreListView.setOnItemClickListener(this);
        tradeDataArrayList = new ArrayList<>();
        loadTrades();
        findViewById(R.id.back_tic_his).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {

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
            Gson gson = new Gson();
            TradeDataList tradeDataList = gson.fromJson(response.toString(), TradeDataList.class);
            if (tradeDataList.getTradeData() != null && tradeDataList.getTradeData().size() > 0) {
                totalCount = tradeDataList.getCount();
                isLoadingForFirstTime = false;
                updateListAdapter(tradeDataList.getTradeData());
            }
            try {
                JSONArray data = response.getJSONArray("Trades");
                database.deleteAllStoredTradeData();
                for (int i = 0; i < data.length(); i++) {
                    String tradeID = data.getJSONObject(i).getString("id");
                    String tradeName = data.getJSONObject(i).getString("trade_name");
                    long l = database.store_trade_data_insert(tradeID, tradeName);
                    System.out.println("Stored Id : " + l);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(final String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(this, error);
    }

    protected void updateListAdapter(ArrayList<TradeData> tradeDataArrayList) {
        this.tradeDataArrayList.addAll(tradeDataArrayList);
        if (tradeDataListAdapter == null) {
            tradeDataListAdapter = new TradeDataListAdapter(this, this.tradeDataArrayList);
            loadMoreListView.setAdapter(tradeDataListAdapter);
        } else {
            tradeDataListAdapter.notifyDataSetChanged();
        }
    }

    private void loadTrades() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(MobilizerConstants.KEY_USER_ID, PreferenceStorage.getUserId(this));
            jsonObject.put(MobilizerConstants.KEY_PIA_ID, PreferenceStorage.getPIAId(this));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = MobilizerConstants.BUILD_URL + MobilizerConstants.TRADES;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onEvent list item click" + position);
        TradeData tradeData = null;
        if ((tradeDataListAdapter != null) && (tradeDataListAdapter.ismSearching())) {
            Log.d(TAG, "while searching");
            int actualindex = tradeDataListAdapter.getActualEventPos(position);
            Log.d(TAG, "actual index" + actualindex);
            tradeData = tradeDataArrayList.get(actualindex);
        } else {
            tradeData = tradeDataArrayList.get(position);
        }
        /*Intent intent = new Intent(this, BatchDetailsFragment.class);
        intent.putExtra("eventObj", tradeData);
        startActivity(intent);*/
    }
}
