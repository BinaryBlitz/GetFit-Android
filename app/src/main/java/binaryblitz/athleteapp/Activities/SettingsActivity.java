package binaryblitz.athleteapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.json.JSONObject;

import binaryblitz.athleteapp.Abstract.BaseActivity;
import binaryblitz.athleteapp.Custom.ProgressDialog;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.DeviceInfoStore;
import binaryblitz.athleteapp.Server.GetFitServerRequest;
import binaryblitz.athleteapp.Server.OnRequestPerformedListener;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        findViewById(R.id.drawer_indicator).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject object = new JSONObject();
                JSONObject user = new JSONObject();

                try {
                    user.put("device_token", JSONObject.NULL);
                    user.put("platform", JSONObject.NULL);
                    object.put("user", user);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                GetFitServerRequest.with(getApplicationContext())
                        .authorize()
                        .listener(new OnRequestPerformedListener() {
                            @Override
                            public void onRequestPerformedListener(Object... objects) {
                                if (objects[0].equals("Internet")) {
                                    cancelRequest();
                                    return;
                                }
                                DeviceInfoStore.resetToken(SettingsActivity.this);
                                DeviceInfoStore.resetUser(SettingsActivity.this);
                                Intent intent = new Intent(SettingsActivity.this, SplashActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .objects(object)
                        .updateUser()
                        .perform();
            }
        });

        ((EditText) findViewById(R.id.editText4)).setText(MyProfileActivity.firstName);
        ((EditText) findViewById(R.id.editText41)).setText(MyProfileActivity.lastName);
        ((EditText) findViewById(R.id.editText43)).setText(MyProfileActivity.description);

        findViewById(R.id.imageView19).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog dialog = new ProgressDialog();
                dialog.show(getFragmentManager(), "atheleteapp");

                JSONObject object = new JSONObject();
                JSONObject toSend = new JSONObject();

                try {
                    object.accumulate("first_name", ((EditText) findViewById(R.id.editText4)).getText().toString());
                    object.accumulate("last_name", ((EditText) findViewById(R.id.editText41)).getText().toString());
                    object.accumulate("description", ((EditText) findViewById(R.id.editText43)).getText().toString());

                    toSend.accumulate("user", object);
                } catch (Exception e) {

                }

                try {
                    MyProfileActivity.description = ((EditText) findViewById(R.id.editText43)).getText().toString();
                    MyProfileActivity.firstName = ((EditText) findViewById(R.id.editText4)).getText().toString();
                    MyProfileActivity.lastName = ((EditText) findViewById(R.id.editText41)).getText().toString();
                } catch (Exception e) {

                }

                GetFitServerRequest.with(SettingsActivity.this)
                        .authorize()
                        .objects(toSend)
                        .listener(new OnRequestPerformedListener() {
                            @Override
                            public void onRequestPerformedListener(Object... objects) {
                                if (objects[0].equals("Internet")) {
                                    cancelRequest();
                                    return;
                                }
                                finish();
                            }
                        })
                        .updateUser()
                        .perform();
            }
        });
    }
}
