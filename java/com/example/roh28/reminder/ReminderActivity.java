package com.example.roh28.reminder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.Calendar;

public class ReminderActivity extends AppCompatActivity implements LocationListener,OnMapReadyCallback, PlaceSelectionListener{
    ReminderList reminderList = ReminderList.getInstance();
    Reminder workingReminder = reminderList.getWorkingReminder();
    MapView mapView;
    GoogleMap gMap;
    LocationManager locationManager;
    AlarmManager alarmManager;
    Location location = null;
    LatLng markerLocation = null;
    MenuItem menuItem;
    Marker marker;
    PlaceAutocompleteFragment placeAutocompleteFragment;
    Circle markerCircle;
    SeekBar seekBar;
    TextView radiusText;
    TextView radiusLabel;


    float proximity_radius_feet = (float) 0; //5048 feet = 1 mile
    float proximity_radius_meter = (float) 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DatePicker datePicker = (DatePicker) findViewById(R.id.dp_date);
        TimePicker timePicker = (TimePicker) findViewById(R.id.tp_time);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(workingReminder.get_hour());
            timePicker.setMinute(workingReminder.get_minute());
            timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker timePicker, int hour, int minute) {
                    workingReminder.set_hour(hour);
                    workingReminder.set_minute(minute);
                }
            });
        }

        datePicker.init(workingReminder.get_year(), workingReminder.get_month(), workingReminder.get_day(),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
                        workingReminder.set_year(year);
                        workingReminder.set_month(month);
                        workingReminder.set_day(day);
                    }
                }
        );


        Switch sw_Location = (Switch) findViewById(R.id.sw_location);
        sw_Location.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                workingReminder.set_isLocation(isChecked);
                LinearLayout locationLayout = (LinearLayout) findViewById(R.id.locationLayout);
                if (isChecked) {
                    locationLayout.setVisibility(View.VISIBLE);

                } else {
                    locationLayout.setVisibility(View.GONE);
                }
            }
        });
        sw_Location.setChecked(workingReminder.isLocation());

        Switch sw_Date = (Switch) findViewById(R.id.sw_date);
        sw_Date.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                workingReminder.set_isDate(isChecked);
                LinearLayout timerLayout = (LinearLayout) findViewById(R.id.timerLayout);
                if (isChecked) {
                    timerLayout.setVisibility(View.VISIBLE);
                } else {
                    timerLayout.setVisibility(View.GONE);
                }
            }
        });
        sw_Date.setChecked(workingReminder.isDate());

        mapView = (MapView) findViewById(R.id.locationView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        getCurrentLocation();

        placeAutocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete);

        placeAutocompleteFragment.setOnPlaceSelectedListener(this);

        placeAutocompleteFragment.getView().findViewById(R.id.place_autocomplete_clear_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gMap.clear();
                        LatLng position = new LatLng(location.getLatitude(),location.getLongitude());
                        gMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                        gMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                        placeAutocompleteFragment.setText("");
                        seekBar.setProgress(0);
                        radiusText.setText("");
                        seekBar.setVisibility(View.GONE);
                        radiusText.setVisibility(View.GONE);
                        radiusLabel.setVisibility(View.GONE);
                        workingReminder.setPlace(null);
                    }
                });
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        radiusText =(TextView) findViewById(R.id.radius);
        radiusLabel = (TextView) findViewById(R.id.radiusLabel);

        seekBar.setVisibility(View.GONE);
        radiusText.setVisibility(View.GONE);
        radiusLabel.setVisibility(View.GONE);
        radiusText.setText(Float.toString(proximity_radius_feet));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (markerCircle != null)
                    markerCircle.remove();

                proximity_radius_feet = (float) progress;
                radiusText.setText(convertToMile());
                convertToMeter();
                CircleOptions circleOptions = new CircleOptions();
                circleOptions.center(marker.getPosition());
                circleOptions.radius(proximity_radius_meter);
                circleOptions.strokeColor(Color.RED);
                circleOptions.strokeWidth(3);
                markerCircle = gMap.addCircle(circleOptions);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onPlaceSelected(Place place) {
        final LatLng latLng = place.getLatLng();
        if (marker != null) {
            marker.remove();
        }
        markerLocation = place.getLatLng();
        marker = gMap.addMarker(new MarkerOptions().position(latLng).title(place.getName().toString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        marker.setTitle(place.getName().toString());
        gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        gMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        seekBar.setVisibility(View.VISIBLE);
        radiusText.setVisibility(View.VISIBLE);
        radiusLabel.setVisibility(View.VISIBLE);
        workingReminder.setPlace(place);
    }

    @Override
    public void onError(Status status) {
        Toast.makeText(ReminderActivity.this, "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menuItem  = menu.findItem(R.id.action_create_reminder);
        menuItem.setIcon(R.drawable.ic_save_white_24dp);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create_reminder:
                long delay = workingReminder.getDelay();
                if (delay > 0) {
                    setAlarmNotification(delay);

                }
                if (workingReminder.isLocation() && workingReminder.getPlace() != null)
                    setLocationNotification();
                super.onBackPressed();
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        gMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        gMap.setMyLocationEnabled(true);
        gMap.getUiSettings().setMyLocationButtonEnabled(true);
        gMap.setMinZoomPreference(12);
        if (workingReminder.getPlace() == null) {
            LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
            gMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        }
        else
        {
            proximity_radius_feet = workingReminder.getProximtyRadius();
            markerLocation = workingReminder.getPlace().getLatLng();
            this.onPlaceSelected(workingReminder.getPlace());
            placeAutocompleteFragment.setText(workingReminder.getPlace().getName());
            seekBar.setProgress((int) proximity_radius_feet);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void getCurrentLocation() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET}, 1);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    private void convertToMeter()  {
        proximity_radius_meter = (float) proximity_radius_feet/(float)3.2808;
    }

    private String convertToMile()  {
        double mile = (proximity_radius_feet/5280);
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(mile);
    }

    private void setLocationNotification()  {
        Intent notificationIntent = new Intent(ReminderActivity.this, NotificationReceiver.class);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION_ID, 2);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION, reminderList.getWorkingReminderId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ReminderActivity.this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (ActivityCompat.checkSelfPermission(ReminderActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ReminderActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        convertToMeter();
        locationManager.addProximityAlert(markerLocation.latitude, markerLocation.longitude, proximity_radius_meter, -1, pendingIntent);
        workingReminder.setProximtyRadius(proximity_radius_feet);

    }

    private void setAlarmNotification(long delay) {
        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION_ID, 2);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION, reminderList.getWorkingReminderId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.RTC_WAKEUP, delay, pendingIntent);
    }
}