package com.happysanztech.mmm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.happysanztech.mmm.R;
import com.happysanztech.mmm.bean.support.WorkDetails;
import com.happysanztech.mmm.helper.AlertDialogHelper;
import com.happysanztech.mmm.helper.ProgressDialogHelper;
import com.happysanztech.mmm.interfaces.DialogClickListener;
import com.happysanztech.mmm.servicehelpers.ServiceHelper;
import com.happysanztech.mmm.serviceinterfaces.IServiceListener;
import com.happysanztech.mmm.utils.AppValidator;
import com.happysanztech.mmm.utils.CommonUtils;
import com.happysanztech.mmm.utils.MobilizerConstants;
import com.happysanztech.mmm.utils.PreferenceStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.util.Log.d;

public class MobiliserWorkComment extends AppCompatActivity implements IServiceListener, DialogClickListener, AdapterView.OnItemClickListener, View.OnClickListener {
    private static final String TAG = MobiliserWorkComment.class.getName();

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private WorkDetails pia;
    private EditText commet;
    private Button done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobiliser_work_comments);
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        pia = (WorkDetails) intent.getSerializableExtra("taskObj");

        commet = findViewById(R.id.mob_comments);
        done = findViewById(R.id.done);
        done.setOnClickListener(this);
    }

    public void callGetClassTestService() {
//        if (classTestArrayList != null)
//            classTestArrayList.clear();

        if (CommonUtils.isNetworkAvailable(this)) {
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            sendComment();
        } else {
//            AlertDialogHelper.showSimpleAlertDialog(this, getString(R.string.error_no_net));
        }
    }

    private boolean validateFields() {
        if (!AppValidator.checkNullString(this.commet.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter valid comment");
            return false;
        } else {
            return true;
        }
    }

    private void sendComment() {
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
            jsonObject.put(MobilizerConstants.PARAMS_MOB_COMMENTS, commet.getText().toString());
            jsonObject.put(MobilizerConstants.PARAMS_ATTEND_ID, attendId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = MobilizerConstants.BUILD_URL + MobilizerConstants.SEND_COMMENTS;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onClick(View v) {
        if (v == done) {
            if (validateFields()) {
                callGetClassTestService();
            }
        }
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

    @Override
    public void onResponse(JSONObject response) {
        progressDialogHelper.hideProgressDialog();

        if (validateSignInResponse(response)) {
//            setResult(RESULT_OK);
            Toast.makeText(this, "Sent successfully...", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(this, TaskActivity.class);
//            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onError(String error) {

    }
}
