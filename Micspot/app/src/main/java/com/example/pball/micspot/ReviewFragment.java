package com.example.pball.micspot;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewFragment extends Fragment implements Callback<List<MicSpotService.Review>>,
        CreateReviewFragment.CreateReviewListener
{
    public static final String PREF_FILE = "SharedPrefs";
    private MicSpotService service;
    private ListView reviewListView;
    private String micId;
    private String jwt;
    private PopupWindow window;
    private FrameLayout frame;
    private ReviewAdapter listAdapter;

    static ReviewFragment newInstance(String micId) {
        ReviewFragment fragment = new ReviewFragment();

        Bundle args = new Bundle();
        args.putString("micId", micId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = new MicSpotService();
        listAdapter = new ReviewAdapter(getActivity());
        jwt = getActivity().getSharedPreferences(
                PREF_FILE, Context.MODE_PRIVATE
        ).getString("jwt", null);

        micId = getArguments().getString("micId");
        TryRefresh();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_review, container, false);
        reviewListView = (ListView) (rootView.findViewById (R.id.review_list));
        reviewListView.setAdapter(listAdapter);

        // Setup listener to open the create review dialog when touched.
        FloatingActionButton reviewFab =
                (FloatingActionButton) rootView.findViewById(R.id.new_review_fab);
        reviewFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                CreateReviewFragment createReview = new CreateReviewFragment();
                createReview.setListener(ReviewFragment.this);
                createReview.show(getFragmentManager(), "createReview");
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
            TextView tvReviewName = (TextView) convertView.findViewById(R.id.review_header);
            TextView tvReviewText = (TextView) convertView.findViewById(R.id.review_text);
            TextView tvReviewDate = (TextView) convertView.findViewById(R.id.review_date);
            ImageView imReviewImage = (ImageView) convertView.findViewById(R.id.review_image);

            tvReviewName.setText(review.userName);
            tvReviewText.setText(review.reviewText);
            SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-YYYY");
            tvReviewDate.setText(formatter.format(review.time));
            return convertView;
        }
    }

    public void TryCreateReview(String text) {
        MicSpotService.MicClient client = service.Create(jwt);
        MicSpotService.CreateReview review = new MicSpotService.CreateReview(
                text.toString()
        );
        Call<Void> call = client.addReview(micId, review);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call,
                                   Response<Void> response) {
                if (response.isSuccessful()) {
                    TryRefresh();
                    Toast.makeText(getContext(), "Review created", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(),
                            "You have already written a review", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call,
                                  Throwable t) {
                t.printStackTrace();
            }
        });
    }

    /**
     * Attempts to refresh the review list.
     */
    public void TryRefresh() {
        MicSpotService.MicClient client = service.Create(jwt);
        Call<List<MicSpotService.Review>> call = client.reviews(micId);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<List<MicSpotService.Review>> call,
                           Response<List<MicSpotService.Review>> response) {
        if (response.isSuccessful()) {
            listAdapter.clear();
            listAdapter.addAll(response.body());
        }
    }

    @Override
    public void onFailure(Call<List<MicSpotService.Review>> call,
                          Throwable t) {
        t.printStackTrace();
    }
}
