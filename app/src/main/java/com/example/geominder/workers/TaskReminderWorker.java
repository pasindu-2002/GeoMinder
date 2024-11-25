package com.example.geominder.workers;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.geominder.R;

public class TaskReminderWorker extends Worker {

    private static final String CHANNEL_ID = "TASK_REMINDER_CHANNEL";
    private static final String TASK_NAME_KEY = "TASK_NAME";

    public TaskReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Get the task name from input data
        String taskName = getInputData().getString(TASK_NAME_KEY);

        if (taskName != null) {
            // Trigger the notification
            sendNotification(taskName);
        }

        return Result.success();
    }

    // Method to send a notification
    private void sendNotification(String taskName) {
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Task Reminder")
                .setContentText("Don't forget to complete: " + taskName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify((int) System.currentTimeMillis(), notification);
        }
    }

    // Helper method to pass task name as input data
    public static Data createInputData(String taskName) {
        return new Data.Builder()
                .putString(TASK_NAME_KEY, taskName)
                .build();
    }
}
