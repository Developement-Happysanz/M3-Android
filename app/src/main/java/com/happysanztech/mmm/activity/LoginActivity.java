package com.happysanztech.mmm.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.happysanztech.mmm.R;
import com.happysanztech.mmm.helper.AlertDialogHelper;
import com.happysanztech.mmm.helper.ProgressDialogHelper;
import com.happysanztech.mmm.interfaces.DialogClickListener;
import com.happysanztech.mmm.servicehelpers.LocationService;
import com.happysanztech.mmm.servicehelpers.ServiceHelper;
import com.happysanztech.mmm.serviceinterfaces.IServiceListener;
import com.happysanztech.mmm.utils.CommonUtils;
import com.happysanztech.mmm.utils.FirstTimePreference;
import com.happysanztech.mmm.utils.MobilizerConstants;
import com.happysanztech.mmm.utils.MobilizerValidator;
import com.happysanztech.mmm.utils.PermissionUtil;
import com.happysanztech.mmm.utils.PreferenceStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static android.util.Log.d;

/**
 * Created by Admin on 01-01-2018.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener {

    private static final String TAG = LoginActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private EditText edtUsername, edtPassword;
    private Button signIn;

    private static String[] PERMISSIONS_ALL = {Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private static final int REQUEST_PERMISSION_All = 111;

    private static final int IGNORE_BATTERY_OPTIMIZATION_REQUEST = 1002;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        signIn = findViewById(R.id.signin);
        signIn.setOnClickListener(this);

        FirstTimePreference prefFirstTime = new FirstTimePreference(getApplicationContext());

        if (prefFirstTime.runTheFirstTime("FirstTimePermit")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                requestAllPermissions();
            }
        }

        addAutoStartup();

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (pm != null && !pm.isIgnoringBatteryOptimizations(getPackageName())) {
                askIgnoreOptimization();
            } else {
//                accepted;
            }
        } else {
//            accepted;
        }*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(getPackageName());
            if (!isIgnoringBatteryOptimizations) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, IGNORE_BATTERY_OPTIMIZATION_REQUEST);

                /*intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);*/
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requestCode == IGNORE_BATTERY_OPTIMIZATION_REQUEST) {
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                boolean isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(getPackageName());
                if (isIgnoringBatteryOptimizations) {
                    // Ignoring battery optimization
                    Toast.makeText(getApplicationContext(), "Ignoring battery optimization", Toast.LENGTH_SHORT).show();
                } else {
                    // Not ignoring battery optimization
                    Toast.makeText(getApplicationContext(), "Not ignoring battery optimization", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    private void requestAllPermissions() {

        boolean requestPermission = PermissionUtil.requestAllPermissions(this);

        if (requestPermission == true) {

            Log.i(TAG,
                    "Displaying contacts permission rationale to provide additional context.");

            // Display a SnackBar with an explanation and a button to trigger the request.

            ActivityCompat
                    .requestPermissions(this, PERMISSIONS_ALL,
                            REQUEST_PERMISSION_All);
        } else {

            ActivityCompat.requestPermissions(this, PERMISSIONS_ALL, REQUEST_PERMISSION_All);
        }
    }

    @Override
    public void onClick(View v) {
        if (CommonUtils.isNetworkAvailable(getApplicationContext())) {
            if (v == signIn) {
                if (validateFields()) {

                    String username = edtUsername.getText().toString();
                    String password = edtPassword.getText().toString();

                    String GCMKey = PreferenceStorage.getGCM(getApplicationContext());

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(MobilizerConstants.PARAMS_USERNAME, username);
                        jsonObject.put(MobilizerConstants.PARAMS_PASSWORD, password);
                        jsonObject.put(MobilizerConstants.PARAMS_GCM_KEY, GCMKey);
                        jsonObject.put(MobilizerConstants.PARAMS_MOBILE_TYPE, "1");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
                    String url = MobilizerConstants.BUILD_URL + MobilizerConstants.USER_LOGIN_API;
                    serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
                }
            }
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection available");
        }
    }

    private boolean validateFields() {
        if (!MobilizerValidator.checkNullString(this.edtUsername.getText().toString().trim())) {
            edtUsername.setError(getString(R.string.err_username));
            requestFocus(edtUsername);
            return false;
        } else if (!MobilizerValidator.checkNullString(this.edtPassword.getText().toString().trim())) {
            edtPassword.setError(getString(R.string.err_empty_password));
            requestFocus(edtPassword);
            return false;
        } else if (!MobilizerValidator.checkStringMinLength(6, this.edtPassword.getText().toString().trim())) {
            edtPassword.setError(getString(R.string.err_min_pass_length));
            requestFocus(edtPassword);
            return false;
        } else {
            return true;
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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

            try {

//                startService(new Intent(LoginActivity.this, LocationService.class));
//                startService(new Intent(LoginActivity.this, GPSTracker.class));

                JSONObject userData = response.getJSONObject("userData");
                JSONObject staffProfile = response.getJSONObject("staffProfile");

                saveUserData(userData);
                saveStaffProfile(staffProfile);

                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(this, error);
    }

    private void saveUserData(JSONObject userData) {

        Log.d(TAG, "userData dictionary" + userData.toString());

        String userId = "";
        String fullName = "";
        String userName = "";
        String userPicture = "";
        String userTypeName = "";
        String userType = "";
        String passwordStatus = "";

        try {

            if (userData != null) {

                // User Preference - User Id
                userId = userData.getString("user_id");
                if ((userId != null) && !(userId.isEmpty()) && !userId.equalsIgnoreCase("null")) {
                    PreferenceStorage.saveUserId(this, userId);
                }

                // User Preference - User Full Name
                fullName = userData.getString("name");
                if ((fullName != null) && !(fullName.isEmpty()) && !fullName.equalsIgnoreCase("null")) {
                    PreferenceStorage.saveName(this, fullName);
                }

                // User Preference - User Name
                userName = userData.getString("user_name");
                if ((userName != null) && !(userName.isEmpty()) && !userName.equalsIgnoreCase("null")) {
                    PreferenceStorage.saveUserName(this, userName);
                }

                // User Preference - User Picture
                userPicture = userData.getString("user_pic");
                if ((userPicture != null) && !(userPicture.isEmpty()) && !userPicture.equalsIgnoreCase("null")) {
                    PreferenceStorage.saveUserPicture(this, userPicture);
                }

                // User Preference - User Type Name
                userTypeName = userData.getString("user_type_name");
                if ((userTypeName != null) && !(userTypeName.isEmpty()) && !userTypeName.equalsIgnoreCase("null")) {
                    PreferenceStorage.saveUserTypeName(this, userTypeName);
                }

                // User Preference - User Type
                userType = userData.getString("user_type");
                if ((userType != null) && !(userType.isEmpty()) && !userType.equalsIgnoreCase("null")) {
                    PreferenceStorage.saveUserType(this, userType);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void saveStaffProfile(JSONObject staffProfile) {

        Log.d(TAG, "staffProfile dictionary" + staffProfile.toString());

        String staffId = "";
        String piaId = "";
        String fullName = "";
        String sex = "";
        String age = "";
        String nationality = "";
        String religion = "";
        String communityClass = "";
        String community = "";
        String address = "";
        String email = "";
        String phone = "";
        String qualification = "";

        try {

            if (staffProfile != null) {

                // User Preference - Staff Id
                staffId = staffProfile.getString("staff_id");
                if ((staffId != null) && !(staffId.isEmpty()) && !staffId.equalsIgnoreCase("null")) {
                    PreferenceStorage.saveStaffId(this, staffId);
                }

                // User Preference - PIA Id
                piaId = staffProfile.getString("pia_id");
                if ((piaId != null) && !(piaId.isEmpty()) && !piaId.equalsIgnoreCase("null")) {
                    PreferenceStorage.savePIAId(this, piaId);
                }

                // User Preference - Staff Full Name
                fullName = staffProfile.getString("name");
                if ((fullName != null) && !(fullName.isEmpty()) && !fullName.equalsIgnoreCase("null")) {
                    PreferenceStorage.saveStaffFullName(this, fullName);
                }

                // User Preference - Staff sex
                sex = staffProfile.getString("sex");
                if ((sex != null) && !(sex.isEmpty()) && !sex.equalsIgnoreCase("null")) {
                    PreferenceStorage.saveStaffSex(this, sex);
                }

                // User Preference - Staff age
                age = staffProfile.getString("age");
                if ((age != null) && !(age.isEmpty()) && !age.equalsIgnoreCase("null")) {
                    PreferenceStorage.saveStaffAge(this, age);
                }

                // User Preference - Staff nationality
                nationality = staffProfile.getString("nationality");
                if ((nationality != null) && !(nationality.isEmpty()) && !nationality.equalsIgnoreCase("null")) {
                    PreferenceStorage.saveStaffNationality(this, nationality);
                }

                // User Preference - Staff religion
                religion = staffProfile.getString("religion");
                if ((religion != null) && !(religion.isEmpty()) && !religion.equalsIgnoreCase("null")) {
                    PreferenceStorage.saveStaffReligion(this, religion);
                }

                // User Preference - Staff community class
                communityClass = staffProfile.getString("community_class");
                if ((communityClass != null) && !(communityClass.isEmpty()) && !communityClass.equalsIgnoreCase("null")) {
                    PreferenceStorage.saveStaffCommunityClass(this, communityClass);
                }

                // User Preference - Staff community
                community = staffProfile.getString("community");
                if ((community != null) && !(community.isEmpty()) && !community.equalsIgnoreCase("null")) {
                    PreferenceStorage.saveStaffCommunity(this, community);
                }

                // User Preference - Staff address
                address = staffProfile.getString("address");
                if ((address != null) && !(address.isEmpty()) && !address.equalsIgnoreCase("null")) {
                    PreferenceStorage.saveStaffAddress(this, address);
                }

                // User Preference - Staff email
                email = staffProfile.getString("email");
                if ((email != null) && !(email.isEmpty()) && !email.equalsIgnoreCase("null")) {
                    PreferenceStorage.saveStaffEmail(this, email);
                }

                // User Preference - Staff phone
                phone = staffProfile.getString("phone");
                if ((phone != null) && !(phone.isEmpty()) && !phone.equalsIgnoreCase("null")) {
                    PreferenceStorage.saveStaffPhone(this, phone);
                }

                // User Preference - Staff qualification
                qualification = staffProfile.getString("qualification");
                if ((qualification != null) && !(qualification.isEmpty()) && !qualification.equalsIgnoreCase("null")) {
                    PreferenceStorage.saveStaffQualification(this, qualification);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addAutoStartup() {

        try {
            Intent intent = new Intent();
            String manufacturer = android.os.Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            } else if ("Letv".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
            } else if ("Honor".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            }

            List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if  (list.size() > 0) {
                startActivity(intent);
            }
        } catch (Exception e) {
            Log.e("exc" , String.valueOf(e));
        }
    }
}
