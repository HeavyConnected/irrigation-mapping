package com.heavyconnect.heavyconnect.rest;

import com.google.gson.Gson;
import com.heavyconnect.heavyconnect.utils.Constants;

import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.client.Request;
import retrofit.converter.GsonConverter;
import retrofit.http.*;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Class that handle http client.
 */
public class HttpRetrofitClient {
    private final String API_URL = "http://heavytest.comuf.com/"; // Tests

    private final RestAdapter restAdapter;
    public final API client;
    private Gson gson = new Gson();

    /**
     * Interface containing all http operations.
     */
    public interface API {
		
        @FormUrlEncoded
        @POST("/Register.php")
        RegisterResult createUser(
                @Field("name") String name,
                @Field("username") String username,
                @Field("password") String password);

        @FormUrlEncoded
        @POST("/FetchUserData.php")
        LoginResult fetchUser(
                @Field("username") String username,
                @Field("password") String password);
    }

    /**
     * Class constructor.
     */
    public HttpRetrofitClient() {
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .setClient(new UrlConnectionClient())
                .setConverter(new GsonConverter(gson))
                .setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("HeavyConnect_api"))
                .build();
        client = restAdapter.create(API.class);
    }

    /**
     * Represents an Url connection client.
     */
    public final class UrlConnectionClient extends retrofit.client.UrlConnectionClient {
        @Override
        protected HttpURLConnection openConnection(Request request) throws IOException {
            HttpURLConnection connection = super.openConnection(request);
            connection.setConnectTimeout(Constants.CONNECTION_TIMEOUT);
            connection.setReadTimeout(Constants.SOCKET_TIMEOUT);
            return connection;
        }
    }
}