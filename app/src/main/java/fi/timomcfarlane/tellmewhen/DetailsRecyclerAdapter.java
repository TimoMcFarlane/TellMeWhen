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

/**
 * Adapter used for displaying alarms inside DetailsFragment alarm recyclerview.
 *
 * @author  Timo McFarlane
 * @version 1.0
 * @since   2014-04-24
 */
public class DetailsRecyclerAdapter extends RecyclerView.Adapter<DetailsRecyclerAdapter.ViewHolder>{

    public ArrayList<AppointmentAlarm> alarms;

    /**
     * Initialize ViewHolder views
     */
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

    /**
     * onCreateViewHolder inflate CardView and get ViewHolder
     * @param parent default
     * @param viewType default
     * @return ViewHolder
     */
    @NonNull
    @Override
    public DetailsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.schedule_detail_list_item, parent,false);
        DetailsRecyclerAdapter.ViewHolder vh = new DetailsRecyclerAdapter.ViewHolder(v);
        return vh;
    }

    /**
     * Convert data to array and set view texts based on position
     * @param holder ViewHolder
     * @param position position of view inside collection
     */
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
