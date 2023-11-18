package com.example.votingapp.ApiLib;


import static com.example.votingapp.constants.Constants.CONNECTION_TIME_OUT;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.votingapp.constants.Constants;
import com.example.votingapp.model.Passcode;
import com.example.votingapp.model.RoundUpdateBody;
import com.example.votingapp.model.Token;
import com.example.votingapp.model.User;
import com.example.votingapp.model.UserSignUp;
import com.example.votingapp.utils.AuthHelper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.client.methods.RequestBuilder;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Network Request class to abstract implementation details of requests
 */
public class NetworkRequest {
    private static final String BASE_URL = Constants.BASE_URL;

    private Callback mCallback;

    private OkHttpClient mClient;

    private List<Call> onGoingCalls;

    private static NetworkRequest sInstance;


    private NetworkRequest() {
        mClient = new OkHttpClient.Builder()  .connectTimeout(CONNECTION_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(CONNECTION_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(CONNECTION_TIME_OUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
        onGoingCalls = new ArrayList<>();
    }

    public void cancelOngoingRequests(){
        for(Call call : onGoingCalls){
            call.cancel();
        }
    }

    public static NetworkRequest getInstance() {
        if (sInstance == null) {
            sInstance = new NetworkRequest();
        }
        return sInstance;
    }

    /**
     * Sets the callback for the network request
     *
     * @param callback
     */
    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    /**
     *
     * @param user
     * @param callback
     */
    public void doLogin(@NonNull User user, Callback callback) {
        setCallback(callback);
        String body = new Gson().toJson(user);
        String loginUrl = BASE_URL + "login";
        doPostRequest(loginUrl, body, callback);
    }

    public void doUpdateRound(Token token , @NonNull RoundUpdateBody roundUpdate, Callback callback) {
        setCallback(callback);
        String body = new Gson().toJson(roundUpdate);
        String loginUrl = BASE_URL + "roundupdate";
        Log.d("info", "doUpdateRound: "+body);
        doPostRequestWithToken(token.getIdToken(), loginUrl, body, callback);
    }


    public void doGetCandidates(Token token, Callback callback) {
        setCallback(callback);
        String getCandidatesUrl = BASE_URL + "getCandidates";
        doGetRequestWithToken(getCandidatesUrl, new HashMap<>(), token.getIdToken(), callback);
    }

    public void doGetLastRoundData(Token token, String electionCode, Callback callback) {
        setCallback(callback);
        String getCandidatesUrl = BASE_URL + "lastrounddata";
        Map<String, String> params = new HashMap<>();
        params.put("ecode", electionCode);
        doGetRequestWithToken(getCandidatesUrl, params, token.getIdToken(), callback);
    }

    public void doGetPasscodes(Token token, Callback callback) {
        setCallback(callback);
        String getPasscodesUrl = BASE_URL + "passcodes";
        doGetRequestWithToken(getPasscodesUrl, new HashMap<>(), token.getIdToken(), callback);
    }

    public Passcode[] doGetPasscodesSync(Token token) {;
        String getPasscodesUrl = BASE_URL + "passcodes";
        Response response = doGetRequestWithTokenSync(getPasscodesUrl, new HashMap<>(), token.getIdToken());
        if(response != null && response.isSuccessful()) {
            try{
                return (Passcode[]) buildObjectFromResponse(response.body().string(), Passcode[].class);
            }catch (IOException e){
                return null;
            }
            finally {
                response.close();
            }
        }else{
            return null;
        }
    }

    public void doUpdatePassword (Token token , @NonNull String userName, Callback callback) {
        setCallback(callback);
        Map<String, String> params = new HashMap<>();
        params.put("email", userName);
        String forgetPasswordUrl = BASE_URL + "forgetpwd";
        doPostRequestWithToken(forgetPasswordUrl,  params, token.getIdToken(), callback);
    }


    public void doSignUp (Token token , @NonNull UserSignUp userSignUp, Callback callback) {
        setCallback(callback);
        String body = new Gson().toJson(userSignUp);
        String loginUrl = BASE_URL + "addauditor";
        Log.d("info", "doSignUp: "+body);
        doPostRequestWithToken(token.getIdToken(), loginUrl, body, callback);
    }

    /**
     * Execute post request
     *
     * @param url
     * @param params
     * @param callback
     */
    private void doPostRequest(@NonNull String url, @NonNull String params, @Nullable final Callback callback) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), params);
        Request request = new Request.Builder().url(httpUrl).post(requestBody).build();
        doRequest(request, callback);
    }


    private void doPostRequestWithToken(@NonNull String token, @NonNull String url, @NonNull String params, @Nullable final Callback callback) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), params);
        Request.Builder requestBuilder = new Request.Builder().url(httpUrl).post(requestBody);
        if (token != null) {
            requestBuilder.addHeader("Authorization", "Bearer " + token);
        }
        doRequest(requestBuilder.build(), callback);
    }

    private void doPostRequestWithToken(@NonNull String url, @NotNull Map<String, String> params, @Nullable String token, @Nullable Callback callback) {
        HttpUrl httpUrl = HttpUrl.parse(url);

        HttpUrl.Builder urlBuilder = httpUrl.newBuilder();
        for (String key : params.keySet()) {
            urlBuilder.addQueryParameter(key, params.get(key));
        }

        Request.Builder requestBuilder = new Request.Builder().url(urlBuilder.build()).post(RequestBody.create(null, ""));

        if (token != null) {
            requestBuilder.addHeader("Authorization", "Bearer " + token);
        }
        doRequest(requestBuilder.build(), callback);
    }


    private void doGetRequestWithToken(@NonNull String url, @NotNull Map<String, String> params, @Nullable String token, @Nullable Callback callback) {
        HttpUrl httpUrl = HttpUrl.parse(url);

        HttpUrl.Builder urlBuilder = httpUrl.newBuilder();
        for (String key : params.keySet()) {
            urlBuilder.addQueryParameter(key, params.get(key));
        }

        Request.Builder requestBuilder = new Request.Builder().url(urlBuilder.build()).get();

        if (token != null) {
            requestBuilder.addHeader("Authorization", "Bearer " + token);
        }
        doRequest(requestBuilder.build(), callback);
    }

    private Response doGetRequestWithTokenSync(@NonNull String url, @NotNull Map<String, String> params, @Nullable String token) {
        HttpUrl httpUrl = HttpUrl.parse(url);

        HttpUrl.Builder urlBuilder = httpUrl.newBuilder();
        for (String key : params.keySet()) {
            urlBuilder.addQueryParameter(key, params.get(key));
        }

        Request.Builder requestBuilder = new Request.Builder().url(urlBuilder.build()).get();

        if (token != null) {
            requestBuilder.addHeader("Authorization", "Bearer " + token);
        }
        return doRequestSync(requestBuilder.build());
    }

    private void doGetRequestNoToken(@NonNull String url, @NonNull Map<String, String> params, @NonNull String token, @Nullable Callback callback) {
        doGetRequestWithToken(url, params, token, callback);
    }

    /**
     * Makes request and fires callback as at when due
     *
     * @param request
     * @param callback
     */
    private void doRequest(@NonNull Request request, final Callback callback) {
        Call call = mClient.newCall(request);
        onGoingCalls.add(call);
        call.enqueue(new okhttp3.Callback() {
            Handler mainHandler = new Handler(Looper.getMainLooper());

            @Override
            public void onFailure(Call call, final IOException e) {
                onGoingCalls.remove(call);
                Log.d("info", "onResponse: "+e.toString());
                if (callback != null) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(Constants.INTERNET_UNAVAILABLE, "Please check your internet connection");
                        }
                    });
                }
            }

            @Override
            public void onResponse(final Call call, final Response response) {
                onGoingCalls.remove(call);
                Log.d("info", "onResponse: "+response.toString());
                if (callback != null) {
                    try {
                        final String stringResponse = response.body().string();
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Object res = buildObjectFromResponse(stringResponse, callback.type());Log.d("info", "run: "+stringResponse);
                                if (res != null) {
                                    callback.onResponse(response.code(), res);
                                } else {
                                    callback.onError(response.code(), stringResponse);
                                }
                            }
                        });
                    } catch (final IOException ioe) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onError(Constants.INTERNET_UNAVAILABLE, "Please check your internet connection");
                            }
                        });
                    }
                }
            }
        });
    }



    private Response doRequestSync(@NonNull Request request) {
        try {
            Response response = mClient.newCall(request).execute();
            if(response.isSuccessful()){
                return response;
            }
        }catch (IOException  e){
            Log.e("error", "doRequestSync: "+e);
            return null;
        }
        return null;
    }

    private Object buildObjectFromResponse(String response, Class cls) {
        if (cls == String.class) {
            return response;
        } else {
            try {
                return new Gson().fromJson(response, cls);
            } catch (JsonSyntaxException jse) {
                return null;
            }
        }
    }

    /**
     * Callback interface for network response and error
     *
     * @param <T>
     */
    public interface Callback<T> {
        void onResponse(int responseCode, @NonNull T response);

        void onError(int responseCode, String error);

        Class<T> type();
    }

    /**
     * ApiResponse interface
     */
    public interface ApiResponse {
        String string();
    }
}
