package binaryblitz.athleteapp.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.json.JSONObject;

import binaryblitz.athleteapp.Abstract.BaseActivity;
import binaryblitz.athleteapp.Custom.ProgressDialog;
import binaryblitz.athleteapp.R;
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
                                finish();
                            }
                        })
                        .updateUser()
                        .perform();
            }
        });
    }
}
