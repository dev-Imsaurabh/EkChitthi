package com.mac.ekchitthi.Stamp;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mac.ekchitthi.R;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Stamps_Adapter_dialog extends RecyclerView.Adapter<Stamps_Adapter_dialog.StampsAdapterView> {

    private Context context;
    private ArrayList<StampModel> list;
    private Dialog dialog;
    private EditText s_from, s_to;
    private TextView r_distance, r_days, r_date, max_words;
    private ImageView stamp_image;
    private TextView dummyTxt;

    public Stamps_Adapter_dialog(Context context, ArrayList<StampModel> list, Dialog dialog, EditText s_from, EditText s_to, TextView r_distance, TextView r_days, TextView r_date, TextView maxWords, ImageView stamp_image, TextView dummyTxt) {
        this.context = context;
        this.list = list;
        this.dialog = dialog;
        this.s_from = s_from;
        this.s_to = s_to;
        this.r_distance = r_distance;
        this.r_days = r_days;
        this.r_date = r_date;
        this.max_words = maxWords;
        this.stamp_image = stamp_image;
        this.dummyTxt=dummyTxt;
    }

    @NonNull
    @Override
    public StampsAdapterView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_stamps_item_layout, parent, false);


        return new StampsAdapterView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StampsAdapterView holder, int position) {
        FirebaseAuth auth;
        FirebaseUser user;
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        DatabaseReference reference;
        reference= FirebaseDatabase.getInstance().getReference().child("CurrentTimeStamp").child(user.getUid().substring(0,4)+"CurrentTimeStamp");
        StampModel stampModel = list.get(position);
        if (stampModel.getStamp_image().equals("1")) {
            holder.image.setImageResource(R.drawable.basic_stamp);
        } else if (stampModel.getStamp_image().equals("2")) {
            holder.image.setImageResource(R.drawable.stand_stamp);

        } else {
            holder.image.setImageResource(R.drawable.premium_stamp);

        }

        holder.id.setText("ID: " + stampModel.getStamp_id());
        holder.from.setText(stampModel.getStamp_from());
        holder.to.setText(stampModel.getStamp_to());
        holder.distance.setText("Distance: " + stampModel.getStamp_distance() + " km");
        holder.days.setText("No of days: " + stampModel.getStamp_days());
        holder.words.setText("Max words: " + stampModel.getStamp_word());



        holder.date.setText("Generated on: " + stampModel.getStamp_date());

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {


                        long timestamp = Long .parseLong(snapshot.getValue().toString());
                        Date date = new Date((timestamp));
                        String[] date1 = getFutureDate(date,Integer.parseInt(stampModel.getStamp_days())).split(",");
                        dummyTxt.setText(date1[2]+","+stampModel.getStamp_id()+","+stampModel.getStamp_image());
//                        Toast.makeText(context, dummyTxt.getText().toString(), Toast.LENGTH_SHORT).show();
                        r_date.setText(date1[1]+","+date1[0]);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                s_from.setText("From: " + stampModel.getStamp_from());
                s_to.setText("To: " + stampModel.getStamp_to());
                r_distance.setText(stampModel.getStamp_distance()+" km");

                max_words.setText(stampModel.getStamp_word());

                if (stampModel.getStamp_image().equals("1")) {
                    stamp_image.setImageResource(R.drawable.basic_stamp);
                    r_days.setText(stampModel.getStamp_days()+" day");

                } else if (stampModel.getStamp_image().equals("2")) {
                    stamp_image.setImageResource(R.drawable.stand_stamp);
                    r_days.setText(stampModel.getStamp_days()+" days");


                } else {
                    stamp_image.setImageResource(R.drawable.premium_stamp);
                    r_days.setText(stampModel.getStamp_days()+" days");


                }


                dialog.dismiss();
            }
        });


    }



    public String getFutureDate(Date currentDate, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.DATE, days);
        DateFormat format = new SimpleDateFormat("dd-MM-yy,h:mm a");
        Date date = null;
        try {
            date = format.parse(String.valueOf(format.format(cal.getTime())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long millies = date.getTime()/1000;


        return format.format(cal.getTime())+","+millies;

    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class StampsAdapterView extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView id, from, to, distance, days, words, date;
        private CardView card;

        public StampsAdapterView(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            id = itemView.findViewById(R.id.id);
            from = itemView.findViewById(R.id.from);
            to = itemView.findViewById(R.id.to);
            distance = itemView.findViewById(R.id.distance);
            days = itemView.findViewById(R.id.days);
            words = itemView.findViewById(R.id.words);
            date = itemView.findViewById(R.id.date);
            card = itemView.findViewById(R.id.card);


        }
    }
}
