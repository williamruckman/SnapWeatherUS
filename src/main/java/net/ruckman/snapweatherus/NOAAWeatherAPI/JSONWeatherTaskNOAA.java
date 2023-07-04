package net.ruckman.snapweatherus.NOAAWeatherAPI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import net.ruckman.snapweatherus.MainActivitySnapWeather;
import net.ruckman.snapweatherus.MainFragmentSnapWeather;

import org.json.JSONException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static net.ruckman.snapweatherus.MainActivitySnapWeather.getAppContext;

public class JSONWeatherTaskNOAA extends AsyncTask<String, Void, ModelWeatherNOAA> {

    Intent StatusIntent = new Intent("UPDATE_STATUS");

    byte[] iconlocaltest= null;

    @Override
    protected ModelWeatherNOAA doInBackground(String... params) {

        //SAVE PREVIOUS ALERTS FOR COMPARISON TO PREVENT REALERTING
        if (ModelWeatherNOAA.CurrentAlerts.event != null){
            StringBuilder comparetext = new StringBuilder();
            if (ModelWeatherNOAA.CurrentAlerts.event.length > 0) {

                //looping through alert list for events
                for (int i = 0; i < ModelWeatherNOAA.CurrentAlerts.event.length; i++) {
                    if (i == 0) {
                        comparetext.append(ModelWeatherNOAA.CurrentAlerts.event[i]);
                    }
                    if (i != 0) {
                        comparetext.append(", ").append(ModelWeatherNOAA.CurrentAlerts.event[i]);
                    }
                }
            }
            ModelWeatherNOAA.CurrentAlerts.compareevent= String.valueOf(comparetext);
            Log.d("AlertManager", "Pre-Compare text: "  + ModelWeatherNOAA.CurrentAlerts.compareevent);
        }

        if (ModelWeatherNOAA.WeatherPoints.LastCheckFailedWeatherPoints == null) {
            Log.d("LastCheckFailed", "Is NULL, setting true");
            ModelWeatherNOAA.WeatherPoints.LastCheckFailedWeatherPoints = "true";
        }
        if (ModelWeatherNOAA.WeatherPoints.LastCheckFailedStationData == null) {
            Log.d("LastCheckFailed2", "Is NULL, setting true");
            ModelWeatherNOAA.WeatherPoints.LastCheckFailedStationData = "true";
        }
        if (ModelWeatherNOAA.Forecast.LastCheckFailedForecast == null) {
            Log.d("LastCheckFailedForecast", "Is NULL, setting true");
            ModelWeatherNOAA.Forecast.LastCheckFailedForecast = "true";
        }

        //update status
        Log.d("StatusUpdate","Sending Broadcast Event");
        ModelWeatherNOAA.WeatherPoints.UpdateStatus="Updating";
        getAppContext().sendBroadcast(StatusIntent);
        ModelWeatherNOAA.WeatherPoints.lastStatusFailedNotComplete=false;

        //If location is not the same then get NOAA location info
        Log.d("LocationSame", String.valueOf(MainActivitySnapWeather.locationIsSameForeground));
        Log.d("LastCheckFailed", ModelWeatherNOAA.WeatherPoints.LastCheckFailedWeatherPoints);
        if (!MainActivitySnapWeather.locationIsSameForeground || ModelWeatherNOAA.WeatherPoints.LastCheckFailedWeatherPoints.equals("true")) {

            if (params == null) {
                //update status
                Log.d("StatusUpdate","Sending Broadcast Event, failed location null");
                ModelWeatherNOAA.WeatherPoints.UpdateStatus="Failed to get location";
                getAppContext().sendBroadcast(StatusIntent);
                ModelWeatherNOAA.WeatherPoints.LastCheckFailedWeatherPoints = "true";
                ModelWeatherNOAA.WeatherPoints.lastStatusFailedNotComplete=true;
                return null;
            }

            Log.d("LocationParameters","Length="+params.length);

            if (params.length != 2) {
                //update status
                Log.d("StatusUpdate","Sending Broadcast Event, failed location improper fields");
                ModelWeatherNOAA.WeatherPoints.UpdateStatus="Failed to get location";
                getAppContext().sendBroadcast(StatusIntent);
                ModelWeatherNOAA.WeatherPoints.LastCheckFailedWeatherPoints = "true";
                ModelWeatherNOAA.WeatherPoints.lastStatusFailedNotComplete=true;
                return null;
            }

            //Open connection and get point coordinates from NOAA
            String points = ((new HTTPClientNOAA()).getWeatherPoints(params[0], params[1]));
            if (points == null) {
                Log.d("POINTS", " is NULL!");

                //update status
                Log.d("StatusUpdate","Sending Broadcast Event");
                ModelWeatherNOAA.WeatherPoints.UpdateStatus="Failed to download location data";
                getAppContext().sendBroadcast(StatusIntent);

                ModelWeatherNOAA.WeatherPoints.LastCheckFailedWeatherPoints = "true";
                ModelWeatherNOAA.WeatherPoints.lastStatusFailedNotComplete=true;
                return null;
            }
            Log.d("POINTS", points);

            try {

                // Parse gathered weather data according to model
                JSONParserNOAA.getWeatherPoints(points);
                //update date variable for location
                @SuppressLint("SimpleDateFormat") DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                dateFormatter.setLenient(false);
                Date DateLocationRetrieved = new Date();
                ModelWeatherNOAA.WeatherPoints.LastLocationUpdate = dateFormatter.format(DateLocationRetrieved);
                Log.d("LastLocationUpdateSet", ModelWeatherNOAA.WeatherPoints.LastLocationUpdate);
            } catch (JSONException e) {
                //update status
                Log.d("StatusUpdate","Sending Broadcast Event");
                ModelWeatherNOAA.WeatherPoints.UpdateStatus="Failed parsing location data.";
                getAppContext().sendBroadcast(StatusIntent);
                ModelWeatherNOAA.WeatherPoints.lastStatusFailedNotComplete=true;

                Log.d("POINTS", " parsing threw a stack trace!");
                ModelWeatherNOAA.WeatherPoints.LastCheckFailedWeatherPoints = "true";
                e.printStackTrace();
            }

            //Open connection and get Forcast Zone Info from NOAA
            String forcastzoneinfo = ((new HTTPClientNOAA()).getForecastZoneData());
            if (forcastzoneinfo == null) {
                ModelWeatherNOAA.WeatherPoints.LastCheckFailedWeatherPoints = "true";
                //update status
                Log.d("StatusUpdate","Sending Broadcast Event");
                ModelWeatherNOAA.WeatherPoints.UpdateStatus="Failed to download forecast zone information.";
                getAppContext().sendBroadcast(StatusIntent);
                ModelWeatherNOAA.WeatherPoints.lastStatusFailedNotComplete=true;
                return null;
            }
            Log.d("ForcastZoneInfo", forcastzoneinfo);

            try {

                // Parse gathered weather data according to model
                JSONParserNOAA.getForcastZone(forcastzoneinfo);

            } catch (JSONException e) {
                //update status
                Log.d("StatusUpdate","Sending Broadcast Event");
                ModelWeatherNOAA.WeatherPoints.UpdateStatus="Forecast zone data malformed. Unable to process.";
                getAppContext().sendBroadcast(StatusIntent);

                ModelWeatherNOAA.WeatherPoints.lastStatusFailedNotComplete=true;
                ModelWeatherNOAA.WeatherPoints.LastCheckFailedWeatherPoints = "true";
                Log.d("ForcastZoneInfo", "Threw an exception!");
                e.printStackTrace();
            }

        } else {

            if (ModelWeatherNOAA.WeatherPoints.point1 == null || ModelWeatherNOAA.WeatherPoints.point2 == null) {
                Log.d("LOCATIONCHECK", "Getting NOAA point information as it was NULL!!!");
                //Open connection and get point coordinates from NOAA
                //update status
                Log.d("StatusUpdate","Sending Broadcast Event");
                ModelWeatherNOAA.WeatherPoints.UpdateStatus="Downloading weather point data";
                getAppContext().sendBroadcast(StatusIntent);

                String points = ((new HTTPClientNOAA()).getWeatherPoints(params[0], params[1]));
                if (points == null) {
                    //update status
                    Log.d("StatusUpdate","Sending Broadcast Event");
                    ModelWeatherNOAA.WeatherPoints.UpdateStatus="Failed to download weather point data";
                    getAppContext().sendBroadcast(StatusIntent);

                    Log.d("POINTS", "Failed to get points! Halting Execution");
                    ModelWeatherNOAA.WeatherPoints.LastCheckFailedWeatherPoints = "true";
                    ModelWeatherNOAA.WeatherPoints.lastStatusFailedNotComplete=true;
                    return null;
                }
                Log.d("POINTS", points);

                try {

                    // Parse gathered weather data according to model
                    //update status
                    JSONParserNOAA.getWeatherPoints(points);
                    //update date variable for location
                    @SuppressLint("SimpleDateFormat") DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                    dateFormatter.setLenient(false);
                    Date DateLocationRetrieved = new Date();
                    ModelWeatherNOAA.WeatherPoints.LastLocationUpdate = dateFormatter.format(DateLocationRetrieved);
                    Log.d("LastLocationUpdateSet", ModelWeatherNOAA.WeatherPoints.LastLocationUpdate);
                } catch (JSONException e) {
                    //update status
                    Log.d("StatusUpdate","Sending Broadcast Event");
                    ModelWeatherNOAA.WeatherPoints.UpdateStatus="Failed to parse weather point information malformed.";
                    getAppContext().sendBroadcast(StatusIntent);

                    ModelWeatherNOAA.WeatherPoints.lastStatusFailedNotComplete=true;
                    ModelWeatherNOAA.WeatherPoints.LastCheckFailedWeatherPoints = "true";
                    e.printStackTrace();
                }

                //Open connection and get Forcast Zone Info from NOAA
                //update status
                Log.d("StatusUpdate","Sending Broadcast Event");
                ModelWeatherNOAA.WeatherPoints.UpdateStatus="Downloading Forecast Zone data";
                getAppContext().sendBroadcast(StatusIntent);

                String forcastzoneinfo = ((new HTTPClientNOAA()).getForecastZoneData());
                if (forcastzoneinfo == null) {
                    //update status
                    Log.d("StatusUpdate","Sending Broadcast Event");
                    ModelWeatherNOAA.WeatherPoints.UpdateStatus="Failed to download Forecast Zone data";
                    getAppContext().sendBroadcast(StatusIntent);

                    Log.d("ForcastZoneInfo", "Failed to get ForcastZoneInfo! Halting Execution");
                    ModelWeatherNOAA.WeatherPoints.LastCheckFailedWeatherPoints = "true";
                    ModelWeatherNOAA.WeatherPoints.lastStatusFailedNotComplete=true;
                    return null;
                }
                Log.d("ForcastZoneInfo", forcastzoneinfo);

                try {
                    // Parse gathered weather data according to model
                    JSONParserNOAA.getForcastZone(forcastzoneinfo);

                } catch (JSONException e) {
                    //update status
                    Log.d("StatusUpdate","Sending Broadcast Event");
                    ModelWeatherNOAA.WeatherPoints.UpdateStatus="Failed parsing forecast zone data.";
                    getAppContext().sendBroadcast(StatusIntent);

                    ModelWeatherNOAA.WeatherPoints.lastStatusFailedNotComplete=true;
                    ModelWeatherNOAA.WeatherPoints.LastCheckFailedWeatherPoints = "true";
                    e.printStackTrace();
                }
            }

        }

        //If location is not the same then get NOAA location info
        Log.d("LocationSame", String.valueOf(MainActivitySnapWeather.locationIsSameForeground));
        Log.d("LastCheckFailed2", ModelWeatherNOAA.WeatherPoints.LastCheckFailedStationData);
        if (!MainActivitySnapWeather.locationIsSameForeground  || ModelWeatherNOAA.WeatherPoints.LastCheckFailedStationData.equals("true")) {

            //update status
            Log.d("StatusUpdate","Sending Broadcast Event");
            ModelWeatherNOAA.WeatherPoints.UpdateStatus="Downloading weather station data";
            getAppContext().sendBroadcast(StatusIntent);

            //Open connection and get station information from NOAA
            String station = (new HTTPClientNOAA().getStationData());
            if (station == null) {
                //update status
                Log.d("StatusUpdate","Sending Broadcast Event");
                ModelWeatherNOAA.WeatherPoints.UpdateStatus="Failed to download weather station data";
                getAppContext().sendBroadcast(StatusIntent);

                ModelWeatherNOAA.WeatherPoints.LastCheckFailedStationData ="true";
                Log.d("station", "Failed to get station! Halting Execution");
                ModelWeatherNOAA.WeatherPoints.lastStatusFailedNotComplete=true;
                return null;
            }
            Log.d("station", station);

            try {

                // Parse gathered weather data according to model
                JSONParserNOAA.getWeatherStations(station);

            } catch (JSONException e) {
                //update status
                Log.d("StatusUpdate","Sending Broadcast Event");
                ModelWeatherNOAA.WeatherPoints.UpdateStatus="Failed parsing weather station data";
                getAppContext().sendBroadcast(StatusIntent);

                ModelWeatherNOAA.WeatherPoints.lastStatusFailedNotComplete=true;
                ModelWeatherNOAA.WeatherPoints.LastCheckFailedStationData ="true";
                e.printStackTrace();
            }

        } else {

            if (ModelWeatherNOAA.WeatherPoints.stationidentifier1 == null) {

                Log.d("LOCATIONCHECK", "Getting NOAA station information as it was NULL!!!");

                //update status
                Log.d("StatusUpdate","Sending Broadcast Event");
                ModelWeatherNOAA.WeatherPoints.UpdateStatus="Downloading weather station data";
                getAppContext().sendBroadcast(StatusIntent);

                //Open connection and get station information from NOAA
                String station = (new HTTPClientNOAA().getStationData());
                if (station == null) {
                    //update status
                    Log.d("StatusUpdate","Sending Broadcast Event");
                    ModelWeatherNOAA.WeatherPoints.UpdateStatus="Failed to download weather station data";
                    getAppContext().sendBroadcast(StatusIntent);

                    Log.d("station", "Failed to get station! Halting Execution");
                    ModelWeatherNOAA.WeatherPoints.LastCheckFailedStationData ="true";
                    ModelWeatherNOAA.WeatherPoints.lastStatusFailedNotComplete=true;
                    return null;
                }
                Log.d("station", station);

                try {

                    // Parse gathered weather data according to model
                    JSONParserNOAA.getWeatherStations(station);

                } catch (JSONException e) {
                    //update status
                    Log.d("StatusUpdate","Sending Broadcast Event");
                    ModelWeatherNOAA.WeatherPoints.UpdateStatus="Failed parsing weather station data";
                    getAppContext().sendBroadcast(StatusIntent);

                    ModelWeatherNOAA.WeatherPoints.lastStatusFailedNotComplete=true;
                    ModelWeatherNOAA.WeatherPoints.LastCheckFailedStationData ="true";
                    e.printStackTrace();
                }
            }

        }

        //get weather alerts from NOAA
        //update status
        Log.d("StatusUpdate","Sending Broadcast Event");
        ModelWeatherNOAA.WeatherPoints.UpdateStatus="Downloading weather alert data";
        getAppContext().sendBroadcast(StatusIntent);

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
                //update status
                Log.d("StatusUpdate","Sending Broadcast Event");
                ModelWeatherNOAA.WeatherPoints.UpdateStatus="Failed parsing weather alert data";
                getAppContext().sendBroadcast(StatusIntent);

                ModelWeatherNOAA.WeatherPoints.lastStatusFailedNotComplete=true;
                e.printStackTrace();
            }
        }

        //Open connection and get current weather observations information from NOAA
        //update status
        Log.d("StatusUpdate","Sending Broadcast Event");
        ModelWeatherNOAA.WeatherPoints.UpdateStatus="Downloading weather observation data";
        getAppContext().sendBroadcast(StatusIntent);

        String observations = (new HTTPClientNOAA().getObservationData());
        if (observations == null){
            //update status
            Log.d("StatusUpdate","Sending Broadcast Event");
            ModelWeatherNOAA.WeatherPoints.UpdateStatus="Failed Downloading weather observation data";
            getAppContext().sendBroadcast(StatusIntent);

            Log.d("observations", "Failed to get observations! Halting Execution");
            ModelWeatherNOAA.WeatherPoints.lastStatusFailedNotComplete=true;
            return null;
        }
        Log.d("observations", observations);

        try {
            // Parse gathered weather data according to model
            JSONParserNOAA.getCurrentConditions(observations);
            //update date variable for location
            @SuppressLint("SimpleDateFormat") DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
            dateFormatter.setLenient(false);
            Date DateLocationRetrieved = new Date();
            ModelWeatherNOAA.CurrentCondition.LastWeatherUpdate=dateFormatter.format(DateLocationRetrieved);
            Log.d("LastWeatherUpdateSet", ModelWeatherNOAA.CurrentCondition.LastWeatherUpdate);

        } catch (JSONException e) {
            //update status
            Log.d("StatusUpdate","Sending Broadcast Event");
            ModelWeatherNOAA.WeatherPoints.UpdateStatus="Failed parsing weather observation data";
            getAppContext().sendBroadcast(StatusIntent);
            ModelWeatherNOAA.WeatherPoints.lastStatusFailedNotComplete=true;

            e.printStackTrace();
        }

        //TODO try to load icon data locally and if not available then download the icon
        //update status
        Log.d("StatusUpdate","Sending Broadcast Event");
        ModelWeatherNOAA.WeatherPoints.UpdateStatus="Downloading weather icon";
        getAppContext().sendBroadcast(StatusIntent);

        iconlocaltest=null;
        try {
            iconlocaltest = MainActivitySnapWeather.ProcessWeatherIconLocally(ModelWeatherNOAA.CurrentCondition.icon,ModelWeatherNOAA.CurrentCondition.descr);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (iconlocaltest != null) {
            Log.d("LocalICONData", "Using local icon");
            ModelWeatherNOAA.CurrentCondition.iconData = iconlocaltest;
        } else {
            //Get icon data
            Log.d("RemoteICONData", "Using remote icon");
            ModelWeatherNOAA.CurrentCondition.iconData = ((new HTTPClientNOAA()).getImage(ModelWeatherNOAA.CurrentCondition.icon));
            if (ModelWeatherNOAA.CurrentCondition.iconData == null){
                //update status
                Log.d("StatusUpdate","Sending Broadcast Event");
                ModelWeatherNOAA.WeatherPoints.UpdateStatus="Failed downloading weather icon";
                getAppContext().sendBroadcast(StatusIntent);
                //ModelWeatherNOAA.WeatherPoints.lastStatusFailedNotComplete=true;

                //Log.d("ICONData", "Failed to download ICON Data! Halting execution");
                //return null; //Testing null condition

            }
            Log.d("ICONData", Arrays.toString(ModelWeatherNOAA.CurrentCondition.iconData));
        }

        //Check if detailed forecast data is missing and set recheck
        if (ModelWeatherNOAA.WeatherPoints.forecastGridURL == null) {
            Log.d("ForecastURL", "Is NULL, setting re-check");
            ModelWeatherNOAA.WeatherPoints.LastCheckFailedWeatherPoints = "true";
        }

        //get extended detailed forecast
        //update status
        Log.d("StatusUpdate","Sending Broadcast Event");
        ModelWeatherNOAA.WeatherPoints.UpdateStatus="Downloading extended forecast";
        getAppContext().sendBroadcast(StatusIntent);

        String forecastData = (new HTTPClientNOAA().getDetailedExtendedForecast());
        if (forecastData !=null && !forecastData.isEmpty() && !forecastData.equals("null")) {
            Log.d("forecastData", forecastData);
            try {
                // Parse gathered forecast data according to model
                JSONParserNOAA.getForecastData(forecastData);
                ModelWeatherNOAA.Forecast.LastCheckFailedForecast = "false";
                //update date variable for location
                @SuppressLint("SimpleDateFormat") DateFormat dateForecastFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                dateForecastFormatter.setLenient(false);
                Date DateForecastRetrieved = new Date();
                ModelWeatherNOAA.Forecast.LastForecastUpdate=dateForecastFormatter.format(DateForecastRetrieved);
                Log.d("LastForecastUpdateSet", ModelWeatherNOAA.Forecast.LastForecastUpdate);
            } catch (JSONException e) {
                //update status
                Log.d("StatusUpdate","Sending Broadcast Event");
                ModelWeatherNOAA.WeatherPoints.UpdateStatus="Failed parsing extended forecast";
                getAppContext().sendBroadcast(StatusIntent);
                ModelWeatherNOAA.WeatherPoints.lastStatusFailedNotComplete=true;

                Log.d("forecastData", "Had an exception!");
                ModelWeatherNOAA.WeatherPoints.LastCheckFailedWeatherPoints = "true";
                ModelWeatherNOAA.Forecast.LastCheckFailedForecast = "true";
                e.printStackTrace();
            }
        } else {
            //update status
            Log.d("StatusUpdate","Sending Broadcast Event");
            ModelWeatherNOAA.WeatherPoints.UpdateStatus="Failed downloading extended forecast";
            getAppContext().sendBroadcast(StatusIntent);
            ModelWeatherNOAA.WeatherPoints.lastStatusFailedNotComplete=true;

            ModelWeatherNOAA.WeatherPoints.LastCheckFailedWeatherPoints = "true";
            ModelWeatherNOAA.Forecast.LastCheckFailedForecast = "true";
            Log.d("forecastData", "IS NULL! Halting execution");
            return null;
        }

        //TODO get icon for each item in array for the extended detailed forecast
        //update status
        Log.d("StatusUpdate","Sending Broadcast Event");
        ModelWeatherNOAA.WeatherPoints.UpdateStatus="Downloading extended forecast icons";
        getAppContext().sendBroadcast(StatusIntent);


        //looping through forecast array (Weather Forecast)
        for (int i = 0; i < ModelWeatherNOAA.Forecast.forecastIconURL.length; i++) {
            //TODO try to load icon data locally and if not available then download the icon
            iconlocaltest=null;
            try {
                iconlocaltest = MainActivitySnapWeather.ProcessWeatherIconLocally(ModelWeatherNOAA.Forecast.forecastIconURL[i],ModelWeatherNOAA.Forecast.forecastShort[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (iconlocaltest != null) {
                Log.d("LocalICONData", "Using local icon");
                if (i==0) {
                    ModelWeatherNOAA.Forecast.iconDataPeriod1 = iconlocaltest;
                }
                if (i==1) {
                    ModelWeatherNOAA.Forecast.iconDataPeriod2 = iconlocaltest;
                }
                if (i==2) {
                    ModelWeatherNOAA.Forecast.iconDataPeriod3 = iconlocaltest;
                }
                if (i==3) {
                    ModelWeatherNOAA.Forecast.iconDataPeriod4 = iconlocaltest;
                }
                if (i==4) {
                    ModelWeatherNOAA.Forecast.iconDataPeriod5 = iconlocaltest;
                }
                if (i==5) {
                    ModelWeatherNOAA.Forecast.iconDataPeriod6 = iconlocaltest;
                }
                if (i==6) {
                    ModelWeatherNOAA.Forecast.iconDataPeriod7 = iconlocaltest;
                }
                if (i==7) {
                    ModelWeatherNOAA.Forecast.iconDataPeriod8 = iconlocaltest;
                }
                if (i==8) {
                    ModelWeatherNOAA.Forecast.iconDataPeriod9 = iconlocaltest;
                }
                if (i==9) {
                    ModelWeatherNOAA.Forecast.iconDataPeriod10 = iconlocaltest;
                }
                if (i==10) {
                    ModelWeatherNOAA.Forecast.iconDataPeriod11 = iconlocaltest;
                }
                if (i==11) {
                    ModelWeatherNOAA.Forecast.iconDataPeriod12 = iconlocaltest;
                }
                if (i==12) {
                    ModelWeatherNOAA.Forecast.iconDataPeriod13 = iconlocaltest;
                }
                if (i==13) {
                    ModelWeatherNOAA.Forecast.iconDataPeriod14 = iconlocaltest;
                }
            } else {
                //Get icon data
                Log.d("RemoteICONData", "Using remote icon");
                if (i==0) {
                    ModelWeatherNOAA.Forecast.iconDataPeriod1 = ((new HTTPClientNOAA()).getImage(ModelWeatherNOAA.Forecast.forecastIconURL[i]));
                    if (ModelWeatherNOAA.Forecast.iconDataPeriod1 == null){
                        Log.d("iconDataPeriod1", "Failed to download ICON Data! Halting execution");
                    } else {
                        Log.d("iconDataPeriod1", Arrays.toString(ModelWeatherNOAA.Forecast.iconDataPeriod1));
                    }
                }
                if (i==1) {
                    ModelWeatherNOAA.Forecast.iconDataPeriod2 = ((new HTTPClientNOAA()).getImage(ModelWeatherNOAA.Forecast.forecastIconURL[i]));
                    if (ModelWeatherNOAA.Forecast.iconDataPeriod2 == null){
                        Log.d("iconDataPeriod2", "Failed to download ICON Data! Halting execution");
                    } else {
                        Log.d("iconDataPeriod2", Arrays.toString(ModelWeatherNOAA.Forecast.iconDataPeriod2));
                    }
                }
                if (i==2) {
                    ModelWeatherNOAA.Forecast.iconDataPeriod3 = ((new HTTPClientNOAA()).getImage(ModelWeatherNOAA.Forecast.forecastIconURL[i]));
                    if (ModelWeatherNOAA.Forecast.iconDataPeriod3 == null){
                        Log.d("iconDataPeriod3", "Failed to download ICON Data! Halting execution");
                    } else {
                        Log.d("iconDataPeriod3", Arrays.toString(ModelWeatherNOAA.Forecast.iconDataPeriod3));
                    }
                }
                if (i==3) {
                    ModelWeatherNOAA.Forecast.iconDataPeriod4 = ((new HTTPClientNOAA()).getImage(ModelWeatherNOAA.Forecast.forecastIconURL[i]));
                    if (ModelWeatherNOAA.Forecast.iconDataPeriod4 == null){
                        Log.d("iconDataPeriod4", "Failed to download ICON Data! Halting execution");
                    } else {
                        Log.d("iconDataPeriod4", Arrays.toString(ModelWeatherNOAA.Forecast.iconDataPeriod4));
                    }
                }
                if (i==4) {
                    ModelWeatherNOAA.Forecast.iconDataPeriod5 = ((new HTTPClientNOAA()).getImage(ModelWeatherNOAA.Forecast.forecastIconURL[i]));
                    if (ModelWeatherNOAA.Forecast.iconDataPeriod5 == null){
                        Log.d("iconDataPeriod5", "Failed to download ICON Data! Halting execution");
                    } else {
                        Log.d("iconDataPeriod5", Arrays.toString(ModelWeatherNOAA.Forecast.iconDataPeriod5));
                    }
                }
                if (i==5) {
                    ModelWeatherNOAA.Forecast.iconDataPeriod6 = ((new HTTPClientNOAA()).getImage(ModelWeatherNOAA.Forecast.forecastIconURL[i]));
                    if (ModelWeatherNOAA.Forecast.iconDataPeriod6 == null){
                        Log.d("iconDataPeriod6", "Failed to download ICON Data! Halting execution");
                    } else {
                        Log.d("iconDataPeriod6", Arrays.toString(ModelWeatherNOAA.Forecast.iconDataPeriod6));
                    }
                }
                if (i==6) {
                    ModelWeatherNOAA.Forecast.iconDataPeriod7 = ((new HTTPClientNOAA()).getImage(ModelWeatherNOAA.Forecast.forecastIconURL[i]));
                    if (ModelWeatherNOAA.Forecast.iconDataPeriod7 == null){
                        Log.d("iconDataPeriod7", "Failed to download ICON Data! Halting execution");
                    } else {
                        Log.d("iconDataPeriod7", Arrays.toString(ModelWeatherNOAA.Forecast.iconDataPeriod7));
                    }
                }
                if (i==7) {
                    ModelWeatherNOAA.Forecast.iconDataPeriod8 = ((new HTTPClientNOAA()).getImage(ModelWeatherNOAA.Forecast.forecastIconURL[i]));
                    if (ModelWeatherNOAA.Forecast.iconDataPeriod8 == null){
                        Log.d("iconDataPeriod8", "Failed to download ICON Data! Halting execution");
                    } else {
                        Log.d("iconDataPeriod8", Arrays.toString(ModelWeatherNOAA.Forecast.iconDataPeriod8));
                    }
                }
                if (i==8) {
                    ModelWeatherNOAA.Forecast.iconDataPeriod9 = ((new HTTPClientNOAA()).getImage(ModelWeatherNOAA.Forecast.forecastIconURL[i]));
                    if (ModelWeatherNOAA.Forecast.iconDataPeriod9 == null){
                        Log.d("iconDataPeriod9", "Failed to download ICON Data! Halting execution");
                    } else {
                        Log.d("iconDataPeriod9", Arrays.toString(ModelWeatherNOAA.Forecast.iconDataPeriod9));
                    }
                }
                if (i==9) {
                    ModelWeatherNOAA.Forecast.iconDataPeriod10 = ((new HTTPClientNOAA()).getImage(ModelWeatherNOAA.Forecast.forecastIconURL[i]));
                    if (ModelWeatherNOAA.Forecast.iconDataPeriod10 == null){
                        Log.d("iconDataPeriod10", "Failed to download ICON Data! Halting execution");
                    } else {
                        Log.d("iconDataPeriod10", Arrays.toString(ModelWeatherNOAA.Forecast.iconDataPeriod10));
                    }
                }
                if (i==10) {
                    ModelWeatherNOAA.Forecast.iconDataPeriod11 = ((new HTTPClientNOAA()).getImage(ModelWeatherNOAA.Forecast.forecastIconURL[i]));
                    if (ModelWeatherNOAA.Forecast.iconDataPeriod11 == null){
                        Log.d("iconDataPeriod11", "Failed to download ICON Data! Halting execution");
                    } else {
                        Log.d("iconDataPeriod11", Arrays.toString(ModelWeatherNOAA.Forecast.iconDataPeriod11));
                    }
                }
                if (i==11) {
                    ModelWeatherNOAA.Forecast.iconDataPeriod12 = ((new HTTPClientNOAA()).getImage(ModelWeatherNOAA.Forecast.forecastIconURL[i]));
                    if (ModelWeatherNOAA.Forecast.iconDataPeriod12 == null){
                        Log.d("iconDataPeriod12", "Failed to download ICON Data! Halting execution");
                    } else {
                        Log.d("iconDataPeriod12", Arrays.toString(ModelWeatherNOAA.Forecast.iconDataPeriod12));
                    }
                }
                if (i==12) {
                    ModelWeatherNOAA.Forecast.iconDataPeriod13 = ((new HTTPClientNOAA()).getImage(ModelWeatherNOAA.Forecast.forecastIconURL[i]));
                    if (ModelWeatherNOAA.Forecast.iconDataPeriod13 == null){
                        Log.d("iconDataPeriod13", "Failed to download ICON Data! Halting execution");
                    } else {
                        Log.d("iconDataPeriod13", Arrays.toString(ModelWeatherNOAA.Forecast.iconDataPeriod13));
                    }
                }
                if (i==13) {
                    ModelWeatherNOAA.Forecast.iconDataPeriod14 = ((new HTTPClientNOAA()).getImage(ModelWeatherNOAA.Forecast.forecastIconURL[i]));
                    if (ModelWeatherNOAA.Forecast.iconDataPeriod14 == null){
                        Log.d("iconDataPeriod14", "Failed to download ICON Data! Halting execution");
                    } else {
                        Log.d("iconDataPeriod14", Arrays.toString(ModelWeatherNOAA.Forecast.iconDataPeriod14));
                    }
                }
            }
        }

        //update status
        Log.d("StatusUpdate","Sending Broadcast Event");
        ModelWeatherNOAA.WeatherPoints.UpdateStatus="Up to date";
        getAppContext().sendBroadcast(StatusIntent);

        //end
        return null;
    }

    @Override
    protected void onPostExecute(ModelWeatherNOAA weather) {
        super.onPostExecute(weather);
        MainActivitySnapWeather.RefreshWeatherDataOnLoad();
        MainFragmentSnapWeather.loadingSpin.setVisibility(View.GONE);
    }

}
