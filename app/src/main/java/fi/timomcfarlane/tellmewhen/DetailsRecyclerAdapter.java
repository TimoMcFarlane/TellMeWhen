package fi.timomcfarlane.tellmewhen;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import fi.timomcfarlane.tellmewhen.data.model.AppointmentAlarm;

public class DetailsRecyclerAdapter extends RecyclerView.Adapter<DetailsRecyclerAdapter.ViewHolder>{

    public ArrayList<AppointmentAlarm> alarms;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView card_date;
        public TextView card_time;
        public ViewHolder(View itemView) {
            super(itemView);
            card_date = (TextView) itemView.findViewById(R.id.list_date);
            card_time = (TextView) itemView.findViewById(R.id.list_time);
        }
    }
    public DetailsRecyclerAdapter(ArrayList<AppointmentAlarm> alarms) {
        this.alarms = alarms;
    }

    @NonNull
    @Override
    public DetailsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.schedule_detail_list_item, parent,false);
        DetailsRecyclerAdapter.ViewHolder vh = new DetailsRecyclerAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull DetailsRecyclerAdapter.ViewHolder holder, int position) {
        if(position != RecyclerView.NO_POSITION) {
            AppointmentAlarm[] temp = new AppointmentAlarm[alarms.size()];
            temp = alarms.toArray(temp);
            holder.card_date.setText(temp[position].getDate());
            holder.card_time.setText(temp[position].getTime());
        }
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }
}
