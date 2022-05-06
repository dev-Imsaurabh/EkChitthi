package com.mac.ekchitthi;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;

public class App extends Application {

    public static final String CHANNEL_ID="EkChitthiService";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);



//        CreateNotificationChannel();
//        StartBackgroundWork();
//        stop();
    }


//    private void CreateNotificationChannel() {
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
//
//            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID,"Ek Chitthi",
//                    NotificationManager.IMPORTANCE_NONE);
//
//            NotificationManager manager = getSystemService(NotificationManager.class);
//
//            manager.createNotificationChannel(serviceChannel);
//        }
//    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onTerminate() {
        super.onTerminate();
//        StartBackgroundWork();

    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    @Override
//    public void onDestroy(@NonNull LifecycleOwner owner) {
//        StartBackgroundWork();
//
//    }









}
