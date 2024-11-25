package com.example.geominder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.geominder.database.Task;

import java.util.List;

public class TaskAdapter extends ArrayAdapter<Task> {

    public TaskAdapter(Context context, List<Task> tasks) {
        super(context, 0, tasks);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_list_item, parent, false);
        }

        Task task = getItem(position);
        TextView taskName = convertView.findViewById(R.id.textTaskName);
        TextView taskDescription = convertView.findViewById(R.id.textTaskDescription);
        TextView taskDate = convertView.findViewById(R.id.textTaskDate);
        TextView taskTime = convertView.findViewById(R.id.textTaskTime);
        TextView taskLocation = convertView.findViewById(R.id.textLocation);

        if (task != null) {
            taskName.setText(task.getTaskName());
            taskDescription.setText(task.getDescription());
            taskDate.setText(task.getDate());
            taskTime.setText(task.getTime());
            taskLocation.setText(task.getLocation());
        }

        return convertView;
    }
}
