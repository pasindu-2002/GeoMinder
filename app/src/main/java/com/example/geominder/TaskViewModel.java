package com.example.geominder;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.geominder.database.Task;
import com.example.geominder.database.TaskDatabase;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    private TaskDatabase database;
    private LiveData<List<Task>> allTasks;

    public TaskViewModel(Application application) {
        super(application);
        database = TaskDatabase.getInstance(application);
        allTasks = (LiveData<List<Task>>) database.taskDao().getAllTasks();
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public void insert(Task task) {
        TaskDatabase.databaseWriteExecutor.execute(() -> database.taskDao().insertTask(task));
    }

    public void update(Task task) {
        TaskDatabase.databaseWriteExecutor.execute(() -> database.taskDao().updateTask(task));
    }

    public void delete(Task task) {
        TaskDatabase.databaseWriteExecutor.execute(() -> database.taskDao().deleteTask(task));
    }
}
