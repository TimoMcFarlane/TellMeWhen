package fi.timomcfarlane.tellmewhen.data;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Collections;

import fi.timomcfarlane.tellmewhen.data.model.Appointment;

public class AppointmentHandler {

    private AppDatabase db;
    private ArrayList<Appointment> apps;
    private Context host;
    private LocalBroadcastManager lBroadcast;
    private Appointment[] currentAppointments;

    public AppointmentHandler(Context host) {
        this.host = host;
        db = Room.databaseBuilder(host, AppDatabase.class, "database-appointments").build();
        apps = new ArrayList<>();
        lBroadcast = LocalBroadcastManager.getInstance(host);
        new AsyncTaskHandler().execute("get");
    }

    public ArrayList<Appointment> getAppointments() {
        return apps;
    }

    public void insertNewData(Appointment... app) {
        currentAppointments = app;
        order(apps);
        new AsyncTaskHandler().execute("insert", "get");
    }

    public void updateExistingData(int position, Appointment... app) {
        apps.set(position, app[0]);
        order(apps);
        currentAppointments = app;
        new AsyncTaskHandler().execute("update");
    }

    public void removeData(Appointment... app) {
        apps.remove(app[0]);
        order(apps);
        currentAppointments = app;
        new AsyncTaskHandler().execute("delete");
    }

    public void order(ArrayList<Appointment> appointments) {
        Collections.sort(appointments, (o1, o2) -> {

            String d1 = ((Appointment) o1).getDate();
            String d2 = ((Appointment) o2).getDate();
            int compResult = d1.compareTo(d2);

            if (compResult != 0) {
                return compResult;
            }

            String t1 = ((Appointment) o1).getTime();
            String t2 = ((Appointment) o2).getTime();
            return t1.compareTo(t2);
        });
    }

    private class AsyncTaskHandler extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... actions) {
            int actionCode = 0;
            for(int i = 0; i < actions.length; i++) {
                switch(actions[i]) {
                    case "get":
                        apps.clear();
                        apps.addAll(db.appointmentDao().getAll());
                        actionCode = 200;
                        break;
                    case "insert":
                        db.appointmentDao().insertAppointments(currentAppointments);
                        actionCode = 201;
                        break;
                    case "delete":
                        db.appointmentDao().deleteAppointments(currentAppointments);
                        actionCode = 204;
                        break;
                    case "update":
                        db.appointmentDao().updateAppointments(currentAppointments);
                        actionCode = 205;
                        break;
                }
            }
            return actionCode;
        }

        @Override
        protected void onPostExecute(Integer code) {
            Intent i = new Intent("appointment_handler");
            i.putExtra("action", code);
            lBroadcast.sendBroadcast(i);
        }
    }
}
