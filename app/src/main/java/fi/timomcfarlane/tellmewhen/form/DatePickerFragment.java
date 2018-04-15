package fi.timomcfarlane.tellmewhen.form;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;


public class DatePickerFragment extends AppCompatDialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private LocalBroadcastManager lBroadcast;
    private String key;

    @Override
    public Dialog onCreateDialog(Bundle b) {
        lBroadcast = LocalBroadcastManager.getInstance(getContext());
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Intent broadcast = new Intent("form_activity");
        if(((FormActivity)getActivity()).getPickerAction().equals("alarm")) {
            broadcast.putExtra("alarm_Date", new int[] {i,i1+1,i2});
        } else {
            broadcast.putExtra("date", new int[] {i,i1+1,i2} );
        }
        lBroadcast.sendBroadcast(broadcast);
    }
}
