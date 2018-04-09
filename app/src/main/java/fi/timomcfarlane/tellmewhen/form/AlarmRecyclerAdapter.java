package fi.timomcfarlane.tellmewhen.form;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import fi.timomcfarlane.tellmewhen.R;
import fi.timomcfarlane.tellmewhen.data.model.AppointmentAlarm;

public class AlarmRecyclerAdapter extends RecyclerView.Adapter<AlarmRecyclerAdapter.ViewHolder> {

    private ArrayList<AppointmentAlarm> alarms;
    private Context host;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Views to be initialised
        public TextView date;
        public TextView time;
        public ViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.form_created_alarm_date);
            time = (TextView) itemView.findViewById(R.id.form_created_alarm_time);
        }
    }

    public AlarmRecyclerAdapter(Context context, ArrayList<AppointmentAlarm> alarms) {
        this.alarms = alarms;
        this.host = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.form_alarm_item, parent, false);
        ViewHolder vh = new ViewHolder(cv);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Configure data to views using holder.view_id.setText
        AppointmentAlarm[] temp = new AppointmentAlarm[alarms.size()];
        temp = alarms.toArray(temp);
        holder.date.setText(temp[position].getDate());
        holder.time.setText(temp[position].getTime());
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }
}
