package com.example.pball.micspot;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class CreateReviewFragment extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks. */
    public interface CreateReviewListener {
        public void TryCreateReview(String text);
    }

    // Use this instance of the interface to deliver action events
    CreateReviewListener mListener = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_create_review, null);
        final EditText reviewText = (EditText) view.findViewById(R.id.new_review_content);

        builder.setTitle("Create new review");
        builder.setView(view)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (mListener != null) {
                            mListener.TryCreateReview(reviewText.getText().toString());
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        CreateReviewFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    public void setListener(CreateReviewListener listener) {
        mListener = listener;
    }
}
