package com.brandsin.user.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.brandsin.user.R;
import com.brandsin.user.ui.activity.auth.AuthActivity;
import com.brandsin.user.ui.activity.home.HomeActivity;

public class NavigationService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        String orderId = intent.getStringExtra("order_id");
        String chatId = intent.getStringExtra("chat_id");
        String notificationTitle = intent.getStringExtra("title");
        String notificationBody = intent.getStringExtra("body");

        // Determine the destination based on the data received
        Intent navigationIntent;

        if (orderId != null) {
            // Navigate to AuthActivity with order_id
            navigationIntent = new Intent(this, AuthActivity.class);
            navigationIntent.putExtra("order_id", orderId);
        } else if (chatId != null) {
            // Navigate to HomeActivity with chat_id
            navigationIntent = new Intent(this, HomeActivity.class);
            navigationIntent.putExtra("chat_id", chatId);
        } else {
            // Default navigation to AuthActivity
            navigationIntent = new Intent(this, AuthActivity.class);
        }

        navigationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Use TaskStackBuilder for proper back navigation
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(navigationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Show the notification
        showNotification(notificationTitle, notificationBody, pendingIntent);

        // Stop the service after navigation is handled
        stopSelf();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // Return null for non-bound services
    }

    private void showNotification(String title, String body, PendingIntent pendingIntent) {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "channel_id")
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setContentInfo(title)
                .setLargeIcon(icon)
                .setColor(getResources().getColor(R.color.color_primary))
                .setLights(Color.RED, 1000, 300)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.mipmap.ic_launcher);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "channel_id", "channel_name", NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("channel description");
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }
}

