package fi.timomcfarlane.tellmewhen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;


public class ScheduleActivity extends AppCompatActivity {
    private final static int ADD_NEW_APPOINTMENT = 10;
    private final static int EDIT_EXISTING_APPOINTMENT = 12;
    private BroadcastReceiver bReceiver;
    private AppointmentListFragment listFragment;
    private AppointmentDetailsFragment details;
    private AppointmentHandler appHandler;
    private RecyclerView recycledList;
    private TextView bannerMonth;
    private TextView bannerWeek;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        initBanner();
        listFragment = new AppointmentListFragment();
        details = new AppointmentDetailsFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, listFragment, "list")
                .addToBackStack(null)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        appHandler = listFragment.getAppHandler();
        recycledList = listFragment.getList();
        setupBroadcastReceiver();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(bReceiver);
        Log.d("MSG", "ScheduleActivity on Pause");
        super.onPause();
    }

    public void initBanner() {
        bannerMonth = (TextView) findViewById(R.id.banner_month);
        bannerWeek = (TextView) findViewById(R.id.banner_week);
        calendar = Calendar.getInstance();
        bannerMonth.setText(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH));
        bannerWeek.setText("WEEK " + calendar.get(Calendar.WEEK_OF_YEAR));
    }



    public void addNewAppointment(View v) {
        Intent i = new Intent(this, FormActivity.class);
        startActivityForResult(i, ADD_NEW_APPOINTMENT);
    }

    public void deleteAppointment(int position) {
        appHandler.removeData(appHandler.getAppointments().remove(position));
        showListFragment();
    }

    public void editAppointmentAtPosition(int position) {
        Intent i = new Intent(this, FormActivity.class);
        Bundle b = createBundleFromAppointment(position);
        i.putExtras(b);
        i.putExtra("edit", true);
        i.putExtra("position", position);
        startActivityForResult(i, EDIT_EXISTING_APPOINTMENT);
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
                        data.getStringExtra("category")
                ));
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
                appHandler.updateExistingData(
                        data.getIntExtra("position", 999), edited);
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
        details.setArguments(createBundleFromAppointment(position));
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, details, "details")
                .commit();
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

                        break;
                    case 201:

                        break;
                    case 204:

                        break;
                    case 205:
                        showListFragment();
                        break;
                }
                recycledList.getAdapter().notifyDataSetChanged();
            }
        };
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(bReceiver, new IntentFilter("appointment_handler"));
    }
}
