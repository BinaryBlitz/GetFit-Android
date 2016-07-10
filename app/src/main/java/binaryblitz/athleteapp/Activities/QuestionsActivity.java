package binaryblitz.athleteapp.Activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONObject;

import binaryblitz.athleteapp.Abstract.BaseActivity;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.GetFitServerRequest;
import binaryblitz.athleteapp.Server.OnRequestPerformedListener;

public class QuestionsActivity extends BaseActivity {

    String condition;
    int weekly_load;
    String goal;
    String location;
    String home_equipment;
    String gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questionnaire_layout);

        findViewById(R.id.drawer_indicator).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.imageView19).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(((EditText) findViewById(R.id.textView35tt)).getText().toString().isEmpty() ||
                        ((EditText) findViewById(R.id.textView36tt)).getText().toString().isEmpty() ||
                ((TextView) findViewById(R.id.textView25rt)).getText().toString().equals(getString(R.string.not_selected_str)) ||
                        ((TextView) findViewById(R.id.textView35wt)).getText().toString().equals(getString(R.string.not_selected_str)) ||
                        ((TextView) findViewById(R.id.textView35et)).getText().toString().equals(getString(R.string.not_selected_str)) ||
                        ((TextView) findViewById(R.id.textView35at)).getText().toString().equals(getString(R.string.not_selected_str)) ||
                        ((TextView) findViewById(R.id.textView35yt)).getText().toString().equals(getString(R.string.not_selected_str))) {
                    Snackbar.make(findViewById(R.id.main),
                            R.string.filled_error_str, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                JSONObject object = new JSONObject();
                JSONObject user = new JSONObject();

                home_equipment = ((EditText) findViewById(R.id.textView35ttq)).getText().toString();

                try {
                    user.put("condition", condition);
                    user.put("weekly_load", weekly_load);
                    user.put("goal", goal);
                    user.put("location", location);
                    user.put("gender", gender);
                    user.put("home_equipment", home_equipment);
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
                                finish();
                            }
                        })
                        .objects(object)
                        .updateUser()
                        .perform();
            }
        });

        findViewById(R.id.fizs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(QuestionsActivity.this)
                        .title(R.string.select_str)
                        .items(getString(R.string.beg_str), getString(R.string.sporti_str), getString(R.string.athlete_str))
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                condition =  which == 0 ? "beginner" : which == 1 ? "intermediate" : "athlete";
                                ((TextView) findViewById(R.id.textView35wt)).setText(
                                        which == 0 ? getString(R.string.beg_str) : which == 1 ?
                                                getString(R.string.sporti_str) : getString(R.string.athlete_str)
                                );
                            }
                        })
                        .show();
            }
        });

        findViewById(R.id.days).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(QuestionsActivity.this)
                        .title(R.string.select_str)
                        .items("1", "2", "3", "4", "5", "6", "7")
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                weekly_load = which + 1;
                                ((TextView) findViewById(R.id.textView35et)).setText(
                                        Integer.toString(which + 1)
                                );
                            }
                        })
                        .show();
            }
        });
        final String[] itemsServer = {
                "weight_loss", "weight_gain", "drying", "stretching", "stamina", "strength", "comprehensive", "other"
        };

        final String[] items = {
                getString(R.string.weight_loss_str), getString(R.string.weight_gain_str),
                getString(R.string.drying_str), getString(R.string.stretch_str), getString(R.string.stamina_str),
                getString(R.string.power_str), getString(R.string.comp_str), getString(R.string.other_str)
        };

        findViewById(R.id.target).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(QuestionsActivity.this)
                        .title(R.string.select_str)
                        .items(items)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                goal = itemsServer[which];
                                ((TextView) findViewById(R.id.textView35yt)).setText(
                                        items[which]
                                );
                            }
                        })
                        .show();
            }
        });

        findViewById(R.id.sport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(QuestionsActivity.this)
                        .title(R.string.select_str)
                        .items(getString(R.string.gym_str), getString(R.string.plos_str), getString(R.string.home_str))
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                location = which == 0 ? "gym" : which == 1 ? "street" : "home";
                                ((TextView) findViewById(R.id.textView35at)).setText(
                                        which == 0 ? getString(R.string.gym_str) : which == 1 ?
                                                getString(R.string.plos_str) : getString(R.string.home_str)
                                );
                            }
                        })
                        .show();
            }
        });

        findViewById(R.id.gender).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(QuestionsActivity.this)
                        .title(R.string.select_str)
                        .items(getString(R.string.male_str), getString(R.string.female_str))
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                gender = which == 0 ? "male" : "female";
                                ((TextView) findViewById(R.id.textView25rt)).setText(
                                        which == 0 ? getString(R.string.male_str) : getString(R.string.female_str)
                                );
                            }
                        })
                        .show();
            }
        });

    }
}