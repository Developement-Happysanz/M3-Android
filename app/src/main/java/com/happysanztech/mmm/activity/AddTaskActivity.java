package com.happysanztech.mmm.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.happysanztech.mmm.R;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.util.Log.d;

/**
 * Created by Admin on 08-01-2018.
 */

public class AddTaskActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener, DatePickerDialog.OnDateSetListener {

    private static final String TAG = AddTaskActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private EditText edtTitle, edtTaskDetails, edtTaskDate;
    private Button btnSave;
    private ImageView ivBack;
    private SimpleDateFormat mDateFormatter;
    private DatePickerDialog mDatePicker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        ivBack = findViewById(R.id.back_tic_his);
        ivBack.setOnClickListener(this);
        edtTitle = findViewById(R.id.et_task_title);
        edtTaskDetails = findViewById(R.id.et_task_detail);
        edtTaskDate = findViewById(R.id.et_task_date);
        edtTaskDate.setOnClickListener(this);
        edtTaskDate.setFocusable(false);
        btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(this);
        mDateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        setupUI(findViewById(R.id.scrollID));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(AddTaskActivity.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }


    @Override
    public void onClick(View v) {
        if (v == ivBack) {
            finish();
        }
        if (v == edtTaskDate) {
            showBirthdayDate();
        }
        if (v == btnSave) {
            if (validateFields()) {
                saveTask();
            }
        }
    }

    private boolean validateFields() {
        if (!AppValidator.checkNullString(this.edtTitle.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter valid title");
            return false;
        } else if (!AppValidator.checkNullString(this.edtTaskDetails.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter valid task details");
            return false;
        } else if (!AppValidator.checkNullString(this.edtTaskDate.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter valid date");
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
            setResult(RESULT_OK);
            Toast.makeText(this, "New task created.", Toast.LENGTH_SHORT).show();
//            finish();
            finish();
        }
    }

    @Override
    public void onError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(this, error);
    }

    private void saveTask() {

        if (CommonUtils.isNetworkAvailable(getApplicationContext())) {

            String title = edtTitle.getText().toString();
            String taskDetails = edtTaskDetails.getText().toString();
            String taskDate = edtTaskDate.getText().toString();

            String serverFormatDate = "";

            if (edtTaskDate.getText().toString() != null && edtTaskDate.getText().toString() != "") {

                String date = edtTaskDate.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");
                Date testDate = null;
                try {
                    testDate = sdf.parse(date);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
                serverFormatDate = formatter.format(testDate);
                System.out.println(".....Date..." + serverFormatDate);
            }

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(MobilizerConstants.PARAMS_TASK_TITLE, title);
                jsonObject.put(MobilizerConstants.KEY_USER_ID, PreferenceStorage.getUserId(getApplicationContext()));
                jsonObject.put(MobilizerConstants.PARAMS_TASK_DESCRIPTION, taskDetails);
                jsonObject.put(MobilizerConstants.PARAMS_TASK_DATE, serverFormatDate);
                jsonObject.put(MobilizerConstants.PARAMS_TASK_STATUS, "Active");
                jsonObject.put(MobilizerConstants.PARAMS_PIA_ID,PreferenceStorage.getPIAId(getApplicationContext()));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            String url = MobilizerConstants.BUILD_URL + MobilizerConstants.TASK_ADD;
            serviceHelper.makeGetServiceCall(jsonObject.toString(), url);

        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection available");
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {


        Calendar newDate = Calendar.getInstance();
        newDate.set(year, monthOfYear, dayOfMonth);
        edtTaskDate.setText(mDateFormatter.format(newDate.getTime()));
//        getAge(year, monthOfYear, dayOfMonth);
    }

    private void showBirthdayDate() {
        Log.d(TAG, "Show the birthday date");
        Calendar newCalendar = Calendar.getInstance();
        String currentdate = edtTaskDate.getText().toString();
        Log.d(TAG, "current date is" + currentdate);
        int month = newCalendar.get(Calendar.MONTH);
        int day = newCalendar.get(Calendar.DAY_OF_MONTH);
        int year = newCalendar.get(Calendar.YEAR);
        if ((currentdate != null) && !(currentdate.isEmpty())) {
            //extract the date/month and year
            try {
                Date startDate = mDateFormatter.parse(currentdate);
                Calendar newDate = Calendar.getInstance();

                newDate.setTime(startDate);
                month = newDate.get(Calendar.MONTH);
                day = newDate.get(Calendar.DAY_OF_MONTH);
                year = newDate.get(Calendar.YEAR);
                Log.d(TAG, "month" + month + "day" + day + "year" + year);

            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                mDatePicker = new DatePickerDialog(this, R.style.datePickerTheme, this, year, month, day);
                mDatePicker.show();
            }
        } else {
            Log.d(TAG, "show default date");

            mDatePicker = new DatePickerDialog(this, R.style.datePickerTheme, this, year, month, day);
            mDatePicker.show();
        }
    }
}
