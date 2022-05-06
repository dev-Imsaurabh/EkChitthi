package com.mac.ekchitthi.Services;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.mac.ekchitthi.Letter.LetterModel;
import com.mac.ekchitthi.MainActivity;
import com.mac.ekchitthi.R;

import java.util.ArrayList;

public class ECBroadCast extends BroadcastReceiver {
    private DatabaseReference reference, reference1, reference2, reference3;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ArrayList<LetterModel> list;
    private AlarmManager alarmManager;

    @Override
    public void onReceive(Context context, Intent intent) {





        SendNotification(context);

        if(isNetworkAvailable(context)){
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();
//            list = new ArrayList<>();
            if(user!=null){
                reference = FirebaseDatabase.getInstance().getReference().child("User").child(user.getPhoneNumber()).child("letters");
                reference2 = FirebaseDatabase.getInstance().getReference().child("User").child(user.getPhoneNumber()).child("mCount");
                reference3 = FirebaseDatabase.getInstance().getReference().child("User").child(user.getPhoneNumber()).child("notification");
                reference1 = FirebaseDatabase.getInstance().getReference().child("CurrentTimeStamp").child(user.getUid().substring(0, 4) + "CurrentTimeStamp");
                UpdateUser(context);
            }
        }


    }


    private void SendNotification(Context context) {

        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);

        createNotificationChannel(context);

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent main = new Intent(context, ECBroadCast.class);

        pendingIntent = PendingIntent.getBroadcast(context, 0, main, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60000,
                AlarmManager.INTERVAL_DAY, pendingIntent);

//        Toast.makeText(context, "Alarm set Successfully", Toast.LENGTH_SHORT).show();

    }

    private void UpdateUser(Context context) {
        reference1.setValue(ServerValue.TIMESTAMP);
        GetTimeStamp(context);
    }

    public void GetTimeStamp(Context context) {
//        reference2.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                int count = Integer.parseInt(snapshot.getValue().toString());
//
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long timeStamp = Long.parseLong(snapshot.getValue().toString()) / 1000;
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getData(timeStamp, context);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void getData(long serverTimestamp, Context context) {

        reference3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

//                list.clear();
//                list=new ArrayList<>();

                if (snapshot.exists()) {


                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
//                        LetterModel data = snapshot1.getValue(LetterModel.class);
//                        list.add(data);
                        String[] split = snapshot1.getValue().toString().split(",");

                        long time = Long.parseLong(split[0].trim());
//                        Toast.makeText(context, String.valueOf(split[0]), Toast.LENGTH_SHORT).show();

                        long compare = time-serverTimestamp;
                        String s = String.valueOf(compare);
//                        Toast.makeText(context,s,Toast.LENGTH_SHORT).show();
                        if(s.contains("-")){

                            Intent i = new Intent(context, MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "ekchitthi")
                                    .setSmallIcon(R.drawable.icon_mail)
                                    .setContentTitle("New Letters")
                                    .setContentText("You have new letter from "+split[2])
                                    .setAutoCancel(true)
                                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setContentIntent(pendingIntent);

                            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                            notificationManagerCompat.notify(123, builder.build());
                            reference3.child(split[1].trim()).removeValue();

                        }else{
//                            Toast.makeText(context, "nothing happening", Toast.LENGTH_SHORT).show();
                        }
//                        int c= Integer.parseInt(s);

//                        if(!String.valueOf(c).equals("0")){
//
//                            Intent i = new Intent(context, MainActivity.class);
//                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);
//
//                            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "ekchitthi")
//                                    .setSmallIcon(R.drawable.icon_mail)
//                                    .setContentTitle("New Letters")
//                                    .setContentText("You have " + String.valueOf(compare) + " new letters")
//                                    .setAutoCancel(true)
//                                    .setDefaults(NotificationCompat.DEFAULT_ALL)
//                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
//                                    .setContentIntent(pendingIntent);
//
//                            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
//                            notificationManagerCompat.notify(123, builder.build());
//
//                        }else{
//                            Toast.makeText(context,"nothing happening",Toast.LENGTH_SHORT).show();
//                        }





//                        Timestamp timestamp = new Timestamp(serverTimestamp);
//                        Timestamp timestamp2 = new Timestamp(time);
//                        if (timestamp.compareTo(timestamp2) >0) {
////                            list.add(0,data);
//                            Intent i = new Intent(context, MainActivity.class);
//                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            PendingIntent pendingIntent = PendingIntent.getActivity(context,0,i,0);
//
//                            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"ekchitthi")
//                                    .setSmallIcon(R.drawable.icon_mail)
//                                    .setContentTitle("New Letters")
//                                    .setContentText("You have "+split[0]+" new letters")
//                                    .setAutoCancel(true)
//                                    .setDefaults(NotificationCompat.DEFAULT_ALL)
//                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
//                                    .setContentIntent(pendingIntent);
//
//                            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
//                            notificationManagerCompat.notify(123,builder.build());
////                            reference3.child(split[1].trim()).removeValue();
//                        }else{
//
//                            Intent i = new Intent(context, MainActivity.class);
//                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            PendingIntent pendingIntent = PendingIntent.getActivity(context,0,i,0);
//
//                            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"ekchitthi")
//                                    .setSmallIcon(R.drawable.icon_mail)
//                                    .setContentTitle("New Letters")
//                                    .setContentText("You have "+split[0]+"letters")
//                                    .setAutoCancel(true)
//                                    .setDefaults(NotificationCompat.DEFAULT_ALL)
//                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
//                                    .setContentIntent(pendingIntent);
//
//                            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
//                            notificationManagerCompat.notify(123,builder.build());
//
//
//
//                        }


                    }


//                    reference2.setValue(list.size());


                } else {
                }

//                SharedPreferences pref = context.getSharedPreferences("post", MODE_PRIVATE);
//                SharedPreferences.Editor editor = pref.edit();
//                editor.putString("count", String.valueOf(list.size()));
//                editor.apply();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void createNotificationChannel(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ekchitthinoti";
            String description = "Channel For Letter Notification";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("ekchitthi", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }

    public boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {


            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {

                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {

                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {

                        return true;
                    }
                }
            }
        }


        return false;

    }

}
