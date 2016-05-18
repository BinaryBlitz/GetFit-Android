package binaryblitz.athleteapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import org.json.JSONArray;
import org.json.JSONObject;

import binaryblitz.athleteapp.Data.SubscriptionsSet;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.GetFitServerRequest;
import binaryblitz.athleteapp.Server.OnRequestPerformedListener;
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
                    .authorize()
                    .listener(new OnRequestPerformedListener() {
                        @Override
                        public void onRequestPerformedListener(Object... objects) {
                            Log.e("qwerty", objects[0].toString());
                            try {
                                JSONArray array = (JSONArray) objects[0];

                                for(int i = 0; i < array.length(); i++) {
                                    JSONObject object = array.getJSONObject(i);

                                    SubscriptionsSet
                                            .load()
                                            .add(new SubscriptionsSet.Subscription(
                                                    object.getString("id"),
                                                    object.getString("trainer_id")
                                            ));
                                }

                                Intent intent = new Intent(SplashActivity.this, CalendarActivity.class);
                                startActivity(intent);
                                finish();
                            } catch (Exception e) {

                            }
                        }
                    })
                    .subscriptions()
                    .perform();
        } catch (Exception e) {
            Intent intent = new Intent(SplashActivity.this, AuthActivity.class);
            startActivity(intent);
            finish();
        }
    }
}