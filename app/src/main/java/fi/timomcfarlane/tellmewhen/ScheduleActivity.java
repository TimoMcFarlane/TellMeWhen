package fi.timomcfarlane.tellmewhen;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;

public class ScheduleActivity extends AppCompatActivity {
    private RecyclerView recycledList;
    private ArrayList<Appointment> apps;
    private AppointmentHandler appHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        appHandler = new AppointmentHandler(this);
        apps = appHandler.getAppointments();

        recycledList = (RecyclerView) findViewById(R.id.recycle_list);
        recycledList.setLayoutManager(new LinearLayoutManager(this));
        recycledList.setAdapter(new RecyclerAdapter(apps));
    }
}
