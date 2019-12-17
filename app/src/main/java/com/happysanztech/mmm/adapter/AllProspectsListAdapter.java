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
import com.happysanztech.mmm.bean.support.AllProspects;

import java.util.ArrayList;
import java.util.Collections;

public class AllProspectsListAdapter extends BaseAdapter {

    //    private final Transformation transformation;
    private Context context;
    private ArrayList<AllProspects> upcomingHoliday;
    private boolean mSearching = false;
    private boolean mAnimateSearch = false;
    private ArrayList<Integer> mValidSearchIndices = new ArrayList<Integer>();

    public AllProspectsListAdapter(Context context, ArrayList<AllProspects> upcomingHoliday) {
        this.context = context;
        this.upcomingHoliday = upcomingHoliday;
        Collections.reverse(upcomingHoliday);
//        transformation = new RoundedTransformationBuilder()
//                .cornerRadiusDp(0)
//                .oval(false)
//                .build();
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
            return upcomingHoliday.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (mSearching) {
            return upcomingHoliday.get(mValidSearchIndices.get(position));
        } else {
            return upcomingHoliday.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AllProspectsListAdapter.ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.all_prospects_list_item, parent, false);

            holder = new AllProspectsListAdapter.ViewHolder();
            holder.txtName = (TextView) convertView.findViewById(R.id.prospect_name);
            holder.txtStatus = (TextView) convertView.findViewById(R.id.prospect_status);
            convertView.setTag(holder);
        } else {
            holder = (AllProspectsListAdapter.ViewHolder) convertView.getTag();
        }

        if (mSearching) {
            position = mValidSearchIndices.get(position);
        } else {
            Log.d("Event List Adapter", "getview pos called" + position);
        }

        holder.txtName.setText(upcomingHoliday.get(position).getName());
        holder.txtStatus.setText(upcomingHoliday.get(position).getStatus());

        return convertView;
    }

    public void startSearch(String eventName) {
        mSearching = true;
        mAnimateSearch = false;
        Log.d("EventListAdapter", "serach for event" + eventName);
        mValidSearchIndices.clear();
        for (int i = 0; i < upcomingHoliday.size(); i++) {
            String holidayTitle = upcomingHoliday.get(i).getName();
            if ((holidayTitle != null) && !(holidayTitle.isEmpty())) {
                if (holidayTitle.toLowerCase().contains(eventName.toLowerCase())) {
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
        public TextView txtName, txtStatus;
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