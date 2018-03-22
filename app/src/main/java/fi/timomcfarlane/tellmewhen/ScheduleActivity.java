package fi.timomcfarlane.tellmewhen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;


public class ScheduleActivity extends AppCompatActivity {
    private final static int REQUEST_NEW_APPOINTMENT = 10;
    private RecyclerView recycledList;
    private AppointmentHandler appHandler;
    private BroadcastReceiver bReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        appHandler = new AppointmentHandler(this);
        recycledList = (RecyclerView) findViewById(R.id.recycle_list);
        recycledList.setLayoutManager(new LinearLayoutManager(this));
        recycledList.setAdapter(new RecyclerAdapter(appHandler.getAppointments()));
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        startActivityForResult(i, REQUEST_NEW_APPOINTMENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_NEW_APPOINTMENT) {
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

    public void setupBroadcastReceiver() {
        bReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int code = intent.getIntExtra("action", 400);
                switch(code) {
                    case 200:
                        recycledList.getAdapter().notifyDataSetChanged();
                        break;
                    case 209:
                        recycledList.getAdapter().notifyDataSetChanged();
                        break;
                    case 204:
                        break;
                    case 205:
                        break;
                    case 400:
                        break;
                }

            }
        };
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(bReceiver, new IntentFilter("appointment_handler"));
    }
}
