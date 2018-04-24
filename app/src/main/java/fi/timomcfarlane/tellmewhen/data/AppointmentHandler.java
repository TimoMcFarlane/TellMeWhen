package fi.timomcfarlane.tellmewhen.data;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import fi.timomcfarlane.tellmewhen.data.model.Appointment;
/**
 * Class used for handling all the applications data and dataflow using AsyncTask.
 *
 * @author  Timo McFarlane
 * @version 1.0
 * @since   2014-04-24
 */
public class AppointmentHandler {

    private AppDatabase db;
    private ArrayList<Appointment> apps;
    private Context host;
    private LocalBroadcastManager lBroadcast;
    private Appointment[] currentAppointments;
    public Calendar offsetCal;
    public SimpleDateFormat sdf;
    public String dateStr;


    /**
     * Initialize database and calendar, broadcast current week data to ScheduleActivity.
     * @param host
     */
    public AppointmentHandler(Context host) {
        this.host = host;
        db = Room.databaseBuilder(host, AppDatabase.class, "database-appointments").build();
        apps = new ArrayList<>();
        lBroadcast = LocalBroadcastManager.getInstance(host);
        offsetCal = Calendar.getInstance();
        offsetCal.set(Calendar.DAY_OF_WEEK, offsetCal.getFirstDayOfWeek());
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        dateStr = sdf.format(offsetCal.getTime());
        addWeekToCurrentWeekOffset();
    }

    /**
     * Add 7 days calendar and retrieve new data based on the adjusted calendar date
     */
    public void addWeekToCurrentWeekOffset() {
        offsetCal.add(Calendar.DAY_OF_YEAR, 7);
        dateStr = sdf.format(offsetCal.getTime());
        new AsyncTaskHandler().execute("get");
    }

    /**
     * Remove 7 days calendar and retrieve new data based on the adjusted calendar date
     */
    public void removeWeekFromCurrentWeekOffset() {
        offsetCal.add(Calendar.DAY_OF_YEAR, -7);
        dateStr = sdf.format(offsetCal.getTime());
        new AsyncTaskHandler().execute("get");
    }

    /**
     * Return current list of appointments
     * @return
     */
    public ArrayList<Appointment> getAppointments() {
        return apps;
    }

    /**
     * Insert one or more Appointments to database as an AsyncTask
     *
     * @param app One or more Appointments provided
     */
    public void insertNewData(Appointment... app) {
        currentAppointments = app;
        new AsyncTaskHandler().execute("insert", "get");
    }

    /**
     * Update existing Appointment at position with given Appointment
     * @param position Integer value to indicate position inside collection
     * @param app New Appointment to replace old data
     */
    public void updateExistingData(int position, Appointment... app) {
        apps.set(position, app[0]);
        order(apps);
        currentAppointments = app;
        new AsyncTaskHandler().execute("update");
    }

    /**
     * Remove one or more Appointments as an AsyncTask
     *
     * @param app One or more Appointments to be removed
     */
    public void removeData(Appointment... app) {
        apps.remove(app[0]);
        order(apps);
        currentAppointments = app;
        new AsyncTaskHandler().execute("delete");
    }

    /**
     * Used for finding the position of the Appointment that fired an alarm and started activity
     * from notification.
     *
     * @param creationTime "Unique id" used for creating Pending Intents (AlarmManager)
     * @return Position of the Appointment inside collection
     */
    public int findByAlarm(long creationTime) {
        for(int i = 0; i < apps.size(); i++) {
            if(apps.get(i).getAlarms().size() != 0) {
                for(int x = 0; x < apps.get(i).getAlarms().size(); x++) {
                    if(apps.get(i).getAlarms().get(x).getCreationTime() == creationTime) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Order appointments based on Date then Time inside collection
     * @param appointments List of appointments to be ordered
     */
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

    /**
     * Inner class for handling dataflow using AsyncTasks.
     *
     * Depending on the action(s) the task broadcasts on completion and the receiving Activity
     * adjusts the views according to what action was completed.
     */
    private class AsyncTaskHandler extends AsyncTask<String, Integer, Integer> {
        @Override
        protected Integer doInBackground(String... actions) {
            int actionCode = 0;
            for(int i = 0; i < actions.length; i++) {
                switch(actions[i]) {
                    case "get":
                        apps.clear();
                        apps.addAll(db.appointmentDao().getAll(dateStr));
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
            Log.d("MSG", apps.size() + "");
            Intent i = new Intent("appointment_handler");
            i.putExtra("action", code);
            lBroadcast.sendBroadcast(i);
        }
    }
}
