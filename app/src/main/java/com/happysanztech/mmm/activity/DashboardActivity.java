package com.happysanztech.mmm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.happysanztech.mmm.R;
import com.happysanztech.mmm.fragments.AddCandidateFragment;


/**
 * Created by Admin on 03-01-2018.
 */

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout layAddCandidate;
    private LinearLayout layCenterInformation;
    private LinearLayout layTrades;
    private LinearLayout layTasks;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_dashboard);


        layAddCandidate = findViewById(R.id.add_candidates);
        layAddCandidate.setOnClickListener(this);

        layCenterInformation = findViewById(R.id.center_information);
        layCenterInformation.setOnClickListener(this);

        layTrades = findViewById(R.id.trades);
        layTrades.setOnClickListener(this);

        layTasks = findViewById(R.id.task);
        layTasks.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if (v == layAddCandidate) {
//            Fragment fragment = new AddCandidateFragment();
//            FragmentManager fragmentManager = this.getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.flContent, fragment);
//            fragmentTransaction.addToBackStack(null);
//            fragmentTransaction.commit();
            Intent navigationIntent = new Intent(this, AddCandidateFragment.class);
            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(navigationIntent);
        }
        if (v == layCenterInformation) {
//            Fragment fragment = new CenterInfoActivity();
//            FragmentManager fragmentManager = this.getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.flContent, fragment);
//            fragmentTransaction.addToBackStack(null);
//            fragmentTransaction.commit();
            Intent navigationIntent = new Intent(this, CenterInfoActivity.class);
            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(navigationIntent);
        }
        if (v == layTrades) {
//            Fragment fragment = new TradeActivity();
//            FragmentManager fragmentManager = this.getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.flContent, fragment);
//            fragmentTransaction.addToBackStack(null);
//            fragmentTransaction.commit();
            Intent navigationIntent = new Intent(this, TradeActivity.class);
            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(navigationIntent);
        }
        if (v == layTasks) {
//            Fragment fragment = new TaskActivity();
//            FragmentManager fragmentManager = this.getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.flContent, fragment);
//            fragmentTransaction.addToBackStack(null);
//            fragmentTransaction.commit();
            Intent navigationIntent = new Intent(this, TaskActivity.class);
            navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(navigationIntent);
        }
    }
}
