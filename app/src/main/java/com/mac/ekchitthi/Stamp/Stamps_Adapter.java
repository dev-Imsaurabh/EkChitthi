package com.mac.ekchitthi.Stamp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mac.ekchitthi.R;

import java.util.ArrayList;

public class Stamps_Adapter extends RecyclerView.Adapter<Stamps_Adapter.StampsAdapterView> {

    private Context context;
    private ArrayList<StampModel>list;

    public Stamps_Adapter(Context context, ArrayList<StampModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public StampsAdapterView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.stamps_item_layout,parent,false);


        return new StampsAdapterView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StampsAdapterView holder, int position) {
        StampModel stampModel = list.get(position);
        if(stampModel.getStamp_image().equals("1")){
            holder.image.setImageResource(R.drawable.basic_stamp);
        }else if(stampModel.getStamp_image().equals("2")){
            holder.image.setImageResource(R.drawable.stand_stamp);

        }else{
            holder.image.setImageResource(R.drawable.premium_stamp);

        }

        holder.id.setText("ID: "+stampModel.getStamp_id());
        holder.from.setText(stampModel.getStamp_from());
        holder.to.setText(stampModel.getStamp_to());
        holder.distance.setText("Distance: "+stampModel.getStamp_distance()+" km");
        holder.days.setText("No of days: "+stampModel.getStamp_days());
        holder.words.setText("Max words: "+stampModel.getStamp_word());
        holder.date.setText("Generated on: "+stampModel.getStamp_date());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class StampsAdapterView extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView id,from,to,distance,days,words,date;
        private CardView card;

        public StampsAdapterView(@NonNull View itemView) {
            super(itemView);

            image=itemView.findViewById(R.id.image);
            id=itemView.findViewById(R.id.id);
            from=itemView.findViewById(R.id.from);
            to=itemView.findViewById(R.id.to);
            distance=itemView.findViewById(R.id.distance);
            days=itemView.findViewById(R.id.days);
            words=itemView.findViewById(R.id.words);
            date=itemView.findViewById(R.id.date);
            card=itemView.findViewById(R.id.card);
        }
    }
}
