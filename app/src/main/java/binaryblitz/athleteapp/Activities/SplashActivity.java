package binaryblitz.athleteapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import binaryblitz.athleteapp.Data.MyProgramsSet;
import binaryblitz.athleteapp.Data.SubscriptionsSet;
import binaryblitz.athleteapp.Data.User;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.DeviceInfoStore;
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
                            try {
                                if(objects[0].equals("AuthError") || objects[0].equals("Error")) {
                                    Intent intent = new Intent(SplashActivity.this, NewsActivity.class);
                                    startActivity(intent);
                                    finish();
                                    return;
                                }

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

                                GetFitServerRequest.with(SplashActivity.this)
                                        .authorize()
                                        .listener(new OnRequestPerformedListener() {
                                            @Override
                                            public void onRequestPerformedListener(Object... objects) {
                                                try {
                                                    JSONObject object = (JSONObject) objects[0];
                                                    User user = new User(
                                                            object.getString("id"),
                                                            object.getString("first_name") + " " + object.getString("last_name"),
                                                            object.getString("phone_number"),
                                                            object.getString("description"),
                                                            GetFitServerRequest.imagesUrl + object.getString("avatar_url"),
                                                            GetFitServerRequest.imagesUrl + object.getString("banner_url"),
                                                            object.isNull("weight") ? 0 : object.getInt("weight"),
                                                            object.isNull("height") ? 0 : object.getInt("height"),
                                                            object.isNull("gender") ? true : object.getBoolean("gender"),
                                                            null
                                                    );

                                                    DeviceInfoStore.saveUser(user);

                                                    GetFitServerRequest.with(SplashActivity.this)
                                                            .authorize()
                                                            .listener(new OnRequestPerformedListener() {
                                                                @Override
                                                                public void onRequestPerformedListener(Object... objects) {
                                                                    try {
                                                                        JSONArray array = (JSONArray) objects[0];

                                                                        for(int i = 0; i < array.length(); i++) {
                                                                            JSONObject object = array.getJSONObject(i);

                                                                            MyProgramsSet.load()
                                                                                    .add(object.getString("id"));
                                                                        }

                                                                        Intent intent = new Intent(SplashActivity.this, NewsActivity.class);
                                                                        startActivity(intent);
                                                                        finish();
                                                                    } catch (Exception e) {

                                                                    }
                                                                }
                                                            })
                                                            .myPrograms()
                                                            .perform();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        })
                                        .getUser()
                                        .perform();
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