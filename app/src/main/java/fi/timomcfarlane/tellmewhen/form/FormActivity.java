package fi.timomcfarlane.tellmewhen.form;

import android.app.AlarmManager;
import android.app.PendingIntent;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import fi.timomcfarlane.tellmewhen.AppointmentAlarmReceiver;
import fi.timomcfarlane.tellmewhen.R;
import fi.timomcfarlane.tellmewhen.data.model.AppointmentAlarm;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import static fi.timomcfarlane.tellmewhen.utils.DateManipulationUtils.formatWithSeparator;
/**
 * Activity class for displaying a Form to the user. Class is used for creating and editing
 * appointments and on submit returning the new/edited values to the ScheduleActivity.
 *
 * @author  Timo McFarlane
 * @version 1.0
 * @since   2014-04-24
 */
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

    /**
     * In onCreate the application initializes the fields and autofills the fields if the activity
     * was started with an intent containing extra named "edit"
     *
     * @param savedInstanceState default param
     */
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
        setPickerAction("not_alarm");
    }

    /**
     * In onStart the application creates a fragment for displaying added alarms inside the form
     */
    @Override
    protected void onStart() {
        super.onStart();
        alarmListFragment = new AlarmListFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.form_alarms_container, alarmListFragment)
                .commit();
    }

    /**
     * Autofill the form fields with given data that was passed inside the intent
     * when intent was started with "edit" as an extra.
     */
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

    /**
     * Initialize form views.
     */
    public void initFields() {
        c = Calendar.getInstance();
        title = (EditText) findViewById(R.id.form_title);
        address = (EditText) findViewById(R.id.form_address);
        notes = (EditText) findViewById(R.id.form_notes);
        categorySpinner = (Spinner) findViewById(R.id.form_category);
        date = (TextView) findViewById(R.id.form_date);
        time = (TextView) findViewById(R.id.form_time);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.form_category_options, R.layout.category_spinner_item);
        adapter.setDropDownViewResource(R.layout.category_spinner_item);

        categorySpinner.setAdapter(adapter);

        categorySpinner.setOnItemSelectedListener(this);
        submit = (Button) findViewById(R.id.submit_form);
    }

    /**
     * Attach a "listener" to keep track of opening and closing of the android system keyboard.
     * Listener looks for layout changes and calculates if the change was over 200px,
     * assuming its the keyboard that was opened.
     * When keyboard is activated it hides certain elements to prevent overlapping of form
     * views and keeping the user interface clean.
     */
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

    /**
     * Convert given DP values to pixels
     * @param context used for getting DisplayMetrics
     * @param valueInDp Dp value to be converted to pixels
     * @return float representing pixels
     */
    public float changeDpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    /**
     * Set placeholder text to date and time views from current date
     */
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

    /**
     * Display datepicker dialog fragment used for setting date
     * @param v Used to check if setting appointment or alarm date
     */
    public void showDatePickerDialog(View v) {
        if(v.getId() != R.id.form_date) {
            setPickerAction("alarm");
        } else {
            setPickerAction("not_alarm");
        }
        AppCompatDialogFragment dFragment = new DatePickerFragment();
        dFragment.show(getSupportFragmentManager(), "datePicker");
    }

    /**
     * Display timepicker dialog fragment used for setting time
     * @param v Used to check if setting appointment or alarm time
     */
    public void showTimePickerDialog(View v) {
        if(v.getId() != R.id.form_time) {
            setPickerAction("alarm");
        } else {
            setPickerAction("not_alarm");
        }
        AppCompatDialogFragment tFragment = new TimePickerFragment();
        tFragment.show(getSupportFragmentManager(), "timePicker");
    }

    /**
     * onResume register broadcast receiver
     */
    @Override
    protected void onResume() {
        super.onResume();
        setupBroadcastReceiver();
    }

    /**
     * onPause unregister broadcast receiver
     */
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(bReceiver);
        super.onPause();
    }

    /**
     * Spinner onItemSelected, category is set to the value of category spinner.
     * @param adapterView Spinner that has options for category
     * @param view default
     * @param i position of element in spinner list
     * @param l default
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        category = (String) adapterView.getItemAtPosition(i);
    }

    /**
     * Implementation forced by AdapterViewOnItemSelectedListener
     * @param adapterView default param
     */
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /**
     * Register broadcast receiver for receiving data from Date/Timepicker dialogs.
     */
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

    /**
     * Create new alarm using Date/Timepicker dialogs
     * @param v default param for onClick
     */
    public void createNewAlarm(View v) {
        setPickerAction("alarm");
        showTimePickerDialog(v);
        showDatePickerDialog(v);
    }

    /**
     * Remove alarm from list of alarms at given position.
     * Also check if editing existing appointment
     * and remove any alarms from the android system AlarmManager
     * @param position position of view to be removed
     */
    public void removeAlarm(int position) {
        if(getIntent().hasExtra("edit")) {
            Intent intent = new Intent(getBaseContext(), AppointmentAlarmReceiver.class);
            intent.setAction("tellmewhen.appointment.ALARM");
            PendingIntent pi = PendingIntent.getBroadcast(
                    getBaseContext(),
                    (int) alarms.get(position).getCreationTime(),
                    intent,0
            );
            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            am.cancel(pi);
        }
        alarms.remove(position);
        alarmListFragment.getList().getAdapter().notifyDataSetChanged();
    }

    /**
     * Set activity result to canceled and finish
     * @param v default onclick
     */
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

    /**
     * Add all data from form to Intent and set activity result to OK.
     * Activate all added alarms using activateAlarm()
     * Finish activity.
     * @param v default onclick
     */
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
        activateAlarms();
        setResult(RESULT_OK, result);
        finish();
    }

    /**
     * Add all alarms that were created to android AlarmManager
     */
    public void activateAlarms() {
        if(this.alarms.size() > 0) {
            AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(getBaseContext(), AppointmentAlarmReceiver.class);
            i.setAction("tellmewhen.appointment.ALARM");

            i.putExtra("title", title.getText().toString());
            i.putExtra("address", address.getText().toString());
            i.putExtra("date", date.getText().toString());
            i.putExtra("time", time.getText().toString());
            i.addFlags(FLAG_ACTIVITY_SINGLE_TOP);
            Calendar cal = null;
            for (int x = 0; x < this.alarms.size(); x++) {
                AppointmentAlarm alarm = this.alarms.get(x);
                i.putExtra("creationtime", alarm.getCreationTime());
                PendingIntent pi = PendingIntent.getBroadcast(getBaseContext(), (int) alarm.getCreationTime(), i, 0);
                try {
                    cal = createDateTimeFromString(alarm.getDate(), alarm.getTime());
                    alarmMgr.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Format a String date to Date object that can be passed to calendar.
     * @param date String representation of wanted date
     * @param time String representation of wanted time
     * @return Calendar object with time set to the passed arguments
     * @throws ParseException
     */
    public Calendar createDateTimeFromString(String date, String time) throws ParseException {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        cal.setTime(sdf.parse(date + " " + time));
        return cal;
    }
}
