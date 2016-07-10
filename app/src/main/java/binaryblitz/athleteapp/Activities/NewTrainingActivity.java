package binaryblitz.athleteapp.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import binaryblitz.athleteapp.Abstract.BaseActivity;
import binaryblitz.athleteapp.Adapters.NewTrainingsAdapter;
import binaryblitz.athleteapp.CalendarUtils.BasicDecorator;
import binaryblitz.athleteapp.CalendarUtils.CalendarDecorator;
import binaryblitz.athleteapp.CalendarUtils.SelectionDecorator;
import binaryblitz.athleteapp.CalendarUtils.TodayDecorator;
import binaryblitz.athleteapp.Custom.ProgressDialog;
import binaryblitz.athleteapp.Data.Training;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Server.GetFitServerRequest;
import binaryblitz.athleteapp.Server.OnRequestPerformedListener;
import binaryblitz.athleteapp.Utils.AndroidUtils;

public class NewTrainingActivity extends BaseActivity implements OnDateSelectedListener, SwipeRefreshLayout.OnRefreshListener {

    private static ArrayList<Training> trainings = new ArrayList<>();
    private NewTrainingsAdapter adapter;
    private static Training training;
    private SwipeRefreshLayout layout;

    private String id = null;

    public static void setTraining(Training training) {
        NewTrainingActivity.training = training;
    }

    public static void setTrainings(ArrayList<Training> trainings) {
        NewTrainingActivity.trainings = trainings;
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        widget.invalidateDecorators();
        findViewById(R.id.textView16).setBackgroundColor(Color.parseColor("#3695ed"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_training_layout);
        findViewById(R.id.dialog).setVisibility(View.GONE);

        RecyclerView view = (RecyclerView) findViewById(R.id.recyclerView);
        view.setItemAnimator(new DefaultItemAnimator());
        view.setLayoutManager(new LinearLayoutManager(this));

        adapter = new NewTrainingsAdapter(this);
        view.setAdapter(adapter);

        id = getIntent().getStringExtra("id");
        findViewById(R.id.empty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtils.animateRevealHide(findViewById(R.id.dialog));
            }
        });

        findViewById(R.id.textView16).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((MaterialCalendarView) findViewById(R.id.calendarView2fd)).getSelectedDates().size() == 0) {
                    return;
                }

                final ProgressDialog dialog = new ProgressDialog();
                dialog.show(getFragmentManager(), "athleteapp");

                CalendarActivity.FLAG = true;

                JSONObject toSend = new JSONObject();

                JSONArray array = new JSONArray();

                for(int i = 0; i < ((MaterialCalendarView) findViewById(R.id.calendarView2fd)).getSelectedDates().size(); i++) {
                    CalendarDay day = ((MaterialCalendarView) findViewById(R.id.calendarView2fd)).getSelectedDates().get(i);
                    JSONObject object = new JSONObject();
                    try {
                        object.accumulate("workout_id", id == null ? training.getId() : id);
                        object.accumulate("scheduled_for", day.getYear() + "-" +
                                (day.getMonth() + 1 > 9 ? (day.getMonth() + 1) : "0" + (day.getMonth() + 1)) + "-" +
                        (day.getDay() > 9 ? day.getDay() : "0" + day.getDay()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    array.put(object);
                }

                try {
                    toSend.accumulate("workout_sessions_attributes", array);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONObject finalObj = new JSONObject();

                try {
                    finalObj.accumulate("user", toSend);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                GetFitServerRequest.with(NewTrainingActivity.this)
                        .authorize()
                        .objects(finalObj)
                        .listener(new OnRequestPerformedListener() {
                            @Override
                            public void onRequestPerformedListener(Object... objects) {
                                dialog.dismiss();
                                if (objects[0].equals("Internet")) {
                                    cancelRequest();
                                    return;
                                }
                                CalendarActivity.reload = true;
                                finish();
                            }
                        })
                        .updateWorkouts()
                        .perform();
            }
        });


        findViewById(R.id.drawer_indicator).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarActivity.FLAG = false;
                finish();
            }
        });

        layout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        layout.setOnRefreshListener(this);
        layout.setColorSchemeResources(R.color.accent_color);

        layout.setRefreshing(true);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                layout.setRefreshing(true);
                loadActivityData();
            }
        });
    }

    public void loadActivityData() {
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
                                            new AlertDialog.Builder(NewTrainingActivity.this)
                                                    .setTitle(getString(R.string.title_str))
                                                    .setMessage(getString(R.string.reg_alert_str))
                                                    .setCancelable(false)
                                                    .setPositiveButton(getString(R.string.cont_upcase_str), new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            Intent intent = new Intent(NewTrainingActivity.this, AuthActivity.class);
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
                            ArrayList<Training> trainings = new ArrayList<>();
                            for(int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);

                                trainings.add(new Training(
                                        object.getString("id"),
                                        object.getString("name"),
                                        object.getJSONObject("program").getString("name"),
                                        object.getInt("exercises_count"),
                                        null,
                                        object.getInt("duration"),
                                        null,
                                        object.getJSONObject("program").getString("id"),
                                        null
                                ));
                            }

                            adapter.setTrainings(trainings);
                            adapter.notifyDataSetChanged();

                            if(id != null) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        showDialog();
                                    }
                                }, 100);
                            }
                        } catch (Exception e) {

                        }
                    }
                })
                .workouts()
                .perform();
    }

    public void showDialog() {
        findViewById(R.id.textView16).setBackgroundColor(Color.parseColor("#4e4e4e"));
        ((MaterialCalendarView) findViewById(R.id.calendarView2fd)).removeDecorators();
        ((MaterialCalendarView) findViewById(R.id.calendarView2fd)).setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);

        ArrayList<CalendarActivity.Workout> dates = new ArrayList<>();

        final ArrayList<CalendarDay> days1 = new ArrayList<>();
        final ArrayList<CalendarDay> days2 = new ArrayList<>();
        final ArrayList<CalendarDay> days3 = new ArrayList<>();

        for(int i = 0; i < trainings.size(); i++) {
            if(dates.size() == 0) {
                dates.add(new CalendarActivity.Workout(new CalendarDay(trainings.get(i).getDate().get(Calendar.YEAR),
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
                    dates.add(new CalendarActivity.Workout(new CalendarDay(trainings.get(i).getDate().get(Calendar.YEAR),
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
                CalendarDecorator decorator1 = new CalendarDecorator(NewTrainingActivity.this, R.drawable.one_training, days1);
                CalendarDecorator decorator2 = new CalendarDecorator(NewTrainingActivity.this, R.drawable.two_trainings, days2);
                CalendarDecorator decorator3 = new CalendarDecorator(NewTrainingActivity.this, R.drawable.busy_day, days3);

                BasicDecorator decorator11 = new BasicDecorator(NewTrainingActivity.this,
                        R.drawable.selector_one);

                SelectionDecorator decorator21 = new SelectionDecorator(NewTrainingActivity.this,
                        R.drawable.selector_new_two,
                        days1);

                SelectionDecorator decorator31 = new SelectionDecorator(NewTrainingActivity.this,
                        R.drawable.selector_three,
                        days2);
                ((MaterialCalendarView) findViewById(R.id.calendarView2fd)).removeDecorators();
                ((MaterialCalendarView) findViewById(R.id.calendarView2fd)).addDecorators(decorator11, decorator21, decorator31);
                ((MaterialCalendarView) findViewById(R.id.calendarView2fd)).addDecorators(decorator1, decorator2, decorator3);
                Calendar calendar = Calendar.getInstance();
                ArrayList<CalendarDay> days4 = new ArrayList<CalendarDay>();
                days4.add(new CalendarDay(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
                TodayDecorator decorator4 = new TodayDecorator(NewTrainingActivity.this, R.drawable.day_selected_circle,
                        days4);
                ((MaterialCalendarView) findViewById(R.id.calendarView2fd)).addDecorators(decorator1, decorator2, decorator3,
                        decorator4);
                ((MaterialCalendarView) findViewById(R.id.calendarView2fd)).invalidateDecorators();
            }
        }, 50);

        ((MaterialCalendarView) findViewById(R.id.calendarView2fd)).setOnDateChangedListener(this);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                AndroidUtils.animateRevealShowFirst(findViewById(R.id.dialog), NewTrainingActivity.this);
            }
        });
    }

    @Override
    public void onRefresh() {
        loadActivityData();
    }
}
