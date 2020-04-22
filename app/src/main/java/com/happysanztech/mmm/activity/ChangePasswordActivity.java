package com.happysanztech.mmm.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.happysanztech.mmm.R;
import com.happysanztech.mmm.helper.AlertDialogHelper;
import com.happysanztech.mmm.helper.ProgressDialogHelper;
import com.happysanztech.mmm.interfaces.DialogClickListener;
import com.happysanztech.mmm.servicehelpers.ServiceHelper;
import com.happysanztech.mmm.serviceinterfaces.IServiceListener;
import com.happysanztech.mmm.utils.AppValidator;
import com.happysanztech.mmm.utils.MobilizerConstants;
import com.happysanztech.mmm.utils.PreferenceStorage;

import org.json.JSONException;
import org.json.JSONObject;

import static android.util.Log.d;


/**
 * Created by Admin on 03-01-2018.
 */

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener {

    private static final String TAG = "TradeActivity";
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private EditText etOldPassword;
    private EditText etNewPassword;
    private EditText etConfirmPassword;
    private Button btnSubmit;
    View rootView;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_change_password);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        etOldPassword = findViewById(R.id.et_old_password);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);
        findViewById(R.id.back_tic_his).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v == btnSubmit) {
            if (validateFields()) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(MobilizerConstants.KEY_USER_ID, PreferenceStorage.getUserId(this));
                    jsonObject.put(MobilizerConstants.PARAMS_OLD_PASSWORD, etOldPassword.getText().toString());
                    jsonObject.put(MobilizerConstants.PARAMS_NEW_PASSWORD, etNewPassword.getText().toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
                String url = MobilizerConstants.BUILD_URL + MobilizerConstants.CHANGE_PASSWORD;
                serviceHelper.makeGetServiceCall(jsonObject.toString(), url);

            }
        }
    }

    private boolean validateFields() {

        if (!AppValidator.checkNullString(this.etOldPassword.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter current password.");
            return false;
        } else if (!AppValidator.checkNullString(this.etNewPassword.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter New password.");
            return false;
        } else if (!AppValidator.checkNullString(this.etConfirmPassword.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Please confirm the new password by re-tying it.");
            return false;
        } else if (!this.etNewPassword.getText().toString().trim().equalsIgnoreCase(this.etNewPassword.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Thsi doesn't match with your new password.");
            return false;
        } else {
            return true;
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
    public void onResponse(JSONObject response) {
        progressDialogHelper.hideProgressDialog();

        if (validateSignInResponse(response)) {
            Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show();
//            Intent navigationIntent = new Intent(this, DashboardActivity.class);
//            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(navigationIntent);
            finish();
        }
    }

    @Override
    public void onError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(getApplicationContext(), error);
    }
}
