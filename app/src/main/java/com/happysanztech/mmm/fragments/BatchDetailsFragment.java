package com.happysanztech.mmm.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.happysanztech.mmm.R;


/**
 * Created by Admin on 03-01-2018.
 */

public class BatchDetailsFragment extends Fragment {

    public BatchDetailsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_batch_details, container, false);
        return rootView;
    }
}
