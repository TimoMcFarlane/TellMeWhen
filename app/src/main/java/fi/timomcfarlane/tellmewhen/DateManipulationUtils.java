package fi.timomcfarlane.tellmewhen;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateManipulationUtils {

    public static String createVerbalDate(String strDate) {
        String str = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date parse = sdf.parse(strDate);
            Calendar c = Calendar.getInstance();
            c.setTime(parse);

            str = c.get(Calendar.DAY_OF_MONTH) + ". "
                    + c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);

        } catch(ParseException e) {
            Log.d("DEBUG", "Incorrect date format. Cannot parse.");
        }
        return str;
    }

    public static Date createDateFromString(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date parse = null;
        try {
            parse = sdf.parse(strDate);
        } catch(ParseException e) {

        }
        return parse;
    }

    public static String createStringFromDate(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }

}
