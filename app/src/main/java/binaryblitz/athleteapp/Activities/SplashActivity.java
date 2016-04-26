package binaryblitz.athleteapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.DeviceInfoStore;
import binaryblitz.athleteapp.Server.GetFitServerRequest;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.vk.sdk.VKSdk;

import io.fabric.sdk.android.Fabric;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.splash_layout);

//        if(DeviceInfoStore.getFirst(this).equals("null")) {
//            Intent intent = new Intent(SplashActivity.this, StartActivity.class);
//            startActivity(intent);
//            finish();
//        }

        try {
            GetFitServerRequest.with(this)
                    .authorize();

            Intent intent = new Intent(SplashActivity.this, CalendarActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Intent intent = new Intent(SplashActivity.this, AuthActivity.class);
            startActivity(intent);
            finish();
        }
    }
}