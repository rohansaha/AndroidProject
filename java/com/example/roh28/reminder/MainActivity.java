package com.example.roh28.reminder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {
    ReminderCustomAdapter customAdapter;
    final ReminderList reminders = ReminderList.getInstance();
    MenuItem menuItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        customAdapter = new ReminderCustomAdapter(this);
        ListView listView = (ListView) findViewById(R.id.reminder_list);
        listView.setAdapter(customAdapter);
        ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(MainActivity.this, TaskListActivity.class);
                intent.putExtra(ReminderList.REMINDERID, position);
                startActivity(intent);
            }
        };
        listView.setOnItemClickListener(itemClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menuItem = menu.findItem(R.id.action_create_reminder);
        menuItem.setIcon(R.drawable.ic_add_white_24px);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId())
        {
            case R.id.action_create_reminder:
                Intent intent = new Intent(MainActivity.this, TaskListActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    public void onCreateTask(View view)
    {
        Intent intent = new Intent(MainActivity.this, TaskListActivity.class);
        startActivity(intent);
    }
}
