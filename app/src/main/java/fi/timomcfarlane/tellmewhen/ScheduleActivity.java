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


public class ScheduleActivity extends AppCompatActivity {
    private final static int ADD_NEW_APPOINTMENT = 10;
    private final static int EDIT_EXISTING_APPOINTMENT = 12;
    private Animation fadeIn;
    private Animation fadeOut;
    private boolean editCanceled;
    private boolean addCanceled;
    private boolean initFromNotification;
    private int editPosition;
    private BroadcastReceiver bReceiver;
    private AppointmentListFragment listFragment;
    private AppointmentDetailsFragment details;
    private RelativeLayout banner;
    private TextView bannerMonth;
    private TextView bannerWeek;
    private Calendar calendarNow;
    private AppointmentHandler appHandler;
    private RecyclerView recycledList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        calendarNow = Calendar.getInstance();
        initBanner();
        initAppointmentHandler();
    }

    public void initAppointmentHandler() {
        appHandler = new AppointmentHandler(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        listFragment = new AppointmentListFragment();
        details = new AppointmentDetailsFragment();
        showListFragment();
    }

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

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(bReceiver);
        super.onPause();
    }

    public void initBanner() {
        banner = (RelativeLayout) findViewById(R.id.banner);
        bannerMonth = (TextView) findViewById(R.id.banner_month);
        bannerWeek = (TextView) findViewById(R.id.banner_week);
        setBanner();
    }

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

    public void addNewAppointment(View v) {
        addCanceled = false;
        Intent i = new Intent(this, FormActivity.class);
        startActivityForResult(i, ADD_NEW_APPOINTMENT);
    }

    public void deleteAppointment(int position) {
        appHandler.removeData(appHandler.getAppointments().remove(position));
        showListFragment();
    }

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

    public void showListFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container,listFragment, "list")
                .commit();
    }

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

    public void activateImmersiveUI() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public AppointmentHandler getAppHandler() {
        return this.appHandler;
    }
}
