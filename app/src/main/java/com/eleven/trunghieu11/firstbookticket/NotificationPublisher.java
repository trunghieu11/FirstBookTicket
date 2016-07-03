package com.eleven.trunghieu11.firstbookticket;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

import java.util.concurrent.ExecutionException;

/**
 * Created by trunghieu11 on 6/26/2016.
 */
public class NotificationPublisher extends BroadcastReceiver {

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {
        String url = intent.getStringExtra(ShareClass.URL_KEY).toLowerCase();
        String term = intent.getStringExtra(ShareClass.TERM_KEY).toLowerCase();
        String startTerm = intent.getStringExtra(ShareClass.START_TERM_KEY).toLowerCase();
        String endTerm = intent.getStringExtra(ShareClass.END_TERM_KEY).toLowerCase();

        if (ticketAvailable(url, startTerm, term, endTerm)) {
            showNotification(context, intent);
        }

        long futureInMillis = System.currentTimeMillis() + ShareClass.DELAY;
        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra(ShareClass.URL_KEY, url);
        notificationIntent.putExtra(ShareClass.TERM_KEY, term);
        notificationIntent.putExtra(ShareClass.START_TERM_KEY, startTerm);
        notificationIntent.putExtra(ShareClass.END_TERM_KEY, endTerm);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);
    }

    private boolean ticketAvailable(String urlString, String startTerm, String term, String endTerm) {
        AsyncTask<String, Void, Boolean> result = new CheckTerm().execute(urlString, startTerm, term, endTerm);
        try {
            return result.get().booleanValue();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void showNotification(Context context, Intent intent) {
        Intent resultIntent = new Intent(Intent.ACTION_VIEW);
        resultIntent.setData(Uri.parse(intent.getStringExtra(ShareClass.URL_KEY).toLowerCase()));
        PendingIntent pending = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle("Đặt vé xem phim được rồi");
        builder.setContentText("Đã cho đặt vé xem phim");
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        builder.setSmallIcon(R.drawable.lightbulb);
        builder.setContentIntent(pending);

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = builder.build();
        int id = (int)System.currentTimeMillis();
        notificationManager.notify(id, notification);
    }
}
