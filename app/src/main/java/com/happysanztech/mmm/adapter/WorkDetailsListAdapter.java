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
import com.happysanztech.mmm.bean.support.WorkDetails;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WorkDetailsListAdapter extends BaseAdapter {

    //    private final Transformation transformation;
    private Context context;
    private ArrayList<WorkDetails> workDetails;
    private boolean mSearching = false;
    private boolean mAnimateSearch = false;
    private ArrayList<Integer> mValidSearchIndices = new ArrayList<Integer>();


    public WorkDetailsListAdapter(Context context, ArrayList<WorkDetails> WorkDetails) {
        this.context = context;
        this.workDetails = WorkDetails;
//        Collections.reverse(WorkDetails);
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
            return workDetails.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (mSearching) {
            return workDetails.get(mValidSearchIndices.get(position));
        } else {
            return workDetails.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WorkDetailsListAdapter.ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.work_detail_list_item, parent, false);

            holder = new WorkDetailsListAdapter.ViewHolder();
            holder.txtDate = (TextView) convertView.findViewById(R.id.work_date);
            holder.txtWorkType = (TextView) convertView.findViewById(R.id.work_type);

            convertView.setTag(holder);
        } else {
            holder = (WorkDetailsListAdapter.ViewHolder) convertView.getTag();
        }

        if (mSearching) {
            position = mValidSearchIndices.get(position);
        } else {
            Log.d("Event List Adapter", "getview pos called" + position);
        }
//        holder.txtDate.setText(WorkDetails.get(position).getattendance_date());
        holder.txtWorkType.setText(workDetails.get(position).getwork_type());

        String start = workDetails.get(position).getattendance_date();
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = formatter.parse(start);
            String a = android.text.format.DateFormat.format("dd", date).toString();
            String b = android.text.format.DateFormat.format("EEE", date).toString();

            if (start != null) {
                holder.txtDate.setText(a+"\n"+b);
            } else {
                holder.txtDate.setText("N/A");
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
        for (int i = 0; i < workDetails.size(); i++) {
            String classStudent = workDetails.get(i).getid();
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
        public TextView txtDate, txtWorkType;
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