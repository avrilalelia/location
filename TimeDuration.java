package com.example.logintime;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class TimeDuration extends AppCompatActivity {

    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    SimpleDateFormat simpleDateFormat2;
    SimpleDateFormat simpleDateFormat4;
    String Date;
    TextView getDateAndTime;
    private Chronometer chronometer;
    EditText timeHour;
    EditText timeMinute;
    Button setTime;
    Button setAlarm;
    TimePickerDialog timePickerDialog;
    Calendar calendar2;
    Calendar calendar3;
    int currentHour;
    int currentMinute;
    DBHelper DB;

    FusedLocationProviderClient mFusedLocationClient;

    TextView latitudeTextView, longitTextView;
    int PERMISSION_ID = 44;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_duration);

        timeHour = findViewById(R.id.etHour);
        timeMinute = findViewById(R.id.etMinute);
        setTime = findViewById(R.id.btnTime);
        setAlarm = findViewById(R.id.btnAlarm);

        setTime.setOnClickListener((v) -> {
            currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            currentMinute = calendar.get(Calendar.MINUTE);

            timePickerDialog = new TimePickerDialog(TimeDuration.this, (timePicker, hourOfDay, minutes) -> {
                timeHour.setText(String.format("%02d", hourOfDay));
                timeMinute.setText(String.format("%02d", minutes));
            }, currentHour, currentMinute, false);

            timePickerDialog.show();
        });

        setAlarm.setOnClickListener((v) -> {
            if (!timeHour.getText().toString().isEmpty() && !timeMinute.getText().toString().isEmpty()) {
                Toast.makeText(TimeDuration.this, "masuk", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(AlarmClock.ACTION_SET_ALARM);
                intent2.putExtra(AlarmClock.EXTRA_HOUR, Integer.parseInt(timeHour.getText().toString()));
                intent2.putExtra(AlarmClock.EXTRA_MINUTES, Integer.parseInt(timeMinute.getText().toString()));
                intent2.putExtra(AlarmClock.EXTRA_MESSAGE, "Set Alarm");
                if (intent2.resolveActivity(getPackageManager()) != null){
                    Toast.makeText(TimeDuration.this, "success", Toast.LENGTH_SHORT).show();
                    startActivity(intent2);
                }else{
                    Toast.makeText(TimeDuration.this, "There is no app that support this action", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(TimeDuration.this, "Please choose a time", Toast.LENGTH_SHORT).show();
            }
        });

        latitudeTextView = findViewById(R.id.latTextView);
        longitTextView = findViewById(R.id.lonTextView);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // method untuk mendapatkan lokasi
        getLastLocation();
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        getLastLocation();
                    }
                },
                180000
        );

//        Set Waktu login
        getDateAndTime = findViewById(R.id.waktulogin);
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
        Date = simpleDateFormat.format(calendar.getTime());
        getDateAndTime.setText(Date);
        DB = new DBHelper(this);

        chronometer = findViewById(R.id.chronometer);
        chronometer.setFormat("%s");
        chronometer.setBase(SystemClock.elapsedRealtime());

        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if ((SystemClock.elapsedRealtime() - chronometer.getBase()) >= 50000) {
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    Toast.makeText(TimeDuration.this, "Bing!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();

        MaterialButton logoutbtn = (MaterialButton) findViewById(R.id.logoutbtn);

        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
                calendar2 = Calendar.getInstance();
                simpleDateFormat2 = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
                String waktulogout = simpleDateFormat2.format(calendar2.getTime());
                Boolean checkinsertdata = DB.insertloginlogoutdata(Date, waktulogout);
                if(checkinsertdata==true) {
                    Toast.makeText(TimeDuration.this, "Data telah dimasukan", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(TimeDuration.this, "Data tidak berhasil dimasukan", Toast.LENGTH_SHORT).show();
                }
                Intent i = new Intent(TimeDuration.this, MainActivity.class);
                i.putExtra("key",elapsedMillis);
                i.putExtra("waktulogout",waktulogout);
                startActivity(i);
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // mengecek apakah sudah ada ijin
        if (checkPermissions()) {

            // mengecek apakah lokasi sudah di enabled
            if (isLocationEnabled()) {

                // mendapatkan lokasi dari objek FusedLocationClient
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            latitudeTextView.setText(location.getLatitude() + "");
                            longitTextView.setText(location.getLongitude() + "");
                            String latitude=String.valueOf(location.getLatitude());
                            String longitude=String.valueOf(location.getLatitude());
                            simpleDateFormat4 = new SimpleDateFormat("EEE, d MMM yyyy");
                            calendar3 = Calendar.getInstance();
                            String tanggal = simpleDateFormat4.format(calendar3.getTime());
                            Boolean checkinsertdata = DB.insertlocationdata(tanggal, latitude, longitude);
                            if(checkinsertdata==true) {
                                Toast.makeText(TimeDuration.this, "Data telah dimasukan", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(TimeDuration.this, "Data tidak berhasil dimasukan", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if ijin tidak ada, maka request ijin
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latitudeTextView.setText("Latitude: " + mLastLocation.getLatitude() + "");
            longitTextView.setText("Longitude: " + mLastLocation.getLongitude() + "");
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }

}