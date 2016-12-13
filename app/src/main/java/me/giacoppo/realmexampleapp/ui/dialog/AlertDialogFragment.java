package me.giacoppo.realmexampleapp.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.giacoppo.realmexampleapp.R;

/**
 * Created by Peppe on 18/10/2016.
 */

public class AlertDialogFragment extends DialogFragment {
    public static String TAG = AlertDialogFragment.class.getSimpleName();

    private static final String TITLE = "TITLE";
    private static final String MESSAGE = "MESSAGE";

    private String title, message, positiveLabel, negativeLabel;
    private boolean hideNegativeButton = false;
    private DialogInterface.OnClickListener positiveListener, negativeListener;
    private boolean cancelable = true;


    public AlertDialogFragment isCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    public AlertDialogFragment setPositiveButton(DialogInterface.OnClickListener listener) {
        this.positiveListener = listener;
        return this;
    }

    public AlertDialogFragment setNegativeButton(String label, DialogInterface.OnClickListener listener) {
        this.negativeLabel = label;
        this.negativeListener = listener;
        return this;
    }

    public AlertDialogFragment setNegativeButton(DialogInterface.OnClickListener listener) {
        this.negativeListener = listener;
        return this;
    }

    public AlertDialogFragment setPositiveButton(String label, DialogInterface.OnClickListener listener) {
        this.positiveLabel = label;
        this.positiveListener = listener;
        return this;
    }

    public AlertDialogFragment hideNegativeButton() {
        hideNegativeButton = true;
        return this;
    }

    public static AlertDialogFragment newInstance(String message) {
        AlertDialogFragment f = new AlertDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(MESSAGE, message);
        f.setArguments(bundle);
        return f;
    }

    public static AlertDialogFragment newInstance(String title, String message) {
        AlertDialogFragment f = new AlertDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        bundle.putString(MESSAGE, message);
        f.setArguments(bundle);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getArguments().getString(TITLE);
        message = getArguments().getString(MESSAGE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.Theme_Dialog_Light));
        if (!TextUtils.isEmpty(title))
            builder.setTitle(title);

        builder.setMessage(message);

        if (!TextUtils.isEmpty(positiveLabel)) {
            builder.setPositiveButton(positiveLabel, positiveListener);
        } else {
            builder.setPositiveButton(R.string.ok, positiveListener);
        }

        if (negativeListener != null) {
            if (!TextUtils.isEmpty(negativeLabel)) {
                builder.setNegativeButton(negativeLabel, negativeListener);
            } else {
                builder.setNegativeButton(R.string.cancel,negativeListener);
            }
        } else if (!hideNegativeButton)
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dismiss();
                }
            });

        builder.setCancelable(cancelable);
        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setCancelable(cancelable);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
