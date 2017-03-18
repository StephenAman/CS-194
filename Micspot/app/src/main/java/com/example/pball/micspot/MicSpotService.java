package com.example.pball.micspot;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.IOException;
import java.util.ArrayList;
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
        public final String createdById;
        public final String meetingBasis;
        public final Date startDate;
        public final Date endDate;

        public MicSummary(String micId, String status, int venueLat, int venueLng, String micName,
                          String createdBy, String createdById, String meetingBasis, Date start,
                          Date end) {
            this.micId = micId;
            this.status = status;
            this.venueLat = venueLat;
            this.venueLng = venueLng;
            this.micName = micName;
            this.createdBy = createdBy;
            this.createdById = createdById;
            this.meetingBasis = meetingBasis;
            this.startDate = start;
            this.endDate = end;
        }
    }

    /**
     * This class contains detailed information about a specific open mic.
     */
    public static class Mic implements Parcelable {
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

        public Mic(Parcel in) {
            this.id = in.readString();
            this.createdBy = in.readString();
            this.micName = in.readString();
            this.venueName = in.readString();
            this.venueAddress = in.readString();
            this.venueLat = in.readFloat();
            this.venueLng = in.readFloat();
            this.startDate = new Date(in.readLong());
            this.duration = in.readInt();
            this.meetingBasis = in.readString();
            this.setTime = in.readInt();
            this.numSlots = in.readInt();
            this.nextInstance = in.readParcelable(Instance.class.getClassLoader());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(createdBy);
            dest.writeString(micName);
            dest.writeString(venueName);
            dest.writeString(venueAddress);
            dest.writeFloat(venueLat);
            dest.writeFloat(venueLng);
            dest.writeLong(startDate.getTime());
            dest.writeInt(duration);
            dest.writeString(meetingBasis);
            dest.writeInt(setTime);
            dest.writeInt(numSlots);
            dest.writeParcelable(nextInstance, flags);
        }

        public static final Parcelable.Creator<Mic> CREATOR
                = new Parcelable.Creator<Mic>() {
            public Mic createFromParcel(Parcel in) {
                return new Mic(in);
            }

            public Mic[] newArray(int size) {
                return new Mic[size];
            }
        };
    }

    public static class Instance implements Parcelable {
        public final String micId;
        public final String instanceId;
        public final Date startDate;
        public final Date endDate;
        public final int numSlots;
        public final int setTime;
        public final int cancelled;
        public final Date signupsOpenDate;
        public final List<Signup> signups;

        public Instance(String micId, String instanceId, Date startDate, Date endDate, int numSlots,
                        int setTime, int cancelled, Date signupsOpenDate, List<Signup> signups) {
            this.micId = micId;
            this.instanceId = instanceId;
            this.startDate = startDate;
            this.endDate = endDate;
            this.numSlots = numSlots;
            this.setTime = setTime;
            this.cancelled = cancelled;
            this.signupsOpenDate = signupsOpenDate;
            this.signups = signups;
        }

        public Instance(Parcel in) {
            this.micId = in.readString();
            this.instanceId = in.readString();
            this.startDate = new Date(in.readLong());
            this.endDate = new Date(in.readLong());
            this.numSlots = in.readInt();
            this.setTime = in.readInt();
            this.cancelled = in.readInt();
            if (in.readInt() == 1) {
                signupsOpenDate = new Date(in.readLong());
            } else {
                signupsOpenDate = null;
            }
            this.signups = new ArrayList<Signup>();
            in.readList(this.signups, Signup.class.getClassLoader());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(micId);
            dest.writeString(instanceId);
            dest.writeLong(startDate.getTime());
            dest.writeLong(endDate.getTime());
            dest.writeInt(numSlots);
            dest.writeInt(setTime);
            dest.writeInt(cancelled);
            if (signupsOpenDate != null) {
                dest.writeInt(1);
                dest.writeLong(signupsOpenDate.getTime());
            } else {
                dest.writeInt(0);
            }
            dest.writeList(signups);
        }

        public static final Parcelable.Creator<Instance> CREATOR
                = new Parcelable.Creator<Instance>() {
            public Instance createFromParcel(Parcel in) {
                return new Instance(in);
            }

            public Instance[] newArray(int size) {
                return new Instance[size];
            }
        };

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

    public static class Signup implements Parcelable {
        public final String userId;
        public final String name;

        public Signup(String userId, String name) {
            this.userId = userId;
            this.name = name;
        }

        public Signup(Parcel in) {
            userId = in.readString();
            name = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(userId);
            dest.writeString(name);
        }

        public static final Parcelable.Creator<Signup> CREATOR
                = new Parcelable.Creator<Signup>() {
            public Signup createFromParcel(Parcel in) {
                return new Signup(in);
            }

            public Signup[] newArray(int size) {
                return new Signup[size];
            }
        };
    }

    public static class UpdateUserData {
        public LastLocationWrapper location;
        public String firebaseToken;
        public UpdateUserData(LastLocationWrapper location, String firebaseToken) {
            this.location = location;
            this.firebaseToken = firebaseToken;
        }
    }

    public static class LastLocationWrapper {
        public float lastLocationLat;
        public float lastLocationLng;
        public LastLocationWrapper(float lat, float lng) {
            this.lastLocationLat = lat;
            this.lastLocationLng = lng;
        }
    }

    public static class Review {
        public String userId;
        public String userName;
        public String reviewText;
        public Date time;
        public Review(String userId, String userName, String reviewText, Date time) {
            this.userId = userId;
            this.userName = userName;
            this.reviewText = reviewText;
            this.time = time;
        }
    }

    public static class CreateReview {
        public String reviewText;
        public CreateReview(String reviewText) {
            this.reviewText = reviewText;
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

        public SignupSlot(String slotNumber) {
            this.slotNumber = slotNumber;
        }
    }

    public interface MicClient {
        @POST("/auth/mobile")
        Call<JWTString> getJWT(@Body FBToken token);

        @PUT("/api/users/{id}")
        Call<Void> updateUser(@Path("id") String userId, @Body UpdateUserData data);

        @GET("/api/mics")
        Call<List<MicSummary>> mics();

        @POST("/api/mics")
        Call<Void> createMic(@Body CreateMicData data);

        @GET("/api/mics/{id}")
        Call<Mic> mic(@Path("id") String micId);

        @GET("/api/mics/{id}/reviews")
        Call<List<Review>> reviews(@Path("id") String micId);

        @POST("/api/mics/{id}/reviews")
        Call<Void> addReview(@Path("id") String micId, @Body CreateReview review);

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

    public void getMic(String micId, SignUpFragment fragment, String jwt) throws IOException {
        MicClient client = Create(jwt);
        Call<Mic> call = client.mic(micId);
        call.enqueue(fragment);
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
