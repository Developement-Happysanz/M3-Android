package com.happysanztech.mmm.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.happysanztech.mmm.R;
import com.happysanztech.mmm.activity.BatchDetailsActivity;
import com.happysanztech.mmm.adapter.TradeDataListAdapter;
import com.happysanztech.mmm.bean.support.TradeData;
import com.happysanztech.mmm.bean.support.TradeDataList;
import com.happysanztech.mmm.helper.AlertDialogHelper;
import com.happysanztech.mmm.helper.ProgressDialogHelper;
import com.happysanztech.mmm.interfaces.DialogClickListener;
import com.happysanztech.mmm.servicehelpers.ServiceHelper;
import com.happysanztech.mmm.serviceinterfaces.IServiceListener;
import com.happysanztech.mmm.utils.MobilizerConstants;
import com.happysanztech.mmm.utils.PreferenceStorage;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.util.Log.d;


/**
 * Created by Admin on 03-01-2018.
 */

public class TradeFragment extends Fragment implements View.OnClickListener, IServiceListener, DialogClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = "TradeFragment";
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    ListView loadMoreListView;
    TradeDataListAdapter tradeDataListAdapter;
    ArrayList<TradeData> tradeDataArrayList;
    protected boolean isLoadingForFirstTime = true;
    Handler mHandler = new Handler();
    int pageNumber = 0, totalCount = 0;

    public TradeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trades, container, false);
        serviceHelper = new ServiceHelper(getActivity());
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(getActivity());
        loadMoreListView = rootView.findViewById(R.id.listView_trades);
        loadMoreListView.setOnItemClickListener(this);
        tradeDataArrayList = new ArrayList<>();
        loadTrades();
        return rootView;
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
                        AlertDialogHelper.showSimpleAlertDialog(getActivity(), msg);

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
                    TradeDataList tradeDataList = gson.fromJson(response.toString(), TradeDataList.class);
                    if (tradeDataList.getTradeData() != null && tradeDataList.getTradeData().size() > 0) {
                        totalCount = tradeDataList.getCount();
                        isLoadingForFirstTime = false;
                        updateListAdapter(tradeDataList.getTradeData());
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
                AlertDialogHelper.showSimpleAlertDialog(getActivity(), error);
            }
        });

    }

    protected void updateListAdapter(ArrayList<TradeData> tradeDataArrayList) {
        this.tradeDataArrayList.addAll(tradeDataArrayList);
        if (tradeDataListAdapter == null) {
            tradeDataListAdapter = new TradeDataListAdapter(getContext(), this.tradeDataArrayList);
            loadMoreListView.setAdapter(tradeDataListAdapter);
        } else {
            tradeDataListAdapter.notifyDataSetChanged();
        }
    }

    private void loadTrades() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(MobilizerConstants.KEY_USER_ID, PreferenceStorage.getUserId(getActivity()));
            jsonObject.put(MobilizerConstants.KEY_PIA_ID, PreferenceStorage.getPIAId(getActivity()));

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
        /*Intent intent = new Intent(getActivity(), BatchDetailsActivity.class);
        intent.putExtra("eventObj", tradeData);
        startActivity(intent);*/
    }
}
