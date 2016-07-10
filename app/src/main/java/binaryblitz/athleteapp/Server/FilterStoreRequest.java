package binaryblitz.athleteapp.Server;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import binaryblitz.athleteapp.Data.ProfessionalType;
import binaryblitz.athleteapp.Utils.AndroidUtils;

public class FilterStoreRequest implements Request {
    String order;
    String spec;
    String price;

    public FilterStoreRequest(String order, String spec, String price) {
        this.order = order;
        this.spec = spec;
        this.price = price;
    }

    @Override
    public void execute(final OnRequestPerformedListener listener, final JSONObject... params) {

        if(!AndroidUtils.isConnected(GetFitServerRequest.context)) {
            GetFitServerRequest.activity.cancelRequest();
            listener.onRequestPerformedListener("Internet");
            return;
        }

        JsonArrayRequest jsObjRequest = new JsonArrayRequest(
                com.android.volley.Request.Method.GET,
                GetFitServerRequest.baseUrl
                        + "/api/programs"
                        + GetFitServerRequest.apiToken + "&min_price=" + price
                        + "&order=" + order
                        + "&program_type_id=" + spec
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