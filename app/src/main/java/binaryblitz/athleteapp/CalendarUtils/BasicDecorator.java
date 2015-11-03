package binaryblitz.athleteapp.CalendarUtils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.ArrayList;
import java.util.Calendar;

public class BasicDecorator implements DayViewDecorator {

    private final Calendar calendar = Calendar.getInstance();
    private final Drawable selectionDrawable;

    public BasicDecorator(Context context, int selectionResId) {
        selectionDrawable = context.getResources().getDrawable(selectionResId);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return true;
    }


    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(selectionDrawable);
    }
}