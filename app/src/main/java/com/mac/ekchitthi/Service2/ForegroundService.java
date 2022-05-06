package com.mac.ekchitthi.Service2;

import static com.mac.ekchitthi.App.CHANNEL_ID;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

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
import com.mac.ekchitthi.MyWorkerClass;
import com.mac.ekchitthi.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ForegroundService extends Service {
    private DatabaseReference reference, reference1, reference2, reference3;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ArrayList<LetterModel> list;
    private AlarmManager alarmManager;
    private int channel_count=0;
    private Document doc;
    private Elements elements1;
    private long servertime;
    private static final String workTag ="ECservice";


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_noti2)
                .build();

        startForeground(1,notification);


        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                channel_count+=1;
                checkWorker();

//                Toast.makeText(ForegroundService.this, "done", Toast.LENGTH_SHORT).show();


                if(isNetworkAvailable(getApplicationContext())){
                    auth = FirebaseAuth.getInstance();
                    user = auth.getCurrentUser();
//            list = new ArrayList<>();
                    if(user!=null){
                        reference = FirebaseDatabase.getInstance().getReference().child("User").child(user.getPhoneNumber()).child("letters");
                        reference2 = FirebaseDatabase.getInstance().getReference().child("User").child(user.getPhoneNumber()).child("mCount");
                        reference3 = FirebaseDatabase.getInstance().getReference().child("User").child(user.getPhoneNumber()).child("notification");
                        reference1 = FirebaseDatabase.getInstance().getReference().child("CurrentTimeStamp").child(user.getUid().substring(0, 4) + "CurrentTimeStamp");
                        UpdateUser(getApplicationContext());
                    }else{
                        stopSelf();
                    }
                }



                handler.postDelayed(this,60000);
            }
        },1000);






        return START_STICKY;


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void UpdateUser(Context context) {
//        reference1.setValue(ServerValue.TIMESTAMP);
        GetTimeStamp(context);
    }


    public void GetTimeStamp(Context context) {


        timestamp timestamp = new timestamp();
        timestamp.execute();
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

//        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                long timeStamp = Long.parseLong(snapshot.getValue().toString()) / 1000;
//                try {
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                getData(timeStamp, context);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


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
                            int reqCode = 1;
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            Toast.makeText(context, String.valueOf(channel_count), Toast.LENGTH_SHORT).show();

                            showNotification(context,"New Letter","You have a new letter from "+split[2],intent,channel_count);

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

    public void showNotification(Context context, String title, String message, Intent intent, int reqCode) {

        PendingIntent pendingIntent = PendingIntent.getActivity(context, reqCode, intent, PendingIntent.FLAG_ONE_SHOT);
        String CHANNEL_ID = "channel_name";// The id of the channel.
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_noti2)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel Name";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(reqCode, notificationBuilder.build()); // 0 is the request code, it should be unique id

    }

    private class timestamp extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
//           String url = "https://www.google.com/search?q=in+which+city+i+am";
            String url = "https://current-timestamp.com/";
//            String url = "https://www.unixtimestamp.com/";
            String userAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36";

            try {
                doc = Jsoup.connect(url).userAgent(userAgent).get();
//                elements1 = doc.getElementsByClass("value epoch");
                elements1 = doc.getElementsByClass("bt");

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            try {
//                elements1=null;
                if(elements1!=null){

                    servertime = Long.parseLong(elements1.text());
//                Toast.makeText(ForegroundService.this, String.valueOf(servertime), Toast.LENGTH_SHORT).show();
                    getData(servertime,getApplicationContext());

                }else{
//                    Toast.makeText(ForegroundService.this, "not available", Toast.LENGTH_SHORT).show();
                }



            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    @Override
//    public void onTaskRemoved(Intent rootIntent) {
//        super.onTaskRemoved(rootIntent);
//
//        if(!checkServiceRunning(ForegroundService.class)){
////           Toast.makeText(this, "not running", Toast.LENGTH_SHORT).show();
//            startForegroundService(new Intent(this,ForegroundService.class));
//        }
//
//    }
//
//
//    public boolean checkServiceRunning(Class<?> serviceClass){
//        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
//        {
//            if (serviceClass.getName().equals(service.service.getClassName()))
//            {
//                return true;
//            }
//        }
//        return false;
//    }

    private void checkWorker() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(
                MyWorkerClass.class,20, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance().enqueueUniquePeriodicWork(workTag, ExistingPeriodicWorkPolicy.REPLACE,periodicWorkRequest);
//        WorkManager.getInstance().enqueue(periodicWorkRequest);
    }

}
