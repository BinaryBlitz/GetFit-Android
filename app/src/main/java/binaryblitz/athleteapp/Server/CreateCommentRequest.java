package binaryblitz.athleteapp.Server;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import binaryblitz.athleteapp.Utils.AndroidUtils;

public class CreateCommentRequest implements Request {

    String id;

    public CreateCommentRequest(String id) {
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
                com.android.volley.Request.Method.POST,
                GetFitServerRequest.baseUrl
                        + "/api/posts/" + id + "/comments" + GetFitServerRequest.apiToken,
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