package binaryblitz.athleteapp.CalendarUtils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.ArrayList;
import java.util.Calendar;

public class SelectionDecorator implements DayViewDecorator {

    private final Calendar calendar = Calendar.getInstance();
    private final Drawable selectionDrawable;

    private ArrayList<CalendarDay> days;

    public SelectionDecorator(Context context, int selectionResId, ArrayList<CalendarDay> days) {
        selectionDrawable = context.getResources().getDrawable(selectionResId);
        this.days = days;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        day.copyTo(calendar);
        for(int i = 0; i < days.size(); i++) {
            if(day.equals(days.get(i))) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(selectionDrawable);
    }
}