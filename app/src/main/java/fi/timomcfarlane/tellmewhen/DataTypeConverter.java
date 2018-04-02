package fi.timomcfarlane.tellmewhen;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class DataTypeConverter {

    @TypeConverter
    public ArrayList<AppointmentAlarm> fromStringToArrayList(String value) {
        Type type = new TypeToken<ArrayList<AppointmentAlarm>>(){}.getType();
        return new Gson().fromJson(value, type);
    }

    @TypeConverter
    public String fromArrayListToString(ArrayList<AppointmentAlarm> alarms) {
        Gson gson = new Gson();
        String json = gson.toJson(alarms);
        return json;
    }

}
