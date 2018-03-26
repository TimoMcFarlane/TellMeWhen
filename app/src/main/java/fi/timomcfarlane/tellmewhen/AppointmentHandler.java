package fi.timomcfarlane.tellmewhen;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;

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
        getNewData();
    }

    public ArrayList<Appointment> getAppointments() {
        return apps;
    }

    public void insertNewData(Appointment... app) {
        currentAppointments = app;
        new AsyncTaskHandler().execute("insert");
        getNewData();
    }

    public void removeData(Appointment... app) {
        currentAppointments = app;
        new AsyncTaskHandler().execute("delete");
        getNewData();
    }

    public void getNewData() {
        new AsyncTaskHandler().execute("get");
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
