package fi.timomcfarlane.tellmewhen;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import fi.timomcfarlane.tellmewhen.data.AppDatabase;
import fi.timomcfarlane.tellmewhen.data.model.Appointment;
import fi.timomcfarlane.tellmewhen.data.model.AppointmentAlarm;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
/**
 * Class used for receving on boot completed broadcasts.
 *
 * AlarmManager doesn't save alarms, this receiver schedules all necessary alarms from today
 * onwards into the system by querying the database and creating alarms according to retrieved
 * appointments.
 *
 * @author  Timo McFarlane
 * @version 1.0
 * @since   2014-04-24
 */
public class BootAlarmReceiver extends BroadcastReceiver {

    /**
     * onReceive acquire a wakelock from android system and in a separate thread add alarms to
     * system based on alarms found from the database.
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm =
                (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl =
                pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "APPOINTMENT_ALARM");

        //Acquire wakelock to ensure all tasks are finished
        wl.acquire();

        //Setup database
        AppDatabase db = Room.databaseBuilder(
                context,
                AppDatabase.class,
                "database-appointments"
        ).build();

        ArrayList<Appointment> apps = new ArrayList<>();

        Thread t = new Thread(() -> {
            // Retrieve data from database
            apps.addAll(db.appointmentDao().getAllFutureAppointments());

            // For each appointment check for alarms and create alarm for each that was found
            for(Appointment a : apps) {
                if(a.getAlarms().size() > 0) {
                    Log.d("MOIST", "SIZE:" + a.getAlarms().size());
                    AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent i = new Intent(context, AppointmentAlarmReceiver.class);
                    i.setAction("tellmewhen.appointment.ALARM");
                    i.putExtra("title", a.getTitle());
                    i.putExtra("address", a.getAddress());
                    i.putExtra("date", a.getDate());
                    i.putExtra("time", a.getTime());
                    i.addFlags(FLAG_ACTIVITY_SINGLE_TOP);
                    Calendar cal = null;
                    for (int x = 0; x < a.getAlarms().size(); x++) {
                        AppointmentAlarm alarm = a.getAlarms().get(x);
                        i.putExtra("creationtime", alarm.getCreationTime());
                        PendingIntent pi = PendingIntent.getBroadcast(context, (int) alarm.getCreationTime(), i, 0);
                        try {
                            cal = createDateTimeFromString(alarm.getDate(), alarm.getTime());
                            alarmMgr.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            //Release wakelock
            wl.release();
        });
        t.start();
    }

    /**
     * Convert given string date and time to a single date object that is passed to calendar
     * @param date String representation of desired date
     * @param time String representation of desired time
     * @return Calendar object with given date
     * @throws ParseException
     */
    public Calendar createDateTimeFromString(String date, String time) throws ParseException {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        cal.setTime(sdf.parse(date + " " + time));
        return cal;
    }
}
