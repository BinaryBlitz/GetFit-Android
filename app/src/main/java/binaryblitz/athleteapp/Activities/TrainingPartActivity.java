package binaryblitz.athleteapp.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.exoplayer.util.Util;
import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.squareup.picasso.Picasso;
import com.thefinestartist.ytpa.YouTubePlayerActivity;
import com.thefinestartist.ytpa.enums.Orientation;
import com.thefinestartist.ytpa.utils.YouTubeApp;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

import binaryblitz.athleteapp.Abstract.BaseActivity;
import binaryblitz.athleteapp.Data.TrainingPart;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.GetFitServerRequest;
import binaryblitz.athleteapp.Server.OnRequestPerformedListener;
import binaryblitz.athleteapp.Utils.AndroidUtils;

public class TrainingPartActivity extends BaseActivity {

    private static TrainingPart part;

    public static void setPart(TrainingPart part) {
        TrainingPartActivity.part = part;
    }

    public void getTitleQuietly() {
        GetFitServerRequest.with(this)
                .authorize()
                .listener(new OnRequestPerformedListener() {
                    @Override
                    public void onRequestPerformedListener(Object... objects) {
                        if (objects[0].equals("Internet")) {
                            cancelRequest();
                            return;
                        }
                        try {
                            JSONObject object = (JSONObject) objects[0];

                            ((TextView) findViewById(R.id.textView28)).setText(
                                    object.getJSONArray("items").getJSONObject(0).getJSONObject("snippet").getString("title"));

                            Picasso.with(TrainingPartActivity.this)
                                    .load(object.getJSONArray("items").getJSONObject(0).
                                            getJSONObject("snippet").getJSONObject("thumbnails")
                                            .getJSONObject("medium").getString("url"))
                                    .into((ImageView) findViewById(R.id.imageView13));
                        } catch (Exception e) {
                        }
                    }
                })
                .videoDetails(part.getVideoUrl())
                .perform();

        GetFitServerRequest.with(this)
                .authorize()
                .listener(new OnRequestPerformedListener() {
                    @Override
                    public void onRequestPerformedListener(Object... objects) {
                        if (objects[0].equals("Internet")) {
                            cancelRequest();
                            return;
                        }
                        try {
                            JSONObject object = (JSONObject) objects[0];
                            ((TextView) findViewById(R.id.textView29)).setText(convertDuration(
                                    object.getJSONArray("items").getJSONObject(0).getJSONObject("contentDetails").getString("duration")));
                        } catch (Exception e) {
                        }
                    }
                })
                .duration(part.getVideoUrl())
                .perform();
    }

    public String convertDuration(String duration) {
        duration = duration.substring(2);  // del. PT-symbols
        String H, M, S;
        // Get Hours:
        int indOfH = duration.indexOf("H");  // position of H-symbol
        if (indOfH > -1) {  // there is H-symbol
            H = duration.substring(0, indOfH);      // take number for hours
            duration = duration.substring(indOfH); // del. hours
            duration = duration.replace("H", "");   // del. H-symbol
        } else {
            H = "";
        }
        // Get Minutes:
        int indOfM = duration.indexOf("M");  // position of M-symbol
        if (indOfM > -1) {  // there is M-symbol
            M = duration.substring(0, indOfM);      // take number for minutes
            duration = duration.substring(indOfM); // del. minutes
            duration = duration.replace("M", "");   // del. M-symbol
            // If there was H-symbol and less than 10 minutes
            // then add left "0" to the minutes
            if (H.length() > 0 && M.length() == 1) {
                M = "0" + M;
            }
        } else {
            // If there was H-symbol then set "00" for the minutes
            // otherwise set "0"
            if (H.length() > 0) {
                M = "00";
            } else {
                M = "0";
            }
        }
        // Get Seconds:
        int indOfS = duration.indexOf("S");  // position of S-symbol
        if (indOfS > -1) {  // there is S-symbol
            S = duration.substring(0, indOfS);      // take number for seconds
            duration = duration.substring(indOfS); // del. seconds
            duration = duration.replace("S", "");   // del. S-symbol
            if (S.length() == 1) {
                S = "0" + S;
            }
        } else {
            S = "00";
        }
        if (H.length() > 0) {
            return H + ":" + M + ":" + S;
        } else {
            return M + ":" + S;
        }
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

        getTitleQuietly();

        findViewById(R.id.video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrainingPartActivity.this, YouTubePlayerActivity.class);
                intent.putExtra(YouTubePlayerActivity.EXTRA_VIDEO_ID, part.getVideoUrl());
                intent.putExtra(YouTubePlayerActivity.EXTRA_PLAYER_STYLE, YouTubePlayer.PlayerStyle.DEFAULT);
                intent.putExtra(YouTubePlayerActivity.EXTRA_ORIENTATION, Orientation.AUTO);
                intent.putExtra(YouTubePlayerActivity.EXTRA_SHOW_AUDIO_UI, true);
                intent.putExtra(YouTubePlayerActivity.EXTRA_HANDLE_ERROR, true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        findViewById(R.id.textView16).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (TrainingActivity.parent.getTips() != null) {
                            AndroidUtils.animateRevealShowFirst(findViewById(R.id.dialog), TrainingPartActivity.this);
                        }
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
                        .title(R.string.end_part_str)
                        .content(getString(R.string.are_you_sure_str))
                        .positiveText(getString(R.string.yes_str))
                        .negativeText(getString(R.string.no_str))
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

        if (TrainingActivity.parent.getTips() != null) {
            ArrayList<Pair<String, String>> tips = TrainingActivity.parent.getTips();
            ((LinearLayout) findViewById(R.id.tips)).removeAllViews();
            for (int i = 0; i < tips.size(); i++) {
                View v1 = LayoutInflater.from(this).inflate(R.layout.title_layout, null);
                View v2 = LayoutInflater.from(this).inflate(R.layout.tip_content_layout, null);
                ((TextView) v1.findViewById(R.id.textView30)).setText(tips.get(i).first);
                ((TextView) v2.findViewById(R.id.textView18)).setText(tips.get(i).second);

                ((LinearLayout) findViewById(R.id.tips)).addView(v1);
                ((LinearLayout) findViewById(R.id.tips)).addView(v2);
            }
        }

        ((TextView) findViewById(R.id.date_text_view)).setText(part.getName());

        if (part.getTime() == 0) {
            findViewById(R.id.mins).setVisibility(View.GONE);
        } else {
            findViewById(R.id.mins).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.textView27fdfd)).setText(Integer.toString(part.getTime()));
        }

        if (part.getReps() == 0) {
            findViewById(R.id.reps).setVisibility(View.GONE);
        } else {
            findViewById(R.id.reps).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.textView27dsds)).setText(Integer.toString(part.getReps()));
        }

        if (part.getWeight() == 0) {
            findViewById(R.id.weight).setVisibility(View.GONE);
        } else {
            findViewById(R.id.weight).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.textView27)).setText(Integer.toString(part.getWeight()));
        }

        if (part.getCount() == 0) {
            findViewById(R.id.times).setVisibility(View.GONE);
        } else {
            findViewById(R.id.times).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.textView27hghg)).setText(Integer.toString(part.getCount()));
        }
    }
}