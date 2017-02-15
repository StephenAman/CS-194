package com.example.pball.micspot;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Headers;
import retrofit2.http.GET;
import retrofit2.http.Path;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Date;

public class MicSpotService {
    public static final String API_URL = "http://138.68.7.110:8080";

    private MicMap map;

    /**
     * MicSummary contains the info necessary to place map markers and populate
     * their respective pop-up windows.
     */
    public static class MicSummary {
        public final String micId;
        public final String status;
        public final float venueLat;
        public final float venueLng;
        public final String micName;
        public final String createdBy;
        public final String meetingBasis;
        public final Date startDate;
        public final Date endDate;

        public MicSummary(String micId, String status, int venueLat, int venueLng, String micName,
                          String createdBy, String meetingBasis, Date start, Date end) {
            this.micId = micId;
            this.status = status;
            this.venueLat = venueLat;
            this.venueLng = venueLng;
            this.micName = micName;
            this.createdBy = createdBy;
            this.meetingBasis = meetingBasis;
            this.startDate = start;
            this.endDate = end;
        }
    }

    /**
     * Mic contains detailed information about a mic.
     */
    public static class Mic {
        // TODO: Implement
    }

    public interface MicClient {
        @Headers("Authorization: JWT ")
        @GET("/api/mics")
        Call<List<MicSummary>> mics();
    }

    public MicSpotService(MicMap map) {
        this.map = map;
    }

    public void Test() throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();

        MicClient client = retrofit.create(MicClient.class);

        // Create a call instance for looking up Retrofit contributors.
        Call<List<MicSummary>> call = client.mics();
        call.enqueue(map);
    }

    // public static
}
