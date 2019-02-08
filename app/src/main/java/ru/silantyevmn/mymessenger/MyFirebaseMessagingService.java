package ru.silantyevmn.mymessenger;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import ru.silantyevmn.mymessenger.ui.messenger.MessengerActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        /*Log.d("MyFirebaseMessagingService", " getFrom() " + remoteMessage.getFrom());
        Log.d("MyFirebaseMessagingService", " getMessageId() " + remoteMessage.getMessageId());
        if (remoteMessage.getData().size() > 0) {
            Log.d("MyFirebaseMessagingService", "  getData>0" + remoteMessage.getData());
        }
        if (remoteMessage.getNotification() != null) {
            Log.d("MyFirebaseMessagingService", " getNotification.getBody()" + remoteMessage.getNotification().getBody());
            Log.d("MyFirebaseMessagingService", " getNotification.getTitle()" + remoteMessage.getNotification().getTitle());
        } else {
            Log.d("MyFirebaseMessagingService", " getNotification " + remoteMessage.getNotification());
        }*/
        sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
    }

    private void sendNotification(String title, String text) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "default_channel_id";
        String channelDescription = "Default Channel";
        // Since android Oreo notification channel is needed.
        //Check if notification channel exists and if not create one
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = manager.getNotificationChannel(channelId);
            if (notificationChannel == null) {
                int importance = NotificationManager.IMPORTANCE_DEFAULT; //Set the importance level
                notificationChannel = new NotificationChannel(channelId, channelDescription, importance);
                notificationChannel.setLightColor(Color.GREEN); //Set if it is necesssary
                notificationChannel.enableVibration(true); //Set if it is necesssary
                manager.createNotificationChannel(notificationChannel);
            }
        }
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        builder.setSmallIcon(R.drawable.ic_favorite_black_24dp)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(Notification.PRIORITY_LOW)
                .setAutoCancel(true)
                .setSound(sound);

        addDefaultIntent(builder);
        manager.notify(0, builder.build());
    }

    private void addDefaultIntent(NotificationCompat.Builder builder) {
        Intent contentIntent = new Intent(this, MessengerActivity.class);
        contentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT; //отменить старый и создать новый
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, contentIntent, flags);
        builder.setContentIntent(pendingIntent);
    }

   /* private void sendNotification(String title, String body) {
        try {
            Intent i = new Intent(this, MessengerActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pi = PendingIntent.getActivity(this,
                    0 *//* Request code *//*,
                    i,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                    getString(R.string.default_notification_channel_id))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title + "jjjjjj")
                    .setContentText(body + "bbbbbb")
                    .setAutoCancel(true)
                    .setSound(sound)
                    .setContentIntent(pi);

            NotificationManager manager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            manager.notify(0, builder.build());
        } catch (Throwable ex) {
            Log.d("MyFirebaseMessagingService", " " + ex.getMessage());
        }
    }*/
}
