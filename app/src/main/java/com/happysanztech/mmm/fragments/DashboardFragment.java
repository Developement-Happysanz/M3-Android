package com.happysanztech.mmm.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.happysanztech.mmm.R;


/**
 * Created by Admin on 03-01-2018.
 */

public class DashboardFragment extends Fragment implements View.OnClickListener {

    private LinearLayout layAddCandidate;
    private LinearLayout layCenterInformation;
    private LinearLayout layTrades;
    private LinearLayout layTasks;
    View rootView;

    public DashboardFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        layAddCandidate = rootView.findViewById(R.id.add_candidates);
        layAddCandidate.setOnClickListener(this);

        layCenterInformation = rootView.findViewById(R.id.center_information);
        layCenterInformation.setOnClickListener(this);

        layTrades = rootView.findViewById(R.id.trades);
        layTrades.setOnClickListener(this);

        layTasks = rootView.findViewById(R.id.task);
        layTasks.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {

        if (v == layAddCandidate) {
            Fragment fragment = new AddCandidateFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.flContent, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
        if (v == layCenterInformation) {
            Fragment fragment = new CenterInfoFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.flContent, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
        if (v == layTrades) {
            Fragment fragment = new TradeFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.flContent, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
        if (v == layTasks) {
            Fragment fragment = new TaskFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.flContent, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
}
