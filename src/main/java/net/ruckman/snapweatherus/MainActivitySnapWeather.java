package net.ruckman.snapweatherus;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import net.ruckman.snapweatherus.NOAAWeatherAPI.JSONWeatherTaskNOAA;
import net.ruckman.snapweatherus.NOAAWeatherAPI.ModelWeatherNOAA;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static android.app.PendingIntent.FLAG_IMMUTABLE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static net.ruckman.snapweatherus.MainFragmentSnapWeather.ModelWeatherNOAA_PREFERENCES;
import static net.ruckman.snapweatherus.MainFragmentSnapWeather.img;

public class MainActivitySnapWeather extends AppCompatActivity implements LocationListener {

    public LocationManager locationManager;
    private static final int REQUEST_LOCATION = 123;
    public static double LATITUDE=0.0;
    public static double LONGITUDE=0.0;
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    public static NotificationManager notificationManager;
    public static PendingIntent pi;
    public static MenuItem Radaritem;
    public static MenuItem RefreshonFail;
    public static Boolean locationIsSameForeground=false;
    BroadcastReceiver StatusListener;
    public static WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("onCreate","Called");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        MainActivitySnapWeather.context = getApplicationContext();
        setContentView(R.layout.activity_main_snapweather);

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {

            fragment = new MainFragmentSnapWeather();
            manager.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }

        //status listener
        StatusListener = new BroadcastReceiver() {
            @Override
            public void onReceive( Context context, Intent intent ) {
                Log.d("StatusListener","Status has been changed to: " + ModelWeatherNOAA.WeatherPoints.UpdateStatus);
                MainFragmentSnapWeather.updateStatus.setText(ModelWeatherNOAA.WeatherPoints.UpdateStatus);
            }};
        IntentFilter filter = new IntentFilter("UPDATE_STATUS");
        this.registerReceiver(StatusListener, filter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Setup Intent for Alert Notification (Launch app)
        Intent resultIntent = new Intent(getApplicationContext(), MainActivitySnapWeather.class);

        pi = PendingIntent
                .getActivity(getApplicationContext(), 0, resultIntent, FLAG_IMMUTABLE);

        //Setup Notification Manager and Channels and groups
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Uri alarmSound = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.weatheralert2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            assert notificationManager != null;
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(ModelWeatherNOAA.CurrentAlerts.ALERT_NOTIFICATION_CHANNEL_ID, "ACTIVE WEATHER ALERT", NotificationManager.IMPORTANCE_MAX);
            // Configure the notification channel.
            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationChannel.setShowBadge(false);
            notificationChannel.setSound(alarmSound,att);
            notificationChannel.setBypassDnd(true);
            notificationManager.createNotificationChannel(notificationChannel);

            //Create Service Notification channel
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel2 = new NotificationChannel(ModelWeatherNOAA.CurrentAlerts.SERVICE_NOTIFICATION_CHANNEL_ID, "SERVICE NOTIFICATION", NotificationManager.IMPORTANCE_LOW);
            // Configure the notification channel.
            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(false);
            notificationChannel.setShowBadge(false);
            notificationChannel.setBypassDnd(false);
            notificationManager.createNotificationChannel(notificationChannel2);

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("onPause","Called");
        locationManager.removeUpdates(this);
        Log.d("Listener","removed");
        //save data
        SharedPreferences settings = getAppContext().getSharedPreferences(ModelWeatherNOAA_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = settings.edit();
        prefEditor.clear();

        //Save Location Data
        MainActivitySnapWeather.SaveDataModel(getAppContext());
    }

    @SuppressLint("InlinedApi")
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onResume","Called");

        MainActivitySnapWeather.LoadDataModel(getApplicationContext());

        Intent serviceintent = new Intent(this, ForegroundServiceNOAA.class);
        stopService(serviceintent);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED) {

                //Only refresh every 5 minutes if app is open 300000 and when app is opened
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300000, 0, (LocationListener)this);
                Log.d("SLOWLOCATE","Started");
                MainFragmentSnapWeather.compass = BitmapFactory.decodeResource(MainFragmentSnapWeather.compassView.getResources(), R.raw.compass);
                MainFragmentSnapWeather.compassView.setImageBitmap(Bitmap.createBitmap(MainFragmentSnapWeather.compass));

            Log.d("Listener","started");
        } else {

            if (Build.VERSION.SDK_INT > 29) {
                Log.d("Permissions","API30+");
                ActivityCompat.requestPermissions(this,

                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.BLUETOOTH,
                                Manifest.permission.BLUETOOTH_ADMIN}, REQUEST_LOCATION);
                //todo needs background permission dialog for service launcher
            }

            if (Build.VERSION.SDK_INT == 29) {
                Log.d("Permissions","API29=");
                ActivityCompat.requestPermissions(this,

                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.BLUETOOTH,
                                Manifest.permission.BLUETOOTH_ADMIN,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_LOCATION);
            }
            if (Build.VERSION.SDK_INT < 29) {
                Log.d("Permissions","API28-");
                ActivityCompat.requestPermissions(this,

                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.BLUETOOTH,
                                Manifest.permission.BLUETOOTH_ADMIN}, REQUEST_LOCATION);
            }

        }

        //update status
        Log.d("StatusUpdateLoad","Sending Broadcast Event");
        Intent StatusIntent = new Intent("UPDATE_STATUS");
        ModelWeatherNOAA.WeatherPoints.UpdateStatus="Updating";
        ModelWeatherNOAA.WeatherPoints.lastStatusFailedNotComplete=false;
        getAppContext().sendBroadcast(StatusIntent);

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("onStop","Called");
        //Start Background Service Worker
        Log.d("Background","Starting Background Worker for Weather Alerts");

        if (ModelWeatherNOAA.Settings.ServiceNotifications) {
            Log.i("Service", "Enabled, Starting...");
            //Start Foreground Service
            Intent serviceintent = new Intent(this, ForegroundServiceNOAA.class);
            if (Build.VERSION.SDK_INT >= 26) {
                startForegroundService(serviceintent);
            } else {
                startService(serviceintent);
            }
        } else {
            Log.i("Service", "Disabled");
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy","Called");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(StatusListener);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d("onRequestPermissions","Result Called");
        if(requestCode == REQUEST_LOCATION) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("LOCATION RESULTS","Permission requested Location permissions granted, starting location");
            }
            if(grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Log.d("LOCATION RESULTS","Permission requested Location permissions DENIED");
                Toast
                        .makeText(this, "You must accept Location! Or SnapWeatherUS cannot operate!", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onLocationChanged(Location location) {
        Log.d("onLocationChanged","Called");

        Double lattmp = location.getLatitude();
        LATITUDE = Math.round(lattmp * 100.0) / 100.0;
        Double longtmp = location.getLongitude();
        LONGITUDE = Math.round(longtmp * 100.0) / 100.0;

        //Check if GPS has changed
        Log.d("LOCATIONCHECK", "Stored Latitude: " + ModelWeatherNOAA.WeatherPoints.latitude);
        Log.d("LOCATIONCHECK", "GPS Latitude: " + LATITUDE);
        Log.d("LOCATIONCHECK", "Stored Longitude: "+ ModelWeatherNOAA.WeatherPoints.longitude);
        Log.d("LOCATIONCHECK", "GPS Longitude: " + LONGITUDE);

        if (!String.valueOf(LATITUDE).equals(ModelWeatherNOAA.WeatherPoints.latitude) || !String.valueOf(LONGITUDE).equals(ModelWeatherNOAA.WeatherPoints.longitude) ) {
            Log.d("LOCATIONCHECK", "New location acquired");
            ModelWeatherNOAA.WeatherPoints.latitude = String.valueOf(LATITUDE);
            ModelWeatherNOAA.WeatherPoints.longitude = String.valueOf(LONGITUDE);
            locationIsSameForeground=false;
        } else {
            Log.d("LOCATIONCHECK", "location is the same");
            locationIsSameForeground=true;
        }

        ModelWeatherNOAA.WeatherPoints.lastlocationgettime = String.valueOf(location.getTime());
        Log.d("LOCATION TIME", ModelWeatherNOAA.WeatherPoints.lastlocationgettime);

        JSONWeatherTaskNOAA task = new JSONWeatherTaskNOAA();
        MainFragmentSnapWeather.loadingSpin.setVisibility(VISIBLE);

        task.execute(String.valueOf(LONGITUDE),String.valueOf(LATITUDE));

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d("LocationProvider","Status Changed");
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.d("LocationProvider","Enabled");
        ModelWeatherNOAA.WeatherPoints.UpdateStatus="Location enabled. Will update on next refresh.";
        RefreshWeatherDataOnLoad();
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.d("LocationProvider","Disabled");
        RefreshWeatherDataOnLoad();
    }

    public static Context getAppContext() {
        return MainActivitySnapWeather.context;
    }

    //function to refresh screen data from model
    public static void RefreshWeatherDataOnLoad() {
        Log.d("Refreshing","Data On Loading");
        // Apply weather data to view
        if (ModelWeatherNOAA.CurrentCondition.iconData != null && ModelWeatherNOAA.CurrentCondition.iconData.length > 0) {
            Bitmap img = BitmapFactory.decodeByteArray(ModelWeatherNOAA.CurrentCondition.iconData, 0, ModelWeatherNOAA.CurrentCondition.iconData.length);
            MainFragmentSnapWeather.imgView.setImageBitmap(Bitmap.createScaledBitmap(img, 300, 300, false));
        } else {
            img = BitmapFactory.decodeResource(getAppContext().getResources(), R.raw.black);
            MainFragmentSnapWeather.imgView.setImageBitmap(Bitmap.createBitmap(img));
        }

        if (ModelWeatherNOAA.WeatherPoints.city != null && !ModelWeatherNOAA.WeatherPoints.city.isEmpty() && !ModelWeatherNOAA.WeatherPoints.city.equals("null")){
            MainFragmentSnapWeather.cityText.setText(ModelWeatherNOAA.WeatherPoints.city + ", " + ModelWeatherNOAA.WeatherPoints.state);
            MainFragmentSnapWeather.currentWeatherLayout.setVisibility(VISIBLE);
        } else {

            if (LONGITUDE == 0.0 && LATITUDE == 0.0) {
                MainFragmentSnapWeather.cityText.setText("REFRESHING WEATHER DATA");
                MainFragmentSnapWeather.currentWeatherLayout.setVisibility(GONE);
                MainFragmentSnapWeather.AlertView.setVisibility(GONE);
            } else {
                MainFragmentSnapWeather.cityText.setText("City/State Unavailable");
                MainFragmentSnapWeather.currentWeatherLayout.setVisibility(GONE);
                MainFragmentSnapWeather.AlertView.setVisibility(GONE);
            }
        }

        if (ModelWeatherNOAA.WeatherPoints.stationname1 != null && !ModelWeatherNOAA.WeatherPoints.stationname1.isEmpty() && !ModelWeatherNOAA.WeatherPoints.stationname1.equals("null")){
            MainFragmentSnapWeather.observationStation.setText("Observations from " + ModelWeatherNOAA.WeatherPoints.stationname1);
        } else {
            if (LONGITUDE == 0.0 && LATITUDE == 0.0) {
                MainFragmentSnapWeather.observationStation.setText("FINDING LOCATION, PLEASE WAIT");
            } else {
                MainFragmentSnapWeather.observationStation.setText("Attempting to locate");
            }
        }

        if (ModelWeatherNOAA.CurrentCondition.descr != null && !ModelWeatherNOAA.CurrentCondition.descr.isEmpty() && !ModelWeatherNOAA.CurrentCondition.descr.equals("null")){
            MainFragmentSnapWeather.condDescr.setText(ModelWeatherNOAA.CurrentCondition.descr);
        } else {
            MainFragmentSnapWeather.condDescr.setText("Condition Unavailable");
        }

        if (ModelWeatherNOAA.CurrentCondition.temperature != null && !ModelWeatherNOAA.CurrentCondition.temperature.isEmpty() && !ModelWeatherNOAA.CurrentCondition.temperature.equals("null")){
            MainFragmentSnapWeather.temp.setText(new DecimalFormat("###.#").format(((Double.parseDouble((ModelWeatherNOAA.CurrentCondition.temperature)) * 1.8 ) + 32 )) + " °F"); //  (" + new DecimalFormat("###.#").format(((Double.parseDouble((ModelWeatherNOAA.CurrentCondition.temperature))))) +"°C)
        } else {
            MainFragmentSnapWeather.temp.setText("--" + " °F"); //  (" + "--" + "°C)
        }

        if (ModelWeatherNOAA.CurrentCondition.pressure != null && !ModelWeatherNOAA.CurrentCondition.pressure.isEmpty() && !ModelWeatherNOAA.CurrentCondition.pressure.equals("null")){
            MainFragmentSnapWeather.press.setText(new DecimalFormat("###.#").format(((Double.parseDouble((ModelWeatherNOAA.CurrentCondition.pressure)) / 3386 ))) + " in"); //  " + new DecimalFormat("###.#").format(((Double.parseDouble((ModelWeatherNOAA.CurrentCondition.pressure)) / 100 ))) + " mb
        } else {
            MainFragmentSnapWeather.press.setText("--" + " in"); //  (-- mb)
        }

        if (ModelWeatherNOAA.CurrentCondition.humidity != null && !ModelWeatherNOAA.CurrentCondition.humidity.isEmpty() && !ModelWeatherNOAA.CurrentCondition.humidity.equals("null")){
            MainFragmentSnapWeather.hum.setText(Math.round(Double.parseDouble(ModelWeatherNOAA.CurrentCondition.humidity)) + " %");
        } else {
            MainFragmentSnapWeather.hum.setText("--" + " %");
        }

        //TODO Feels like temperature
        if (ModelWeatherNOAA.CurrentCondition.temperature != null && !ModelWeatherNOAA.CurrentCondition.temperature.isEmpty() && !ModelWeatherNOAA.CurrentCondition.temperature.equals("null")) {

                //If the temp is between 10 and 21.1111 set feels like equal to temp
            if ((Double.parseDouble(ModelWeatherNOAA.CurrentCondition.temperature) < 21.1111) && (Double.parseDouble(ModelWeatherNOAA.CurrentCondition.temperature) >= 10.0)) {
                MainFragmentSnapWeather.feelslike.setText(new DecimalFormat("###.#").format(((Double.parseDouble((ModelWeatherNOAA.CurrentCondition.temperature)) * 1.8 ) + 32 )) + " °F"); // (" + new DecimalFormat("###.#").format(((Double.parseDouble((ModelWeatherNOAA.CurrentCondition.temperature))))) +"°C)
                MainFragmentSnapWeather.heatcoldWarningimgView.setVisibility(GONE);
            }

                //Run heat index if equal or above temp 21.1111c or 70f
            if (Double.parseDouble(ModelWeatherNOAA.CurrentCondition.temperature) >= 21.1111) {
                if (ModelWeatherNOAA.CurrentCondition.heatindex != null && !ModelWeatherNOAA.CurrentCondition.heatindex.isEmpty() && !ModelWeatherNOAA.CurrentCondition.heatindex.equals("null")) {
                    MainFragmentSnapWeather.feelslike.setText(new DecimalFormat("###.#").format(((Double.parseDouble((ModelWeatherNOAA.CurrentCondition.heatindex)) * 1.8 ) + 32 )) + " °F"); //  (" + new DecimalFormat("###.#").format(((Double.parseDouble((ModelWeatherNOAA.CurrentCondition.heatindex))))) +"°C)
                    //set icon for heat index
                    if (Double.parseDouble(ModelWeatherNOAA.CurrentCondition.heatindex) >= 32.2222 && Double.parseDouble(ModelWeatherNOAA.CurrentCondition.heatindex) < 35) {
                        //yellow
                        Log.d("HeatIndexIcon", "Yellow");
                        MainFragmentSnapWeather.heatcoldwarningimg = BitmapFactory.decodeResource(MainFragmentSnapWeather.heatcoldWarningimgView.getResources(), net.ruckman.snapweatherus.R.raw.yellow_heat);
                        MainFragmentSnapWeather.heatcoldWarningimgView.setImageBitmap(Bitmap.createBitmap(MainFragmentSnapWeather.heatcoldwarningimg));
                        MainFragmentSnapWeather.heatcoldWarningimgView.setVisibility(VISIBLE);
                    }
                    if (Double.parseDouble(ModelWeatherNOAA.CurrentCondition.heatindex) >= 35 && Double.parseDouble(ModelWeatherNOAA.CurrentCondition.heatindex) < 37.7778) {
                        //orange
                        Log.d("HeatIndexIcon", "orange");
                        MainFragmentSnapWeather.heatcoldwarningimg = BitmapFactory.decodeResource(MainFragmentSnapWeather.heatcoldWarningimgView.getResources(), net.ruckman.snapweatherus.R.raw.orange_heat);
                        MainFragmentSnapWeather.heatcoldWarningimgView.setImageBitmap(Bitmap.createBitmap(MainFragmentSnapWeather.heatcoldwarningimg));
                        MainFragmentSnapWeather.heatcoldWarningimgView.setVisibility(VISIBLE);
                    }
                    if (Double.parseDouble(ModelWeatherNOAA.CurrentCondition.heatindex) >= 37.7778) {
                        //red
                        Log.d("HeatIndexIcon", "red");
                        MainFragmentSnapWeather.heatcoldwarningimg = BitmapFactory.decodeResource(MainFragmentSnapWeather.heatcoldWarningimgView.getResources(), net.ruckman.snapweatherus.R.raw.red_heat);
                        MainFragmentSnapWeather.heatcoldWarningimgView.setImageBitmap(Bitmap.createBitmap(MainFragmentSnapWeather.heatcoldwarningimg));
                        MainFragmentSnapWeather.heatcoldWarningimgView.setVisibility(VISIBLE);
                    }
                    if (Double.parseDouble(ModelWeatherNOAA.CurrentCondition.heatindex) < 32.2222) {
                        Log.d("HeatIndexIcon", "nominal");
                        MainFragmentSnapWeather.heatcoldWarningimgView.setVisibility(GONE);
                    }

                } else {
                    //Calculate Heat Index
                    if (ModelWeatherNOAA.CurrentCondition.humidity != null && !ModelWeatherNOAA.CurrentCondition.humidity.isEmpty() && !ModelWeatherNOAA.CurrentCondition.humidity.equals("null") && ModelWeatherNOAA.CurrentCondition.temperature != null && !ModelWeatherNOAA.CurrentCondition.temperature.isEmpty() && !ModelWeatherNOAA.CurrentCondition.temperature.equals("null")) {
                        Double T = Double.valueOf(ModelWeatherNOAA.CurrentCondition.temperature);
                        Double R = Double.valueOf(ModelWeatherNOAA.CurrentCondition.humidity);
                        Double HI = 0.0;

                        HI = ((-8.78469475556) + (1.61139411 * T) + (2.33854883889 * R) + (-0.14611605 * T * R) + (-0.012308094 * Math.pow(T,2)) + (-0.0164248277778 * Math.pow(R,2)) + (0.002211732 * Math.pow(T,2) * R) + (0.00072546 * T * Math.pow(R,2)) + (-0.000003582 * Math.pow(T,2) * Math.pow(R,2)));
                        Log.d("HeatIndexCalculated", String.valueOf(HI));

                        //set text for heat index calculated
                        MainFragmentSnapWeather.feelslike.setText(new DecimalFormat("###.#").format(((HI * 1.8 ) + 32 )) + " °F"); //  (" + new DecimalFormat("###.#").format(((HI))) +"°C)

                        //set icon for heat index calculated
                        if (HI >= 32.2222 && HI < 35) {
                            //yellow
                            Log.d("HeatIndexIcon", "Yellow");
                            MainFragmentSnapWeather.heatcoldwarningimg = BitmapFactory.decodeResource(MainFragmentSnapWeather.heatcoldWarningimgView.getResources(), net.ruckman.snapweatherus.R.raw.yellow_heat);
                            MainFragmentSnapWeather.heatcoldWarningimgView.setImageBitmap(Bitmap.createBitmap(MainFragmentSnapWeather.heatcoldwarningimg));
                            MainFragmentSnapWeather.heatcoldWarningimgView.setVisibility(VISIBLE);
                        }
                        if (HI >= 35 && HI < 37.7778) {
                            //orange
                            Log.d("HeatIndexIcon", "orange");
                            MainFragmentSnapWeather.heatcoldwarningimg = BitmapFactory.decodeResource(MainFragmentSnapWeather.heatcoldWarningimgView.getResources(), net.ruckman.snapweatherus.R.raw.orange_heat);
                            MainFragmentSnapWeather.heatcoldWarningimgView.setImageBitmap(Bitmap.createBitmap(MainFragmentSnapWeather.heatcoldwarningimg));
                            MainFragmentSnapWeather.heatcoldWarningimgView.setVisibility(VISIBLE);
                        }
                        if (HI >= 37.7778) {
                            //red
                            Log.d("HeatIndexIcon", "red");
                            MainFragmentSnapWeather.heatcoldwarningimg = BitmapFactory.decodeResource(MainFragmentSnapWeather.heatcoldWarningimgView.getResources(), net.ruckman.snapweatherus.R.raw.red_heat);
                            MainFragmentSnapWeather.heatcoldWarningimgView.setImageBitmap(Bitmap.createBitmap(MainFragmentSnapWeather.heatcoldwarningimg));
                            MainFragmentSnapWeather.heatcoldWarningimgView.setVisibility(VISIBLE);
                        }
                        if (HI < 32.2222) {
                            Log.d("HeatIndexIcon", "nominal");
                            MainFragmentSnapWeather.heatcoldWarningimgView.setVisibility(GONE);
                        }

                    } else {
                        MainFragmentSnapWeather.feelslike.setText("--" + " °F"); // (" + "--" + "°C)
                        MainFragmentSnapWeather.heatcoldWarningimgView.setVisibility(GONE);
                    }
                }
            }

            //Run wind chill if below temp 10c or 50f
            if (Double.parseDouble(ModelWeatherNOAA.CurrentCondition.temperature) < 10.0) {
                //Calculate Wind Chill
                if (ModelWeatherNOAA.CurrentCondition.windspeed != null && !ModelWeatherNOAA.CurrentCondition.windspeed.isEmpty() && !ModelWeatherNOAA.CurrentCondition.windspeed.equals("null") && ModelWeatherNOAA.CurrentCondition.temperature != null && !ModelWeatherNOAA.CurrentCondition.temperature.isEmpty() && !ModelWeatherNOAA.CurrentCondition.temperature.equals("null")) {
                    Double V = (Double.valueOf(ModelWeatherNOAA.CurrentCondition.windspeed)); //Kilometers per hour
                    Double Ta = Double.valueOf(ModelWeatherNOAA.CurrentCondition.temperature);
                    Double wCI = 0.0;

                    wCI = (13.12 + (0.6215 * Ta) - (11.37 * Math.pow(V, 0.16)) + (0.3965 * Ta * Math.pow(V, 0.16)));
                    if (V != 0) {
                        Log.d("WindIndexCalculated", String.valueOf(wCI));
                        MainFragmentSnapWeather.feelslike.setText(new DecimalFormat("###.#").format(((wCI * 1.8) + 32)) + " °F"); // (" + new DecimalFormat("###.#").format(((wCI))) +"°C)
                    }
                    if (V == 0) {
                        Log.d("WindIndexCalculated", "Equal to Temp");
                        MainFragmentSnapWeather.feelslike.setText(new DecimalFormat("###.#").format(((Double.parseDouble((ModelWeatherNOAA.CurrentCondition.temperature)) * 1.8 ) + 32 )) + " °F"); // (" + new DecimalFormat("###.#").format(((Double.parseDouble((ModelWeatherNOAA.CurrentCondition.temperature))))) +"°C)
                    }

                    //set icon for wind chill index calculated if sub-zero
                    if (wCI <= -17.7778 || Double.parseDouble(ModelWeatherNOAA.CurrentCondition.temperature) < -17.7778) {
                        //blue
                        Log.d("HeatIndexIcon", "blue");
                        MainFragmentSnapWeather.heatcoldwarningimg = BitmapFactory.decodeResource(MainFragmentSnapWeather.heatcoldWarningimgView.getResources(), R.raw.blue_heat);
                        MainFragmentSnapWeather.heatcoldWarningimgView.setImageBitmap(Bitmap.createBitmap(MainFragmentSnapWeather.heatcoldwarningimg));
                        MainFragmentSnapWeather.heatcoldWarningimgView.setVisibility(VISIBLE);
                    } else {
                        MainFragmentSnapWeather.heatcoldWarningimgView.setVisibility(GONE);
                    }

                } else {
                    MainFragmentSnapWeather.feelslike.setText("--" + " °F"); //  (" + "--" + "°C)
                    MainFragmentSnapWeather.heatcoldWarningimgView.setVisibility(GONE);
                }
            }

            //else fail commands
        } else {
            MainFragmentSnapWeather.feelslike.setText("--" + " °F"); //  (" + "--" + "°C)
            MainFragmentSnapWeather.heatcoldWarningimgView.setVisibility(GONE);
        }

        if (ModelWeatherNOAA.CurrentCondition.windspeed != null && !ModelWeatherNOAA.CurrentCondition.windspeed.isEmpty() && !ModelWeatherNOAA.CurrentCondition.windspeed.equals("null")){
            MainFragmentSnapWeather.windSpeed.setText(new DecimalFormat("###.#").format(Double.parseDouble((ModelWeatherNOAA.CurrentCondition.windspeed))/1.609) + " mph"); //  (" + new DecimalFormat("###.#").format(Double.parseDouble((ModelWeatherNOAA.CurrentCondition.windspeed))) + " kph)
        } else {
            MainFragmentSnapWeather.windSpeed.setText("--" + " mph");
        }

        if (ModelWeatherNOAA.CurrentCondition.winddirection != null && !ModelWeatherNOAA.CurrentCondition.winddirection.isEmpty() && !ModelWeatherNOAA.CurrentCondition.winddirection.equals("null")){
            //Calculate Wind Direction
            String winddirectioncompass = "";
            if (Math.round(Float.parseFloat(ModelWeatherNOAA.CurrentCondition.winddirection)) >= (float) 348.75 && Math.round(Float.parseFloat(ModelWeatherNOAA.CurrentCondition.winddirection)) <= (float) 360){
                winddirectioncompass="N";
            }
            if (Math.round(Float.parseFloat(ModelWeatherNOAA.CurrentCondition.winddirection)) >= (float) 0 && Math.round(Float.parseFloat(ModelWeatherNOAA.CurrentCondition.winddirection)) <= (float) 11.25){
                winddirectioncompass="N";
            }
            if (Math.round(Float.parseFloat(ModelWeatherNOAA.CurrentCondition.winddirection)) > (float) 11.25 && Math.round(Float.parseFloat(ModelWeatherNOAA.CurrentCondition.winddirection)) <= (float) 33.75){
                winddirectioncompass="NNE";
            }
            if (Math.round(Float.parseFloat(ModelWeatherNOAA.CurrentCondition.winddirection)) > (float) 33.75 && Math.round(Float.parseFloat(ModelWeatherNOAA.CurrentCondition.winddirection)) <= (float) 56.25){
                winddirectioncompass="NE";
            }
            if (Math.round(Float.parseFloat(ModelWeatherNOAA.CurrentCondition.winddirection)) > (float) 56.25 && Math.round(Float.parseFloat(ModelWeatherNOAA.CurrentCondition.winddirection)) <= (float) 78.75){
                winddirectioncompass="ENE";
            }
            if (Math.round(Float.parseFloat(ModelWeatherNOAA.CurrentCondition.winddirection)) > (float) 78.75 && Math.round(Float.parseFloat(ModelWeatherNOAA.CurrentCondition.winddirection)) <= (float) 101.25){
                winddirectioncompass="E";
            }
            if (Math.round(Float.parseFloat(ModelWeatherNOAA.CurrentCondition.winddirection)) > (float) 101.25 && Math.round(Float.parseFloat(ModelWeatherNOAA.CurrentCondition.winddirection)) <= (float) 123.75){
                winddirectioncompass="ESE";
            }
            if (Math.round(Float.parseFloat(ModelWeatherNOAA.CurrentCondition.winddirection)) > (float) 123.75 && Math.round(Float.parseFloat(ModelWeatherNOAA.CurrentCondition.winddirection)) <= (float) 146.25){
                winddirectioncompass="SE";
            }
            if (Math.round(Float.parseFloat(ModelWeatherNOAA.CurrentCondition.winddirection)) > (float) 146.25 && Math.round(Float.parseFloat(ModelWeatherNOAA.CurrentCondition.winddirection)) <= (float) 168.75){
                winddirectioncompass="SSE";
            }
            if (Math.round(Float.parseFloat(ModelWeatherNOAA.CurrentCondition.winddirection)) > (float) 168.75 && Math.round(Float.parseFloat(ModelWeatherNOAA.CurrentCondition.winddirection)) <= (float) 191.25){
                winddirectioncompass="S";
            }
            if (Math.round(Float.parseFloat(ModelWeatherNOAA.CurrentCondition.winddirection)) > (float) 191.25 && Math.round(Float.parseFloat(ModelWeatherNOAA.CurrentCondition.winddirection)) <= (float) 213.75){
                winddirectioncompass="SSW";
            }
            if (Math.round(Float.parseFloat(ModelWeatherNOAA.CurrentCondition.winddirection)) > (float) 213.75 && Math.round(Float.parseFloat(ModelWeatherNOAA.CurrentCondition.winddirection)) <= (float)  236.25){
                winddirectioncompass="SW";
            }
            if (Math.round(Float.parseFloat(ModelWeatherNOAA.CurrentCondition.winddirection)) > (float) 236.25 && Math.round(Float.parseFloat(ModelWeatherNOAA.CurrentCondition.winddirection)) <= (float)  258.75){
                winddirectioncompass="WSW";
            }
            if (Math.round(Float.parseFloat(ModelWeatherNOAA.CurrentCondition.winddirection)) > (float) 258.75 && Math.round(Float.parseFloat(ModelWeatherNOAA.CurrentCondition.winddirection)) <= (float)  281.25){
                winddirectioncompass="W";
            }
            if (Math.round(Float.parseFloat(ModelWeatherNOAA.CurrentCondition.winddirection)) > (float) 281.25 && Math.round(Float.parseFloat(ModelWeatherNOAA.CurrentCondition.winddirection)) <= (float)  303.75){
                winddirectioncompass="WNW";
            }
            if (Math.round(Float.parseFloat(ModelWeatherNOAA.CurrentCondition.winddirection)) > (float) 303.75 && Math.round(Float.parseFloat(ModelWeatherNOAA.CurrentCondition.winddirection)) <= (float)  326.25){
                winddirectioncompass="NW";
            }
            if (Math.round(Float.parseFloat(ModelWeatherNOAA.CurrentCondition.winddirection)) > (float) 326.25 && Math.round(Float.parseFloat(ModelWeatherNOAA.CurrentCondition.winddirection)) <= (float)  348.75){
                winddirectioncompass="NNW";
            }

            MainFragmentSnapWeather.windDeg.setText(winddirectioncompass + " " + Math.round(Float.parseFloat(ModelWeatherNOAA.CurrentCondition.winddirection)) + " °");

            //rotate compass image

            MainFragmentSnapWeather.compass = BitmapFactory.decodeResource(MainFragmentSnapWeather.compassView.getResources(), R.raw.compass);
            MainFragmentSnapWeather.compassView.setImageBitmap(Bitmap.createBitmap(MainFragmentSnapWeather.compass));

            MainFragmentSnapWeather.compassView.setScaleType(ImageView.ScaleType.MATRIX);   //required
            float pivotX = (float) (Bitmap.createBitmap(MainFragmentSnapWeather.compass).getWidth() / 2);
            float pivotY  = (float) (Bitmap.createBitmap(MainFragmentSnapWeather.compass).getHeight() / 2);

            Log.d("Rotate","Rotating");

            MainFragmentSnapWeather.matrix.setRotate((Float.parseFloat(ModelWeatherNOAA.CurrentCondition.winddirection)), pivotX, pivotY);
            MainFragmentSnapWeather.compassView.setImageMatrix(MainFragmentSnapWeather.matrix);
            MainFragmentSnapWeather.compassView.setVisibility(View.VISIBLE);

        } else {
            MainFragmentSnapWeather.windDeg.setText("--" + " °");
            MainFragmentSnapWeather.compassView.setVisibility(GONE);
        }

        //Update Weather date
        if (ModelWeatherNOAA.CurrentCondition.LastWeatherUpdate!=null){

            //Convert to human readable
            SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
            java.util.Date date = null;
            try {
                date = form.parse(ModelWeatherNOAA.CurrentCondition.LastWeatherUpdate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat postFormater = new SimpleDateFormat("MM.dd.yyyy 'at' hh:mm:ssaaa z");
            String newDateStr = postFormater.format(date);

            MainFragmentSnapWeather.lastUpdated.setText(newDateStr);

        } else {
            MainFragmentSnapWeather.lastUpdated.setText("--");
        }

        //Set Weather Alerts
        Log.d("RefreshAlertEvent", Arrays.toString(ModelWeatherNOAA.CurrentAlerts.event));
        if (ModelWeatherNOAA.CurrentAlerts.event!=null) {

            if (ModelWeatherNOAA.CurrentAlerts.event.length > 0) {
                MainFragmentSnapWeather.AlertView.setVisibility(View.VISIBLE);
                //looping through alert list for events
                for (int i = 0; i < ModelWeatherNOAA.CurrentAlerts.event.length; i++) {
                    if (i == 0) {
                        MainFragmentSnapWeather.AlertList.setText(ModelWeatherNOAA.CurrentAlerts.event[i]);
                    }
                    if (i != 0) {
                        MainFragmentSnapWeather.AlertList.append("\n" + ModelWeatherNOAA.CurrentAlerts.event[i]);
                    }
                }

                ModelWeatherNOAA.CurrentAlerts.AlertONOFF=true;

                StringBuilder alerttext = new StringBuilder();

                if (ModelWeatherNOAA.CurrentAlerts.event.length > 0) {
                    //looping through alert list for events
                    for (int i = 0; i < ModelWeatherNOAA.CurrentAlerts.event.length; i++) {
                        if (i == 0) {
                            alerttext.append(ModelWeatherNOAA.CurrentAlerts.event[i]);
                        }
                        if (i != 0) {
                            alerttext.append(", " + ModelWeatherNOAA.CurrentAlerts.event[i]);
                        }
                    }
                }
                String testalerttest = String.valueOf(alerttext);
                Log.d("AlertManager", "Pre-Compare text: "  + ModelWeatherNOAA.CurrentAlerts.compareevent);
                Log.d("AlertManager", "Post-Compare text: "  + testalerttest);
                if (ModelWeatherNOAA.CurrentAlerts.compareevent==null){
                    ModelWeatherNOAA.CurrentAlerts.compareevent="";
                }
                if (!ModelWeatherNOAA.CurrentAlerts.compareevent.equals(testalerttest)  ) {
                    Log.d("AlertManager","Alerting");

                    //Setup Alarm Notification
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MainActivitySnapWeather.getAppContext(), ModelWeatherNOAA.CurrentAlerts.ALERT_NOTIFICATION_CHANNEL_ID);

                    Uri alarmSound = Uri.parse("android.resource://" + MainActivitySnapWeather.getAppContext().getPackageName() + "/" + R.raw.weatheralert2);

                    Log.d("AlertSoundURI", String.valueOf(alarmSound));

                    //Looping through alarm notifications
                    if (ModelWeatherNOAA.CurrentAlerts.event.length > 0) {
                        //looping through alert list for events
                        assert notificationManager != null;
                        notificationManager.cancelAll();
                        for (int i = 0; i < ModelWeatherNOAA.CurrentAlerts.event.length; i++) {
                            //Do Alarm Notification
                            notificationBuilder.setAutoCancel(false)
                                    .setWhen(System.currentTimeMillis())
                                    .setSmallIcon(R.drawable.ic_notification)
                                    .setLargeIcon(BitmapFactory.decodeResource(MainActivitySnapWeather.getAppContext().getResources(),
                                            R.mipmap.ic_launcher))
                                    .setPriority(Notification.PRIORITY_MAX)
                                    .setContentIntent(pi)
                                    .setContentTitle("ACTIVE WEATHER ALERT")
                                    .setSound(Uri.parse("android.resource://" + MainActivitySnapWeather.getAppContext().getPackageName() + "/" + R.raw.weatheralert2))
                                    .setContentText(String.valueOf(ModelWeatherNOAA.CurrentAlerts.event[i]));
                            assert notificationManager != null;
                            if (ModelWeatherNOAA.Settings.ServiceNotifications) {

                                if (!ModelWeatherNOAA.CurrentAlerts.severity[i].equals("") && ModelWeatherNOAA.CurrentAlerts.severity[i] !=null && !ModelWeatherNOAA.CurrentAlerts.severity[i].equals("null")) {
                                    if (ModelWeatherNOAA.CurrentAlerts.severity[i].equals("Extreme") && ModelWeatherNOAA.Settings.SeverityExtreme) {
                                        Log.d("AlertManager", "Notifications Enabled. Alerting. Extreme Severity");
                                        notificationManager.notify(i, notificationBuilder.build());
                                    }
                                    if (ModelWeatherNOAA.CurrentAlerts.severity[i].equals("Severe") && ModelWeatherNOAA.Settings.SeveritySevere) {
                                        Log.d("AlertManager", "Notifications Enabled. Alerting. Severe Severity");
                                        notificationManager.notify(i, notificationBuilder.build());
                                    }
                                    if (ModelWeatherNOAA.CurrentAlerts.severity[i].equals("Moderate") && ModelWeatherNOAA.Settings.SeverityModerate) {
                                        Log.d("AlertManager", "Notifications Enabled. Alerting. Moderate Severity");
                                        notificationManager.notify(i, notificationBuilder.build());
                                    }
                                    if (ModelWeatherNOAA.CurrentAlerts.severity[i].equals("Minor") && ModelWeatherNOAA.Settings.SeverityMinor) {
                                        Log.d("AlertManager", "Notifications Enabled. Alerting. Minor Severity");
                                        notificationManager.notify(i, notificationBuilder.build());
                                    }
                                    if (ModelWeatherNOAA.CurrentAlerts.severity[i].equals("Unknown") && ModelWeatherNOAA.Settings.SeverityUnknown) {
                                        Log.d("AlertManager", "Notifications Enabled. Alerting. Unknown Severity");
                                        notificationManager.notify(i, notificationBuilder.build());
                                    }
                                }

                            }
                            ModelWeatherNOAA.CurrentAlerts.compareevent = testalerttest;

                        }
                    }



                } else {
                    Log.d("AlertManager","Alerts the same, not re-alerting");
                }

            } else {
                MainFragmentSnapWeather.AlertView.setVisibility(GONE);
                ModelWeatherNOAA.CurrentAlerts.AlertONOFF=false;
                NotificationManager notificationManager = (NotificationManager) MainActivitySnapWeather.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
                assert notificationManager != null;
                notificationManager.cancelAll();
            }
        } else {
            MainFragmentSnapWeather.AlertView.setVisibility(GONE);
            ModelWeatherNOAA.CurrentAlerts.AlertONOFF=false;
            NotificationManager notificationManager = (NotificationManager) MainActivitySnapWeather.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.cancelAll();
        }

        //Enable radar if station is found
        MainActivitySnapWeather.EnableDisableRadarOption();

        //Extended Forecast refresh

        //TODO If there is a forecast, load it, otherwise hide the area
        if (ModelWeatherNOAA.Forecast.forecastName !=null) {

            //Update Forecast date
            if (ModelWeatherNOAA.Forecast.LastForecastUpdate!=null){

                //Convert to human readable
                SimpleDateFormat Forecastform = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                java.util.Date Forecastdate = null;
                try {
                    Forecastdate = Forecastform.parse(ModelWeatherNOAA.Forecast.LastForecastUpdate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                SimpleDateFormat postForecastFormater = new SimpleDateFormat("MM.dd.yyyy 'at' hh:mm:ssaaa z");
                String newForecastDateStr = postForecastFormater.format(Forecastdate);

                MainFragmentSnapWeather.extendedForecastlastupdated.setText(newForecastDateStr);

            } else {
                MainFragmentSnapWeather.extendedForecastlastupdated.setText("--");
            }

            //loop through extended forecast data
            for (int i = 0; (i < 14); i++) {
                //process day1
                if (i==0) {
                    //Set day
                    if (ModelWeatherNOAA.Forecast.forecastName[i] != null && !ModelWeatherNOAA.Forecast.forecastName[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastName[i].equals("null") ) {
                        MainFragmentSnapWeather.extendedForcastDay1.setText(ModelWeatherNOAA.Forecast.forecastName[i]);

                        //set icon
                        if (ModelWeatherNOAA.Forecast.iconDataPeriod1 != null) {
                            Bitmap icon = BitmapFactory.decodeByteArray(ModelWeatherNOAA.Forecast.iconDataPeriod1, 0, ModelWeatherNOAA.Forecast.iconDataPeriod1.length);
                            MainFragmentSnapWeather.extendedForecastIcon1.setImageBitmap(Bitmap.createScaledBitmap(icon, 86, 86, false));
                        } else {
                            Bitmap icon = BitmapFactory.decodeResource(getAppContext().getResources(), R.raw.black);
                            MainFragmentSnapWeather.extendedForecastIcon1.setImageBitmap(Bitmap.createScaledBitmap(icon, 86, 86, false));
                        }

                        //set short forecast
                        if (ModelWeatherNOAA.Forecast.forecastShort[i] != null && !ModelWeatherNOAA.Forecast.forecastShort[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastShort[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastShortDesc1.setText(ModelWeatherNOAA.Forecast.forecastShort[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastShortDesc1.setText("Not Provided");
                        }

                        //set temp forecast
                        if (ModelWeatherNOAA.Forecast.forecastTemp[i] != null && !ModelWeatherNOAA.Forecast.forecastTemp[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastTemp[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecasttemp1.setText(ModelWeatherNOAA.Forecast.forecastTemp[i] + " °F");
                        } else {
                            MainFragmentSnapWeather.extendedForecasttemp1.setText("Not Provided");
                        }

                        //set wind speed forecast
                        if (ModelWeatherNOAA.Forecast.forecastWindspeed[i] != null && !ModelWeatherNOAA.Forecast.forecastWindspeed[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastWindspeed[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastwindSpeed1.setText(ModelWeatherNOAA.Forecast.forecastWindspeed[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastwindSpeed1.setText("Not Provided");
                        }

                        //set wind direction forecast
                        if (ModelWeatherNOAA.Forecast.forecastWinddirection[i] != null && !ModelWeatherNOAA.Forecast.forecastWinddirection[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastWinddirection[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastwindDirection1.setText(ModelWeatherNOAA.Forecast.forecastWinddirection[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastwindDirection1.setText("Not Provided");
                        }

                        //set detailed forecast
                        if (ModelWeatherNOAA.Forecast.forecastDetailed[i] != null && !ModelWeatherNOAA.Forecast.forecastDetailed[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastDetailed[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastDetailed1.setText(ModelWeatherNOAA.Forecast.forecastDetailed[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastDetailed1.setText("Not Provided");
                        }

                        //Show extended forecast
                        MainFragmentSnapWeather.extendedForecastRelativeLayout1.setVisibility(VISIBLE);
                        MainFragmentSnapWeather.extendedForecastLayoutMain.setVisibility(VISIBLE);

                        } else {
                        MainFragmentSnapWeather.extendedForecastRelativeLayout1.setVisibility(View.GONE);
                        MainFragmentSnapWeather.extendedForecastLayoutMain.setVisibility(View.GONE);
                    }
                }

                //process day2
                if (i==1) {
                    //Set day
                    if (ModelWeatherNOAA.Forecast.forecastName[i] != null && !ModelWeatherNOAA.Forecast.forecastName[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastName[i].equals("null") ) {
                        MainFragmentSnapWeather.extendedForcastDay2.setText(ModelWeatherNOAA.Forecast.forecastName[i]);

                        //set icon
                        if (ModelWeatherNOAA.Forecast.iconDataPeriod2 != null) {
                            Bitmap icon = BitmapFactory.decodeByteArray(ModelWeatherNOAA.Forecast.iconDataPeriod2, 0, ModelWeatherNOAA.Forecast.iconDataPeriod2.length);
                            MainFragmentSnapWeather.extendedForecastIcon2.setImageBitmap(Bitmap.createScaledBitmap(icon, 86, 86, false));
                        } else {
                            Bitmap icon = BitmapFactory.decodeResource(getAppContext().getResources(), R.raw.black);
                            MainFragmentSnapWeather.extendedForecastIcon2.setImageBitmap(Bitmap.createScaledBitmap(icon, 86, 86, false));
                        }

                        //set short forecast
                        if (ModelWeatherNOAA.Forecast.forecastShort[i] != null && !ModelWeatherNOAA.Forecast.forecastShort[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastShort[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastShortDesc2.setText(ModelWeatherNOAA.Forecast.forecastShort[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastShortDesc2.setText("Not Provided");
                        }

                        //set temp forecast
                        if (ModelWeatherNOAA.Forecast.forecastTemp[i] != null && !ModelWeatherNOAA.Forecast.forecastTemp[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastTemp[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecasttemp2.setText(ModelWeatherNOAA.Forecast.forecastTemp[i] + " °F");
                        } else {
                            MainFragmentSnapWeather.extendedForecasttemp2.setText("Not Provided");
                        }

                        //set wind speed forecast
                        if (ModelWeatherNOAA.Forecast.forecastWindspeed[i] != null && !ModelWeatherNOAA.Forecast.forecastWindspeed[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastWindspeed[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastwindSpeed2.setText(ModelWeatherNOAA.Forecast.forecastWindspeed[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastwindSpeed2.setText("Not Provided");
                        }

                        //set wind direction forecast
                        if (ModelWeatherNOAA.Forecast.forecastWinddirection[i] != null && !ModelWeatherNOAA.Forecast.forecastWinddirection[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastWinddirection[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastwindDirection2.setText(ModelWeatherNOAA.Forecast.forecastWinddirection[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastwindDirection2.setText("Not Provided");
                        }

                        //set detailed forecast
                        if (ModelWeatherNOAA.Forecast.forecastDetailed[i] != null && !ModelWeatherNOAA.Forecast.forecastDetailed[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastDetailed[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastDetailed2.setText(ModelWeatherNOAA.Forecast.forecastDetailed[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastDetailed2.setText("Not Provided");
                        }

                        //Show extended forecast
                        MainFragmentSnapWeather.extendedForecastRelativeLayout2.setVisibility(VISIBLE);
                        MainFragmentSnapWeather.extendedForecastLayoutMain.setVisibility(VISIBLE);

                    } else {
                        MainFragmentSnapWeather.extendedForecastRelativeLayout2.setVisibility(View.GONE);
                    }
                }

                //process day3
                if (i==2) {
                    //Set day
                    if (ModelWeatherNOAA.Forecast.forecastName[i] != null && !ModelWeatherNOAA.Forecast.forecastName[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastName[i].equals("null") ) {
                        MainFragmentSnapWeather.extendedForcastDay3.setText(ModelWeatherNOAA.Forecast.forecastName[i]);

                        //set icon
                        if (ModelWeatherNOAA.Forecast.iconDataPeriod3 != null) {
                            Bitmap icon = BitmapFactory.decodeByteArray(ModelWeatherNOAA.Forecast.iconDataPeriod3, 0, ModelWeatherNOAA.Forecast.iconDataPeriod3.length);
                            MainFragmentSnapWeather.extendedForecastIcon3.setImageBitmap(Bitmap.createScaledBitmap(icon, 86, 86, false));
                        } else {
                            Bitmap icon = BitmapFactory.decodeResource(getAppContext().getResources(), R.raw.black);
                            MainFragmentSnapWeather.extendedForecastIcon3.setImageBitmap(Bitmap.createScaledBitmap(icon, 86, 86, false));
                        }

                        //set short forecast
                        if (ModelWeatherNOAA.Forecast.forecastShort[i] != null && !ModelWeatherNOAA.Forecast.forecastShort[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastShort[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastShortDesc3.setText(ModelWeatherNOAA.Forecast.forecastShort[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastShortDesc3.setText("Not Provided");
                        }

                        //set temp forecast
                        if (ModelWeatherNOAA.Forecast.forecastTemp[i] != null && !ModelWeatherNOAA.Forecast.forecastTemp[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastTemp[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecasttemp3.setText(ModelWeatherNOAA.Forecast.forecastTemp[i] + " °F");
                        } else {
                            MainFragmentSnapWeather.extendedForecasttemp3.setText("Not Provided");
                        }

                        //set wind speed forecast
                        if (ModelWeatherNOAA.Forecast.forecastWindspeed[i] != null && !ModelWeatherNOAA.Forecast.forecastWindspeed[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastWindspeed[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastwindSpeed3.setText(ModelWeatherNOAA.Forecast.forecastWindspeed[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastwindSpeed3.setText("Not Provided");
                        }

                        //set wind direction forecast
                        if (ModelWeatherNOAA.Forecast.forecastWinddirection[i] != null && !ModelWeatherNOAA.Forecast.forecastWinddirection[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastWinddirection[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastwindDirection3.setText(ModelWeatherNOAA.Forecast.forecastWinddirection[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastwindDirection3.setText("Not Provided");
                        }

                        //set detailed forecast
                        if (ModelWeatherNOAA.Forecast.forecastDetailed[i] != null && !ModelWeatherNOAA.Forecast.forecastDetailed[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastDetailed[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastDetailed3.setText(ModelWeatherNOAA.Forecast.forecastDetailed[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastDetailed3.setText("Not Provided");
                        }

                        //Show extended forecast
                        MainFragmentSnapWeather.extendedForecastRelativeLayout3.setVisibility(VISIBLE);
                        MainFragmentSnapWeather.extendedForecastLayoutMain.setVisibility(VISIBLE);

                    } else {
                        MainFragmentSnapWeather.extendedForecastRelativeLayout3.setVisibility(View.GONE);
                    }
                }

                //process day4
                if (i==3) {
                    //Set day
                    if (ModelWeatherNOAA.Forecast.forecastName[i] != null && !ModelWeatherNOAA.Forecast.forecastName[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastName[i].equals("null") ) {
                        MainFragmentSnapWeather.extendedForcastDay4.setText(ModelWeatherNOAA.Forecast.forecastName[i]);

                        //set icon
                        if (ModelWeatherNOAA.Forecast.iconDataPeriod4 != null) {
                            Bitmap icon = BitmapFactory.decodeByteArray(ModelWeatherNOAA.Forecast.iconDataPeriod4, 0, ModelWeatherNOAA.Forecast.iconDataPeriod4.length);
                            MainFragmentSnapWeather.extendedForecastIcon4.setImageBitmap(Bitmap.createScaledBitmap(icon, 86, 86, false));
                        } else {
                            Bitmap icon = BitmapFactory.decodeResource(getAppContext().getResources(), R.raw.black);
                            MainFragmentSnapWeather.extendedForecastIcon4.setImageBitmap(Bitmap.createScaledBitmap(icon, 86, 86, false));
                        }

                        //set short forecast
                        if (ModelWeatherNOAA.Forecast.forecastShort[i] != null && !ModelWeatherNOAA.Forecast.forecastShort[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastShort[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastShortDesc4.setText(ModelWeatherNOAA.Forecast.forecastShort[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastShortDesc4.setText("Not Provided");
                        }

                        //set temp forecast
                        if (ModelWeatherNOAA.Forecast.forecastTemp[i] != null && !ModelWeatherNOAA.Forecast.forecastTemp[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastTemp[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecasttemp4.setText(ModelWeatherNOAA.Forecast.forecastTemp[i] + " °F");
                        } else {
                            MainFragmentSnapWeather.extendedForecasttemp4.setText("Not Provided");
                        }

                        //set wind speed forecast
                        if (ModelWeatherNOAA.Forecast.forecastWindspeed[i] != null && !ModelWeatherNOAA.Forecast.forecastWindspeed[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastWindspeed[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastwindSpeed4.setText(ModelWeatherNOAA.Forecast.forecastWindspeed[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastwindSpeed4.setText("Not Provided");
                        }

                        //set wind direction forecast
                        if (ModelWeatherNOAA.Forecast.forecastWinddirection[i] != null && !ModelWeatherNOAA.Forecast.forecastWinddirection[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastWinddirection[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastwindDirection4.setText(ModelWeatherNOAA.Forecast.forecastWinddirection[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastwindDirection4.setText("Not Provided");
                        }

                        //set detailed forecast
                        if (ModelWeatherNOAA.Forecast.forecastDetailed[i] != null && !ModelWeatherNOAA.Forecast.forecastDetailed[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastDetailed[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastDetailed4.setText(ModelWeatherNOAA.Forecast.forecastDetailed[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastDetailed4.setText("Not Provided");
                        }

                        //Show extended forecast
                        MainFragmentSnapWeather.extendedForecastRelativeLayout4.setVisibility(VISIBLE);
                        MainFragmentSnapWeather.extendedForecastLayoutMain.setVisibility(VISIBLE);

                    } else {
                        MainFragmentSnapWeather.extendedForecastRelativeLayout4.setVisibility(View.GONE);
                    }
                }

                //process day5
                if (i==4) {
                    //Set day
                    if (ModelWeatherNOAA.Forecast.forecastName[i] != null && !ModelWeatherNOAA.Forecast.forecastName[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastName[i].equals("null") ) {
                        MainFragmentSnapWeather.extendedForcastDay5.setText(ModelWeatherNOAA.Forecast.forecastName[i]);

                        //set icon
                        if (ModelWeatherNOAA.Forecast.iconDataPeriod5 != null) {
                            Bitmap icon = BitmapFactory.decodeByteArray(ModelWeatherNOAA.Forecast.iconDataPeriod5, 0, ModelWeatherNOAA.Forecast.iconDataPeriod5.length);
                            MainFragmentSnapWeather.extendedForecastIcon5.setImageBitmap(Bitmap.createScaledBitmap(icon, 86, 86, false));
                        } else {
                            Bitmap icon = BitmapFactory.decodeResource(getAppContext().getResources(), R.raw.black);
                            MainFragmentSnapWeather.extendedForecastIcon5.setImageBitmap(Bitmap.createScaledBitmap(icon, 86, 86, false));
                        }

                        //set short forecast
                        if (ModelWeatherNOAA.Forecast.forecastShort[i] != null && !ModelWeatherNOAA.Forecast.forecastShort[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastShort[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastShortDesc5.setText(ModelWeatherNOAA.Forecast.forecastShort[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastShortDesc5.setText("Not Provided");
                        }

                        //set temp forecast
                        if (ModelWeatherNOAA.Forecast.forecastTemp[i] != null && !ModelWeatherNOAA.Forecast.forecastTemp[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastTemp[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecasttemp5.setText(ModelWeatherNOAA.Forecast.forecastTemp[i] + " °F");
                        } else {
                            MainFragmentSnapWeather.extendedForecasttemp5.setText("Not Provided");
                        }

                        //set wind speed forecast
                        if (ModelWeatherNOAA.Forecast.forecastWindspeed[i] != null && !ModelWeatherNOAA.Forecast.forecastWindspeed[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastWindspeed[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastwindSpeed5.setText(ModelWeatherNOAA.Forecast.forecastWindspeed[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastwindSpeed5.setText("Not Provided");
                        }

                        //set wind direction forecast
                        if (ModelWeatherNOAA.Forecast.forecastWinddirection[i] != null && !ModelWeatherNOAA.Forecast.forecastWinddirection[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastWinddirection[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastwindDirection5.setText(ModelWeatherNOAA.Forecast.forecastWinddirection[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastwindDirection5.setText("Not Provided");
                        }

                        //set detailed forecast
                        if (ModelWeatherNOAA.Forecast.forecastDetailed[i] != null && !ModelWeatherNOAA.Forecast.forecastDetailed[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastDetailed[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastDetailed5.setText(ModelWeatherNOAA.Forecast.forecastDetailed[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastDetailed5.setText("Not Provided");
                        }

                        //Show extended forecast
                        MainFragmentSnapWeather.extendedForecastRelativeLayout5.setVisibility(VISIBLE);
                        MainFragmentSnapWeather.extendedForecastLayoutMain.setVisibility(VISIBLE);

                    } else {
                        MainFragmentSnapWeather.extendedForecastRelativeLayout5.setVisibility(View.GONE);
                    }
                }

                //process day6
                if (i==5) {
                    //Set day
                    if (ModelWeatherNOAA.Forecast.forecastName[i] != null && !ModelWeatherNOAA.Forecast.forecastName[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastName[i].equals("null") ) {
                        MainFragmentSnapWeather.extendedForcastDay6.setText(ModelWeatherNOAA.Forecast.forecastName[i]);

                        //set icon
                        if (ModelWeatherNOAA.Forecast.iconDataPeriod6 != null) {
                            Bitmap icon = BitmapFactory.decodeByteArray(ModelWeatherNOAA.Forecast.iconDataPeriod6, 0, ModelWeatherNOAA.Forecast.iconDataPeriod6.length);
                            MainFragmentSnapWeather.extendedForecastIcon6.setImageBitmap(Bitmap.createScaledBitmap(icon, 86, 86, false));
                        } else {
                            Bitmap icon = BitmapFactory.decodeResource(getAppContext().getResources(), R.raw.black);
                            MainFragmentSnapWeather.extendedForecastIcon6.setImageBitmap(Bitmap.createScaledBitmap(icon, 86, 86, false));
                        }

                        //set short forecast
                        if (ModelWeatherNOAA.Forecast.forecastShort[i] != null && !ModelWeatherNOAA.Forecast.forecastShort[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastShort[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastShortDesc6.setText(ModelWeatherNOAA.Forecast.forecastShort[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastShortDesc6.setText("Not Provided");
                        }

                        //set temp forecast
                        if (ModelWeatherNOAA.Forecast.forecastTemp[i] != null && !ModelWeatherNOAA.Forecast.forecastTemp[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastTemp[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecasttemp6.setText(ModelWeatherNOAA.Forecast.forecastTemp[i] + " °F");
                        } else {
                            MainFragmentSnapWeather.extendedForecasttemp6.setText("Not Provided");
                        }

                        //set wind speed forecast
                        if (ModelWeatherNOAA.Forecast.forecastWindspeed[i] != null && !ModelWeatherNOAA.Forecast.forecastWindspeed[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastWindspeed[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastwindSpeed6.setText(ModelWeatherNOAA.Forecast.forecastWindspeed[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastwindSpeed6.setText("Not Provided");
                        }

                        //set wind direction forecast
                        if (ModelWeatherNOAA.Forecast.forecastWinddirection[i] != null && !ModelWeatherNOAA.Forecast.forecastWinddirection[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastWinddirection[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastwindDirection6.setText(ModelWeatherNOAA.Forecast.forecastWinddirection[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastwindDirection6.setText("Not Provided");
                        }

                        //set detailed forecast
                        if (ModelWeatherNOAA.Forecast.forecastDetailed[i] != null && !ModelWeatherNOAA.Forecast.forecastDetailed[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastDetailed[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastDetailed6.setText(ModelWeatherNOAA.Forecast.forecastDetailed[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastDetailed6.setText("Not Provided");
                        }

                        //Show extended forecast
                        MainFragmentSnapWeather.extendedForecastRelativeLayout6.setVisibility(VISIBLE);
                        MainFragmentSnapWeather.extendedForecastLayoutMain.setVisibility(VISIBLE);

                    } else {
                        MainFragmentSnapWeather.extendedForecastRelativeLayout6.setVisibility(View.GONE);
                    }
                }

                //process day7
                if (i==6) {
                    //Set day
                    if (ModelWeatherNOAA.Forecast.forecastName[i] != null && !ModelWeatherNOAA.Forecast.forecastName[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastName[i].equals("null") ) {
                        MainFragmentSnapWeather.extendedForcastDay7.setText(ModelWeatherNOAA.Forecast.forecastName[i]);

                        //set icon
                        if (ModelWeatherNOAA.Forecast.iconDataPeriod7 != null) {
                            Bitmap icon = BitmapFactory.decodeByteArray(ModelWeatherNOAA.Forecast.iconDataPeriod7, 0, ModelWeatherNOAA.Forecast.iconDataPeriod7.length);
                            MainFragmentSnapWeather.extendedForecastIcon7.setImageBitmap(Bitmap.createScaledBitmap(icon, 86, 86, false));
                        } else {
                            Bitmap icon = BitmapFactory.decodeResource(getAppContext().getResources(), R.raw.black);
                            MainFragmentSnapWeather.extendedForecastIcon7.setImageBitmap(Bitmap.createScaledBitmap(icon, 86, 86, false));
                        }

                        //set short forecast
                        if (ModelWeatherNOAA.Forecast.forecastShort[i] != null && !ModelWeatherNOAA.Forecast.forecastShort[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastShort[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastShortDesc7.setText(ModelWeatherNOAA.Forecast.forecastShort[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastShortDesc7.setText("Not Provided");
                        }

                        //set temp forecast
                        if (ModelWeatherNOAA.Forecast.forecastTemp[i] != null && !ModelWeatherNOAA.Forecast.forecastTemp[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastTemp[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecasttemp7.setText(ModelWeatherNOAA.Forecast.forecastTemp[i] + " °F");
                        } else {
                            MainFragmentSnapWeather.extendedForecasttemp7.setText("Not Provided");
                        }

                        //set wind speed forecast
                        if (ModelWeatherNOAA.Forecast.forecastWindspeed[i] != null && !ModelWeatherNOAA.Forecast.forecastWindspeed[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastWindspeed[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastwindSpeed7.setText(ModelWeatherNOAA.Forecast.forecastWindspeed[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastwindSpeed7.setText("Not Provided");
                        }

                        //set wind direction forecast
                        if (ModelWeatherNOAA.Forecast.forecastWinddirection[i] != null && !ModelWeatherNOAA.Forecast.forecastWinddirection[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastWinddirection[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastwindDirection7.setText(ModelWeatherNOAA.Forecast.forecastWinddirection[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastwindDirection7.setText("Not Provided");
                        }

                        //set detailed forecast
                        if (ModelWeatherNOAA.Forecast.forecastDetailed[i] != null && !ModelWeatherNOAA.Forecast.forecastDetailed[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastDetailed[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastDetailed7.setText(ModelWeatherNOAA.Forecast.forecastDetailed[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastDetailed7.setText("Not Provided");
                        }

                        //Show extended forecast
                        MainFragmentSnapWeather.extendedForecastRelativeLayout7.setVisibility(VISIBLE);
                        MainFragmentSnapWeather.extendedForecastLayoutMain.setVisibility(VISIBLE);

                    } else {
                        MainFragmentSnapWeather.extendedForecastRelativeLayout7.setVisibility(View.GONE);
                    }
                }

                //process day8
                if (i==7) {
                    //Set day
                    if (ModelWeatherNOAA.Forecast.forecastName[i] != null && !ModelWeatherNOAA.Forecast.forecastName[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastName[i].equals("null") ) {
                        MainFragmentSnapWeather.extendedForcastDay8.setText(ModelWeatherNOAA.Forecast.forecastName[i]);

                        //set icon
                        if (ModelWeatherNOAA.Forecast.iconDataPeriod8 != null) {
                            Bitmap icon = BitmapFactory.decodeByteArray(ModelWeatherNOAA.Forecast.iconDataPeriod8, 0, ModelWeatherNOAA.Forecast.iconDataPeriod8.length);
                            MainFragmentSnapWeather.extendedForecastIcon8.setImageBitmap(Bitmap.createScaledBitmap(icon, 86, 86, false));
                        } else {
                            Bitmap icon = BitmapFactory.decodeResource(getAppContext().getResources(), R.raw.black);
                            MainFragmentSnapWeather.extendedForecastIcon8.setImageBitmap(Bitmap.createScaledBitmap(icon, 86, 86, false));
                        }

                        //set short forecast
                        if (ModelWeatherNOAA.Forecast.forecastShort[i] != null && !ModelWeatherNOAA.Forecast.forecastShort[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastShort[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastShortDesc8.setText(ModelWeatherNOAA.Forecast.forecastShort[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastShortDesc8.setText("Not Provided");
                        }

                        //set temp forecast
                        if (ModelWeatherNOAA.Forecast.forecastTemp[i] != null && !ModelWeatherNOAA.Forecast.forecastTemp[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastTemp[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecasttemp8.setText(ModelWeatherNOAA.Forecast.forecastTemp[i] + " °F");
                        } else {
                            MainFragmentSnapWeather.extendedForecasttemp8.setText("Not Provided");
                        }

                        //set wind speed forecast
                        if (ModelWeatherNOAA.Forecast.forecastWindspeed[i] != null && !ModelWeatherNOAA.Forecast.forecastWindspeed[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastWindspeed[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastwindSpeed8.setText(ModelWeatherNOAA.Forecast.forecastWindspeed[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastwindSpeed8.setText("Not Provided");
                        }

                        //set wind direction forecast
                        if (ModelWeatherNOAA.Forecast.forecastWinddirection[i] != null && !ModelWeatherNOAA.Forecast.forecastWinddirection[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastWinddirection[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastwindDirection8.setText(ModelWeatherNOAA.Forecast.forecastWinddirection[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastwindDirection8.setText("Not Provided");
                        }

                        //set detailed forecast
                        if (ModelWeatherNOAA.Forecast.forecastDetailed[i] != null && !ModelWeatherNOAA.Forecast.forecastDetailed[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastDetailed[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastDetailed8.setText(ModelWeatherNOAA.Forecast.forecastDetailed[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastDetailed8.setText("Not Provided");
                        }

                        //Show extended forecast
                        MainFragmentSnapWeather.extendedForecastRelativeLayout8.setVisibility(VISIBLE);
                        MainFragmentSnapWeather.extendedForecastLayoutMain.setVisibility(VISIBLE);

                    } else {
                        MainFragmentSnapWeather.extendedForecastRelativeLayout8.setVisibility(View.GONE);
                    }
                }

                //process day9
                if (i==8) {
                    //Set day
                    if (ModelWeatherNOAA.Forecast.forecastName[i] != null && !ModelWeatherNOAA.Forecast.forecastName[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastName[i].equals("null") ) {
                        MainFragmentSnapWeather.extendedForcastDay9.setText(ModelWeatherNOAA.Forecast.forecastName[i]);

                        //set icon
                        if (ModelWeatherNOAA.Forecast.iconDataPeriod9 != null) {
                            Bitmap icon = BitmapFactory.decodeByteArray(ModelWeatherNOAA.Forecast.iconDataPeriod9, 0, ModelWeatherNOAA.Forecast.iconDataPeriod9.length);
                            MainFragmentSnapWeather.extendedForecastIcon9.setImageBitmap(Bitmap.createScaledBitmap(icon, 86, 86, false));
                        } else {
                            Bitmap icon = BitmapFactory.decodeResource(getAppContext().getResources(), R.raw.black);
                            MainFragmentSnapWeather.extendedForecastIcon9.setImageBitmap(Bitmap.createScaledBitmap(icon, 86, 86, false));
                        }

                        //set short forecast
                        if (ModelWeatherNOAA.Forecast.forecastShort[i] != null && !ModelWeatherNOAA.Forecast.forecastShort[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastShort[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastShortDesc9.setText(ModelWeatherNOAA.Forecast.forecastShort[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastShortDesc9.setText("Not Provided");
                        }

                        //set temp forecast
                        if (ModelWeatherNOAA.Forecast.forecastTemp[i] != null && !ModelWeatherNOAA.Forecast.forecastTemp[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastTemp[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecasttemp9.setText(ModelWeatherNOAA.Forecast.forecastTemp[i] + " °F");
                        } else {
                            MainFragmentSnapWeather.extendedForecasttemp9.setText("Not Provided");
                        }

                        //set wind speed forecast
                        if (ModelWeatherNOAA.Forecast.forecastWindspeed[i] != null && !ModelWeatherNOAA.Forecast.forecastWindspeed[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastWindspeed[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastwindSpeed9.setText(ModelWeatherNOAA.Forecast.forecastWindspeed[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastwindSpeed9.setText("Not Provided");
                        }

                        //set wind direction forecast
                        if (ModelWeatherNOAA.Forecast.forecastWinddirection[i] != null && !ModelWeatherNOAA.Forecast.forecastWinddirection[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastWinddirection[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastwindDirection9.setText(ModelWeatherNOAA.Forecast.forecastWinddirection[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastwindDirection9.setText("Not Provided");
                        }

                        //set detailed forecast
                        if (ModelWeatherNOAA.Forecast.forecastDetailed[i] != null && !ModelWeatherNOAA.Forecast.forecastDetailed[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastDetailed[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastDetailed9.setText(ModelWeatherNOAA.Forecast.forecastDetailed[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastDetailed9.setText("Not Provided");
                        }

                        //Show extended forecast
                        MainFragmentSnapWeather.extendedForecastRelativeLayout9.setVisibility(VISIBLE);
                        MainFragmentSnapWeather.extendedForecastLayoutMain.setVisibility(VISIBLE);

                    } else {
                        MainFragmentSnapWeather.extendedForecastRelativeLayout9.setVisibility(View.GONE);
                    }
                }

                //process day10
                if (i==9) {
                    //Set day
                    if (ModelWeatherNOAA.Forecast.forecastName[i] != null && !ModelWeatherNOAA.Forecast.forecastName[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastName[i].equals("null") ) {
                        MainFragmentSnapWeather.extendedForcastDay10.setText(ModelWeatherNOAA.Forecast.forecastName[i]);

                        //set icon
                        if (ModelWeatherNOAA.Forecast.iconDataPeriod10 != null) {
                            Bitmap icon = BitmapFactory.decodeByteArray(ModelWeatherNOAA.Forecast.iconDataPeriod10, 0, ModelWeatherNOAA.Forecast.iconDataPeriod10.length);
                            MainFragmentSnapWeather.extendedForecastIcon10.setImageBitmap(Bitmap.createScaledBitmap(icon, 86, 86, false));
                        } else {
                            Bitmap icon = BitmapFactory.decodeResource(getAppContext().getResources(), R.raw.black);
                            MainFragmentSnapWeather.extendedForecastIcon10.setImageBitmap(Bitmap.createScaledBitmap(icon, 86, 86, false));
                        }

                        //set short forecast
                        if (ModelWeatherNOAA.Forecast.forecastShort[i] != null && !ModelWeatherNOAA.Forecast.forecastShort[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastShort[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastShortDesc10.setText(ModelWeatherNOAA.Forecast.forecastShort[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastShortDesc10.setText("Not Provided");
                        }

                        //set temp forecast
                        if (ModelWeatherNOAA.Forecast.forecastTemp[i] != null && !ModelWeatherNOAA.Forecast.forecastTemp[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastTemp[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecasttemp10.setText(ModelWeatherNOAA.Forecast.forecastTemp[i] + " °F");
                        } else {
                            MainFragmentSnapWeather.extendedForecasttemp10.setText("Not Provided");
                        }

                        //set wind speed forecast
                        if (ModelWeatherNOAA.Forecast.forecastWindspeed[i] != null && !ModelWeatherNOAA.Forecast.forecastWindspeed[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastWindspeed[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastwindSpeed10.setText(ModelWeatherNOAA.Forecast.forecastWindspeed[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastwindSpeed10.setText("Not Provided");
                        }

                        //set wind direction forecast
                        if (ModelWeatherNOAA.Forecast.forecastWinddirection[i] != null && !ModelWeatherNOAA.Forecast.forecastWinddirection[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastWinddirection[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastwindDirection10.setText(ModelWeatherNOAA.Forecast.forecastWinddirection[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastwindDirection10.setText("Not Provided");
                        }

                        //set detailed forecast
                        if (ModelWeatherNOAA.Forecast.forecastDetailed[i] != null && !ModelWeatherNOAA.Forecast.forecastDetailed[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastDetailed[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastDetailed10.setText(ModelWeatherNOAA.Forecast.forecastDetailed[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastDetailed10.setText("Not Provided");
                        }

                        //Show extended forecast
                        MainFragmentSnapWeather.extendedForecastRelativeLayout10.setVisibility(VISIBLE);
                        MainFragmentSnapWeather.extendedForecastLayoutMain.setVisibility(VISIBLE);

                    } else {
                        MainFragmentSnapWeather.extendedForecastRelativeLayout10.setVisibility(View.GONE);
                    }
                }

                //process day11
                if (i==10) {
                    //Set day
                    if (ModelWeatherNOAA.Forecast.forecastName[i] != null && !ModelWeatherNOAA.Forecast.forecastName[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastName[i].equals("null") ) {
                        MainFragmentSnapWeather.extendedForcastDay11.setText(ModelWeatherNOAA.Forecast.forecastName[i]);

                        //set icon
                        if (ModelWeatherNOAA.Forecast.iconDataPeriod11 != null) {
                            Bitmap icon = BitmapFactory.decodeByteArray(ModelWeatherNOAA.Forecast.iconDataPeriod11, 0, ModelWeatherNOAA.Forecast.iconDataPeriod11.length);
                            MainFragmentSnapWeather.extendedForecastIcon11.setImageBitmap(Bitmap.createScaledBitmap(icon, 86, 86, false));
                        } else {
                            Bitmap icon = BitmapFactory.decodeResource(getAppContext().getResources(), R.raw.black);
                            MainFragmentSnapWeather.extendedForecastIcon11.setImageBitmap(Bitmap.createScaledBitmap(icon, 86, 86, false));
                        }

                        //set short forecast
                        if (ModelWeatherNOAA.Forecast.forecastShort[i] != null && !ModelWeatherNOAA.Forecast.forecastShort[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastShort[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastShortDesc11.setText(ModelWeatherNOAA.Forecast.forecastShort[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastShortDesc11.setText("Not Provided");
                        }

                        //set temp forecast
                        if (ModelWeatherNOAA.Forecast.forecastTemp[i] != null && !ModelWeatherNOAA.Forecast.forecastTemp[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastTemp[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecasttemp11.setText(ModelWeatherNOAA.Forecast.forecastTemp[i] + " °F");
                        } else {
                            MainFragmentSnapWeather.extendedForecasttemp11.setText("Not Provided");
                        }

                        //set wind speed forecast
                        if (ModelWeatherNOAA.Forecast.forecastWindspeed[i] != null && !ModelWeatherNOAA.Forecast.forecastWindspeed[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastWindspeed[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastwindSpeed11.setText(ModelWeatherNOAA.Forecast.forecastWindspeed[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastwindSpeed11.setText("Not Provided");
                        }

                        //set wind direction forecast
                        if (ModelWeatherNOAA.Forecast.forecastWinddirection[i] != null && !ModelWeatherNOAA.Forecast.forecastWinddirection[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastWinddirection[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastwindDirection11.setText(ModelWeatherNOAA.Forecast.forecastWinddirection[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastwindDirection11.setText("Not Provided");
                        }

                        //set detailed forecast
                        if (ModelWeatherNOAA.Forecast.forecastDetailed[i] != null && !ModelWeatherNOAA.Forecast.forecastDetailed[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastDetailed[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastDetailed11.setText(ModelWeatherNOAA.Forecast.forecastDetailed[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastDetailed11.setText("Not Provided");
                        }

                        //Show extended forecast
                        MainFragmentSnapWeather.extendedForecastRelativeLayout11.setVisibility(VISIBLE);
                        MainFragmentSnapWeather.extendedForecastLayoutMain.setVisibility(VISIBLE);

                    } else {
                        MainFragmentSnapWeather.extendedForecastRelativeLayout11.setVisibility(View.GONE);
                    }
                }

                //process day12
                if (i==11) {
                    //Set day
                    if (ModelWeatherNOAA.Forecast.forecastName[i] != null && !ModelWeatherNOAA.Forecast.forecastName[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastName[i].equals("null") ) {
                        MainFragmentSnapWeather.extendedForcastDay12.setText(ModelWeatherNOAA.Forecast.forecastName[i]);

                        //set icon
                        if (ModelWeatherNOAA.Forecast.iconDataPeriod12 != null) {
                            Bitmap icon = BitmapFactory.decodeByteArray(ModelWeatherNOAA.Forecast.iconDataPeriod12, 0, ModelWeatherNOAA.Forecast.iconDataPeriod12.length);
                            MainFragmentSnapWeather.extendedForecastIcon12.setImageBitmap(Bitmap.createScaledBitmap(icon, 86, 86, false));
                        } else {
                            Bitmap icon = BitmapFactory.decodeResource(getAppContext().getResources(), R.raw.black);
                            MainFragmentSnapWeather.extendedForecastIcon12.setImageBitmap(Bitmap.createScaledBitmap(icon, 86, 86, false));
                        }

                        //set short forecast
                        if (ModelWeatherNOAA.Forecast.forecastShort[i] != null && !ModelWeatherNOAA.Forecast.forecastShort[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastShort[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastShortDesc12.setText(ModelWeatherNOAA.Forecast.forecastShort[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastShortDesc12.setText("Not Provided");
                        }

                        //set temp forecast
                        if (ModelWeatherNOAA.Forecast.forecastTemp[i] != null && !ModelWeatherNOAA.Forecast.forecastTemp[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastTemp[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecasttemp12.setText(ModelWeatherNOAA.Forecast.forecastTemp[i] + " °F");
                        } else {
                            MainFragmentSnapWeather.extendedForecasttemp12.setText("Not Provided");
                        }

                        //set wind speed forecast
                        if (ModelWeatherNOAA.Forecast.forecastWindspeed[i] != null && !ModelWeatherNOAA.Forecast.forecastWindspeed[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastWindspeed[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastwindSpeed12.setText(ModelWeatherNOAA.Forecast.forecastWindspeed[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastwindSpeed12.setText("Not Provided");
                        }

                        //set wind direction forecast
                        if (ModelWeatherNOAA.Forecast.forecastWinddirection[i] != null && !ModelWeatherNOAA.Forecast.forecastWinddirection[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastWinddirection[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastwindDirection12.setText(ModelWeatherNOAA.Forecast.forecastWinddirection[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastwindDirection12.setText("Not Provided");
                        }

                        //set detailed forecast
                        if (ModelWeatherNOAA.Forecast.forecastDetailed[i] != null && !ModelWeatherNOAA.Forecast.forecastDetailed[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastDetailed[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastDetailed12.setText(ModelWeatherNOAA.Forecast.forecastDetailed[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastDetailed12.setText("Not Provided");
                        }

                        //Show extended forecast
                        MainFragmentSnapWeather.extendedForecastRelativeLayout12.setVisibility(VISIBLE);
                        MainFragmentSnapWeather.extendedForecastLayoutMain.setVisibility(VISIBLE);

                    } else {
                        MainFragmentSnapWeather.extendedForecastRelativeLayout12.setVisibility(View.GONE);
                    }
                }

                //process day13
                if (i==12) {
                    //Set day
                    if (ModelWeatherNOAA.Forecast.forecastName[i] != null && !ModelWeatherNOAA.Forecast.forecastName[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastName[i].equals("null") ) {
                        MainFragmentSnapWeather.extendedForcastDay13.setText(ModelWeatherNOAA.Forecast.forecastName[i]);

                        //set icon
                        if (ModelWeatherNOAA.Forecast.iconDataPeriod13 != null) {
                            Bitmap icon = BitmapFactory.decodeByteArray(ModelWeatherNOAA.Forecast.iconDataPeriod13, 0, ModelWeatherNOAA.Forecast.iconDataPeriod13.length);
                            MainFragmentSnapWeather.extendedForecastIcon13.setImageBitmap(Bitmap.createScaledBitmap(icon, 86, 86, false));
                        } else {
                            Bitmap icon = BitmapFactory.decodeResource(getAppContext().getResources(), R.raw.black);
                            MainFragmentSnapWeather.extendedForecastIcon13.setImageBitmap(Bitmap.createScaledBitmap(icon, 86, 86, false));
                        }

                        //set short forecast
                        if (ModelWeatherNOAA.Forecast.forecastShort[i] != null && !ModelWeatherNOAA.Forecast.forecastShort[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastShort[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastShortDesc13.setText(ModelWeatherNOAA.Forecast.forecastShort[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastShortDesc13.setText("Not Provided");
                        }

                        //set temp forecast
                        if (ModelWeatherNOAA.Forecast.forecastTemp[i] != null && !ModelWeatherNOAA.Forecast.forecastTemp[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastTemp[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecasttemp13.setText(ModelWeatherNOAA.Forecast.forecastTemp[i] + " °F");
                        } else {
                            MainFragmentSnapWeather.extendedForecasttemp13.setText("Not Provided");
                        }

                        //set wind speed forecast
                        if (ModelWeatherNOAA.Forecast.forecastWindspeed[i] != null && !ModelWeatherNOAA.Forecast.forecastWindspeed[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastWindspeed[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastwindSpeed13.setText(ModelWeatherNOAA.Forecast.forecastWindspeed[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastwindSpeed13.setText("Not Provided");
                        }

                        //set wind direction forecast
                        if (ModelWeatherNOAA.Forecast.forecastWinddirection[i] != null && !ModelWeatherNOAA.Forecast.forecastWinddirection[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastWinddirection[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastwindDirection13.setText(ModelWeatherNOAA.Forecast.forecastWinddirection[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastwindDirection13.setText("Not Provided");
                        }

                        //set detailed forecast
                        if (ModelWeatherNOAA.Forecast.forecastDetailed[i] != null && !ModelWeatherNOAA.Forecast.forecastDetailed[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastDetailed[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastDetailed13.setText(ModelWeatherNOAA.Forecast.forecastDetailed[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastDetailed13.setText("Not Provided");
                        }

                        //Show extended forecast
                        MainFragmentSnapWeather.extendedForecastRelativeLayout13.setVisibility(VISIBLE);
                        MainFragmentSnapWeather.extendedForecastLayoutMain.setVisibility(VISIBLE);

                    } else {
                        MainFragmentSnapWeather.extendedForecastRelativeLayout13.setVisibility(View.GONE);
                    }
                }

                //process day14
                if (i==13) {
                    //Set day
                    if (ModelWeatherNOAA.Forecast.forecastName[i] != null && !ModelWeatherNOAA.Forecast.forecastName[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastName[i].equals("null") ) {
                        MainFragmentSnapWeather.extendedForcastDay14.setText(ModelWeatherNOAA.Forecast.forecastName[i]);

                        //set icon
                        if (ModelWeatherNOAA.Forecast.iconDataPeriod14 != null) {
                            Bitmap icon = BitmapFactory.decodeByteArray(ModelWeatherNOAA.Forecast.iconDataPeriod14, 0, ModelWeatherNOAA.Forecast.iconDataPeriod14.length);
                            MainFragmentSnapWeather.extendedForecastIcon14.setImageBitmap(Bitmap.createScaledBitmap(icon, 86, 86, false));
                        } else {
                            Bitmap icon = BitmapFactory.decodeResource(getAppContext().getResources(), R.raw.black);
                            MainFragmentSnapWeather.extendedForecastIcon14.setImageBitmap(Bitmap.createScaledBitmap(icon, 86, 86, false));
                        }

                        //set short forecast
                        if (ModelWeatherNOAA.Forecast.forecastShort[i] != null && !ModelWeatherNOAA.Forecast.forecastShort[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastShort[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastShortDesc14.setText(ModelWeatherNOAA.Forecast.forecastShort[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastShortDesc14.setText("Not Provided");
                        }

                        //set temp forecast
                        if (ModelWeatherNOAA.Forecast.forecastTemp[i] != null && !ModelWeatherNOAA.Forecast.forecastTemp[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastTemp[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecasttemp14.setText(ModelWeatherNOAA.Forecast.forecastTemp[i] + " °F");
                        } else {
                            MainFragmentSnapWeather.extendedForecasttemp14.setText("Not Provided");
                        }

                        //set wind speed forecast
                        if (ModelWeatherNOAA.Forecast.forecastWindspeed[i] != null && !ModelWeatherNOAA.Forecast.forecastWindspeed[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastWindspeed[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastwindSpeed14.setText(ModelWeatherNOAA.Forecast.forecastWindspeed[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastwindSpeed14.setText("Not Provided");
                        }

                        //set wind direction forecast
                        if (ModelWeatherNOAA.Forecast.forecastWinddirection[i] != null && !ModelWeatherNOAA.Forecast.forecastWinddirection[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastWinddirection[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastwindDirection14.setText(ModelWeatherNOAA.Forecast.forecastWinddirection[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastwindDirection14.setText("Not Provided");
                        }

                        //set detailed forecast
                        if (ModelWeatherNOAA.Forecast.forecastDetailed[i] != null && !ModelWeatherNOAA.Forecast.forecastDetailed[i].isEmpty() && !ModelWeatherNOAA.Forecast.forecastDetailed[i].equals("null") ) {
                            MainFragmentSnapWeather.extendedForecastDetailed14.setText(ModelWeatherNOAA.Forecast.forecastDetailed[i]);
                        } else {
                            MainFragmentSnapWeather.extendedForecastDetailed14.setText("Not Provided");
                        }

                        //Show extended forecast
                        MainFragmentSnapWeather.extendedForecastRelativeLayout14.setVisibility(VISIBLE);
                        MainFragmentSnapWeather.extendedForecastLayoutMain.setVisibility(VISIBLE);

                    } else {
                        MainFragmentSnapWeather.extendedForecastRelativeLayout14.setVisibility(View.GONE);
                    }
                }

            }

        } else {
            //Hide extended forecast
            MainFragmentSnapWeather.extendedForecastLayoutMain.setVisibility(VISIBLE);
            MainFragmentSnapWeather.extendedForecastRelativeLayout1.setVisibility(View.GONE);
            MainFragmentSnapWeather.extendedForecastRelativeLayout2.setVisibility(View.GONE);
            MainFragmentSnapWeather.extendedForecastRelativeLayout3.setVisibility(View.GONE);
            MainFragmentSnapWeather.extendedForecastRelativeLayout4.setVisibility(View.GONE);
            MainFragmentSnapWeather.extendedForecastRelativeLayout5.setVisibility(View.GONE);
            MainFragmentSnapWeather.extendedForecastRelativeLayout6.setVisibility(View.GONE);
            MainFragmentSnapWeather.extendedForecastRelativeLayout7.setVisibility(View.GONE);
            MainFragmentSnapWeather.extendedForecastRelativeLayout8.setVisibility(View.GONE);
            MainFragmentSnapWeather.extendedForecastRelativeLayout9.setVisibility(View.GONE);
            MainFragmentSnapWeather.extendedForecastRelativeLayout10.setVisibility(View.GONE);
            MainFragmentSnapWeather.extendedForecastRelativeLayout11.setVisibility(View.GONE);
            MainFragmentSnapWeather.extendedForecastRelativeLayout12.setVisibility(View.GONE);
            MainFragmentSnapWeather.extendedForecastRelativeLayout13.setVisibility(View.GONE);
            MainFragmentSnapWeather.extendedForecastRelativeLayout14.setVisibility(View.GONE);
        }

        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            //update status
            Log.d("LocationDisabled","Location is disabled");
            ModelWeatherNOAA.WeatherPoints.UpdateStatus="Location disabled! Please Enable location.";
        }

        //update status
        Log.d("StatusUpdateLoad","Sending Broadcast Event");
        Intent StatusIntent = new Intent("UPDATE_STATUS");
        getAppContext().sendBroadcast(StatusIntent);

        //Enable refresh icon if failure occurs
        EnableDisableRefreshOption();

    }

    //function to save data from model to storage
    public static void SaveDataModel(Context context) {
        Log.d("Saving","Data Model");
        //Save model to storage
        SharedPreferences settings = context.getSharedPreferences(ModelWeatherNOAA_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = settings.edit();
        prefEditor.clear();

        if (ModelWeatherNOAA.WeatherPoints.firstrun != null) {
            prefEditor.putBoolean("ModelWeatherNOAA.WeatherPoints.firstrun", ModelWeatherNOAA.WeatherPoints.firstrun);
        }
        if (ModelWeatherNOAA.WeatherPoints.UID != null) {
            prefEditor.putString("ModelWeatherNOAA.WeatherPoints.UID", ModelWeatherNOAA.WeatherPoints.UID);
        }

        if (ModelWeatherNOAA.WeatherPoints.lastStatusFailedNotComplete != null) {
            prefEditor.putBoolean("ModelWeatherNOAA.WeatherPoints.lastStatusFailedNotComplete", ModelWeatherNOAA.WeatherPoints.lastStatusFailedNotComplete);
        }

        //Save Location Data
        if (ModelWeatherNOAA.WeatherPoints.point1 != null) {
            prefEditor.putString("ModelWeatherNOAA.WeatherPoints.longitude", ModelWeatherNOAA.WeatherPoints.longitude);
            prefEditor.putString("ModelWeatherNOAA.WeatherPoints.latitude", ModelWeatherNOAA.WeatherPoints.latitude);
            prefEditor.putString("ModelWeatherNOAA.WeatherPoints.lastlocationgettime", ModelWeatherNOAA.WeatherPoints.lastlocationgettime);
            prefEditor.putFloat("ModelWeatherNOAA.WeatherPoints.point1", ModelWeatherNOAA.WeatherPoints.point1);
            prefEditor.putFloat("ModelWeatherNOAA.WeatherPoints.point2", ModelWeatherNOAA.WeatherPoints.point2);
            prefEditor.putString("ModelWeatherNOAA.WeatherPoints.office", ModelWeatherNOAA.WeatherPoints.office);
            prefEditor.putString("ModelWeatherNOAA.WeatherPoints.stationidentifier1", ModelWeatherNOAA.WeatherPoints.stationidentifier1);
            prefEditor.putString("ModelWeatherNOAA.WeatherPoints.stationname1", ModelWeatherNOAA.WeatherPoints.stationname1);
            prefEditor.putString("ModelWeatherNOAA.WeatherPoints.gridx", ModelWeatherNOAA.WeatherPoints.gridx);
            prefEditor.putString("ModelWeatherNOAA.WeatherPoints.gridy", ModelWeatherNOAA.WeatherPoints.gridy);
            prefEditor.putString("ModelWeatherNOAA.WeatherPoints.forecastGridURL", ModelWeatherNOAA.WeatherPoints.forecastGridURL);
            prefEditor.putString("ModelWeatherNOAA.WeatherPoints.radarstation", ModelWeatherNOAA.WeatherPoints.radarstation);
            prefEditor.putString("ModelWeatherNOAA.WeatherPoints.city", ModelWeatherNOAA.WeatherPoints.city);
            prefEditor.putString("ModelWeatherNOAA.WeatherPoints.state", ModelWeatherNOAA.WeatherPoints.state);
            prefEditor.putString("ModelWeatherNOAA.WeatherPoints.forcastzoneurl", ModelWeatherNOAA.WeatherPoints.forcastzoneurl);
            prefEditor.putString("ModelWeatherNOAA.WeatherPoints.forcastzoneid", ModelWeatherNOAA.WeatherPoints.forcastzoneid);
            prefEditor.putString("ModelWeatherNOAA.WeatherPoints.county", ModelWeatherNOAA.WeatherPoints.county);
            prefEditor.putString("ModelWeatherNOAA.WeatherPoints.LastLocationUpdate", ModelWeatherNOAA.WeatherPoints.LastLocationUpdate);
            prefEditor.putString("ModelWeatherNOAA.WeatherPoints.LastCheckFailed", ModelWeatherNOAA.WeatherPoints.LastCheckFailedWeatherPoints);
            prefEditor.putString("ModelWeatherNOAA.WeatherPoints.LastCheckFailed2", ModelWeatherNOAA.WeatherPoints.LastCheckFailedStationData);
            prefEditor.putString("ModelWeatherNOAA.WeatherPoints.UpdateStatus", ModelWeatherNOAA.WeatherPoints.UpdateStatus);

            //Save Current Conditions
            prefEditor.putString("ModelWeatherNOAA.CurrentCondition.dewpoint", ModelWeatherNOAA.CurrentCondition.dewpoint);
            prefEditor.putString("ModelWeatherNOAA.CurrentCondition.descr", ModelWeatherNOAA.CurrentCondition.descr);
            prefEditor.putString("ModelWeatherNOAA.CurrentCondition.icon", ModelWeatherNOAA.CurrentCondition.icon);
            if (ModelWeatherNOAA.CurrentCondition.iconData != null){
                prefEditor.putString("ModelWeatherNOAA.CurrentCondition.iconData", Base64.encodeToString(ModelWeatherNOAA.CurrentCondition.iconData, Base64.DEFAULT)); //Byte array to string
            }
            prefEditor.putString("ModelWeatherNOAA.CurrentCondition.temperature", ModelWeatherNOAA.CurrentCondition.temperature);
            prefEditor.putString("ModelWeatherNOAA.CurrentCondition.humidity", ModelWeatherNOAA.CurrentCondition.humidity);
            prefEditor.putString("ModelWeatherNOAA.CurrentCondition.windspeed", ModelWeatherNOAA.CurrentCondition.windspeed);
            prefEditor.putString("ModelWeatherNOAA.CurrentCondition.windgusts", ModelWeatherNOAA.CurrentCondition.windgusts);
            prefEditor.putString("ModelWeatherNOAA.CurrentCondition.winddirection", ModelWeatherNOAA.CurrentCondition.winddirection);
            prefEditor.putString("ModelWeatherNOAA.CurrentCondition.elevation", ModelWeatherNOAA.CurrentCondition.elevation);
            prefEditor.putString("ModelWeatherNOAA.CurrentCondition.heatindex", ModelWeatherNOAA.CurrentCondition.heatindex);
            prefEditor.putString("ModelWeatherNOAA.CurrentCondition.pressure", ModelWeatherNOAA.CurrentCondition.pressure);
            prefEditor.putString("ModelWeatherNOAA.CurrentCondition.visibility", ModelWeatherNOAA.CurrentCondition.visibility);
            prefEditor.putString("ModelWeatherNOAA.CurrentCondition.LastWeatherUpdate", ModelWeatherNOAA.CurrentCondition.LastWeatherUpdate);

            //Save Forecast Data
            if (ModelWeatherNOAA.Forecast.LastForecastUpdate != null) {
                prefEditor.putString("ModelWeatherNOAA.Forecast.LastForecastUpdate", ModelWeatherNOAA.Forecast.LastForecastUpdate);
            }

            if (ModelWeatherNOAA.Forecast.forecastName != null) {
                StringBuilder sbforecastName = new StringBuilder();
                for (int i = 0; i < ModelWeatherNOAA.Forecast.forecastName.length; i++) {
                    sbforecastName.append(ModelWeatherNOAA.Forecast.forecastName[i]).append(",,");
                }
                prefEditor.putString("ModelWeatherNOAA.Forecast.forecastName", sbforecastName.toString());
            }
            if (ModelWeatherNOAA.Forecast.forecastTemp != null) {
                StringBuilder sbforecastTemp = new StringBuilder();
                for (int i = 0; i < ModelWeatherNOAA.Forecast.forecastTemp.length; i++) {
                    sbforecastTemp.append(ModelWeatherNOAA.Forecast.forecastTemp[i]).append(",,");
                }
                prefEditor.putString("ModelWeatherNOAA.Forecast.forecastTemp", sbforecastTemp.toString());
            }
            if (ModelWeatherNOAA.Forecast.forecastWindspeed != null) {
                StringBuilder sbforecastWindspeed = new StringBuilder();
                for (int i = 0; i < ModelWeatherNOAA.Forecast.forecastWindspeed.length; i++) {
                    sbforecastWindspeed.append(ModelWeatherNOAA.Forecast.forecastWindspeed[i]).append(",,");
                }
                prefEditor.putString("ModelWeatherNOAA.Forecast.forecastWindspeed", sbforecastWindspeed.toString());
            }
            if (ModelWeatherNOAA.Forecast.forecastWinddirection != null) {
                StringBuilder sbforecastWinddirection = new StringBuilder();
                for (int i = 0; i < ModelWeatherNOAA.Forecast.forecastWinddirection.length; i++) {
                    sbforecastWinddirection.append(ModelWeatherNOAA.Forecast.forecastWinddirection[i]).append(",,");
                }
                prefEditor.putString("ModelWeatherNOAA.Forecast.forecastWinddirection", sbforecastWinddirection.toString());
            }
            if (ModelWeatherNOAA.Forecast.forecastIconURL != null) {
                StringBuilder sbforecastIconURL = new StringBuilder();
                for (int i = 0; i < ModelWeatherNOAA.Forecast.forecastIconURL.length; i++) {
                    sbforecastIconURL.append(ModelWeatherNOAA.Forecast.forecastIconURL[i]).append(",,");
                }
                prefEditor.putString("ModelWeatherNOAA.Forecast.forecastIconURL", sbforecastIconURL.toString());
            }
            if (ModelWeatherNOAA.Forecast.forecastShort != null) {
                StringBuilder sbforecastShort = new StringBuilder();
                for (int i = 0; i < ModelWeatherNOAA.Forecast.forecastShort.length; i++) {
                    sbforecastShort.append(ModelWeatherNOAA.Forecast.forecastShort[i]).append(",,");
                }
                prefEditor.putString("ModelWeatherNOAA.Forecast.forecastShort", sbforecastShort.toString());
            }
            if (ModelWeatherNOAA.Forecast.forecastDetailed != null) {
                StringBuilder sbforecastDetailed = new StringBuilder();
                for (int i = 0; i < ModelWeatherNOAA.Forecast.forecastDetailed.length; i++) {
                    sbforecastDetailed.append(ModelWeatherNOAA.Forecast.forecastDetailed[i]).append(",,");
                }
                prefEditor.putString("ModelWeatherNOAA.Forecast.forecastDetailed", sbforecastDetailed.toString());
            }
            if (ModelWeatherNOAA.Forecast.iconDataPeriod1 != null){
                prefEditor.putString("ModelWeatherNOAA.Forecast.iconDataPeriod1", Base64.encodeToString(ModelWeatherNOAA.Forecast.iconDataPeriod1, Base64.DEFAULT)); //Byte array to string
            }
            if (ModelWeatherNOAA.Forecast.iconDataPeriod2 != null){
                prefEditor.putString("ModelWeatherNOAA.Forecast.iconDataPeriod2", Base64.encodeToString(ModelWeatherNOAA.Forecast.iconDataPeriod2, Base64.DEFAULT)); //Byte array to string
            }
            if (ModelWeatherNOAA.Forecast.iconDataPeriod3 != null){
                prefEditor.putString("ModelWeatherNOAA.Forecast.iconDataPeriod3", Base64.encodeToString(ModelWeatherNOAA.Forecast.iconDataPeriod3, Base64.DEFAULT)); //Byte array to string
            }
            if (ModelWeatherNOAA.Forecast.iconDataPeriod4 != null){
                prefEditor.putString("ModelWeatherNOAA.Forecast.iconDataPeriod4", Base64.encodeToString(ModelWeatherNOAA.Forecast.iconDataPeriod4, Base64.DEFAULT)); //Byte array to string
            }
            if (ModelWeatherNOAA.Forecast.iconDataPeriod5 != null){
                prefEditor.putString("ModelWeatherNOAA.Forecast.iconDataPeriod5", Base64.encodeToString(ModelWeatherNOAA.Forecast.iconDataPeriod5, Base64.DEFAULT)); //Byte array to string
            }
            if (ModelWeatherNOAA.Forecast.iconDataPeriod6 != null){
                prefEditor.putString("ModelWeatherNOAA.Forecast.iconDataPeriod6", Base64.encodeToString(ModelWeatherNOAA.Forecast.iconDataPeriod6, Base64.DEFAULT)); //Byte array to string
            }
            if (ModelWeatherNOAA.Forecast.iconDataPeriod7 != null){
                prefEditor.putString("ModelWeatherNOAA.Forecast.iconDataPeriod7", Base64.encodeToString(ModelWeatherNOAA.Forecast.iconDataPeriod7, Base64.DEFAULT)); //Byte array to string
            }
            if (ModelWeatherNOAA.Forecast.iconDataPeriod8 != null){
                prefEditor.putString("ModelWeatherNOAA.Forecast.iconDataPeriod8", Base64.encodeToString(ModelWeatherNOAA.Forecast.iconDataPeriod8, Base64.DEFAULT)); //Byte array to string
            }
            if (ModelWeatherNOAA.Forecast.iconDataPeriod9 != null){
                prefEditor.putString("ModelWeatherNOAA.Forecast.iconDataPeriod9", Base64.encodeToString(ModelWeatherNOAA.Forecast.iconDataPeriod9, Base64.DEFAULT)); //Byte array to string
            }
            if (ModelWeatherNOAA.Forecast.iconDataPeriod10 != null){
                prefEditor.putString("ModelWeatherNOAA.Forecast.iconDataPeriod10", Base64.encodeToString(ModelWeatherNOAA.Forecast.iconDataPeriod10, Base64.DEFAULT)); //Byte array to string
            }
            if (ModelWeatherNOAA.Forecast.iconDataPeriod11 != null){
                prefEditor.putString("ModelWeatherNOAA.Forecast.iconDataPeriod11", Base64.encodeToString(ModelWeatherNOAA.Forecast.iconDataPeriod11, Base64.DEFAULT)); //Byte array to string
            }
            if (ModelWeatherNOAA.Forecast.iconDataPeriod12 != null){
                prefEditor.putString("ModelWeatherNOAA.Forecast.iconDataPeriod12", Base64.encodeToString(ModelWeatherNOAA.Forecast.iconDataPeriod12, Base64.DEFAULT)); //Byte array to string
            }
            if (ModelWeatherNOAA.Forecast.iconDataPeriod13 != null){
                prefEditor.putString("ModelWeatherNOAA.Forecast.iconDataPeriod13", Base64.encodeToString(ModelWeatherNOAA.Forecast.iconDataPeriod13, Base64.DEFAULT)); //Byte array to string
            }
            if (ModelWeatherNOAA.Forecast.iconDataPeriod14 != null){
                prefEditor.putString("ModelWeatherNOAA.Forecast.iconDataPeriod14", Base64.encodeToString(ModelWeatherNOAA.Forecast.iconDataPeriod14, Base64.DEFAULT)); //Byte array to string
            }
            if (ModelWeatherNOAA.Forecast.LastCheckFailedForecast != null){
                prefEditor.putString("ModelWeatherNOAA.Forecast.LastCheckFailedForecast", ModelWeatherNOAA.Forecast.LastCheckFailedForecast);
            }

            //Save compare alert info
            if (ModelWeatherNOAA.CurrentAlerts.event != null) {
                StringBuilder comparetext = new StringBuilder();
                if (ModelWeatherNOAA.CurrentAlerts.event.length > 0) {
                    Log.d("SavingCompareBefore", Arrays.toString(ModelWeatherNOAA.CurrentAlerts.event));

                    //looping through alert list for events
                    for (int i = 0; i < ModelWeatherNOAA.CurrentAlerts.event.length; i++) {
                        if (i == 0) {
                            comparetext.append(ModelWeatherNOAA.CurrentAlerts.event[i]);
                        }
                        if (i != 0) {
                            comparetext.append(", " + ModelWeatherNOAA.CurrentAlerts.event[i]);
                        }
                    }
                }
                ModelWeatherNOAA.CurrentAlerts.compareevent = String.valueOf(comparetext);
                Log.d("SavingCompareEvent", String.valueOf(comparetext));
            }
            //Save Alert Info
            if (ModelWeatherNOAA.CurrentAlerts.effective != null) {
                StringBuilder sbeffective = new StringBuilder();
                for (int i = 0; i < ModelWeatherNOAA.CurrentAlerts.effective.length; i++) {
                    sbeffective.append(ModelWeatherNOAA.CurrentAlerts.effective[i]).append(",,");
                }
                prefEditor.putString("ModelWeatherNOAA.CurrentAlerts.effective", sbeffective.toString());
            }

            if (ModelWeatherNOAA.CurrentAlerts.expires != null) {
                StringBuilder sbexpires = new StringBuilder();
                for (int i = 0; i < ModelWeatherNOAA.CurrentAlerts.expires.length; i++) {
                    sbexpires.append(ModelWeatherNOAA.CurrentAlerts.expires[i]).append(",,");
                }
                prefEditor.putString("ModelWeatherNOAA.CurrentAlerts.expires", sbexpires.toString());
            }

            if (ModelWeatherNOAA.CurrentAlerts.event != null) {
                StringBuilder sbevent = new StringBuilder();
                if (ModelWeatherNOAA.CurrentAlerts.event.length > 0) {
                    Log.d("SavingCurrentBefore", Arrays.toString(ModelWeatherNOAA.CurrentAlerts.event));

                    //looping through alert list for events
                    for (int i = 0; i < ModelWeatherNOAA.CurrentAlerts.event.length; i++) {
                        if (i == 0) {
                            sbevent.append(ModelWeatherNOAA.CurrentAlerts.event[i]);
                        }
                        if (i != 0) {
                            sbevent.append(", " + ModelWeatherNOAA.CurrentAlerts.event[i]);
                        }
                    }
                    Log.d("SavingCurrentAlerts", String.valueOf(sbevent));
                    prefEditor.putString("ModelWeatherNOAA.CurrentAlerts.event", String.valueOf(sbevent));

                }
            }

            if (ModelWeatherNOAA.CurrentAlerts.headline != null) {
                StringBuilder sbheadline = new StringBuilder();
                for (int i = 0; i < ModelWeatherNOAA.CurrentAlerts.headline.length; i++) {
                    sbheadline.append(ModelWeatherNOAA.CurrentAlerts.headline[i]).append(",,");
                }
                prefEditor.putString("ModelWeatherNOAA.CurrentAlerts.headline", sbheadline.toString());
            }

            if (ModelWeatherNOAA.CurrentAlerts.description != null) {
                StringBuilder sbdescription = new StringBuilder();
                for (int i = 0; i < ModelWeatherNOAA.CurrentAlerts.description.length; i++) {
                    sbdescription.append(ModelWeatherNOAA.CurrentAlerts.description[i]).append(",,");
                }
                prefEditor.putString("ModelWeatherNOAA.CurrentAlerts.description", sbdescription.toString());
            }

            if (ModelWeatherNOAA.CurrentAlerts.severity != null) {
                StringBuilder sbseverity = new StringBuilder();
                for (int i = 0; i < ModelWeatherNOAA.CurrentAlerts.severity.length; i++) {
                    sbseverity.append(ModelWeatherNOAA.CurrentAlerts.severity[i]).append(",,");
                }
                prefEditor.putString("ModelWeatherNOAA.CurrentAlerts.severity", sbseverity.toString());
            }

            if (ModelWeatherNOAA.CurrentAlerts.instruction != null) {
                StringBuilder sbinstruction = new StringBuilder();
                for (int i = 0; i < ModelWeatherNOAA.CurrentAlerts.instruction.length; i++) {
                    sbinstruction.append(ModelWeatherNOAA.CurrentAlerts.instruction[i]).append(",,");
                }
                prefEditor.putString("ModelWeatherNOAA.CurrentAlerts.instruction", sbinstruction.toString());
            }

            prefEditor.putBoolean("ModelWeatherNOAA.CurrentAlerts.AlertONOFF", ModelWeatherNOAA.CurrentAlerts.AlertONOFF);
            prefEditor.putString("ModelWeatherNOAA.CurrentAlerts.compareevent", ModelWeatherNOAA.CurrentAlerts.compareevent);
            prefEditor.putString("ModelWeatherNOAA.CurrentAlerts.LastAlertsUpdate", ModelWeatherNOAA.CurrentAlerts.LastAlertsUpdate);

            //Save Settings
            if (ModelWeatherNOAA.Settings.ServiceNotifications != null) {
                prefEditor.putBoolean("ModelWeatherNOAA.Settings.ServiceNotifications", ModelWeatherNOAA.Settings.ServiceNotifications);
            }
            if (ModelWeatherNOAA.Settings.SeverityExtreme != null) {
                prefEditor.putBoolean("ModelWeatherNOAA.Settings.SeverityExtreme", ModelWeatherNOAA.Settings.SeverityExtreme);
            }
            if (ModelWeatherNOAA.Settings.SeveritySevere != null) {
                prefEditor.putBoolean("ModelWeatherNOAA.Settings.SeveritySevere", ModelWeatherNOAA.Settings.SeveritySevere);
            }
            if (ModelWeatherNOAA.Settings.SeverityModerate != null) {
                prefEditor.putBoolean("ModelWeatherNOAA.Settings.SeverityModerate", ModelWeatherNOAA.Settings.SeverityModerate);
            }
            if (ModelWeatherNOAA.Settings.SeverityMinor != null) {
                prefEditor.putBoolean("ModelWeatherNOAA.Settings.SeverityMinor", ModelWeatherNOAA.Settings.SeverityMinor);
            }
            if (ModelWeatherNOAA.Settings.SeverityUnknown != null) {
                prefEditor.putBoolean("ModelWeatherNOAA.Settings.SeverityUnknown", ModelWeatherNOAA.Settings.SeverityUnknown);
            }
            if (ModelWeatherNOAA.Settings.RadarL0 != null) {
                prefEditor.putBoolean("ModelWeatherNOAA.Settings.RadarL0", ModelWeatherNOAA.Settings.RadarL0);
            }
            if (ModelWeatherNOAA.Settings.RadarL2 != null) {
                prefEditor.putBoolean("ModelWeatherNOAA.Settings.RadarL2", ModelWeatherNOAA.Settings.RadarL2);
            }
            if (ModelWeatherNOAA.Settings.RadarL3 != null) {
                prefEditor.putBoolean("ModelWeatherNOAA.Settings.RadarL3", ModelWeatherNOAA.Settings.RadarL3);
            }
            if (ModelWeatherNOAA.Settings.RadarL4 != null) {
                prefEditor.putBoolean("ModelWeatherNOAA.Settings.RadarL4", ModelWeatherNOAA.Settings.RadarL4);
            }
            if (ModelWeatherNOAA.Settings.RadarL5 != null) {
                prefEditor.putBoolean("ModelWeatherNOAA.Settings.RadarL5", ModelWeatherNOAA.Settings.RadarL5);
            }
            if (ModelWeatherNOAA.Settings.RadarL6 != null) {
                prefEditor.putBoolean("ModelWeatherNOAA.Settings.RadarL6", ModelWeatherNOAA.Settings.RadarL6);
            }
            if (ModelWeatherNOAA.Settings.RadarL7 != null) {
                prefEditor.putBoolean("ModelWeatherNOAA.Settings.RadarL7", ModelWeatherNOAA.Settings.RadarL7);
            }

            prefEditor.commit();
        }
    }

    //function to load data from storage to model
    public static void LoadDataModel(Context context) {
        Log.d("Loading","Data Model");
        //load data if sharedpreferences contains a valid tag
        SharedPreferences settings = context.getSharedPreferences(ModelWeatherNOAA_PREFERENCES, Context.MODE_PRIVATE);

        ModelWeatherNOAA.WeatherPoints.firstrun = settings.getBoolean("ModelWeatherNOAA.WeatherPoints.firstrun", true);
        if (ModelWeatherNOAA.WeatherPoints.firstrun) {
            ModelWeatherNOAA.WeatherPoints.UID = UUID.randomUUID().toString();
            Log.d("GenerateUID",ModelWeatherNOAA.WeatherPoints.UID);
            ModelWeatherNOAA.WeatherPoints.firstrun=false;
        } else {
            ModelWeatherNOAA.WeatherPoints.UID = settings.getString("ModelWeatherNOAA.WeatherPoints.UID", null);
            Log.d("LoadUID",ModelWeatherNOAA.WeatherPoints.UID);
        }

        ModelWeatherNOAA.WeatherPoints.lastStatusFailedNotComplete = settings.getBoolean("ModelWeatherNOAA.WeatherPoints.lastStatusFailedNotComplete", false);

        //Load Settings
        ModelWeatherNOAA.Settings.ServiceNotifications = settings.getBoolean("ModelWeatherNOAA.Settings.ServiceNotifications", true);
        ModelWeatherNOAA.Settings.SeverityExtreme = settings.getBoolean("ModelWeatherNOAA.Settings.SeverityExtreme", true);
        ModelWeatherNOAA.Settings.SeveritySevere = settings.getBoolean("ModelWeatherNOAA.Settings.SeveritySevere", true);
        ModelWeatherNOAA.Settings.SeverityModerate = settings.getBoolean("ModelWeatherNOAA.Settings.SeverityModerate", true);
        ModelWeatherNOAA.Settings.SeverityMinor = settings.getBoolean("ModelWeatherNOAA.Settings.SeverityMinor", true);
        ModelWeatherNOAA.Settings.SeverityUnknown = settings.getBoolean("ModelWeatherNOAA.Settings.SeverityUnknown", true);
        ModelWeatherNOAA.Settings.RadarL0 = settings.getBoolean("ModelWeatherNOAA.Settings.RadarL0", false);
        ModelWeatherNOAA.Settings.RadarL2 = settings.getBoolean("ModelWeatherNOAA.Settings.RadarL2", true);
        ModelWeatherNOAA.Settings.RadarL3 = settings.getBoolean("ModelWeatherNOAA.Settings.RadarL3", false);
        ModelWeatherNOAA.Settings.RadarL4 = settings.getBoolean("ModelWeatherNOAA.Settings.RadarL4", true);
        ModelWeatherNOAA.Settings.RadarL5 = settings.getBoolean("ModelWeatherNOAA.Settings.RadarL5", true);
        ModelWeatherNOAA.Settings.RadarL6 = settings.getBoolean("ModelWeatherNOAA.Settings.RadarL6", true);
        ModelWeatherNOAA.Settings.RadarL7 = settings.getBoolean("ModelWeatherNOAA.Settings.RadarL7", true);

        if(settings.contains("ModelWeatherNOAA.WeatherPoints.LastLocationUpdate")) {
            ModelWeatherNOAA.WeatherPoints.longitude = settings.getString("ModelWeatherNOAA.WeatherPoints.longitude", null);
            ModelWeatherNOAA.WeatherPoints.latitude = settings.getString("ModelWeatherNOAA.WeatherPoints.latitude", null);
            ModelWeatherNOAA.WeatherPoints.lastlocationgettime = settings.getString("ModelWeatherNOAA.WeatherPoints.lastlocationgettime", null);
            ModelWeatherNOAA.WeatherPoints.point1 = settings.getFloat("ModelWeatherNOAA.WeatherPoints.point1", 0);
            ModelWeatherNOAA.WeatherPoints.point2 = settings.getFloat("ModelWeatherNOAA.WeatherPoints.point2", 0);
            ModelWeatherNOAA.WeatherPoints.office = settings.getString("ModelWeatherNOAA.WeatherPoints.office", null);
            ModelWeatherNOAA.WeatherPoints.stationidentifier1 = settings.getString("ModelWeatherNOAA.WeatherPoints.stationidentifier1", null);
            ModelWeatherNOAA.WeatherPoints.stationname1 = settings.getString("ModelWeatherNOAA.WeatherPoints.stationname1", null);
            ModelWeatherNOAA.WeatherPoints.gridx = settings.getString("ModelWeatherNOAA.WeatherPoints.gridx", null);
            ModelWeatherNOAA.WeatherPoints.gridy = settings.getString("ModelWeatherNOAA.WeatherPoints.gridy", null);
            ModelWeatherNOAA.WeatherPoints.forecastGridURL = settings.getString("ModelWeatherNOAA.WeatherPoints.forecastGridURL", null);
            ModelWeatherNOAA.WeatherPoints.radarstation = settings.getString("ModelWeatherNOAA.WeatherPoints.radarstation", null);
            ModelWeatherNOAA.WeatherPoints.city = settings.getString("ModelWeatherNOAA.WeatherPoints.city", null);
            ModelWeatherNOAA.WeatherPoints.state = settings.getString("ModelWeatherNOAA.WeatherPoints.state", null);
            ModelWeatherNOAA.WeatherPoints.forcastzoneurl = settings.getString("ModelWeatherNOAA.WeatherPoints.forcastzoneurl", null);
            ModelWeatherNOAA.WeatherPoints.forcastzoneid = settings.getString("ModelWeatherNOAA.WeatherPoints.forcastzoneid", null);
            ModelWeatherNOAA.WeatherPoints.county = settings.getString("ModelWeatherNOAA.WeatherPoints.county", null);
            ModelWeatherNOAA.WeatherPoints.LastLocationUpdate = settings.getString("ModelWeatherNOAA.WeatherPoints.LastLocationUpdate", null);
            ModelWeatherNOAA.WeatherPoints.LastCheckFailedWeatherPoints = settings.getString("ModelWeatherNOAA.WeatherPoints.LastCheckFailed", null);
            ModelWeatherNOAA.WeatherPoints.LastCheckFailedStationData = settings.getString("ModelWeatherNOAA.WeatherPoints.LastCheckFailed2", null);
            ModelWeatherNOAA.WeatherPoints.UpdateStatus = settings.getString("ModelWeatherNOAA.WeatherPoints.UpdateStatus", null);

            ModelWeatherNOAA.CurrentCondition.dewpoint = settings.getString("ModelWeatherNOAA.CurrentCondition.dewpoint", null);
            ModelWeatherNOAA.CurrentCondition.descr = settings.getString("ModelWeatherNOAA.CurrentCondition.descr", null);
            ModelWeatherNOAA.CurrentCondition.icon = settings.getString("ModelWeatherNOAA.CurrentCondition.icon", null);
            if (settings.getString("ModelWeatherNOAA.CurrentCondition.iconData", null) != null) {
                ModelWeatherNOAA.CurrentCondition.iconData = Base64.decode(settings.getString("ModelWeatherNOAA.CurrentCondition.iconData", null), Base64.DEFAULT);
            }
            ModelWeatherNOAA.CurrentCondition.temperature = settings.getString("ModelWeatherNOAA.CurrentCondition.temperature", null);
            ModelWeatherNOAA.CurrentCondition.humidity = settings.getString("ModelWeatherNOAA.CurrentCondition.humidity", null);
            ModelWeatherNOAA.CurrentCondition.windspeed = settings.getString("ModelWeatherNOAA.CurrentCondition.windspeed", null);
            ModelWeatherNOAA.CurrentCondition.windgusts = settings.getString("ModelWeatherNOAA.CurrentCondition.windgusts", null);
            ModelWeatherNOAA.CurrentCondition.winddirection = settings.getString("ModelWeatherNOAA.CurrentCondition.winddirection", null);
            ModelWeatherNOAA.CurrentCondition.elevation = settings.getString("ModelWeatherNOAA.CurrentCondition.elevation", null);
            ModelWeatherNOAA.CurrentCondition.heatindex = settings.getString("ModelWeatherNOAA.CurrentCondition.heatindex", null);
            ModelWeatherNOAA.CurrentCondition.pressure = settings.getString("ModelWeatherNOAA.CurrentCondition.pressure", null);
            ModelWeatherNOAA.CurrentCondition.visibility = settings.getString("ModelWeatherNOAA.CurrentCondition.visibility", null);
            ModelWeatherNOAA.CurrentCondition.LastWeatherUpdate = settings.getString("ModelWeatherNOAA.CurrentCondition.LastWeatherUpdate", null);

            //Load Forecast
            if (settings.getString("ModelWeatherNOAA.Forecast.LastForecastUpdate", null) != null) {
                ModelWeatherNOAA.Forecast.LastForecastUpdate = settings.getString("ModelWeatherNOAA.Forecast.LastForecastUpdate", null);
            }
            if (settings.getString("ModelWeatherNOAA.Forecast.forecastName", null) != null) {
                ModelWeatherNOAA.Forecast.forecastName = settings.getString("ModelWeatherNOAA.Forecast.forecastName", null).split(",,");
            }
            if (settings.getString("ModelWeatherNOAA.Forecast.forecastTemp", null) != null) {
                ModelWeatherNOAA.Forecast.forecastTemp = settings.getString("ModelWeatherNOAA.Forecast.forecastTemp", null).split(",,");
            }
            if (settings.getString("ModelWeatherNOAA.Forecast.forecastWindspeed", null) != null) {
                ModelWeatherNOAA.Forecast.forecastWindspeed = settings.getString("ModelWeatherNOAA.Forecast.forecastWindspeed", null).split(",,");
            }
            if (settings.getString("ModelWeatherNOAA.Forecast.forecastWinddirection", null) != null) {
                ModelWeatherNOAA.Forecast.forecastWinddirection = settings.getString("ModelWeatherNOAA.Forecast.forecastWinddirection", null).split(",,");
            }
            if (settings.getString("ModelWeatherNOAA.Forecast.forecastIconURL", null) != null) {
                ModelWeatherNOAA.Forecast.forecastIconURL = settings.getString("ModelWeatherNOAA.Forecast.forecastIconURL", null).split(",,");
            }
            if (settings.getString("ModelWeatherNOAA.Forecast.forecastShort", null) != null) {
                ModelWeatherNOAA.Forecast.forecastShort = settings.getString("ModelWeatherNOAA.Forecast.forecastShort", null).split(",,");
            }
            if (settings.getString("ModelWeatherNOAA.Forecast.forecastDetailed", null) != null) {
                ModelWeatherNOAA.Forecast.forecastDetailed = settings.getString("ModelWeatherNOAA.Forecast.forecastDetailed", null).split(",,");
            }
            if (settings.getString("ModelWeatherNOAA.Forecast.iconDataPeriod1", null) != null) {
                ModelWeatherNOAA.Forecast.iconDataPeriod1 = Base64.decode(settings.getString("ModelWeatherNOAA.Forecast.iconDataPeriod1", null), Base64.DEFAULT);
            }
            if (settings.getString("ModelWeatherNOAA.Forecast.iconDataPeriod2", null) != null) {
                ModelWeatherNOAA.Forecast.iconDataPeriod2 = Base64.decode(settings.getString("ModelWeatherNOAA.Forecast.iconDataPeriod2", null), Base64.DEFAULT);
            }
            if (settings.getString("ModelWeatherNOAA.Forecast.iconDataPeriod3", null) != null) {
                ModelWeatherNOAA.Forecast.iconDataPeriod3 = Base64.decode(settings.getString("ModelWeatherNOAA.Forecast.iconDataPeriod3", null), Base64.DEFAULT);
            }
            if (settings.getString("ModelWeatherNOAA.Forecast.iconDataPeriod4", null) != null) {
                ModelWeatherNOAA.Forecast.iconDataPeriod4 = Base64.decode(settings.getString("ModelWeatherNOAA.Forecast.iconDataPeriod4", null), Base64.DEFAULT);
            }
            if (settings.getString("ModelWeatherNOAA.Forecast.iconDataPeriod5", null) != null) {
                ModelWeatherNOAA.Forecast.iconDataPeriod5 = Base64.decode(settings.getString("ModelWeatherNOAA.Forecast.iconDataPeriod5", null), Base64.DEFAULT);
            }
            if (settings.getString("ModelWeatherNOAA.Forecast.iconDataPeriod6", null) != null) {
                ModelWeatherNOAA.Forecast.iconDataPeriod6 = Base64.decode(settings.getString("ModelWeatherNOAA.Forecast.iconDataPeriod6", null), Base64.DEFAULT);
            }
            if (settings.getString("ModelWeatherNOAA.Forecast.iconDataPeriod7", null) != null) {
                ModelWeatherNOAA.Forecast.iconDataPeriod7 = Base64.decode(settings.getString("ModelWeatherNOAA.Forecast.iconDataPeriod7", null), Base64.DEFAULT);
            }
            if (settings.getString("ModelWeatherNOAA.Forecast.iconDataPeriod8", null) != null) {
                ModelWeatherNOAA.Forecast.iconDataPeriod8 = Base64.decode(settings.getString("ModelWeatherNOAA.Forecast.iconDataPeriod8", null), Base64.DEFAULT);
            }
            if (settings.getString("ModelWeatherNOAA.Forecast.iconDataPeriod9", null) != null) {
                ModelWeatherNOAA.Forecast.iconDataPeriod9 = Base64.decode(settings.getString("ModelWeatherNOAA.Forecast.iconDataPeriod9", null), Base64.DEFAULT);
            }
            if (settings.getString("ModelWeatherNOAA.Forecast.iconDataPeriod10", null) != null) {
                ModelWeatherNOAA.Forecast.iconDataPeriod10 = Base64.decode(settings.getString("ModelWeatherNOAA.Forecast.iconDataPeriod10", null), Base64.DEFAULT);
            }
            if (settings.getString("ModelWeatherNOAA.Forecast.iconDataPeriod11", null) != null) {
                ModelWeatherNOAA.Forecast.iconDataPeriod11 = Base64.decode(settings.getString("ModelWeatherNOAA.Forecast.iconDataPeriod11", null), Base64.DEFAULT);
            }
            if (settings.getString("ModelWeatherNOAA.Forecast.iconDataPeriod12", null) != null) {
                ModelWeatherNOAA.Forecast.iconDataPeriod12 = Base64.decode(settings.getString("ModelWeatherNOAA.Forecast.iconDataPeriod12", null), Base64.DEFAULT);
            }
            if (settings.getString("ModelWeatherNOAA.Forecast.iconDataPeriod13", null) != null) {
                ModelWeatherNOAA.Forecast.iconDataPeriod13 = Base64.decode(settings.getString("ModelWeatherNOAA.Forecast.iconDataPeriod13", null), Base64.DEFAULT);
            }
            if (settings.getString("ModelWeatherNOAA.Forecast.iconDataPeriod14", null) != null) {
                ModelWeatherNOAA.Forecast.iconDataPeriod14 = Base64.decode(settings.getString("ModelWeatherNOAA.Forecast.iconDataPeriod14", null), Base64.DEFAULT);
            }
            if (settings.getString("ModelWeatherNOAA.Forecast.LastCheckFailedForecast", null) != null) {
                ModelWeatherNOAA.Forecast.LastCheckFailedForecast = settings.getString("ModelWeatherNOAA.Forecast.LastCheckFailedForecast", null);
            }


            //Load Alerts
            if (settings.getString("ModelWeatherNOAA.CurrentAlerts.effective", null) != null) {
                ModelWeatherNOAA.CurrentAlerts.effective = settings.getString("ModelWeatherNOAA.CurrentAlerts.effective", null).split(",,");
            }
            if (settings.getString("ModelWeatherNOAA.CurrentAlerts.expires", null) != null) {
                ModelWeatherNOAA.CurrentAlerts.expires = settings.getString("ModelWeatherNOAA.CurrentAlerts.expires", null).split(",,");
            }
            if (settings.getString("ModelWeatherNOAA.CurrentAlerts.event", null) != null) {
                ModelWeatherNOAA.CurrentAlerts.event = settings.getString("ModelWeatherNOAA.CurrentAlerts.event", null).split(", ");
                Log.d("LoadingCurrentAlertsW", Arrays.toString(ModelWeatherNOAA.CurrentAlerts.event));
            }
            if (settings.getString("ModelWeatherNOAA.CurrentAlerts.headline", null) != null) {
                ModelWeatherNOAA.CurrentAlerts.headline = settings.getString("ModelWeatherNOAA.CurrentAlerts.headline", null).split(",,");
            }
            if (settings.getString("ModelWeatherNOAA.CurrentAlerts.description", null) != null) {
                ModelWeatherNOAA.CurrentAlerts.description = settings.getString("ModelWeatherNOAA.CurrentAlerts.description", null).split(",,");
            }
            if (settings.getString("ModelWeatherNOAA.CurrentAlerts.severity", null) != null) {
                ModelWeatherNOAA.CurrentAlerts.severity = settings.getString("ModelWeatherNOAA.CurrentAlerts.severity", null).split(",,");
            }
            if (settings.getString("ModelWeatherNOAA.CurrentAlerts.instruction", null) != null) {
                ModelWeatherNOAA.CurrentAlerts.instruction = settings.getString("ModelWeatherNOAA.CurrentAlerts.instruction", null).split(",,");
            }
            ModelWeatherNOAA.CurrentAlerts.AlertONOFF = settings.getBoolean("ModelWeatherNOAA.CurrentAlerts.AlertONOFF", false);
            if (settings.getString("ModelWeatherNOAA.CurrentAlerts.compareevent", null) != null) {
                ModelWeatherNOAA.CurrentAlerts.compareevent = settings.getString("ModelWeatherNOAA.CurrentAlerts.compareevent", null);
                Log.d("LoadingCompareEventsW", String.valueOf(ModelWeatherNOAA.CurrentAlerts.compareevent));
            }
            ModelWeatherNOAA.CurrentAlerts.LastAlertsUpdate = settings.getString("ModelWeatherNOAA.CurrentAlerts.LastAlertsUpdate", null);
        }

    }

    //process weather icon locally and fetch if not exist (Requires full icon URL and Condition Description)
    @Nullable
    public static byte[] ProcessWeatherIconLocally(String myStr,String conditionDescription) throws IOException {

        if (myStr == null || myStr.equals("null") || myStr.isEmpty()) {
            return null;
        }
        String[] segments = myStr.split("/");

        Log.d("DayorNight",segments[5]);

        if ((!segments[5].equals("day")) && (!segments[5].equals("night"))) {
            return null;
        }

        //TODO Local List1
        ArrayList<String> arr1 = new ArrayList<String>(7);
        arr1.add("Fair");
        arr1.add("Clear");
        arr1.add("Fair with Haze");
        arr1.add("Clear with Haze");
        arr1.add("Fair and Breezy");
        arr1.add("Clear and Breezy");
        arr1.add("Sunny");

        if (arr1.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.skc);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.nskc);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO Local List2
        ArrayList<String> arr2 = new ArrayList<String>(4);
        arr2.add("A Few Clouds");
        arr2.add("A Few Clouds with Haze");
        arr2.add("A Few Clouds and Breezy");
        arr2.add("Mostly Clear");

        if (arr2.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.few);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.nfew);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO List3
        ArrayList<String> arr3 = new ArrayList<String>(3);
        arr3.add("Partly Cloudy");
        arr3.add("Partly Cloudy with Haze");
        arr3.add("Partly Cloudy and Breezy");

        if (arr3.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.sct);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.nsct);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO List4
        ArrayList<String> arr4 = new ArrayList<String>(3);
        arr4.add("Mostly Cloudy");
        arr4.add("Mostly Cloudy with Haze");
        arr4.add("Mostly Cloudy and Breezy");

        if (arr4.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.bkn);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.nbkn);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO List5
        ArrayList<String> arr5 = new ArrayList<String>(4);
        arr5.add("Overcast");
        arr5.add("Overcast with Haze");
        arr5.add("Overcast and Breezy");
        arr5.add("Cloudy");

        if (arr5.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.ovc);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.novc);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO List6
        ArrayList<String> arr6 = new ArrayList<String>(45);
        arr6.add("Snow");
        arr6.add("Light Snow");
        arr6.add("Heavy Snow");
        arr6.add("Snow Showers");
        arr6.add("Light Snow Showers");
        arr6.add("Heavy Snow Showers");
        arr6.add("Showers Snow");
        arr6.add("Light Showers Snow");
        arr6.add("Heavy Showers Snow");
        arr6.add("Snow Fog/Mist");
        arr6.add("Light Snow Fog/Mist");
        arr6.add("Heavy Snow Fog/Mist");
        arr6.add("Snow Showers Fog/Mist");
        arr6.add("Light Snow Showers Fog/Mist");
        arr6.add("Heavy Snow Showers Fog/Mist");
        arr6.add("Showers Snow Fog/Mist");
        arr6.add("Light Showers Snow Fog/Mist");
        arr6.add("Heavy Showers Snow Fog/Mist");
        arr6.add("Snow Fog");
        arr6.add("Light Snow Fog");
        arr6.add("Heavy Snow Fog");
        arr6.add("Snow Showers Fog");
        arr6.add("Light Snow Showers Fog");
        arr6.add("Heavy Snow Showers Fog");
        arr6.add("Showers in Vicinity Snow");
        arr6.add("Snow Showers in Vicinity");
        arr6.add("Snow Showers in Vicinity Fog/Mist");
        arr6.add("Snow Showers in Vicinity Fog");
        arr6.add("Low Drifting Snow");
        arr6.add("Blowing Snow");
        arr6.add("Snow Low Drifting Snow");
        arr6.add("Snow Blowing Snow");
        arr6.add("Light Snow Low Drifting Snow");
        arr6.add("Light Snow Blowing Snow");
        arr6.add("Light Snow Blowing Snow Fog/Mist");
        arr6.add("Heavy Snow Low Drifting Snow");
        arr6.add("Heavy Snow Blowing Snow");
        arr6.add("Thunderstorm Snow");
        arr6.add("Light Thunderstorm Snow");
        arr6.add("Heavy Thunderstorm Snow");
        arr6.add("Snow Grains");
        arr6.add("Light Snow Grains");
        arr6.add("Heavy Snow Grains");
        arr6.add("Heavy Blowing Snow");
        arr6.add("Blowing Snow in Vicinity");

        if (arr6.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.sn);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.nsn);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO List7
        ArrayList<String> arr7 = new ArrayList<String>(12);
        arr7.add("Rain Snow");
        arr7.add("Light Rain Snow");
        arr7.add("Heavy Rain Snow");
        arr7.add("Snow Rain");
        arr7.add("Light Snow Rain");
        arr7.add("Heavy Snow Rain");
        arr7.add("Drizzle Snow");
        arr7.add("Light Drizzle Snow");
        arr7.add("Heavy Drizzle Snow");
        arr7.add("Snow Drizzle");
        arr7.add("Light Snow Drizzle");
        arr7.add("Heavy Drizzle Snow");

        if (arr7.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.ra_sn);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.nra_sn);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO List8
        ArrayList<String> arr8 = new ArrayList<String>(12);
        arr8.add("Rain Ice Pellets");
        arr8.add("Light Rain Ice Pellets");
        arr8.add("Heavy Rain Ice Pellets");
        arr8.add("Drizzle Ice Pellets");
        arr8.add("Light Drizzle Ice Pellets");
        arr8.add("Heavy Drizzle Ice Pellets");
        arr8.add("Ice Pellets Rain");
        arr8.add("Light Ice Pellets Rain");
        arr8.add("Heavy Ice Pellets Rain");
        arr8.add("Ice Pellets Drizzle");
        arr8.add("Light Ice Pellets Drizzle");
        arr8.add("Heavy Ice Pellets Drizzle");

        if (arr8.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.raip);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.nraip);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO List9
        ArrayList<String> arr9 = new ArrayList<String>(8);
        arr9.add("Freezing Rain");
        arr9.add("Freezing Drizzle");
        arr9.add("Light Freezing Rain");
        arr9.add("Light Freezing Drizzle");
        arr9.add("Heavy Freezing Rain");
        arr9.add("Heavy Freezing Drizzle");
        arr9.add("Freezing Rain in Vicinity");
        arr9.add("Freezing Drizzle in Vicinity");

        if (arr9.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.fzra);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.nfzra);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO List10
        ArrayList<String> arr10 = new ArrayList<String>(12);
        arr10.add("Freezing Rain Rain");
        arr10.add("Light Freezing Rain Rain");
        arr10.add("Heavy Freezing Rain Rain");
        arr10.add("Rain Freezing Rain");
        arr10.add("Light Rain Freezing Rain");
        arr10.add("Heavy Rain Freezing Rain");
        arr10.add("Freezing Drizzle Rain");
        arr10.add("Light Freezing Drizzle Rain");
        arr10.add("Heavy Freezing Drizzle Rain");
        arr10.add("Rain Freezing Drizzle");
        arr10.add("Light Rain Freezing Drizzle");
        arr10.add("Heavy Rain Freezing Drizzle");

        if (arr10.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.ra_fzra);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.nra_fzra);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO List11
        ArrayList<String> arr11 = new ArrayList<String>(12);
        arr11.add("Freezing Rain Snow");
        arr11.add("Light Freezing Rain Snow");
        arr11.add("Heavy Freezing Rain Snow");
        arr11.add("Freezing Drizzle Snow");
        arr11.add("Light Freezing Drizzle Snow");
        arr11.add("Heavy Freezing Drizzle Snow");
        arr11.add("Snow Freezing Rain");
        arr11.add("Light Snow Freezing Rain");
        arr11.add("Heavy Snow Freezing Rain");
        arr11.add("Snow Freezing Drizzle");
        arr11.add("Light Snow Freezing Drizzle");
        arr11.add("Heavy Snow Freezing Drizzle");

        if (arr11.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.fzra_sn);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.nfzra_sn);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO List12
        ArrayList<String> arr12 = new ArrayList<String>(13);
        arr12.add("Ice Pellets");
        arr12.add("Light Ice Pellets");
        arr12.add("Heavy Ice Pellets");
        arr12.add("Ice Pellets in Vicinity");
        arr12.add("Showers Ice Pellets");
        arr12.add("Thunderstorm Ice Pellets");
        arr12.add("Ice Crystals");
        arr12.add("Hail");
        arr12.add("Small Hail/Snow Pellets");
        arr12.add("Light Small Hail/Snow Pellets");
        arr12.add("Heavy small Hail/Snow Pellets");
        arr12.add("Showers Hail");
        arr12.add("Hail Showers");

        if (arr12.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.ip);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.nip);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO List13
        ArrayList<String> arr13 = new ArrayList<String>(1);
        arr13.add("Snow Ice Pellets");

        if (arr13.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.snip);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.nsnip);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO List14
        ArrayList<String> arr14 = new ArrayList<String>(12);
        arr14.add("Light Rain");
        arr14.add("Drizzle");
        arr14.add("Light Drizzle");
        arr14.add("Heavy Drizzle");
        arr14.add("Light Rain Fog/Mist");
        arr14.add("Drizzle Fog/Mist");
        arr14.add("Light Drizzle Fog/Mist");
        arr14.add("Heavy Drizzle Fog/Mist");
        arr14.add("Light Rain Fog");
        arr14.add("Drizzle Fog");
        arr14.add("Light Drizzle Fog");
        arr14.add("Heavy Drizzle Fog");

        if (arr14.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.minus_ra);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.nra);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO List15
        ArrayList<String> arr15 = new ArrayList<String>(6);
        arr15.add("Rain");
        arr15.add("Heavy Rain");
        arr15.add("Rain Fog/Mist");
        arr15.add("Heavy Rain Fog/Mist");
        arr15.add("Rain Fog");
        arr15.add("Heavy Rain Fog");

        if (arr15.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.ra);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.nra);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO List16
        ArrayList<String> arr16 = new ArrayList<String>(17);
        arr16.add("Rain Showers");
        arr16.add("Light Rain Showers");
        arr16.add("Light Rain and Breezy");
        arr16.add("Heavy Rain Showers");
        arr16.add("Rain Showers in Vicinity");
        arr16.add("Light Showers Rain");
        arr16.add("Heavy Showers Rain");
        arr16.add("Showers Rain");
        arr16.add("Showers Rain in Vicinity");
        arr16.add("Rain Showers Fog/Mist");
        arr16.add("Light Rain Showers Fog/Mist");
        arr16.add("Heavy Rain Showers Fog/Mist");
        arr16.add("Rain Showers in Vicinity Fog/Mist");
        arr16.add("Light Showers Rain Fog/Mist");
        arr16.add("Heavy Showers Rain Fog/Mist");
        arr16.add("Showers Rain Fog/Mist");
        arr16.add("Showers Rain in Vicinity Fog/Mist");

        if (arr16.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.shra);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.nshra);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO List17
        ArrayList<String> arr17 = new ArrayList<String>(4);
        arr17.add("Showers in Vicinity");
        arr17.add("Showers in Vicinity Fog/Mist");
        arr17.add("Showers in Vicinity Fog");
        arr17.add("Showers in Vicinity Haze");

        if (arr17.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.hi_shwrs);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.hi_nshwrs);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO List18
        ArrayList<String> arr18 = new ArrayList<String>(57);
        arr18.add("Thunderstorm");
        arr18.add("Thunderstorm Rain");
        arr18.add("Light Thunderstorm Rain");
        arr18.add("Heavy Thunderstorm Rain");
        arr18.add("Thunderstorm Rain Fog/Mist");
        arr18.add("Light Thunderstorm Rain Fog/Mist");
        arr18.add("Heavy Thunderstorm Rain Fog and Windy");
        arr18.add("Heavy Thunderstorm Rain Fog/Mist");
        arr18.add("Thunderstorm Showers in Vicinity");
        arr18.add("Light Thunderstorm Rain Haze");
        arr18.add("Heavy Thunderstorm Rain Haze");
        arr18.add("Thunderstorm Fog");
        arr18.add("Light Thunderstorm Rain Fog");
        arr18.add("Heavy Thunderstorm Rain Fog");
        arr18.add("Thunderstorm Light Rain");
        arr18.add("Thunderstorm Heavy Rain");
        arr18.add("Thunderstorm Rain Fog/Mist");
        arr18.add("Thunderstorm Light Rain Fog/Mist");
        arr18.add("Thunderstorm Heavy Rain Fog/Mist");
        arr18.add("Thunderstorm in Vicinity Fog/Mist");
        arr18.add("Thunderstorm Showers in Vicinity");
        arr18.add("Thunderstorm in Vicinity Haze");
        arr18.add("Thunderstorm Haze in Vicinity");
        arr18.add("Thunderstorm Light Rain Haze");
        arr18.add("Thunderstorm Heavy Rain Haze");
        arr18.add("Thunderstorm Fog");
        arr18.add("Thunderstorm Light Rain Fog");
        arr18.add("Thunderstorm Heavy Rain Fog");
        arr18.add("Thunderstorm Hail");
        arr18.add("Light Thunderstorm Rain Hail");
        arr18.add("Heavy Thunderstorm Rain Hail");
        arr18.add("Thunderstorm Rain Hail Fog/Mist");
        arr18.add("Light Thunderstorm Rain Hail Fog/Mist");
        arr18.add("Heavy Thunderstorm Rain Hail Fog/Hail");
        arr18.add("Thunderstorm Showers in Vicinity Hail");
        arr18.add("Light Thunderstorm Rain Hail Haze");
        arr18.add("Heavy Thunderstorm Rain Hail Haze");
        arr18.add("Thunderstorm Hail Fog");
        arr18.add("Light Thunderstorm Rain Hail Fog");
        arr18.add("Heavy Thunderstorm Rain Hail Fog");
        arr18.add("Thunderstorm Light Rain Hail");
        arr18.add("Thunderstorm Heavy Rain Hail");
        arr18.add("Thunderstorm Rain Hail Fog/Mist");
        arr18.add("Thunderstorm Light Rain Hail Fog/Mist");
        arr18.add("Thunderstorm Heavy Rain Hail Fog/Mist");
        arr18.add("Thunderstorm in Vicinity Hail");
        arr18.add("Thunderstorm in Vicinity Hail Haze");
        arr18.add("Thunderstorm Haze in Vicinity Hail");
        arr18.add("Thunderstorm Light Rain Hail Haze");
        arr18.add("Thunderstorm Heavy Rain Hail Haze");
        arr18.add("Thunderstorm Hail Fog");
        arr18.add("Thunderstorm Light Rain Hail Fog");
        arr18.add("Thunderstorm Heavy Rain Hail Fog");
        arr18.add("Thunderstorm Small Hail/Snow Pellets");
        arr18.add("Thunderstorm Rain Small Hail/Snow Pellets");
        arr18.add("Light Thunderstorm Rain Small Hail/Snow Pellets");
        arr18.add("Heavy Thunderstorm Rain Small Hail/Snow Pellets");

        if (arr18.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.tsra);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.ntsra);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO List19
        ArrayList<String> arr19 = new ArrayList<String>(1);
        arr19.add("Thunderstorm in Vicinity");

        if (arr19.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.scttsra);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.nscttsra);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO List20
        ArrayList<String> arr20 = new ArrayList<String>(2);
        arr20.add("Thunderstorm in Vicinity Fog");
        arr20.add("Thunderstorm in Vicinity Haze");

        if (arr20.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.hi_tsra);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.hi_ntsra);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO List21
        ArrayList<String> arr21 = new ArrayList<String>(3);
        arr21.add("Funnel Cloud");
        arr21.add("Funnel Cloud in Vicinity");
        arr21.add("Tornado/Water Spout");

        if (arr21.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.fc);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.nfc);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO List22
        ArrayList<String> arr22 = new ArrayList<String>(1);
        arr22.add("Tornado");

        if (arr22.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.tor);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.ntor);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO List23
        ArrayList<String> arr23 = new ArrayList<String>(1);
        arr23.add("Hurricane Warning");

        if (arr23.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.hur_warn);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.hur_warn);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO List24
        ArrayList<String> arr24 = new ArrayList<String>(1);
        arr24.add("Hurricane Watch");

        if (arr24.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.hur_watch);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.hur_watch);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO List25
        ArrayList<String> arr25 = new ArrayList<String>(1);
        arr25.add("Tropical Storm Warning");

        if (arr25.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.ts_warn);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.ts_warn);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO List26
        ArrayList<String> arr26 = new ArrayList<String>(1);
        arr26.add("Tropical Storm Watch");

        if (arr26.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.ts_watch);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.ts_watch);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO List27
        ArrayList<String> arr27 = new ArrayList<String>(1);
        arr27.add("Tropical Storm Conditions presently exist w/Hurricane Warning in effect");

        if (arr27.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.ts_nowarn);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.ts_nowarn);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO List28
        ArrayList<String> arr28 = new ArrayList<String>(3);
        arr28.add("Windy");
        arr28.add("Breezy");
        arr28.add("Fair and Windy");

        if (arr28.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.wind_skc);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.nwind_skc);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO List29
        ArrayList<String> arr29 = new ArrayList<String>(1);
        arr29.add("A Few Clouds and Windy");

        if (arr29.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.wind_few);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.nwind_few);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO List30
        ArrayList<String> arr30 = new ArrayList<String>(1);
        arr30.add("Partly Cloudy and Windy");

        if (arr30.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.wind_sct);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.nwind_sct);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO List31
        ArrayList<String> arr31 = new ArrayList<String>(1);
        arr31.add("Mostly Cloudy and Windy");

        if (arr31.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.wind_bkn);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.nwind_bkn);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO List32
        ArrayList<String> arr32 = new ArrayList<String>(1);
        arr32.add("Overcast and Windy");

        if (arr32.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.wind_ovc);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.nwind_ovc);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO List33
        ArrayList<String> arr33 = new ArrayList<String>(14);
        arr33.add("Dust");
        arr33.add("Low Drifting Dust");
        arr33.add("Blowing Dust");
        arr33.add("Sand");
        arr33.add("Blowing Sand");
        arr33.add("Low Drifting Sand");
        arr33.add("Dust/Sand Whirls");
        arr33.add("Dust/Sand Whirls in Vicinity");
        arr33.add("Dust Storm");
        arr33.add("Heavy Dust Storm");
        arr33.add("Dust Storm in Vicinity");
        arr33.add("Sand Storm");
        arr33.add("Heavy Sand Storm");
        arr33.add("Sand Storm in Vicinity");

        if (arr33.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.du);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.ndu);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO List34
        ArrayList<String> arr34 = new ArrayList<String>(1);
        arr34.add("Smoke");

        if (arr34.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.fu);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.nfu);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO List35
        ArrayList<String> arr35 = new ArrayList<String>(1);
        arr35.add("Haze");

        if (arr35.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.hz);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.hz);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO List36
        ArrayList<String> arr36 = new ArrayList<String>(1);
        arr36.add("Hot");

        if (arr36.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.hot);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.hot);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO List37
        ArrayList<String> arr37 = new ArrayList<String>(1);
        arr37.add("Cold");

        if (arr37.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.cold);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.ncold);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO List38
        ArrayList<String> arr38 = new ArrayList<String>(1);
        arr38.add("Blizzard");

        if (arr38.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.blizzard);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.nblizzard);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //TODO List39
        ArrayList<String> arr39 = new ArrayList<String>(13);
        arr39.add("Fog/Mist");
        arr39.add("Freezing Fog");
        arr39.add("Shallow Fog");
        arr39.add("Partial Fog");
        arr39.add("Patches of Fog");
        arr39.add("Fog in Vicinity");
        arr39.add("Freezing Fog in Vicinity");
        arr39.add("Shallow Fog in Vicinity");
        arr39.add("Partial Fog in Vicinity");
        arr39.add("Patches of Fog in Vicinity");
        arr39.add("Showers in Vicinity Fog");
        arr39.add("Light Freezing Fog");
        arr39.add("Heavy Freezing Fog");

        if (arr39.contains(conditionDescription)) {
            InputStream inputStream;
            if (segments[5].equals("day")) {
                inputStream = context.getResources().openRawResource(R.raw.fg);
            } else {
                inputStream = context.getResources().openRawResource(R.raw.nfg);
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        }

        //todo RESOURCE: https://www.weather.gov/forecast-icons

        //return null if nothing catches
        return null;
    }

    //Enable/Disable Radar Option Menu
    public static void EnableDisableRadarOption() {
        if (MainActivitySnapWeather.Radaritem != null) {
            if (ModelWeatherNOAA.WeatherPoints.radarstation != null && !ModelWeatherNOAA.WeatherPoints.radarstation.isEmpty() && !ModelWeatherNOAA.WeatherPoints.radarstation.equals("null")) {
                Log.d("RadarOption", "Radar available, enabling button");
                //TODO DISABLING RADAR UNTIL FURTHER NOTICE!!!
                MainActivitySnapWeather.Radaritem.setVisible(true);
            } else {
                Log.d("RadarOption", "Radar not available, disabling button");
                MainActivitySnapWeather.Radaritem.setVisible(false);
            }
        }
    }

    //Enable/Disable Refresh Option Menu
    public static void EnableDisableRefreshOption() {
        Log.d("RefreshOption", "Started");
        if (MainActivitySnapWeather.RefreshonFail != null) {
            if (ModelWeatherNOAA.WeatherPoints.lastStatusFailedNotComplete) {
                Log.d("RefreshOption", "Failed Update, enabling button");
                MainActivitySnapWeather.RefreshonFail.setVisible(true);
            } else {
                Log.d("RefreshOption", "Successful Update, disabling button");
                MainActivitySnapWeather.RefreshonFail.setVisible(false);
            }
        }
    }

//End
}
