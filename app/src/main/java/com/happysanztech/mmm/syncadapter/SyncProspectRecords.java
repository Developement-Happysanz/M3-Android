package com.happysanztech.mmm.syncadapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.happysanztech.mmm.R;
import com.happysanztech.mmm.activity.AddCandidateActivity;
import com.happysanztech.mmm.activity.MainActivity;
import com.happysanztech.mmm.bean.database.SQLiteHelper;
import com.happysanztech.mmm.helper.AlertDialogHelper;
import com.happysanztech.mmm.servicehelpers.ServiceHelper;
import com.happysanztech.mmm.serviceinterfaces.IServiceListener;
import com.happysanztech.mmm.utils.AndroidMultiPartEntity;
import com.happysanztech.mmm.utils.MobilizerConstants;
import com.happysanztech.mmm.utils.PreferenceStorage;

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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.util.Log.d;

public class SyncProspectRecords implements IServiceListener {

    private ServiceHelper serviceHelper;
    private Context context;
    private SQLiteHelper database;
    private String _id = "";
    private String mActualFilePath = "";
    private String admissionId = "";
    long totalSize = 0;

    public SyncProspectRecords(Context context) {
        this.context = context;
        serviceHelper = new ServiceHelper(context);
        serviceHelper.setServiceListener(this);
    }

    public void SyncToServer() {
        callService();
    }

    private void callService() {

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
        String CandidatesyearOfEdu ="";
        String CandidatesyearOfPass ="";
        String CandidatesiMarkOne = "";
        String CandidatesiMarkTwo ="";
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