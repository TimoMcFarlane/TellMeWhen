package fi.timomcfarlane.tellmewhen;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends AppCompatDialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private LocalBroadcastManager lBroadcast;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        lBroadcast = LocalBroadcastManager.getInstance(getContext());

        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(),
                this,
                hour,
                minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        Intent broadcast = new Intent("form_activity");
        broadcast.putExtra("time", new int[] {i,i1} );
        lBroadcast.sendBroadcast(broadcast);
    }
}
