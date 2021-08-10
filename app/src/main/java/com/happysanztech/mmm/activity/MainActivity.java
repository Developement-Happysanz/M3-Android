package com.happysanztech.mmm.activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.multidex.BuildConfig;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
//import com.happysanztech.mmm.BuildConfig;
import com.happysanztech.mmm.R;
import com.happysanztech.mmm.bean.database.SQLiteHelper;
import com.happysanztech.mmm.fragments.AddCandidateFragment;
import com.happysanztech.mmm.helper.AlertDialogHelper;
import com.happysanztech.mmm.helper.ProgressDialogHelper;
import com.happysanztech.mmm.interfaces.DialogClickListener;
import com.happysanztech.mmm.servicehelpers.LocationUpdatesService;
import com.happysanztech.mmm.servicehelpers.ServiceHelper;
import com.happysanztech.mmm.serviceinterfaces.IServiceListener;
import com.happysanztech.mmm.syncadapter.UploadDataSyncAdapter;
import com.happysanztech.mmm.utils.MobilizerConstants;
import com.happysanztech.mmm.utils.PreferenceStorage;
import com.happysanztech.mmm.utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        DialogClickListener, IServiceListener, SharedPreferences.OnSharedPreferenceChangeListener, View.OnClickListener {

    private int count = 0;
    private static final String TAG = com.happysanztech.mmm.activity.AddCandidateActivity.class.getName();
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
    boolean firstLaunch = true;
    private static final int IGNORE_BATTERY_OPTIMIZATION_REQUEST = 1002;
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    File image = null;

    // Used in checking for runtime permissions.
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    // The BroadcastReceiver used to listen from broadcasts from the service.
    private MyReceiver myReceiver;

    // A reference to the service used to get location updates.
    private LocationUpdatesService mService = null;

    // Tracks the bound state of the service.
    private boolean mBound = false;

    // UI elements.
    private Button mRequestLocationUpdatesButton;
    private Button mRemoveLocationUpdatesButton;

    // Monitors the state of the connection to the service.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };

    private LinearLayout layProspects, layAddCandidate, layCenterInformation, layTrades, layTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myReceiver = new MyReceiver();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        database = new SQLiteHelper(getApplicationContext());
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        View hView = navigationView.getHeaderView(0);

        ivUserProfile = (ImageView) hView.findViewById(R.id.imageView);

        String url = PreferenceStorage.getUserPicture(this);
        if (((url != null) && !(url.isEmpty()))) {
            Picasso.get().load(url).placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile).into(ivUserProfile);
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
//                openImageIntent();
            }
        });

        try {
            String alarm = Context.ALARM_SERVICE;
            am = (AlarmManager) getSystemService(alarm);

            Intent intent = new Intent("REFRESH_THIS");
            pi = PendingIntent.getBroadcast(this, 123456789, intent, 0);

            int type = AlarmManager.RTC_WAKEUP;
            long interval = 50 * 50;

            am.setInexactRepeating(type, System.currentTimeMillis(), interval, pi);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }

//        startService(new Intent(MainActivity.this, GoogleLocationService.class));

        if (!checkPhoneModel()) {
            if (PreferenceStorage.getLocationCheck(this)) {
                PreferenceStorage.saveLocationCheck(this, false);
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setTitle("Auto Start");
                alertDialogBuilder.setMessage("Enable auto start for the app to function properly");
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        addAutoStartup();
                    }
                });
                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.show();

            }
        }

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

        layProspects = findViewById(R.id.prospects);
        layProspects.setOnClickListener(this);

        layAddCandidate = findViewById(R.id.add_candidates);
        layAddCandidate.setOnClickListener(this);

        layCenterInformation = findViewById(R.id.center_information);
        layCenterInformation.setOnClickListener(this);

        layTrades = findViewById(R.id.trades);
        layTrades.setOnClickListener(this);

        layTasks = findViewById(R.id.task);
        layTasks.setOnClickListener(this);

        dailyLoginActivity();
//        loadDashboard();

        UploadDataSyncAdapter.initializeSyncAdapter(getApplicationContext());

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

    private void dailyLoginActivity() {

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put(MobilizerConstants.KEY_USER_ID, PreferenceStorage.getUserId(this));
            jsonObject.put(MobilizerConstants.DAILY_ACTIVITY, "login");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = MobilizerConstants.BUILD_URL + MobilizerConstants.USER_DAILY_ACTIVITY;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
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
            /*Intent i = new Intent(getApplicationContext(), NotificationActivity.class);
            startActivity(i);*/
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
            mRequestLocationUpdatesButton.setVisibility(View.VISIBLE);
            mRemoveLocationUpdatesButton.setVisibility(View.VISIBLE);
            // Handle the camera action
//            fragment = new DashboardActivity();
//            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            ft.replace(R.id.flContent, fragment);
//            ft.commit();
            Intent navigationIntent = new Intent(this, MainActivity.class);
            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(navigationIntent);
        } else if (id == R.id.nav_add_candidate) {
            mRequestLocationUpdatesButton.setVisibility(View.GONE);
            mRemoveLocationUpdatesButton.setVisibility(View.GONE);
//            fragment = new AddCandidateFragment();
//            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            ft.replace(R.id.flContent, fragment);
//            ft.commit();
            Intent navigationIntent = new Intent(this, AddCandidateFragment.class);
            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(navigationIntent);
        } else if (id == R.id.nav_center_information) {
            mRequestLocationUpdatesButton.setVisibility(View.GONE);
            mRemoveLocationUpdatesButton.setVisibility(View.GONE);
//            fragment = new CenterInfoActivity();
//            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            ft.replace(R.id.flContent, fragment);
//            ft.commit();
            Intent navigationIntent = new Intent(this, CenterInfoActivity.class);
            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(navigationIntent);
        }
        /*else if (id == R.id.nav_batch_details) {
            fragment = new BatchDetailsFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContent, fragment);
            ft.commit();
        }*/
        else if (id == R.id.nav_trade) {
            mRequestLocationUpdatesButton.setVisibility(View.GONE);
            mRemoveLocationUpdatesButton.setVisibility(View.GONE);
//            fragment = new TradeActivity();
//            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            ft.replace(R.id.flContent, fragment);
//            ft.commit();
            Intent navigationIntent = new Intent(this, TradeActivity.class);
            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(navigationIntent);
        } else if (id == R.id.nav_task) {
            mRequestLocationUpdatesButton.setVisibility(View.GONE);
            mRemoveLocationUpdatesButton.setVisibility(View.GONE);
//            fragment = new TaskActivity();
//            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            ft.replace(R.id.flContent, fragment);
//            ft.commit();
            Intent navigationIntent = new Intent(this, MobiliserWorkTypeActivity.class);
            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(navigationIntent);
        } else if (id == R.id.nav_sync) {
            mRequestLocationUpdatesButton.setVisibility(View.GONE);
            mRemoveLocationUpdatesButton.setVisibility(View.GONE);
            /*fragment = new SyncFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContent, fragment);
            ft.commit();*/
            Intent navigationIntent = new Intent(this, SyncRecordsActivity.class);
            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(navigationIntent);
        } else if (id == R.id.nav_footer_1) {
            mRequestLocationUpdatesButton.setVisibility(View.GONE);
            mRemoveLocationUpdatesButton.setVisibility(View.GONE);
            Intent navigationIntent = new Intent(this, ProfileActivity.class);
            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(navigationIntent);
        } else if (id == R.id.nav_footer_2) {
            mRequestLocationUpdatesButton.setVisibility(View.GONE);
            mRemoveLocationUpdatesButton.setVisibility(View.GONE);
//            fragment = new ChangePasswordActivity();
//            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            ft.replace(R.id.flContent, fragment);
//            ft.commit();
            Intent navigationIntent = new Intent(this, AboutUsActivity.class);
            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(navigationIntent);
        } else if (id == R.id.nav_change_password) {
            mRequestLocationUpdatesButton.setVisibility(View.GONE);
            mRemoveLocationUpdatesButton.setVisibility(View.GONE);
//            fragment = new ChangePasswordActivity();
//            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            ft.replace(R.id.flContent, fragment);
//            ft.commit();
            Intent navigationIntent = new Intent(this, ChangePasswordActivity.class);
            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(navigationIntent);
        } else if (id == R.id.nav_logout) {
            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Logout");
            alertDialogBuilder.setMessage("Do you really want to logout?");
            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    doLogout();
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadDashboard() {

//        Fragment fragment = new DashboardActivity();
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.flContent, fragment);
//        ft.commit();
        Intent navigationIntent = new Intent(this, DashboardActivity.class);
        navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(navigationIntent);
    }

    public void doLogout() {

        database.deleteAllCurrentBestLocation();
        database.deleteAllPreviousBestLocation();
        database.deleteAllStoredLocationData();
        database.deleteAllStoredProspectData();
        database.deleteAllStoredDocData();
//        deleteTableRecords.deleteAllRecords();
//        stopService(new Intent(MainActivity.this, GoogleLocationService.class));
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

    private boolean validateSignInResponse(JSONObject response) {
        boolean signInSuccess = false;
        if ((response != null)) {
            try {
                String status = response.getString("status");
                String msg = response.getString(MobilizerConstants.PARAM_MESSAGE);
                Log.d(TAG, "status val" + status + "msg" + msg);

                if ((status != null)) {
                    if (((status.equalsIgnoreCase("activationError")) || (status.equalsIgnoreCase("alreadyRegistered")) ||
                            (status.equalsIgnoreCase("notRegistered")) || (status.equalsIgnoreCase("error")))) {
                        signInSuccess = false;
                        Log.d(TAG, "Show error dialog");
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
        if (validateSignInResponse(response)) {
        }
    }

    @Override
    public void onError(String error) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        mRequestLocationUpdatesButton = (Button) findViewById(R.id.request_location_updates_button);
        mRemoveLocationUpdatesButton = (Button) findViewById(R.id.remove_location_updates_button);

        mRequestLocationUpdatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkPermissions()) {
                    requestPermissions();
                } else {
                    mService.requestLocationUpdates();
                    PreferenceStorage.saveTrackStatus(getApplicationContext(),"Start");
                }
            }
        });

        mRemoveLocationUpdatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenceStorage.saveTrackStatus(getApplicationContext(),"Stop");
                mService.removeLocationUpdates();

            }
        });

        // Restore the state of the buttons when the activity (re)launches.
        setButtonsState(Utils.requestingLocationUpdates(this));

        // Bind to the service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can exit foreground mode.
        bindService(new Intent(this, LocationUpdatesService.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
                new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            unbindService(mServiceConnection);
            mBound = false;
        }
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    /**
     * Returns the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    findViewById(R.id.drawer_layout),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                mService.requestLocationUpdates();
            } else {
                // Permission denied.
                setButtonsState(false);
                Snackbar.make(
                        findViewById(R.id.drawer_layout),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }

    @Override
    public void onClick(View v) {

        if (v == layProspects) {
//            Fragment fragment = new AddCandidateFragment();
//            FragmentManager fragmentManager = this.getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.flContent, fragment);
//            fragmentTransaction.addToBackStack(null);
//            fragmentTransaction.commit();
            Intent navigationIntent = new Intent(this, ProspectsActivity.class);
            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(navigationIntent);
        }
        if (v == layAddCandidate) {
//            Fragment fragment = new AddCandidateFragment();
//            FragmentManager fragmentManager = this.getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.flContent, fragment);
//            fragmentTransaction.addToBackStack(null);
//            fragmentTransaction.commit();
            Intent navigationIntent = new Intent(this, AddCandidateFragment.class);
            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(navigationIntent);
        }
        if (v == layCenterInformation) {
//            Fragment fragment = new CenterInfoActivity();
//            FragmentManager fragmentManager = this.getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.flContent, fragment);
//            fragmentTransaction.addToBackStack(null);
//            fragmentTransaction.commit();
            Intent navigationIntent = new Intent(this, CenterInfoActivity.class);
            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(navigationIntent);
        }
        if (v == layTrades) {
//            Fragment fragment = new TradeActivity();
//            FragmentManager fragmentManager = this.getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.flContent, fragment);
//            fragmentTransaction.addToBackStack(null);
//            fragmentTransaction.commit();
            Intent navigationIntent = new Intent(this, TradeActivity.class);
            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(navigationIntent);
        }
        if (v == layTasks) {
//            Fragment fragment = new TaskActivity();
//            FragmentManager fragmentManager = this.getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.flContent, fragment);
//            fragmentTransaction.addToBackStack(null);
//            fragmentTransaction.commit();
            Intent navigationIntent = new Intent(this, MobiliserWorkTypeActivity.class);
            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(navigationIntent);
        }
    }

    /**
     * Receiver for broadcasts sent by {@link LocationUpdatesService}.
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
            if (location != null) {
                Toast.makeText(MainActivity.this, Utils.getLocationText(location),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        // Update the buttons state depending on whether location updates are being requested.
        if (s.equals(Utils.KEY_REQUESTING_LOCATION_UPDATES)) {
            setButtonsState(sharedPreferences.getBoolean(Utils.KEY_REQUESTING_LOCATION_UPDATES,
                    false));
        }
    }

    private void setButtonsState(boolean requestingLocationUpdates) {
        if (requestingLocationUpdates) {
            mRequestLocationUpdatesButton.setEnabled(false);
            mRemoveLocationUpdatesButton.setEnabled(true);
        } else {
            mRequestLocationUpdatesButton.setEnabled(true);
            mRemoveLocationUpdatesButton.setEnabled(false);
        }
    }

//    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
//        private static final String TAG = "UploadFileToServer";
//        private HttpClient httpclient;
//        HttpPost httppost;
//        public boolean isTaskAborted = false;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... progress) {
//
//        }
//
//        @Override
//        protected String doInBackground(Void... params) {
//            return uploadFile();
//        }
//
//        @SuppressWarnings("deprecation")
//        private String uploadFile() {
//            String responseString = null;
//
//            httpclient = new DefaultHttpClient();
//            httppost = new HttpPost(String.format(MobilizerConstants.BUILD_URL + MobilizerConstants.UPLOAD_USER_PIC + PreferenceStorage.getUserId(getApplicationContext()) + "/"));
//
//            try {
//                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
//                        new AndroidMultiPartEntity.ProgressListener() {
//
//                            @Override
//                            public void transferred(long num) {
//
//                            }
//                        });
//                Log.d(TAG, "actual file path is" + mActualFilePath);
//                if (mActualFilePath != null) {
//
//                    File sourceFile = new File(mActualFilePath);
//
//                    // Adding file data to http body
//                    //fileToUpload
//                    entity.addPart("user_pic", new FileBody(sourceFile));
//
//                    // Extra parameters if you want to pass to server
////                    entity.addPart("user_id", new StringBody(PreferenceStorage.getUserId(getApplicationContext())));
////                    entity.addPart("user_type", new StringBody(PreferenceStorage.getUserType(ProfileActivity.this)));
//
//                    totalSize = entity.getContentLength();
//                    httppost.setEntity(entity);
//
//                    // Making server call
//                    HttpResponse response = httpclient.execute(httppost);
//                    HttpEntity r_entity = response.getEntity();
//
//                    int statusCode = response.getStatusLine().getStatusCode();
//                    if (statusCode == 200) {
//                        // Server response
//                        responseString = EntityUtils.toString(r_entity);
//                        try {
//                            JSONObject resp = new JSONObject(responseString);
//                            String successVal = resp.getString("status");
//
//                            mUpdatedImageUrl = resp.getString("picture_url");
//                            PreferenceStorage.saveUserPicture(getApplicationContext(), mUpdatedImageUrl);
//
//                            Log.d(TAG, "updated image url is" + mUpdatedImageUrl);
//                            if (successVal.equalsIgnoreCase("success")) {
//                                Log.d(TAG, "Updated image succesfully");
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        responseString = "Error occurred! Http Status Code: "
//                                + statusCode;
//                    }
//                }
//
//            } catch (ClientProtocolException e) {
//                responseString = e.toString();
//            } catch (IOException e) {
//                responseString = e.toString();
//            }
//
//            return responseString;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            Log.e(TAG, "Response from server: " + result);
//
//            super.onPostExecute(result);
//            if ((result == null) || (result.isEmpty()) || (result.contains("Error"))) {
//                Toast.makeText(MainActivity.this, "Unable to save profile picture", Toast.LENGTH_SHORT).show();
//            } else {
//                if (mUpdatedImageUrl != null) {
//                    PreferenceStorage.saveUserPicture(MainActivity.this, mUpdatedImageUrl);
//                }
//            }
//
//            if (mProgressDialog != null) {
//                mProgressDialog.cancel();
//            }
//
//            Toast.makeText(getApplicationContext(), "User profile image successfully...", Toast.LENGTH_SHORT).show();
////            finish();
//        }
//
//        @Override
//        protected void onCancelled() {
//            super.onCancelled();
//        }
//    }

//    /**
//     * Uploading the file to server
//     */
//    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
//        private static final String TAG = "UploadFileToServer";
//        private HttpClient httpclient;
//        HttpPost httppost;
//        public boolean isTaskAborted = false;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... progress) {
//
//        }
//
//        @Override
//        protected String doInBackground(Void... params) {
//            return uploadFile();
//        }
//
//        @SuppressWarnings("deprecation")
//        private String uploadFile() {
//            String responseString = null;
//
//            httpclient = new DefaultHttpClient();
//            httppost = new HttpPost(String.format(MobilizerConstants.BUILD_URL + MobilizerConstants.UPLOAD_USER_PIC + PreferenceStorage.getUserId(getApplicationContext())));
//
//            try {
//                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
//                        new AndroidMultiPartEntity.ProgressListener() {
//
//                            @Override
//                            public void transferred(long num) {
//
//                            }
//                        });
//                Log.d(TAG, "actual file path is" + mActualFilePath);
//                if (mActualFilePath != null) {
//
//                    File sourceFile = new File(mActualFilePath);
//
//                    // Adding file data to http body
//                    //fileToUpload
//                    entity.addPart("user_pic", new FileBody(sourceFile));
//
//                    // Extra parameters if you want to pass to server
//                    entity.addPart("user_id", new StringBody(PreferenceStorage.getUserId(getApplicationContext())));
////                    entity.addPart("user_type", new StringBody(PreferenceStorage.getUserType(MainActivity.this)));
//
//                    totalSize = entity.getContentLength();
//                    httppost.setEntity(entity);
//
//                    // Making server call
//                    HttpResponse response = httpclient.execute(httppost);
//                    HttpEntity r_entity = response.getEntity();
//
//                    int statusCode = response.getStatusLine().getStatusCode();
//                    if (statusCode == 200) {
//                        // Server response
//                        responseString = EntityUtils.toString(r_entity);
//                        try {
//                            JSONObject resp = new JSONObject(responseString);
//                            String successVal = resp.getString("status");
//
//                            mUpdatedImageUrl = resp.getString("user_picture");
//
//                            Log.d(TAG, "updated image url is" + mUpdatedImageUrl);
//                            if (successVal.equalsIgnoreCase("success")) {
//                                Log.d(TAG, "Updated image succesfully");
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        responseString = "Error occurred! Http Status Code: "
//                                + statusCode;
//                    }
//                }
//
//            } catch (ClientProtocolException e) {
//                responseString = e.toString();
//            } catch (IOException e) {
//                responseString = e.toString();
//            }
//
//            return responseString;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            Log.e(TAG, "Response from server: " + result);
//
//            super.onPostExecute(result);
//            if ((result == null) || (result.isEmpty()) || (result.contains("Error"))) {
//                Toast.makeText(MainActivity.this, "Unable to save profile picture", Toast.LENGTH_SHORT).show();
//            } else {
//                if (mUpdatedImageUrl != null) {
//                    PreferenceStorage.saveUserPicture(MainActivity.this, mUpdatedImageUrl);
//                }
//            }
//
//            if (mProgressDialog != null) {
//                mProgressDialog.cancel();
//            }
//
//            Toast.makeText(getApplicationContext(), "User profile image successfully...", Toast.LENGTH_SHORT).show();
////            finish();
////            saveCandidate();
//        }
//
//        @Override
//        protected void onCancelled() {
//            super.onCancelled();
//        }
//    }

    private boolean checkPhoneModel() {

        String manufacturer = android.os.Build.MANUFACTURER;

        if ("xiaomi".equalsIgnoreCase(manufacturer)) {
            return false;
        } else if ("oppo".equalsIgnoreCase(manufacturer)) {
            return false;
        } else if ("vivo".equalsIgnoreCase(manufacturer)) {
            return false;
        } else if ("Letv".equalsIgnoreCase(manufacturer)) {
            return false;
        } else if ("Honor".equalsIgnoreCase(manufacturer)) {
            return false;
        } else if ("oneplus".equalsIgnoreCase(manufacturer)) {
            return false;
        } else {
            return true;
        }
    }

    private void addAutoStartup() {

        try {
            Intent intent = new Intent();
            String manufacturer = android.os.Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity"));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            } else if ("Letv".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
            } else if ("Honor".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            } else if ("oneplus".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.oneplus.security", "com.oneplus.security.chainlaunch.view.ChainLaunchAppListAct‌​ivity"));
            }
//            intent.setComponent(new ComponentName("com.samsung.android.lool",
//                    "com.samsung.android.sm.ui.battery.BatteryActivity"));
//                    new Intent("miui.intent.action.OP_AUTO_START").addCategory(Intent.CATEGORY_DEFAULT);
//                    intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
//                    intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
//                    intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
//                    intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
//                    intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity"));
//                    intent.setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity"));
//                    intent.setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"));
//                    intent.setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager"));
//                    intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
//                    intent.setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.entry.FunctionActivity")).setData(
//                            Uri.parse("mobilemanager://function/entry/AutoStart"));

            List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (list.size() > 0) {
                startActivity(intent);
            }
        } catch (Exception e) {
            Log.e("exc", String.valueOf(e));
        }
    }
}
