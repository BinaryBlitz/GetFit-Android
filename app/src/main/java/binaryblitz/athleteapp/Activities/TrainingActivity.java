package binaryblitz.athleteapp.Activities;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.SwipeDismissItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import binaryblitz.athleteapp.Abstract.BaseActivity;
import binaryblitz.athleteapp.Adapters.TrainingPartsAdapter;
import binaryblitz.athleteapp.Custom.ProgressDialog;
import binaryblitz.athleteapp.Data.Training;
import binaryblitz.athleteapp.Data.TrainingPart;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.GetFitServerRequest;
import binaryblitz.athleteapp.Server.OnRequestPerformedListener;
import binaryblitz.athleteapp.Utils.AndroidUtils;

public class TrainingActivity extends BaseActivity {
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewSwipeManager mRecyclerViewSwipeManager;
    private RecyclerViewTouchActionGuardManager mRecyclerViewTouchActionGuardManager;

    public static Training parent;
    private static ArrayList<TrainingPart> parts;
    private TrainingPartsAdapter myItemAdapter;

    public static String trainerName = "";
    public static String trainerId = "";
    public static String desc = "";
    public static String trainerAvatarUrl = "";

    public static boolean FLAG = false;

    public static void setParts(ArrayList<TrainingPart> parts) {
        TrainingActivity.parts = parts;
    }

    public static void setParent(Training parent) {
        TrainingActivity.parent = parent;
    }

    public void loadCollection() {
        ArrayList<Pair<Integer, Pair<Integer, Object>>> trainings = new ArrayList<>();

        trainings.add(new Pair<>(0, new Pair<>(TrainingPartsAdapter.HEADER, null)));
        trainings.add(new Pair<>(1, new Pair<>(TrainingPartsAdapter.SPACE, null)));
        trainings.add(new Pair<>(3, new Pair<>(TrainingPartsAdapter.SPACE, null)));

        for(int i = 0; i < parts.size(); i++) {
            if(parts.get(i).isCompleted()) {
                trainings.add(new Pair<>(4 + i, new Pair<>(TrainingPartsAdapter.DONE, (Object) parts.get(i))));
            }
        }

        trainings.add(new Pair<>(3, new Pair<>(TrainingPartsAdapter.SPACE, null)));

        for(int i = 0; i < parts.size(); i++) {
            if(!parts.get(i).isCompleted()) {
                trainings.add(new Pair<>(4 + i, new Pair<>(TrainingPartsAdapter.BASIC, (Object) parts.get(i))));
            }
        }

        trainings.add(new Pair<>(4 + parts.size(), new Pair<>(TrainingPartsAdapter.FOOTER, null)));

        myItemAdapter.setTrainings(trainings);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.training_layout);

        findViewById(R.id.drawer_indicator).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        parts = new ArrayList<>();

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        myItemAdapter = new TrainingPartsAdapter(this, parent);

        mRecyclerViewTouchActionGuardManager = new RecyclerViewTouchActionGuardManager();
        mRecyclerViewTouchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true);
        mRecyclerViewTouchActionGuardManager.setEnabled(true);

        // swipe manager
        mRecyclerViewSwipeManager = new RecyclerViewSwipeManager();

        myItemAdapter.setEventListener(new TrainingPartsAdapter.EventListener() {
            @Override
            public void onItemRemoved(int position) {

            }


            @Override
            public void onItemPinned(int position) {
            }


            @Override
            public void onItemViewClicked(View v, boolean pinned) {
                //onItemViewClick(v, pinned);
            }
        });

        mAdapter = myItemAdapter;


        mWrappedAdapter = mRecyclerViewSwipeManager.createWrappedAdapter(myItemAdapter);      // wrap for swiping


        final GeneralItemAnimator animator = new SwipeDismissItemAnimator();

        animator.setSupportsChangeAnimations(true);

        mRecyclerView.setAdapter(mWrappedAdapter);  // requires *wrapped* adapter
        mRecyclerView.setItemAnimator(animator);

        mRecyclerViewTouchActionGuardManager.attachRecyclerView(mRecyclerView);
        mRecyclerViewSwipeManager.attachRecyclerView(mRecyclerView);

        findViewById(R.id.dialog).setVisibility(View.GONE);

        findViewById(R.id.empty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(parent.getTips() != null) {
                    AndroidUtils.animateRevealHide(findViewById(R.id.dialog));
                }
            }
        });

        findViewById(R.id.textView23).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(TrainingActivity.this)
                        .title(R.string.end_part_str)
                        .content(getString(R.string.are_you_sure_str))
                        .positiveText(getString(R.string.yes_str))
                        .negativeText(getString(R.string.no_str))
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                CalendarActivity.DONE = true;
                                final ProgressDialog dialog1 = new ProgressDialog();
                                dialog1.show(getFragmentManager(), "atheleteapp");
                                JSONObject object = new JSONObject();
                                JSONObject toSend = new JSONObject();

                                try {
                                    object.accumulate("completed", true);
                                    toSend.accumulate("workout_session", object);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                GetFitServerRequest.with(TrainingActivity.this)
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
                                        .completeWorkout(getIntent().getStringExtra("id"))
                                        .perform();
                            }
                        })
                        .show();
            }
        });

        loadCollection();

        GetFitServerRequest.with(this)
                .authorize()
                .listener(new OnRequestPerformedListener() {
                    @Override
                    public void onRequestPerformedListener(Object... objects) {
                        try {
                            if (objects[0].equals("Internet")) {
                                cancelRequest();
                                return;
                            }
                            JSONArray array = (JSONArray) objects[0];

                            for(int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);

                                parts.add(new TrainingPart(
                                        object.getString("id"),
                                        object.getJSONObject("exercise").getJSONObject("exercise_type").getString("name"),
                                        object.isNull("weight") ? 0 :
                                                object.getInt("weight"),
                                        object.isNull("sets") ? 0 :
                                                object.getInt("sets"),
                                        object.isNull("reps") ? 0 :
                                                object.getInt("reps"),
                                        object.isNull("distance") ? 0 :
                                                object.getInt("distance"),
                                        object.getJSONObject("exercise").getJSONObject("exercise_type").getString("description"),
                                        object.getBoolean("completed"),
                                        i,
                                        object.getJSONObject("exercise").getJSONObject("exercise_type").getString("video_url")
                                ));
                            }

                            loadCollection();
                        } catch (Exception e) {
                        }
                    }
                })
                .exercises(getIntent().getStringExtra("id"))
                .perform();

        GetFitServerRequest.with(this)
                .authorize()
                .listener(new OnRequestPerformedListener() {
                    @Override
                    public void onRequestPerformedListener(Object... objects) {
                        try {
                            if (objects[0].equals("Internet")) {
                                cancelRequest();
                                return;
                            }
                            JSONObject object = (JSONObject) objects[0];

                            desc = object.getString("description");
                            trainerName = object.getJSONObject("trainer").getString("first_name") + " " +
                                            object.getJSONObject("trainer").getString("last_name");

                            trainerAvatarUrl = GetFitServerRequest.imagesUrl + object.getJSONObject("trainer").getString("avatar_url");
                            trainerId = object.getJSONObject("trainer").getString("id");

                            myItemAdapter.notifyItemChanged(0);
                        } catch (Exception e) {
                        }
                    }
                })
                .program(getIntent().getStringExtra("programId"))
                .perform();

        if(parent.getTips() != null) {
            ArrayList<Pair<String, String>> tips = parent.getTips();
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
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(FLAG) {
            FLAG = false;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    myItemAdapter.doneTraining(TrainingPartsAdapter.SELECTED);
                }
            }, 100);
        }
    }

    public void setPercents(int percent) {
        ((TextView) findViewById(R.id.textView24)).setText(percent + " %");
    }

    public void animateTip() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if(parent.getTips() != null) {
                    AndroidUtils.animateRevealShowFirst(findViewById(R.id.dialog), TrainingActivity.this);
                }
            }
        });
    }

    public void show(final DialogFinishedListener listener, int current) {

        final Dialog d = new Dialog(TrainingActivity.this);
        d.setTitle(R.string.select_val_str);
        d.setContentView(R.layout.dialog);
        View b1 = d.findViewById(R.id.button1);
        View b2 = d.findViewById(R.id.button2);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue(500);
        np.setValue(current);
        np.setMinValue(0);
        np.setWrapSelectorWheel(false);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDialogFinished(np.getValue());
                d.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();
    }

    public interface DialogFinishedListener {
        void onDialogFinished(int val);
    }
}