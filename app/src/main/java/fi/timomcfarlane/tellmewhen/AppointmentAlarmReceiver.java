package fi.timomcfarlane.tellmewhen;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;

import static android.support.v4.app.NotificationCompat.VISIBILITY_PRIVATE;
/**
 * BroadcastReceiver for creating appointment notifications and alarming user.
 *
 * @author  Timo McFarlane
 * @version 1.0
 * @since   2014-04-24
 */
public class AppointmentAlarmReceiver extends BroadcastReceiver {

    /**
     * In onReceive create notification and display it to user.
     * Alarm user of new notification.
     * @param context default param
     * @param intent Intent that called receiver
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm =
                (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl =
                pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "APPOINTMENT_ALARM");

        //Acquire wakelock to ensure all tasks are finished
        wl.acquire();

        // Create intent for starting ScheduleActivity from notification
        Intent tapIntent = new Intent(context, ScheduleActivity.class);
        // Add creationTime to extra, so that application can find which alarm(appointment) fired.
        tapIntent.putExtra("details", intent.getLongExtra("creationtime", -1));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, tapIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        // Create notification
        NotificationCompat.Builder nBuild
                = new NotificationCompat.Builder(context, "AppointmentALARM")
                .setSmallIcon(R.drawable.ic_alarm_on_white_24dp)
                .setContentIntent(pendingIntent)
                .setColor(context.getResources().getColor(R.color.background))
                .setContentTitle(intent.getStringExtra("title"))
                .setContentText(intent.getStringExtra(
                        "address")
                        + ", " +
                        intent.getStringExtra("date")
                        + " " +
                        intent.getStringExtra("time")
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        nBuild.setVisibility(VISIBILITY_PRIVATE);

        // Send notification to user
        NotificationManagerCompat nManager = NotificationManagerCompat.from(context);
        nManager.notify(1653, nBuild.build());

        // Play device default notification and vibrate
        MediaPlayer mp = MediaPlayer.create(context, Settings.System.DEFAULT_NOTIFICATION_URI);
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        mp.start();
        v.vibrate(600);


        //Release wakelock
        wl.release();
    }

}
