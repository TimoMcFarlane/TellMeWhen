package fi.timomcfarlane.tellmewhen;

import android.arch.persistence.room.TypeConverter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTypeConverter {

    @TypeConverter
    public static String createStringFromDate(Date date) {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        return df.format(date);
    }

    @TypeConverter
    public static Date createDateFromString(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date parse = null;
        try {
            parse = sdf.parse(strDate);
        } catch(ParseException e) {

        }
        return parse;
    }
}
