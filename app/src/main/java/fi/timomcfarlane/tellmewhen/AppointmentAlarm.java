package fi.timomcfarlane.tellmewhen;


public class AppointmentAlarm {
    private int actionCode;
    private String date;
    private String time;

    public AppointmentAlarm() {}

    public AppointmentAlarm(int actionCode, String date, String time) {
        this.actionCode = actionCode;
        this.date = date;
        this.time = time;
    }

    public int getActionCode() {
        return actionCode;
    }

    public void setActionCode(int actionCode) {
        this.actionCode = actionCode;
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
