package com.example.pball.micspot;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.Callback;
import retrofit2.http.Headers;
import retrofit2.http.GET;
import retrofit2.http.Path;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Date;

public final class MicSpotService {
    public static final String API_URL = "http://138.68.7.110:8080";

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
     * This class contains detailed information about a specific open mic.
     */
    public static class Mic {
        // TODO: Implement
    }

    public static class FBToken {
        public final String id;
        public final String token;
        public FBToken(String id, String token) {
            this.id = id;
            this.token = token;
        }
    }

    public static class JWTString {
        public final String jwt;
        public JWTString(String jwt) {
            this.jwt = jwt;
        }
    }

    public interface MicClient {
        @POST("/auth/mobile")
        Call<JWTString> getJWT(@Body FBToken token);

        @GET("/api/mics")
        Call<List<MicSummary>> mics();
    }

    public void GetAllMics(String jwt, MicMap map) throws IOException {
        MicClient client = Create(jwt);
        Call<List<MicSummary>> call = client.mics();
        call.enqueue(map);
    }

    public static void GetJWT(String fbId, String fbToken, LoginActivity login) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MicClient client = retrofit.create(MicClient.class);
        Call<JWTString> call = client.getJWT(new FBToken(fbId, fbToken));
        call.enqueue(login);
    }

    /**
     * MicClient factory.
     */
    public MicClient Create(String jwt) {
        OkHttpClient okHttpClient;
        if (jwt == null) {
            okHttpClient = new OkHttpClient.Builder().build();
        } else {
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new JWTInterceptor(jwt))
                    .build();
        }
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();
        MicClient client = retrofit.create(MicClient.class);
        return client;
    }

    /**
     * Add JWT authorization header to every API request.
     */
    final class JWTInterceptor implements Interceptor {
        private String jwt;

        public JWTInterceptor(String jwt) {
            this.jwt = jwt;
        }

        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request newRequest = originalRequest.newBuilder()
                    .header("Authorization", "JWT " + jwt)
                    .method(originalRequest.method(), originalRequest.body()).build();
            return chain.proceed(newRequest);
        }
    }
}
