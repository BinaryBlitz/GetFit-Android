package binaryblitz.athleteapp.Utils;

import android.content.Context;

import java.util.Calendar;

import binaryblitz.athleteapp.R;

public class DateUtils {

    public static boolean isAfterToday(int year, int month, int day) {
        Calendar today = Calendar.getInstance();
        Calendar myDate = Calendar.getInstance();

        myDate.set(year, month, day);

        return !myDate.before(today);
    }

    public static boolean isAfter(Calendar first, Calendar second) {
        return !first.before(second);
    }

    public static boolean isAfterToday(Calendar calendar) {
        Calendar today = Calendar.getInstance();

        return !calendar.before(today);
    }

//    public static String getMonthString(int number, Context context) {
//        String month = "";
//
//        switch(number) {
//            case 0:
//                month = context.getString(R.string.january_string);
//                break;
//            case 1:
//                month = context.getString(R.string.febuary_string);
//                break;
//            case 2:
//                month = context.getString(R.string.march_string);
//                break;
//            case 3:
//                month = context.getString(R.string.april_string);
//                break;
//            case 4:
//                month = context.getString(R.string.may_string);
//                break;
//            case 5:
//                month = context.getString(R.string.june_string);
//                break;
//            case 6:
//                month = context.getString(R.string.july_string);
//                break;
//            case 7:
//                month = context.getString(R.string.august_string);
//                break;
//            case 8:
//                month = context.getString(R.string.september_string);
//                break;
//            case 9:
//                month = context.getString(R.string.octoboer_string);
//                break;
//            case 10:
//                month = context.getString(R.string.november_string);
//                break;
//            case 11:
//                month = context.getString(R.string.december_string);
//                break;
//            default:
//                return month;
//        }
//
//        return month;
//    }

    public static String getDateStringRepresentation(Calendar startTime) {
        String date = Integer.toString(startTime.get(Calendar.DAY_OF_MONTH)) + "." +
                (startTime.get(Calendar.MONTH) + 1 > 9 ? (startTime.get(Calendar.MONTH) + 1) : "0" + (startTime.get(Calendar.MONTH) + 1)) + "." +
                Integer.toString(startTime.get(Calendar.YEAR));
        date += " ";
        date += (startTime.get(Calendar.HOUR_OF_DAY) > 9 ?
                Integer.toString(startTime.get(Calendar.HOUR_OF_DAY)) :
                "0" + Integer.toString(startTime.get(Calendar.HOUR_OF_DAY)))
                + ":" + (startTime.get(Calendar.MINUTE) > 9 ?
                Integer.toString(startTime.get(Calendar.MINUTE)) :
                "0" + Integer.toString(startTime.get(Calendar.MINUTE)));

        return date;
    }

    public static String getDateStringRepresentationForMessager(Calendar startTime) {
        String date = Integer.toString(startTime.get(Calendar.DAY_OF_MONTH)) + "." +
                (startTime.get(Calendar.MONTH) + 1 > 9 ? (startTime.get(Calendar.MONTH) + 1) : "0" + (startTime.get(Calendar.MONTH) + 1));
        date += " ";
        date += (startTime.get(Calendar.HOUR_OF_DAY) > 9 ?
                Integer.toString(startTime.get(Calendar.HOUR_OF_DAY)) :
                "0" + Integer.toString(startTime.get(Calendar.HOUR_OF_DAY)))
                + ":" + (startTime.get(Calendar.MINUTE) > 9 ?
                Integer.toString(startTime.get(Calendar.MINUTE)) :
                "0" + Integer.toString(startTime.get(Calendar.MINUTE)));

        return date;
    }

    public static String getDateStringRepresentationWithoutTime(Calendar startTime) {
        return Integer.toString(startTime.get(Calendar.DAY_OF_MONTH)) + "." +
                (startTime.get(Calendar.MONTH) + 1 > 9 ? (startTime.get(Calendar.MONTH) + 1) : "0" + (startTime.get(Calendar.MONTH) + 1)) + "." +
                Integer.toString(startTime.get(Calendar.YEAR));
    }

    public static int getAgeFromCalendar(Calendar calendar) {
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);
        if (today.get(Calendar.MONTH) < calendar.get(Calendar.MONTH)) {
            age--;
        } else if (today.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
                && today.get(Calendar.DAY_OF_MONTH) < calendar.get(Calendar.DAY_OF_MONTH)) {
            age--;
        }

        return age;
    }
}
