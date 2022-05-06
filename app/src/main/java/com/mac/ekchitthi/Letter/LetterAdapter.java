package com.mac.ekchitthi.Letter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mac.ekchitthi.LetterBox.NewLetterActivity;
import com.mac.ekchitthi.R;

import java.util.ArrayList;
import java.util.HashMap;

public class LetterAdapter extends RecyclerView.Adapter<LetterAdapter.TrackViewAdapter> {
    private Context context;
    private ArrayList<LetterModel> list;

    public LetterAdapter(Context context, ArrayList<LetterModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public TrackViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.letter_item_layout, parent, false);
        return new TrackViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewAdapter holder, @SuppressLint("RecyclerView") int position) {

        LetterModel letterModel = list.get(position);
        if(letterModel.getRead_status().equals("1")){
            holder.reply_btn.setVisibility(View.VISIBLE);
            holder.more_options.setVisibility(View.VISIBLE);

        }
        FirebaseAuth auth;
        FirebaseUser user;
        ProgressDialog pd;
        pd = new ProgressDialog(context);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User");

        if (letterModel.getStamp_image().equals("1")) {
            holder.stamp_image.setImageResource(R.drawable.basic_stamp);
        } else if (letterModel.getStamp_image().equals("2")) {
            holder.stamp_image.setImageResource(R.drawable.stand_stamp);

        } else {
            holder.stamp_image.setImageResource(R.drawable.premium_stamp);

        }

        String[] name = letterModel.getSender_name().split(",");


        holder.recipient_name.setText(name[0]);
        String time = DateUtils.formatDateTime(context, Long.parseLong(name[1]), DateUtils.FORMAT_SHOW_TIME);
        String date = DateUtils.formatDateTime(context, Long.parseLong(name[1]), DateUtils.FORMAT_SHOW_DATE);
        holder.sent_on.setText("Sent on: "+date+" , "+time);
        String[] city = letterModel.getFrom().split(":");
        String[] city1 = letterModel.getTo().split(":");
        holder.from_city.setText("From: " + city[1].trim());
        holder.to_city.setText("To: " + city1[1].trim());
        holder.msg_box.setText(letterModel.getSender_letter());
        holder.date.setText("Delivery date: \n" + letterModel.getSent_time());
        if (letterModel.getRead_status().equals("")) {
            holder.status.setAnimation(R.raw.new_anim);
            holder.status.playAnimation();
        } else {
            holder.status.setVisibility(View.GONE);
        }

        holder.openBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.setMessage("Opening...");
                pd.setCancelable(false);
                pd.show();
                reference.child(letterModel.getSender_id()).child("tracks").child(letterModel.getTicketID())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){

                                    HashMap hp = new HashMap();
                                    hp.put("read_status", "1");
                                    reference.child(letterModel.getSender_id()).child("tracks").child(letterModel.getTicketID()).updateChildren(hp)
                                            .addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {
                                                    reference.child(letterModel.getRece_number()).child("letters").child(letterModel.getTicketID()).updateChildren(hp).addOnCompleteListener(new OnCompleteListener() {
                                                        @Override
                                                        public void onComplete(@NonNull Task task) {


                                                            pd.dismiss();

                                                            Intent intent = new Intent(context, ReadLetterActivity.class);
                                                            intent.putExtra("letter", letterModel.getSender_letter());
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                            context.startActivity(intent);

                                                        }
                                                    });

                                                }
                                            });

                                }else{
                                    HashMap hp = new HashMap();
                                    hp.put("read_status", "1");
                                    try {
                                        pd.dismiss();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    reference.child(letterModel.getRece_number()).child("letters").child(letterModel.getTicketID()).updateChildren(hp).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {


                                            Intent intent = new Intent(context, ReadLetterActivity.class);
                                            intent.putExtra("letter", letterModel.getSender_letter());
                                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                            context.startActivity(intent);




                                        }
                                    });



                                }



                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


            }
        });

        holder.reply_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, NewLetterActivity.class);
                intent.putExtra("sender_number", letterModel.getSender_id());
                intent.putExtra("from", city1[1]);
                intent.putExtra("to", city[1]);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);

            }
        });

        holder.delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child(user.getPhoneNumber()).child("letters").child(letterModel.getTicketID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {



                    }
                });
                notifyItemRemoved(position);

            }

        });

        holder.block_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String action = holder.block_btn.getText().toString();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                if(action.equals("Block")){
                    builder.setMessage("Do you really want to "+action+" this user ?\nAfter this action you are unable to receive any letter from this user!");

                }else{
                    builder.setMessage("Do you really want to "+action+" this user ?");

                }
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(holder.block_btn.getText().equals("Unblock")){
                            reference.child(user.getPhoneNumber()).child("blocked").child(letterModel.getSender_id()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(context, "Unblocked", Toast.LENGTH_SHORT).show();
                                    notifyItemRangeChanged(position, list.size());





                                }
                            });
                        }else{

//                            HashMap hp = new HashMap();
//                            hp.put(letterModel.getSender_id(),letterModel.getSender_id());

                            reference.child(user.getPhoneNumber()).child("blocked").child(letterModel.getSender_id()).setValue(letterModel.getSender_id()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(context, "Blocked", Toast.LENGTH_SHORT).show();
                                    notifyItemRangeChanged(position, list.size());



                                }
                            });


                        }


                    }

                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();

                    }
                });

                builder.show();


            }
        });


        reference.child(user.getPhoneNumber()).child("blocked").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    for(DataSnapshot snapshot1:snapshot.getChildren()){

                        if(letterModel.getSender_id().equals(snapshot1.getValue(String.class))) {
                            holder.block_btn.setText("Unblock");

                        }



                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    public class TrackViewAdapter extends RecyclerView.ViewHolder {
        private TextView recipient_name, from_city, to_city, date, msg_box, sent_by, sent_on,delete_btn,block_btn;
        private ImageView stamp_image;
        private LottieAnimationView status;
        private Button openBtn,reply_btn;
        private LinearLayout more_options;

        public TrackViewAdapter(@NonNull View itemView) {
            super(itemView);


            recipient_name = itemView.findViewById(R.id.recipient_name);
            from_city = itemView.findViewById(R.id.from_city);
            to_city = itemView.findViewById(R.id.to_city);
            status = itemView.findViewById(R.id.status);
            date = itemView.findViewById(R.id.date);
            msg_box = itemView.findViewById(R.id.msg_box);
            openBtn = itemView.findViewById(R.id.open_btn);
            stamp_image = itemView.findViewById(R.id.stamp_image);
            sent_on = itemView.findViewById(R.id.sent_on);
            reply_btn = itemView.findViewById(R.id.reply_btn);
            delete_btn = itemView.findViewById(R.id.delete_btn);
            block_btn = itemView.findViewById(R.id.block_btn);
            more_options = itemView.findViewById(R.id.more_options);

        }
    }


}
