package com.example.pball.micspot;

import java.io.IOException;
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

public class MicSpotService {
    public static final String API_URL = "http://138.68.7.110:8080";

    private MicMap map;

    public static class MicSummary {
        public final String micId;
        public final String status;
        public final float venueLat;
        public final float venueLng;

        public MicSummary(String micId, String status, int venueLat, int venueLng) {
            this.micId = micId;
            this.status = status;
            this.venueLat = venueLat;
            this.venueLng = venueLng;
        }
    }

    public interface MicClient {
        @Headers("Authorization: JWT")
        @GET("/api/mics")
        Call<List<MicSummary>> mics();
    }

    public MicSpotService(MicMap map) {
        this.map = map;
    }

    public void Test() throws IOException {
        System.out.println("hello");

        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        // Create a very simple REST adapter which points the GitHub API.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        // Create an instance of our GitHub API interface.
        MicClient client = retrofit.create(MicClient.class);

        // Create a call instance for looking up Retrofit contributors.
        Call<List<MicSummary>> call = client.mics();
        call.enqueue(map);
    }
}
