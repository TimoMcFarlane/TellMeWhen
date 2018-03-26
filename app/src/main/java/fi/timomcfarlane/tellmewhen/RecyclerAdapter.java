package fi.timomcfarlane.tellmewhen;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;

import static fi.timomcfarlane.tellmewhen.DateManipulationUtils.createVerbalDate;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{

    private ArrayList<Appointment> apps;

    private Context host;
    private CustomCardClickListener listener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView card;
        public TextView card_title;
        public TextView card_address;
        public TextView card_time;
        public TextView card_date;
        public RelativeLayout card_category_image;
        public ImageView category_image;

        public ViewHolder(View itemView) {
            super(itemView);
            card = (CardView)itemView.findViewById(R.id.card_view);
            card_title = (TextView)itemView.findViewById(R.id.card_text_view_title);
            card_address = (TextView)itemView.findViewById(R.id.card_text_view_address);
            card_category_image = (RelativeLayout) itemView.findViewById(R.id.card_category_img);
            category_image = (ImageView)itemView.findViewById(R.id.category_img);
            card_time = (TextView)itemView.findViewById(R.id.card_text_view_time);
            card_date = (TextView)itemView.findViewById(R.id.card_text_view_date);
        }
    }

    public RecyclerAdapter(Context context, ArrayList<Appointment> set, CustomCardClickListener listener) {
        this.apps = set;
        this.host = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_card_item, parent,false);
        ViewHolder vh = new ViewHolder(v);
        v.setOnClickListener(view -> listener.onItemClick(view, vh.getAdapterPosition()));
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, int position) {
        Appointment[] temp = new Appointment[apps.size()];
        temp = apps.toArray(temp);
        holder.card_title.setText(temp[position].getTitle());
        holder.card_address.setText(temp[position].getAddress());
        holder.card_time.setText(temp[position].getTime());
        holder.card_date.setText(createVerbalDate(temp[position].getDate()));
        addCategoryTint(holder, temp[position]);
    }

    private void addCategoryTint(ViewHolder holder, Appointment appointment) {
        switch(appointment.getCategory()) {
            case "work":
                holder.card_category_image.setBackgroundResource(R.color.work);
                holder.category_image.setImageResource(R.drawable.work_24dp);
                break;
            case "social":
                holder.card_category_image.setBackgroundResource(R.color.social);
                holder.category_image.setImageResource(R.drawable.social_24dp);
                break;
            case "family":
                holder.card_category_image.setBackgroundResource(R.color.family);
                holder.category_image.setImageResource(R.drawable.family_24dp);
                break;
            case "personal":
                holder.card_category_image.setBackgroundResource(R.color.personal);
                holder.category_image.setImageResource(R.drawable.personal_24dp);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }
}
