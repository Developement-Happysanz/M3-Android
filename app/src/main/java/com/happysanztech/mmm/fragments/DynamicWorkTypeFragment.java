package com.happysanztech.mmm.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.happysanztech.mmm.R;
import com.happysanztech.mmm.activity.MobiliserWorkTypeDetailActivity;
import com.happysanztech.mmm.adapter.WorkDetailsListAdapter;
import com.happysanztech.mmm.bean.support.WorkDetails;
import com.happysanztech.mmm.bean.support.WorkDetailsList;
import com.happysanztech.mmm.bean.support.WorkMonth;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.util.Log.d;

public class DynamicWorkTypeFragment extends Fragment implements IServiceListener, AdapterView.OnItemClickListener, DialogClickListener {
    Context context;
    private View view;
    private static ArrayList<WorkMonth> WorkMonthArrayList;
    private ArrayList<WorkDetails> serviceArrayList;
    private int val;
    private WorkDetailsListAdapter serviceListAdapter;
    private static final String TAG = DynamicWorkTypeFragment.class.getName();
    private String subCatId = "";
    private ServiceHelper serviceHelper;
    private int totalCount = 0, checkrun = 0;
    private  boolean isLoadingForFirstTime = true;
    private ProgressDialogHelper progressDialogHelper;
    private ListView loadMoreListView;
    private Boolean msgErr = false;
    private Boolean noService = false;
    private String res = "";
    private String id = "";

    private static boolean _hasLoadedOnce = false; // your boolean field

    public static DynamicWorkTypeFragment newInstance(int val, ArrayList<WorkMonth> categoryArrayList) {
        DynamicWorkTypeFragment fragment = new DynamicWorkTypeFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", val);
        fragment.setArguments(args);
        WorkMonthArrayList = categoryArrayList;
        if (String.valueOf(val).equalsIgnoreCase("1")) {
            _hasLoadedOnce = true;
        } else {
            _hasLoadedOnce = false;
        }
        return fragment;
    }


    @Override
    public void setUserVisibleHint(boolean isFragmentVisible_) {
        super.setUserVisibleHint(true);

        if (this.isVisible()) {
            // we check that the fragment is becoming visible
//            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
            if (isFragmentVisible_ && !_hasLoadedOnce) {
                loadDayMonth();
                _hasLoadedOnce = true;
                if(noService) {
                    AlertDialogHelper.showSimpleAlertDialog(view.getContext(), "No service found");
                    noService = false;
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list, container, false);
        serviceHelper = new ServiceHelper(view.getContext());
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(view.getContext());
        val = getArguments().getInt("someInt", 0);
//        categories = WorkMonthList.getCategoryArrayList();
//        categories = WorkMonthList.getCategoryArrayList();
        subCatId = WorkMonthArrayList.get(val).getmonth_id();
//        PreferenceStorage.saveSubCatClick(view.getContext(), subCatId);
//        rateCount = (TextView) view.findViewById(R.id.service_count);
//        summary = (TextView) view.findViewById(R.id.view_summary);
//        summary.setOnClickListener(this);
//        c = view.findViewById(R.id.c);
//        c.setText("" + subCatId);
        loadMoreListView = view.findViewById(R.id.workList);
        loadMoreListView.setOnItemClickListener(this);
        loadDayMonth();
        return view;
    }

    @Override
    public void onResponse(JSONObject response) {
        progressDialogHelper.hideProgressDialog();
        if (validateSignInResponse(response)) {
            try {
                if (res.equalsIgnoreCase("day")) {
                    JSONArray getData = response.getJSONArray("result");
//                loadMembersList(getData.length());
                    Gson gson = new Gson();
                    WorkDetailsList serviceList = gson.fromJson(response.toString(), WorkDetailsList.class);
                    serviceArrayList = new ArrayList<>();
                    if (serviceList.getCategoryArrayList() != null && serviceList.getCategoryArrayList().size() > 0) {
                        totalCount = serviceList.getCount();
//                    this.categoryArrayList.addAll(WorkMonthList.getCategoryArrayList());
                        isLoadingForFirstTime = false;
                        updateListAdapter(serviceList.getCategoryArrayList());
                    } else {
                        if (serviceArrayList != null) {
                            serviceArrayList.clear();
                            updateListAdapter(serviceList.getCategoryArrayList());
//                            serviceListAdapter = new MainServiceListAdapter(view.getContext(), this.serviceArrayList);
//                            loadMoreListView.setAdapter(serviceListAdapter);
                        }
                    }
                } else if (res.equalsIgnoreCase("clear")) {
//                    PreferenceStorage.saveServiceCount(view.getContext(), "");
//                    PreferenceStorage.saveRate(view.getContext(), "");
//                    PreferenceStorage.savePurchaseStatus(view.getContext(), false);
//                    loadCat();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "Error while sign In");
        }
    }

    private void clearCart() {
        res = "clear";
        id = PreferenceStorage.getUserId(view.getContext());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(MobilizerConstants.KEY_USER_ID, id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
//        String url = MobilizerConstants.BUILD_URL + MobilizerConstants.CLEAR_CART;
//        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
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
                        if (msg.equalsIgnoreCase("Services not found")){
                            msgErr = true;
                            noService = true;
                        }
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
    public void onError(String error) {

    }

    private void loadDayMonth() {
        res = "day";
        JSONObject jsonObject = new JSONObject();
//        String id = "";
        String mobid = "";
        id = PreferenceStorage.getUserId(view.getContext());
//        mobid = PreferenceStorage.getMobId(view.getContext());
        try {
            jsonObject.put(MobilizerConstants.KEY_USER_ID, id);
            jsonObject.put(MobilizerConstants.KEY_MOBILISER_ID, id);
            jsonObject.put(MobilizerConstants.PARAMS_MONTH_ID, subCatId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = MobilizerConstants.BUILD_URL + MobilizerConstants.GET_WORK;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    protected void updateListAdapter(ArrayList<WorkDetails> serviceArrayList) {
        if (msgErr) {
//            AlertDialogHelper.showSimpleAlertDialog(view.getContext(), "No Services found");
        } else {
            msgErr = false;
            this.serviceArrayList.clear();
            this.serviceArrayList.addAll(serviceArrayList);
            if (serviceListAdapter == null) {
                serviceListAdapter = new WorkDetailsListAdapter(view.getContext(), this.serviceArrayList);
                loadMoreListView.setAdapter(serviceListAdapter);
            } else {
                serviceListAdapter.notifyDataSetChanged();
            }
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onEvent list item clicked" + position);
        WorkDetails service = null;
        if ((serviceListAdapter != null) && (serviceListAdapter.ismSearching())) {
            Log.d(TAG, "while searching");
            int actualindex = serviceListAdapter.getActualEventPos(position);
            Log.d(TAG, "actual index" + actualindex);
            service = serviceArrayList.get(actualindex);
        } else {
            service = serviceArrayList.get(position);
        }
        String start = service.getattendance_date();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = formatter.parse(start);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String a = android.text.format.DateFormat.format("M", date).toString();
        PreferenceStorage.saveMonthId(view.getContext(), a);

        Intent intent = new Intent(view.getContext(), MobiliserWorkTypeDetailActivity.class);
        intent.putExtra("serviceObj", service);
        startActivity(intent);
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }
}