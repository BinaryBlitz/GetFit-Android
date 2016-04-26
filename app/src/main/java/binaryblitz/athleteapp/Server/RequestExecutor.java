package binaryblitz.athleteapp.Server;

import org.json.JSONObject;

public class RequestExecutor {

    private Request request;
    private OnRequestPerformedListener listener;
    private JSONObject[] objects;

    public RequestExecutor(Request request,
                           OnRequestPerformedListener listener,
                           JSONObject[] objects) {
        this.request = request;
        this.objects = objects;
        this.listener = listener;
    }

    public void perform() {
        request.execute(listener, objects);
    }

}
