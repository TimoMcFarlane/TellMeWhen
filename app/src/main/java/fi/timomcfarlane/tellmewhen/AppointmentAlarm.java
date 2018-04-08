package fi.timomcfarlane.tellmewhen;


public class AppointmentAlarm {
    private long creationTime;
    private String date;
    private String time;

    public AppointmentAlarm() {}

    public AppointmentAlarm(long actionCode, String date, String time) {
        this.creationTime = actionCode;
        this.date = date;
        this.time = time;
    }

    public long getActionCode() {
        return creationTime;
    }

    public void setActionCode(long actionCode) {
        this.creationTime = actionCode;
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
