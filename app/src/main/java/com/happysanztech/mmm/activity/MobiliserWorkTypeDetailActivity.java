package com.happysanztech.mmm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.happysanztech.mmm.R;
import com.happysanztech.mmm.bean.support.WorkDetails;
import com.happysanztech.mmm.bean.support.WorkMonth;
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

public class MobiliserWorkTypeDetailActivity extends AppCompatActivity implements IServiceListener, DialogClickListener, AdapterView.OnItemClickListener, View.OnClickListener {

    private static final String TAG = MobiliserWorkTypeActivity.class.getName();

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private Handler mHandler = new Handler();
    int totalCount = 0, checkrun = 0;
    protected boolean isLoadingForFirstTime = true;
    private WorkDetails pia;
    private ArrayList yearList = new ArrayList();
    private ArrayAdapter<String> dataAdapter3;
    private Spinner yearSelect;
    private String checkS = "";
//    TabLayout tab;
//    ViewPager viewPager;
    private String storeClassId = "";

    EditText txtTitle, txtDetails, txtDate, txtStatus, txtType, txtDist;
    Button viewPhotos;
    ImageView EditTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobiliser_work_type_detail);
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txtTitle = findViewById(R.id.task_title);
        txtDetails = findViewById(R.id.task_link);
        txtDate = findViewById(R.id.task_date);
        txtStatus = findViewById(R.id.status);
        txtType = findViewById(R.id.task_type);
        txtDist = findViewById(R.id.km);

        txtTitle.setClickable(false);
        txtTitle.setFocusable(false);

        txtDetails.setClickable(false);
        txtDetails.setFocusable(false);

        txtDate.setClickable(false);
        txtDate.setFocusable(false);

        txtStatus.setClickable(false);
        txtStatus.setFocusable(false);

        txtType.setClickable(false);
        txtType.setFocusable(false);

        txtDist.setClickable(false);
        txtDist.setFocusable(false);

        viewPhotos = findViewById(R.id.btn_view_photos);
        viewPhotos.setOnClickListener(this);

        EditTask = findViewById(R.id.edit_task);
        EditTask.setOnClickListener(this);

        Intent intent = getIntent();
        if (intent.hasExtra("serviceObj")) {
            pia = (WorkDetails) intent.getSerializableExtra("serviceObj");
//            PreferenceStorage.savePIAProfileId(this, pia.getUser_id());
            callGetClassTestService();
        } else {
            callGetClassTestService();
        }

        if (pia.getwork_type_id().equalsIgnoreCase("1")) {
            viewPhotos.setVisibility(View.GONE);
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

    private void loadYear() {
        checkS = "year";
        JSONObject jsonObject = new JSONObject();
        String id = "";
        String mobid = "";
        String yearid = "";
        String monthid = "";
        String attendId = "";
        id = PreferenceStorage.getUserId(this);
        mobid = pia.getmobilizer_id();
        yearid = PreferenceStorage.getYearId(this);
        monthid = PreferenceStorage.getMonthId(this);
        attendId = pia.getid();
        try {
            jsonObject.put(MobilizerConstants.KEY_USER_ID, id);
            jsonObject.put(MobilizerConstants.KEY_MOBILISER_ID, mobid);
            jsonObject.put(MobilizerConstants.PARAMS_YEAR_ID, yearid);
            jsonObject.put(MobilizerConstants.PARAMS_MONTH_ID, monthid);
            jsonObject.put(MobilizerConstants.PARAMS_ATTEND_ID, attendId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = MobilizerConstants.BUILD_URL + MobilizerConstants.GET_WORK_DETAIL;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
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
                JSONObject getData = response.getJSONObject("attedance_details");
                JSONObject getkmData = response.getJSONObject("km_data_details");
                JSONObject getDataResult = getData.getJSONObject("result");
                txtTitle.setText(getDataResult.getString("title"));
                txtDate.setText(getDataResult.getString("attendance_date"));
                txtStatus.setText(getDataResult.getString("status"));
                txtDetails.setText(getDataResult.getString("comments"));
                txtType.setText(getDataResult.getString("work_type"));
                if (getkmData.getString("status").equalsIgnoreCase("success")) {
                    txtDist.setText(getkmData.getJSONObject("km_data").getString("km"));
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
        if (v == viewPhotos) {
            Intent intent = new Intent(getApplicationContext(), ViewTaskPhotosActivity.class);
            intent.putExtra("eventObj", pia);
            startActivity(intent);
        }else if (v == EditTask) {
            Intent intent = new Intent(this, MobiliserWorkComment.class);
            intent.putExtra("taskObj", pia);
            startActivity(intent);
            finish();
        }
    }
}