package fi.timomcfarlane.tellmewhen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

public class FormActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner categorySpinner;
    private ArrayAdapter<CharSequence> adapter;
    private String appointmentDate;
    private String appointmentTime;
    private Calendar c;
    private BroadcastReceiver bReceiver;
    private EditText title;
    private String category;
    private EditText address;
    private TextView date;
    private TextView time;
    private EditText notes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        c = Calendar.getInstance();
        title = (EditText) findViewById(R.id.form_title);
        address = (EditText) findViewById(R.id.form_address);
        notes = (EditText) findViewById(R.id.form_notes);
        categorySpinner = (Spinner) findViewById(R.id.form_category);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.form_category_options, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        categorySpinner.setOnItemSelectedListener(this);
        initDateTimePlaceholders();
    }

    public void initDateTimePlaceholders() {
        String dateText = formatWithSeparator(new int[] {
                c.get(Calendar.DAY_OF_MONTH),
                c.get(Calendar.MONTH),
                c.get(Calendar.YEAR)
        }, '-');

        String timeText = formatWithSeparator(new int[] {
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE)
        }, ':');

        date = (TextView) findViewById(R.id.form_date);
        date.setText(dateText);
        time = (TextView) findViewById(R.id.form_time);
        time.setText(timeText);
    }

    public void showDatePickerDialog(View v) {
        AppCompatDialogFragment dFragment = new DatePickerFragment();
        dFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        AppCompatDialogFragment tFragment = new TimePickerFragment();
        tFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    protected void onResume() {
        super.onResume();
        bReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getIntArrayExtra("date") != null) {
                    int[] temp = intent.getIntArrayExtra("date");
                    appointmentDate = "";
                    appointmentDate = formatWithSeparator(temp, '-');
                    date.setText(appointmentDate);

                } else if(intent.getIntArrayExtra("time") != null) {
                    int[] temp = intent.getIntArrayExtra("time");
                    appointmentTime = "";
                    appointmentTime = formatWithSeparator(temp, ':');
                    time.setText(appointmentTime);
                }
            }
        };
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(bReceiver, new IntentFilter("form_activity"));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(bReceiver);
        super.onPause();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        category = (String) adapterView.getItemAtPosition(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void onFormCancel(View v) {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    public void onFormSubmit(View v) {
        Intent result = new Intent();
        result.putExtra("title", title.getText().toString());
        result.putExtra("category", category.toLowerCase());
        result.putExtra("address", address.getText().toString());
        result.putExtra("date", date.getText().toString());
        result.putExtra("time", time.getText().toString());
        result.putExtra("notes", notes.getText().toString());
        setResult(RESULT_OK, result);
        finish();
    }

    public String formatWithSeparator(int[] temp, char separator) {
        String formatted = "";

        for(int i = 0; i < temp.length; i++) {
            if(temp[i] < 10) {
                formatted += ("0" + temp[i]);
            } else {
                formatted += temp[i];
            }
            if(i < temp.length - 1) {
                formatted += separator;
            }
        }
        return formatted;
    }
}
