package fi.timomcfarlane.tellmewhen.form;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import fi.timomcfarlane.tellmewhen.R;
import fi.timomcfarlane.tellmewhen.data.model.AppointmentAlarm;

import static fi.timomcfarlane.tellmewhen.utils.DateManipulationUtils.formatWithSeparator;

public class FormActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private View root;
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
    private Button submit;
    private ArrayList<AppointmentAlarm> alarms;
    private String tempAlarmDate;
    private String tempAlarmTime;
    private AlarmListFragment alarmListFragment;
    private String pickerAction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        initFields();
        attachGlobalFormListener();
        if(getIntent().hasExtra("edit")) {
            autoFillFormFields();
            if(getIntent().hasExtra("alarms")) {
                alarms = (ArrayList<AppointmentAlarm>) getIntent().getSerializableExtra("alarms");
            } else {
                alarms = new ArrayList<>();
            }
        } else {
            submit.setText("Submit");
            alarms = new ArrayList<>();
            initDateTimePlaceholders();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        alarmListFragment = new AlarmListFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.form_alarms_container, alarmListFragment)
                .commit();
    }

    public void autoFillFormFields() {
        submit.setText("Edit");
        title.setText(getIntent().getStringExtra("title"));
        address.setText(getIntent().getStringExtra("address"));
        notes.setText(getIntent().getStringExtra("notes"));
        date.setText(getIntent().getStringExtra("date"));
        time.setText(getIntent().getStringExtra("time"));

        String category = getIntent().getStringExtra("category");

        categorySpinner.setSelection(
                adapter.getPosition(
                        category.substring(0,1).toUpperCase() + category.substring(1)
                ));
    }

    public void initFields() {
        c = Calendar.getInstance();
        title = (EditText) findViewById(R.id.form_title);
        address = (EditText) findViewById(R.id.form_address);
        notes = (EditText) findViewById(R.id.form_notes);
        categorySpinner = (Spinner) findViewById(R.id.form_category);
        date = (TextView) findViewById(R.id.form_date);
        time = (TextView) findViewById(R.id.form_time);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.form_category_options, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        categorySpinner.setOnItemSelectedListener(this);
        submit = (Button) findViewById(R.id.submit_form);
    }


    public void attachGlobalFormListener() {
        root = findViewById(R.id.form_parent);
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int difference = root.getRootView().getHeight() - root.getHeight();
                if (difference > changeDpToPx(getBaseContext(), 200) && notes.isFocused()) {
                    findViewById(R.id.alarms).setVisibility(RelativeLayout.GONE);
                    findViewById(R.id.form_alarms_container).setVisibility(RelativeLayout.GONE);
                }
                else if(difference < changeDpToPx(getBaseContext(), 200) && notes.isFocused()) {
                    findViewById(R.id.alarms).setVisibility(RelativeLayout.VISIBLE);
                    findViewById(R.id.form_alarms_container).setVisibility(RelativeLayout.VISIBLE);
                }
                else if(difference > changeDpToPx(getBaseContext(), 200) && !notes.isFocused()) {
                    findViewById(R.id.form_footer).setVisibility(RelativeLayout.GONE);
                    if(!findViewById(R.id.alarms).isShown()) {
                        findViewById(R.id.alarms).setVisibility(RelativeLayout.VISIBLE);
                        findViewById(R.id.form_alarms_container).setVisibility(RelativeLayout.VISIBLE);
                    }
                }
                else if(difference < changeDpToPx(getBaseContext(), 200) && !notes.isFocused()) {
                    findViewById(R.id.form_footer).setVisibility(RelativeLayout.VISIBLE);
                    if(!findViewById(R.id.alarms).isShown()) {
                        findViewById(R.id.alarms).setVisibility(RelativeLayout.VISIBLE);
                        findViewById(R.id.form_alarms_container).setVisibility(RelativeLayout.VISIBLE);
                    }
                }

            }
        });
    }


    public float changeDpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }


    public void initDateTimePlaceholders() {
        String dateText = formatWithSeparator(new int[] {
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH) + 1,
                c.get(Calendar.DAY_OF_MONTH)
        }, '-');

        String timeText = formatWithSeparator(new int[] {
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE)
        }, ':');

        date.setText(dateText);
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
        setupBroadcastReceiver();
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

    public void setupBroadcastReceiver() {
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
                } else if(intent.getIntArrayExtra("alarm_Date") != null) {
                    int[] temp = intent.getIntArrayExtra("alarm_Date");
                    tempAlarmDate = formatWithSeparator(temp, '-');
                } else if(intent.getIntArrayExtra("alarm_Time") != null) {
                    int[] temp = intent.getIntArrayExtra("alarm_Time");
                    tempAlarmTime = formatWithSeparator(temp, ':');
                    long tempTime = c.getTimeInMillis();
                    if(tempAlarmTime.length() > 0 && tempAlarmDate.length() > 0) {
                        alarms.add(new AppointmentAlarm(tempTime, tempAlarmDate, tempAlarmTime));
                        alarmListFragment.getList().getAdapter().notifyDataSetChanged();
                        tempAlarmDate = "";
                        tempAlarmTime = "";
                    }
                    setPickerAction("");
                }
            }
        };
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(bReceiver, new IntentFilter("form_activity"));
    }

    public void createNewAlarm(View v) {
        setPickerAction("alarm");
        showTimePickerDialog(v);
        showDatePickerDialog(v);
    }

    public void onFormCancel(View v) {
        Intent i = new Intent();
        i.putExtra("position", getIntent().getIntExtra("position", -1));
        setResult(RESULT_CANCELED, i);
        finish();
    }

    public ArrayList<AppointmentAlarm> getAlarms() {
        return this.alarms;
    }

    public void setPickerAction(String a) {
        this.pickerAction = a;
    }

    public String getPickerAction() {
        return this.pickerAction;
    }

    public void onFormSubmit(View v) {
        Intent result = new Intent();
        result.putExtra("title", title.getText().toString());
        result.putExtra("category", category.toLowerCase());
        result.putExtra("address", address.getText().toString());
        result.putExtra("date", date.getText().toString());
        result.putExtra("time", time.getText().toString());
        result.putExtra("notes", notes.getText().toString());
        result.putExtra("alarms", getAlarms());
        result.putExtra("position", getIntent().getIntExtra("position", -1));
        setResult(RESULT_OK, result);
        finish();
    }
}
