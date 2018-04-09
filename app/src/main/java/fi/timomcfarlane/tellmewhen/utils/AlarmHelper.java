package fi.timomcfarlane.tellmewhen.utils;

import java.util.ArrayList;
import java.util.Arrays;

import fi.timomcfarlane.tellmewhen.data.model.AppointmentAlarm;

/**
 * Created by tmcf on 08/04/2018.
 */

public class AlarmHelper {
    public static ArrayList<AppointmentAlarm> currentAlarms = new ArrayList<>();

    public static void setCurrentAlarms(AppointmentAlarm... alarms) {
        currentAlarms.clear();
        currentAlarms.addAll(Arrays.asList(alarms));
    }

    public static ArrayList<AppointmentAlarm> getCurrentAlarms() {
        return currentAlarms;
    }

}
