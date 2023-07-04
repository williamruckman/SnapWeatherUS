package net.ruckman.snapweatherus.NOAAWeatherAPI;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class JSONParserNOAA {
    public static void getWeatherPoints(String data) throws JSONException {

        // We create out JSONObject from the data
        JSONObject jObj = new JSONObject(data);

        // Get the main weatherpoints information
        // We get weather info (This is an array)
        if (jObj.has("geometry")) {
            JSONObject JSONGeometry = jObj.getJSONObject("geometry");
            if (JSONGeometry.has("coordinates")) {
                JSONArray jArrCoordinates = JSONGeometry.getJSONArray("coordinates");

                //looping through coordinates (points)
                for (int i = 0; i < jArrCoordinates.length(); i++) {
                    Float point = Float.valueOf(jArrCoordinates.getString(i));
                    if (i == 0) {
                        ModelWeatherNOAA.WeatherPoints.point1 = point;
                        Log.d("point1", String.valueOf(point));
                    }
                    if (i == 1) {
                        ModelWeatherNOAA.WeatherPoints.point2 = point;
                        Log.d("point2", String.valueOf(point));
                    }

                }

            }

        }

        if (jObj.has("properties")) {
            JSONObject JSONProperties = jObj.getJSONObject("properties");
            if (JSONProperties.has("cwa")) {
                String office = JSONProperties.getString("cwa");
                Log.d("office", office);
                ModelWeatherNOAA.WeatherPoints.office = office;
            }
            if (JSONProperties.has("gridX")) {
                String gridx = JSONProperties.getString("gridX");
                Log.d("gridx", gridx);
                ModelWeatherNOAA.WeatherPoints.gridx = gridx;
            }
            if (JSONProperties.has("gridY")) {
                String gridy = JSONProperties.getString("gridY");
                Log.d("gridy", gridy);
                ModelWeatherNOAA.WeatherPoints.gridy = gridy;
            }
            if (JSONProperties.has("forecast")) {
                String forecast = JSONProperties.getString("forecast");
                Log.d("forecastGridURL", forecast);
                ModelWeatherNOAA.WeatherPoints.forecastGridURL = forecast;
            }
            if (JSONProperties.has("radarStation")) {
                String radar = JSONProperties.getString("radarStation");
                Log.d("radarStation", radar);
                ModelWeatherNOAA.WeatherPoints.radarstation = radar;
            }
            //WAS forecastZone, but changed to county as weather alerts were unreliable (Uses county information instead)
            if (JSONProperties.has("county")) {
                String forcastzone = JSONProperties.getString("county");
                Log.d("county", forcastzone);
                ModelWeatherNOAA.WeatherPoints.forcastzoneurl = forcastzone;
            }
            if (JSONProperties.has("relativeLocation")) {
                JSONObject JSONRelativeLocation = JSONProperties.getJSONObject("relativeLocation");
                if (JSONRelativeLocation.has("properties")) {
                    JSONObject JSONLocationProperties = JSONRelativeLocation.getJSONObject("properties");
                    if (JSONLocationProperties.has("city")) {
                        String city = JSONLocationProperties.getString("city");
                        Log.d("City",city);
                        ModelWeatherNOAA.WeatherPoints.city=city;
                    }
                    if (JSONLocationProperties.has("state")) {
                        String state = JSONLocationProperties.getString("state");
                        Log.d("State",state);
                        ModelWeatherNOAA.WeatherPoints.state=state;
                    }
                }
            }
        }
    }

    static void getWeatherStations(String data) throws JSONException {

        // We create out JSONObject from the data
        JSONObject jObj = new JSONObject(data);
        if (jObj.has("features")) {
            JSONArray JSONFeaturesArr = jObj.getJSONArray("features");

            //looping through stations array (offices)
            for (int i = 0; i < 1; i++) {
                JSONObject JSONOffice = JSONFeaturesArr.getJSONObject(i);
                if (JSONOffice.has("properties")) {
                    JSONObject JSONOfficeProperties = JSONOffice.getJSONObject("properties");
                    if (JSONOfficeProperties.has("stationIdentifier")) {
                            String identifier = JSONOfficeProperties.getString("stationIdentifier");
                        Log.d("StationIdentifier", identifier);
                        ModelWeatherNOAA.WeatherPoints.stationidentifier1 = identifier;
                    }
                    if (JSONOfficeProperties.has("name")) {
                        String stationname = JSONOfficeProperties.getString("name");
                        Log.d("name", stationname);
                        ModelWeatherNOAA.WeatherPoints.stationname1 = stationname;
                    }
                }

            }

        }
    }

    static void getCurrentConditions(String data) throws JSONException {
        // We create out JSONObject from the data
        JSONObject jObj = new JSONObject(data);
        if (jObj.has("properties")) {
            JSONObject JSONCurrentConditionProperties = jObj.getJSONObject("properties");
            if (JSONCurrentConditionProperties.has("textDescription")){
                String TextDescription = JSONCurrentConditionProperties.getString("textDescription");
                Log.d("TextDescription", TextDescription);
                ModelWeatherNOAA.CurrentCondition.descr = TextDescription;
            }
            if (JSONCurrentConditionProperties.has("icon")){
                String icon = JSONCurrentConditionProperties.getString("icon");
                Log.d("IconString", icon);
                ModelWeatherNOAA.CurrentCondition.icon = icon;
            }
            if (JSONCurrentConditionProperties.has("elevation")){
                JSONObject JSONElevationData = JSONCurrentConditionProperties.getJSONObject("elevation");
                if (JSONElevationData.has("value")){
                    String elevationvalue = JSONElevationData.getString("value");
                    Log.d("ElevationValue", elevationvalue);
                    ModelWeatherNOAA.CurrentCondition.elevation = elevationvalue;
                }
            }
            if (JSONCurrentConditionProperties.has("temperature")){
                JSONObject JSONTemperatureData = JSONCurrentConditionProperties.getJSONObject("temperature");
                if (JSONTemperatureData.has("value")){
                    String temperaturevalue = JSONTemperatureData.getString("value");
                    Log.d("TempratureValue", temperaturevalue);
                    ModelWeatherNOAA.CurrentCondition.temperature = temperaturevalue;
                }
            }
            if (JSONCurrentConditionProperties.has("dewpoint")){
                JSONObject JSONDewpointData = JSONCurrentConditionProperties.getJSONObject("dewpoint");
                if (JSONDewpointData.has("value")){
                    String dewpointvalue = JSONDewpointData.getString("value");
                    Log.d("DewpointValue", dewpointvalue);
                    ModelWeatherNOAA.CurrentCondition.dewpoint = dewpointvalue;
                }
            }
            if (JSONCurrentConditionProperties.has("windDirection")){
                JSONObject JSONWindDirectionData = JSONCurrentConditionProperties.getJSONObject("windDirection");
                if (JSONWindDirectionData.has("value")){
                    String winddirectionvalue = JSONWindDirectionData.getString("value");
                    Log.d("WindDirectionValue", winddirectionvalue);
                    ModelWeatherNOAA.CurrentCondition.winddirection = winddirectionvalue;
                }
            }
            if (JSONCurrentConditionProperties.has("windSpeed")){
                JSONObject JSONWindSpeedData = JSONCurrentConditionProperties.getJSONObject("windSpeed");
                if (JSONWindSpeedData.has("value")){
                    String windspeedvalue = JSONWindSpeedData.getString("value");
                    Log.d("WindSpeedValue", windspeedvalue);
                    ModelWeatherNOAA.CurrentCondition.windspeed = windspeedvalue;
                }
            }
            if (JSONCurrentConditionProperties.has("windGust")){
                JSONObject JSONWindGustData = JSONCurrentConditionProperties.getJSONObject("windGust");
                if (JSONWindGustData.has("value")){
                    String windgustvalue = JSONWindGustData.getString("value");
                    Log.d("WindGustsValue", windgustvalue);
                    ModelWeatherNOAA.CurrentCondition.windgusts = windgustvalue;
                }
            }
            if (JSONCurrentConditionProperties.has("relativeHumidity")){
                JSONObject JSONHumidityData = JSONCurrentConditionProperties.getJSONObject("relativeHumidity");
                if (JSONHumidityData.has("value")){
                    String humidityvalue = JSONHumidityData.getString("value");
                    Log.d("HumidityValue", humidityvalue);
                    ModelWeatherNOAA.CurrentCondition.humidity = humidityvalue;
                }
            }
            if (JSONCurrentConditionProperties.has("heatIndex")){
                JSONObject JSONHeatIndexData = JSONCurrentConditionProperties.getJSONObject("heatIndex");
                if (JSONHeatIndexData.has("value")){
                    String heatindexvalue = JSONHeatIndexData.getString("value");
                    Log.d("HeatIndexValue", heatindexvalue);
                    ModelWeatherNOAA.CurrentCondition.heatindex = heatindexvalue;
                }
            }
            if (JSONCurrentConditionProperties.has("barometricPressure")){
                JSONObject JSONbarometricPressureData = JSONCurrentConditionProperties.getJSONObject("barometricPressure");
                if (JSONbarometricPressureData.has("value")){
                    String barometricPressure = JSONbarometricPressureData.getString("value");
                    Log.d("barometricPressure", barometricPressure);
                    ModelWeatherNOAA.CurrentCondition.pressure = barometricPressure;
                }
            }
            if (JSONCurrentConditionProperties.has("visibility")){
                JSONObject JSONvisibilityData = JSONCurrentConditionProperties.getJSONObject("visibility");
                if (JSONvisibilityData.has("value")){
                    String visibility = JSONvisibilityData.getString("value");
                    Log.d("visibility", visibility);
                    ModelWeatherNOAA.CurrentCondition.visibility = visibility;
                }
            }


        }


    }

    public static void getForcastZone(String data) throws JSONException {

        // We create out JSONObject from the data
        JSONObject jObj = new JSONObject(data);
        if (jObj.has("properties")) {
            JSONObject JSONProperiesObject = jObj.getJSONObject("properties");
            if (JSONProperiesObject.has("id")) {
                String zoneid = JSONProperiesObject.getString("id");
                Log.d("zoneid", zoneid);
                ModelWeatherNOAA.WeatherPoints.forcastzoneid=zoneid;
            }
            if (JSONProperiesObject.has("name")) {
                String county = JSONProperiesObject.getString("name");
                Log.d("county", county);
                ModelWeatherNOAA.WeatherPoints.county=county;
            }
        }
    }

    public static void getWeatherAlerts(String data) throws JSONException {

        // We create out JSONObject from the data
        JSONObject jObj = new JSONObject(data);
        if (jObj.has("features")) {
            JSONArray JSONFeaturesArr = jObj.getJSONArray("features");

            Log.d("AlertArrayLength", String.valueOf(JSONFeaturesArr.length()));
            //Must Initialize Arrays
            ModelWeatherNOAA.CurrentAlerts.effective = new String[JSONFeaturesArr.length()];
            ModelWeatherNOAA.CurrentAlerts.expires = new String[JSONFeaturesArr.length()];
            ModelWeatherNOAA.CurrentAlerts.event = new String[JSONFeaturesArr.length()];
            ModelWeatherNOAA.CurrentAlerts.headline = new String[JSONFeaturesArr.length()];
            ModelWeatherNOAA.CurrentAlerts.description = new String[JSONFeaturesArr.length()];
            ModelWeatherNOAA.CurrentAlerts.severity = new String[JSONFeaturesArr.length()];
            ModelWeatherNOAA.CurrentAlerts.instruction = new String[JSONFeaturesArr.length()];

            //looping through alerts feature array (Weather Alerts)
            for (int i = 0; i < JSONFeaturesArr.length(); i++) {
                    JSONObject JSONAlerts = JSONFeaturesArr.getJSONObject(i);
                    if (JSONAlerts.has("properties")) {
                        JSONObject JSONAlertsProperties = JSONAlerts.getJSONObject("properties");
                        if (JSONAlertsProperties.has("effective")) {
                            String effective = JSONAlertsProperties.getString("effective");
                            Log.d("effective", effective);
                            ModelWeatherNOAA.CurrentAlerts.effective[i] = effective;
                        }
                        if (JSONAlertsProperties.has("expires")) {
                            String expires = JSONAlertsProperties.getString("expires");
                            Log.d("expires", expires);
                            ModelWeatherNOAA.CurrentAlerts.expires[i] = expires;
                        }
                        if (JSONAlertsProperties.has("event")) {
                            String event = JSONAlertsProperties.getString("event");
                            Log.d("event", event);
                            ModelWeatherNOAA.CurrentAlerts.event[i] = event;
                        }
                        if (JSONAlertsProperties.has("headline")) {
                            String headline = JSONAlertsProperties.getString("headline");
                            Log.d("headline", headline);
                            ModelWeatherNOAA.CurrentAlerts.headline[i] = headline;
                        }
                        if (JSONAlertsProperties.has("description")) {
                            String description = JSONAlertsProperties.getString("description");
                            Log.d("description", description);
                            ModelWeatherNOAA.CurrentAlerts.description[i] = description;
                        }
                        if (JSONAlertsProperties.has("severity")) {
                            String severity = JSONAlertsProperties.getString("severity");
                            Log.d("severity", severity);
                            ModelWeatherNOAA.CurrentAlerts.severity[i] = severity;
                        }
                        if (JSONAlertsProperties.has("instruction")) {
                            String instruction = JSONAlertsProperties.getString("instruction");
                            Log.d("instruction", instruction);
                            ModelWeatherNOAA.CurrentAlerts.instruction[i] = instruction;
                        }
                    }
            }


        }
    }

    public static void getForecastData(String data) throws JSONException {

        // We create out JSONObject from the data
        Log.d("Forecast", "Processing JSON extended detailed forecast into string arrays");
        JSONObject jObj = new JSONObject(data);
        if (jObj.has("properties")) {
            JSONObject JSONForecastProperties = jObj.getJSONObject("properties");
            if (JSONForecastProperties.has("periods")){
                JSONArray JSONForecastArr = JSONForecastProperties.getJSONArray("periods");
                Log.d("ForecastArrayLength", String.valueOf(JSONForecastArr.length()));
                //Must Initialize Arrays
                ModelWeatherNOAA.Forecast.forecastName = new String[JSONForecastArr.length()];
                ModelWeatherNOAA.Forecast.forecastTemp = new String[JSONForecastArr.length()];
                ModelWeatherNOAA.Forecast.forecastWindspeed = new String[JSONForecastArr.length()];
                ModelWeatherNOAA.Forecast.forecastWinddirection = new String[JSONForecastArr.length()];
                ModelWeatherNOAA.Forecast.forecastIconURL = new String[JSONForecastArr.length()];
                ModelWeatherNOAA.Forecast.forecastShort = new String[JSONForecastArr.length()];
                ModelWeatherNOAA.Forecast.forecastDetailed = new String[JSONForecastArr.length()];

                //looping through forecast array (Weather Forecast)
                for (int i = 0; i < JSONForecastArr.length(); i++) {
                    JSONObject JSONForecast = JSONForecastArr.getJSONObject(i);
                    if (JSONForecast.has("name")) {
                        String name = JSONForecast.getString("name");
                        Log.d("name", name);
                        ModelWeatherNOAA.Forecast.forecastName[i] = name;
                    }
                    if (JSONForecast.has("temperature")) {
                        String temperature = JSONForecast.getString("temperature");
                        Log.d("temperature", temperature);
                        ModelWeatherNOAA.Forecast.forecastTemp[i] = temperature;
                    }
                    if (JSONForecast.has("windSpeed")) {
                        String windSpeed = JSONForecast.getString("windSpeed");
                        Log.d("windSpeed", windSpeed);
                        ModelWeatherNOAA.Forecast.forecastWindspeed[i] = windSpeed;
                    }
                    if (JSONForecast.has("windDirection")) {
                        String windDirection = JSONForecast.getString("windDirection");
                        Log.d("windDirection", windDirection);
                        ModelWeatherNOAA.Forecast.forecastWinddirection[i] = windDirection;
                    }
                    if (JSONForecast.has("icon")) {
                        String icon = JSONForecast.getString("icon");
                        Log.d("icon", icon);
                        ModelWeatherNOAA.Forecast.forecastIconURL[i] = icon;
                    }
                    if (JSONForecast.has("shortForecast")) {
                        String shortForecast = JSONForecast.getString("shortForecast");
                        Log.d("shortForecast", shortForecast);
                        ModelWeatherNOAA.Forecast.forecastShort[i] = shortForecast;
                    }
                    if (JSONForecast.has("detailedForecast")) {
                        String detailedForecast = JSONForecast.getString("detailedForecast");
                        Log.d("detailedForecast", detailedForecast);
                        ModelWeatherNOAA.Forecast.forecastDetailed[i] = detailedForecast;
                    }
                }
            }
        }
    }

//end
}

