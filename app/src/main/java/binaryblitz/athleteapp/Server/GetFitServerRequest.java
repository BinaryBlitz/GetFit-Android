package binaryblitz.athleteapp.Server;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;

import binaryblitz.athleteapp.Abstract.BaseActivity;
import binaryblitz.athleteapp.Abstract.InternetConnectionDependentActivity;

public class GetFitServerRequest {
    private static GetFitServerRequest request;

    static String apiToken;
    static Context context;
    public static String baseUrl = "http://getfit.binaryblitz.ru";

    static String PREFERENCE_NAME = "GetFitAppPrefs";
    static String TOKEN_ENTITY = "auth_token";
    static String TOKEN_MESSAGER_ENTITY = "messager_token";
    static String TOKEN_DEVICE_ENTITY = "device_token";
    static String USER_ENTITY = "auth_info";

    static InternetConnectionDependentActivity activity;
    public static RequestQueue queue;

    public static GetFitServerRequest with(Context context) {
        GetFitServerRequest.context = context;
        if (request == null) {
            synchronized (GetFitServerRequest.class) {
                if (request == null) {
                    request = new GetFitServerRequest(context);
                }
            }
        }
        return request;
    }

    public static GetFitServerRequest with(BaseActivity context) {
        GetFitServerRequest.context = context;
        activity = context;
        if (request == null) {
            synchronized (GetFitServerRequest.class) {
                if (request == null) {
                    request = new GetFitServerRequest(context);
                }
            }
        }
        return request;
    }

    private GetFitServerRequest(Context context) {
        GetFitServerRequest.context = context;

        if (queue == null) {
            Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024); // 1MB cap
            Network network = new BasicNetwork(new OkHttpStack());
            queue = new RequestQueue(cache, network);
            queue.start();
        }
    }

    private GetFitServerRequest(BaseActivity context) {
        GetFitServerRequest.activity = context;
        GetFitServerRequest.context = context;

        if (queue == null) {
            Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024); // 1MB cap
            Network network = new BasicNetwork(new OkHttpStack());
            queue = new RequestQueue(cache, network);
            queue.start();
        }
    }

    public RequestPerformer authorize() throws IllegalArgumentException {
        if(DeviceInfoStore.getToken(context).equals("null")) {
            throw new IllegalArgumentException();
        }
        apiToken = "?api_token=" + DeviceInfoStore.getToken(context);
        return new RequestPerformer();
    }

    public RequestPerformer skipAuth() {
        return new RequestPerformer();
    }
}
