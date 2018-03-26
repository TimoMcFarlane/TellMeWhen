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


public class ScheduleActivity extends AppCompatActivity {
    private final static int ADD_NEW_APPOINTMENT = 10;
    private BroadcastReceiver bReceiver;
    private AppointmentListFragment listFragment;
    private AppointmentDetailsFragment details;
    private AppointmentHandler appHandler;
    private RecyclerView recycledList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
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
        super.onPause();
    }


    public void addNewAppointment(View v) {
        Intent i = new Intent(this, FormActivity.class);
        startActivityForResult(i, ADD_NEW_APPOINTMENT);
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
            } else if(resultCode == RESULT_CANCELED) {
                Log.d("MSG", "CANCELED");
            }
        }
    }

    public void showListFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container,listFragment)
                .commit();
    }

    public void viewDetailsFragment(int position) {
        details.setArguments(createBundleFromAppointment(position));
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, details)
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
        return payload;
    }

    public void setupBroadcastReceiver() {
        bReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int code = intent.getIntExtra("action", 400);
                recycledList.getAdapter().notifyDataSetChanged();
            }
        };
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(bReceiver, new IntentFilter("appointment_handler"));
    }
}
