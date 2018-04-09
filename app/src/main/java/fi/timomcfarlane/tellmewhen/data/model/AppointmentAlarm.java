package fi.timomcfarlane.tellmewhen.data.model;


public class AppointmentAlarm {
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
