package binaryblitz.athleteapp.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import binaryblitz.athleteapp.Abstract.BaseActivity;
import binaryblitz.athleteapp.Data.Program;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.GetFitServerRequest;
import binaryblitz.athleteapp.Server.OnRequestPerformedListener;

public class ProgramActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.program_layout);

        findViewById(R.id.drawer_indicator).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        GetFitServerRequest.with(this)
                .authorize()
                .listener(new OnRequestPerformedListener() {
                    @Override
                    public void onRequestPerformedListener(Object... objects) {
                        try {
                            JSONObject object = (JSONObject) objects[0];

                            ((TextView) findViewById(R.id.textView2)).setText(object.getString("description"));

                            ((TextView) findViewById(R.id.textView3)).setText(
                                    object.isNull("rating") ? "0" : object.getString("rating"));
                            ((TextView) findViewById(R.id.textView4)).setText(object.getString("users_count"));
                            ((TextView) findViewById(R.id.textView6)).setText(object.getString("price")+ "$");
                            ((TextView) findViewById(R.id.textView)).setText(object.getString("name"));

                            ((TextView) findViewById(R.id.textView7)).setText(object.getJSONObject("program_type").getString("name"));

                            ((TextView) findViewById(R.id.textView8)).setText((object.getJSONArray("workouts")).length() + " workouts");

                            ((TextView) findViewById(R.id.textViewfdfd)).setText(
                                    object.getJSONObject("trainer").getString("first_name") + " " +
                                            object.getJSONObject("trainer").getString("last_name"));

                            Picasso.with(ProgramActivity.this)
                                    .load(GetFitServerRequest.imagesUrl + object.getString("banner_url"))
                                    .into((ImageView) findViewById(R.id.imageView2));

                            Picasso.with(ProgramActivity.this)
                                    .load(GetFitServerRequest.imagesUrl + object.getJSONObject("trainer").getString("avatar_url"))
                                    .into((ImageView) findViewById(R.id.imageView));

                            JSONArray array = object.getJSONArray("workouts");

                            for(int i = 0; i < array.length(); i++) {
                                View v = LayoutInflater.
                                        from(ProgramActivity.this)
                                        .inflate(R.layout.workout_card,
                                                null);

                                ((TextView) v.findViewById(R.id.textView42)).setText(array.getJSONObject(i).getString("duration"));

                                ((LinearLayout) findViewById(R.id.container)).addView(v);

                                JSONArray array1 = array.getJSONObject(i).getJSONArray("exercises");

                                for(int j = 0; j < array1.length(); j++) {
                                    View v1 = LayoutInflater.
                                            from(ProgramActivity.this)
                                            .inflate(R.layout.workout_part_card,
                                                    null);

                                    ((TextView) v1.findViewById(R.id.textView22)).setText(
                                            array1.getJSONObject(j).getJSONObject("exercise_type").getString("name"));

                                    ((LinearLayout) findViewById(R.id.container)).addView(v1);
                                }
                            }

                            View v = LayoutInflater.
                                    from(ProgramActivity.this)
                                    .inflate(R.layout.unlock_btn,
                                            null);

                            v.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });

                            ((LinearLayout) findViewById(R.id.container)).addView(v);
                        } catch (Exception e) {
                            Log.e("qwerty", e.getLocalizedMessage());
                        }
                    }
                })
                .program(getIntent().getStringExtra("id"))
                .perform();
    }
}