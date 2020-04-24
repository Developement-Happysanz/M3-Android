package com.happysanztech.mmm.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.happysanztech.mmm.BuildConfig;
import com.happysanztech.mmm.R;
import com.happysanztech.mmm.helper.AlertDialogHelper;
import com.happysanztech.mmm.helper.ProgressDialogHelper;
import com.happysanztech.mmm.interfaces.DialogClickListener;
import com.happysanztech.mmm.servicehelpers.ServiceHelper;
import com.happysanztech.mmm.serviceinterfaces.IServiceListener;
import com.happysanztech.mmm.utils.AndroidMultiPartEntity;
import com.happysanztech.mmm.utils.AppValidator;
import com.happysanztech.mmm.utils.MobilizerConstants;
import com.happysanztech.mmm.utils.PreferenceStorage;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static android.util.Log.d;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener {

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private static final String TAG = "TradeActivity";
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private EditText mail, address;
    private TextView name, number;
    private Button btnSubmit;
    private ImageView profileImg;
    View rootView;
    String res, userIdSend;

    private Uri outputFileUri;
    static final int REQUEST_IMAGE_GET = 1;
    private String mActualFilePath = null;
    private Uri mSelectedImageUri = null;
    private Bitmap mCurrentUserImageBitmap = null;
    private ProgressDialog mProgressDialog = null;
    private String mUpdatedImageUrl = null;
    long totalSize = 0;
    File image = null;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        getData();
        name = findViewById(R.id.user_name);
        name.setText(PreferenceStorage.getName(this));
        number = findViewById(R.id.phone);
        number.setText(PreferenceStorage.getStaffPhone(this));
        mail = findViewById(R.id.email);
        mail.setText(PreferenceStorage.getStaffEmail(this));
        address = findViewById(R.id.address);
        address.setText(PreferenceStorage.getStaffAddress(this));
        profileImg = findViewById(R.id.profile_img);
        profileImg.setOnClickListener(this);
        String url = PreferenceStorage.getUserPicture(this);
        if (((url != null) && !(url.isEmpty()))) {
            Picasso.get().load(url).placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile).into(profileImg);
        }
        btnSubmit = findViewById(R.id.save_user);
        btnSubmit.setOnClickListener(this);
        findViewById(R.id.back_tic_his).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == btnSubmit) {
            if (validateFields()) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(MobilizerConstants.KEY_USER_ID, PreferenceStorage.getUserId(this));
                    jsonObject.put(MobilizerConstants.KEY_ADDRESS, address.getText().toString());
                    jsonObject.put(MobilizerConstants.PARAMS_EMAIL, mail.getText().toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
                String url = MobilizerConstants.BUILD_URL + MobilizerConstants.UPDATE_USER_PROFILE;
                serviceHelper.makeGetServiceCall(jsonObject.toString(), url);

            }
        }
        if (v == profileImg) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                } else {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                    openImageIntent();
                }
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String permissions[],
            int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImageIntent();
//                    Toast.makeText(ProfileActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }


    private void openImageIntent() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Remove Photo", "Cancel"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Change Profile Picture");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
//                    openCamera();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    Uri f = FileProvider.getUriForFile(getApplicationContext(),
                            BuildConfig.APPLICATION_ID + ".provider",
                            createImageFile());
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, f);
                    startActivityForResult(intent, 1);
                } else if (options[item].equals("Choose from Gallery")) {
                    openImagesDocument();
                } else if (options[item].equals("Remove Photo")) {
                    PreferenceStorage.saveUserPicture(getApplicationContext(), "");
                    profileImg.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_profile));
                    mSelectedImageUri = Uri.parse("android.resource://com.palprotech.heylaapp/drawable/ic_default_profile");
                    mActualFilePath = mSelectedImageUri.getPath();
                    saveUserImage();
                } else if (options[item].equals("Cancel")) {

                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void openImagesDocument() {
        Intent pictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pictureIntent.setType("image/*");
        pictureIntent.addCategory(Intent.CATEGORY_OPENABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String[] mimeTypes = new String[]{"image/jpeg", "image/png"};
            pictureIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        }
        startActivityForResult(Intent.createChooser(pictureIntent, "Select Picture"), 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
//                Uri uri = Uri.parse(mActualFilePath);
//                openCropActivity(uri, uri);
                final File file = new File(mActualFilePath);
                try {
                    InputStream ims = new FileInputStream(file);
                    profileImg.setImageBitmap(BitmapFactory.decodeStream(ims));
                } catch (FileNotFoundException e) {
                    return;
                }

                // ScanFile so it will be appeared on Gallery
                MediaScannerConnection.scanFile(this,
                        new String[]{mActualFilePath}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
//                                performCrop(uri);
                                Uri destinationUri = Uri.fromFile(file);  // 3
                                openCropActivity(uri, destinationUri);
                            }
                        });
            } else if (requestCode == 2) {
                Uri sourceUri = data.getData(); // 1
                File file = null; // 2
                try {
                    file = getImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Uri destinationUri = Uri.fromFile(file);  // 3
                openCropActivity(sourceUri, destinationUri);  // 4
            } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
                if (data != null) {
                    Uri uri = UCrop.getOutput(data);
                    profileImg.setImageURI(uri);
//                    mActualFilePath = uri.getPath();
                    saveUserImage();
                }
            }
        }
    }

    private File getImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        mActualFilePath = image.getAbsolutePath();
        return image;
    }

    private File createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PNG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".png",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Save a file: path for use with ACTION_VIEW intents
        mActualFilePath = image.getAbsolutePath();
        return image;
    }
    private void openCropActivity(Uri sourceUri, Uri destinationUri) {
        UCrop.Options options = new UCrop.Options();
        options.setCircleDimmedLayer(true);
        options.setCropFrameColor(ContextCompat.getColor(this, R.color.colorPrimary));
        UCrop.of(sourceUri, destinationUri)
                .withMaxResultSize(100, 100)
                .withAspectRatio(5f, 5f)
                .start(this);
    }
//    private void openCropActivity(Uri sourceUri, Uri destinationUri) {
//        UCrop.Options options = new UCrop.Options();
//        options.setCircleDimmedLayer(true);
//        options.setCropFrameColor(ContextCompat.getColor(this, R.color.colorPrimary));
//        UCrop.of(sourceUri, destinationUri)
//                .withMaxResultSize(100, 100)
//                .withAspectRatio(5f, 5f)
//                .start(this);
//    }

    private void saveUserImage() {

        mUpdatedImageUrl = null;

        new UploadFileToServer().execute();
    }


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
            httppost = new HttpPost(String.format(MobilizerConstants.BUILD_URL + MobilizerConstants.UPLOAD_USER_PIC + PreferenceStorage.getUserId(getApplicationContext()) + "/"));

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
                    entity.addPart("user_pic", new FileBody(sourceFile));

                    // Extra parameters if you want to pass to server
//                    entity.addPart("user_id", new StringBody(PreferenceStorage.getUserId(this)));
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

                            mUpdatedImageUrl = resp.getString("user_picture");
                            String newmUpdatedImageUrl = MobilizerConstants.BUILD_URL+""+mUpdatedImageUrl;
                            PreferenceStorage.saveUserPicture(getApplicationContext(), newmUpdatedImageUrl);

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
                Toast.makeText(getApplicationContext(), "Unable to save profile picture", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "User profile image successfully...", Toast.LENGTH_SHORT).show();
//                if (mUpdatedImageUrl != null) {
//                    PreferenceStorage.saveUserPicture(getApplicationContext(), mUpdatedImageUrl);
//                }
            }

            if (mProgressDialog != null) {
                mProgressDialog.cancel();
            }

//            finish();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }


    private void getData() {
        res = "get";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(MobilizerConstants.KEY_USER_ID, PreferenceStorage.getUserId(this));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = MobilizerConstants.BUILD_URL + MobilizerConstants.GET_USER_PROFILE;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private boolean validateFields() {

        if (!AppValidator.checkNullString(this.address.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter address");
            return false;
        } else if (!AppValidator.checkNullString(this.mail.getText().toString().trim())) {
            AlertDialogHelper.showSimpleAlertDialog(this, "Enter email");
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
            try {
                if (res.equalsIgnoreCase("get")) {
                    JSONObject dataa = response.getJSONObject("userprofile");
                    userIdSend = dataa.getString("id");
                    saveUserData(dataa);
                } else {
                    Toast.makeText(this, "Changes to your profile are saved.", Toast.LENGTH_SHORT).show();
//                    Fragment fragment = new DashboardActivity();
//                    FragmentManager fragmentManager = this.getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    fragmentTransaction.replace(R.id.flContent, fragment);
//                    fragmentTransaction.addToBackStack(null);
//                    fragmentTransaction.commit();
//                    Intent navigationIntent = new Intent(this, DashboardActivity.class);
//                    navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(navigationIntent);
                    finish();
                }
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
        String cutrrentaddress = "";
        String phoneNo = "";
        String emailID = "";

        try {

            if (userData != null) {

                // User Preference - User Id
//                userId = userData.getString("id");
//                if ((userId != null) && !(userId.isEmpty()) && !userId.equalsIgnoreCase("null")) {
//                    PreferenceStorage.saveUserId(this, userId);
//                }

                // User Preference - User Full Name
                fullName = userData.getString("name");
                if ((fullName != null) && !(fullName.isEmpty()) && !fullName.equalsIgnoreCase("null")) {
                    PreferenceStorage.saveName(this, fullName);
                }

//                // User Preference - User Name
//                userName = userData.getString("user_name");
//                if ((userName != null) && !(userName.isEmpty()) && !userName.equalsIgnoreCase("null")) {
//                    PreferenceStorage.saveUserName(this, userName);
//                }

                // User Preference - User Picture
                phoneNo = userData.getString("phone");
                if ((phoneNo != null) && !(phoneNo.isEmpty()) && !phoneNo.equalsIgnoreCase("null")) {
                    PreferenceStorage.saveStaffPhone(this, phoneNo);
                }

                // User Preference - User Picture
                userPicture = userData.getString("profile_pic");
                if ((userPicture != null) && !(userPicture.isEmpty()) && !userPicture.equalsIgnoreCase("null")) {
                    PreferenceStorage.saveUserPicture(this, userPicture);
                }

                // User Preference - User Picture
                cutrrentaddress = userData.getString("address");
                if ((cutrrentaddress != null) && !(cutrrentaddress.isEmpty()) && !cutrrentaddress.equalsIgnoreCase("null")) {
                    PreferenceStorage.saveStaffAddress(this, cutrrentaddress);
                }

                // User Preference - User Picture
                emailID = userData.getString("email");
                if ((emailID != null) && !(emailID.isEmpty()) && !emailID.equalsIgnoreCase("null")) {
                    PreferenceStorage.saveStaffEmail(this, emailID);
                }

                // User Preference - User Type Name
//                userTypeName = userData.getString("user_type_name");
//                if ((userTypeName != null) && !(userTypeName.isEmpty()) && !userTypeName.equalsIgnoreCase("null")) {
//                    PreferenceStorage.saveUserTypeName(this, userTypeName);
//                }

                // User Preference - User Type
//                userType = userData.getString("user_type");
//                if ((userType != null) && !(userType.isEmpty()) && !userType.equalsIgnoreCase("null")) {
//                    PreferenceStorage.saveUserType(this, userType);
//                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
