package binaryblitz.athleteapp.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import org.json.JSONObject;

import java.util.Arrays;

import binaryblitz.athleteapp.Custom.ProgressDialog;
import binaryblitz.athleteapp.Push.QuickstartPreferences;
import binaryblitz.athleteapp.Push.RegistrationIntentService;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.DeviceInfoStore;
import binaryblitz.athleteapp.Server.GetFitServerRequest;
import binaryblitz.athleteapp.Server.OnRequestPerformedListener;

public class AuthActivity extends AppCompatActivity {

    static CallbackManager callbackManager;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressDialog dialog1;

    private static final String[] sMyScope = new String[]{
            VKScope.FRIENDS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_layout);

        callbackManager = CallbackManager.Factory.create();

        dialog1 = new ProgressDialog();

        findViewById(R.id.textView34).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AuthActivity.this, PhoneActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.textView32).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(AuthActivity.this, Arrays.asList("public_profile", "user_friends"));
            }
        });

        findViewById(R.id.textView33).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VKSdk.login(AuthActivity.this, sMyScope);
            }
        });

        LoginManager.getInstance().registerCallback(AuthActivity.callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e("qwerty", "success");

                final ProgressDialog dialog = new ProgressDialog();
                dialog.show(getFragmentManager(), "getfitapp");

                GetFitServerRequest.with(AuthActivity.this)
                        .skipAuth()
                        .listener(new OnRequestPerformedListener() {
                            @Override
                            public void onRequestPerformedListener(Object... objects) {
                                dialog.dismiss();
                                Log.e("qwerty", objects[0].toString());
                                if (objects[0].equals("Internet")) {
                                    return;
                                }
                                if (objects[0].equals("Error")) {
                                    Snackbar.make(findViewById(R.id.main), R.string.error_try_str, Snackbar.LENGTH_SHORT).show();
                                    return;
                                }

                                try {
                                    JSONObject object = (JSONObject) objects[0];

                                    DeviceInfoStore.saveToken(object.getString("api_token"));

                                    if (checkPlayServices()) {
                                        Intent intent = new Intent(AuthActivity.this, RegistrationIntentService.class);
                                        startService(intent);
                                        dialog1.show(getFragmentManager(), "getfitapp");
                                    } else {
                                        Intent intent2 = new Intent(AuthActivity.this, SplashActivity.class);
                                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent2);
                                        finish();
                                    }
                                } catch (Exception ignored) {}
                            }
                        })
                        .fbAuth(loginResult.getAccessToken().getToken())
                        .perform();

            }

            @Override
            public void onCancel() {
                Log.e("qwerty", "cancel");
            }

            @Override
            public void onError(FacebookException e) {
                Snackbar
                        .make(findViewById(R.id.main), "Error.", Snackbar.LENGTH_LONG)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            }
                        })
                        .show();
            }
        });

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                dialog1.dismiss();
                Intent intent2 = new Intent(AuthActivity.this, SplashActivity.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent2);
                finish();
            }
        };
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                Log.e("werty", res.accessToken);

                final ProgressDialog dialog = new ProgressDialog();
                dialog.show(getFragmentManager(), "getfitapp");

                GetFitServerRequest.with(AuthActivity.this)
                        .skipAuth()
                        .listener(new OnRequestPerformedListener() {
                            @Override
                            public void onRequestPerformedListener(Object... objects) {
                                Log.e("qwerty", objects[0].toString());
                                dialog.dismiss();
                                if (objects[0].equals("Internet")) {
                                    return;
                                }
                                if (objects[0].equals("Error")) {
                                    Snackbar.make(findViewById(R.id.main), R.string.error_try_str, Snackbar.LENGTH_SHORT).show();
                                    return;
                                }

                                try {
                                    JSONObject object = (JSONObject) objects[0];

                                    DeviceInfoStore.saveToken(object.getString("api_token"));

                                    if (checkPlayServices()) {
                                        Intent intent = new Intent(AuthActivity.this, RegistrationIntentService.class);
                                        startService(intent);
                                        dialog1.show(getFragmentManager(), "getfitapp");
                                    } else {
                                        Intent intent2 = new Intent(AuthActivity.this, SplashActivity.class);
                                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent2);
                                        finish();
                                    }
                                } catch (Exception ignored) {
                                }
                            }
                        })
                        .vkAuth(res.accessToken)
                        .perform();
            }

            @Override
            public void onError(VKError error) {
                Snackbar
                        .make(findViewById(R.id.main), "Error.", Snackbar.LENGTH_LONG)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            }
                        })
                        .show();
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

}