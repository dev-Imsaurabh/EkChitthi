package com.mac.ekchitthi.Services;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class ECService extends Service {
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        SharedPreferences sharedPreferences = getSharedPreferences("ECService",MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("service","true");
//        editor.apply();




//                Toast.makeText(getApplicationContext(), "i m running", Toast.LENGTH_SHORT).show();

                createNotificationChannel();

                alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                Intent main = new Intent(getApplicationContext(),ECBroadCast.class);

                pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,main,0);

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+60000,
                        AlarmManager.INTERVAL_DAY,pendingIntent);

//                Toast.makeText(getApplicationContext(), "Alarm set Successfully", Toast.LENGTH_SHORT).show();

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

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ekchitthinoti";
            String description = "Channel For Letter Notification";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("ekchitthi", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }
}
