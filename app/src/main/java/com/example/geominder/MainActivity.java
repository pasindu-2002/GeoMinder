package com.example.geominder;

import com.example.geominder.*;
import com.example.*;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.geominder.database.Task;
import com.example.geominder.database.TaskDatabase;
import com.example.geominder.schedulers.TaskScheduler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Task> tasks;
    private TaskAdapter adapter;
    private TaskDatabase taskDatabase;
    private TaskScheduler taskScheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listViewTasks = findViewById(R.id.listViewTasks);
        FloatingActionButton fabAddTask = findViewById(R.id.fabAddTask);

        // Initialize database
        taskDatabase = TaskDatabase.getInstance(this);
        taskScheduler = new TaskScheduler();

        // Initialize the task list and adapter
        tasks = new ArrayList<>();
        adapter = new TaskAdapter(this, tasks);
        listViewTasks.setAdapter(adapter);

        // Load tasks from the database
        loadTasks();

        // Add a new task using FAB
        fabAddTask.setOnClickListener(v -> {
            AddTaskDialogFragment dialog = new AddTaskDialogFragment();
            dialog.setTaskCallback((taskName, description, date, time, location) -> {
                // Create a new Task object
                Task newTask = new Task(taskName, description, date, time, location);

                // Save the task to the database
                saveTask(newTask);

                // Schedule a notification reminder for the task
                scheduleNotificationForTask(newTask);
            });
            dialog.show(getSupportFragmentManager(), "AddTaskDialog");
        });

        // Handle list item clicks for edit/delete
        listViewTasks.setOnItemClickListener((parent, view, position, id) -> showEditDeleteDialog(position));
    }

    // Method to load tasks from the Room database
    private void loadTasks() {
        AsyncTask.execute(() -> {
            tasks.clear();
            tasks.addAll(taskDatabase.taskDao().getAllTasks());
            runOnUiThread(() -> adapter.notifyDataSetChanged());
        });
    }

    // Method to schedule a notification for the task
    private void scheduleNotificationForTask(Task task) {
        if (task.getDate() != null && task.getTime() != null) {
            taskScheduler.scheduleTaskReminder(this,task);
        } else {
            Toast.makeText(this, "Task reminder not scheduled: missing date/time", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to save a task to the Room database
    private void saveTask(Task task) {
        AsyncTask.execute(() -> {
            taskDatabase.taskDao().insertTask(task);
            tasks.add(task);
            runOnUiThread(() -> adapter.notifyDataSetChanged());
        });
    }

    // Method to update a task in the Room database
    private void updateTask(Task updatedTask, int position) {
        AsyncTask.execute(() -> {
            taskDatabase.taskDao().updateTask(updatedTask);
            tasks.set(position, updatedTask);
            runOnUiThread(() -> adapter.notifyDataSetChanged());
        });
    }

    // Method to delete a task from the Room database
    private void deleteTask(Task task, int position) {
        AsyncTask.execute(() -> {
            taskDatabase.taskDao().deleteTask(task);
            tasks.remove(position);
            runOnUiThread(() -> adapter.notifyDataSetChanged());
        });
    }

    // Method to show the edit/delete dialog
    private void showEditDeleteDialog(int position) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.edit_task);

        EditText editTextTitle = dialog.findViewById(R.id.editTextTaskTitle);
        EditText editTextDescription = dialog.findViewById(R.id.editTextTaskDescription);
        EditText editTextData = dialog.findViewById(R.id.editTextTaskData);
        EditText editTextTime = dialog.findViewById(R.id.editTextTaskTime);
        EditText editTextLocation = dialog.findViewById(R.id.editTextTaskLocation);


        Button buttonSave = dialog.findViewById(R.id.buttonSaveTask);
        Button buttonDelete = dialog.findViewById(R.id.buttonDeleteTask);

        // Get the selected task
        Task task = tasks.get(position);

        // Pre-fill existing details
        editTextTitle.setText(task.getTaskName());
        editTextDescription.setText(task.getDescription());
        editTextData.setText(task.getDate());
        editTextTime.setText(task.getTime());
        editTextLocation.setText(task.getLocation());

        // Save button logic
        buttonSave.setOnClickListener(v -> {
            String updatedTitle = editTextTitle.getText().toString().trim();
            String updatedDescription = editTextDescription.getText().toString().trim();

            if (!updatedTitle.isEmpty()) {
                task.setTaskName(updatedTitle);
                task.setDescription(updatedDescription);
                updateTask(task, position);
                dialog.dismiss();
            } else {
                Toast.makeText(MainActivity.this, "Task title cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        // Delete button logic
        buttonDelete.setOnClickListener(v -> {
            deleteTask(task, position);
            dialog.dismiss();
        });

        dialog.show();
    }
}
