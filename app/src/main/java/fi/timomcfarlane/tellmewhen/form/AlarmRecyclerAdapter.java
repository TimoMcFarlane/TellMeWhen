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
import fi.timomcfarlane.tellmewhen.utils.CustomCardClickListener;

/**
 * Class represents a ReyclerView Adapter used in displaying recycler list of alarms
 * inside FormActivity
 *
 * @author  Timo McFarlane
 * @version 1.0
 * @since   2014-04-24
 */
public class AlarmRecyclerAdapter extends RecyclerView.Adapter<AlarmRecyclerAdapter.ViewHolder> {

    private ArrayList<AppointmentAlarm> alarms;
    private Context host;
    private static CustomCardClickListener listener;

    /**
     * Initialize list item views and add a onItemClick listener
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView date;
        public TextView time;
        public CardView del;
        public ViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.form_created_alarm_date);
            time = (TextView) itemView.findViewById(R.id.form_created_alarm_time);
            del = (CardView) itemView.findViewById(R.id.form_created_alarm_remove);
            del.setOnClickListener(view -> listener.onItemClick(view, getAdapterPosition()));
        }
    }

    public AlarmRecyclerAdapter(Context context, ArrayList<AppointmentAlarm> alarms, CustomCardClickListener listener) {
        this.alarms = alarms;
        this.host = context;
        this.listener = listener;
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

    /**
     * Convert ArrayList of alarms to array, and set list view element data according to position.
     * @param holder CardView that represents the most outer view in a recyclerview list item.
     * @param position Indicates where the list item is inside the list
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
