package com.example.roh28.reminder;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by roh28 on 4/10/2018.
 */

class Reminder {

    private ArrayList<String> _taskList;
    private String _taskTitle;
    private int _year;
    private int _month;
    private int _day;
    private int _hour;
    private int _minute;
    private boolean _isOnOff;
    private boolean _isDate;
    private boolean _isLocation;
    private Place _reminderPlace;
    private float _proximtyRadius;

    public void setTaskItems(ArrayList<String> taskList)  {  _taskList = taskList;  }

    public void setTaskTitle(String taskTitle)  { _taskTitle = taskTitle; }

    public ArrayList<String> getTaskList(){return _taskList; }

    public String getTaskTitle(){return _taskTitle; }

    public Reminder()
    {
        Calendar now = Calendar.getInstance();
        _isOnOff = true;
        _taskList = new ArrayList<String>();
        _year = now.get(Calendar.YEAR);
        _month = now.get(Calendar.MONTH);
        _day = now.get(Calendar.DAY_OF_MONTH);
        _hour = now.get(Calendar.HOUR_OF_DAY);
        _minute = now.get(Calendar.MINUTE);
    }

    public String toString() {  return _taskTitle;   }

    public String getTaskById(int id ){return _taskList.get(id);}

    public void addTask(String task) { _taskList.add(task);  }

    public void updateTask(int index, String task) { _taskList.set(index,task);   }

    public int get_year() {
        return _year;
    }
    public void set_year(int year) {
        this._year = year;
    }

    public int get_month() {
        return _month;
    }
    public void set_month(int month) {
        this._month = month;
    }

    public int get_day() {
        return _day;
    }
    public void set_day(int day) {
        this._day = day;
    }

    public int get_hour() {
        return _hour;
    }
    public void set_hour(int hour) {
        this._hour = hour;
    }

    public int get_minute() {
        return _minute;
    }
    public void set_minute(int minute) {
        this._minute = minute;
    }

    public void setPlace(Place place){this._reminderPlace = place;}
    public Place getPlace(){return this._reminderPlace;}

    public void setProximtyRadius(float proximtyRadius){this._proximtyRadius = proximtyRadius; }
    public float getProximtyRadius(){return this._proximtyRadius;}

    public boolean getOnOff() {
        return _isOnOff;
    }
    public void setOnOff(boolean isOnOff) {
        this._isOnOff = isOnOff;
    }

    public boolean isDate() {
        return _isDate;
    }
    public void set_isDate(boolean isDate) {
        this._isDate = isDate;
    }

    public boolean isLocation() {
        return _isLocation;
    }
    public void set_isLocation(boolean isLocation) {
        this._isLocation = isLocation;
    }

    public long getDelay()
    {
        long timeinMillsecs = 0 ;
        if (isDate())
        {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH,get_day());
            calendar.set(Calendar.MONTH,get_month());
            calendar.set(Calendar.YEAR,get_year());
            calendar.set(Calendar.HOUR_OF_DAY,get_hour());
            calendar.set(Calendar.MINUTE,get_minute());
            timeinMillsecs = calendar.getTimeInMillis();
        }
        return  timeinMillsecs;
    }
}

class ReminderList {
    public final static String REMINDERID = "reminderID";

    private static final ReminderList ourInstance = new ReminderList();

    static ReminderList getInstance() {
        return ourInstance;
    }

    private ArrayList<Reminder> _reminders;

    private Reminder _workingReminder;

    public void setWorkingReminder(Reminder reminder){_workingReminder = reminder; }

    public Reminder getWorkingReminder(){return _workingReminder; }

    public void setReminders (Reminder reminder)  {  _reminders.add(reminder);  }

    public ArrayList<Reminder> getReminders(){return _reminders; }

    private ReminderList () {
        _reminders = new ArrayList<Reminder>();
    }

    public Reminder getReminderbyID(int id) {return _reminders.get(id);}

    public int getWorkingReminderId()
    {
        return _reminders.indexOf(_workingReminder);
    }

}
