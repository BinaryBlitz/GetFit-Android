package binaryblitz.athleteapp.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import binaryblitz.athleteapp.Abstract.BaseActivity;
import binaryblitz.athleteapp.Adapters.TrainingsAdapter;
import binaryblitz.athleteapp.CalendarUtils.BasicDecorator;
import binaryblitz.athleteapp.CalendarUtils.CalendarDecorator;
import binaryblitz.athleteapp.CalendarUtils.SelectionDecorator;
import binaryblitz.athleteapp.CalendarUtils.TodayDecorator;
import binaryblitz.athleteapp.Data.Training;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.GetFitServerRequest;
import binaryblitz.athleteapp.Server.OnRequestPerformedListener;
import binaryblitz.athleteapp.Utils.AndroidUtils;
import binaryblitz.athleteapp.Utils.DateTimeUtil;
import binaryblitz.athleteapp.Utils.ResizeAnimation;
import binaryblitz.athleteapp.Utils.ShowHideScrollListener;

public class CalendarActivity extends BaseActivity implements OnDateSelectedListener, SwipeRefreshLayout.OnRefreshListener {
    private TrainingsAdapter myItemAdapter;
    View fab;
    private SwipeRefreshLayout layout;
    ArrayList<CalendarDay> days1;
    ArrayList<CalendarDay> days2;
    ArrayList<CalendarDay> days3;

    private static ArrayList<Training> trainings;

    @Override
    public void onRefresh() {
        loadActivityData();
    }

    public static class Workout {
        CalendarDay day;
        int count;

        public Workout(CalendarDay day, int count) {
            this.day = day;
            this.count = count;
        }
    }

    public static int getCount() {
        return trainings.size();
    }

    private static int selected = 0;

    private boolean calendarOpened = false;

    public static boolean FLAG = false;
    public static boolean DONE = false;

    public static boolean reload = false;

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        widget.invalidateDecorators();
        findViewById(R.id.textView16).setBackgroundColor(Color.parseColor("#3695ed"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_layout);
        init();

        findViewById(R.id.dialog).setVisibility(View.GONE);
        days1 = new ArrayList<>();
        days2 = new ArrayList<>();
        days3 = new ArrayList<>();
    }

    @Override
    protected void onStart() {
        super.onStart();

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        myItemAdapter = new TrainingsAdapter(this);

        RecyclerViewTouchActionGuardManager mRecyclerViewTouchActionGuardManager = new RecyclerViewTouchActionGuardManager();
        mRecyclerViewTouchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true);
        mRecyclerViewTouchActionGuardManager.setEnabled(true);

        findViewById(R.id.empty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtils.animateRevealHide(findViewById(R.id.dialog));
            }
        });

        // swipe manager
        RecyclerViewSwipeManager mRecyclerViewSwipeManager = new RecyclerViewSwipeManager();

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
                        openCalendarDialog();
                    }
                });
            }


            @Override
            public void onItemViewClicked(View v, boolean pinned) {
                Intent intent = new Intent(CalendarActivity.this, TrainingActivity.class);
                intent.putExtra("id", "1");
                startActivity(intent);
            }
        });

        ((MaterialCalendarView) findViewById(R.id.calendarView2)).setTopbarVisible(false);
        ((MaterialCalendarView) findViewById(R.id.calendarView2)).setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);

        ((MaterialCalendarView) findViewById(R.id.calendarView2)).setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                ((TextView) findViewById(R.id.date_text_viewd)).setText(DateTimeUtil.getMonthString(CalendarActivity.this, date.getMonth() + 1));
            }
        });

        ((MaterialCalendarView) findViewById(R.id.calendarView2)).setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                ArrayList<Training> trainings1 = new ArrayList<>();
                for (int i = 0; i < trainings.size(); i++) {
                    Calendar _date = trainings.get(i).getDate();
                    if (_date.get(Calendar.YEAR) == date.getCalendar().get(Calendar.YEAR) &&
                            _date.get(Calendar.MONTH) == date.getCalendar().get(Calendar.MONTH) &&
                            _date.get(Calendar.DAY_OF_MONTH) == date.getCalendar().get(Calendar.DAY_OF_MONTH)) {
                        trainings1.add(trainings.get(i));
                    }
                }

                if (trainings1.size() == 0) {
                    Snackbar.make(findViewById(R.id.parent), R.string.no_trainings_code_str, Snackbar.LENGTH_SHORT).show();
                } else {
                    myItemAdapter.setTrainings(trainings1);
                    myItemAdapter.notifyDataSetChanged();
                }

                ((MaterialCalendarView) findViewById(R.id.calendarView2)).invalidateDecorators();
            }
        });

        ((TextView) findViewById(R.id.date_text_viewd)).setText(
                DateTimeUtil.getMonthString(CalendarActivity.this, ((MaterialCalendarView) findViewById(R.id.calendarView2)).getCurrentDate().getMonth() + 1));

        findViewById(R.id.fab12).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalendarActivity.this, NewTrainingActivity.class);
                NewTrainingActivity.setTrainings(trainings);
                startActivity(intent);
            }
        });

        RecyclerView.Adapter mWrappedAdapter = mRecyclerViewSwipeManager.createWrappedAdapter(myItemAdapter);

        final GeneralItemAnimator animator = new SwipeDismissItemAnimator();

        animator.setSupportsChangeAnimations(true);

        mRecyclerView.setAdapter(mWrappedAdapter);
        mRecyclerView.setItemAnimator(animator);

        mRecyclerViewTouchActionGuardManager.attachRecyclerView(mRecyclerView);
        mRecyclerViewSwipeManager.attachRecyclerView(mRecyclerView);

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

        layout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        layout.setOnRefreshListener(this);
        layout.setColorSchemeResources(R.color.accent_color);

        layout.setRefreshing(true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadActivityData();
            }
        }, 150);
    }

    public void loadActivityData() {
        trainings = new ArrayList<>();

        GetFitServerRequest.with(this)
                .authorize()
                .listener(new OnRequestPerformedListener() {
                    @Override
                    public void onRequestPerformedListener(Object... objects) {
                        try {
                            layout.setRefreshing(false);

                            if(objects[0].equals("AuthError")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!isFinishing()) {
                                            new AlertDialog.Builder(CalendarActivity.this)
                                                    .setTitle(getString(R.string.title_str))
                                                    .setMessage(getString(R.string.reg_alert_str))
                                                    .setCancelable(false)
                                                    .setPositiveButton(getString(R.string.cont_upcase_str), new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            Intent intent = new Intent(CalendarActivity.this, AuthActivity.class);
                                                            startActivity(intent);
                                                        }
                                                    })
                                                    .setNegativeButton(getString(R.string.cancel_upcase_str), new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    })
                                                    .show();
                                        }
                                    }
                                });

                                return;
                            }

                            if (objects[0].equals("Internet")) {
                                cancelRequest();
                                return;
                            }

                            JSONArray array = (JSONArray) objects[0];

                            for(int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);

                                Calendar start = Calendar.getInstance();

                                try {
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                                    format.setTimeZone(TimeZone.getTimeZone("UTC"));
                                    Date date = format.parse(object.getString("scheduled_for"));
                                    start.setTime(date);
                                } catch (Exception ignored) {}

                                ArrayList<Pair<String, String>> tips = new ArrayList<>();

                                if(object.getJSONObject("workout").isNull("tips")) {
                                    tips = null;
                                } else {

                                    String tip = object.getJSONObject("workout").getString("tips");

                                    String[] arr = tip.split("\r\n");
                                    ArrayList<String> list = new ArrayList<>();
                                    for (String anArr : arr) {
                                        if (anArr.isEmpty()) {
                                            continue;
                                        }

                                        list.add(anArr);
                                    }

                                    for (int j = 0; j < list.size(); j += 2) {
                                        tips.add(new Pair<>(list.get(j), list.get(j + 1)));
                                    }
                                }

                                trainings.add(new Training(
                                        object.getString("id"),
                                        object.getJSONObject("workout").getString("name"),
                                        object.getJSONObject("workout").getJSONObject("program").getString("name"),
                                        object.getJSONObject("workout").getInt("exercises_count"),
                                        start,
                                        object.getJSONObject("workout").getInt("duration"),
                                        "",
                                        object.getJSONObject("workout").getJSONObject("program").getString("id"),
                                        tips
                                ));
                            }

                            Collections.sort(trainings, new Comparator<Training>() {
                                @Override
                                public int compare(Training lhs, Training rhs) {
                                    if(lhs.getDate().after(rhs.getDate())) {
                                        return -1;
                                    } else {
                                        return 1;
                                    }
                                }
                            });

                            myItemAdapter.setTrainings(trainings);
                            myItemAdapter.notifyDataSetChanged();

                            ArrayList<Workout> dates = new ArrayList<>();

                            for(int i = 0; i < trainings.size(); i++) {
                                if(dates.size() == 0) {
                                    dates.add(new Workout(new CalendarDay(trainings.get(i).getDate().get(Calendar.YEAR),
                                            trainings.get(i).getDate().get(Calendar.MONTH),
                                            trainings.get(i).getDate().get(Calendar.DAY_OF_MONTH)), 1));
                                    continue;
                                }

                                for(int j = 0; j < dates.size(); j++) {
                                    if(dates.get(j).day.getDay() ==  trainings.get(i).getDate().get(Calendar.DAY_OF_MONTH) &&
                                            dates.get(j).day.getMonth() ==  trainings.get(i).getDate().get(Calendar.MONTH) &&
                                            dates.get(j).day.getYear() ==  trainings.get(i).getDate().get(Calendar.YEAR)) {
                                        dates.get(j).count = dates.get(j).count + 1;
                                        break;
                                    }

                                    if(j == dates.size() - 1) {
                                        dates.add(new Workout(new CalendarDay(trainings.get(i).getDate().get(Calendar.YEAR),
                                                trainings.get(i).getDate().get(Calendar.MONTH),
                                                trainings.get(i).getDate().get(Calendar.DAY_OF_MONTH)), 0));
                                    }
                                }
                            }



                            for(int i = 0; i < dates.size(); i++) {
                                if(dates.get(i).count == 1) {
                                    days1.add(dates.get(i).day);
                                } else if(dates.get(i).count == 2) {
                                    days2.add(dates.get(i).day);
                                } else {
                                    days3.add(dates.get(i).day);
                                }
                            }

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    CalendarDecorator decorator1 = new CalendarDecorator(CalendarActivity.this, R.drawable.one_training, days1);
                                    CalendarDecorator decorator2 = new CalendarDecorator(CalendarActivity.this, R.drawable.two_trainings, days2);
                                    CalendarDecorator decorator3 = new CalendarDecorator(CalendarActivity.this, R.drawable.busy_day, days3);
                                    ((MaterialCalendarView) findViewById(R.id.calendarView2)).removeDecorators();
                                    Calendar calendar = Calendar.getInstance();
                                    ArrayList<CalendarDay> days4 = new ArrayList<CalendarDay>();
                                    days4.add(new CalendarDay(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
                                    TodayDecorator decorator4 = new TodayDecorator(CalendarActivity.this, R.drawable.day_selected_circle,
                                            days4);
                                    ((MaterialCalendarView) findViewById(R.id.calendarView2)).addDecorators(decorator1, decorator2, decorator3,
                                            decorator4);
                                    ((MaterialCalendarView) findViewById(R.id.calendarView2)).invalidateDecorators();
                                }
                            }, 50);
                        } catch (Exception ignored) {}
                    }
                })
                .workoutSessions()
                .perform();
    }

    public void invalidateDates() {
        ArrayList<Workout> dates = new ArrayList<>();
        days1.clear();
        days2.clear();
        days3.clear();
        for(int i = 0; i < trainings.size(); i++) {
            if(dates.size() == 0) {
                dates.add(new Workout(new CalendarDay(trainings.get(i).getDate().get(Calendar.YEAR),
                        trainings.get(i).getDate().get(Calendar.MONTH),
                        trainings.get(i).getDate().get(Calendar.DAY_OF_MONTH)), 1));
                continue;
            }

            for(int j = 0; j < dates.size(); j++) {
                if(dates.get(j).day.getDay() ==  trainings.get(i).getDate().get(Calendar.DAY_OF_MONTH) &&
                        dates.get(j).day.getMonth() ==  trainings.get(i).getDate().get(Calendar.MONTH) &&
                        dates.get(j).day.getYear() ==  trainings.get(i).getDate().get(Calendar.YEAR)) {
                    dates.get(j).count = dates.get(j).count + 1;
                    break;
                }

                if(j == dates.size() - 1) {
                    dates.add(new Workout(new CalendarDay(trainings.get(i).getDate().get(Calendar.YEAR),
                            trainings.get(i).getDate().get(Calendar.MONTH),
                            trainings.get(i).getDate().get(Calendar.DAY_OF_MONTH)), 0));
                }
            }
        }

        for(int i = 0; i < dates.size(); i++) {
            if(dates.get(i).count == 1) {
                days1.add(dates.get(i).day);
            } else if(dates.get(i).count == 2) {
                days2.add(dates.get(i).day);
            } else {
                days3.add(dates.get(i).day);
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CalendarDecorator decorator1 = new CalendarDecorator(CalendarActivity.this, R.drawable.one_training, days1);
                CalendarDecorator decorator2 = new CalendarDecorator(CalendarActivity.this, R.drawable.two_trainings, days2);
                CalendarDecorator decorator3 = new CalendarDecorator(CalendarActivity.this, R.drawable.busy_day, days3);
                ((MaterialCalendarView) findViewById(R.id.calendarView2)).removeDecorators();
                ((MaterialCalendarView) findViewById(R.id.calendarView2)).addDecorators(decorator1, decorator2, decorator3);
                Calendar calendar = Calendar.getInstance();
                ArrayList<CalendarDay> days4 = new ArrayList<CalendarDay>();
                days4.add(new CalendarDay(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
                TodayDecorator decorator4 = new TodayDecorator(CalendarActivity.this, R.drawable.day_selected_circle,
                        days4);
                ((MaterialCalendarView) findViewById(R.id.calendarView2)).addDecorators(decorator1, decorator2, decorator3,
                        decorator4);
                ((MaterialCalendarView) findViewById(R.id.calendarView2)).invalidateDecorators();
            }
        }, 50);
    }

    public void invalidateCalendar() {
        CalendarDay day = ((MaterialCalendarView) findViewById(R.id.calendarView2fd)).getSelectedDate();
        if(day == null) {
            return;
        }

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, day.getYear());
        calendar.set(Calendar.MONTH, day.getMonth());
        calendar.set(Calendar.DAY_OF_MONTH, day.getDay());

        trainings.get(selected).setDate(calendar);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                myItemAdapter.notifyItemChanged(selected);
                selected = 0;
            }
        }, 100);
        AndroidUtils.animateRevealHide(findViewById(R.id.dialog));

        JSONObject object = new JSONObject();
        JSONObject toSend = new JSONObject();

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = formatter.format(calendar.getTime());

        try {
            object.accumulate("scheduled_for", currentDate);
            toSend.accumulate("workout_session", object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        GetFitServerRequest.with(this)
                .authorize()
                .objects(toSend)
                .listener(new OnRequestPerformedListener() {
                    @Override
                    public void onRequestPerformedListener(Object... objects) {
                        if (objects[0].equals("Internet")) {
                            cancelRequest();
                        }
                    }
                })
                .completeWorkout(trainings.get(selected).getId())
                .perform();

       invalidateDates();
    }

    public void openCalendarDialog() {
        ArrayList<Workout> dates = new ArrayList<>();
        findViewById(R.id.textView16).setBackgroundColor(Color.parseColor("#4e4e4e"));
        ((MaterialCalendarView) findViewById(R.id.calendarView2fd)).removeDecorators();
        ((MaterialCalendarView) findViewById(R.id.calendarView2fd)).setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
        for(int i = 0; i < trainings.size(); i++) {
            if(dates.size() == 0) {
                dates.add(new Workout(new CalendarDay(trainings.get(i).getDate().get(Calendar.YEAR),
                        trainings.get(i).getDate().get(Calendar.MONTH),
                        trainings.get(i).getDate().get(Calendar.DAY_OF_MONTH)), 1));
                continue;
            }

            for(int j = 0; j < dates.size(); j++) {
                if(dates.get(j).day.getDay() ==  trainings.get(i).getDate().get(Calendar.DAY_OF_MONTH) &&
                        dates.get(j).day.getMonth() ==  trainings.get(i).getDate().get(Calendar.MONTH) &&
                        dates.get(j).day.getYear() ==  trainings.get(i).getDate().get(Calendar.YEAR)) {
                    dates.get(j).count = dates.get(j).count + 1;
                    break;
                }

                if(j == dates.size() - 1) {
                    dates.add(new Workout(new CalendarDay(trainings.get(i).getDate().get(Calendar.YEAR),
                            trainings.get(i).getDate().get(Calendar.MONTH),
                            trainings.get(i).getDate().get(Calendar.DAY_OF_MONTH)), 0));
                }
            }
        }

        for(int i = 0; i < dates.size(); i++) {
            if(dates.get(i).count == 1) {
                days1.add(dates.get(i).day);
            } else if(dates.get(i).count == 2) {
                days2.add(dates.get(i).day);
            } else {
                days3.add(dates.get(i).day);
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CalendarDecorator decorator1 = new CalendarDecorator(CalendarActivity.this, R.drawable.one_training, days1);
                CalendarDecorator decorator2 = new CalendarDecorator(CalendarActivity.this, R.drawable.two_trainings, days2);
                CalendarDecorator decorator3 = new CalendarDecorator(CalendarActivity.this, R.drawable.busy_day, days3);
                ((MaterialCalendarView) findViewById(R.id.calendarView2fd)).removeDecorators();

                BasicDecorator decorator11 = new BasicDecorator(CalendarActivity.this,
                        R.drawable.selector_one);

                SelectionDecorator decorator21 = new SelectionDecorator(CalendarActivity.this,
                        R.drawable.selector_new_two,
                        days1);

                SelectionDecorator decorator31 = new SelectionDecorator(CalendarActivity.this,
                        R.drawable.selector_three,
                        days2);
                ((MaterialCalendarView) findViewById(R.id.calendarView2fd)).addDecorators(decorator11, decorator21, decorator31);

                ((MaterialCalendarView) findViewById(R.id.calendarView2fd)).addDecorators(decorator1, decorator2, decorator3);
                Calendar calendar = Calendar.getInstance();
                ArrayList<CalendarDay> days4 = new ArrayList<CalendarDay>();
                days4.add(new CalendarDay(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
                TodayDecorator decorator4 = new TodayDecorator(CalendarActivity.this, R.drawable.day_selected_circle,
                        days4);
                ((MaterialCalendarView) findViewById(R.id.calendarView2fd)).addDecorators(decorator1, decorator2, decorator3,
                        decorator4);
                ((MaterialCalendarView) findViewById(R.id.calendarView2fd)).invalidateDecorators();
                ((MaterialCalendarView) findViewById(R.id.calendarView2fd)).setOnDateChangedListener(CalendarActivity.this);
            }
        }, 50);
        AndroidUtils.animateRevealShowFirst(findViewById(R.id.dialog), CalendarActivity.this);
    }
}