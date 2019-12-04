package com.happysanztech.mmm.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.happysanztech.mmm.R;
import com.happysanztech.mmm.adapter.CenterVideoListAdapter;
import com.happysanztech.mmm.bean.support.CenterVideo;
import com.happysanztech.mmm.bean.support.CenterVideoList;
import com.happysanztech.mmm.bean.support.TaskData;
import com.happysanztech.mmm.helper.AlertDialogHelper;
import com.happysanztech.mmm.helper.ProgressDialogHelper;
import com.happysanztech.mmm.interfaces.DialogClickListener;
import com.happysanztech.mmm.servicehelpers.ServiceHelper;
import com.happysanztech.mmm.serviceinterfaces.IServiceListener;
import com.happysanztech.mmm.utils.MobilizerConstants;
import com.happysanztech.mmm.utils.PreferenceStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.util.Log.d;

/**
 * Created by Admin on 11-01-2018.
 */

public class CenterVideosActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener {

    private static final String TAG = CenterVideosActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private ImageView ivBack;
    private TaskData taskData;
    protected ListView loadMoreListView;
    protected CenterVideoListAdapter centerVideoListAdapter;
    protected ArrayList<CenterVideo> centerVideoArrayList;
    Handler mHandler = new Handler();
    int pageNumber = 0, totalCount = 0;
    protected boolean isLoadingForFirstTime = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center_videos);

        taskData = (TaskData) getIntent().getSerializableExtra("eventObj");

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        ivBack = findViewById(R.id.back_tic_his);
        ivBack.setOnClickListener(this);

        loadMoreListView = (ListView) findViewById(R.id.listView_events);
        centerVideoArrayList = new ArrayList<>();

        viewCenterVideos();
    }

    @Override
    public void onClick(View v) {
        if (v == ivBack) {
            finish();
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
            Gson gson = new Gson();
            CenterVideoList centerVideoList = gson.fromJson(response.toString(), CenterVideoList.class);
            if (centerVideoList.getCenterVideo() != null && centerVideoList.getCenterVideo().size() > 0) {
                totalCount = centerVideoList.getCount();
                isLoadingForFirstTime = false;
                updateListAdapter(centerVideoList.getCenterVideo());
            }
        }
    }

    @Override
    public void onError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(CenterVideosActivity.this, error);
    }

    private void viewCenterVideos() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(MobilizerConstants.PARAMS_CENTER_ID, PreferenceStorage.getCenterId(getApplicationContext()));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = MobilizerConstants.BUILD_URL + MobilizerConstants.CENTER_VIDEO_LIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    protected void updateListAdapter(ArrayList<CenterVideo> centerVideoArrayList) {
        this.centerVideoArrayList.addAll(centerVideoArrayList);
//        if (taskDataListAdapter == null) {
        centerVideoListAdapter = new CenterVideoListAdapter(CenterVideosActivity.this, this.centerVideoArrayList);
        loadMoreListView.setAdapter(centerVideoListAdapter);
//        } else {
        centerVideoListAdapter.notifyDataSetChanged();
//        }
    }
}
