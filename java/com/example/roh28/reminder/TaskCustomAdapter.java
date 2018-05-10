package com.example.roh28.reminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roh28 on 4/10/2018.
 */

public class TaskCustomAdapter extends BaseAdapter {

    Context con;
    ReminderList reminderList = ReminderList.getInstance();
    public TaskCustomAdapter(Context context) {
        this.con = context;
    }

    @Override
    public int getCount() {
        return reminderList.getWorkingReminder().getTaskList().size();
    }

    @Override
    public Object getItem(int i) {
        return reminderList.getWorkingReminder().getTaskList().get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_custom_view, viewGroup, false);
        }
        TextView text = (TextView) view.findViewById(R.id.customText); //recognize your view like this
        text.setText(reminderList.getWorkingReminder().getTaskList().get(i));

        Button removeBtn = (Button) view.findViewById(R.id.removeBtn);
        removeBtn.setOnClickListener(removeBtnClick);
        notifyDataSetChanged();
        return view;
    }
    private View.OnClickListener removeBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            View viewParent = (View) view.getParent();
            ListView listView = (ListView) viewParent.getParent();
            TextView taskText = (TextView) viewParent.findViewById(R.id.customText);
            String task = taskText.getText().toString();
            reminderList.getWorkingReminder().getTaskList().remove(task);
            listView.removeViewInLayout(viewParent);
            notifyDataSetChanged();
        }
    };

}


