package com.example.badgerconnect;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class BioDialogFragment extends DialogFragment {
    EditText editText;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Inflate the custom layout for the dialog
        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.bio_dialog_fragment, null);

        // Get the EditText view from the layout
        editText = view.findViewById(R.id.bioField);

        // Build the AlertDialog with the custom layout and buttons
        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setView(view)
                .setTitle("Enter a short bio:")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String inputText = editText.getText().toString();
                        ((ProfileCreationGeneralInfoActivity) getActivity())
                                .onTextEntered(inputText);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((ProfileCreationGeneralInfoActivity) getActivity())
                                .onTextEntered(null);
                    }
                })
                .create();

        return alertDialog;
    }
}
