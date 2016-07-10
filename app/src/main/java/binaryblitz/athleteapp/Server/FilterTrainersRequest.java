package binaryblitz.athleteapp.Server;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import binaryblitz.athleteapp.Data.ProfessionalType;
import binaryblitz.athleteapp.Utils.AndroidUtils;

public class FilterTrainersRequest implements Request {

    ProfessionalType type;
    String order;
    String spec;

    public FilterTrainersRequest(ProfessionalType type, String order, String spec) {
        this.type = type;
        this.order = order;
        this.spec = spec;
    }

    @Override
    public void execute(final OnRequestPerformedListener listener, final JSONObject... params) {

        if(!AndroidUtils.isConnected(GetFitServerRequest.context)) {
            GetFitServerRequest.activity.cancelRequest();
            listener.onRequestPerformedListener("Internet");
            return;
        }

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
                        + "&order=" + order
                        + "&specialization_id=" + spec
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