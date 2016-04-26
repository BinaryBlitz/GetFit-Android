package binaryblitz.athleteapp;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.vk.sdk.VKSdk;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(this);
        VKSdk.initialize(this);
    }
}
