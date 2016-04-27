package binaryblitz.athleteapp.Activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import binaryblitz.athleteapp.Abstract.BaseActivity;
import binaryblitz.athleteapp.Data.TrainingPart;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Utils.AndroidUtils;

public class TrainingPartActivity extends BaseActivity {

    private static TrainingPart part;

    public static void setPart(TrainingPart part) {
        TrainingPartActivity.part = part;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.training_part_layout);

        findViewById(R.id.drawer_indicator).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.textView16).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        AndroidUtils.animateRevealShowFirst(findViewById(R.id.dialog), TrainingPartActivity.this);
                    }
                });
            }
        });

        findViewById(R.id.empty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtils.animateRevealHide(findViewById(R.id.dialog));
            }
        });

        ((TextView) findViewById(R.id.textView25)).setText(part.getDesc());

        findViewById(R.id.textView16trtr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialDialog.Builder(TrainingPartActivity.this)
                        .title("End part")
                        .content("Are you sure?")
                        .positiveText("Yes")
                        .negativeText("No")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                TrainingActivity.FLAG = true;
                                finish();
                            }
                        })
                        .show();
            }
        });

        ((TextView) findViewById(R.id.date_text_view)).setText(part.getName());

        if(part.getTime() == 0) {
            findViewById(R.id.mins).setVisibility(View.GONE);
        } else {
            findViewById(R.id.mins).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.textView27fdfd)).setText(Integer.toString(part.getTime()));
        }

        if(part.getReps() == 0) {
            findViewById(R.id.reps).setVisibility(View.GONE);
        } else {
            findViewById(R.id.reps).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.textView27dsds)).setText(Integer.toString(part.getReps()));
        }

        if(part.getWeight() == 0) {
            findViewById(R.id.weight).setVisibility(View.GONE);
        } else {
            findViewById(R.id.weight).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.textView27)).setText(Integer.toString(part.getWeight()));
        }

        if(part.getCount() == 0) {
            findViewById(R.id.times).setVisibility(View.GONE);
        } else {
            findViewById(R.id.times).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.textView27hghg)).setText(Integer.toString(part.getCount()));
        }
    }
}