package com.heavyconnect.heavyconnect.rest;

import com.google.gson.Gson;
import com.heavyconnect.heavyconnect.entities.Equipment;
import com.heavyconnect.heavyconnect.utils.Constants;

import java.io.IOException;
import java.net.HttpURLConnection;

import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.client.Request;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.http.*;

/**
 * Class that handle http client.
 */
public class HttpRetrofitClient {
    private final String API_URL = "http://hcdroidapi-dev.herokuapp.com/"; //"http://sheltered-dusk-6568.herokuapp.com"; Heroku server

    private final RestAdapter restAdapter;
    public final API client;
    private Gson gson = new Gson();

    /**
     * Interface containing all http operations.
     */
    public interface API {
		
        @FormUrlEncoded
        @POST("/register/")
        RegisterResult createUser(
                @Field("first_name") String firstName,
                @Field("last_name") String lastName,
                @Field("username") String username,
                @Field("password") String password,
                @Field("email") String email);

        @FormUrlEncoded
        @POST("/login/")
        LoginResult fetchUser(
                @Field("username") String username,
                @Field("password") String password);

        @GET("/equipment/")
        EquipmentListResult fetchUserEquips(
                @Header("Authorization") String token);

        @FormUrlEncoded
        @POST("/equipment/")
        EquipmentDetailsResult createEquip(
                @Header("Authorization") String token,
                @Field("name") String name,
                @Field("model_number") String modelNumber,
                @Field("asset_number") int assetNumber,
                @Field("status") int status,
                @Field("hours") int hours,
                @Field("latitude") double latitude,
                @Field("longitude") double longitude
        );

        @GET("/equipment/{id}")
        Equipment fetchEquipmentDetails(
                @Header("Authorization") String token,
                @Path("id") int equipId
        );

        @FormUrlEncoded
        @PUT("/equipment/{id}")
        EquipmentDetailsResult saveEquipChanges(
                @Header("Authorization") String token,
                @Path("id") int equipId,
                @Field("name") String name,
                @Field("model_number") String modelNumber,
                @Field("asset_number") int assetNumber,
                @Field("status") int status,
                @Field("hours") int hours,
                @Field("latitude") double latitude,
                @Field("longitude") double longitude
        );

        @DELETE("/equipment/{id}")
        Response removeEquip(
                @Header("Authorization") String token,
                @Path("id") int equipId
        );


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