package com.heavyconnect.heavyconnect.utils;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.heavyconnect.heavyconnect.IrrigationMapActivity;
import com.heavyconnect.heavyconnect.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FieldNameDialogFragment extends DialogFragment {

    private EditText mFieldNameEdit;
    private String mFieldName;

    public FieldNameDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder nameDialog = new AlertDialog.Builder(getActivity());

        // Get the layout inflater to set the custom view on our dialog
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate custom dialog view
        View view = inflater.inflate(R.layout.field_name_dialog_fragment, null);

        mFieldNameEdit = (EditText) view.findViewById(R.id.field_name_edit_text);
        mFieldNameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mFieldName = s.toString();
                Log.d("Edit Text", s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Set custom dialog view
        nameDialog.setView(view);

        nameDialog.setTitle("Field Name");
        nameDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendResult(Activity.RESULT_OK);
            }
        });

        nameDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
           @Override
            public void onClick(DialogInterface dialog, int which) {
               dialog.cancel();
           }
        });



        return nameDialog.create();
    }

    public static FieldNameDialogFragment getInstance() {
        FieldNameDialogFragment nameDialogFragment = new FieldNameDialogFragment();

        Bundle args = new Bundle();

        nameDialogFragment.setArguments(args);

        return nameDialogFragment;
    }

    private void sendResult(int resultOk) {
        Intent intent = new Intent();
        intent.putExtra("field_name", mFieldName);

        IrrigationMapActivity.updateFieldName(mFieldName);
    }
}
