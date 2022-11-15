package com.example.ttp_appointment;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements  SlotRequestCallback{

    private EditText locationInput;
    private EditText dateInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        createNotificationChannel();
        scheduledJob();
        locationInput = findViewById(R.id.locationInput);
        dateInput = findViewById(R.id.dateInput);
        Button getResultButton = findViewById(R.id.getResultButton);
        getResultButton.setOnClickListener(v -> getResult());
    }

    private void getResult(){
        int typeOfRequest;
        if (dateInput.getText().toString().equals(""))
            typeOfRequest = 0;
        else
            typeOfRequest = 1;
        new ApiHandler().startProcess(this, locationInput.getText().toString(), dateInput.getText().toString(), typeOfRequest);
    }

    @Override
    public void processSuccess(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Result");
        builder.setMessage("The soonest available slot is " + s);
//        builder.
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void processFailure(Exception e) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Error");
        builder.setMessage(e.getMessage());
    }

//    private void createNotificationChannel(){
//        CharSequence name = "TTP Notification";
//        int importance = NotificationManager.IMPORTANCE_DEFAULT;
//        NotificationChannel channel = new NotificationChannel("TTP Notification", name, importance);
//
//        NotificationManager notificationManager = getSystemService(NotificationManager.class);
//        notificationManager.createNotificationChannel(channel);
//    }

    private void scheduledJob() {
        JobScheduler jobScheduler = (JobScheduler) this.getSystemService(JOB_SCHEDULER_SERVICE);
        ComponentName componentName = new ComponentName(this, AppointmentCheckerJobService.class);
        JobInfo jobInfo = new JobInfo.Builder(0, componentName).setPeriodic(1000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NOT_ROAMING)
                .setPersisted(true)
                .build();
        if (jobScheduler != null)
            jobScheduler.schedule(jobInfo);
    }
}