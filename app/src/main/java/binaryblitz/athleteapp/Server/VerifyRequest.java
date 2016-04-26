package binaryblitz.athleteapp.Server;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import binaryblitz.athleteapp.Utils.AndroidUtils;

public class VerifyRequest implements Request {

    String id;
    String number;
    String code;

    VerifyRequest(String id, String code, String number) {
        this.id = id;
        this.code = code;
        this.number = number;
    }

    @Override
    public void execute(final OnRequestPerformedListener listener, final JSONObject... params) {

        if(!AndroidUtils.isConnected(GetFitServerRequest.context)) {
            GetFitServerRequest.activity.cancelRequest();
            listener.onRequestPerformedListener("Internet");
            return;
        }

        JSONObject object = new JSONObject();

        try {
            object.accumulate("phone_number", number);
            object.accumulate("code", code);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                com.android.volley.Request.Method.PATCH,
                GetFitServerRequest.baseUrl +
                       "/api/verification_tokens/" +
                        id,
                object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onRequestPerformedListener(response);
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
