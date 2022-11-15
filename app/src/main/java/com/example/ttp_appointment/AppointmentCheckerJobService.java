package com.example.ttp_appointment;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class AppointmentCheckerJobService extends JobService implements  SlotRequestCallback{

    private int s;

    public AppointmentCheckerJobService() {
        this.s = 0;
    }



    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        new ApiHandler().startProcess(this, "5020", "2023/06/21", 1);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    @Override
    public void processSuccess(String s) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date receivedDate = simpleDateFormat.parse(s);
            Date previousDate = simpleDateFormat.parse("2023/06/21");
            assert receivedDate != null;
            if (receivedDate.before(previousDate)){
                PushNotifications("good date: " +s);
            }
            else
                PushNotifications("bad date: "+s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        jobFinished(null, true);
    }

    @Override
    public void processFailure(Exception e) {

    }

    private void PushNotifications(String message){
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "TTP Notification")
                .setSmallIcon(com.google.android.material.R.drawable.ic_clock_black_24dp)
                .setContentTitle("New TTP appointment time")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(200, builder.build());
    }

    private void createNotificationChannel(){
        CharSequence name = "TTP Notification";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("TTP Notification", name, importance);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

}
