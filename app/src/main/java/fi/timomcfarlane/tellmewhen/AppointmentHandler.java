package fi.timomcfarlane.tellmewhen;

import android.content.Context;
import java.util.ArrayList;
import java.util.Calendar;

public class AppointmentHandler {

    private ArrayList<Appointment> apps;

    private Context host;

    public AppointmentHandler(Context host) {
        this.host = host;
        apps = new ArrayList<>();
        apps.add(new Appointment("Test", Calendar.getInstance().getTime(), "12:00"));
        apps.add(new Appointment("Test2", Calendar.getInstance().getTime(), "12:00"));
        apps.add(new Appointment("Test3", Calendar.getInstance().getTime(), "12:00"));
        apps.add(new Appointment("Test4", Calendar.getInstance().getTime(), "12:00"));
    }

    public ArrayList<Appointment> getAppointments() {
        return this.apps;
    }
}
