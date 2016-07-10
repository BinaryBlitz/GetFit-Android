package binaryblitz.athleteapp.Server;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import binaryblitz.athleteapp.Utils.AndroidUtils;

public class SendMessageRequest implements Request {

    String id;
    String content;
    String photo;
    DisplayImageOptions options;

    SendMessageRequest(String id, String content, String photo) {

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(GetFitServerRequest.context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());

        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(false)
                .build();


        this.id = id;
        this.content = content;
        this.photo = photo;
    }

    @Override
    public void execute(final OnRequestPerformedListener listener, final JSONObject... params) {

        if(!AndroidUtils.isConnected(GetFitServerRequest.context)) {
            GetFitServerRequest.activity.cancelRequest();
            listener.onRequestPerformedListener("Internet");
            return;
        }

        if(photo != null) {
            ImageLoader.getInstance()
                    .loadImage("file:///" + photo, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                            String base64 = encodeToBase64(loadedImage);
                            JSONObject user = new JSONObject();
                            try {
                                user.accumulate("image", "data:image/gif;base64," + base64);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            JSONObject to_send = new JSONObject();
                            try {
                                to_send.accumulate("message", user);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                                    com.android.volley.Request.Method.POST,
                                    GetFitServerRequest.baseUrl +
                                            "/api/subscriptions/" + id + "/messages" + GetFitServerRequest.apiToken
                                    ,to_send,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            listener.onRequestPerformedListener(response);
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

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }
                    });
        } else {
            JSONObject user = new JSONObject();
            try {
                user.accumulate("content", content);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONObject to_send = new JSONObject();
            try {
                to_send.accumulate("message", user);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                    com.android.volley.Request.Method.POST,
                    GetFitServerRequest.baseUrl +
                            "/api/subscriptions/" + id + "/messages" + GetFitServerRequest.apiToken
                    ,to_send,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            listener.onRequestPerformedListener(response);
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

    public static String encodeToBase64(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }
}