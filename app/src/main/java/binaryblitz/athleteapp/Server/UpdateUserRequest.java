package binaryblitz.athleteapp.Server;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import binaryblitz.athleteapp.Data.User;

public class UpdateUserRequest implements Request {

    @Override
    public void execute(final OnRequestPerformedListener listener, final JSONObject... params) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                com.android.volley.Request.Method.PATCH,
                GetFitServerRequest.baseUrl
                        + "/api/user"
                        + GetFitServerRequest.apiToken
                ,
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
                        listener.onRequestPerformedListener("Error");
                    }
                }
        );
        GetFitServerRequest.queue.add(jsObjRequest);
    }
}