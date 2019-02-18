package com.happysanztech.mmm.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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


import com.happysanztech.mmm.R;
import com.happysanztech.mmm.activity.AddCandidateActivity;
import com.happysanztech.mmm.activity.CenterPhotosActivity;
import com.happysanztech.mmm.activity.CenterVideos;
import com.happysanztech.mmm.activity.CenterVideosActivity;
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

public class CenterInfoFragment extends Fragment implements View.OnClickListener, IServiceListener, DialogClickListener {

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

    public CenterInfoFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_center_info, container, false);
        serviceHelper = new ServiceHelper(getActivity());
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(getActivity());
        txtBannerName = rootView.findViewById(R.id.im_banner1);
        txtDescriptionDetails = rootView.findViewById(R.id.txt_description_details);
        btnTrades = rootView.findViewById(R.id.btn_trades);
        btnTrades.setOnClickListener(this);
        btnPhotos = rootView.findViewById(R.id.btn_photos);
        btnPhotos.setOnClickListener(this);
        btnVideos = rootView.findViewById(R.id.btn_videos);
        btnVideos.setOnClickListener(this);
        loadCenterInfo();
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v == btnPhotos) {
            Intent myIntent = new Intent(getActivity(), CenterPhotosActivity.class);

            getActivity().startActivity(myIntent);
        }
        if (v == btnVideos) {
            Intent myIntent = new Intent(getActivity(), VideoListDemoActivity.class);
            getActivity().startActivity(myIntent);
        }
        if (v == btnTrades) {
            Fragment fragment = new TradeFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.flContent, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
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
                        AlertDialogHelper.showSimpleAlertDialog(getActivity(), msg);

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

                PreferenceStorage.saveCenterId(getActivity(), centerId);

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
        AlertDialogHelper.showSimpleAlertDialog(getActivity(), error);

    }

    private void loadCenterInfo() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(MobilizerConstants.KEY_USER_ID, PreferenceStorage.getUserId(getActivity()));
            jsonObject.put(MobilizerConstants.KEY_PIA_ID, PreferenceStorage.getPIAId(getActivity()));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = MobilizerConstants.BUILD_URL + MobilizerConstants.CENTER_INFO;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void loadTrainerUI(JSONArray getTrainer) {
        try {
            layout_trainer = rootView.findViewById(R.id.layout_trainer);
            TableLayout layout = new TableLayout(getActivity());
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

                TableRow tr = new TableRow(getActivity());

                tr.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                tr.setBackgroundColor(Color.WHITE);
                tr.setPadding(0, 0, 0, 1);

                TableRow.LayoutParams llp = new
                        TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                llp.setMargins(1, 1, 1, 1);//2px right-margin

                for (int c1 = 0; c1 < getTrainer.length(); c1++) {

                    LinearLayout cell = new LinearLayout(getActivity());
                    cell.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
                    TextView viewDateFormat = new TextView(getActivity());
                    final TextView trainerName = new TextView(getActivity());
                    Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/open_sans_regular.ttf");
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
                            Toast.makeText(getActivity(), trainerName.getText(), Toast.LENGTH_LONG).show();
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
            layout_success_stories = rootView.findViewById(R.id.layout_success_stories);
            TableLayout layout = new TableLayout(getActivity());
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

                TableRow tr = new TableRow(getActivity());

                tr.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                tr.setBackgroundColor(Color.WHITE);
                tr.setPadding(0, 0, 0, 1);

                TableRow.LayoutParams llp = new
                        TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                llp.setMargins(1, 1, 1, 1);//2px right-margin

                for (int c1 = 0; c1 < getSuccessStories.length(); c1++) {

                    LinearLayout cell = new LinearLayout(getActivity());
                    cell.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
                    TextView viewDateFormat = new TextView(getActivity());
                    final TextView trainerName = new TextView(getActivity());
                    Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/open_sans_regular.ttf");
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
                            Toast.makeText(getActivity(), trainerName.getText(), Toast.LENGTH_LONG).show();
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
