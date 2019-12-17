package com.happysanztech.mmm.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.happysanztech.mmm.R;
import com.happysanztech.mmm.activity.CenterPhotosActivity;
import com.happysanztech.mmm.activity.VideoListDemoActivity;
import com.happysanztech.mmm.helper.AlertDialogHelper;
import com.happysanztech.mmm.helper.ProgressDialogHelper;
import com.happysanztech.mmm.interfaces.DialogClickListener;
import com.happysanztech.mmm.servicehelpers.ServiceHelper;
import com.happysanztech.mmm.serviceinterfaces.IServiceListener;
import com.happysanztech.mmm.utils.MobilizerConstants;
import com.happysanztech.mmm.utils.PreferenceStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.util.Log.d;


/**
 * Created by Admin on 03-01-2018.
 */

public class CenterInfoFragment extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener {

    private static final String TAG = "CenterInfoFragment";
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private TextView txtDescriptionDetails, txtBannerName;
    private Button btnTrades;
    private Button btnPhotos;
    private Button btnVideos;
    private LinearLayout layout_trainer;
    private LinearLayout layout_success_stories;
    View rootView;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_center_info);
        
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        txtBannerName = findViewById(R.id.im_banner1);
        txtDescriptionDetails = findViewById(R.id.txt_description_details);
        btnTrades = findViewById(R.id.btn_trades);
        btnTrades.setOnClickListener(this);
        btnPhotos = findViewById(R.id.btn_photos);
        btnPhotos.setOnClickListener(this);
        btnVideos = findViewById(R.id.btn_videos);
        btnVideos.setOnClickListener(this);
        loadCenterInfo();
    }

    @Override
    public void onClick(View v) {
        if (v == btnPhotos) {
            Intent myIntent = new Intent(this, CenterPhotosActivity.class);

            this.startActivity(myIntent);
        }
        if (v == btnVideos) {
            Intent myIntent = new Intent(this, VideoListDemoActivity.class);
            this.startActivity(myIntent);
        }
        if (v == btnTrades) {
            Intent navigationIntent = new Intent(this, TradeFragment.class);
            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(navigationIntent);
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
                JSONObject centerData = response.getJSONObject("centerData");

                String centerId = "";
                String centerName = "";
                String centerBanner = "";
                String centerInfo = "";
                String centerAddress = "";

                centerId = centerData.getString("center_id");
                centerName = centerData.getString("center_name");
                centerBanner = centerData.getString("center_banner");
                centerInfo = centerData.getString("center_info");
                centerAddress = centerData.getString("center_address");

                PreferenceStorage.saveCenterId(this, centerId);

                txtBannerName.setText(centerName);
                txtDescriptionDetails.setText(centerInfo);


                JSONArray getTrainer = response.getJSONArray("trainer");
                loadTrainerUI(getTrainer);

                JSONArray getSuccessStories = response.getJSONArray("stories");
                loadSuccessStoriesUI(getSuccessStories);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onError(String error) {

        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(getApplicationContext(), error);

    }

    private void loadCenterInfo() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(MobilizerConstants.KEY_USER_ID, PreferenceStorage.getUserId(this));
            jsonObject.put(MobilizerConstants.KEY_PIA_ID, PreferenceStorage.getPIAId(this));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = MobilizerConstants.BUILD_URL + MobilizerConstants.CENTER_INFO;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void loadTrainerUI(JSONArray getTrainer) {
        try {
            layout_trainer = findViewById(R.id.layout_trainer);
            TableLayout layout = new TableLayout(this);
            layout.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            layout_trainer.setScrollbarFadingEnabled(false);
            layout.setPadding(0, 5, 0, 5);

            TableLayout.LayoutParams rowLp = new TableLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            TableRow.LayoutParams cellLp = new TableRow.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);

            cellLp.setMargins(2, 2, 2, 2);

            int i = 0;
            int r = 0;
            int col = 0;
            for (int f = 0; f < 1; f++) {

                TableRow tr = new TableRow(this);

                tr.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                tr.setBackgroundColor(Color.WHITE);
                tr.setPadding(0, 0, 0, 1);

                TableRow.LayoutParams llp = new
                        TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                llp.setMargins(1, 1, 1, 1);//2px right-margin

                for (int c1 = 0; c1 < getTrainer.length(); c1++) {

                    LinearLayout cell = new LinearLayout(this);
                    cell.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
                    TextView viewDateFormat = new TextView(this);
                    final TextView trainerName = new TextView(this);
                    Typeface font = Typeface.createFromAsset(this.getAssets(), "fonts/open_sans_regular.ttf");
                    trainerName.setTypeface(font);

                    JSONObject jsonobj = getTrainer.getJSONObject(i);

                    String showName = "", showPicture = "";
                    showName = jsonobj.getString("name");
                    showPicture = jsonobj.getString("profile_pic");
                    System.out.println("showName : " + i + " = " + showName);
                    System.out.println("showPicture : " + i + " = " + showPicture);

                    /*try {
                        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
      `                  Date date = (Date) formatter.parse(showDates);
                        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
                        String month_name = month_date.format(date.getTime());
                        SimpleDateFormat event_date = new SimpleDateFormat("dd");
                        String date_name = event_date.format(date.getTime());
                        if ((showDates != null)) {
                            viewDateFormat.setText(date_name + "\n" + month_name);
                        } else {
                            viewDateFormat.setText("N/A");
                        }
                    } catch (final ParseException e) {
                        e.printStackTrace();
                    }*/

                    cell.setBackgroundColor(Color.WHITE);//argb(255,104,53,142)

                    trainerName.setText(showName);
                    viewDateFormat.setText(showName);

                    viewDateFormat.setBackgroundColor(Color.parseColor("#468dcb"));
                    viewDateFormat.setTextColor(Color.parseColor("#FFFFFF"));

                    viewDateFormat.setTextSize(13.0f);
                    trainerName.setTextSize(13.0f);

                    viewDateFormat.setTypeface(null, Typeface.BOLD);
                    trainerName.setTypeface(null, Typeface.BOLD);

                    viewDateFormat.setAllCaps(true);
                    trainerName.setAllCaps(true);

                    viewDateFormat.setGravity(Gravity.CENTER);
                    trainerName.setGravity(Gravity.CENTER);

                    viewDateFormat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
//                            viewDateFormat.setBackgroundColor(Color.parseColor("#708090"));
                            Toast.makeText(getApplicationContext(), trainerName.getText(), Toast.LENGTH_LONG).show();
                            /*showDate = functionalDateFormat.getText().toString();
                            loadBookingTimings();
                            if (showDate.equalsIgnoreCase("")) {
                                flagBookingDate = "no";
                            } else {
                                flagBookingDate = "yes";
                            }*/
                        }
                    });

                    viewDateFormat.setPressed(true);
                    viewDateFormat.setHeight(200);
                    trainerName.setHeight(0);

                    viewDateFormat.setWidth(200);
                    trainerName.setWidth(0);

                    viewDateFormat.setPadding(1, 0, 2, 0);
                    trainerName.setPadding(0, 0, 0, 0);
                    trainerName.setVisibility(View.INVISIBLE);
                    cell.addView(viewDateFormat);
                    cell.addView(trainerName);
                    cell.setLayoutParams(llp);//2px border on the right for the cell

                    tr.addView(cell, cellLp);
                    i++;
                    col++;
                } // for
                layout.addView(tr, rowLp);
                r++;
            }
            // for

            layout_trainer.addView(layout);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadSuccessStoriesUI(JSONArray getSuccessStories) {
        try {
            layout_success_stories = findViewById(R.id.layout_success_stories);
            TableLayout layout = new TableLayout(this);
            layout.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            layout_success_stories.setScrollbarFadingEnabled(false);
            layout.setPadding(0, 5, 0, 5);

            TableLayout.LayoutParams rowLp = new TableLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            TableRow.LayoutParams cellLp = new TableRow.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);

            cellLp.setMargins(2, 2, 2, 2);

            int i = 0;
            int r = 0;
            int col = 0;
            for (int f = 0; f < 1; f++) {

                TableRow tr = new TableRow(this);

                tr.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                tr.setBackgroundColor(Color.WHITE);
                tr.setPadding(0, 0, 0, 1);

                TableRow.LayoutParams llp = new
                        TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                llp.setMargins(1, 1, 1, 1);//2px right-margin

                for (int c1 = 0; c1 < getSuccessStories.length(); c1++) {

                    LinearLayout cell = new LinearLayout(this);
                    cell.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
                    TextView viewDateFormat = new TextView(this);
                    final TextView trainerName = new TextView(this);
                    Typeface font = Typeface.createFromAsset(this.getAssets(), "fonts/open_sans_regular.ttf");
                    trainerName.setTypeface(font);

                    JSONObject jsonobj = getSuccessStories.getJSONObject(i);

                    String showName = "", showPicture = "";
                    showName = jsonobj.getString("storydetails");
                    showPicture = jsonobj.getString("storyvideo");
                    System.out.println("showName : " + i + " = " + showName);
                    System.out.println("showPicture : " + i + " = " + showPicture);

                    /*try {
                        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
                        Date date = (Date) formatter.parse(showDates);
                        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
                        String month_name = month_date.format(date.getTime());
                        SimpleDateFormat event_date = new SimpleDateFormat("dd");
                        String date_name = event_date.format(date.getTime());
                        if ((showDates != null)) {
                            viewDateFormat.setText(date_name + "\n" + month_name);
                        } else {
                            viewDateFormat.setText("N/A");
                        }
                    } catch (final ParseException e) {
                        e.printStackTrace();
                    }*/

                    cell.setBackgroundColor(Color.WHITE);//argb(255,104,53,142)

                    trainerName.setText(showName);
                    viewDateFormat.setText(showName);

                    viewDateFormat.setBackgroundColor(Color.parseColor("#468dcb"));
                    viewDateFormat.setTextColor(Color.parseColor("#FFFFFF"));

                    viewDateFormat.setTextSize(13.0f);
                    trainerName.setTextSize(13.0f);

                    viewDateFormat.setTypeface(null, Typeface.BOLD);
                    trainerName.setTypeface(null, Typeface.BOLD);

                    viewDateFormat.setAllCaps(true);
                    trainerName.setAllCaps(true);

                    viewDateFormat.setGravity(Gravity.CENTER);
                    trainerName.setGravity(Gravity.CENTER);

                    viewDateFormat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
//                            viewDateFormat.setBackgroundColor(Color.parseColor("#708090"));
                            Toast.makeText(getApplicationContext(), trainerName.getText(), Toast.LENGTH_LONG).show();
                            /*showDate = functionalDateFormat.getText().toString();
                            loadBookingTimings();
                            if (showDate.equalsIgnoreCase("")) {
                                flagBookingDate = "no";
                            } else {
                                flagBookingDate = "yes";
                            }*/
                        }
                    });

                    viewDateFormat.setPressed(true);
                    viewDateFormat.setHeight(200);
                    trainerName.setHeight(0);

                    viewDateFormat.setWidth(200);
                    trainerName.setWidth(0);

                    viewDateFormat.setPadding(1, 0, 2, 0);
                    trainerName.setPadding(0, 0, 0, 0);
                    trainerName.setVisibility(View.INVISIBLE);
                    cell.addView(viewDateFormat);
                    cell.addView(trainerName);
                    cell.setLayoutParams(llp);//2px border on the right for the cell

                    tr.addView(cell, cellLp);
                    i++;
                    col++;
                } // for
                layout.addView(tr, rowLp);
                r++;
            }
            // for

            layout_success_stories.addView(layout);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
