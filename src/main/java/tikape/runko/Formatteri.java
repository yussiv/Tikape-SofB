package tikape.runko;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Formatteri {

    private String stringToDateFormat;
    private String dateToStringFormat;
    private String dateString;

    public Formatteri(String format) {
        this.stringToDateFormat = format;
    }

    public Formatteri() {
        this.stringToDateFormat = "yyyy-MM-dd hh:mm:ss";
        this.dateToStringFormat = "dd.MM.yyyy hh:mm";
    }

    public String formatoi(String date) {
        dateString = "";
        try {
            SimpleDateFormat sdf1 = new SimpleDateFormat(stringToDateFormat);
            Date pvm = sdf1.parse(date);
            SimpleDateFormat sdf2 = new SimpleDateFormat(dateToStringFormat);
            dateString = sdf2.format(pvm);
            return dateString;
        } catch (ParseException e) {
            return "";
        }
    }
}
