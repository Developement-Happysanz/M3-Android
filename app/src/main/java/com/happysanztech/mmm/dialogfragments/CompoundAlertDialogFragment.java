package com.happysanztech.mmm.dialogfragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.happysanztech.mmm.R;
import com.happysanztech.mmm.activity.SplashScreenActivity;
import com.happysanztech.mmm.interfaces.DialogClickListener;
import com.happysanztech.mmm.utils.MobilizerConstants;


/**
 * Created by Admin on 20-10-2017.
 */

public class CompoundAlertDialogFragment extends DialogFragment {

    private int tag;
    DialogClickListener dialogActions;

    public static CompoundAlertDialogFragment newInstance(String title, String message, String posButton, String negButton, int tag) {
        CompoundAlertDialogFragment frag = new CompoundAlertDialogFragment();
        Bundle args = new Bundle();
        args.putString(MobilizerConstants.ALERT_DIALOG_TITLE, title);
        args.putString(MobilizerConstants.ALERT_DIALOG_MESSAGE, message);
        args.putString(MobilizerConstants.ALERT_DIALOG_POS_BUTTON, posButton);
        args.putString(MobilizerConstants.ALERT_DIALOG_NEG_BUTTON, negButton);
        args.putInt(MobilizerConstants.ALERT_DIALOG_TAG, tag);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            dialogActions = (DialogClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling activity must implement DialogClickListener interface");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();
        String message = args.getString(MobilizerConstants.ALERT_DIALOG_MESSAGE, "");
        String title = args.getString(MobilizerConstants.ALERT_DIALOG_TITLE);
        tag = args.getInt(MobilizerConstants.ALERT_DIALOG_TAG, 0);
        String posButton = args.getString(MobilizerConstants.ALERT_DIALOG_POS_BUTTON, getActivity().getString(R.string.alert_button_ok));
        String negButton = args.getString(MobilizerConstants.ALERT_DIALOG_NEG_BUTTON, getActivity().getString(R.string.alert_button_cancel));
        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(posButton, mListener)
                .setNegativeButton(negButton, mListener)
                .create();

    }

    DialogInterface.OnClickListener mListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {

            if (which == -1) {
                dialog.cancel();
                if (CompoundAlertDialogFragment.this.dialogActions != null)
                    CompoundAlertDialogFragment.this.dialogActions
                            .onAlertPositiveClicked(tag);

                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(getActivity());
                sharedPreferences.edit().clear().commit();


                Intent navigationIntent = new Intent(getActivity(), SplashScreenActivity.class);
                navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(navigationIntent);
                getActivity().finish();

            } else {
                dialog.cancel();
                if (CompoundAlertDialogFragment.this.dialogActions != null)
                    CompoundAlertDialogFragment.this.dialogActions
                            .onAlertNegativeClicked(tag);
            }
        }

    };
}
