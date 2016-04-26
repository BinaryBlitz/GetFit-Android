package binaryblitz.athleteapp.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Date;

import binaryblitz.athleteapp.Adapters.NewTrainingsAdapter;
import binaryblitz.athleteapp.CalendarUtils.BasicDecorator;
import binaryblitz.athleteapp.CalendarUtils.CalendarDecorator;
import binaryblitz.athleteapp.CalendarUtils.SelectionDecorator;
import binaryblitz.athleteapp.Data.Training;
import binaryblitz.athleteapp.R;
import binaryblitz.athleteapp.Utils.AndroidUtils;

public class NewTrainingActivity extends AppCompatActivity implements OnDateSelectedListener {

    private static ArrayList<Training> trainings;

    private static Training training;

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
        view.setAdapter(new NewTrainingsAdapter(this));

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

                CalendarActivity.FLAG = true;
                ArrayList<Training> trainings = new ArrayList<>();
                int idStart = CalendarActivity.getCount();

                for(int i = 0; i < ((MaterialCalendarView) findViewById(R.id.calendarView2fd)).getSelectedDates().size(); i++) {
                    CalendarDay day = ((MaterialCalendarView) findViewById(R.id.calendarView2fd)).getSelectedDates().get(i);
                    Training new_training = new Training("1", idStart + i,
                            training.getName(), training.getType(), training.getExCount(),
                            new Date(day.getYear(), day.getMonth() + 1, day.getDay()),
                            training.getTime(), training.getParts(), training.getOwner(), training.getDesc());
                   trainings.add(new_training);
                }

                CalendarActivity.setNewTraining(trainings);

                finish();
            }
        });


        findViewById(R.id.drawer_indicator).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarActivity.FLAG = false;
                finish();
            }
        });

        ArrayList<CalendarDay> days3 = new ArrayList<>();
        days3.add(new CalendarDay(2015, 10, 4));
        days3.add(new CalendarDay(2015, 10, 8));
        days3.add(new CalendarDay(2015, 10, 12));
        days3.add(new CalendarDay(2015, 10, 15));
        CalendarDecorator decorator3 = new CalendarDecorator(this, R.drawable.one_training, days3);

        ((MaterialCalendarView) findViewById(R.id.calendarView2fd)).addDecorators(decorator3);
    }

    public void showDialog() {
        findViewById(R.id.textView16).setBackgroundColor(Color.parseColor("#4e4e4e"));
        ((MaterialCalendarView) findViewById(R.id.calendarView2fd)).removeDecorators();
        ((MaterialCalendarView) findViewById(R.id.calendarView2fd)).setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);

        ArrayList<CalendarDay> days1 = new ArrayList<>();
        ArrayList<CalendarDay> days2 = new ArrayList<>();

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

       // AndroidUtils.animateRevealShowFirst(findViewById(R.id.dialog), NewTrainingActivity.this);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                AndroidUtils.animateRevealShowFirst(findViewById(R.id.dialog), NewTrainingActivity.this);
            }
        });
    }
}
