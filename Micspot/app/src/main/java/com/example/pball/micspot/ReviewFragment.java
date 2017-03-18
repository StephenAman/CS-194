package com.example.pball.micspot;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.facebook.GraphResponse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import org.json.JSONObject;

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
        public ReviewAdapter(Context context) {
            super(context, 0);
        }

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
            ImageView imView = (ImageView) convertView.findViewById(R.id.review_image);
            tvReviewName.setText(review.userName);
            tvReviewText.setText(review.reviewText);
            SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-YYYY");
            tvReviewDate.setText(formatter.format(review.time));
            GetProfilePicture(review.userId, imView);
            return convertView;
        }

        public void GetProfilePicture(String userId, final ImageView imView) {
            Bundle params = new Bundle();
            params.putString("fields", "picture.type(large)");

            // Request profile picture URL from Facebook Graph.
            new GraphRequest(AccessToken.getCurrentAccessToken(), userId, params, HttpMethod.GET,
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            if (response != null) {
                                try {
                                    JSONObject data = response.getJSONObject();
                                    if (data.has("picture")) {
                                        // Download picture asynchronously.
                                        new AsyncLoadProfilePicture(
                                                data.getJSONObject("picture").getJSONObject("data").getString("url"),
                                                imView
                                        ).execute();
                                    }
                                } catch (Exception e) {
                                    // Test users will end up here since they cannot request
                                    // prof pics from the public graph, unfortunately.
                                }
                            }
                        }
                    }).executeAsync();
        }
    }

    /**
     * Download the actual profile picture on a separate thread.
     */
    public class AsyncLoadProfilePicture extends AsyncTask<String, Void, Bitmap> {
        String urlString;
        ImageView imView;

        public AsyncLoadProfilePicture(String url, ImageView imView) {
            this.urlString = url;
            this.imView = imView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap profilePic = null;
            try {
                URL url = new URL(urlString);
                try {
                    profilePic = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    return profilePic;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch(MalformedURLException e) {
                e.printStackTrace();
            }
            return profilePic;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                imView.setImageBitmap(bitmap);
            }
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
