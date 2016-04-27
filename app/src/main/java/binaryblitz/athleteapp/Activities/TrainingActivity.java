package binaryblitz.athleteapp.Activities;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.SwipeDismissItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;

import java.util.ArrayList;

import binaryblitz.athleteapp.Abstract.BaseActivity;
import binaryblitz.athleteapp.Adapters.TrainingPartsAdapter;
import binaryblitz.athleteapp.Data.Training;
import binaryblitz.athleteapp.Data.TrainingPart;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Utils.AndroidUtils;

public class TrainingActivity extends BaseActivity {
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewSwipeManager mRecyclerViewSwipeManager;
    private RecyclerViewTouchActionGuardManager mRecyclerViewTouchActionGuardManager;

    private static Training parent;
    private static ArrayList<TrainingPart> parts;
    private TrainingPartsAdapter myItemAdapter;

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
            trainings.add(new Pair<>(4 + i, new Pair<>(TrainingPartsAdapter.BASIC, (Object)parts.get(i))));
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
                AndroidUtils.animateRevealHide(findViewById(R.id.dialog));
            }
        });

        findViewById(R.id.textView23).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(TrainingActivity.this)
                        .title("End part")
                        .content("Are you sure?")
                        .positiveText("Yes")
                        .negativeText("No")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                CalendarActivity.DONE = true;
                                finish();
                            }
                        })
                        .show();
            }
        });

        loadCollection();

        ((TextView) findViewById(R.id.date_text_viewd)).setText(parent.getName());
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
                AndroidUtils.animateRevealShowFirst(findViewById(R.id.dialog), TrainingActivity.this);
            }
        });
    }

    public void show(final DialogFinishedListener listener, int current) {

        final Dialog d = new Dialog(TrainingActivity.this);
        d.setTitle("Select Value");
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