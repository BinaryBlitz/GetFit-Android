package binaryblitz.athleteapp.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONObject;

import binaryblitz.athleteapp.Abstract.BaseActivity;
import binaryblitz.athleteapp.Custom.ProgressDialog;
import binaryblitz.athleteapp.Push.QuickstartPreferences;
import binaryblitz.athleteapp.Push.RegistrationIntentService;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.GetFitServerRequest;
import binaryblitz.athleteapp.Server.OnRequestPerformedListener;

public class CreateAccountActivity extends BaseActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account_layout);

        final ProgressDialog dialog1 = new ProgressDialog();

        ((EditText) findViewById(R.id.phon2e)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if ((!((EditText) findViewById(R.id.phon2e)).getText().toString().isEmpty()) &&
                        (!((EditText) findViewById(R.id.phon2e2)).getText().toString().isEmpty())) {
                    findViewById(R.id.textView16).setBackgroundColor(Color.parseColor("#3695ed"));
                    findViewById(R.id.textView16).setClickable(true);
                } else {
                    findViewById(R.id.textView16).setBackgroundColor(Color.parseColor("#7e848c"));
                    findViewById(R.id.textView16).setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ((EditText) findViewById(R.id.phon2e2)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if ((!((EditText) findViewById(R.id.phon2e)).getText().toString().isEmpty()) &&
                        (!((EditText) findViewById(R.id.phon2e2)).getText().toString().isEmpty())) {
                    findViewById(R.id.textView16).setBackgroundColor(Color.parseColor("#3695ed"));
                    findViewById(R.id.textView16).setClickable(true);
                } else {
                    findViewById(R.id.textView16).setBackgroundColor(Color.parseColor("#7e848c"));
                    findViewById(R.id.textView16).setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        findViewById(R.id.textView16).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((!((EditText) findViewById(R.id.phon2e)).getText().toString().isEmpty()) &&
                        (!((EditText) findViewById(R.id.phon2e2)).getText().toString().isEmpty())) {
                    ProgressDialog dialog = new ProgressDialog();
                    dialog.show(getFragmentManager(), "getfitapp");

                    JSONObject object = new JSONObject();
                    try {
                        object.accumulate("phone_number", getIntent().getStringExtra("phone"));
                        object.accumulate("verification_token", PhoneActivity.token);

                        object.accumulate("first_name", ((EditText) findViewById(R.id.phon2e)).getText().toString());
                        object.accumulate("last_name", ((EditText) findViewById(R.id.phon2e2)).getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    JSONObject toSend = new JSONObject();
                    try {
                        toSend.accumulate("user", object);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    GetFitServerRequest.with(CreateAccountActivity.this)
                            .skipAuth()
                            .objects(toSend)
                            .listener(new OnRequestPerformedListener() {
                                @Override
                                public void onRequestPerformedListener(Object... objects) {
                                    if (objects[0].equals("Internet")) {
                                        cancelRequest();
                                        return;
                                    }
                                    if (objects[0].equals("Error")) {
                                        Snackbar.make(findViewById(R.id.main), R.string.error_try_str, Snackbar.LENGTH_SHORT).show();
                                        return;
                                    }
                                    if (checkPlayServices()) {
                                        Intent intent = new Intent(CreateAccountActivity.this, RegistrationIntentService.class);
                                        startService(intent);
                                        dialog1.show(getFragmentManager(), "getfitapp");
                                    } else {
                                        Intent intent2 = new Intent(CreateAccountActivity.this, SplashActivity.class);
                                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent2);
                                        finish();
                                    }
                                }
                            })
                            .createUser()
                            .perform();
                }
            }
        });

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                dialog1.dismiss();
                Intent intent2 = new Intent(CreateAccountActivity.this, SplashActivity.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent2);
                finish();
            }
        };
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
                finish();
            }
            return false;
        }
        return true;
    }
}