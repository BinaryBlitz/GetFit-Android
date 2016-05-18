package binaryblitz.athleteapp.Server;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

public class RateTrainerRequest implements Request {

    String id;

    public RateTrainerRequest(String id) {
        this.id = id;
    }

    @Override
    public void execute(final OnRequestPerformedListener listener, final JSONObject... params) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                com.android.volley.Request.Method.POST,
                GetFitServerRequest.baseUrl
                        + "/api/trainers/" + id + "/ratings"
                        + GetFitServerRequest.apiToken,
                params[0],
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
                            Log.e("qwerty", new String(error.networkResponse.data));
                        } catch (Exception e) {

                        }
                        listener.onRequestPerformedListener("Error");
                    }
                }
        );
        GetFitServerRequest.queue.add(jsObjRequest);
    }
}