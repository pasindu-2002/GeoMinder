package com.example.geominder.schedulers;

import android.content.Context;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.geominder.database.Task;
import com.example.geominder.workers.TaskReminderWorker;

import java.util.concurrent.TimeUnit;

public class TaskScheduler {

    // Schedule a task reminder notification
    public static void scheduleTaskReminder(Context context, Task task) {
        if (task.getDate() == null || task.getTime() == null) {
            return; // Ensure task has valid date and time
        }

        // Calculate delay in minutes
        long delayInMinutes = calculateDelayInMinutes(task.getDate(), task.getTime());

        // Prepare data for Worker
        Data data = new Data.Builder()
                .putString("taskName", task.getTaskName())
                .putString("description", task.getDescription())
                .build();

        // Build the WorkRequest
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(TaskReminderWorker.class)
                .setInitialDelay(delayInMinutes, TimeUnit.MINUTES)
                .setInputData(data)
                .build();

        // Enqueue the WorkRequest
        WorkManager.getInstance(context).enqueue(workRequest);
    }

    // Cancel a task reminder notification
    public static void cancelTaskReminder(Context context, Task task) {
        // Use the task ID as a unique tag to cancel the WorkRequest
        WorkManager.getInstance(context).cancelAllWorkByTag(String.valueOf(task.getId()));
    }

    // Helper method to calculate delay in minutes
    private static long calculateDelayInMinutes(String date, String time) {
        // Combine the date and time into a single timestamp
        String dateTime = date + " " + time;

        // Parse the date-time string
        long currentTime = System.currentTimeMillis();
        long taskTimeMillis = DateTimeUtils.parseDateTimeToMillis(dateTime);

        // Calculate the delay in minutes
        return Math.max((taskTimeMillis - currentTime) / (1000 * 60), 0); // Ensure non-negative delay
    }
}
