package com.mac.ekchitthi.Services;

import static android.content.Context.ACTIVITY_SERVICE;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.mac.ekchitthi.MainActivity;
import com.mac.ekchitthi.MyWorkerClass;
import com.mac.ekchitthi.Service2.ForegroundService;

import java.util.concurrent.TimeUnit;

public class ECBootCast extends BroadcastReceiver {
    private AlarmManager alarmManager;
    private static final String workTag ="ECservice";


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
//        Toast.makeText(context, "Starting EC service", Toast.LENGTH_SHORT).show();
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
            if(!checkServiceRunning(ForegroundService.class,context)){
                context.startForegroundService(new Intent(context,ForegroundService.class));
                checkWorker();

            }


        }


//        setNot(context);

    }


    public boolean checkServiceRunning(Class<?> serviceClass,Context context){
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (serviceClass.getName().equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
    }


//    private void setNot(Context context) {
//        Intent i = new Intent(context, MainActivity.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);
//
//        createNotificationChannel(context);
//
//        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//
//        Intent main = new Intent(context, ECBroadCast.class);
//
//        pendingIntent = PendingIntent.getBroadcast(context, 0, main, 0);
//
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60000,
//                AlarmManager.INTERVAL_DAY, pendingIntent);
//    }
//
//    private void createNotificationChannel(Context context) {
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = "ekchitthinoti";
//            String description = "Channel For Letter Notification";
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//            NotificationChannel channel = new NotificationChannel("ekchitthi", name, importance);
//            channel.setDescription(description);
//
//            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//
//        }
//
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
