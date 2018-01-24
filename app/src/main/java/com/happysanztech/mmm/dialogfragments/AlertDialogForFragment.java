package com.happysanztech.mmm.dialogfragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.happysanztech.mmm.R;
import com.happysanztech.mmm.interfaces.DialogClickListener;
import com.happysanztech.mmm.utils.MobilizerConstants;


/**
 * Created by Admin on 17-10-2017.
 */

public class AlertDialogForFragment extends DialogFragment {

    private int tag;
    DialogClickListener dialogActions;

    public static AlertDialogForFragment newInstance(int title, String message) {
        AlertDialogForFragment frag = new AlertDialogForFragment();
        Bundle args = new Bundle();
        args.putInt(MobilizerConstants.ALERT_DIALOG_TITLE, title);
        args.putString(MobilizerConstants.ALERT_DIALOG_MESSAGE, message);
        frag.setArguments(args);
        return frag;
    }

    public static AlertDialogForFragment newInstance(int title, String message, int tag) {
        AlertDialogForFragment frag = new AlertDialogForFragment();
        Bundle args = new Bundle();
        args.putInt(MobilizerConstants.ALERT_DIALOG_TITLE, title);
        args.putString(MobilizerConstants.ALERT_DIALOG_MESSAGE, message);
        args.putInt(MobilizerConstants.ALERT_DIALOG_TAG, tag);
        frag.setArguments(args);
        return frag;
    }

    public void setDialogListener(DialogClickListener dialogActions) {
        this.dialogActions = dialogActions;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();
        String message = args.getString(MobilizerConstants.ALERT_DIALOG_MESSAGE, "");
        int title = args.getInt(MobilizerConstants.ALERT_DIALOG_TITLE);
        tag = args.getInt(MobilizerConstants.ALERT_DIALOG_TAG, 0);

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.alert_button_ok, mListener).create();
    }

    DialogInterface.OnClickListener mListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {

            if (which == -1) {
                dialog.cancel();
                dialogActions.onAlertPositiveClicked(tag);

            } else {
                dialog.cancel();
                dialogActions.onAlertNegativeClicked(tag);
            }
        }

    };
}
