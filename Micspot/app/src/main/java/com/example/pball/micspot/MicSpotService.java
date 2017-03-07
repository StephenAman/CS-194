package com.example.pball.micspot;

import java.io.IOException;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.http.HTTP;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.DELETE;
import retrofit2.http.PUT;
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
        public final String id;
        public final String createdBy;
        public final String micName;
        public final String venueName;
        public final String venueAddress;
        public final float venueLat;
        public final float venueLng;
        public final Date startDate;
        public final int duration;
        public final String meetingBasis;
        public final int setTime;
        public final int numSlots;
        public final Instance nextInstance;

        public Mic(String id, String createdBy, String micName, String venueName,
                   String venueAddress, float venueLat, float venueLng, Date startDate,
                   int duration, String meetingBasis, int setTime, int numSlots,
                   Instance nextInstance) {
            this.id = id;
            this.createdBy = createdBy;
            this.micName = micName;
            this.venueName = venueName;
            this.venueAddress = venueAddress;
            this.venueLat = venueLat;
            this.venueLng = venueLng;
            this.startDate = startDate;
            this.duration = duration;
            this.meetingBasis = meetingBasis;
            this.setTime = setTime;
            this.numSlots = numSlots;
            this.nextInstance = nextInstance;
        }
    }

    public static class Instance {
        public final String micId;
        public final String instanceId;
        public final Date startDate;
        public final Date endDate;
        public final int numSlots;
        public final int setTime;
        public final int cancelled;
        public final List<Signup> signups;
        public Instance(String micId, String instanceId, Date startDate, Date endDate, int numSlots,
                        int setTime, int cancelled, List<Signup> signups) {
            this.micId = micId;
            this.instanceId = instanceId;
            this.startDate = startDate;
            this.endDate = endDate;
            this.numSlots = numSlots;
            this.setTime = setTime;
            this.cancelled = cancelled;
            this.signups = signups;
        }
    }

    public static class CreateMicData {
        public final String micName;
        public final String venueName;
        public final String venueAddress;
        public final float venueLat;
        public final float venueLng;
        public final Date startDate;
        public final int duration;
        public final String meetingBasis;
        public final int setTime;
        public final int numSlots;
        public CreateMicData(String micName, String venueName, String venueAddress,
                             float venueLat, float venueLng, Date startDate, int duration,
                             String meetingBasis, int setTime, int numSlots) {
            this.micName = micName;
            this.venueName = venueName;
            this.venueAddress = venueAddress;
            this.venueLat = venueLat;
            this.venueLng = venueLng;
            this.startDate = startDate;
            this.duration = duration;
            this.meetingBasis = meetingBasis;
            this.setTime = setTime;
            this.numSlots = numSlots;
        }
    }

    public static class UpdateInstanceData {
        public EventDateWrapper eventDate;
        public Date signupsOpenDate;
        public int numSlots;
        public int setTime;
        public int cancelled;
        public String meetingBasis;
        public UpdateInstanceData(EventDateWrapper eventDate, Date signupsOpenDate, int numSlots,
                                  int setTime, int cancelled, String meetingBasis) {
            this.eventDate = eventDate;
            this.signupsOpenDate = signupsOpenDate;
            this.numSlots = numSlots;
            this.setTime = setTime;
            this.cancelled = cancelled;
            this.meetingBasis = meetingBasis;
        }
    }

    public static class EventDateWrapper {
        public final Date startDate;
        public final int duration;
        public final int updateDefaultStartDate;
        public EventDateWrapper(Date startDate, int duration, int updateDefaultStartDate) {
            this.startDate = startDate;
            this.duration = duration;
            this.updateDefaultStartDate = updateDefaultStartDate;
        }
    }

    public static class Signup {
        public final String userId;
        public final String name;
        public Signup(String userId, String name) {
            this.userId = userId;
            this.name = name;
        }
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

    public static class SignupSlot {
        public final String slotNumber;
        public SignupSlot(String slotNumber ) { this.slotNumber = slotNumber; }
    }

    public interface MicClient {
        @POST("/auth/mobile")
        Call<JWTString> getJWT(@Body FBToken token);

        @GET("/api/mics")
        Call<List<MicSummary>> mics();

        @POST("/api/mics")
        Call<Void> createMic(@Body CreateMicData data);

        @GET("/api/mics/{id}")
        Call<Mic> mic(@Path("id") String micId);

        @POST("/api/mics/{micId}/instances/{instanceId}/signups")
        Call<Void> addSignup(@Path("micId") String micId, @Path("instanceId") String instanceId,
                             @Body SignupSlot slot);

        @HTTP(method = "DELETE", path = "/api/mics/{micId}/instances/{instanceId}/signups", hasBody = true)
        Call<Void> removeSignup(@Path("micId") String micId, @Path("instanceId") String instanceId,
                                @Body SignupSlot slot);

        @PUT("/api/mics/{micId}/instances/{instanceId}")
        Call<Void> updateInstance(@Path("micId") String micId, @Path("instanceId") String instanceId, @Body UpdateInstanceData data);
    }

    public void getAllMics(MicMap map, String jwt) throws IOException {
        MicClient client = Create(jwt);
        Call<List<MicSummary>> call = client.mics();
        call.enqueue(map);
    }

    public void getMic(String micId, SignUpActivity activity, String jwt) throws IOException {
        MicClient client = Create(jwt);
        Call<Mic> call = client.mic(micId);
        call.enqueue(activity);
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
