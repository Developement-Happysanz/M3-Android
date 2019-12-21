package com.happysanztech.mmm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.happysanztech.mmm.R;
import com.happysanztech.mmm.activity.AddTaskActivity;
import com.happysanztech.mmm.activity.UpdateTaskActivity;
import com.happysanztech.mmm.adapter.TaskDataListAdapter;
import com.happysanztech.mmm.bean.support.TaskData;
import com.happysanztech.mmm.bean.support.TaskDataList;
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

import static android.app.Activity.RESULT_OK;
import static android.util.Log.d;


/**
 * Created by Admin on 03-01-2018.
 */

public class TaskActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = "TaskActivity";
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    ListView loadMoreListView;
    TaskDataListAdapter taskDataListAdapter;
    ArrayList<TaskData> taskDataArrayList;
    protected boolean isLoadingForFirstTime = true;
    Handler mHandler = new Handler();
    int pageNumber = 0, totalCount = 0;
    private FloatingActionButton fabAddTask;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tasks);
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        loadMoreListView = findViewById(R.id.listView_task);
        loadMoreListView.setOnItemClickListener(this);
        taskDataArrayList = new ArrayList<>();
        fabAddTask = findViewById(R.id.fab_add_task);
        fabAddTask.setOnClickListener(this);
        loadTask();
    }

    private void loadTask() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(MobilizerConstants.KEY_USER_ID, PreferenceStorage.getUserId(this));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = MobilizerConstants.BUILD_URL + MobilizerConstants.TASK_LIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onClick(View v) {
        if (v == fabAddTask) {

            startPersonDetailsActivity(0);
        }
    }

    public void startPersonDetailsActivity(long id) {
        Intent intent = new Intent(this, AddTaskActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (taskDataArrayList != null) {
                taskDataArrayList.clear();
                taskDataListAdapter = new TaskDataListAdapter(this, this.taskDataArrayList);
                loadMoreListView.setAdapter(taskDataListAdapter);
                loadTask();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onEvent list item click" + position);
        TaskData taskData = null;
        if ((taskDataListAdapter != null) && (taskDataListAdapter.ismSearching())) {
            Log.d(TAG, "while searching");
            int actualindex = taskDataListAdapter.getActualEventPos(position);
            Log.d(TAG, "actual index" + actualindex);
            taskData = taskDataArrayList.get(actualindex);
        } else {
            taskData = taskDataArrayList.get(position);
        }
        Intent intent = new Intent(this, UpdateTaskActivity.class);
        intent.putExtra("eventObj", taskData);
        startActivity(intent);
        finish();
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
    public void onResponse(final JSONObject response) {
        progressDialogHelper.hideProgressDialog();

        if (validateSignInResponse(response)) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
//                    progressDialogHelper.hideProgressDialog();

                    Gson gson = new Gson();
                    TaskDataList taskDataList = gson.fromJson(response.toString(), TaskDataList.class);
                    if (taskDataList.getTaskData() != null && taskDataList.getTaskData().size() > 0) {
                        totalCount = taskDataList.getCount();
                        isLoadingForFirstTime = false;
                        updateListAdapter(taskDataList.getTaskData());
                    }
                }
            });
        }
    }

    @Override
    public void onError(final String error) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                progressDialogHelper.hideProgressDialog();
                AlertDialogHelper.showSimpleAlertDialog(getApplicationContext(), error);
            }
        });
    }

    protected void updateListAdapter(ArrayList<TaskData> taskDataArrayList) {
        this.taskDataArrayList.addAll(taskDataArrayList);
//        if (taskDataListAdapter == null) {
        taskDataListAdapter = new TaskDataListAdapter(this, this.taskDataArrayList);
        loadMoreListView.setAdapter(taskDataListAdapter);
//        } else {
        taskDataListAdapter.notifyDataSetChanged();
//        }
    }
}
