package com.example.pball.micspot;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.vision.Frame;

import java.io.IOException;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.support.design.R.styleable.FloatingActionButton;
import static java.security.AccessController.getContext;


public class ReviewFragment extends Fragment {
    public static final String PREF_FILE = "SharedPrefs";
    private MicSpotService service;
    private MicSpotService.Mic mic;
    private ListView reviewListView;
    private String micId;
    private String userId;
    private String jwt;
    private PopupWindow window;
    private FrameLayout frame;
    private ReviewAdapter listAdapter;

    //private OnFragmentInteractionListener mListener;

    static ReviewFragment newInstance(String micId) {
        ReviewFragment fragment = new ReviewFragment();

        Bundle args = new Bundle();
        args.putString("micId", micId);
        fragment.setArguments(args);
        return fragment;
    }

    public ReviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = new MicSpotService();
        userId = getActivity().getSharedPreferences(
                PREF_FILE, Context.MODE_PRIVATE
        ).getString("userId", null);
        jwt = getActivity().getSharedPreferences(
                PREF_FILE, Context.MODE_PRIVATE
        ).getString("jwt", null);

        micId = getArguments().getString("micId");
        /*
        try {
            service.getMic(micId, this, jwt);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        listAdapter = new ReviewAdapter(getActivity());

        MicSpotService.Review r = new MicSpotService.Review(null, "Peter Ballmer", "This mic is awesome!", null);
        listAdapter.add(r);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_review, container, false);
        reviewListView = (ListView) (rootView.findViewById (R.id.review_list));
        frame = (FrameLayout) rootView.findViewById(R.id.big_layout);
        frame.getForeground().setAlpha(0);//not dim
        reviewListView.setAdapter(listAdapter);

        FloatingActionButton reviewFab = (FloatingActionButton) rootView.findViewById(R.id.new_review_fab);
        reviewFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.review_popup,null);
                window = new PopupWindow(
                        popupView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                ImageButton closeButton = (ImageButton) popupView.findViewById(R.id.close);

                // Set a click listener for the popup window close button
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        frame.getForeground().setAlpha(0);
                        window.dismiss();
                    }
                });
                frame.getForeground().setAlpha(220);//dim
                final EditText reviewContent = (EditText) popupView.findViewById(R.id.new_review_content);
                Button addReview = (Button) popupView.findViewById(R.id.add_button);
                addReview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MicSpotService.Review r = new MicSpotService.Review(null, "test", reviewContent.getText().toString(), null);
                        listAdapter.add(r);
                        frame.getForeground().setAlpha(0);
                        window.dismiss();
                    }

                });
                window.showAtLocation(frame, Gravity.CENTER,0,0);


            }
        });
        return rootView;
    }



    public class ReviewAdapter extends ArrayAdapter<MicSpotService.Review> {
        public ReviewAdapter (Context context) { super(context, 0); }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MicSpotService.Review review = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(
                        getContext()).inflate(R.layout.written_review, parent, false
                );
            }

            TextView tvReviewerName = (TextView) convertView.findViewById(R.id.reviewer_name);
            TextView tvReviewText = (TextView) convertView.findViewById(R.id.review_text);
            tvReviewerName.setText(review.userName);
            tvReviewText.setText(review.reviewText);
            return convertView;
        }



    }
}
