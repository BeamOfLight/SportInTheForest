package beamoflight.sportintheforest;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by beamoflight on 30.05.18.
 */


public class NetworkHelper {
    //private Context context;
    private GameHelper gameHelper;
    private String apiUrl;

    public OkHttpClient getClient() {
        return client;
    }

    private OkHttpClient client;

    public NetworkHelper(Context current) {
        client = new OkHttpClient();
        //context = current;
        gameHelper = new GameHelper(current);
        apiUrl = current.getResources().getString(R.string.app_internet_api_url);
        //apiUrl = current.getResources().getString(R.string.app_internet_api_url_test);
    }

    private Request getRequest(RequestBody requestBody)
    {
        return new Request.Builder()
                .url(apiUrl)
                .post(requestBody)
                .build();
    }

//    private String send(Request request)
//    {
//        try {
//            Response response = client.newCall(request).execute();
//            return response.body().string();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
/*
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                serverResponse = null;
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                serverResponse = response.body().string();
            }
        });
*/
//    }

    Request getCreateCompetitionRequest(CompetitionView competition_view, String player_name) throws IOException {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("request", "CreateCompetition")
                .addFormDataPart("competition_state", new Gson().toJson(competition_view))
                .addFormDataPart("competition_state_hash", "123")
                .addFormDataPart("player_name", player_name)
                .addFormDataPart("exercise_name", competition_view.exerciseName)
                .addFormDataPart("token", gameHelper.getDeviceIMEI())
                .build();

        return getRequest(requestBody);
    }

    Request getUpdateCompetitionInfoRequest(CompetitionView competition_view, String player_name, String invite_code) throws IOException {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("request", "UpdateCompetitionInfo")
                .addFormDataPart("competition_state", new Gson().toJson(competition_view))
                .addFormDataPart("competition_state_hash", "123")
                .addFormDataPart("player_name", player_name)
                .addFormDataPart("invite_code", invite_code)
                .addFormDataPart("exercise_name", competition_view.exerciseName)
                .addFormDataPart("token", gameHelper.getDeviceIMEI())
                .build();

        return getRequest(requestBody);
    }

    Request getGetCompetitionInfoRequest(String invite_code) throws IOException {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("request", "GetCompetitionInfo")
                .addFormDataPart("invite_code", invite_code)
                .addFormDataPart("competition_state_hash", "123")
                .addFormDataPart("token", gameHelper.getDeviceIMEI())
                .build();

        return getRequest(requestBody);
    }

    class CreateCompetitionResponse
    {
        @SerializedName("invite_code")
        @Expose
        String inviteCode;

        @SerializedName("state")
        @Expose
        String state;

        @SerializedName("msg")
        @Expose
        String msg;
    }

    class UpdateCompetitionInfoResponse
    {
        @SerializedName("state")
        @Expose
        String state;
    }

    class GetCompetitionInfoResponse
    {
        @SerializedName("competition_state")
        @Expose
        String competitionState;

        @SerializedName("state")
        @Expose
        String state;
    }
}
