package binaryblitz.athleteapp.Utils;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;

import binaryblitz.athleteapp.R;

public class DateTimeUtil {

    public static Date parseDate(String date) {
        return new Date();
    }

    public static String getFriendlyDateRepresentation(Date date) {
        return date.getDate() + " / " + date.getMonth();
    }

    public static Calendar dateToCalendar(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public static String getMonthString(Context context, int number) {
        String month = "";

        switch(number) {
            case 1:
                month = context.getString(R.string.jan_str);
                break;
            case 2:
                month = context.getString(R.string.feb_str);
                break;
            case 3:
                month = context.getString(R.string.march_str);
                break;
            case 4:
                month = context.getString(R.string.april_str);
                break;
            case 5:
                month = context.getString(R.string.may_str);
                break;
            case 6:
                month = context.getString(R.string.june_str);
                break;
            case 7:
                month = context.getString(R.string.july_str);
                break;
            case 8:
                month = context.getString(R.string.august_str);
                break;
            case 9:
                month = context.getString(R.string.sep_str);
                break;
            case 10:
                month = context.getString(R.string.cot_str);
                break;
            case 11:
                month = context.getString(R.string.nov_str);
                break;
            case 12:
                month = context.getString(R.string.dec_str);
                break;
            default:
                return month;
        }

        return month;
    }
}
