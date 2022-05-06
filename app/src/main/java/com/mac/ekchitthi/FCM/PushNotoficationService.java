package com.mac.ekchitthi.FCM;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mac.ekchitthi.R;

public class PushNotoficationService extends FirebaseMessagingService {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        String title = message.getNotification().getTitle();
        String body = message.getNotification().getBody();
        final String CHANNEL_ID1 ="HEADS_UP_NOTIFICATIONS";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID1,"MyNotification", NotificationManager.IMPORTANCE_HIGH);
        getSystemService(NotificationManager.class).createNotificationChannel(channel);

        Notification.Builder notification = new Notification.Builder(this,CHANNEL_ID1)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_noti2)
                .setAutoCancel(true);

        NotificationManagerCompat.from(this).notify(3, notification.build());
;

    }
}
