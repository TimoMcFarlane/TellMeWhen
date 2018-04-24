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

/**
 * Class used for creating a DatePicker -dialog as a fragment
 *
 * @author  Timo McFarlane
 * @version 1.0
 * @since   2014-04-24
 */
public class DatePickerFragment extends AppCompatDialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private LocalBroadcastManager lBroadcast;
    private String key;

    /**
     * When creating the dialog set the current date as the starting point
     * @param b default param
     * @return Dialog object
     */
    @Override
    public Dialog onCreateDialog(Bundle b) {
        lBroadcast = LocalBroadcastManager.getInstance(getContext());
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    /**
     * When date is set, broadcast the data back to FormActivity
     *
     * @param datePicker DatePicker that set the time
     * @param i Year that was set
     * @param i1 Month that was set
     * @param i2 Day that was set
     */
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
