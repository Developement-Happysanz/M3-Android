package com.happysanztech.mmm.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.happysanztech.mmm.R;
import com.happysanztech.mmm.activity.AddCandidateActivity;
import com.happysanztech.mmm.adapter.AllProspectsListAdapter;
import com.happysanztech.mmm.bean.support.AllProspects;
import com.happysanztech.mmm.bean.support.AllProspectsList;
import com.happysanztech.mmm.helper.AlertDialogHelper;
import com.happysanztech.mmm.helper.ProgressDialogHelper;
import com.happysanztech.mmm.servicehelpers.ServiceHelper;
import com.happysanztech.mmm.serviceinterfaces.IServiceListener;
import com.happysanztech.mmm.utils.MobilizerConstants;
import com.happysanztech.mmm.utils.PreferenceStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.util.Log.d;

public class RejectedProspectsFragment extends Fragment implements AdapterView.OnItemClickListener, IServiceListener {
    private View rootView;
    private static final String TAG = AllProspectsFragment.class.getName();
    protected ListView loadMoreListView;
    protected AllProspectsListAdapter upcomingHolidayListAdapter;
    protected ArrayList<AllProspects> upcomingHolidayArrayList;
    protected ProgressDialogHelper progressDialogHelper;
    private ServiceHelper serviceHelper;
    protected boolean isLoadingForFirstTime = true;
    int pageNumber = 0, totalCount = 0;
    Boolean msgErr = false;

    public RejectedProspectsFragment() {
    }

    private boolean _hasLoadedOnce= false; // your boolean field

//    @Override
//    public void setUserVisibleHint(boolean isFragmentVisible_) {
//        super.setUserVisibleHint(true);
//
//
//        if (this.isVisible()) {
//            // we check that the fragment is becoming visible
//            if (!isFragmentVisible_ && !_hasLoadedOnce) {
//                //run your async task here since the user has just focused on your fragment
//                new HttpAsyncTask().execute("");
//                _hasLoadedOnce = true;
//            }
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_all_prospects, container, false);
        loadMoreListView = rootView.findViewById(R.id.all_prospects_list);
        initializeEventHelpers();
        getHolsList();
        return rootView;
    }

//    private class HttpAsyncTask extends AsyncTask<String, Void, Void> {
//        @Override
//        protected Void doInBackground(String... urls) {
//
//            JSONObject jsonObject = new JSONObject();
//            String id = "";
//            if (PreferenceStorage.getUserId(getActivity()).equalsIgnoreCase("1")) {
//                id = PreferenceStorage.getPIAProfileId(getActivity());
//            } else {
//                id = PreferenceStorage.getUserId(getActivity());
//            }
//            try {
//                jsonObject.put(MobilizerConstants.KEY_USER_ID, id);
//                jsonObject.put(MobilizerConstants.PARAMS_TASK_STATUS, "Rejected");
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
//            String url = MobilizerConstants.BUILD_URL + MobilizerConstants.STUDENTS_LIST_STATUS;
//            serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
//
//            return null;
//        }
//
//        // onPostExecute displays the results of the AsyncTask.
//        @Override
//        protected void onPostExecute(Void result) {
//            progressDialogHelper.cancelProgressDialog();
//        }
//    }

    public void getHolsList() {
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = PreferenceStorage.getUserId(getActivity());

        try {
            jsonObject.put(MobilizerConstants.KEY_USER_ID, id);
            jsonObject.put(MobilizerConstants.PARAMS_TASK_STATUS, "Rejected");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = MobilizerConstants.BUILD_URL + MobilizerConstants.STUDENTS_LIST_STATUS;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    protected void initializeEventHelpers() {
        serviceHelper = new ServiceHelper(getActivity());
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(getActivity());
        upcomingHolidayArrayList = new ArrayList<>();
    }

    private void LoadListView(JSONObject response) {
        progressDialogHelper.hideProgressDialog();
//        loadMoreListView.onLoadMoreComplete();
        Gson gson = new Gson();
        AllProspectsList upcomingHolidayList = gson.fromJson(response.toString(), AllProspectsList.class);
        if (upcomingHolidayList != null) {
            Log.d(TAG, "fetched all event list count" + upcomingHolidayList.getCount());
        }
//        updateListAdapter(eventsList.getEvents());
        int totalNearbyCount = 0;
        if (upcomingHolidayList.getAllProspects() != null && upcomingHolidayList.getAllProspects().size() > 0) {


            isLoadingForFirstTime = false;
            totalCount = upcomingHolidayList.getCount();
            updateListAdapter(upcomingHolidayList.getAllProspects());
        }
    }

    protected void updateListAdapter(ArrayList<AllProspects> upcomingHolidayArrayList) {
        if (msgErr) {
            AlertDialogHelper.showSimpleAlertDialog(getActivity(), "No Rejected Prospect");
        }
        this.upcomingHolidayArrayList.addAll(upcomingHolidayArrayList);
       /* if (mNoEventsFound != null)
            mNoEventsFound.setVisibility(View.GONE);*/

        if (upcomingHolidayListAdapter == null) {
            upcomingHolidayListAdapter = new AllProspectsListAdapter(getActivity(), this.upcomingHolidayArrayList);
            loadMoreListView.setAdapter(upcomingHolidayListAdapter);
        } else {
            upcomingHolidayListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        d(TAG, "onEvent list item click" + position);
        AllProspects centers = null;
        if ((upcomingHolidayListAdapter != null) && (upcomingHolidayListAdapter.ismSearching())) {
            d(TAG, "while searching");
            int actualindex = upcomingHolidayListAdapter.getActualEventPos(position);
            d(TAG, "actual index" + actualindex);
            centers = upcomingHolidayArrayList.get(actualindex);
        } else {
            centers = upcomingHolidayArrayList.get(position);
        }

        if (!PreferenceStorage.getUserId(getActivity()).equalsIgnoreCase("1")) {
            Intent intent = new Intent(getActivity(), AddCandidateActivity.class);
            intent.putExtra("pros", centers);
            startActivity(intent);
        } else {

        }

    }

    private boolean validateSignInResponse(JSONObject response) {
        boolean signInsuccess = false;
        if ((response != null)) {
            try {
                String status = response.getString("status");
                String msg = response.getString(MobilizerConstants.PARAM_MESSAGE);
                Log.d(TAG, "status val" + status + "msg" + msg);

                if ((status != null)) {
                    if (((status.equalsIgnoreCase("activationError")) || (status.equalsIgnoreCase("alreadyRegistered")) ||
                            (status.equalsIgnoreCase("notRegistered")) || (status.equalsIgnoreCase("error")))) {
                        signInsuccess = false;
                        Log.d(TAG, "Show error dialog");
                        if (msg.equalsIgnoreCase("Students Not Found")) {
                            msgErr = true;
                        } else {
                            AlertDialogHelper.showSimpleAlertDialog(getActivity(), msg);
                        }
                    } else {
                        signInsuccess = true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return signInsuccess;
    }

    @Override
    public void onResponse(final JSONObject response) {
        progressDialogHelper.hideProgressDialog();

        if (validateSignInResponse(response)) {
            LoadListView(response);
        }
    }

//    @Override
//    public void onResponse(JSONObject response) {
//        progressDialogHelper.hideProgressDialog();
//
//        if (validateSignInResponse(response)) {
//            LoadListView(response);
//        }
//    }

    @Override
    public void onError(String error) {

    }
}