package net.ruckman.snapweatherus;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import net.ruckman.snapweatherus.NOAAWeatherAPI.ModelWeatherNOAA;

import java.util.Arrays;
import java.util.UUID;

import static net.ruckman.snapweatherus.MainFragmentSnapWeather.ModelWeatherNOAA_PREFERENCES;

public class UpdateDataModel104 {

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
                ModelWeatherNOAA.Forecast.forecastName = settings.getString("ModelWeatherNOAA.Forecast.forecastName", null).split(",");
            }
            if (settings.getString("ModelWeatherNOAA.Forecast.forecastTemp", null) != null) {
                ModelWeatherNOAA.Forecast.forecastTemp = settings.getString("ModelWeatherNOAA.Forecast.forecastTemp", null).split(",");
            }
            if (settings.getString("ModelWeatherNOAA.Forecast.forecastWindspeed", null) != null) {
                ModelWeatherNOAA.Forecast.forecastWindspeed = settings.getString("ModelWeatherNOAA.Forecast.forecastWindspeed", null).split(",");
            }
            if (settings.getString("ModelWeatherNOAA.Forecast.forecastWinddirection", null) != null) {
                ModelWeatherNOAA.Forecast.forecastWinddirection = settings.getString("ModelWeatherNOAA.Forecast.forecastWinddirection", null).split(",");
            }
            if (settings.getString("ModelWeatherNOAA.Forecast.forecastIconURL", null) != null) {
                ModelWeatherNOAA.Forecast.forecastIconURL = settings.getString("ModelWeatherNOAA.Forecast.forecastIconURL", null).split(",");
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
                ModelWeatherNOAA.CurrentAlerts.effective = settings.getString("ModelWeatherNOAA.CurrentAlerts.effective", null).split(",");
            }
            if (settings.getString("ModelWeatherNOAA.CurrentAlerts.expires", null) != null) {
                ModelWeatherNOAA.CurrentAlerts.expires = settings.getString("ModelWeatherNOAA.CurrentAlerts.expires", null).split(",");
            }
            if (settings.getString("ModelWeatherNOAA.CurrentAlerts.event", null) != null) {
                ModelWeatherNOAA.CurrentAlerts.event = settings.getString("ModelWeatherNOAA.CurrentAlerts.event", null).split(", ");
                Log.d("LoadingCurrentAlertsW", Arrays.toString(ModelWeatherNOAA.CurrentAlerts.event));
            }
            if (settings.getString("ModelWeatherNOAA.CurrentAlerts.headline", null) != null) {
                ModelWeatherNOAA.CurrentAlerts.headline = settings.getString("ModelWeatherNOAA.CurrentAlerts.headline", null).split(",");
            }
            if (settings.getString("ModelWeatherNOAA.CurrentAlerts.description", null) != null) {
                ModelWeatherNOAA.CurrentAlerts.description = settings.getString("ModelWeatherNOAA.CurrentAlerts.description", null).split(",");
            }
            if (settings.getString("ModelWeatherNOAA.CurrentAlerts.severity", null) != null) {
                ModelWeatherNOAA.CurrentAlerts.severity = settings.getString("ModelWeatherNOAA.CurrentAlerts.severity", null).split(",");
            }
            if (settings.getString("ModelWeatherNOAA.CurrentAlerts.instruction", null) != null) {
                ModelWeatherNOAA.CurrentAlerts.instruction = settings.getString("ModelWeatherNOAA.CurrentAlerts.instruction", null).split(",");
            }
            ModelWeatherNOAA.CurrentAlerts.AlertONOFF = settings.getBoolean("ModelWeatherNOAA.CurrentAlerts.AlertONOFF", false);
            if (settings.getString("ModelWeatherNOAA.CurrentAlerts.compareevent", null) != null) {
                ModelWeatherNOAA.CurrentAlerts.compareevent = settings.getString("ModelWeatherNOAA.CurrentAlerts.compareevent", null);
                Log.d("LoadingCompareEventsW", String.valueOf(ModelWeatherNOAA.CurrentAlerts.compareevent));
            }
            ModelWeatherNOAA.CurrentAlerts.LastAlertsUpdate = settings.getString("ModelWeatherNOAA.CurrentAlerts.LastAlertsUpdate", null);
        }

    }

}