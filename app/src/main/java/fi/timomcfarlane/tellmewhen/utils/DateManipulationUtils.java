package fi.timomcfarlane.tellmewhen.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
/**
 * Tools for formatting dates
 *
 * @author  Timo McFarlane
 * @version 1.0
 * @since   2014-04-24
 */
public class DateManipulationUtils {

    /**
     * Create verbal date in the form of "Day. Monthname"
     * @param strDate Full date to be converted
     * @return return formatted verbal date
     */
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

    /**
     * Format date/time array (example: [year, month, day]) into single string with given separator
     * @param temp date/time split into an array
     * @param separator Array element separator
     * @return formatted String with elements separated by provided separator (10-04-1999)
     */
    public static String formatWithSeparator(int[] temp, char separator) {
        String formatted = "";

        for(int i = 0; i < temp.length; i++) {
            if(temp[i] < 10) {
                formatted += ("0" + temp[i]);
            } else {
                formatted += temp[i];
            }
            if(i < temp.length - 1) {
                formatted += separator;
            }
        }
        return formatted;
    }
}
