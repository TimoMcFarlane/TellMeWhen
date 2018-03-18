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
        apps.add(new Appointment("Drink coffee","Potato street 1", Calendar.getInstance().getTime(), "12:00", "work"));
        apps.add(new Appointment("Eat chicken wings","Potato street 1", Calendar.getInstance().getTime(), "12:00", "personal"));
        apps.add(new Appointment("Discuss blogposts","Potato street 1", Calendar.getInstance().getTime(), "12:00","social"));
        apps.add(new Appointment("Meet with family","Potato street 1",Calendar.getInstance().getTime(), "12:00", "family"));
        apps.add(new Appointment("Attend wedding","Potato street 1",Calendar.getInstance().getTime(), "12:00", "family"));
    }

    public ArrayList<Appointment> getAppointments() {
        return this.apps;
    }
}
