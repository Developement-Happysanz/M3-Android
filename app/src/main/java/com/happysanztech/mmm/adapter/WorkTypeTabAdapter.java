package com.happysanztech.mmm.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;


import com.happysanztech.mmm.bean.support.WorkMonth;
import com.happysanztech.mmm.fragments.DynamicWorkTypeFragment;

import java.util.ArrayList;

public class WorkTypeTabAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    ArrayList<WorkMonth> WorkMonthArrayList;
    public WorkTypeTabAdapter(FragmentManager fm, int NumOfTabs, ArrayList<WorkMonth> categoryArrayList) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.WorkMonthArrayList = categoryArrayList;
    }
    @Override
    public Fragment getItem(int position) {
        return DynamicWorkTypeFragment.newInstance(position,WorkMonthArrayList);
    }
    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}