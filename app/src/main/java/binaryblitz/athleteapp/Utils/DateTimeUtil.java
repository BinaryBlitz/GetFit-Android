package binaryblitz.athleteapp.Utils;

import java.util.Calendar;
import java.util.Date;

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

    public static String getMonthString(int number) {
        String month = "";

        switch(number) {
            case 1:
                month = "January";
                break;
            case 2:
                month = "February";
                break;
            case 3:
                month = "March";
                break;
            case 4:
                month = "April";
                break;
            case 5:
                month = "May";
                break;
            case 6:
                month = "June";
                break;
            case 7:
                month = "July";
                break;
            case 8:
                month = "August";
                break;
            case 9:
                month = "September";
                break;
            case 10:
                month = "October";
                break;
            case 11:
                month = "November";
                break;
            case 12:
                month = "December";
                break;
            default:
                return month;
        }

        return month;
    }
}
