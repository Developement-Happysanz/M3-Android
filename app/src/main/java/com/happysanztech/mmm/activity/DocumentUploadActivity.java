package com.happysanztech.mmm.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.happysanztech.mmm.R;
import com.happysanztech.mmm.bean.database.SQLiteHelper;
import com.happysanztech.mmm.helper.AlertDialogHelper;
import com.happysanztech.mmm.helper.ProgressDialogHelper;
import com.happysanztech.mmm.interfaces.DialogClickListener;
import com.happysanztech.mmm.servicehelpers.ServiceHelper;
import com.happysanztech.mmm.serviceinterfaces.IServiceListener;
import com.happysanztech.mmm.utils.CommonUtils;
import com.happysanztech.mmm.utils.MobilizerConstants;
import com.happysanztech.mmm.utils.PreferenceStorage;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.util.Log.d;

public class DocumentUploadActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener {

    private static final String TAG = DocumentUploadActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private ImageView ivBack;
    private LinearLayout cast, disab;
    private TextView aadhaar, transferCertificate, communityCertificate, disability, rationCard, voterId, jobCard, passBook;
    SQLiteHelper database;

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
    String selectedFilePath = "";
    ProgressDialog dialog;

    private String prospectID = "";
    private String storeDocumentNumber = "";
    private String storeDocumentMasterId = "";

    private Button done;

    private boolean docOne = false;
    private boolean docTwo = false;
    private boolean docThree = false;
    private boolean docFour = false;
    private boolean docFive = false;
    private boolean docSix = false;
    private boolean docSeven = false;
    private boolean docEight = false;
    private boolean uploadCheck = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_upload);
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        ivBack = findViewById(R.id.back_tic_his);
        ivBack.setOnClickListener(this);

        prospectID = PreferenceStorage.getAdmissionId(this);
        cast = findViewById(R.id.commi);
        aadhaar = findViewById(R.id.txtUploadAadhaarCard);
        transferCertificate = findViewById(R.id.txtUploadTcMs);
        communityCertificate = findViewById(R.id.txtUploadCC);
        disability = findViewById(R.id.txtUploadDisabilityCard);
        rationCard = findViewById(R.id.txtUploadRationCard);
        voterId = findViewById(R.id.txtUploadVoterID);
        jobCard = findViewById(R.id.txtUploadJobCard);
        passBook = findViewById(R.id.txtUploadPassbook);
        done = findViewById(R.id.btn_done);
        checkDisplay();

        aadhaar.setOnClickListener(this);
        transferCertificate.setOnClickListener(this);
        communityCertificate.setOnClickListener(this);
        disability.setOnClickListener(this);
        rationCard.setOnClickListener(this);
        voterId.setOnClickListener(this);
        jobCard.setOnClickListener(this);
        passBook.setOnClickListener(this);
        done.setOnClickListener(this);
        database = new SQLiteHelper(this);

//        docFive = true;
//        docEight = true;

        getDocStatus();

    }

    private void checkDisplay() {
        String caste = PreferenceStorage.getCaste(this);
        if (caste.equalsIgnoreCase("SC") || caste.equalsIgnoreCase("ST")) {
            cast.setVisibility(View.VISIBLE);
        } else {
            cast.setVisibility(View.GONE);
            docThree = true;
        }
        disab = findViewById(R.id.disab);
        String dis = PreferenceStorage.getDisability(this);
        if (dis.equalsIgnoreCase("1")) {
            disab.setVisibility(View.VISIBLE);
        } else {
            disab.setVisibility(View.GONE);
            docSeven = true;
        }
    }

    private void showAlerrry() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Go back");
        alertDialogBuilder.setMessage("If you choose to go back you will lose the prospect data. Are you sure");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
//                database.dele
                PreferenceStorage.saveAdmissionId(getApplicationContext(), "");
                finish();
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

    @Override
    public void onClick(View v) {
        if (v == aadhaar) {
            if (docOne) {
                uploadCheck = true;
            } else {
                uploadCheck = false;
            }
            storeDocumentMasterId = "1";
            openImageIntent();
        } else if (v == transferCertificate) {
            if (docTwo) {
                uploadCheck = true;
            } else {
                uploadCheck = false;
            }
            storeDocumentMasterId = "2";
            openImageIntent();
        } else if (v == communityCertificate) {
            if (docThree) {
                uploadCheck = true;
            } else {
                uploadCheck = false;
            }
            storeDocumentMasterId = "3";
            openImageIntent();
        } else if (v == disability) {
            if (docSeven) {
                uploadCheck = true;
            } else {
                uploadCheck = false;
            }
            storeDocumentMasterId = "7";
            openImageIntent();
        } else if (v == rationCard) {
            if (docFour) {
                uploadCheck = true;
            } else {
                uploadCheck = false;
            }
            storeDocumentMasterId = "4";
            openImageIntent();
        } else if (v == voterId) {
            if (docFive) {
                uploadCheck = true;
            } else {
                uploadCheck = false;
            }
            storeDocumentMasterId = "5";
            openImageIntent();
        } else if (v == jobCard) {
            if (docSix) {
                uploadCheck = true;
            } else {
                uploadCheck = false;
            }
            storeDocumentMasterId = "6";
            openImageIntent();
        } else if (v == passBook) {
            if (docEight) {
                uploadCheck = true;
            } else {
                uploadCheck = false;
            }
            storeDocumentMasterId = "8";
            openImageIntent();
        } else if (v == done) {
            if (docOne && docTwo && docThree && docFour && docSix && docSeven) {
                PreferenceStorage.saveAdmissionId(this, "");
                finish();
            } else {
                AlertDialogHelper.showSimpleAlertDialog(this, "Upload all necessary documents");
            }
        } else if (v == ivBack) {
            if (!CommonUtils.isNetworkAvailableNew(this)) {
                showAlerrry();
            } else {
                finish();
            }
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
//            setResult(RESULT_OK);
//            Toast.makeText(this, "New task created.", Toast.LENGTH_SHORT).show();
////            finish();
//            finish();
            try {
                JSONArray resdata = response.getJSONArray("doc_status");
                String docStatus = "";
                String docTypeID = "";
                for (int i = 0; i < resdata.length(); i++) {
                    docTypeID = resdata.getJSONObject(i).getString("id");
                    docStatus = resdata.getJSONObject(i).getString("selected");
                    if (!docStatus.equalsIgnoreCase("0")) {
                        setTheVal(docTypeID);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            checkDisplay();

        }
    }

    private void setTheVal(String docId) {
        switch (docId) {

            case "1":
                docOne = true;
//                        aadhaar.setEnabled(false);
                aadhaar.setFocusable(false);
//                        aadhaar.setClickable(false);
                aadhaar.setText("Uploaded");
                aadhaar.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_uploded, 0, 0);
                break;
            case "2":
                docTwo = true;
//                        transferCertificate.setEnabled(false);
                transferCertificate.setFocusable(false);
//                        transferCertificate.setClickable(false);
                transferCertificate.setText("Uploaded");
                transferCertificate.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_uploded, 0, 0);
                break;
            case "3":
                docThree = true;
//                        communityCertificate.setEnabled(false);
                communityCertificate.setFocusable(false);
//                        communityCertificate.setClickable(false);
                communityCertificate.setText("Uploaded");
                communityCertificate.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_uploded, 0, 0);
                break;
            case "4":
                docFour = true;
//                        rationCard.setEnabled(false);
                rationCard.setFocusable(false);
//                        rationCard.setClickable(false);
                rationCard.setText("Uploaded");
                rationCard.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_uploded, 0, 0);
                break;
            case "5":
                docFive = true;
//                        voterId.setEnabled(false);
                voterId.setFocusable(false);
//                        voterId.setClickable(false);
                voterId.setText("Uploaded");
                voterId.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_uploded, 0, 0);
                break;
            case "6":
                docSix = true;
//                        jobCard.setEnabled(false);
                jobCard.setFocusable(false);
//                        jobCard.setClickable(false);
                jobCard.setText("Uploaded");
                jobCard.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_uploded, 0, 0);
                break;
            case "7":
                docSeven = true;
//                        disability.setEnabled(false);
                disability.setFocusable(false);
//                        disability.setClickable(false);
                disability.setText("Uploaded");
                disability.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_uploded, 0, 0);
                break;
            case "8":
                docEight = true;
//                        passBook.setEnabled(false);
                passBook.setFocusable(false);
//                        passBook.setClickable(false);
                passBook.setText("Uploaded");
                passBook.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_uploded, 0, 0);
                break;
        }
    }


    @Override
    public void onError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(this, error);
    }

    private void getDocStatus() {

        if (!CommonUtils.isNetworkAvailableNew(this)) {
//            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection available");

        } else {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(MobilizerConstants.KEY_PROSPECT_ID, PreferenceStorage.getAdmissionId(this));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            String url = MobilizerConstants.BUILD_URL + MobilizerConstants.DOC_STATUS;
            serviceHelper.makeGetServiceCall(jsonObject.toString(), url);

        }
    }


    private void openImageIntent() {

        // Determine Uri of camera image to save.
//        final File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyDir");
//        File pictureFolder = Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES
//        );
        File pictureFolder = getExternalFilesDir(Environment.DIRECTORY_DCIM);
        final File root = new File(pictureFolder, "M3Images");

        if (!root.exists()) {
            if (!root.mkdirs()) {
                Log.d(TAG, "Failed to create directory for storing images");
                return;
            }
        }

//        final String fname = PreferenceStorage.getUserId(this) + ".png";
//        final File sdImageMainDirectory = new File(root.getPath() + File.separator + fname);
//        outputFileUri = Uri.fromFile(sdImageMainDirectory);
        Calendar newCalendar = Calendar.getInstance();
        int month = newCalendar.get(Calendar.MONTH) + 1;
        int day = newCalendar.get(Calendar.DAY_OF_MONTH);
        int year = newCalendar.get(Calendar.YEAR);
        int hours = newCalendar.get(Calendar.HOUR_OF_DAY);
        int minutes = newCalendar.get(Calendar.MINUTE);
        int seconds = newCalendar.get(Calendar.SECOND);
        final String fname = PreferenceStorage.getUserId(this) + "_" + day + "_" + month + "_" + year + "_" + hours + "_" + minutes + "_" + seconds + ".png";
        final File sdImageMainDirectory = new File(root.getPath() + File.separator + fname);
//        destFile = sdImageMainDirectory;
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
//                    setPic(selectedImageUri);
                }

                if (!CommonUtils.isNetworkAvailableNew(this)) {
                    String studeID = PreferenceStorage.getDbID(this);
                    long x = database.store_doc_data_insert(storeDocumentMasterId, studeID, mActualFilePath);
                    System.out.println("Stored Id : " + x);

                    setTheVal(storeDocumentMasterId);


//                    PreferenceStorage.saveAdmissionId(getApplicationContext(), "");
//                    Toast.makeText(this, "Prospect details stored. Sync later when network is available", Toast.LENGTH_SHORT).show();
//                    finish();
                } else {
                    try {
                        Document document = new Document();

//                    String directoryPath = android.os.Environment.getExternalStorageDirectory().toString();
//                        File pictureFolder = Environment.getExternalStoragePublicDirectory(
//                                Environment.DIRECTORY_DOCUMENTS
//                        );
                        File pictureFolder = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);

                        final File root = new File(pictureFolder, "M3Documents");

                        if (!root.exists()) {
                            if (!root.mkdirs()) {
                                Log.d(TAG, "Failed to create directory for storing docs");
                                return;
                            }
                        }
                        Calendar newCalendar = Calendar.getInstance();
                        int month = newCalendar.get(Calendar.MONTH) + 1;
                        int day = newCalendar.get(Calendar.DAY_OF_MONTH);
                        int year = newCalendar.get(Calendar.YEAR);
                        int hours = newCalendar.get(Calendar.HOUR_OF_DAY);
                        int minutes = newCalendar.get(Calendar.MINUTE);
                        int seconds = newCalendar.get(Calendar.SECOND);
                        final String fname = PreferenceStorage.getUserId(this) + "_" + day + "_" + month + "_" + year + "_" + hours + "_" + minutes + "_" + seconds + ".pdf";

                        PdfWriter.getInstance(document, new FileOutputStream(root.getPath() + File.separator + fname)); //  Change pdf's name.

                        document.open();

                        Image image = Image.getInstance(mActualFilePath);  // Change image's name and extension.

                        float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                                - document.rightMargin() - 0) / image.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.
                        image.scalePercent(scaler);
                        image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);

                        document.add(image);
                        document.close();

                        selectedFilePath = root.getPath() + File.separator + fname;
                        Log.i(TAG, "Selected File Path:" + selectedFilePath);
                        File sizeCge;
//                selectedFilePath = mActualFilePath;
                        if (selectedFilePath != null && !selectedFilePath.equals("")) {
                            sizeCge = new File(selectedFilePath);
                            if (sizeCge.length() >= 12000000) {
                                AlertDialogHelper.showSimpleAlertDialog(this, "File size too large. File should be at least 12MB");
                                selectedFilePath = null;
                            } else {
                                Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show();
                                dialog = ProgressDialog.show(DocumentUploadActivity.this, "", "Uploading File...", true);

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //creating new thread to handle Http Operations
//                        uploadFile(selectedFilePath);
                                        new PostDataAsyncTask().execute();
                                    }
                                }).start();
                            }
                        } else {
                            Toast.makeText(this, "Cannot upload file to server", Toast.LENGTH_SHORT).show();
                        }
                    } catch (FileNotFoundException | DocumentException e) {
                        e.printStackTrace();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private class PostDataAsyncTask extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        //android upload file to server
        private String uploadFile() {

            int serverResponseCode = 0;
            String serverResponseMessage = null;
            HttpURLConnection connection;
            DataOutputStream dataOutputStream;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";

            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 3 * 1024 * 1024;
            File selectedFile = new File(selectedFilePath);
            double len = selectedFile.length();

            String[] parts = selectedFilePath.split("/");
            final String fileName = parts[parts.length - 1];

            if (!selectedFile.isFile()) {
                dialog.dismiss();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        tvFileName.setText("Source File Doesn't Exist: " + selectedFilePath);
                    }
                });
                return "";
            } else {
                try {
                    String id = PreferenceStorage.getUserId(getApplicationContext());
//                    String id = "118";
                    String document_master_id = storeDocumentMasterId;
                    String document_proof_number = "0";

                    FileInputStream fileInputStream = new FileInputStream(selectedFile);
                    String SERVER_URL;
                    if (uploadCheck) {
                        SERVER_URL = MobilizerConstants.BUILD_URL + MobilizerConstants.DOC_UPLOAD_UPDATE + "" + id + "/" + document_master_id + "/" + prospectID + "/" + document_proof_number + "/";
                    } else {
                        SERVER_URL = MobilizerConstants.BUILD_URL + MobilizerConstants.DOC_UPLOAD + "" + id + "/" + document_master_id + "/" + prospectID + "/" + document_proof_number + "/";
                    }
                    URI uri = new URI(SERVER_URL.replace(" ", "%20"));
                    String baseURL = uri.toString();
                    URL url = new URL(baseURL);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);//Allow Inputs
                    connection.setDoOutput(true);//Allow Outputs
                    connection.setUseCaches(false);//Don't use a cached Copy
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                    connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                    connection.setRequestProperty("upload_document", selectedFilePath);
//                    connection.setRequestProperty("user_id", id);
//                    connection.setRequestProperty("doc_name", title);
//                    connection.setRequestProperty("doc_month_year", start);

                    //creating new dataoutputstream
                    dataOutputStream = new DataOutputStream(connection.getOutputStream());

                    //writing bytes to data outputstream
                    dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"upload_document\";filename=\""
                            + fileName + "\"" + lineEnd);
//                    outputStream.writeBytes("Content-Disposition: form-data; name=\"" + filefield + "\"; filename=\"" + q[idx] + "\"" + lineEnd);
                    dataOutputStream.writeBytes(lineEnd);

                    //returns no. of bytes present in fileInputStream
                    bytesAvailable = fileInputStream.available();
                    //selecting the buffer size as minimum of available bytes or 1 MB
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    //setting the buffer as byte array of size of bufferSize
                    buffer = new byte[bufferSize];

                    //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                    while (bytesRead > 0) {
                        //write the bytes read from inputstream
                        dataOutputStream.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }

                    dataOutputStream.writeBytes(lineEnd);
                    dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    serverResponseCode = connection.getResponseCode();
                    serverResponseMessage = connection.getResponseMessage();

                    Log.i(TAG, "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);

                    //response code of 200 indicates the server status OK
                    if (serverResponseCode == 200) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                            tvFileName.setText("File Upload completed.\n\n You can see the uploaded file here: \n\n" + "http://coderefer.com/extras/uploads/"+ fileName);
//                                tvFileName.setText("File Upload completed.\n\n"+ fileName);
                            }
                        });
                    }

                    //closing the input and output streams
                    fileInputStream.close();
                    dataOutputStream.flush();
                    dataOutputStream.close();


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "File Not Found", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "URL error!", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Cannot Read/Write File!", Toast.LENGTH_SHORT).show();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
                return serverResponseMessage;
            }

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);
            progressDialogHelper.hideProgressDialog();

            super.onPostExecute(result);
            if ((result.contains("OK"))) {
                Toast.makeText(getApplicationContext(), "Uploaded successfully!", Toast.LENGTH_SHORT).show();
                setTheVal(storeDocumentMasterId);

            } else {
                Toast.makeText(getApplicationContext(), "Unable to upload file", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
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

}
