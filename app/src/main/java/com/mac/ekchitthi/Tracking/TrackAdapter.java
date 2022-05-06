package com.mac.ekchitthi.Tracking;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.mac.ekchitthi.Letter.LetterModel;
import com.mac.ekchitthi.R;

import java.util.ArrayList;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewAdapter> {
    private Context context;
    private ArrayList<LetterModel>list;

    public TrackAdapter(Context context, ArrayList<LetterModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public TrackViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.track_item_layout,parent,false);
        return new TrackViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewAdapter holder, int position) {
        LetterModel letterModel = list.get(position);

        try {
            if (letterModel.getStamp_image().equals("1")) {
                holder.stamp_image.setImageResource(R.drawable.basic_stamp);
            } else if (letterModel.getStamp_image().equals("2")) {
                holder.stamp_image.setImageResource(R.drawable.stand_stamp);

            } else {
                holder.stamp_image.setImageResource(R.drawable.premium_stamp);

            }

            holder.recipient_number.setText(letterModel.getRece_number());
            String[] city = letterModel.getFrom().split(":");
            String[] city1 = letterModel.getTo().split(":");
            holder.from_city.setText("From: "+city[1].trim());
            holder.to_city.setText("To: "+city1[1].trim());
            holder.msg_box.setText(letterModel.getSender_letter());
            holder.date.setText("Delivery date: \n"+letterModel.getSent_time());

            if(letterModel.getRead_status().equals("")){
                holder.status.setText("Not Opened");
                holder.status.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_invalid, 0);

            }else{
                holder.status.setText("Opened");
                holder.status.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class TrackViewAdapter extends RecyclerView.ViewHolder {
        private TextView recipient_number,from_city,to_city,status,date,msg_box;
        private ImageView stamp_image;
        private LottieAnimationView animationView;

        public TrackViewAdapter(@NonNull View itemView) {
            super(itemView);


            recipient_number=itemView.findViewById(R.id.recipient_number);
            from_city=itemView.findViewById(R.id.from_city);
            to_city=itemView.findViewById(R.id.to_city);
            status=itemView.findViewById(R.id.status);
            date=itemView.findViewById(R.id.date);
            msg_box=itemView.findViewById(R.id.msg_box);
            stamp_image=itemView.findViewById(R.id.stamp_image);
            animationView=itemView.findViewById(R.id.animationView);

        }
    }
}
