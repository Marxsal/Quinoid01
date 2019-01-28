package com.example.my32ndapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class TwDialogFragment extends DialogFragment {
    static final String EXTRA_POSITION = "twFileIndex";
    static final String LOG_TAG = "32XND";

    TwFile mTwFile;
    EditText mEditDisplayTitle ;
    CheckBox mCheckBoxDialogBrowse;
    CheckBox mCheckBoxDialogDelete;

    TwDialogFragmentListener mListener ;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        int twFileIndex = (int) getArguments().getInt(EXTRA_POSITION);
        mTwFile = TwManager.get(getActivity()).getTwFile(twFileIndex);
        String display = mTwFile.getDisplayName();
        boolean browseAble = mTwFile.isBrowsable();

        View v = getActivity().getLayoutInflater().inflate(R.layout.tw_fragment_dialog, null);

        mEditDisplayTitle = v.findViewById(R.id.editDescription);
        mEditDisplayTitle.setText(display,TextView.BufferType.EDITABLE);

        mCheckBoxDialogBrowse = v.findViewById(R.id.checkBoxDialogBrowse);
        mCheckBoxDialogBrowse.setChecked(browseAble);
        mCheckBoxDialogDelete = v.findViewById(R.id.checkBoxDialogDelete);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setPositiveButton(R.string.dialog_confirm_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String display = mEditDisplayTitle.getText().toString();
                                Log.d(LOG_TAG, "TDF...onClick: I see display title: " + display);
                                if (display != null ) {
                                    mTwFile.setDisplayName(display);
                                    Log.d(LOG_TAG, "TDF...onClick: I just set display title: " + display);

                                }
                                mTwFile.setIsBrowsable(mCheckBoxDialogBrowse.isChecked());
                                if(mCheckBoxDialogDelete.isChecked()) TwManager.get(getActivity()).deleteTwFile(mTwFile);
                                mListener.onDialogPositiveClick(TwDialogFragment.this);
                            }
                        }
                )

                .setNegativeButton(R.string.dialog_cancel_button, null)
                .create();
    }

    public static TwDialogFragment newInstance(int twFileIndex) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_POSITION,twFileIndex);
        TwDialogFragment fragment = new TwDialogFragment() ;
        fragment.setArguments(args);
        return fragment ;
    }

    public interface TwDialogFragmentListener {
        public void onDialogPositiveClick(DialogFragment dialog) ;
        public void onDialogNegativeClick(DialogFragment dialog) ;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (TwDialogFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement TwDialogFragmentListener");
        }
    }
}
