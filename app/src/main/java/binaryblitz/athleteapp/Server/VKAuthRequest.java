package binaryblitz.athleteapp.Server;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import binaryblitz.athleteapp.Utils.AndroidUtils;

public class VKAuthRequest implements Request {

    String token;

    public VKAuthRequest(String token) {
        this.token = token;
    }

    @Override
    public void execute(final OnRequestPerformedListener listener, final JSONObject... params) {

        if(!AndroidUtils.isConnected(GetFitServerRequest.context)) {
            GetFitServerRequest.activity.cancelRequest();
            listener.onRequestPerformedListener("Internet");
            return;
        }

        binaryblitz.athleteapp.Server.JsonObjectRequest jsObjRequest = new binaryblitz.athleteapp.Server.JsonObjectRequest(
                com.android.volley.Request.Method.POST,
                GetFitServerRequest.baseUrl
                        + "/api/user/authenticate_vk?token=" + token,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response.toString().startsWith("{\"error\"")) {
                            listener.onRequestPerformedListener("Error");
                        } else {
                            listener.onRequestPerformedListener(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            if(error.networkResponse.statusCode == 401) {
                                listener.onRequestPerformedListener("AuthError");
                                return;
                            }
                        } catch (Exception ignored) {}
                        listener.onRequestPerformedListener("Error");
                    }
                }
        );
        GetFitServerRequest.queue.add(jsObjRequest);
    }
}
