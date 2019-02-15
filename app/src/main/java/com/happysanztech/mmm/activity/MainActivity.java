package com.happysanztech.mmm.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.happysanztech.mmm.R;
import com.happysanztech.mmm.bean.database.SQLiteHelper;
import com.happysanztech.mmm.fragments.AddCandidateFragment;
import com.happysanztech.mmm.fragments.CenterInfoFragment;
import com.happysanztech.mmm.fragments.ChangePasswordFragment;
import com.happysanztech.mmm.fragments.DashboardFragment;
import com.happysanztech.mmm.fragments.TaskFragment;
import com.happysanztech.mmm.fragments.TradeFragment;
import com.happysanztech.mmm.interfaces.DialogClickListener;
import com.happysanztech.mmm.servicehelpers.LocationService;
import com.happysanztech.mmm.utils.AndroidMultiPartEntity;
import com.happysanztech.mmm.utils.MobilizerConstants;
import com.happysanztech.mmm.utils.PreferenceStorage;
import com.squareup.picasso.Picasso;

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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        DialogClickListener {

    private int count = 0;
    private static final String TAG = AddCandidateActivity.class.getName();
    private Uri outputFileUri;
    static final int REQUEST_IMAGE_GET = 1;
    private String mActualFilePath = null;
    private Uri mSelectedImageUri = null;
    private Bitmap mCurrentUserImageBitmap = null;
    private ProgressDialog mProgressDialog = null;
    private String mUpdatedImageUrl = null;
    long totalSize = 0;
    ImageView ivUserProfile;
    AlarmManager am;
    PendingIntent pi;
    SQLiteHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        database = new SQLiteHelper(getApplicationContext());
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
       /* if (Department.contentEquals("31964e21-6996-4910-a936-12496e9f0559")) {
            MenuItem item = navigationView.getMenu().getItem(2);
            item.setVisible(false);
        } else if (Department.contentEquals("13b65ba2-9c63-40f9-91b9-5c52d7209977")) {
            MenuItem item = navigationView.getMenu().getItem(1);
            item.setVisible(false);
        }
*/


        View hView = navigationView.getHeaderView(0);

        ivUserProfile = (ImageView) hView.findViewById(R.id.imageView);

        String url = PreferenceStorage.getUserPicture(this);
        if (((url != null) && !(url.isEmpty()))) {
            Picasso.with(this).load(url).placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile).into(ivUserProfile);
        }
        String name = PreferenceStorage.getName(this);
        TextView headerName = (TextView) hView.findViewById(R.id.txtName);
//        iv.setImageResource(R.drawable.ic_profile);
        if (((name != null) && !(name.isEmpty()))) {
            headerName.setText(name);
        } else {
            headerName.setText("Mobilizer");
        }

        ivUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "Hi", Toast.LENGTH_LONG).show();
//                doIncrease();
                openImageIntent();
            }
        });

        try {
            String alarm = Context.ALARM_SERVICE;
            am = (AlarmManager) getSystemService(alarm);

            Intent intent = new Intent("REFRESH_THIS");
            pi = PendingIntent.getBroadcast(this, 123456789, intent, 0);

            int type = AlarmManager.RTC_WAKEUP;
            long interval = 500 * 50;

            am.setInexactRepeating(type, System.currentTimeMillis(), interval, pi);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }

        startService(new Intent(MainActivity.this, LocationService.class));

        loadDashboard();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }


    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem menuItem = menu.findItem(R.id.testAction);
        menuItem.setIcon(buildCounterDrawable(count, R.drawable.ic_notification));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.testAction) {
            Intent i = new Intent(getApplicationContext(), NotificationActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Drawable buildCounterDrawable(int count, int backgroundImageId) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.counter_menuitem_layout, null);
        view.setBackgroundResource(backgroundImageId);

        if (count == 0) {
            View counterTextPanel = view.findViewById(R.id.counterValuePanel);
            counterTextPanel.setVisibility(View.GONE);
        } else {
            TextView textView = (TextView) view.findViewById(R.id.count);
            textView.setText("" + count);
        }

        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(getResources(), bitmap);
    }

    private void doIncrease() {
        count++;
        invalidateOptionsMenu();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;

        if (id == R.id.nav_dashboard) {
            // Handle the camera action
            fragment = new DashboardFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContent, fragment);
            ft.commit();
        } else if (id == R.id.nav_add_candidate) {
            fragment = new AddCandidateFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContent, fragment);
            ft.commit();
        } else if (id == R.id.nav_center_information) {
            fragment = new CenterInfoFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContent, fragment);
            ft.commit();
        }
        /*else if (id == R.id.nav_batch_details) {
            fragment = new BatchDetailsFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContent, fragment);
            ft.commit();
        }*/
        else if (id == R.id.nav_trade) {
            fragment = new TradeFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContent, fragment);
            ft.commit();
        } else if (id == R.id.nav_task) {
            fragment = new TaskFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContent, fragment);
            ft.commit();
        } else if (id == R.id.nav_change_password) {
            fragment = new ChangePasswordFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContent, fragment);
            ft.commit();
        } else if (id == R.id.nav_logout) {
            doLogout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadDashboard() {

        Fragment fragment = new DashboardFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flContent, fragment);
        ft.commit();
    }

    public void doLogout() {

        database.deleteAllCurrentBestLocation();
        database.deleteAllPreviousBestLocation();
//        deleteTableRecords.deleteAllRecords();
        stopService(new Intent(MainActivity.this, LocationService.class));
//        stopService(new Intent(MainActivity.this, GPSTracker.class));


        am.cancel(pi);

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().clear().apply();

        Intent homeIntent = new Intent(this, SplashScreenActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        this.finish();
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    private void openImageIntent() {

        // Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyDir");

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

        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_IMAGE_GET) {
                Log.d(TAG, "ONActivity Result");
                final boolean isCamera;
                if (data == null) {
                    Log.d(TAG, "camera is true");
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    Log.d(TAG, "camera action is" + action);
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                Uri selectedImageUri;

                if (isCamera) {
                    Log.d(TAG, "Add to gallery");
                    selectedImageUri = outputFileUri;
                    mActualFilePath = outputFileUri.getPath();
                    galleryAddPic(selectedImageUri);
                } else {
                    selectedImageUri = data == null ? null : data.getData();
                    mActualFilePath = getRealPathFromURI(this, selectedImageUri);
                    Log.d(TAG, "path to image is" + mActualFilePath);

                }
                Log.d(TAG, "image Uri is" + selectedImageUri);
                if (selectedImageUri != null) {
                    Log.d(TAG, "image URI is" + selectedImageUri);
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
        int targetW = ivUserProfile.getWidth();
        int targetH = ivUserProfile.getHeight();

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
            ivUserProfile.setImageBitmap(bitmap);
            mCurrentUserImageBitmap = bitmap;

            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("Updating Profile");
            mProgressDialog.show();

            saveUserImage();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveUserImage() {

        mUpdatedImageUrl = null;

        new UploadFileToServer().execute();
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
            httppost = new HttpPost(String.format(MobilizerConstants.BUILD_URL + MobilizerConstants.UPLOAD_USER_PIC + PreferenceStorage.getUserId(getApplicationContext())));

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
                    entity.addPart("user_id", new StringBody(PreferenceStorage.getUserId(getApplicationContext())));
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
                Toast.makeText(MainActivity.this, "Unable to save profile picture", Toast.LENGTH_SHORT).show();
            } else {
                if (mUpdatedImageUrl != null) {
                    PreferenceStorage.saveUserPicture(MainActivity.this, mUpdatedImageUrl);
                }
            }

            if (mProgressDialog != null) {
                mProgressDialog.cancel();
            }

            Toast.makeText(getApplicationContext(), "User profile image successfully...", Toast.LENGTH_SHORT).show();
//            finish();
//            saveCandidate();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}
