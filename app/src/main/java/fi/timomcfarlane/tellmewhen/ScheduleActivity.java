package fi.timomcfarlane.tellmewhen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.transition.Visibility;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import fi.timomcfarlane.tellmewhen.data.AppointmentHandler;
import fi.timomcfarlane.tellmewhen.data.model.Appointment;
import fi.timomcfarlane.tellmewhen.data.model.AppointmentAlarm;
import fi.timomcfarlane.tellmewhen.form.FormActivity;

/**
 * ScheduleActivity is the main activity of this application.
 * This activity displays the main view to the user using a recyclerview
 * for displaying scheduled appointments
 *
 * @author  Timo McFarlane
 * @version 1.0
 * @since   2014-04-24
 */
public class ScheduleActivity extends AppCompatActivity {
    /**
     * ADD_NEW_APPOINTMENT used as a code to indicate action for startActivityForResult
     */
    private final static int ADD_NEW_APPOINTMENT = 10;
    /**
     * EDIT_EXISTING_APPOINTMENT used as a code to indicate action for startActivityForResult
     */
    private final static int EDIT_EXISTING_APPOINTMENT = 12;
    /**
     * editCanceled used as a flag for checking if edit in ActivityForm was cancelled
     */
    private boolean editCanceled;
    /**
     * addCanceled used as a flag for checking if add in ActivityForm was cancelled
     */
    private boolean addCanceled;
    /**
     * initFromNotification used as a flag for checking if activity was started from notification
     */
    private boolean initFromNotification;
    private int editPosition;
    /**
     * Local broadcastreceiver used for receiving broadcasts inside application
     */
    private BroadcastReceiver bReceiver;
    /**
     * Fragment for displaying recycler view list of appointments
     */
    private AppointmentListFragment listFragment;
    /**
     * Fragment for displaying details of one appointment
     */
    private AppointmentDetailsFragment details;
    private RelativeLayout banner;
    private TextView bannerMonth;
    private TextView bannerWeek;
    private Calendar calendarNow;
    private AppointmentHandler appHandler;
    private RecyclerView recycledList;


    /**
     * In onCreate of this activity the application initializes the main view
     * and instantiates an AppointmentHandler to get data for all the necessary view components.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        calendarNow = Calendar.getInstance();
        initBanner();
        initAppointmentHandler();
    }

    /**
     * Get an instance of the AppointmentHandler class.
     */
    public void initAppointmentHandler() {
        appHandler = new AppointmentHandler(this);
    }

    /**
     * In onStart of this activity the necessary fragments are instantiated
     * and the listFragment is called to be displayed.
     */
    @Override
    protected void onStart() {
        super.onStart();
        listFragment = new AppointmentListFragment();
        details = new AppointmentDetailsFragment();
        showListFragment();
    }

    /**
     * In onResume of this activity the recyclerview list is retrieved
     * and a broadcastreceiver is setup to listen for incoming broadcasts
     * from within the application.
     *
     * There is also a check for if the software was opened from a notification using an intent.
     */
    @Override
    protected void onResume() {
        super.onResume();
        recycledList = listFragment.getList();
        setupBroadcastReceiver();
        activateImmersiveUI();
        if(getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().size() > 0) {
            this.initFromNotification = true;
        } else {
            this.initFromNotification = false;
        }
    }

    /**
     * In onPostResume of this activity the application checks if an operation was cancelled
     * or if the application was initially started from a notification.
     *
     * Depending on what state the application was in, show the correct fragment.
     */
    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(editCanceled) {
            viewDetailsFragment(editPosition);
        }
        if(addCanceled) {
            showListFragment();
        }

        if(initFromNotification) {
            Bundle extras = getIntent().getExtras();
            int position = appHandler.findByAlarm(extras.getLong("details"));
            viewDetailsFragment(position);
        }
    }

    /**
     * In onPause of this activity, unregister broadcast receiver.
     */
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(bReceiver);
        super.onPause();
    }

    /**
     * In initBanner the application initializes necessary views for the banner portion
     * of the main view.
     */
    public void initBanner() {
        banner = (RelativeLayout) findViewById(R.id.banner);
        bannerMonth = (TextView) findViewById(R.id.banner_month);
        bannerWeek = (TextView) findViewById(R.id.banner_week);
        setBanner();
    }

    /**
     * In setBanner the application sets the main view banner texts and image.
     * Images are set based on the 4 seasons (winter, spring, summer, autumn).
     */
    public void setBanner() {
        bannerWeek.setText("WEEK " + calendarNow.get(Calendar.WEEK_OF_YEAR));
        bannerMonth.setText(calendarNow.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH));
        if (calendarNow.get(Calendar.MONTH) == 11 || calendarNow.get(Calendar.MONTH) <= 1) {
            banner.setBackgroundResource(R.drawable.winter);
        } else if (calendarNow.get(Calendar.MONTH) >= 2 && calendarNow.get(Calendar.MONTH) <= 4) {
            banner.setBackgroundResource(R.drawable.spring);
        } else if (calendarNow.get(Calendar.MONTH) >= 5 && calendarNow.get(Calendar.MONTH) <= 7) {
            banner.setBackgroundResource(R.drawable.summer);
        } else {
            banner.setBackgroundResource(R.drawable.autumn);
        }
    }


    /**
     * In addNewAppointment activity starts FormActivity for a result.
     * On success the form returns necessary data to build a new appointment.
     * addCanceled is used for flagging onPostResume method conditionals
     *
     * @param v Necessary View parameter for view onClick
     */
    public void addNewAppointment(View v) {
        addCanceled = false;
        Intent i = new Intent(this, FormActivity.class);
        startActivityForResult(i, ADD_NEW_APPOINTMENT);
    }

    /**
     * In deleteAppointment activity requests AppointmentHandler to remove one Appointment
     * at the given position.
     * After delete display listFragment in view.
     *
     * @param position Integer value used for removing data at arraylist index
     */
    public void deleteAppointment(int position) {
        appHandler.removeData(appHandler.getAppointments().remove(position));
        showListFragment();
    }

    /**
     * In editAppointment create a bundle from the data inside the current Appointment
     * and send it to the FormActivity for autofilling.
     *
     * @param position Integer value used for getting data at arraylist index
     */
    public void editAppointmentAtPosition(int position) {
        editPosition = position;
        editCanceled = false;
        Intent i = new Intent(this, FormActivity.class);
        Bundle b = createBundleFromAppointment(position);
        i.putExtras(b);
        i.putExtra("edit", true);
        i.putExtra("position", position);
        startActivityForResult(i, EDIT_EXISTING_APPOINTMENT);
    }

    /**
     * In changeWeek application adds or removes one week from the current calendar time value
     * and requests new data from AppointmentHandler based on what week it is.
     * Update banner according to new week data.
     *
     * @param v Necessary parameter for view onClick used to distinguish which arrow was triggered.
     */
    public void changeWeek(View v) {
        if(v.getId() == R.id.left_arrow) {
            calendarNow.add(Calendar.DAY_OF_YEAR, -7);

            appHandler.removeWeekFromCurrentWeekOffset();
        } else if(v.getId() == R.id.right_arrow) {
            calendarNow.add(Calendar.DAY_OF_YEAR, 7);
            appHandler.addWeekToCurrentWeekOffset();
        }
        setBanner();
    }

    /**
     * In onActivityResult of this activity the application creates or edits appointments
     * depending on the users actions.
     *
     * @param requestCode Integer used to distinguish what action was used in startActivityForResult
     * @param resultCode Integer used to check if action was successful in startActivityForResult
     * @param data Intent passed from Activity that was started using startActivityForResult
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ADD_NEW_APPOINTMENT) {
            if(resultCode == RESULT_OK) {
                appHandler.insertNewData(new Appointment(
                        data.getStringExtra("title"),
                        data.getStringExtra("address"),
                        data.getStringExtra("date"),
                        data.getStringExtra("time"),
                        data.getStringExtra("notes"),
                        data.getStringExtra("category"),
                        (ArrayList<AppointmentAlarm>) data.getSerializableExtra("alarms")
                ));
            } else if(resultCode == RESULT_CANCELED) {
                addCanceled = true;
            }
        }
        else if(requestCode == EDIT_EXISTING_APPOINTMENT) {
            if(resultCode == RESULT_OK) {
                Appointment edited = appHandler.getAppointments()
                                        .get(data.getExtras().getInt("position"));

                edited.setTitle(data.getStringExtra("title"));
                edited.setAddress(data.getStringExtra("address"));
                edited.setDate(data.getStringExtra("date"));
                edited.setTime(data.getStringExtra("time"));
                edited.setNotes(data.getStringExtra("notes"));
                edited.setCategory(data.getStringExtra("category"));
                edited.setAlarms((ArrayList<AppointmentAlarm>) data.getSerializableExtra("alarms"));
                appHandler.updateExistingData(
                        data.getIntExtra("position", 999), edited);
            } else if(resultCode == RESULT_CANCELED) {
                editCanceled = true;
            }
        }
    }

    /**
     * Display listFragment that has a recycler view populated with list items.
     */
    public void showListFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container,listFragment, "list")
                .commit();
    }

    /**
     * View details of one recycler view list item at the given position inside a separate fragment.
     *
     * @param position Integer value used to distinguish which appointment was tapped.
     */
    public void viewDetailsFragment(int position) {
        Log.d("MSG", "Position value: " + position);
        if(position != -1) {
            details.setArguments(createBundleFromAppointment(position));
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, details, "details")
                    .commit();
        }
    }

    /**
     * In createBundleFromAppointment the application creates a Bundle object based on data provided
     * by the appointment at given position.
     * @param position Integer value used in getting the correct appointment
     * @return Bundle object containing necessary data extras
     */
    public Bundle createBundleFromAppointment(int position) {
        Appointment clickedApp = appHandler.getAppointments().get(position);
        Bundle payload = new Bundle();
        payload.putString("title", clickedApp.getTitle());
        payload.putString("address", clickedApp.getAddress());
        payload.putString("date", clickedApp.getDate());
        payload.putString("time", clickedApp.getTime());
        payload.putString("category", clickedApp.getCategory());
        payload.putString("notes", clickedApp.getNotes());
        payload.putSerializable("alarms", clickedApp.getAlarms());
        payload.putInt("position", position);
        return payload;
    }

    /**
     * Register local broadcast receiver to receive broadcasts from within the application.
     * Depending on the broadcasted "action code" the application will run necessary methods
     * to update views.
     */
    public void setupBroadcastReceiver() {
        bReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int code = intent.getIntExtra("action", 400);
                switch(code) {
                    case 200:
                        recycledList.getAdapter().notifyDataSetChanged();
                        break;
                    case 201:

                        break;
                    case 204:
                        recycledList.getAdapter().notifyDataSetChanged();
                        break;
                    case 205:
                        recycledList.getAdapter().notifyDataSetChanged();
                        showListFragment();
                        break;
                }

            }
        };
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(bReceiver, new IntentFilter("appointment_handler"));
    }

    /**
     * In activateImmersiveUI the application hides the system status bar and default device
     * navigation. By swiping close to the top or bottom of the screen you can show the UI again.
     */
    public void activateImmersiveUI() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    /**
     * In getAppHandler the applications retrieves this activitys current instance of
     * AppointmentHandler
     *
     * @return AppointmentHandler object
     */
    public AppointmentHandler getAppHandler() {
        return this.appHandler;
    }
}
