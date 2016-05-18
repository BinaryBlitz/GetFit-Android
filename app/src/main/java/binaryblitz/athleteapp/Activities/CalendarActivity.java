package binaryblitz.athleteapp.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.SwipeDismissItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.util.ArrayList;
import java.util.Date;

import binaryblitz.athleteapp.Abstract.BaseActivity;
import binaryblitz.athleteapp.Adapters.TrainingsAdapter;
import binaryblitz.athleteapp.CalendarUtils.BasicDecorator;
import binaryblitz.athleteapp.CalendarUtils.CalendarDecorator;
import binaryblitz.athleteapp.CalendarUtils.SelectionDecorator;
import binaryblitz.athleteapp.Data.Professional;
import binaryblitz.athleteapp.Data.ProfessionalType;
import binaryblitz.athleteapp.Data.Training;
import binaryblitz.athleteapp.Data.TrainingPart;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.GetFitServerRequest;
import binaryblitz.athleteapp.Server.OnRequestPerformedListener;
import binaryblitz.athleteapp.Utils.AndroidUtils;
import binaryblitz.athleteapp.Utils.DateTimeUtil;
import binaryblitz.athleteapp.Utils.ResizeAnimation;
import binaryblitz.athleteapp.Utils.ShowHideScrollListener;

public class CalendarActivity extends BaseActivity implements OnDateSelectedListener {
    private RecyclerView.Adapter mAdapter;
    private TrainingsAdapter myItemAdapter;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewSwipeManager mRecyclerViewSwipeManager;
    private RecyclerViewTouchActionGuardManager mRecyclerViewTouchActionGuardManager;
    View fab;

    private static ArrayList<Training> trainings;

    public static int getCount() {
        return trainings.size();
    }

    private static ArrayList<Training> newTraining;

    private static int selected = 0;

    private boolean calendarOpened = false;

    public static boolean FLAG = false;
    public static boolean DONE = false;

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        widget.invalidateDecorators();
        findViewById(R.id.textView16).setBackgroundColor(Color.parseColor("#3695ed"));
    }

    public static void setNewTraining(ArrayList<Training> newTraining) {
        CalendarActivity.newTraining = newTraining;
    }

    @Override
    protected void onResume() {
        super.onResume();

        myItemAdapter.setTrainings(trainings);
        myItemAdapter.notifyDataSetChanged();

        if(FLAG) {
            FLAG = false;
            for(int i = 0; i < newTraining.size(); i++) {
                trainings.add(newTraining.get(i));
            }
            myItemAdapter.notifyDataSetChanged();
            invalidateDates();
        } else if(DONE) {
            DONE = false;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    trainings.remove(TrainingsAdapter.SELECTED);
                    myItemAdapter.notifyItemRemoved(TrainingsAdapter.SELECTED);
                    TrainingsAdapter.SELECTED = 0;
                    invalidateDates();
                }
            }, 100);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_layout);
        init();

        findViewById(R.id.dialog).setVisibility(View.GONE);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        myItemAdapter = new TrainingsAdapter(this);

        mRecyclerViewTouchActionGuardManager = new RecyclerViewTouchActionGuardManager();
        mRecyclerViewTouchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true);
        mRecyclerViewTouchActionGuardManager.setEnabled(true);

        findViewById(R.id.empty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtils.animateRevealHide(findViewById(R.id.dialog));
            }
        });

        // swipe manager
        mRecyclerViewSwipeManager = new RecyclerViewSwipeManager();

        myItemAdapter.setEventListener(new TrainingsAdapter.EventListener() {
            @Override
            public void onItemRemoved(int position) {
                invalidateDates();
            }


            @Override
            public void onItemPinned(final int position) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        selected = position;
                        openCalendarDialog(position);
                    }
                });
            }


            @Override
            public void onItemViewClicked(View v, boolean pinned) {
                Intent intent = new Intent(CalendarActivity.this, TrainingActivity.class);
                startActivity(intent);
            }
        });

        ((MaterialCalendarView) findViewById(R.id.calendarView2)).setTopbarVisible(false);
        ((MaterialCalendarView) findViewById(R.id.calendarView2)).setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);

        ((MaterialCalendarView) findViewById(R.id.calendarView2)).setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                ((TextView) findViewById(R.id.date_text_viewd)).setText(DateTimeUtil.getMonthString(date.getMonth() + 1));
            }
        });

        ((MaterialCalendarView) findViewById(R.id.calendarView2)).setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                ArrayList<Training> trainings1 = new ArrayList<>();
                for (int i = 0; i < trainings.size(); i++) {
                    Date _date = trainings.get(i).getDate();
                    if (_date.equals(new Date(date.getYear(), date.getMonth() + 1, date.getDay()))) {
                        trainings1.add(trainings.get(i));
                    }
                }

                if (trainings1.size() == 0) {
                    Snackbar.make(findViewById(R.id.parent), "No trainings this day", Snackbar.LENGTH_SHORT).show();
                } else {
                    myItemAdapter.setTrainings(trainings1);
                    myItemAdapter.notifyDataSetChanged();
                }

                ((MaterialCalendarView) findViewById(R.id.calendarView2)).invalidateDecorators();
            }
        });

        ((TextView) findViewById(R.id.date_text_viewd)).setText(
                DateTimeUtil.getMonthString(((MaterialCalendarView) findViewById(R.id.calendarView2)).getCurrentDate().getMonth() + 1));

        findViewById(R.id.fab12).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalendarActivity.this, NewTrainingActivity.class);
                NewTrainingActivity.setTrainings(trainings);
                startActivity(intent);
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

        loadActivityData();

        findViewById(R.id.date_text_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (calendarOpened) {
                    ResizeAnimation animation = new ResizeAnimation(findViewById(R.id.calendarView),
                            AndroidUtils.convertDpToPixel(350f, CalendarActivity.this),
                            AndroidUtils.convertDpToPixel(1f, CalendarActivity.this));

                    findViewById(R.id.calendarView).startAnimation(animation);
                    findViewById(R.id.calendarView).invalidate();
                    findViewById(R.id.calendarView).forceLayout();

                    RotateAnimation rotate = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    rotate.setDuration(200);
                    //rotate.
                    rotate.setInterpolator(new LinearInterpolator());

                    final ImageView image = (ImageView) findViewById(R.id.imageView7);

                    rotate.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            image.setRotation(0);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    image.startAnimation(rotate);
                } else {
                    ResizeAnimation animation = new ResizeAnimation(findViewById(R.id.calendarView),
                            AndroidUtils.convertDpToPixel(1f, CalendarActivity.this),
                            AndroidUtils.convertDpToPixel(350f, CalendarActivity.this));

                    findViewById(R.id.calendarView).startAnimation(animation);
                    findViewById(R.id.calendarView).invalidate();
                    findViewById(R.id.calendarView).forceLayout();

                    RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    rotate.setDuration(200);
                    rotate.setInterpolator(new LinearInterpolator());
                    final ImageView image = (ImageView) findViewById(R.id.imageView7);

                    rotate.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            image.setRotation(180);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    image.startAnimation(rotate);
                }

                calendarOpened = !calendarOpened;
            }
        });

        fab = findViewById(R.id.fab12);

        mRecyclerView.addOnScrollListener(new ShowHideScrollListener() {
            @Override
            public void onHide() {
                AndroidUtils.hideFab(fab);
            }

            @Override
            public void onShow() {
                AndroidUtils.showFab(fab);
            }
        });

        findViewById(R.id.textView16).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invalidateCalendar();
            }
        });

        GetFitServerRequest.with(this)
                .authorize()
                .listener(new OnRequestPerformedListener() {
                    @Override
                    public void onRequestPerformedListener(Object... objects) {
                        Log.e("qwerty", objects[0].toString());
                    }
                })
                .workoutSessions()
                .perform();
    }

    public void loadActivityData() {
        trainings = new ArrayList<>();

        ArrayList<TrainingPart> parts = new ArrayList<>();
//
//        parts.add(new TrainingPart("1", "Squats", 0, 50, 5 , 0, "Your legs on your shoulders line. Keep your arms outstretched in front of you. Do not tear off your feet from the ground. Slowly let your pelvis down till your arms reach your knees."));
//        parts.add(new TrainingPart("1", "Power Ups", 0, 30, 4 , 0, "Put your arms on the floor on your shoulders line, same as your legs. Let down your body. Do not let your loin sag. Keep your body straight as an arrow."));
//        parts.add(new TrainingPart("1", "Pull-Ups", 0, 10, 4 , 0, "Jump on the horizontal bar. Your arms on your shoulders line. Pull your body up, till your elbow reaches the bar. Try not to shake your body. Keep it straight. Go down slowly till you fully release you arms."));
//        parts.add(new TrainingPart("1", "Jumping", 0, 20, 4 , 0, "Your legs on your shoulders line. Keep your arms outstretched in front of you. Do not tear off your feet from the ground. Slowly let your pelvis down till your arms reach your knees. Jump out as high as you can. Do not pull your legs up. They just lie down."));
//
//        trainings.add(new Training("1", 1, "Intensive cross-fit training", "Crossfit, Power", 4,
//                new Date(2015, 11, 4), 80, parts,
//                new Professional("1", "photo", null, R.drawable.test4, R.drawable.test9,
//                        "Mike Silvestri", "Cross-fit coach. Your faithful assistant to athletic body in short time.",
//                        ProfessionalType.COACH, false, 8, 32, 4.7),
//                "A complex of 18 exercises to get your body in shape, and achieve great results in stamina and power. Appreciated by more than 1000+ users."));
//
//
//        ArrayList<TrainingPart> parts2 = new ArrayList<>();
//
//        parts2.add(new TrainingPart("1", "Warm-up walk", 0, 0, 0 , 5, "So to warm up your body, walk slowly."));
//        parts2.add(new TrainingPart("1", "Jogging", 0, 0, 0 , 15, "Slowly start to jog. Check out your breathe."));
//        parts2.add(new TrainingPart("1", "Run From Problems", 0, 0, 0 , 30, "Simple run. Speed about 7-9 km/h. Check out your breathe."));
//        parts2.add(new TrainingPart("1", "Speed Ups", 0, 10, 0 , 3, "Speed Up and run as fast as you can. Do not stop after each speed up! Run the way you did before speed up. "));
//        parts2.add(new TrainingPart("1", "Shuttle Run", 0, 2, 0 , 5, "Make two checkpoints on the ground(line, stones, bottles, whatever). Run from one checkpoint to another as fast as you can touching the checkpoint with your arm each time."));
//        parts2.add(new TrainingPart("1", "Run From Problems", 0, 0, 0 , 30, "Simple run. Speed about 7-9 km/h. Check out your breathe. "));
//
//        trainings.add(new Training("1", 2, "Run Forrest, Run!", "Running, Stamina", 6,
//                new Date(2015, 11, 8), 80, parts2,
//                new Professional("1", "photo", null, R.drawable.test5, R.drawable.test10,
//                        "Henry Harrison", "Professional marathoner. The best choice to improve your stamina and get fit.",
//                        ProfessionalType.COACH, false, 12, 68, 4.5),
//                "A set of running programs to maintain your shape and develop stamina. Breath right, run fast and GetFit with other 2000 subscribers."));
//
//        ArrayList<TrainingPart> parts3 = new ArrayList<>();
//
//        parts3.add(new TrainingPart("1", "Warm-up", 0, 0, 0 , 5, "A very simple warm-up complex for 5 minutes."));
//        parts3.add(new TrainingPart("1", "Warm-up run", 0, 0, 0 , 15, "Slowly start to jog. Check out your breathe. Do not try to run fast. Your speed should be about 5-6 km/h."));
//        parts3.add(new TrainingPart("1", "Hyperextension", 0, 10, 4 , 0, "Fix your body on the hyperextension kit. Slowly let your body down till your breast reaches your knees level. Slowly go up. If the exercise is to easy for you, you can take some additional weight in your arms."));
//        parts3.add(new TrainingPart("1", "Squats with weight", 0, 12, 4 , 0, "Your legs on your shoulders line. Put a bar with needed weight on your shoulders and hold it with your arms. Do not tear off your feet from the ground. Slowly let your pelvis down till your arms reach your knees. "));
//
//        trainings.add(new Training("1", 3, "Tinas’ Choice", "Fitness", 4,
//                new Date(2015, 11, 12), 80, parts3,
//                new Professional("1", "photo", null, R.drawable.test6, R.drawable.tina,
//                        "Tina Kandelaki", "Fitness fan. Get the training celebrity uses, with all the features eventually developed.",
//                        ProfessionalType.COACH, false, 5, 188, 4.9),
//                "A training developed by Tina! Get her shape, with all the tips provided by strong woman who uses this set for years!"));
//
//        ArrayList<TrainingPart> parts4 = new ArrayList<>();
//
//        parts4.add(new TrainingPart("1", "Lunges on one leg", 0, 10, 4 , 0, "Slowly lunge on one leg, than on another. Starting position: Legs on your shoulders line. Arms lying down. Put your leg a big step(70 cm) forward and let your pelvis down, putting weight on your forward leg."));
//        parts4.add(new TrainingPart("1", "Cycle on your back", 0, 0, 4 , 2, "Lye on the ground on your back. Arms on the ground on your shoulders line. Put your legs up in the air. Make moves like you are cycling on a bike."));
//        parts4.add(new TrainingPart("1", "Leg pushes", 50, 10, 4, 0, "Take a position in a push kit. Put legs on your shoulders line. Slowly push the weight up, and slowly let it down"));
//
//        trainings.add(new Training("1", 4, "Rehabilitation after knee injury", "Rehabilitation", 3,
//                new Date(2015, 11, 15), 80, parts4,
//                new Professional("1", "photo", null, R.drawable.sports_medicine, R.drawable.arkov,
//                        "Vladimir Arkov", "Ph.D in Medical Sciences, Head of Physiotherapy Department in Moscow Sports Medicine Clinic",
//                        ProfessionalType.DOCTOR, false, 6, 224, 4.9),
//                "This program is a complex of exercises for sportsmen rehabilitation after knee injuries."));

        myItemAdapter.setTrainings(trainings);

        ArrayList<CalendarDay> days3 = new ArrayList<>();
        days3.add(new CalendarDay(2015, 10, 4));
        days3.add(new CalendarDay(2015, 10, 8));
        days3.add(new CalendarDay(2015, 10, 12));
        days3.add(new CalendarDay(2015, 10, 15));
        CalendarDecorator decorator3 = new CalendarDecorator(this, R.drawable.one_training, days3);
        ((MaterialCalendarView) findViewById(R.id.calendarView2)).addDecorators(decorator3);

    }

    public void invalidateDates() {
        ArrayList<CalendarDay> days1 = new ArrayList<>();
        ArrayList<CalendarDay> days2 = new ArrayList<>();
        ArrayList<CalendarDay> days3 = new ArrayList<>();
        ArrayList<Pair<Date, Integer>> decorators = new ArrayList<>();

        main_loop:
        for(int i = 0; i < trainings.size(); i++) {
            Date date = trainings.get(i).getDate();

            for(int j = 0; j < decorators.size(); j++) {
                if(decorators.get(j).first.equals(date)) {
                    decorators.set(j, new Pair<>(date, decorators.get(j).second + 1));
                    continue  main_loop;
                }
            }

            decorators.add(new Pair<>(date, 1));
        }


        for(int i = 0; i < decorators.size(); i++) {
            switch (decorators.get(i).second) {
                case 1:
                    days1.add(new CalendarDay(decorators.get(i).first.getYear(),
                            decorators.get(i).first.getMonth() - 1,
                            decorators.get(i).first.getDate()));
                    break;
                case 2:
                    days2.add(new CalendarDay(decorators.get(i).first.getYear(),
                            decorators.get(i).first.getMonth() - 1,
                            decorators.get(i).first.getDate()));
                    break;
                default:
                    days3.add(new CalendarDay(decorators.get(i).first.getYear(),
                            decorators.get(i).first.getMonth() - 1,
                            decorators.get(i).first.getDate()));
                    break;
            }
        }

        CalendarDecorator decorator1 = new CalendarDecorator(this,
                R.drawable.one_training,
                days1);

        CalendarDecorator decorator2 = new CalendarDecorator(this,
                R.drawable.two_trainings,
                days2);

        CalendarDecorator decorator3 = new CalendarDecorator(this,
                R.drawable.busy_day,
                days3);

        ((MaterialCalendarView) findViewById(R.id.calendarView2)).removeDecorators();
        ((MaterialCalendarView) findViewById(R.id.calendarView2)).addDecorators(decorator1, decorator2, decorator3);
        ((MaterialCalendarView) findViewById(R.id.calendarView2)).invalidateDecorators();
    }

    public void invalidateCalendar() {
        CalendarDay day = ((MaterialCalendarView) findViewById(R.id.calendarView2fd)).getSelectedDate();
        if(day == null) {
            return;
        }
        trainings.get(selected).setDate(new Date(day.getYear(), day.getMonth() + 1, day.getDay()));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                myItemAdapter.notifyItemChanged(selected);
                selected = 0;
            }
        }, 100);
        AndroidUtils.animateRevealHide(findViewById(R.id.dialog));

        ArrayList<CalendarDay> days1 = new ArrayList<>();
        ArrayList<CalendarDay> days2 = new ArrayList<>();
        ArrayList<CalendarDay> days3 = new ArrayList<>();
        ArrayList<Pair<Date, Integer>> decorators = new ArrayList<>();

        main_loop:
        for(int i = 0; i < trainings.size(); i++) {
            Date date = trainings.get(i).getDate();

            for(int j = 0; j < decorators.size(); j++) {
                if(decorators.get(j).first.equals(date)) {
                    decorators.set(j, new Pair<>(date, decorators.get(j).second + 1));
                    continue  main_loop;
                }
            }

            decorators.add(new Pair<>(date, 1));
        }


        for(int i = 0; i < decorators.size(); i++) {
            switch (decorators.get(i).second) {
                case 1:
                    days1.add(new CalendarDay(decorators.get(i).first.getYear(),
                            decorators.get(i).first.getMonth() - 1,
                            decorators.get(i).first.getDate()));
                    break;
                case 2:
                    days2.add(new CalendarDay(decorators.get(i).first.getYear(),
                            decorators.get(i).first.getMonth() - 1,
                            decorators.get(i).first.getDate()));
                    break;
                default:
                    days3.add(new CalendarDay(decorators.get(i).first.getYear(),
                            decorators.get(i).first.getMonth() - 1,
                            decorators.get(i).first.getDate()));
                    break;
            }
        }

        CalendarDecorator decorator1 = new CalendarDecorator(this,
                R.drawable.one_training,
                days1);

        CalendarDecorator decorator2 = new CalendarDecorator(this,
                R.drawable.two_trainings,
                days2);

        CalendarDecorator decorator3 = new CalendarDecorator(this,
                R.drawable.busy_day,
                days3);

        ((MaterialCalendarView) findViewById(R.id.calendarView2fd)).clearSelection();
        ((MaterialCalendarView) findViewById(R.id.calendarView2)).removeDecorators();
        ((MaterialCalendarView) findViewById(R.id.calendarView2)).addDecorators(decorator1, decorator2, decorator3);
        ((MaterialCalendarView) findViewById(R.id.calendarView2)).invalidateDecorators();
    }

    public void openCalendarDialog(int position) {
        findViewById(R.id.textView16).setBackgroundColor(Color.parseColor("#4e4e4e"));
        ((MaterialCalendarView) findViewById(R.id.calendarView2fd)).removeDecorators();

        ArrayList<CalendarDay> days1 = new ArrayList<>();
        ArrayList<CalendarDay> days2 = new ArrayList<>();

        ArrayList<Pair<Date, Integer>> decorators = new ArrayList<>();

        main_loop:
        for(int i = 0; i < trainings.size(); i++) {
            if(i == position) continue;

            Date date = trainings.get(i).getDate();

            for(int j = 0; j < decorators.size(); j++) {
                if(decorators.get(j).first.equals(date)) {
                    decorators.set(j, new Pair<>(date, decorators.get(j).second + 1));
                    continue  main_loop;
                }
            }

            decorators.add(new Pair<>(date, 1));
        }


        for(int i = 0; i < decorators.size(); i++) {
            switch (decorators.get(i).second) {
                case 1:
                    days1.add(new CalendarDay(decorators.get(i).first.getYear(),
                            decorators.get(i).first.getMonth() - 1,
                            decorators.get(i).first.getDate()));
                    break;
                case 2:
                    days2.add(new CalendarDay(decorators.get(i).first.getYear(),
                            decorators.get(i).first.getMonth() - 1,
                            decorators.get(i).first.getDate()));
                    break;
                default:
                    days1.add(new CalendarDay(decorators.get(i).first.getYear(),
                            decorators.get(i).first.getMonth() - 1,
                            decorators.get(i).first.getDate()));
                    break;
            }
        }

        BasicDecorator decorator1 = new BasicDecorator(this,
                R.drawable.selector_one);

        SelectionDecorator decorator2 = new SelectionDecorator(this,
                R.drawable.selector_new_two,
                days1);

        SelectionDecorator decorator3 = new SelectionDecorator(this,
                R.drawable.selector_three,
                days2);

        ((MaterialCalendarView) findViewById(R.id.calendarView2fd)).addDecorators(decorator1, decorator2, decorator3);
        ((MaterialCalendarView) findViewById(R.id.calendarView2fd)).setOnDateChangedListener(this);
        ((MaterialCalendarView) findViewById(R.id.calendarView2fd)).invalidateDecorators();
        AndroidUtils.animateRevealShowFirst(findViewById(R.id.dialog), CalendarActivity.this);
    }
}