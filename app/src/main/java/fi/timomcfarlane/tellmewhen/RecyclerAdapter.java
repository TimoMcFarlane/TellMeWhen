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

import fi.timomcfarlane.tellmewhen.data.model.Appointment;
import fi.timomcfarlane.tellmewhen.utils.CustomCardClickListener;

import static fi.timomcfarlane.tellmewhen.utils.DateManipulationUtils.createVerbalDate;
/**
 * RecyclerAdapter for Recyclerview inside Fragment that displays a list of appointments
 *
 * @author  Timo McFarlane
 * @version 1.0
 * @since   2014-04-24
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{

    private ArrayList<Appointment> apps;

    private Context host;
    private CustomCardClickListener listener;

    /**
     * Init views inside viewholder
     */
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

    /**
     * Inflate view, create viewholder and set cardview onclick using custom interface
     * @param parent default
     * @param viewType default
     * @return ViewHolder (CardView)
     */
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

    /**
     * Set view data according to data at given position
     * @param holder default
     * @param position position of element
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, int position) {
        try {
            Thread.sleep(20);
            if(position != RecyclerView.NO_POSITION) {
                Appointment[] temp = new Appointment[apps.size()];
                temp = apps.toArray(temp);
                holder.card_title.setText(temp[position].getTitle());
                holder.card_address.setText(temp[position].getAddress());
                holder.card_time.setText(temp[position].getTime());
                holder.card_date.setText(createVerbalDate(temp[position].getDate()));
                addCategoryTint(holder, temp[position]);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set card category view image and background color based on provided category
     * @param holder ViewHolder
     * @param appointment Current appointment
     */
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
