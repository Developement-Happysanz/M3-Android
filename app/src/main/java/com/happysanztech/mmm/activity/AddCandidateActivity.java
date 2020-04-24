package com.happysanztech.mmm.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.happysanztech.mmm.R;
import com.happysanztech.mmm.bean.database.SQLiteHelper;
import com.happysanztech.mmm.bean.support.AllProspects;
import com.happysanztech.mmm.bean.support.StoreBloodGroup;
import com.happysanztech.mmm.bean.support.StoreTimings;
import com.happysanztech.mmm.bean.support.StoreTrade;
import com.happysanztech.mmm.helper.AlertDialogHelper;
import com.happysanztech.mmm.helper.ProgressDialogHelper;
import com.happysanztech.mmm.interfaces.DialogClickListener;
import com.happysanztech.mmm.servicehelpers.ServiceHelper;
import com.happysanztech.mmm.serviceinterfaces.IServiceListener;
import com.happysanztech.mmm.utils.AndroidMultiPartEntity;
import com.happysanztech.mmm.utils.AppValidator;
import com.happysanztech.mmm.utils.CommonUtils;
import com.happysanztech.mmm.utils.MobilizerConstants;
import com.happysanztech.mmm.utils.PreferenceStorage;
import com.happysanztech.mmm.utils.aadhaar.DataAttributes;
import com.happysanztech.mmm.utils.aadhaar.Storage;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.squareup.picasso.Picasso;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import static android.util.Log.d;

/**
 * Created by Admin on 04-01-2018.
 */

public class AddCandidateActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, View.OnClickListener,
        IServiceListener, DialogClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = AddCandidateActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private Uri outputFileUri;
    static final int REQUEST_IMAGE_GET = 1;
    private String mActualFilePath = null;
    private Uri mSelectedImageUri = null;
    private Bitmap mCurrentUserImageBitmap = null;
    private String checkInternalState;
    private DatePickerDialog mDatePicker;
    private SimpleDateFormat mDateFormatter;
    private ProgressDialog mProgressDialog = null;
    private String mUpdatedImageUrl = null;
    long totalSize = 0;
    String admissionId = "";
    AllProspects allProspects;

    //Define a request code to send to Google Play services
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;
    private String locationAddressResult = "";
    Location mLastLocation = null;
    private ProgressDialog mLocationProgress = null;

    private ImageView imBack;
    private ImageView imCandidatePicture;
    private EditText etCandidateName;
    private EditText etCandidateSex;
    private ArrayAdapter<String> mGenderAdapter = null;
    private List<String> mGenderList = new ArrayList<String>();
    private EditText etCandidateDOB;
    private EditText etCandidateAge;
    private EditText etCandidateNationality;
    private EditText etCandidateReligion;
    private EditText etCandidateCommunityClass;
    private EditText etCandidateCommunity;
    private EditText etCandidateBloodGroup;
    ArrayAdapter<StoreBloodGroup> mBloodGroupAdapter = null;
    ArrayList<StoreBloodGroup> bloodGroupList;
    private EditText etCandidateFatherName;
    private EditText etCandidateMotherName;
    private EditText etCandidateMobileNo;
    private EditText etCandidateAlterMobileNo;
    private EditText etCandidateEmailId;
    private EditText etCandidateState;
    private EditText etCandidateCity;
    private EditText etCandidateAddressLine1;
    private EditText etCandidateAddressLine2;
    private EditText etCandidateMotherTongue;
    private CheckBox cbAnyDisability;
    private EditText etCandidateDisabilityReason;
    private EditText etCandidatesPreferredTrade;
    ArrayAdapter<StoreTrade> mTradeAdapter = null;
    ArrayList<StoreTrade> tradeList;
    private EditText etCandidatesPreferredTiming;
    ArrayAdapter<StoreTimings> mTimingsAdapter = null;
    ArrayList<StoreTimings> timingsList;
    private EditText etCandidatesQualification;
    private EditText etCandidatesLastInstitute;
    private EditText etCandidatesQualifiedPromotion;
    private EditText etCandidatesTC;
    private CheckBox cbCandidatesTC;
    private CheckBox cbCandidatesAadhaarStatus;
    private EditText etCandidatesAadhaarNo;
    private Button btnSubmit;

    String tradeId = "";
    String timigsId = "";
    String bloodGroupId = "";

    private Storage storage;
    private JSONArray storageDataArray;

    SQLiteHelper database;
    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    LocationManager locationManager;
    private String gpsStatus = "N";
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

    private EditText qualificationDetails, yearOfEdu, yearOfPass, iMarkOne, iMarkTwo, langKnown, motherMob, fatherMob,
            headFamilyName, eduHeadOfFamily, familyMembers, yearlyIncome, jobCard;

    private ArrayAdapter<String> mJobCardAdapter = null;
    private List<String> mJobCardList = new ArrayList<String>();

    private ArrayAdapter<String> mYearAdapter = null;
    private List<String> mYearList = new ArrayList<String>();

    private ArrayAdapter<String> mQualificationAdapter = null;
    private List<String> mQualificationList = new ArrayList<String>();

    private ArrayAdapter<String> mNationalityAdapter = null;
    private List<String> mNationalityList = new ArrayList<String>();

    private ArrayAdapter<String> mCasteAdapter = null;
    private List<String> mCasteList = new ArrayList<String>();

    private ArrayAdapter<String> mPassAdapter = null;
    private List<String> mPassList = new ArrayList<String>();

    private Boolean imgurl = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_candidate_new);

        database = new SQLiteHelper(getApplicationContext());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
//                onNewLocation(locationResult.getLastLocation());
            }
        };
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!isGPSEnable && !isNetworkEnable) {

            gpsStatus = "N";

        } else {

            gpsStatus = "Y";
        }
        allProspects = (AllProspects) getIntent().getSerializableExtra("pros");

        setUpUI();
//        setupUI(findViewById(R.id.scrollID));

        bloodGroupList = new ArrayList<>();

        bloodGroupList.add(new StoreBloodGroup("1", "A+"));
        bloodGroupList.add(new StoreBloodGroup("2", "O+"));
        bloodGroupList.add(new StoreBloodGroup("3", "B+"));
        bloodGroupList.add(new StoreBloodGroup("4", "AB+"));
        bloodGroupList.add(new StoreBloodGroup("5", "A-"));
        bloodGroupList.add(new StoreBloodGroup("6", "O-"));
        bloodGroupList.add(new StoreBloodGroup("7", "B-"));
        bloodGroupList.add(new StoreBloodGroup("8", "AB-"));

        //fill data in spinner
        mBloodGroupAdapter = new ArrayAdapter<StoreBloodGroup>(getApplicationContext(), R.layout.gender_layout, R.id.gender_name, bloodGroupList) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Log.d(TAG, "getview called" + position);
                View view = getLayoutInflater().inflate(R.layout.gender_layout, parent, false);
                TextView gendername = (TextView) view.findViewById(R.id.gender_name);
                gendername.setText(bloodGroupList.get(position).getBloodGroupName());

                // ... Fill in other views ...
                return view;
            }
        };

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

//    public static void hideSoftKeyboard(Activity activity) {
//        InputMethodManager inputMethodManager =
//                (InputMethodManager) activity.getSystemService(
//                        Activity.INPUT_METHOD_SERVICE);
//        inputMethodManager.hideSoftInputFromWindow(
//
//                .getCurrentFocus().getWindowToken(), 0);
//    }
//
//    public void setupUI(View view) {
//
//        // Set up touch listener for non-text box views to hide keyboard.
//        if (!(view instanceof EditText)) {
//            view.setOnTouchListener(new View.OnTouchListener() {
//                public boolean onTouch(View v, MotionEvent event) {
//                    hideSoftKeyboard(AddCandidateActivity.this);
//                    return false;
//                }
//            });
//        }
//
//        //If a layout container, iterate over children and seed recursion.
//        if (view instanceof ViewGroup) {
//            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
//                View innerView = ((ViewGroup) view).getChildAt(i);
//                setupUI(innerView);
//            }
//        }
//    }

    @Override
    public void onClick(View v) {
        if (v == imBack) {
            finish();
        } else if (v == imCandidatePicture) {
            openImageIntent();
        } else if (v == btnSubmit) {
            if (mLastLocation != null) {
                if (validateFields()) {
                    if (allProspects == null) {
                        checkInternalState = "candidate_submit";
                        saveCandidate();
                    } else {
                        updateCandidate();
                    }
                }
            } else {
                if (mGoogleApiClient.isConnected()) {
//                    fetchCurrentLocation();
                    getLastLocation();
                    if (mLastLocation == null) {
                        // AlertDialogHelper.showSimpleAlertDialog(getActivity(), "Enable Location services in settings");
                        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getApplicationContext());
                        alertDialogBuilder.setMessage("Enable Location services in settings");
                        alertDialogBuilder.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        // getActivity().getFragmentManager().popBackStack();
                                        // endOfCalibration();
                                        //add pause button
                                        Intent viewIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        startActivity(viewIntent);


                                    }
                                });

                        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }

                } else {
                    mLocationProgress = new ProgressDialog(getApplicationContext());
                    mLocationProgress.setIndeterminate(true);
                    mLocationProgress.setMessage("Loading");
                    mLocationProgress.show();
                }
            }
        } else if (v == etCandidateBloodGroup) {
            showBloodGroups();
        } else if (v == etCandidatesPreferredTrade) {
            showTrades();
        } else if (v == etCandidatesPreferredTiming) {
            showTimings();
        }
    }

    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();
                            currentLatitude = mLastLocation.getLatitude();
                            currentLongitude = mLastLocation.getLongitude();

                        } else {
                            Log.w(TAG, "getLastLocation:exception", task.getException());
                        }
                    }
                });
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
        try {
            if (validateSignInResponse(response)) {
                if (checkInternalState.equalsIgnoreCase("trade")) {
                    JSONArray getData = response.getJSONArray("Trades");
                    int getLength = getData.length();
                    String tradeId = "";
                    String tradeName = "";
                    tradeList = new ArrayList<>();

                    for (int i = 0; i < getLength; i++) {
                        tradeId = getData.getJSONObject(i).getString("id");
                        tradeName = getData.getJSONObject(i).getString("trade_name");
                        tradeList.add(new StoreTrade(tradeId, tradeName));
                    }

                    //fill data in spinner
                    mTradeAdapter = new ArrayAdapter<StoreTrade>(getApplicationContext(), R.layout.gender_layout, R.id.gender_name, tradeList) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            Log.d(TAG, "getview called" + position);
                            View view = getLayoutInflater().inflate(R.layout.gender_layout, parent, false);
                            TextView gendername = (TextView) view.findViewById(R.id.gender_name);
                            gendername.setText(tradeList.get(position).getTradeName());

                            // ... Fill in other views ...
                            return view;
                        }
                    };
                    if (allProspects == null) {
                        Log.d(TAG, "null");
                    } else {
                        getProspectdata();
                    }
                } else if (checkInternalState.equalsIgnoreCase("timings")) {
                    JSONArray getData = response.getJSONArray("Timings");
                    int getLength = getData.length();
                    String timingsId = "";
                    String timingsName = "";
                    timingsList = new ArrayList<>();

                    for (int i = 0; i < getLength; i++) {
                        timingsId = getData.getJSONObject(i).getString("id");
                        timingsName = getData.getJSONObject(i).getString("from_time");
                        timingsList.add(new StoreTimings(timingsId, timingsName));
                    }

                    //fill data in spinner
                    mTimingsAdapter = new ArrayAdapter<StoreTimings>(getApplicationContext(), R.layout.gender_layout, R.id.gender_name, timingsList) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            Log.d(TAG, "getview called" + position);
                            View view = getLayoutInflater().inflate(R.layout.gender_layout, parent, false);
                            TextView gendername = (TextView) view.findViewById(R.id.gender_name);
                            gendername.setText(timingsList.get(position).getTimingsName());

                            // ... Fill in other views ...
                            return view;
                        }
                    };
                } else if (checkInternalState.equalsIgnoreCase("bloodGroup")) {
                    JSONArray getData = response.getJSONArray("Bloodgroup");
                    int getLength = getData.length();
                    String bloodGroupId = "";
                    String bloodGroupName = "";
                    bloodGroupList = new ArrayList<>();

                    for (int i = 0; i < getLength; i++) {
                        bloodGroupId = getData.getJSONObject(i).getString("id");
                        bloodGroupName = getData.getJSONObject(i).getString("blood_group_name");
                        bloodGroupList.add(new StoreBloodGroup(bloodGroupId, bloodGroupName));
                    }

                    //fill data in spinner
                    mBloodGroupAdapter = new ArrayAdapter<StoreBloodGroup>(getApplicationContext(), R.layout.gender_layout, R.id.gender_name, bloodGroupList) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            Log.d(TAG, "getview called" + position);
                            View view = getLayoutInflater().inflate(R.layout.gender_layout, parent, false);
                            TextView gendername = (TextView) view.findViewById(R.id.gender_name);
                            gendername.setText(bloodGroupList.get(position).getBloodGroupName());

                            // ... Fill in other views ...
                            return view;
                        }
                    };

                    GetTrade();
                } else if (checkInternalState.equalsIgnoreCase("addStudent")) {
                    if (mProgressDialog != null) {
                        mProgressDialog.cancel();
                    }
                    admissionId = response.getString("admission_id");
                    if ((mActualFilePath != null)) {
                        Log.d(TAG, "Update profile picture");
                        mProgressDialog = new ProgressDialog(this);
                        mProgressDialog.setIndeterminate(true);
                        mProgressDialog.setMessage("Updating Profile");
                        mProgressDialog.show();
                        saveUserImage();
                    } else {
                        Toast.makeText(getApplicationContext(), "Created a new prospect.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), DocumentUploadActivity.class);
                        startActivity(intent);
                        finish();

                    }
                } else if (checkInternalState.equalsIgnoreCase("prospect")) {
//                    JSONArray getData = response.getJSONArray("studentDetails");
                    JSONObject getData = response.getJSONObject("studentDetails");

                    etCandidateName.setText(getData.getString("name"));
                    etCandidateSex.setText(getData.getString("sex"));

                    String dobFormat = "";
                    String dob = getData.getString("dob");
//                    etCandidateDOB.setText(getData.getString("dob"));
                    if (dob != null && dob != "") {

                        String date = dob;
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
                        Date testDate = null;
                        try {
                            testDate = sdf.parse(date);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-mm-yyyy");
                        dobFormat = formatter.format(testDate);
                        System.out.println(".....Date..." + dobFormat);

//                        SimpleDateFormat ageFormatter = new SimpleDateFormat("yyy-MM-dd");
//                        ageFormat = ageFormatter.format(testDate);

                    }
                    etCandidateDOB.setText(dobFormat);

                    etCandidateAge.setText(getData.getString("age"));
                    etCandidateNationality.setText(getData.getString("nationality"));
                    etCandidateReligion.setText(getData.getString("religion"));
                    etCandidateCommunityClass.setText(getData.getString("community_class"));
                    etCandidateCommunity.setText(getData.getString("community"));
                    etCandidateBloodGroup.setText(getData.getString("blood_group"));
                    etCandidateFatherName.setText(getData.getString("father_name"));
                    etCandidateMotherName.setText(getData.getString("mother_name"));
                    etCandidateMobileNo.setText(getData.getString("mobile"));
                    etCandidateAlterMobileNo.setText(getData.getString("sec_mobile"));
                    etCandidateEmailId.setText(getData.getString("email"));
                    etCandidateState.setText(getData.getString("state"));
                    etCandidateCity.setText(getData.getString("city"));
                    etCandidateAddressLine1.setText(getData.getString("address"));
//                    etCandidateAddressLine2.setText(getData.getString("name"));
                    etCandidateMotherTongue.setText(getData.getString("mother_tongue"));
                    if (getData.getString("disability").equalsIgnoreCase("0")) {
                        cbAnyDisability.setChecked(false);
                    } else {
                        cbAnyDisability.setChecked(true);
                    }
                    if (getData.getString("have_aadhaar_card").equalsIgnoreCase("0")) {
                        cbCandidatesAadhaarStatus.setChecked(false);
                    } else {
                        cbCandidatesAadhaarStatus.setChecked(true);
                        etCandidatesAadhaarNo.setText(getData.getString("aadhaar_card_number"));
                    }
//                    etCandidateDisabilityReason.setText(getData.getString("name"));
//                    etCandidatesPreferredTrade.setText(getData.getString("preferred_trade"));
                    tradeId = getData.getString("preferred_trade");
                    selectTradeById(tradeId);
                    etCandidatesPreferredTiming.setText(getData.getString("preferred_timing"));
                    etCandidatesQualification.setText(getData.getString("last_studied"));
                    etCandidatesLastInstitute.setText(getData.getString("last_institute"));
                    etCandidatesQualifiedPromotion.setText(getData.getString("qualified_promotion"));
//                    etCandidateStatus.setText(getData.getString("status"));
                    admissionId = getData.getString("id");
                    String url = getData.getString("student_pic");
                    if (((url != null) && !(url.isEmpty()))) {
                        imgurl = true;
                        Picasso.get().load(url).into(imCandidatePicture);
                    }

                    fatherMob.setText(getData.getString("father_mobile"));
                    etCandidatesQualification.setText(getData.getString("qualification"));
                    qualificationDetails.setText(getData.getString("qualification_details"));
                    yearOfEdu.setText(getData.getString("year_of_edu"));
                    yearOfPass.setText(getData.getString("year_of_pass"));
                    iMarkOne.setText(getData.getString("identification_mark_1"));
                    iMarkTwo.setText(getData.getString("identification_mark_2"));
                    langKnown.setText(getData.getString("lang_known"));
                    motherMob.setText(getData.getString("mother_mobile"));
                    headFamilyName.setText(getData.getString("head_family_name"));
                    eduHeadOfFamily.setText(getData.getString("head_family_edu"));
                    familyMembers.setText(getData.getString("no_family"));
                    yearlyIncome.setText(getData.getString("yearly_income"));

                    String CandidatesJobCard = getData.getString("jobcard_type");
                    if (CandidatesJobCard.equalsIgnoreCase("MG")) {
                        jobCard.setText("MGNRGEA Job Card");
                    } else if (CandidatesJobCard.equalsIgnoreCase("BP")) {
                        jobCard.setText("BPL/PIP Card");
                    }

                    PreferenceStorage.saveCaste(this, getData.getString("community_class"));
                    PreferenceStorage.saveDisability(this, getData.getString("disability"));

                } else if (checkInternalState.equalsIgnoreCase("updateStudent")) {
                    if (mProgressDialog != null) {
                        mProgressDialog.cancel();
                    }
                    if ((mActualFilePath != null)) {
                        Log.d(TAG, "Update profile picture");
                        mProgressDialog = new ProgressDialog(this);
                        mProgressDialog.setIndeterminate(true);
                        mProgressDialog.setMessage("Updating Profile");
                        mProgressDialog.show();
                        saveUserImage();
                    } else {
                        Toast.makeText(getApplicationContext(), "Changes made to candidate profile are saved.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), DocumentUploadActivity.class);
                        startActivity(intent);
                        finish();
                    }


                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(this, error);
    }

    private void setUpUI() {

        /*Location */

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
        /*Location*/

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);


        imBack = findViewById(R.id.back_tic_his);
        imBack.setOnClickListener(this);

        imCandidatePicture = findViewById(R.id.im_candidate_picture);
        imCandidatePicture.setOnClickListener(this);

        if (allProspects != null) {
            String url = allProspects.getProfile_pic();
            if (((url != null) && !(url.isEmpty()))) {
                imgurl = true;
                Picasso.get().load(url).placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile).into(imCandidatePicture);
            }
        }

        etCandidateName = findViewById(R.id.et_candidate_name);

        etCandidateSex = findViewById(R.id.et_candidate_sex);
        etCandidateSex.setFocusable(false);

        etCandidateDOB = findViewById(R.id.et_candidate_dob);
        etCandidateDOB.setFocusable(false);

        etCandidateAge = findViewById(R.id.et_candidate_age);
        etCandidateAge.setFocusable(false);

        mNationalityList.add("Indian");
        mNationalityList.add("Other");

        mNationalityAdapter = new ArrayAdapter<String>(this, R.layout.gender_layout, R.id.gender_name, mNationalityList) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Log.d(TAG, "getview called" + position);
                View view = getLayoutInflater().inflate(R.layout.gender_layout, parent, false);
                TextView gendername = (TextView) view.findViewById(R.id.gender_name);
                gendername.setText(mNationalityList.get(position));

                // ... Fill in other views ...
                return view;
            }
        };

        etCandidateNationality = findViewById(R.id.et_candidate_nationality);
        etCandidateNationality.setFocusable(false);
        etCandidateNationality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNationalityList();
            }
        });

        etCandidateReligion = findViewById(R.id.et_candidate_religion);

        mCasteList.add("SC");
        mCasteList.add("ST");
        mCasteList.add("BC");
        mCasteList.add("MBC");
        mCasteList.add("OC");

        mCasteAdapter = new ArrayAdapter<String>(this, R.layout.gender_layout, R.id.gender_name, mCasteList) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Log.d(TAG, "getview called" + position);
                View view = getLayoutInflater().inflate(R.layout.gender_layout, parent, false);
                TextView gendername = (TextView) view.findViewById(R.id.gender_name);
                gendername.setText(mCasteList.get(position));

                // ... Fill in other views ...
                return view;
            }
        };

        etCandidateCommunityClass = findViewById(R.id.et_candidate_community_class);
        etCandidateCommunityClass.setFocusable(false);
        etCandidateCommunityClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCasteList();
            }
        });

        etCandidateCommunity = findViewById(R.id.et_candidate_community);

        etCandidateBloodGroup = findViewById(R.id.et_candidate_blood_group);
        etCandidateBloodGroup.setOnClickListener(this);
        etCandidateBloodGroup.setFocusable(false);

        etCandidateFatherName = findViewById(R.id.et_candidate_father_name);

        etCandidateMotherName = findViewById(R.id.et_candidate_mother_name);

        etCandidateMobileNo = findViewById(R.id.et_candidate_mobile_no);

        etCandidateAlterMobileNo = findViewById(R.id.et_candidate_alternative_mobile_no);

        etCandidateEmailId = findViewById(R.id.et_candidate_email_id);

        etCandidateState = findViewById(R.id.et_candidate_state);

        etCandidateCity = findViewById(R.id.et_candidate_city);

        etCandidateAddressLine1 = findViewById(R.id.et_candidate_address_line_1);

        etCandidateAddressLine2 = findViewById(R.id.et_candidate_address_line_2);

        etCandidateMotherTongue = findViewById(R.id.et_candidate_mother_tongue);

        cbAnyDisability = findViewById(R.id.cb_candidate_disability);

        etCandidateDisabilityReason = findViewById(R.id.et_candidate_disability_reason);

        etCandidatesPreferredTrade = findViewById(R.id.et_candidate_preferred_trade);
        etCandidatesPreferredTrade.setOnClickListener(this);
        etCandidatesPreferredTrade.setFocusable(false);

        etCandidatesPreferredTiming = findViewById(R.id.et_candidate_preferred_timing);
        etCandidatesPreferredTiming.setOnClickListener(this);
        etCandidatesPreferredTiming.setFocusable(false);

        mQualificationList.add("School");
        mQualificationList.add("UG");
        mQualificationList.add("PG");
        mQualificationList.add("Diploma");
        mQualificationList.add("Other");

        mQualificationAdapter = new ArrayAdapter<String>(this, R.layout.gender_layout, R.id.gender_name, mCasteList) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Log.d(TAG, "getview called" + position);
                View view = getLayoutInflater().inflate(R.layout.gender_layout, parent, false);
                TextView gendername = (TextView) view.findViewById(R.id.gender_name);
                gendername.setText(mQualificationList.get(position));

                // ... Fill in other views ...
                return view;
            }
        };

        etCandidatesQualification = findViewById(R.id.et_candidate_qualification);
        etCandidatesQualification.setFocusable(false);
        etCandidatesQualification.setClickable(false);
        etCandidatesQualification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQualificationList();
            }
        });

        mPassList.add("PASS");
        mPassList.add("FAIL");
        mPassList.add("DROPOUT");

        mPassAdapter = new ArrayAdapter<String>(this, R.layout.gender_layout, R.id.gender_name, mPassList) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Log.d(TAG, "getview called" + position);
                View view = getLayoutInflater().inflate(R.layout.gender_layout, parent, false);
                TextView gendername = (TextView) view.findViewById(R.id.gender_name);
                gendername.setText(mPassList.get(position));

                // ... Fill in other views ...
                return view;
            }
        };

        etCandidatesLastInstitute = findViewById(R.id.et_candidate_last_institute);

        etCandidatesQualifiedPromotion = findViewById(R.id.et_candidate_qualified_promotion);

        etCandidatesQualifiedPromotion.setFocusable(false);
        etCandidatesQualifiedPromotion.setClickable(false);
        etCandidatesQualifiedPromotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPassList();
            }
        });

        etCandidatesTC = findViewById(R.id.et_candidate_tc);

        cbCandidatesTC = findViewById(R.id.cb_candidate_tc);

        cbCandidatesAadhaarStatus = findViewById(R.id.cb_candidate_have_aadhaar_card);

        etCandidatesAadhaarNo = findViewById(R.id.et_candidate_aadhaar_card_number);


        qualificationDetails = findViewById(R.id.et_candidate_qualification_details);

        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 1980; i <= thisYear; i++) {
            mYearList.add(Integer.toString(i));
        }
        Collections.reverse(mYearList);
        mYearAdapter = new ArrayAdapter<String>(this, R.layout.gender_layout, R.id.gender_name, mYearList) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Log.d(TAG, "getview called" + position);
                View view = getLayoutInflater().inflate(R.layout.gender_layout, parent, false);
                TextView gendername = (TextView) view.findViewById(R.id.gender_name);
                gendername.setText(mYearList.get(position));

                // ... Fill in other views ...
                return view;
            }
        };

        yearOfEdu = findViewById(R.id.et_qualification_year_start);
        yearOfEdu.setFocusable(false);

        yearOfPass = findViewById(R.id.et_qualification_year_end);
        yearOfPass.setFocusable(false);

        yearOfEdu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEduYearList();
            }
        });

        yearOfPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPassYearList();
            }
        });

        iMarkOne = findViewById(R.id.et_candidate_identity_one);

        iMarkTwo = findViewById(R.id.et_candidate_identity_two);

        langKnown = findViewById(R.id.et_candidate_languages);

        motherMob = findViewById(R.id.et_mother_mobile_no);

        fatherMob = findViewById(R.id.et_father_mobile_no);

        headFamilyName = findViewById(R.id.et_head_of_the_family);

        eduHeadOfFamily = findViewById(R.id.et_head_of_the_family_qualification);

        familyMembers = findViewById(R.id.et_family_members);

        yearlyIncome = findViewById(R.id.et_family_income);

        jobCard = findViewById(R.id.et_job_card);
        jobCard.setFocusable(false);
        jobCard.setClickable(false);

        btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);


        mDateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        etCandidateDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "birthday widget selected");
                showBirthdayDate();
            }
        });

        mGenderList.add("Male");
        mGenderList.add("Female");
        mGenderList.add("Other");
        mGenderList.add("Rather not say");

        mGenderAdapter = new ArrayAdapter<String>(this, R.layout.gender_layout, R.id.gender_name, mGenderList) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Log.d(TAG, "getview called" + position);
                View view = getLayoutInflater().inflate(R.layout.gender_layout, parent, false);
                TextView gendername = (TextView) view.findViewById(R.id.gender_name);
                gendername.setText(mGenderList.get(position));

                // ... Fill in other views ...
                return view;
            }
        };

        etCandidateSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGenderList();
            }
        });


        mJobCardList.add("MGNRGEA Job Card");
        mJobCardList.add("BPL/PIP Card");

        mJobCardAdapter = new ArrayAdapter<String>(this, R.layout.gender_layout, R.id.gender_name, mJobCardList) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Log.d(TAG, "getview called" + position);
                View view = getLayoutInflater().inflate(R.layout.gender_layout, parent, false);
                TextView gendername = (TextView) view.findViewById(R.id.gender_name);
                gendername.setText(mJobCardList.get(position));

                // ... Fill in other views ...
                return view;
            }
        };

        jobCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showJobCardList();
            }
        });

        cbAnyDisability.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // perform logic
                    etCandidateDisabilityReason.setVisibility(View.VISIBLE);
                } else {
                    etCandidateDisabilityReason.setVisibility(View.GONE);
                }

            }
        });

        /*cbCandidatesAadhaarStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // perform logic
                    etCandidatesAadhaarNo.setVisibility(View.VISIBLE);
                } else {
                    etCandidatesAadhaarNo.setVisibility(View.VISIBLE);
                }
            }
        });*/

//        GetBloodGroup();
        if (!CommonUtils.isNetworkAvailableNew(this)) {
            Cursor c = database.getStoredTradeData();
            String traId = "";
            String traName = "";
            tradeList = new ArrayList<>();

            if (c.getCount() > 0) {
                if (c.moveToFirst()) {
                    do {
                        traId = c.getString(1);
                        traName = c.getString(2);


                        tradeList.add(new StoreTrade(traId, traName));

                    } while (c.moveToNext());
                }
            }
            Log.d(TAG, "TradeLIStaskldja is" + tradeList);
            mTradeAdapter = new ArrayAdapter<StoreTrade>(getApplicationContext(), R.layout.gender_layout, R.id.gender_name, tradeList) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    Log.d(TAG, "getview called" + position);
                    View view = getLayoutInflater().inflate(R.layout.gender_layout, parent, false);
                    TextView gendername = (TextView) view.findViewById(R.id.gender_name);
                    gendername.setText(tradeList.get(position).getTradeName());

                    // ... Fill in other views ...
                    return view;
                }
            };
        } else {
            GetTrade();
        }
        String checkFromAadhaarScan = "";

        checkFromAadhaarScan = PreferenceStorage.getAadhaarAction(getApplicationContext());

        if (checkFromAadhaarScan.equalsIgnoreCase("aadhaar")) {
            // init storage
            storage = new Storage(this);

            // read data from storage
            String storageData = storage.readFromFile();

            //check if file is not empty
            if (storageData.length() > 0) {
                try {
                    // variables to contain extracted values
                    String uid = "", name = "", gender = "", yearOfBirth = "", careOf = "", house = "", street = "",
                            location = "", villageTehsil = "", postOffice = "", district = "", state = "",
                            postCode = "", dob = "";
                    // convert JSON string to array
                    storageDataArray = new JSONArray(storageData);

                    // handle case of empty JSONArray after delete
                    if (storageDataArray.length() < 1) {
                        // hide list and show message
//                        tv_no_saved_card.setVisibility(View.VISIBLE);
//                        lv_saved_card_list.setVisibility(View.GONE);
                        //exit
                        return;
                    }

                    // init data list
//                    cardDataList = new <JSONObject>ArrayList();

                    //prepare the data list for list adapter
                    for (int i = 0; i < storageDataArray.length(); i++) {
                        JSONObject dataObject = storageDataArray.getJSONObject(i);
                        uid = dataObject.getString(DataAttributes.AADHAR_UID_ATTR);
                        name = dataObject.getString(DataAttributes.AADHAR_NAME_ATTR);
                        gender = dataObject.getString(DataAttributes.AADHAR_GENDER_ATTR);
                        yearOfBirth = dataObject.getString(DataAttributes.AADHAR_YOB_ATTR);
                        careOf = dataObject.getString(DataAttributes.AADHAR_CO_ATTR);
                        house = dataObject.getString(DataAttributes.AADHAR_HOUSE);
                        street = dataObject.getString(DataAttributes.AADHAR_STREET);
                        location = dataObject.getString(DataAttributes.AADHAR_LOCATION);
                        villageTehsil = dataObject.getString(DataAttributes.AADHAR_VTC_ATTR);
                        postOffice = dataObject.getString(DataAttributes.AADHAR_PO_ATTR);
                        district = dataObject.getString(DataAttributes.AADHAR_DIST_ATTR);
                        state = dataObject.getString(DataAttributes.AADHAR_STATE_ATTR);
                        postCode = dataObject.getString(DataAttributes.AADHAR_PC_ATTR);
                        dob = dataObject.getString(DataAttributes.AADHAR_DOB);
//                        cardDataList.add(dataObject);
                    }

                    etCandidateName.setText(name);
                    if (gender.equalsIgnoreCase("M")) {
                        etCandidateSex.setText("Male");
                    } else if (gender.equalsIgnoreCase("F")) {
                        etCandidateSex.setText("Female");
                    } else {
                        etCandidateSex.setText("Others");
                    }
                    etCandidateAddressLine1.setText(house + " " + street + " " + location);
                    etCandidateFatherName.setText(careOf);
                    etCandidateCity.setText(district);
                    etCandidateDOB.setText(dob);
                    etCandidateState.setText(state);
                    etCandidateNationality.setText("Indian");
                    cbCandidatesAadhaarStatus.setVisibility(View.VISIBLE);
                    cbCandidatesAadhaarStatus.setChecked(true);

                    /*String dobFormat = "", ageFormat = "";
                    if (dob != null && dob != "") {

                        String date = dob;
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
                        Date testDate = null;
                        try {
                            testDate = sdf.parse(date);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-mm-yyyy");
                        dobFormat = formatter.format(testDate);
                        System.out.println(".....Date..." + dobFormat);

                        SimpleDateFormat ageFormatter = new SimpleDateFormat("yyy-MM-dd");
                        ageFormat = ageFormatter.format(testDate);

                    }
                    etCandidateDOB.setText(dobFormat);
                    String getAgeToDisplay = "";
                    try {
                        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
                        Date date = (Date) formatter.parse(ageFormat);
                        SimpleDateFormat year_date = new SimpleDateFormat("yyyy");
                        String year_name = year_date.format(date.getTime());
                        SimpleDateFormat month_date = new SimpleDateFormat("mm");
                        String month_name = month_date.format(date.getTime());
                        SimpleDateFormat event_date = new SimpleDateFormat("dd");
                        String date_name = event_date.format(date.getTime());
                        if ((ageFormat != null)) {
//                            viewDateFormat.setText(date_name + "\n" + month_name);
                            getAgeToDisplay = getAge(Integer.parseInt(year_name), Integer.parseInt(month_name), Integer.parseInt(date_name));
//                            int year, int month, int day
                        } else {
//                            viewDateFormat.setText("N/A");
                        }
                    } catch (final ParseException e) {
                        e.printStackTrace();
                    }

                    etCandidateAge.setText(getAgeToDisplay);*/

                    etCandidatesAadhaarNo.setText(uid);

                    for (int i = 0; i < storageDataArray.length(); i++) {
                        JSONObject dataObject = storageDataArray.getJSONObject(i);
                        String uid_del = dataObject.getString(DataAttributes.AADHAR_UID_ATTR);
                        deleteCard(uid_del);
                    }

                    PreferenceStorage.saveAadhaarAction(getApplicationContext(), "");

                    // create List Adapter with data
//                    ArrayAdapter<ArrayList> savedCardListAdapter = new CardListAdapter(this,cardDataList);
                    // populate list
//                    lv_saved_card_list.setAdapter(savedCardListAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                // hide list and show message
//                tv_no_saved_card.setVisibility(View.VISIBLE);
//                lv_saved_card_list.setVisibility(View.GONE);
            }
        }
    }

    private void updateCandidate() {

        checkInternalState = "updateStudent";

        String CandidateName = etCandidateName.getText().toString();
        String CandidateSex = etCandidateSex.getText().toString();
        String CandidateDOB = etCandidateDOB.getText().toString();
        String CandidateAge = etCandidateAge.getText().toString();
        String CandidateNationality = etCandidateNationality.getText().toString();
        String CandidateReligion = etCandidateReligion.getText().toString();
        String CandidateCommunityClass = etCandidateCommunityClass.getText().toString();
        String CandidateCommunity = etCandidateCommunity.getText().toString();
        String CandidateBloodGroup = etCandidateBloodGroup.getText().toString();
        String CandidateFatherName = etCandidateFatherName.getText().toString();
        String CandidateMotherName = etCandidateMotherName.getText().toString();
        String CandidateMobileNo = etCandidateMobileNo.getText().toString();
        String CandidateAlterMobileNo = etCandidateAlterMobileNo.getText().toString();
        String CandidateEmailId = etCandidateEmailId.getText().toString();
        String CandidateState = etCandidateState.getText().toString();
        String CandidateCity = etCandidateCity.getText().toString();
        String CandidateAddressLine1 = etCandidateAddressLine1.getText().toString();
        String CandidateAddressLine2 = etCandidateAddressLine2.getText().toString();
        String CandidateMotherTongue = etCandidateMotherTongue.getText().toString();
        String AnyDisability = "" + cbAnyDisability.isChecked();
        String CandidateDisabilityReason = "";
        if (cbAnyDisability.isChecked()) {
            AnyDisability = "1";
            CandidateDisabilityReason = etCandidateDisabilityReason.getText().toString();
        } else {
            AnyDisability = "0";
            CandidateDisabilityReason = "";
        }
        String CandidatesPreferredTrade = etCandidatesPreferredTrade.getText().toString();
        String CandidatesPreferredTiming = etCandidatesPreferredTiming.getText().toString();
        String CandidatesQualification = etCandidatesQualification.getText().toString();
        String CandidatesLastInstitute = etCandidatesLastInstitute.getText().toString();
        String CandidatesQualifiedPromotion = etCandidatesQualifiedPromotion.getText().toString();
//        String CandidatesTC = etCandidatesTC.getText().toString();
        String CandidatesTC = "";
        if (cbCandidatesTC.isChecked()) {
            CandidatesTC = "1";
        } else {
            CandidatesTC = "0";
        }
        String CandidatesAadhaarStatus = "" + cbCandidatesAadhaarStatus.isChecked();
        String CandidatesAadhaarNo = "";
        if (cbCandidatesAadhaarStatus.isChecked()) {
            CandidatesAadhaarStatus = "1";
            CandidatesAadhaarNo = etCandidatesAadhaarNo.getText().toString();
        } else {
            CandidatesAadhaarStatus = "0";
            CandidatesAadhaarNo = "";
        }
        String CandidatesqualificationDetails = qualificationDetails.getText().toString();
        String CandidatesyearOfEdu = yearOfEdu.getText().toString();
        String CandidatesyearOfPass = yearOfPass.getText().toString();
        String CandidatesiMarkOne = iMarkOne.getText().toString();
        String CandidatesiMarkTwo = iMarkTwo.getText().toString();
        String CandidateslangKnown = langKnown.getText().toString();
        String CandidatesmotherMob = motherMob.getText().toString();
        String CandidatesfatherMob = fatherMob.getText().toString();
        String CandidatesheadFamilyName = headFamilyName.getText().toString();
        String CandidateseduHeadOfFamily = eduHeadOfFamily.getText().toString();
        String CandidatesfamilyMembers = familyMembers.getText().toString();
        String CandidatesyearlyIncome = yearlyIncome.getText().toString();


        String CandidatesJobCard = "";
        if (jobCard.getText().toString().equalsIgnoreCase("MGNRGEA Job Card")) {
            CandidatesJobCard = "MG";
        } else if (jobCard.getText().toString().equalsIgnoreCase("BPL/PIP Card")) {
            CandidatesJobCard = "BP";
        }

        PreferenceStorage.saveCaste(this, CandidateCommunityClass);
        PreferenceStorage.saveDisability(this, AnyDisability);
        PreferenceStorage.saveAdmissionId(this, allProspects.getId());

        String serverFormatDate = "";
        if (etCandidateDOB.getText().toString() != null && etCandidateDOB.getText().toString() != "") {

            String date = etCandidateDOB.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date testDate = null;
            try {
                testDate = sdf.parse(date);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            serverFormatDate = formatter.format(testDate);
            System.out.println(".....Date..." + serverFormatDate);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date currentTime = Calendar.getInstance().getTime();
        String currentDateandTime = sdf.format(currentTime);

        locationAddressResult = getCompleteAddressString(currentLatitude, currentLongitude);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(MobilizerConstants.PARAMS_HAVE_AADHAAR_CARD, CandidatesAadhaarStatus);
            jsonObject.put(MobilizerConstants.PARAMS_AADHAAR_CARD_NUMBER, CandidatesAadhaarNo);
            jsonObject.put(MobilizerConstants.PARAMS_NAME, CandidateName);
            jsonObject.put(MobilizerConstants.PARAMS_SEX, CandidateSex);
            jsonObject.put(MobilizerConstants.PARAMS_DOB, serverFormatDate);
            jsonObject.put(MobilizerConstants.PARAMS_AGE, CandidateAge);
            jsonObject.put(MobilizerConstants.PARAMS_NATIONALITY, CandidateNationality);
            jsonObject.put(MobilizerConstants.PARAMS_RELIGION, CandidateReligion);
            jsonObject.put(MobilizerConstants.PARAMS_COMMUNITY_CLASS, CandidateCommunityClass);
            jsonObject.put(MobilizerConstants.PARAMS_COMMUNITY, CandidateCommunity);
            jsonObject.put(MobilizerConstants.PARAMS_FATHER_NAME, CandidateFatherName);
            jsonObject.put(MobilizerConstants.PARAMS_MOTHER_NAME, CandidateMotherName);
            jsonObject.put(MobilizerConstants.PARAMS_MOBILE, CandidateMobileNo);
            jsonObject.put(MobilizerConstants.PARAMS_SEC_MOBILE, CandidateAlterMobileNo);
            jsonObject.put(MobilizerConstants.PARAMS_EMAIL, CandidateEmailId);
            jsonObject.put(MobilizerConstants.PARAMS_STATE, CandidateState);
            jsonObject.put(MobilizerConstants.PARAMS_CITY, CandidateCity);
            jsonObject.put(MobilizerConstants.PARAMS_ADDRESS, CandidateAddressLine1);
            jsonObject.put(MobilizerConstants.PARAMS_MOTHER_TONGUE, CandidateMotherTongue);
            jsonObject.put(MobilizerConstants.PARAMS_DISABILITY, AnyDisability);
            jsonObject.put(MobilizerConstants.PARAMS_BLOOD_GROUP, CandidateBloodGroup);
            jsonObject.put(MobilizerConstants.PARAMS_ADMISSION_DATE, currentDateandTime);
            jsonObject.put(MobilizerConstants.PARAMS_ADMISSION_LOCATION, locationAddressResult);
            jsonObject.put(MobilizerConstants.PARAMS_ADMISSION_LATITUDE, "" + currentLatitude);
            jsonObject.put(MobilizerConstants.PARAMS_ADMISSION_LONGITUDE, "" + currentLongitude);
            jsonObject.put(MobilizerConstants.PARAMS_PREFERRED_TRADE, tradeId);
            jsonObject.put(MobilizerConstants.PARAMS_PREFERRED_TIMING, CandidatesPreferredTiming);
            jsonObject.put(MobilizerConstants.PARAMS_LAST_INSTITUTE, CandidatesLastInstitute);
            jsonObject.put(MobilizerConstants.PARAMS_LAST_STUDIED, CandidatesQualification);
            jsonObject.put(MobilizerConstants.PARAMS_QUALIFIED_PROMOTION, CandidatesQualifiedPromotion);
//            jsonObject.put(MobilizerConstants.PARAMS_TRANSFER_CERTIFICATE, CandidatesTC);
            jsonObject.put(MobilizerConstants.PARAMS_STATUS, "Pending");
//            jsonObject.put(MobilizerConstants.PARAMS_CREATED_BY, PreferenceStorage.getUserId(getApplicationContext()));
//            jsonObject.put(MobilizerConstants.PARAMS_CREATED_AT, currentDateandTime);
            jsonObject.put(MobilizerConstants.KEY_USER_ID, PreferenceStorage.getUserId(getApplicationContext()));
            jsonObject.put(MobilizerConstants.PARAMS_ADMISSION_ID, allProspects.getId());
            jsonObject.put(MobilizerConstants.PARAMS_FATHER_MOBILE, CandidatesfatherMob);
            jsonObject.put(MobilizerConstants.PARAMS_MOTHER_MOBILE, CandidatesmotherMob);
            jsonObject.put(MobilizerConstants.KEY_QUALIFICATION, CandidatesQualification);
            jsonObject.put(MobilizerConstants.PARAMS_QUALIFICATION_DETAILS, CandidatesqualificationDetails);
            jsonObject.put(MobilizerConstants.PARAMS_YEAR_OF_EDU, CandidatesyearOfEdu);
            jsonObject.put(MobilizerConstants.PARAMS_YEAR_OF_PASS, CandidatesyearOfPass);
            jsonObject.put(MobilizerConstants.PARAMS_IDENTITY_MARK_ONE, CandidatesiMarkOne);
            jsonObject.put(MobilizerConstants.PARAMS_IDENTITY_MARK_TWO, CandidatesiMarkTwo);
            jsonObject.put(MobilizerConstants.PARAMS_LANGUAGES_KNOWN, CandidateslangKnown);
            jsonObject.put(MobilizerConstants.PARAMS_HEAD_OF_FAMILY, CandidatesheadFamilyName);
            jsonObject.put(MobilizerConstants.PARAMS_EDU_OF_HEAD_OF_FAMILY, CandidateseduHeadOfFamily);
            jsonObject.put(MobilizerConstants.PARAMS_NO_OF_FAMILY, CandidatesfamilyMembers);
            jsonObject.put(MobilizerConstants.PARAMS_YEARLY_INCOME, CandidatesyearlyIncome);
            jsonObject.put(MobilizerConstants.PARAMS_JOB_CARD, CandidatesJobCard);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = MobilizerConstants.BUILD_URL + MobilizerConstants.UPDATE_CANDIDATE;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private boolean validateFields() {

        if (!AppValidator.checkNullString(this.etCandidateName.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter candidate's name");
            return false;
        } else if (!AppValidator.checkNullString(this.etCandidateSex.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Select candidate's gender");
            return false;
        } else if (!AppValidator.checkNullString(this.etCandidateDOB.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter candidate's date of birth");
            return false;
        } else if (!AppValidator.checkNullString(this.etCandidateNationality.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter candidate's nationality");
            return false;
        } else if (!AppValidator.checkNullString(this.etCandidateReligion.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter candidate's religion");
            return false;
        } else if (!AppValidator.checkNullString(this.etCandidateCommunityClass.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter candidate's community class");
            return false;
        } else if (!AppValidator.checkNullString(this.etCandidateCommunity.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter candidate's community");
            return false;
        } else if (!AppValidator.checkNullString(this.etCandidateBloodGroup.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Select candidate's blood group");
            return false;
        } else if (!AppValidator.checkNullString(this.etCandidateFatherName.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter candidate's father's name");
            return false;
        } else if (!AppValidator.checkNullString(this.etCandidateMotherName.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter candidate's mother's name");
            return false;
        } else if (!AppValidator.checkNullString(this.etCandidateMobileNo.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter candidate's mobile number");
            return false;
        } else if (!AppValidator.checkNullString(this.etCandidateState.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter candidate's state");
            return false;
        } else if (!AppValidator.checkNullString(this.etCandidateCity.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter candidate's city");
            return false;
        } else if (!AppValidator.checkNullString(this.etCandidateAddressLine1.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter candidate's address");
            return false;
        } else if (!AppValidator.checkNullString(this.etCandidateMotherTongue.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter candidate's mother tongue");
            return false;
        } else if (!AppValidator.checkNullString(this.etCandidatesPreferredTrade.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter candidate's course");
            return false;
        } /*else if (!AppValidator.checkNullString(this.etCandidatesPreferredTiming.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter valid trade timings");
            return false;
        }*/ else if (!AppValidator.checkNullString(this.etCandidatesAadhaarNo.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter candidate's aadhaar card number");
            return false;
        }else if (!AppValidator.checkNullString(this.etCandidatesQualification.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Select candidate's qualification");
            return false;
        }else if (!AppValidator.checkNullString(this.qualificationDetails.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter candidate's qualification detail");
            return false;
        }else if (!AppValidator.checkNullString(this.yearOfEdu.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Select candidate's year of admission");
            return false;
        }else if (!AppValidator.checkNullString(this.yearOfPass.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Select candidate's year of graduation");
            return false;
        }else if (!AppValidator.checkNullString(this.iMarkOne.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter candidate's identification mark");
            return false;
        }else if (!AppValidator.checkNullString(this.iMarkTwo.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter candidate's identification mark");
            return false;
        }else if (!AppValidator.checkNullString(this.langKnown.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter candidate's known languages");
            return false;
        }else if (!AppValidator.checkNullString(this.motherMob.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter mother's mobile number");
            return false;
        }else if (!AppValidator.checkNullString(this.fatherMob.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter father's mobile number");
            return false;
        }else if (!AppValidator.checkNullString(this.headFamilyName.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter family head name");
            return false;
        }else if (!AppValidator.checkNullString(this.eduHeadOfFamily.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter family head education");
            return false;
        }else if (!AppValidator.checkNullString(this.familyMembers.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter number of family member");
            return false;
        }else if (!AppValidator.checkNullString(this.yearlyIncome.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter annual salary");
            return false;
        } else if (!imgurl) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Add student image");
            return false;
        } else if (Integer.parseInt(yearOfEdu.getText().toString().trim()) > Integer.parseInt(yearOfPass.getText().toString().trim()) ) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Select Proper Year Of Education and Year of Passing");
            return false;
        } else {
            return true;
        }
    }

    void saveProfile() {
      /*  mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Updating Profile");
        mProgressDialog.show();
        if ((mActualFilePath != null)) {
            Log.d(TAG, "Update profile picture");
//            saveUserImage();
        } else {*/
        saveCandidate();
//        }
    }

    private void saveUserImage() {

        mUpdatedImageUrl = null;

        new UploadFileToServer().execute();
    }

    private void saveCandidate() {

        checkInternalState = "addStudent";
        String CandidateName = etCandidateName.getText().toString();
        String CandidateSex = etCandidateSex.getText().toString();
        String CandidateDOB = etCandidateDOB.getText().toString();
        String CandidateAge = etCandidateAge.getText().toString();
        String CandidateNationality = etCandidateNationality.getText().toString();
        String CandidateReligion = etCandidateReligion.getText().toString();
        String CandidateCommunityClass = etCandidateCommunityClass.getText().toString();
        String CandidateCommunity = etCandidateCommunity.getText().toString();
        String CandidateBloodGroup = etCandidateBloodGroup.getText().toString();
        String CandidateFatherName = etCandidateFatherName.getText().toString();
        String CandidateMotherName = etCandidateMotherName.getText().toString();
        String CandidateMobileNo = etCandidateMobileNo.getText().toString();
        String CandidateAlterMobileNo = etCandidateAlterMobileNo.getText().toString();
        String CandidateEmailId = etCandidateEmailId.getText().toString();
        String CandidateState = etCandidateState.getText().toString();
        String CandidateCity = etCandidateCity.getText().toString();
        String CandidateAddressLine1 = etCandidateAddressLine1.getText().toString();
        String CandidateAddressLine2 = etCandidateAddressLine2.getText().toString();
        String CandidateMotherTongue = etCandidateMotherTongue.getText().toString();
        String AnyDisability = "";
        String CandidateDisabilityReason = "";
        if (cbAnyDisability.isChecked()) {
            AnyDisability = "1";
            CandidateDisabilityReason = etCandidateDisabilityReason.getText().toString();
        } else {
            AnyDisability = "0";
            CandidateDisabilityReason = "";
        }
        String CandidatesPreferredTrade = etCandidatesPreferredTrade.getText().toString();
        String CandidatesPreferredTiming = etCandidatesPreferredTiming.getText().toString();
        String CandidatesQualification = etCandidatesQualification.getText().toString();
        String CandidatesLastInstitute = etCandidatesLastInstitute.getText().toString();
        String CandidatesQualifiedPromotion = etCandidatesQualifiedPromotion.getText().toString();
//        String CandidatesTC = etCandidatesTC.getText().toString();
        String CandidatesTC = "";
        if (cbCandidatesTC.isChecked()) {
            CandidatesTC = "1";
        } else {
            CandidatesTC = "0";
        }
        String CandidatesAadhaarStatus = "" + cbCandidatesAadhaarStatus.isChecked();
        String CandidatesAadhaarNo = "";
        if (cbCandidatesAadhaarStatus.isChecked()) {
            CandidatesAadhaarStatus = "1";
            CandidatesAadhaarNo = etCandidatesAadhaarNo.getText().toString();
        } else {
            CandidatesAadhaarStatus = "0";
            CandidatesAadhaarNo = "";
        }

        String serverFormatDate = "";
        if (etCandidateDOB.getText().toString() != null && etCandidateDOB.getText().toString() != "") {

            String date = etCandidateDOB.getText().toString();
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
        String CandidatesqualificationDetails = qualificationDetails.getText().toString();
        String CandidatesyearOfEdu = yearOfEdu.getText().toString();
        String CandidatesyearOfPass = yearOfPass.getText().toString();
        String CandidatesiMarkOne = iMarkOne.getText().toString();
        String CandidatesiMarkTwo = iMarkTwo.getText().toString();
        String CandidateslangKnown = langKnown.getText().toString();
        String CandidatesmotherMob = motherMob.getText().toString();
        String CandidatesfatherMob = fatherMob.getText().toString();
        String CandidatesheadFamilyName = headFamilyName.getText().toString();
        String CandidateseduHeadOfFamily = eduHeadOfFamily.getText().toString();
        String CandidatesfamilyMembers = familyMembers.getText().toString();
        String CandidatesyearlyIncome = yearlyIncome.getText().toString();


        String CandidatesJobCard = "";
        if (jobCard.getText().toString().equalsIgnoreCase("MGNRGEA Job Card")) {
            CandidatesJobCard = "MG";
        } else if (jobCard.getText().toString().equalsIgnoreCase("BPL/PIP Card")) {
            CandidatesJobCard = "BP";
        }

        PreferenceStorage.saveCaste(this, CandidateCommunityClass);
        PreferenceStorage.saveDisability(this, AnyDisability);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        String cLat = "" + currentLatitude;
        String cLong = "" + currentLongitude;
        String timeing = "";
        String stattt = "Pending";
        String userIID = PreferenceStorage.getUserId(this);
        String PIAiid = PreferenceStorage.getPIAId(this);
        locationAddressResult = getCompleteAddressString(currentLatitude, currentLongitude);
        if (!CommonUtils.isNetworkAvailableNew(this)) {
            long x = database.store_prospect_data_insert(
                    CandidatesAadhaarStatus,
                    CandidatesAadhaarNo,
                    CandidateName,
                    CandidateSex,
                    serverFormatDate,
                    CandidateAge,
                    CandidateNationality,
                    CandidateReligion,
                    CandidateCommunityClass,
                    CandidateCommunity,
                    CandidateFatherName,
                    CandidateMotherName,
                    CandidateMobileNo,
                    CandidateAlterMobileNo,
                    CandidateEmailId,
                    CandidateState,
                    CandidateCity,
                    CandidateAddressLine1,
                    CandidateMotherTongue,
                    AnyDisability,
                    bloodGroupId,
                    currentDateandTime,
                    locationAddressResult,
                    cLat,
                    cLong,
                    tradeId,
                    CandidatesLastInstitute,
                    CandidatesQualification,
                    CandidatesQualifiedPromotion,
                    userIID,
                    currentDateandTime,
                    PIAiid,
                    mActualFilePath,
                    CandidatesqualificationDetails,
                    CandidatesyearOfEdu,
                    CandidatesyearOfPass,
                    CandidatesiMarkOne,
                    CandidatesiMarkTwo,
                    CandidateslangKnown,
                    CandidatesmotherMob,
                    CandidatesfatherMob,
                    CandidatesheadFamilyName,
                    CandidateseduHeadOfFamily,
                    CandidatesfamilyMembers,
                    CandidatesyearlyIncome,
                    CandidatesJobCard);

            System.out.println("Stored Id : " + x);
            String dbId = String.valueOf(x);
            PreferenceStorage.saveDbID(getApplicationContext(), dbId);
            Intent i = new Intent(getApplicationContext(), DocumentUploadActivity.class);
//            i.putExtra("job_card", jobCard.getText().toString());
            startActivity(i);
            finish();
//            Toast.makeText(this, "Prospect details stored. Sync later when network is available", Toast.LENGTH_SHORT).show();
//            finish();
        } else {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(MobilizerConstants.PARAMS_HAVE_AADHAAR_CARD, CandidatesAadhaarStatus);
                jsonObject.put(MobilizerConstants.PARAMS_AADHAAR_CARD_NUMBER, CandidatesAadhaarNo);
                jsonObject.put(MobilizerConstants.PARAMS_NAME, CandidateName);
                jsonObject.put(MobilizerConstants.PARAMS_SEX, CandidateSex);
                jsonObject.put(MobilizerConstants.PARAMS_DOB, serverFormatDate);
                jsonObject.put(MobilizerConstants.PARAMS_AGE, CandidateAge);
                jsonObject.put(MobilizerConstants.PARAMS_NATIONALITY, CandidateNationality);
                jsonObject.put(MobilizerConstants.PARAMS_RELIGION, CandidateReligion);
                jsonObject.put(MobilizerConstants.PARAMS_COMMUNITY_CLASS, CandidateCommunityClass);
                jsonObject.put(MobilizerConstants.PARAMS_COMMUNITY, CandidateCommunity);
                jsonObject.put(MobilizerConstants.PARAMS_FATHER_NAME, CandidateFatherName);
                jsonObject.put(MobilizerConstants.PARAMS_MOTHER_NAME, CandidateMotherName);
                jsonObject.put(MobilizerConstants.PARAMS_MOBILE, CandidateMobileNo);
                jsonObject.put(MobilizerConstants.PARAMS_SEC_MOBILE, CandidateAlterMobileNo);
                jsonObject.put(MobilizerConstants.PARAMS_EMAIL, CandidateEmailId);
                jsonObject.put(MobilizerConstants.PARAMS_STATE, CandidateState);
                jsonObject.put(MobilizerConstants.PARAMS_CITY, CandidateCity);
                jsonObject.put(MobilizerConstants.PARAMS_ADDRESS, CandidateAddressLine1);
                jsonObject.put(MobilizerConstants.PARAMS_MOTHER_TONGUE, CandidateMotherTongue);
                jsonObject.put(MobilizerConstants.PARAMS_DISABILITY, AnyDisability);
                jsonObject.put(MobilizerConstants.PARAMS_BLOOD_GROUP, bloodGroupId);
                jsonObject.put(MobilizerConstants.PARAMS_ADMISSION_DATE, currentDateandTime);
                jsonObject.put(MobilizerConstants.PARAMS_ADMISSION_LOCATION, locationAddressResult);
                jsonObject.put(MobilizerConstants.PARAMS_ADMISSION_LATITUDE, "" + currentLatitude);
                jsonObject.put(MobilizerConstants.PARAMS_ADMISSION_LONGITUDE, "" + currentLongitude);
                jsonObject.put(MobilizerConstants.PARAMS_PREFERRED_TRADE, tradeId);
                jsonObject.put(MobilizerConstants.PARAMS_PREFERRED_TIMING, "");
                jsonObject.put(MobilizerConstants.PARAMS_LAST_INSTITUTE, CandidatesLastInstitute);
                jsonObject.put(MobilizerConstants.PARAMS_LAST_STUDIED, CandidatesQualification);
                jsonObject.put(MobilizerConstants.PARAMS_QUALIFIED_PROMOTION, CandidatesQualifiedPromotion);
//            jsonObject.put(MobilizerConstants.PARAMS_TRANSFER_CERTIFICATE, CandidatesTC);
                jsonObject.put(MobilizerConstants.PARAMS_STATUS, "Pending");
                jsonObject.put(MobilizerConstants.PARAMS_CREATED_BY, PreferenceStorage.getUserId(getApplicationContext()));
                jsonObject.put(MobilizerConstants.PARAMS_CREATED_AT, currentDateandTime);
                jsonObject.put(MobilizerConstants.PARAMS_PIA_ID, PreferenceStorage.getPIAId(getApplicationContext()));
                jsonObject.put(MobilizerConstants.PARAMS_FATHER_MOBILE, CandidatesfatherMob);
                jsonObject.put(MobilizerConstants.PARAMS_MOTHER_MOBILE, CandidatesmotherMob);
                jsonObject.put(MobilizerConstants.KEY_QUALIFICATION, CandidatesQualification);
                jsonObject.put(MobilizerConstants.PARAMS_QUALIFICATION_DETAILS, CandidatesqualificationDetails);
                jsonObject.put(MobilizerConstants.PARAMS_YEAR_OF_EDU, CandidatesyearOfEdu);
                jsonObject.put(MobilizerConstants.PARAMS_YEAR_OF_PASS, CandidatesyearOfPass);
                jsonObject.put(MobilizerConstants.PARAMS_IDENTITY_MARK_ONE, CandidatesiMarkOne);
                jsonObject.put(MobilizerConstants.PARAMS_IDENTITY_MARK_TWO, CandidatesiMarkTwo);
                jsonObject.put(MobilizerConstants.PARAMS_LANGUAGES_KNOWN, CandidateslangKnown);
                jsonObject.put(MobilizerConstants.PARAMS_HEAD_OF_FAMILY, CandidatesheadFamilyName);
                jsonObject.put(MobilizerConstants.PARAMS_EDU_OF_HEAD_OF_FAMILY, CandidateseduHeadOfFamily);
                jsonObject.put(MobilizerConstants.PARAMS_NO_OF_FAMILY, CandidatesfamilyMembers);
                jsonObject.put(MobilizerConstants.PARAMS_YEARLY_INCOME, CandidatesyearlyIncome);
                jsonObject.put(MobilizerConstants.PARAMS_JOB_CARD, CandidatesJobCard);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            String url = MobilizerConstants.BUILD_URL + MobilizerConstants.ADD_CANDIDATE;
            serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
        }
    }

    /**
     * Uploading the file to server
     */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        private static final String TAG = "UploadFileToServer";
        private HttpClient httpclient;
        HttpPost httppost;
        public boolean isTaskAborted = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {

        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            httpclient = new DefaultHttpClient();
            httppost = new HttpPost(String.format(MobilizerConstants.BUILD_URL + MobilizerConstants.UPLOAD_CANDIDATE_PIC + admissionId));

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {

                            }
                        });
                Log.d(TAG, "actual file path is" + mActualFilePath);
                if (mActualFilePath != null) {

                    File sourceFile = new File(mActualFilePath);

                    // Adding file data to http body
                    //fileToUpload
                    entity.addPart("student_pic", new FileBody(sourceFile));

                    // Extra parameters if you want to pass to server
                    entity.addPart("admission_id", new StringBody(admissionId));
//                    entity.addPart("user_type", new StringBody(PreferenceStorage.getUserType(ProfileActivity.this)));

                    totalSize = entity.getContentLength();
                    httppost.setEntity(entity);

                    // Making server call
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity r_entity = response.getEntity();

                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == 200) {
                        // Server response
                        responseString = EntityUtils.toString(r_entity);
                        try {
                            JSONObject resp = new JSONObject(responseString);
                            String successVal = resp.getString("status");

                            mUpdatedImageUrl = resp.getString("student_picture");

                            Log.d(TAG, "updated image url is" + mUpdatedImageUrl);
                            if (successVal.equalsIgnoreCase("success")) {
                                Log.d(TAG, "Updated image succesfully");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        responseString = "Error occurred! Http Status Code: "
                                + statusCode;
                    }
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);

            super.onPostExecute(result);
            if ((result == null) || (result.isEmpty()) || (result.contains("Error"))) {
                Toast.makeText(AddCandidateActivity.this, "Unable to save picture", Toast.LENGTH_SHORT).show();
            } else {
                if (mUpdatedImageUrl != null) {
//                    PreferenceStorage.saveUserPicture(AddCandidateFragment.this, mUpdatedImageUrl);
                }
            }

            if (mProgressDialog != null) {
                mProgressDialog.cancel();
            }

//            Toast.makeText(getApplicationContext(), "Student profile image updated successfully...", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(), DocumentUploadActivity.class);
            i.putExtra("job_card", jobCard.getText().toString());
            startActivity(i);
            finish();
            PreferenceStorage.saveAdmissionId(getApplicationContext(), admissionId);
//            saveCandidate();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    private void openImageIntent() {

        // Determine Uri of camera image to save.
//        final File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyDir");
        File root = getExternalFilesDir(Environment.DIRECTORY_DCIM);

        if (!root.exists()) {
            if (!root.mkdirs()) {
                Log.d(TAG, "Failed to create directory for storing images");
                return;
            }
        }

        final String fname = PreferenceStorage.getUserId(this) + ".png";
        final File sdImageMainDirectory = new File(root.getPath() + File.separator + fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);
        Log.d(TAG, "camera output Uri" + outputFileUri);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_PICK);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Profile Photo");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, REQUEST_IMAGE_GET);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_IMAGE_GET) {
                d(TAG, "ONActivity Result");
                final boolean isCamera;
                if (data == null) {
                    d(TAG, "camera is true");
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    d(TAG, "camera action is" + action);
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                Uri selectedImageUri;

                if (isCamera) {
                    d(TAG, "Add to gallery");
                    selectedImageUri = outputFileUri;
                    mActualFilePath = outputFileUri.getPath();
                    galleryAddPic(selectedImageUri);
                } else {
                    selectedImageUri = data == null ? null : data.getData();
                    mActualFilePath = getRealPathFromURI(this, selectedImageUri);
                    d(TAG, "path to image is" + mActualFilePath);

                }
                d(TAG, "image Uri is" + selectedImageUri);
                if (selectedImageUri != null) {
                    d(TAG, "image URI is" + selectedImageUri);
                    imgurl = true;
                    setPic(selectedImageUri);
                }

            }
        }
    }

    private void galleryAddPic(Uri urirequest) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(urirequest.getPath());
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        String result = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);

            Cursor cursor = loader.loadInBackground();
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                result = cursor.getString(column_index);
                cursor.close();
            } else {
                Log.d(TAG, "cursor is null");
            }
        } catch (Exception e) {
            result = null;
            Toast.makeText(this, "Was unable to save  image", Toast.LENGTH_SHORT).show();

        } finally {
            return result;
        }
    }

    private void setPic(Uri selectedImageUri) {
        // Get the dimensions of the View
        int targetW = imCandidatePicture.getWidth();
        int targetH = imCandidatePicture.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(this.getContentResolver().openInputStream(selectedImageUri), null, bmOptions);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        mSelectedImageUri = selectedImageUri;

        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(selectedImageUri), null, bmOptions);
            imCandidatePicture.setImageBitmap(bitmap);
            mCurrentUserImageBitmap = bitmap;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        Calendar userAge = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        Calendar minAdultAge = new GregorianCalendar();
        minAdultAge.add(Calendar.YEAR, -15);
        if (minAdultAge.before(userAge)) {
//            SHOW_ERROR_MESSAGE;
            Toast.makeText(getApplicationContext(), "Age must be 15+", Toast.LENGTH_LONG).show();
        } else {
            etCandidateDOB.setText(mDateFormatter.format(userAge.getTime()));
            getAge(year, monthOfYear, dayOfMonth);
        }

//        Calendar newDate = Calendar.getInstance();
//        newDate.set(year, monthOfYear, dayOfMonth);
//        etCandidateDOB.setText(mDateFormatter.format(userAge.getTime()));
//        getAge(year, monthOfYear, dayOfMonth);
    }

    private void showBirthdayDate() {
        Log.d(TAG, "Show the birthday date");
        Calendar newCalendar = Calendar.getInstance();
        String currentdate = etCandidateDOB.getText().toString();
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

    private String getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        if (ageInt >= 10) {
            etCandidateAge.setText(ageS);
        } else {
            etCandidateAge.setText(ageS);
        }


        return ageS;
    }

    private void showGenderList() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);

        builderSingle.setTitle("Select Gender");
        View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);
        header.setText("Select Gender");
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(mGenderAdapter
                ,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = mGenderList.get(which);
                        etCandidateSex.setText(strName);
                    }
                });
        builderSingle.show();
    }

    private void showQualificationList() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);

        builderSingle.setTitle("Select Qualification");
        View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);
        header.setText("Select Qualification");
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(mQualificationAdapter
                ,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = mQualificationList.get(which);
                        etCandidatesQualification.setText(strName);
                    }
                });
        builderSingle.show();
    }

    private void showNationalityList() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);

        builderSingle.setTitle("Select Nationality");
        View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);
        header.setText("Select Nationality");
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(mNationalityAdapter
                ,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = mNationalityList.get(which);
                        etCandidateNationality.setText(strName);
                    }
                });
        builderSingle.show();
    }

    private void showCasteList() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);

        builderSingle.setTitle("Select Caste");
        View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);
        header.setText("Select Caste");
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(mCasteAdapter
                ,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = mCasteList.get(which);
                        etCandidateCommunityClass.setText(strName);
                    }
                });
        builderSingle.show();
    }

    private void showJobCardList() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);

        builderSingle.setTitle("Select Job Card");
        View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);
        header.setText("Select Job Card");
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(mJobCardAdapter
                ,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = mJobCardList.get(which);
                        jobCard.setText(strName);
                    }
                });
        builderSingle.show();
    }

    private void showEduYearList() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);

        builderSingle.setTitle("Select Year");
        View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);
        header.setText("Select Year");
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(mYearAdapter
                ,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = mYearList.get(which);
                        yearOfEdu.setText(strName);
                    }
                });
        builderSingle.show();
    }

    private void showPassList() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);

        builderSingle.setTitle("Select Qualification Status");
        View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);
        header.setText("Select Qualification Status");
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(mPassAdapter
                ,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = mPassList.get(which);
                        etCandidatesQualifiedPromotion.setText(strName);
                    }
                });
        builderSingle.show();
    }

    private void showPassYearList() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);

        builderSingle.setTitle("Select Year");
        View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);
        header.setText("Select Year");
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(mYearAdapter
                ,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = mYearList.get(which);
                        yearOfPass.setText(strName);
                    }
                });
        builderSingle.show();
    }

    private void selectTradeById(String id) {
        if (!tradeList.isEmpty()) {
            String test = "";
            for (int ss = 0; ss < tradeList.size(); ss++) {
                if (tradeList.get(ss).getTradeId().equalsIgnoreCase(id)) {
                    test = tradeList.get(ss).getTradeName();
                }
            }
            etCandidatesPreferredTrade.setText(test);
        } else {
            etCandidatesPreferredTrade.setText("");
        }
    }

    private void GetBloodGroup() {

        checkInternalState = "bloodGroup";

        if (CommonUtils.isNetworkAvailable(this)) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(MobilizerConstants.KEY_USER_ID, PreferenceStorage.getUserId(getApplicationContext()));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            String url = MobilizerConstants.BUILD_URL + MobilizerConstants.BLOOD_GROUP_LIST;
            serviceHelper.makeGetServiceCall(jsonObject.toString(), url);


        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection");
        }
    }

    private void showBloodGroups() {
        Log.d(TAG, "Show blood group lists");
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);
        header.setText("Select Blood Group");
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(mBloodGroupAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StoreBloodGroup bloodGroup = bloodGroupList.get(which);
                        etCandidateBloodGroup.setText(bloodGroup.getBloodGroupName());
                        bloodGroupId = bloodGroup.getBloodGroupId();
//                        GetTrade();
                    }
                });
        builderSingle.show();
    }

    private void GetTrade() {

        checkInternalState = "trade";

        if (CommonUtils.isNetworkAvailable(this)) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(MobilizerConstants.KEY_USER_ID, PreferenceStorage.getUserId(getApplicationContext()));
                jsonObject.put(MobilizerConstants.KEY_PIA_ID, PreferenceStorage.getPIAId(getApplicationContext()));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            String url = MobilizerConstants.BUILD_URL + MobilizerConstants.TRADES;
            serviceHelper.makeGetServiceCall(jsonObject.toString(), url);


        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection");
        }
    }

    private void showTrades() {
        Log.d(TAG, "Show trade lists");
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);
        header.setText("Select Trade");
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(mTradeAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StoreTrade trade = tradeList.get(which);
                        etCandidatesPreferredTrade.setText(trade.getTradeName());
                        tradeId = trade.getTradeId();
//                        GetTradeTimings();
                    }
                });
        builderSingle.show();
    }

    private void getProspectdata() {

        checkInternalState = "prospect";

        if (CommonUtils.isNetworkAvailable(this)) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(MobilizerConstants.PARAMS_ADMISSION_ID, allProspects.getId());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            String url = MobilizerConstants.BUILD_URL + MobilizerConstants.STUDENT_DETAIL;
            serviceHelper.makeGetServiceCall(jsonObject.toString(), url);


        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, getString(R.string.error_occurred));
        }
    }

    private void GetTradeTimings() {

        checkInternalState = "timings";

        if (CommonUtils.isNetworkAvailable(this)) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(MobilizerConstants.KEY_USER_ID, PreferenceStorage.getUserId(getApplicationContext()));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            String url = MobilizerConstants.BUILD_URL + MobilizerConstants.TIMINGS_LIST;
            serviceHelper.makeGetServiceCall(jsonObject.toString(), url);

        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection");
        }
    }

    private void showTimings() {
        Log.d(TAG, "Show timing lists");
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.gender_header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.gender_header);
        header.setText("Select Timings");
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(mTimingsAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StoreTimings timings = timingsList.get(which);
                        etCandidatesPreferredTiming.setText(timings.getTimingsName());
                        timigsId = timings.getTimingsId();
                    }
                });
        builderSingle.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Now lets connect to the API
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(this.getClass().getSimpleName(), "onPause()");

        //Disconnect from API onPause()
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getLastLocation();
      /*  if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }*/
//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

//        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//
//        if (mLastLocation == null) {
//            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//
//        } else {
//            //If everything went fine lets get latitude and longitude
//            currentLatitude = mLastLocation.getLatitude();
//            currentLongitude = mLastLocation.getLongitude();
//            LatLng pos = new LatLng(currentLatitude, currentLongitude);
//
////                Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
//        }

        //zoom the camera to current location
//        if (mLastLocation != null) {
        /* mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos,10));*/
//            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 14), 1000, null);
//        }
//        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    /**
     * If locationChanges change lat and long
     *
     * @param location
     */

    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();

    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                AddCandidateActivity.this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        AddCandidateActivity.this.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    private void fetchCurrentLocation() {
        Log.d(TAG, "fetch the current location");
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
//            try {
//            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mLastLocation == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

            } else {
                //If everything went fine lets get latitude and longitude
                currentLatitude = mLastLocation.getLatitude();
                currentLongitude = mLastLocation.getLongitude();

//                Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
            }

            // Log.e(TAG, "Current location is" + "Lat" + String.valueOf(mLastLocation.getLatitude()) + "Long" + String.valueOf(mLastLocation.getLongitude()));
            if (mLocationProgress != null) {
                mLocationProgress.cancel();
            }
            /*if (mNearbySelected && (mLastLocation != null)) {
                mTotalReceivedEvents = 0;
                super.callGetEventService(1);
                // getNearbyLIst(2);
            }*/
            if (mLastLocation == null) {
                Log.e(TAG, "Received location is null");
            }

//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append(" ");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("MyCurrentloctionaddress", strReturnedAddress.toString());
            } else {
                Log.w("MyCurrentloctionaddress", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("MyCurrentloctionaddress", "Canont get Address!");
        }
        return strAdd;
    }

    /**
     * delete saved aadhaar card
     *
     * @param uid
     */
    public void deleteCard(String uid) {
        // read data from storage
        String storageData = storage.readFromFile();

        JSONArray storageDataArray;
        //check if file is empty
        if (storageData.length() > 0) {
            try {
                storageDataArray = new JSONArray(storageData);
                // coz I am working on Android version which doesnot support remove method on JSONArray
                JSONArray updatedStorageDataArray = new JSONArray();

                // check if data already exists
                for (int i = 0; i < storageDataArray.length(); i++) {
                    String dataUid = storageDataArray.getJSONObject(i).getString(DataAttributes.AADHAR_UID_ATTR);
                    if (!uid.equals(dataUid)) {
                        updatedStorageDataArray.put(storageDataArray.getJSONObject(i));
                    }
                }

                // save the updated list
                storage.writeToFile(updatedStorageDataArray.toString());

                // Hide the list if all cards are deleted
                if (updatedStorageDataArray.length() < 1) {
                    // hide list and show message
//                    tv_no_saved_card.setVisibility(View.VISIBLE);
//                    lv_saved_card_list.setVisibility(View.GONE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
