package com.example.roh28.reminder;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class TaskListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    ReminderList reminderList = ReminderList.getInstance();
    int currentTaskId = 0;
    ListView listView;
    TaskCustomAdapter arrayAdapter;
    Button updateButton;
    Button addButton;
    MenuItem menuItem;
    EditText titleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar =  getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        titleText = (EditText) findViewById(R.id.reminderTitle);

        updateButton = findViewById(R.id.taskUpdateButton);
        updateButton.setVisibility(View.GONE);

        addButton = findViewById(R.id.taskAddButton);
        addButton.setVisibility(View.VISIBLE);

        listView = (ListView) findViewById(R.id.taskList);

        if (getIntent().getExtras()!= null) {
            int reminderId = (int) getIntent().getExtras().get(ReminderList.REMINDERID);
            setDetails(reminderId);
        }
        else
        {
            reminderList.setWorkingReminder(new Reminder());
            reminderList.setReminders(reminderList.getWorkingReminder());
            setDetails();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menuItem  = menu.findItem(R.id.action_create_reminder);
        menuItem.setTitle("Add Reminder");
        menuItem.setIcon(R.drawable.ic_add_alert_white_24dp);
        return super.onCreateOptionsMenu(menu);
    }

   @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                if (!titleText.getText().toString().isEmpty()) {
                    reminderList.getWorkingReminder().setTaskTitle(titleText.getText().toString());
                }
                super.onBackPressed();
                break;
            case R.id.action_create_reminder:
                if (!titleText.getText().toString().isEmpty()) {
                    Intent intent = new Intent(this, ReminderActivity.class);
                    startActivity(intent);
                }
                break;
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String task = reminderList.getWorkingReminder().getTaskById(i);
        currentTaskId = i;
        EditText taskText = (EditText) findViewById(R.id.task);
        taskText.setText(task);
        updateButton.setVisibility(View.VISIBLE);
        addButton.setVisibility(View.GONE);
    }

    private void setDetails(int reminderId) {
        Reminder currentReminder = reminderList.getReminderbyID(reminderId);
        reminderList.setWorkingReminder(currentReminder);
        EditText titleText = (EditText) findViewById(R.id.reminderTitle);

        arrayAdapter = new TaskCustomAdapter(this );

        listView.setAdapter(arrayAdapter);
        titleText.setText(currentReminder.getTaskTitle());
        listView.setOnItemClickListener(this);
    }

    private void setDetails() {
        arrayAdapter = new TaskCustomAdapter(this);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(this);
    }

    public void onTaskAdd(View view) {
        EditText taskText = (EditText) findViewById(R.id.task);
        String task = taskText.getText().toString();
        if (!task.isEmpty()) {
            reminderList.getWorkingReminder().addTask(task);
            arrayAdapter.notifyDataSetChanged();
            currentTaskId++;
            taskText.setText("");
        }
    }

    public void onTaskUpdate(View view) {
        EditText taskText = (EditText) findViewById(R.id.task);
        String task = taskText.getText().toString();
        if (!task.isEmpty()) {
            reminderList.getWorkingReminder().updateTask(currentTaskId, task);
            arrayAdapter.notifyDataSetChanged();
            taskText.setText("");
            updateButton.setVisibility(View.GONE);
            addButton.setVisibility(View.VISIBLE);
        }
    }

    public void onAddReminder(View view){
        if (!titleText.getText().toString().isEmpty()) {
            reminderList.getWorkingReminder().setTaskTitle(titleText.getText().toString());
        }
        Intent intent = new Intent(this, ReminderActivity.class);
        startActivity(intent);
    }




}
