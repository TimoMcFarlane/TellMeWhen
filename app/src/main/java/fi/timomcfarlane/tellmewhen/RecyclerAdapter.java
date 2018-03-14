package fi.timomcfarlane.tellmewhen;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{

    private ArrayList<Appointment> apps;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView card;
        public TextView card_title;
        public TextView card_address;
        public ViewHolder(View itemView) {
            super(itemView);
            card = (CardView)itemView.findViewById(R.id.card_view);
            card_title = (TextView)itemView.findViewById(R.id.card_text_view_title);
            card_address = (TextView)itemView.findViewById(R.id.card_text_view_address);
        }
    }

    public RecyclerAdapter(ArrayList<Appointment> set) {
        this.apps = set;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_card_item, parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, int position) {
        Appointment[] temp = new Appointment[apps.size()];
        temp = apps.toArray(temp);
        holder.card_title.setText(temp[position].getTitle());
        holder.card_address.setText(temp[position].getAddress());
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }
}
