package com.heavyconnect.heavyconnect.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.heavyconnect.heavyconnect.R;

/*
* This class implements a dialogFragment, that edits attributes for each pipe line
*
* */

public class EditLineAttributesDialogFragment extends DialogFragment implements TextView.OnEditorActionListener {

    private EditText mEditRow;;
    private EditText mEditLength;
    private EditText mEditDepth;
    private String mPipeCoordinates;
    private String coordinate;
    private static final String COORD_KEY = "coordi";
    private static final String PIPE_COORDINATES = "pipe_coordinates";
    Context mContext;




    public EditLineAttributesDialogFragment(){
        mContext = getActivity();

        Log.d("EditLIneAttributes", "the Fragment was created");

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //retrieves information from bundle arguments
        coordinate = getArguments().getString(COORD_KEY);
        mPipeCoordinates = getArguments().getString(PIPE_COORDINATES);
        Log.d("EditLIneAttributes", "" + mPipeCoordinates);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        //creates a builder for an dialog fragment
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //inflates the layout
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.fragment_edit_dialog_fragment, null);

        setDefaultEditText(view);

        builder.setTitle("Edit PipeLines");

        //Positive Button will save the attributes length, rows,
        builder.setPositiveButton("Enter", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                String row = mEditRow.getText().toString();
                Log.d("editLineAttriRow", row);

                String length = mEditLength.getText().toString();
                Log.d("editLineAttrLenght", length);

                String depth = mEditDepth.getText().toString();
                Log.d("editLineAttriDEPTH", depth);

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setIcon(R.mipmap.ic_launcher);

        builder.setView(view);

        Dialog dialog = builder.create();

        return dialog;
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

        return false;
    }

    public static EditLineAttributesDialogFragment getInstance(String coordinate, String pipeCoordinates){

        //instantiates the dialogFragment
        EditLineAttributesDialogFragment dialogFragment = new EditLineAttributesDialogFragment();

        //creates a bundle that sends the coordinate for the firstmarker
        Bundle bundle = new Bundle();

        bundle.putString(COORD_KEY, coordinate);
        bundle.putString(PIPE_COORDINATES, pipeCoordinates);

        dialogFragment.setArguments(bundle);

        return dialogFragment;
    }

    public void setDefaultEditText(View view){

        mEditRow = (EditText) view.findViewById(R.id.edit_row);

        mEditLength = (EditText) view.findViewById(R.id.edit_length);

        mEditDepth = (EditText) view.findViewById(R.id.edit_depth);

    }

    public void setPreviewsEditText(View view, String row, String depth, String length){

        mEditRow = (EditText) view.findViewById(R.id.edit_row);
        mEditRow.setText(row, TextView.BufferType.EDITABLE);

        mEditLength = (EditText) view.findViewById(R.id.edit_length);
        mEditLength.setText(length, TextView.BufferType.EDITABLE);

        mEditDepth = (EditText) view.findViewById(R.id.edit_depth);
        mEditDepth.setText(depth, TextView.BufferType.EDITABLE);

    }
}
