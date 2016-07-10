package binaryblitz.athleteapp.Server;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import binaryblitz.athleteapp.Utils.AndroidUtils;

public class GetDurationRequest implements Request {

    String id;

    public GetDurationRequest(String id) {
        this.id = id;
    }

    @Override
    public void execute(final OnRequestPerformedListener listener, final JSONObject... params) {

        if(!AndroidUtils.isConnected(GetFitServerRequest.context)) {
            GetFitServerRequest.activity.cancelRequest();
            listener.onRequestPerformedListener("Internet");
            return;
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                com.android.volley.Request.Method.GET,
                "https://www.googleapis.com/youtube/v3/videos?key=AIzaSyApr00xYqk8H_iyUFB_1PiJndObPvNSqwc&part=contentDetails&id=" + id
                ,
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