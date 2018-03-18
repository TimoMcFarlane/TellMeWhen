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
import android.view.View;


public class ScheduleActivity extends AppCompatActivity {
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
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(bReceiver);
        super.onPause();
    }


    public void clicked(View v) {
        appHandler.insertNewData(new Appointment("Drink cola", "Näyttelijänkatu 21B19","2018-03-18", "23:59", "work"));
        //appHandler.getNewData();
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
