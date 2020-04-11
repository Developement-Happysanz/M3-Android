package com.happysanztech.mmm.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.happysanztech.mmm.R;
import com.happysanztech.mmm.adapter.WorkTypeTabAdapter;
import com.happysanztech.mmm.bean.support.WorkMonth;
import com.happysanztech.mmm.bean.support.WorkMonthList;
import com.happysanztech.mmm.helper.AlertDialogHelper;
import com.happysanztech.mmm.helper.ProgressDialogHelper;
import com.happysanztech.mmm.interfaces.DialogClickListener;
import com.happysanztech.mmm.servicehelpers.ServiceHelper;
import com.happysanztech.mmm.serviceinterfaces.IServiceListener;
import com.happysanztech.mmm.utils.CommonUtils;
import com.happysanztech.mmm.utils.MobilizerConstants;
import com.happysanztech.mmm.utils.PreferenceStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.util.Log.d;

public class MobiliserWorkTypeActivity extends AppCompatActivity implements IServiceListener, DialogClickListener, AdapterView.OnItemClickListener, View.OnClickListener {

    private static final String TAG = MobiliserWorkTypeActivity.class.getName();

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private Handler mHandler = new Handler();
    int totalCount = 0, checkrun = 0;
    protected boolean isLoadingForFirstTime = true;
    private ArrayList<WorkMonth> mobilizerArrayList;
    private ListView loadMoreListView;
    private ArrayList yearList = new ArrayList();
    private ArrayAdapter<String> dataAdapter3;
    private EditText yearSelect;
    private String checkS = "";
    TabLayout tab;
    ViewPager viewPager;
    private String storeClassId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobiliser_work_type);
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


//        pia = (Mobilizer) getIntent().getSerializableExtra("userObj");
        String mobid = "";
//        mobid = pia.getUser_id();
        PreferenceStorage.saveMobId(this, mobid);
        mobilizerArrayList = new ArrayList<>();
//        loadMoreListView = (ListView) findViewById(R.id.list_mobilizers);
//        loadMoreListView.setOnItemClickListener(this);

        tab = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        yearSelect = findViewById(R.id.year_select);
        yearSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showYears();
            }
        });

        Intent intent = getIntent();
        if (intent.hasExtra("eventObj")) {
            callGetClassTestService();
        } else {
            callGetClassTestService();
        }

    }

    public void callGetClassTestService() {
//        if (classTestArrayList != null)
//            classTestArrayList.clear();

        if (CommonUtils.isNetworkAvailable(this)) {
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            loadYear();
        } else {
//            AlertDialogHelper.showSimpleAlertDialog(this, getString(R.string.error_no_net));
        }
    }

    private void showYears() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);

        builderSingle.setTitle("Select Year");
        View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);
        header.setText("Select Year");
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(dataAdapter3
                ,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = yearList.get(which).toString();
                        yearSelect.setText(strName);
                        storeClassId = strName;
                        PreferenceStorage.saveYearId(getApplicationContext(), storeClassId);
                        loadMonth();
                    }
                });
        builderSingle.show();
    }

    private void loadYear() {
        checkS = "year";
        JSONObject jsonObject = new JSONObject();
        String id = "";
        String mobid = "";
        id = PreferenceStorage.getUserId(getApplicationContext());
        try {
            jsonObject.put(MobilizerConstants.KEY_USER_ID, id);
            jsonObject.put(MobilizerConstants.KEY_MOBILISER_ID, id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = MobilizerConstants.BUILD_URL + MobilizerConstants.GET_YEAR_LIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void loadMonth() {
        checkS = "month";
        JSONObject jsonObject = new JSONObject();
        String id = "";
        String mobid = "";
        id = PreferenceStorage.getUserId(getApplicationContext());
//        mobid = pia.getUser_id();
        try {
            jsonObject.put(MobilizerConstants.KEY_USER_ID, id);
            jsonObject.put(MobilizerConstants.KEY_MOBILISER_ID, id);
            jsonObject.put(MobilizerConstants.PARAMS_YEAR_ID, storeClassId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = MobilizerConstants.BUILD_URL + MobilizerConstants.GET_MONTH_LIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void initialiseTabs(JSONArray subCategory) {
        for (int k = 0; k < subCategory.length(); k++) {
            try {
                tab.addTab(tab.newTab().setText("" + subCategory.getJSONObject(k).get("month_name")));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        WorkTypeTabAdapter adapter = new WorkTypeTabAdapter
                (getSupportFragmentManager(), tab.getTabCount(), mobilizerArrayList);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab));
        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String id = "";
                id = mobilizerArrayList.get(tab.getPosition()).getmonth_id();
//                PreferenceStorage.saveSubCatClick(getApplicationContext(), id);
                viewPager.setCurrentItem(tab.getPosition());
                viewPager.performClick();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
//                 recreate();
                String id = "";
                id = mobilizerArrayList.get(tab.getPosition()).getmonth_id();
//                PreferenceStorage.saveSubCatClick(getApplicationContext(), id);
                viewPager.setCurrentItem(tab.getPosition());
                viewPager.performClick();
            }
        });
//        tab.removeOnTabSelectedListener(TabLayout.OnTabSelectedListener);
//Bonus Code : If your tab layout has more than 2 tabs then tab will scroll other wise they will take whole width of the screen
        if (tab.getTabCount() <= 2) {
            tab.setTabMode(TabLayout.
                    MODE_FIXED);
        } else {
            tab.setTabMode(TabLayout.
                    MODE_SCROLLABLE);
        }
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    @Override
    public void onResponse(JSONObject response) {
        progressDialogHelper.hideProgressDialog();
        if (validateSignInResponse(response)) {
            try {
                if (checkS.equalsIgnoreCase("year")) {
                    JSONArray getData = response.getJSONArray("result");
                    String year = "";
                    yearList.clear();
                    for (int i = 0; i < getData.length(); i++) {
                        year = getData.getJSONObject(i).getString("year_id");
                        yearList.add(year);
                    }
                    dataAdapter3 = new ArrayAdapter<String>(this, R.layout.gender_layout, R.id.gender_name, yearList) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            Log.d(TAG, "getview called" + position);
                            View view = getLayoutInflater().inflate(R.layout.gender_layout, parent, false);
                            TextView gendername = (TextView) view.findViewById(R.id.gender_name);
                            gendername.setText(yearList.get(position).toString());

                            // ... Fill in other views ...
                            return view;
                        }
                    };
                } else if (checkS.equalsIgnoreCase("month")) {
                    JSONArray getData = response.getJSONArray("result");

                    Gson gson = new Gson();
                    WorkMonthList subCategoryList = gson.fromJson(response.toString(), WorkMonthList.class);
                    if (subCategoryList.getCategoryArrayList() != null && subCategoryList.getCategoryArrayList().size() > 0) {
                        totalCount = subCategoryList.getCount();
                        this.mobilizerArrayList.addAll(subCategoryList.getCategoryArrayList());
                        isLoadingForFirstTime = false;
//                    updateListAdapter(subCategoryList.getCategoryArrayList());
                    } else {
                        if (mobilizerArrayList != null) {
                            mobilizerArrayList.clear();
//                        updateListAdapter(subCategoryList.getCategoryArrayList());
                        }
                    }

                    initialiseTabs(getData);
//                    loadDayMonth();
                } else if (checkS.equalsIgnoreCase("day")) {
                    JSONArray getData = response.getJSONArray("result");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onError(String error) {

    }

    @Override
    public void onClick(View v) {

    }
}
