package binaryblitz.athleteapp.Server;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import binaryblitz.athleteapp.Data.ProfessionalType;

public class GetProfsRequest implements Request {

    ProfessionalType type;

    public GetProfsRequest(ProfessionalType type) {
        this.type = type;
    }

    @Override
    public void execute(final OnRequestPerformedListener listener, final JSONObject... params) {

        String res = "";

        switch (type) {
            case DOCTOR:
                res += "physician";
                break;
            case NUTRITIONIST:
                res += "nutritionist";
                break;
            case COACH:
                res += "trainer";
                break;
            default:
                break;
        }

        JsonArrayRequest jsObjRequest = new JsonArrayRequest(
                com.android.volley.Request.Method.GET,
                GetFitServerRequest.baseUrl
                        + "/api/trainers"
                        + GetFitServerRequest.apiToken + "&category=" + res
                ,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
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