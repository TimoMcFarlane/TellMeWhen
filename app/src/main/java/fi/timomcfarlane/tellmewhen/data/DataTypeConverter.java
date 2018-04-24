package fi.timomcfarlane.tellmewhen.data;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import fi.timomcfarlane.tellmewhen.data.model.AppointmentAlarm;

/**
 * Class used for DataTypeConversion.
 *
 * Necessary TypeConverters for Room Persistence Library Data Access Objects
 *
 * @author  Timo McFarlane
 * @version 1.0
 * @since   2014-04-24
 */
public class DataTypeConverter {

    /**
     * Used for converting string of Alarms to ArrayList
     * @param value String representation of a List of alarms
     * @return ArrayList of alarms
     */
    @TypeConverter
    public ArrayList<AppointmentAlarm> fromStringToArrayList(String value) {
        Type type = new TypeToken<ArrayList<AppointmentAlarm>>(){}.getType();
        return new Gson().fromJson(value, type);
    }

    /**
     * Used for converting ArrayList to String of alarms
     * @param alarms ArrayList of alarms
     * @return String representation of List of alarms
     */
    @TypeConverter
    public String fromArrayListToString(ArrayList<AppointmentAlarm> alarms) {
        Gson gson = new Gson();
        String json = gson.toJson(alarms);
        return json;
    }

}
