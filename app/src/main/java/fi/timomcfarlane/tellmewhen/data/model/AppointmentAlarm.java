package fi.timomcfarlane.tellmewhen.data.model;


import java.io.Serializable;
/**
 * Class is used as a data model for a single Appointments alarms.
 *
 * Implements Serializable so that it can be passed inside intents as a collection of alarms.
 *
 * @author  Timo McFarlane
 * @version 1.0
 * @since   2014-04-24
 */
public class AppointmentAlarm implements Serializable {
    /**
     * Used as a "unique id" for creating indentifiable Pending Intents (AlarmManager)
     */
    private long creationTime;
    private String date;
    private String time;

    public AppointmentAlarm() {}

    public AppointmentAlarm(long creationTime, String date, String time) {
        this.creationTime = creationTime;
        this.date = date;
        this.time = time;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
