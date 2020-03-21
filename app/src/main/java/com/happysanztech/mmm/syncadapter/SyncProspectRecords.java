package com.happysanztech.mmm.syncadapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.happysanztech.mmm.R;
import com.happysanztech.mmm.activity.AddCandidateActivity;
import com.happysanztech.mmm.activity.MainActivity;
import com.happysanztech.mmm.bean.database.SQLiteHelper;
import com.happysanztech.mmm.helper.AlertDialogHelper;
import com.happysanztech.mmm.helper.ProgressDialogHelper;
import com.happysanztech.mmm.servicehelpers.ServiceHelper;
import com.happysanztech.mmm.serviceinterfaces.IServiceListener;
import com.happysanztech.mmm.utils.AndroidMultiPartEntity;
import com.happysanztech.mmm.utils.MobilizerConstants;
import com.happysanztech.mmm.utils.PreferenceStorage;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
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
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.util.Log.d;

public class SyncProspectRecords implements IServiceListener {

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private Context context;
    private SQLiteHelper database;
    private String _id = "";
    private String mActualFilePath = "";
    private String admissionId = "";
    long totalSize = 0;
    private String checkVal = "";
    private ProgressDialog dialog;
    private String selectedFilePath = "";
    private String storeDocumentMasterId = "";


    public SyncProspectRecords(Context context) {
        this.context = context;
        serviceHelper = new ServiceHelper(context);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(context);
    }

    public void SyncToServer() {
        callService();
    }

    private void callService() {
        checkVal = "addProsp";
        database = new SQLiteHelper(context);

        String CandidatesAadhaarStatus = "";
        String CandidatesAadhaarNo = "";
        String CandidateName = "";
        String CandidateSex = "";
        String CandidateDOB = "";
        String CandidateAge = "";
        String CandidateNationality = "";
        String CandidateReligion = "";
        String CandidateCommunityClass = "";
        String CandidateCommunity = "";
        String CandidateBloodGroup = "";
        String CandidateFatherName = "";
        String CandidateMotherName = "";
        String CandidateMobileNo = "";
        String CandidateAlterMobileNo = "";
        String CandidateEmailId = "";
        String CandidateState = "";
        String CandidateCity = "";
        String CandidateAddressLine1 = "";
        String CandidatesLastInstitute = "";
        String CandidateMotherTongue = "";
        String AnyDisability = "";
        String currentDateandTime = "";
        String locationAddressResult = "";
        String currentLatitude = "";
        String currentLongitude = "";
        String tradeId = "";
        String CandidatesQualification = "";
        String CandidatesQualifiedPromotion = "";
        String userId = "";
        String piaId = "";
        String CandidatesqualificationDetails = "";
        String CandidatesyearOfEdu = "";
        String CandidatesyearOfPass = "";
        String CandidatesiMarkOne = "";
        String CandidatesiMarkTwo = "";
        String CandidateslangKnown = "";
        String CandidatesmotherMob = "";
        String CandidatesfatherMob = "";
        String CandidatesheadFamilyName = "";
        String CandidateseduHeadOfFamily = "";
        String CandidatesfamilyMembers = "";
        String CandidatesyearlyIncome = "";
        String CandidatesJobCard = "";
        Cursor c = database.getStoredProspectData();
        if (c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    _id = c.getString(0);
                    CandidatesAadhaarStatus = c.getString(1);
                    CandidatesAadhaarNo = c.getString(2);
                    CandidateName = c.getString(3);
                    CandidateSex = c.getString(4);
                    CandidateDOB = c.getString(5);
                    CandidateAge = c.getString(6);
                    CandidateNationality = c.getString(7);
                    CandidateReligion = c.getString(8);
                    CandidateCommunityClass = c.getString(9);
                    CandidateCommunity = c.getString(10);
                    CandidateFatherName = c.getString(11);
                    CandidateMotherName = c.getString(12);
                    CandidateMobileNo = c.getString(13);
                    CandidateAlterMobileNo = c.getString(14);
                    CandidateEmailId = c.getString(15);
                    CandidateState = c.getString(16);
                    CandidateCity = c.getString(17);
                    CandidateAddressLine1 = c.getString(18);
                    CandidateMotherTongue = c.getString(19);
                    AnyDisability = c.getString(20);
                    CandidateBloodGroup = c.getString(21);
                    currentDateandTime = c.getString(22);
                    currentLatitude = c.getString(24);
                    currentLongitude = c.getString(25);
                    if (!currentLatitude.isEmpty() && !currentLongitude.isEmpty()) {
                        locationAddressResult = getCompleteAddressString(Double.valueOf(currentLatitude), Double.valueOf(currentLongitude));
                    }
                    tradeId = c.getString(26);
                    CandidatesLastInstitute = c.getString(27);
                    CandidatesQualification = c.getString(28);
                    CandidatesQualifiedPromotion = c.getString(29);
                    userId = c.getString(30);
                    piaId = c.getString(32);
                    mActualFilePath = c.getString(33);
                    CandidatesqualificationDetails = c.getString(34);
                    CandidatesyearOfEdu = c.getString(35);
                    CandidatesyearOfPass = c.getString(36);
                    CandidatesiMarkOne = c.getString(37);
                    CandidatesiMarkTwo = c.getString(38);
                    CandidateslangKnown = c.getString(39);
                    CandidatesmotherMob = c.getString(40);
                    CandidatesfatherMob = c.getString(41);
                    CandidatesheadFamilyName = c.getString(42);
                    CandidateseduHeadOfFamily = c.getString(43);
                    CandidatesfamilyMembers = c.getString(44);
                    CandidatesyearlyIncome = c.getString(45);
                    CandidatesJobCard = c.getString(46);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(MobilizerConstants.PARAMS_HAVE_AADHAAR_CARD, CandidatesAadhaarStatus);
                        jsonObject.put(MobilizerConstants.PARAMS_AADHAAR_CARD_NUMBER, CandidatesAadhaarNo);
                        jsonObject.put(MobilizerConstants.PARAMS_NAME, CandidateName);
                        jsonObject.put(MobilizerConstants.PARAMS_SEX, CandidateSex);
                        jsonObject.put(MobilizerConstants.PARAMS_DOB, CandidateDOB);
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
                        jsonObject.put(MobilizerConstants.PARAMS_ADMISSION_LATITUDE, currentLatitude);
                        jsonObject.put(MobilizerConstants.PARAMS_ADMISSION_LONGITUDE, currentLongitude);
                        jsonObject.put(MobilizerConstants.PARAMS_PREFERRED_TRADE, tradeId);
                        jsonObject.put(MobilizerConstants.PARAMS_PREFERRED_TIMING, "");
                        jsonObject.put(MobilizerConstants.PARAMS_LAST_INSTITUTE, CandidatesLastInstitute);
                        jsonObject.put(MobilizerConstants.PARAMS_LAST_STUDIED, CandidatesQualification);
                        jsonObject.put(MobilizerConstants.PARAMS_QUALIFIED_PROMOTION, CandidatesQualifiedPromotion);
//            jsonObject.put(MobilizerConstants.PARAMS_TRANSFER_CERTIFICATE, CandidatesTC);
                        jsonObject.put(MobilizerConstants.PARAMS_STATUS, "Pending");
                        jsonObject.put(MobilizerConstants.PARAMS_CREATED_BY, userId);
                        jsonObject.put(MobilizerConstants.PARAMS_CREATED_AT, currentDateandTime);
                        jsonObject.put(MobilizerConstants.PARAMS_PIA_ID, piaId);
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

                    String url = MobilizerConstants.BUILD_URL + MobilizerConstants.ADD_CANDIDATE;
                    serviceHelper.makeGetServiceCall(jsonObject.toString(), url);

                } while (c.moveToNext());
            }
        }
    }

    private boolean validateSignInResponse(JSONObject response) {
        boolean signInSuccess = false;
        if ((response != null)) {
            try {
                String status = response.getString("status");
                String msg = response.getString(MobilizerConstants.PARAM_MESSAGE);
                d("HI", "status val" + status + "msg" + msg);

                if ((status != null)) {
                    if (((status.equalsIgnoreCase("activationError")) || (status.equalsIgnoreCase("alreadyRegistered")) ||
                            (status.equalsIgnoreCase("notRegistered")) || (status.equalsIgnoreCase("error")))) {
                        signInSuccess = false;
                        d("HI", "Show error dialog");
                        AlertDialogHelper.showSimpleAlertDialog(context, msg);

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

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
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

    @Override
    public void onResponse(JSONObject response) {
        if (validateSignInResponse(response)) {
            if (checkVal.equalsIgnoreCase("addProsp")) {
                if ((mActualFilePath != null) && (!mActualFilePath.isEmpty())) {
                    try {
                        if (!response.getString("admission_id").isEmpty()) {
                            admissionId = response.getString("admission_id");
                            new UploadFileToServer().execute();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                database.updateProspectSyncStatus(_id);
                senddoc();
            } else if (checkVal.equalsIgnoreCase("adddoc")) {
                Cursor c = database.getStoredProspectData();
                if (c.getCount() > 0) {
                    SyncToServer();
                } else {
                    Toast.makeText(context, "All records synced", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(context, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
            }
        }
    }

    private void senddoc() {
        Cursor c = database.getStoredDocData(_id);
        if (c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    storeDocumentMasterId = c.getString(1);
                    mActualFilePath = c.getString(3);
                }while (c.moveToNext());
            }
        }
        try {
            Document document = new Document();

//                    String directoryPath = android.os.Environment.getExternalStorageDirectory().toString();
            File pictureFolder = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS
            );
            final File root = new File(pictureFolder, "M3Documents");

            if (!root.exists()) {
                if (!root.mkdirs()) {
//                            Log.d(TAG, "Failed to create directory for storing docs");
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
            final String fname = PreferenceStorage.getUserId(context) + "_" + day + "_" + month + "_" + year + "_" + hours + "_" + minutes + "_" + seconds + ".pdf";

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
//                    Log.i(TAG, "Selected File Path:" + selectedFilePath);
            File sizeCge;
//                selectedFilePath = mActualFilePath;
            if (selectedFilePath != null && !selectedFilePath.equals("")) {
                sizeCge = new File(selectedFilePath);
                if (sizeCge.length() >= 12000000) {
                    AlertDialogHelper.showSimpleAlertDialog(context, "File size too large. File should be at least 12MB");
                    selectedFilePath = null;
                } else {
                    Toast.makeText(context, "Uploading...", Toast.LENGTH_SHORT).show();
                    dialog = ProgressDialog.show(context, "", "Uploading File...", true);

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
                Toast.makeText(context, "Cannot upload file to server", Toast.LENGTH_SHORT).show();
            }
        } catch (FileNotFoundException | DocumentException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private class PostDataAsyncTask extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialogHelper.showProgressDialog("Uploading");
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

//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
////                        tvFileName.setText("Source File Doesn't Exist: " + selectedFilePath);
//                    }
//                });
                return "";
            } else {
                try {
                    String id = PreferenceStorage.getUserId(context);
//                    String id = "118";
                    String document_master_id = storeDocumentMasterId;
                    String document_proof_number = "0";

                    FileInputStream fileInputStream = new FileInputStream(selectedFile);
                    String SERVER_URL = MobilizerConstants.BUILD_URL + MobilizerConstants.DOC_UPLOAD + "" + id + "/" + document_master_id + "/" + admissionId + "/" + document_proof_number + "/";
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

//                    Log.i(TAG, "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);

                    //response code of 200 indicates the server status OK
                    if (serverResponseCode == 200) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
////                            tvFileName.setText("File Upload completed.\n\n You can see the uploaded file here: \n\n" + "http://coderefer.com/extras/uploads/"+ fileName);
////                                tvFileName.setText("File Upload completed.\n\n"+ fileName);
//                            }
//                        });
                    }

                    //closing the input and output streams
                    fileInputStream.close();
                    dataOutputStream.flush();
                    dataOutputStream.close();


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(getApplicationContext(), "File Not Found", Toast.LENGTH_SHORT).show();
//                        }
//                    });
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "URL error!", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Cannot Read/Write File!", Toast.LENGTH_SHORT).show();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
                return serverResponseMessage;
            }

        }

        @Override
        protected void onPostExecute(String result) {
//            Log.e(TAG, "Response from server: " + result);
            progressDialogHelper.hideProgressDialog();

            super.onPostExecute(result);
            if ((result.contains("OK"))) {
                Toast.makeText(context, "Uploaded successfully!", Toast.LENGTH_SHORT).show();
//                switch (storeDocumentMasterId) {
//
//                    case "1":
//                        docOne = true;
//                        aadhaar.setEnabled(false);
//                        aadhaar.setFocusable(false);
//                        aadhaar.setClickable(false);
//                        aadhaar.setText("Uploaded");
//                        aadhaar.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_uploded, 0, 0);
//                        break;
//                    case "2":
//                        docTwo = true;
//                        transferCertificate.setEnabled(false);
//                        transferCertificate.setFocusable(false);
//                        transferCertificate.setClickable(false);
//                        transferCertificate.setText("Uploaded");
//                        transferCertificate.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_uploded, 0, 0);
//                        break;
//                    case "3":
//                        docThree = true;
//                        communityCertificate.setEnabled(false);
//                        communityCertificate.setFocusable(false);
//                        communityCertificate.setClickable(false);
//                        communityCertificate.setText("Uploaded");
//                        communityCertificate.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_uploded, 0, 0);
//                        break;
//                    case "4":
//                        docFour = true;
//                        rationCard.setEnabled(false);
//                        rationCard.setFocusable(false);
//                        rationCard.setClickable(false);
//                        rationCard.setText("Uploaded");
//                        rationCard.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_uploded, 0, 0);
//                        break;
//                    case "5":
//                        docFive = true;
//                        voterId.setEnabled(false);
//                        voterId.setFocusable(false);
//                        voterId.setClickable(false);
//                        voterId.setText("Uploaded");
//                        voterId.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_uploded, 0, 0);
//                        break;
//                    case "6":
//                        docSix = true;
//                        jobCard.setEnabled(false);
//                        jobCard.setFocusable(false);
//                        jobCard.setClickable(false);
//                        jobCard.setText("Uploaded");
//                        jobCard.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_uploded, 0, 0);
//                        break;
//                    case "7":
//                        docSeven = true;
//                        disability.setEnabled(false);
//                        disability.setFocusable(false);
//                        disability.setClickable(false);
//                        disability.setText("Uploaded");
//                        disability.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_uploded, 0, 0);
//                        break;
//                    case "8":
//                        docEight = true;
//                        passBook.setEnabled(false);
//                        passBook.setFocusable(false);
//                        passBook.setClickable(false);
//                        passBook.setText("Uploaded");
//                        passBook.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_uploded, 0, 0);
//                        break;
//                }
            } else {
                Toast.makeText(context, "Unable to upload file", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
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

//                            mUpdatedImageUrl = resp.getString("student_picture");

//                            Log.d(TAG, "updated image url is" + mUpdatedImageUrl);
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
                Toast.makeText(context, "Unable to save picture", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(context, "Student profile image updated successfully...", Toast.LENGTH_SHORT).show();
//            saveCandidate();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    @Override
    public void onError(String error) {

    }
}