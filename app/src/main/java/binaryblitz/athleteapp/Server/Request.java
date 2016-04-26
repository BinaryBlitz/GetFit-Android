package binaryblitz.athleteapp.Server;

import org.json.JSONObject;

public interface Request {
    void execute(final OnRequestPerformedListener listener, final JSONObject... params);
}
