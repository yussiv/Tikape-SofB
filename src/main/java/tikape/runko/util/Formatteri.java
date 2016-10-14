package tikape.runko.util;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public final class Formatteri {

    private static final String stringToDateFormat  = "yyyy-MM-dd HH:mm:ss";
    private static final String dateToStringFormat  = "dd.MM.yyyy HH:mm";
    private static String dateString;

    private Formatteri() {
    }

    public static String formatoi(String date) {
        dateString = "";
        try {
            SimpleDateFormat sdf1 = new SimpleDateFormat(stringToDateFormat);
            Date dateTimeGMT = sdf1.parse(date);

            Calendar postausAika = new GregorianCalendar();
            postausAika.setTime(dateTimeGMT);
            postausAika.add(10, 3);

            Calendar aikaNyt = new GregorianCalendar();
            Date aikaNytDate = new Date();
            aikaNytDate.getTime();
            aikaNyt.setTime(aikaNytDate);
            aikaNyt.set(10, 3);

            if (aikaNyt.get(Calendar.YEAR) == postausAika.get(Calendar.YEAR) && aikaNyt.get(Calendar.MONTH) == postausAika.get(Calendar.MONTH) && aikaNyt.get(Calendar.DAY_OF_MONTH) == postausAika.get(Calendar.DAY_OF_MONTH)) {
                dateString = "Tänään kello " + postausAika.get(11) + ".";
                if (postausAika.get(12) < 10) {
                    dateString += "0" +  postausAika.get(12);
                } else {
                    dateString += postausAika.get(12);
                }
            } else if(aikaNyt.get(Calendar.YEAR) == postausAika.get(Calendar.YEAR) && aikaNyt.get(Calendar.MONTH) == postausAika.get(Calendar.MONTH) && aikaNyt.get(Calendar.DAY_OF_MONTH) - postausAika.get(Calendar.DAY_OF_MONTH) == 1) {
                dateString = "Eilen kello " + postausAika.get(11) + ".";
                if (postausAika.get(12) < 10) {
                    dateString += "0" +  postausAika.get(12);
                } else {
                    dateString += postausAika.get(12);
                }
            } else {
                Date postausAikaDate = postausAika.getTime();
                SimpleDateFormat sdf2 = new SimpleDateFormat(dateToStringFormat);
                dateString = sdf2.format(postausAikaDate);
            }
            return dateString;
            
        } catch (Exception e) { // if all else fails, do nothing
            return "";
        }
    }
}