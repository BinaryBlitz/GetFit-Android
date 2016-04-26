package binaryblitz.athleteapp.Server;

import android.content.Context;
import android.content.SharedPreferences;

import binaryblitz.athleteapp.Data.User;

@SuppressWarnings("unused")
public class DeviceInfoStore {

    public static void saveToken(String token) {
        SharedPreferences prefs = GetFitServerRequest.context.getSharedPreferences(
                GetFitServerRequest.PREFERENCE_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(GetFitServerRequest.TOKEN_ENTITY, token).apply();
    }

    public static void resetToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                GetFitServerRequest.PREFERENCE_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(GetFitServerRequest.TOKEN_ENTITY, "null").apply();
    }

    public static String getToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                GetFitServerRequest.PREFERENCE_NAME, Context.MODE_PRIVATE);
        return prefs.getString(GetFitServerRequest.TOKEN_ENTITY, "null");
    }

    public static void resetDeviceToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                GetFitServerRequest.PREFERENCE_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(GetFitServerRequest.TOKEN_DEVICE_ENTITY, "null").apply();
    }

    public static String getDeviceToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                GetFitServerRequest.PREFERENCE_NAME, Context.MODE_PRIVATE);
        return prefs.getString(GetFitServerRequest.TOKEN_DEVICE_ENTITY, "null");
    }

    public static void saveUser(User user) {
        SharedPreferences prefs = GetFitServerRequest.context.getSharedPreferences(
                GetFitServerRequest.PREFERENCE_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(GetFitServerRequest.USER_ENTITY, user.asString()).apply();
    }

    public static String getUser() {
        SharedPreferences prefs = GetFitServerRequest.context.getSharedPreferences(
                GetFitServerRequest.PREFERENCE_NAME, Context.MODE_PRIVATE);
        return prefs.getString(GetFitServerRequest.USER_ENTITY, "null");
    }

    public static void resetUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                GetFitServerRequest.PREFERENCE_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(GetFitServerRequest.USER_ENTITY, "null").apply();
    }

    public static void saveFirst(Context context, String flag) {
        SharedPreferences prefs = context.getSharedPreferences(
                GetFitServerRequest.PREFERENCE_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString("first_run", flag).apply();
    }

    public static String getFirst(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                GetFitServerRequest.PREFERENCE_NAME, Context.MODE_PRIVATE);
        return prefs.getString("first_run", "null");
    }
}
