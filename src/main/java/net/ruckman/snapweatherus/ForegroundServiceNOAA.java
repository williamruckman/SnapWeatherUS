package net.ruckman.snapweatherus;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import net.ruckman.snapweatherus.NOAAWeatherAPI.HTTPClientNOAA;
import net.ruckman.snapweatherus.NOAAWeatherAPI.JSONParserNOAA;
import net.ruckman.snapweatherus.NOAAWeatherAPI.ModelWeatherNOAA;

import org.json.JSONException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


import static net.ruckman.snapweatherus.NOAAWeatherAPI.ModelWeatherNOAA.CurrentAlerts.ALERT_NOTIFICATION_CHANNEL_ID;

public class ForegroundServiceNOAA extends Service implements LocationListener {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    NotificationManager notificationManager;
    PendingIntent pi;

    @Override
    public void onCreate() {
        super.onCreate();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Setup Intent for Alert Notification (Launch app)
        Intent resultIntent = new Intent(getApplicationContext(), MainActivitySnapWeather.class);

        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        pi = PendingIntent
                .getActivity(getApplicationContext(), 0, resultIntent,FLAG_IMMUTABLE);

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
            notificationChannel.setSound(alarmSound, att);
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

            //Do Persistent Notification
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), ModelWeatherNOAA.CurrentAlerts.SERVICE_NOTIFICATION_CHANNEL_ID);
            notificationBuilder.setAutoCancel(false)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_service)
                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                            R.mipmap.ic_launcher))
                    .setPriority(Notification.PRIORITY_LOW)
                    .setContentIntent(pi)
                    .setContentTitle("SnapWeatherUS Service")
                    .setContentText("Monitoring for severe weather");
            assert notificationManager != null;
            startForeground(2000, notificationBuilder.build());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (notificationManager != null) {
            notificationManager.cancel(2000);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        LocationManager locationManagerService;
        locationManagerService = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED) {

            assert locationManagerService != null;
            locationManagerService.requestLocationUpdates(LocationManager.GPS_PROVIDER, 900000, 0, (LocationListener) this);
            Log.d("SERVICE LOCATION", "Started");
        }

        return START_STICKY;
    }

    @Override
    public void onLocationChanged(Location location) {

        int NumberOfCurrentAlarms = 0;
        Boolean locationIsSame=false;

        //Load Data Model
        Log.d("SERVICE","Loading Data");
        MainActivitySnapWeather.LoadDataModel(getApplicationContext());
        if (ModelWeatherNOAA.CurrentAlerts.event != null) {
            NumberOfCurrentAlarms = ModelWeatherNOAA.CurrentAlerts.event.length;
        }
        Log.d("SERVICE","Number of active alerts: " + NumberOfCurrentAlarms);

        double lattmp = location.getLatitude();
        double LATITUDE = Math.round(lattmp * 100.0) / 100.0;
        double longtmp = location.getLongitude();
        double LONGITUDE = Math.round(longtmp * 100.0) / 100.0;

        //Check if GPS has changed
        Log.d("LOCATIONCHECK", "Stored Latitude: " + ModelWeatherNOAA.WeatherPoints.latitude);
        Log.d("LOCATIONCHECK", "GPS Latitude: " + LATITUDE);
        Log.d("LOCATIONCHECK", "Stored Longitude: "+ ModelWeatherNOAA.WeatherPoints.longitude);
        Log.d("LOCATIONCHECK", "GPS Longitude: " + LONGITUDE);

        if (!String.valueOf(LATITUDE).equals(ModelWeatherNOAA.WeatherPoints.latitude) || !String.valueOf(LONGITUDE).equals(ModelWeatherNOAA.WeatherPoints.longitude) ) {
            Log.d("LOCATIONCHECK", "New location acquired");
            ModelWeatherNOAA.WeatherPoints.latitude = String.valueOf(LATITUDE);
            ModelWeatherNOAA.WeatherPoints.longitude = String.valueOf(LONGITUDE);
            locationIsSame=false;
        } else {
            Log.d("LOCATIONCHECK", "location is the same");
            locationIsSame=true;
        }

        ModelWeatherNOAA.WeatherPoints.lastlocationgettime = String.valueOf(location.getTime());

        if (ModelWeatherNOAA.WeatherPoints.LastCheckFailedWeatherPoints == null) {
            Log.d("LastCheckFailed", "Is NULL, setting true");
            ModelWeatherNOAA.WeatherPoints.LastCheckFailedWeatherPoints = "true";
        }
        if (ModelWeatherNOAA.WeatherPoints.LastCheckFailedStationData == null) {
            Log.d("LastCheckFailed2", "Is NULL, setting true");
            ModelWeatherNOAA.WeatherPoints.LastCheckFailedStationData = "true";
        }

        //If location is not the same then get NOAA location info
        if (!locationIsSame  || ModelWeatherNOAA.WeatherPoints.LastCheckFailedWeatherPoints.equals("true") || ModelWeatherNOAA.WeatherPoints.LastCheckFailedStationData.equals("true")) {
            Log.d("LocationSame", String.valueOf(locationIsSame));
            Log.d("LastCheckFailed", ModelWeatherNOAA.WeatherPoints.LastCheckFailedWeatherPoints);
            Log.d("LastCheckFailed2", ModelWeatherNOAA.WeatherPoints.LastCheckFailedStationData);
            Log.d("LOCATIONCHECK", "Getting NOAA zone information");

            //Open connection and get point coordinates from NOAA
            String points = ((new HTTPClientNOAA()).getWeatherPoints(ModelWeatherNOAA.WeatherPoints.longitude, ModelWeatherNOAA.WeatherPoints.latitude));
            Log.d("POINTS", "" + points);

            try {

                // Parse gathered weather data according to model
                if (points != null) {
                    JSONParserNOAA.getWeatherPoints(points);
                } else {
                    ModelWeatherNOAA.WeatherPoints.LastCheckFailedWeatherPoints = "true";
                    Log.d("LocationPointFailure", "Unable to get location data from URL possible dns failure...");
                }
                //update date variable for location
                @SuppressLint("SimpleDateFormat") DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                dateFormatter.setLenient(false);
                Date DateLocationRetrieved = new Date();
                ModelWeatherNOAA.WeatherPoints.LastLocationUpdate = dateFormatter.format(DateLocationRetrieved);
                Log.d("LastLocationUpdateSet", ModelWeatherNOAA.WeatherPoints.LastLocationUpdate);
            } catch (JSONException e) {
                ModelWeatherNOAA.WeatherPoints.LastCheckFailedWeatherPoints = "true";
                e.printStackTrace();
            }

            //Open connection and get Forcast Zone Info from NOAA
            String forcastzoneinfo = ((new HTTPClientNOAA()).getForecastZoneData());
            if (forcastzoneinfo != null) {
                Log.d("ForcastZoneInfo", forcastzoneinfo);

                try {

                    // Parse gathered weather data according to model
                    JSONParserNOAA.getForcastZone(forcastzoneinfo);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d("ForecastZoneFailure", "Unable to get forecast zone data from URL possible dns failure...");
            }

        } else {
            if (ModelWeatherNOAA.WeatherPoints.forcastzoneid == null || ModelWeatherNOAA.WeatherPoints.forcastzoneid.isEmpty() || ModelWeatherNOAA.WeatherPoints.forcastzoneid.equals("null")) {
                Log.d("LOCATIONCHECK", "Getting NOAA zone information as it was NULL!!!");

                //Open connection and get point coordinates from NOAA
                String points = ((new HTTPClientNOAA()).getWeatherPoints(ModelWeatherNOAA.WeatherPoints.longitude, ModelWeatherNOAA.WeatherPoints.latitude));
                Log.d("POINTS", "" + points);

                try {

                    // Parse gathered weather data according to model
                    if (points != null) {
                        JSONParserNOAA.getWeatherPoints(points);
                    } else {
                        ModelWeatherNOAA.WeatherPoints.LastCheckFailedWeatherPoints = "true";
                        Log.d("LocationPointFailure", "Unable to get location data from URL possible dns failure...");
                    }
                    //update date variable for location
                    @SuppressLint("SimpleDateFormat") DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                    dateFormatter.setLenient(false);
                    Date DateLocationRetrieved = new Date();
                    ModelWeatherNOAA.WeatherPoints.LastLocationUpdate = dateFormatter.format(DateLocationRetrieved);
                    Log.d("LastLocationUpdateSet", ModelWeatherNOAA.WeatherPoints.LastLocationUpdate);
                } catch (JSONException e) {
                    ModelWeatherNOAA.WeatherPoints.LastCheckFailedWeatherPoints = "true";
                    e.printStackTrace();
                }

                //Open connection and get Forcast Zone Info from NOAA
                String forcastzoneinfo = ((new HTTPClientNOAA()).getForecastZoneData());
                if (forcastzoneinfo != null) {
                    Log.d("ForcastZoneInfo", forcastzoneinfo);

                    try {

                        // Parse gathered weather data according to model
                        JSONParserNOAA.getForcastZone(forcastzoneinfo);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("ForecastZoneFailure", "Unable to get forecast zone data from URL possible dns failure...");
                }
            }
        }

        //get weather alerts from NOAA
        String weatheralerts = (new HTTPClientNOAA().getWeatherAlerts());

        if (weatheralerts !=null && !weatheralerts.isEmpty() && !weatheralerts.equals("null")) {
            Log.d("WeatherAlerts", weatheralerts);
            //Parse weather alerts into array
            try {

                // Parse gathered weather data according to model
                JSONParserNOAA.getWeatherAlerts(weatheralerts);
                //update date variable for location
                @SuppressLint("SimpleDateFormat") DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                dateFormatter.setLenient(false);
                Date DateLocationRetrieved = new Date();
                ModelWeatherNOAA.CurrentAlerts.LastAlertsUpdate=dateFormatter.format(DateLocationRetrieved);
                Log.d("LastAlertUpdateSet", ModelWeatherNOAA.CurrentAlerts.LastAlertsUpdate);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Set Weather Alerts
        if (ModelWeatherNOAA.CurrentAlerts.event!=null) {

            if (ModelWeatherNOAA.CurrentAlerts.event.length > 0) {

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
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), ALERT_NOTIFICATION_CHANNEL_ID);

                    Uri alarmSound = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.weatheralert2);

                    Log.d("AlertSoundURI", String.valueOf(alarmSound));

                    //Looping through alarm notifications
                    if (ModelWeatherNOAA.CurrentAlerts.event.length > 0) {
                        //looping through alert list for events
                        for (int j = 0; j < NumberOfCurrentAlarms; j++) {
                            assert notificationManager != null;
                            notificationManager.cancel(j);
                        }
                        for (int i = 0; i < ModelWeatherNOAA.CurrentAlerts.event.length; i++) {
                            //Do Alarm Notification
                            notificationBuilder.setAutoCancel(false)
                                    .setWhen(System.currentTimeMillis())
                                    .setSmallIcon(R.drawable.ic_notification)
                                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                            R.mipmap.ic_launcher))
                                    .setPriority(Notification.PRIORITY_MAX)
                                    .setContentIntent(pi)
                                    .setContentTitle("ACTIVE WEATHER ALERT")
                                    .setSound(Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.weatheralert2))
                                    .setContentText(String.valueOf(ModelWeatherNOAA.CurrentAlerts.event[i]));
                            assert notificationManager != null;
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
                            }                        }
                    }



                } else {
                    Log.d("AlertManager","Alerts the same, not re-alerting");
                }

            } else {
                ModelWeatherNOAA.CurrentAlerts.AlertONOFF=false;
                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                assert notificationManager != null;
                for (int i = 0; i < NumberOfCurrentAlarms; i++) {
                    notificationManager.cancel(i);
                }
            }
        } else {
            ModelWeatherNOAA.CurrentAlerts.AlertONOFF=false;
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            assert notificationManager != null;
            for (int i = 0; i < NumberOfCurrentAlarms; i++) {
                notificationManager.cancel(i);
            }
        }

        //Save Data
        Log.d("SERVICE","Saving Data");
        MainActivitySnapWeather.SaveDataModel(getApplicationContext());

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d("LocationProvider","Status Changed");
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.d("LocationProvider","Enabled");
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.d("LocationProvider","Disabled");
    }
}