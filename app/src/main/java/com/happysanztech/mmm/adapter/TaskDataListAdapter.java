package com.happysanztech.mmm.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.happysanztech.mmm.R;
import com.happysanztech.mmm.bean.support.TaskData;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Admin on 09-01-2018.
 */

public class TaskDataListAdapter extends BaseAdapter {

    //    private final Transformation transformation;
    private Context context;
    private ArrayList<TaskData> taskData;
    private boolean mSearching = false;
    private boolean mAnimateSearch = false;
    private ArrayList<Integer> mValidSearchIndices = new ArrayList<Integer>();


    public TaskDataListAdapter(Context context, ArrayList<TaskData> taskData) {
        this.context = context;
        this.taskData = taskData;
//        Collections.reverse(taskData);
        mSearching = false;
    }

    @Override
    public int getCount() {
        if (mSearching) {
            if (!mAnimateSearch) {
                mAnimateSearch = true;
            }
            return mValidSearchIndices.size();

        } else {
            return taskData.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (mSearching) {
            return taskData.get(mValidSearchIndices.get(position));
        } else {
            return taskData.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.task_list_item, parent, false);

            holder = new ViewHolder();
            holder.txtTaskId = (TextView) convertView.findViewById(R.id.text_task_id);
            holder.txtTaskTitle = (TextView) convertView.findViewById(R.id.text_task_title);
            holder.txtTaskDescription = (TextView) convertView.findViewById(R.id.text_task_description);
            holder.txtTaskDate = (TextView) convertView.findViewById(R.id.task_date);
            holder.txtTaskAssignedTo = (TextView) convertView.findViewById(R.id.text_task_assigned_to);
            holder.txtTaskAssignedBy = (TextView) convertView.findViewById(R.id.text_task_assigned_by);
            holder.txtTaskStatus = (TextView) convertView.findViewById(R.id.task_status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (mSearching) {
            position = mValidSearchIndices.get(position);
        } else {
            Log.d("Event List Adapter", "getview pos called" + position);
        }
        holder.txtTaskId.setText(taskData.get(position).getId());
        holder.txtTaskTitle.setText(taskData.get(position).getTaskTitle());
        holder.txtTaskDescription.setText(taskData.get(position).getTaskDescription());
        holder.txtTaskDescription.setVisibility(View.GONE);
        holder.txtTaskAssignedTo.setText("Assigned To: "+taskData.get(position).getAssigned_to());
        holder.txtTaskAssignedBy.setText("Assigned By: "+taskData.get(position).getAssigned_from());
        holder.txtTaskStatus.setText(taskData.get(position).getStatus());
        String start = taskData.get(position).getTaskDate();
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = formatter.parse(start);
            SimpleDateFormat sent_date = new SimpleDateFormat("dd-MM-yyyy");
            String sent_date_name = sent_date.format(date.getTime());
            if (start != null) {
                holder.txtTaskDate.setText(sent_date_name);
            } else {
                holder.txtTaskDate.setText("N/A");
            }
        } catch (final ParseException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    public void startSearch(String eventName) {
        mSearching = true;
        mAnimateSearch = false;
        Log.d("EventListAdapter", "serach for event" + eventName);
        mValidSearchIndices.clear();
        for (int i = 0; i < taskData.size(); i++) {
            String classStudent = taskData.get(i).getTaskTitle();
            if ((classStudent != null) && !(classStudent.isEmpty())) {
                if (classStudent.toLowerCase().contains(eventName.toLowerCase())) {
                    mValidSearchIndices.add(i);
                }
            }
        }
        Log.d("Event List Adapter", "notify" + mValidSearchIndices.size());
    }

    public void exitSearch() {
        mSearching = false;
        mValidSearchIndices.clear();
        mAnimateSearch = false;
    }

    public void clearSearchFlag() {
        mSearching = false;
    }

    public class ViewHolder {
        public TextView txtTaskId, txtTaskTitle, txtTaskDescription, txtTaskDate, txtTaskAssignedTo, txtTaskAssignedBy, txtTaskStatus;
    }

    public boolean ismSearching() {
        return mSearching;
    }

    public int getActualEventPos(int selectedSearchpos) {
        if (selectedSearchpos < mValidSearchIndices.size()) {
            return mValidSearchIndices.get(selectedSearchpos);
        } else {
            return 0;
        }
    }
}
