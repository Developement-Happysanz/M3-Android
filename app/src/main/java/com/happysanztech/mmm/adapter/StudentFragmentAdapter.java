package com.happysanztech.mmm.adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.happysanztech.mmm.fragments.AllProspectsFragment;
import com.happysanztech.mmm.fragments.ConfirmedProspectsFragment;
import com.happysanztech.mmm.fragments.RejectedProspectsFragment;

public class StudentFragmentAdapter extends FragmentStatePagerAdapter {

    public StudentFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AllProspectsFragment();
            case 1:
                return new ConfirmedProspectsFragment();
            case 2:
                return new RejectedProspectsFragment();
        }
        return null;
    }


    @Override
    public int getCount() {
        return 3;
    }
}
