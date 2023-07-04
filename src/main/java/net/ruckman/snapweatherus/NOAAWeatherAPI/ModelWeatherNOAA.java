package net.ruckman.snapweatherus.NOAAWeatherAPI;

public class ModelWeatherNOAA {

    public static class WeatherPoints {
        public static Boolean firstrun;
        public static String UID;
        public static String userAgent = "SnapWeatherUS 2.12 william@ruckman.net";
        public static String longitude;
        public static String latitude;
        public static String lastlocationgettime;
        public static Float point1;
        public static Float point2;
        public static String office;
        public static String stationidentifier1;
        public static String stationname1;
        public static String gridx;
        public static String gridy;
        public static String radarstation;
        public static String city;
        public static String state;
        public static String forcastzoneurl;
        public static String forcastzoneid;
        public static String forecastGridURL;
        public static String county;
        public static String LastLocationUpdate;
        public static String LastCheckFailedWeatherPoints;
        public static String LastCheckFailedStationData;
        public static String UpdateStatus;
        public static Boolean lastStatusFailedNotComplete;
    }

    public static class CurrentCondition {
        public static String dewpoint;
        public static String descr;
        public static String icon;
        public static byte[] iconData;
        public static String temperature;
        public static String humidity;
        public static String windspeed;
        public static String windgusts;
        public static String winddirection;
        public static String elevation;
        public static String heatindex;
        public static String pressure;
        public static String visibility;
        public static String LastWeatherUpdate;
    }

    public static class Forecast {
        public static String LastForecastUpdate;
        public static String LastCheckFailedForecast;
        public static String[] forecastName;
        public static String[] forecastTemp;
        public static String[] forecastWindspeed;
        public static String[] forecastWinddirection;
        public static String[] forecastIconURL;
        public static String[] forecastShort;
        public static String[] forecastDetailed;
        public static byte[] iconDataPeriod1;
        public static byte[] iconDataPeriod2;
        public static byte[] iconDataPeriod3;
        public static byte[] iconDataPeriod4;
        public static byte[] iconDataPeriod5;
        public static byte[] iconDataPeriod6;
        public static byte[] iconDataPeriod7;
        public static byte[] iconDataPeriod8;
        public static byte[] iconDataPeriod9;
        public static byte[] iconDataPeriod10;
        public static byte[] iconDataPeriod11;
        public static byte[] iconDataPeriod12;
        public static byte[] iconDataPeriod13;
        public static byte[] iconDataPeriod14;
    }

    public static class CurrentAlerts {
        public static String[] effective;
        public static String[] expires;
        public static String[] event;
        public static String[] headline;
        public static String[] description;
        public static String[] instruction;
        public static String[] severity;
        public static String ALERT_NOTIFICATION_CHANNEL_ID="SnapWeatherUS Main Alert";
        public static String SERVICE_NOTIFICATION_CHANNEL_ID="SnapWeatherUS Service Notification";
        public static Boolean AlertONOFF = false;
        public static String compareevent;
        public static String LastAlertsUpdate;
    }

    public static class RadarImagesCache {
        public static String RadarImageSiteName;
        public static byte[] RadarImageArrayL0;
        public static byte[] RadarImageArrayL1;
        public static byte[] RadarImageArrayL2;
        public static byte[] RadarImageArrayL3;
        public static byte[] RadarImageArrayL4;
        public static byte[] RadarImageArrayL5;
        public static byte[] RadarImageArrayL6;
        public static byte[] RadarImageArrayL7;

        public static byte[] AnimatedRadarImageArrayM1;
        public static byte[] AnimatedRadarImageArrayM2;
        public static byte[] AnimatedRadarImageArrayM3;
        public static byte[] AnimatedRadarImageArrayM4;
        public static byte[] AnimatedRadarImageArrayM5;
        public static byte[] AnimatedRadarImageArrayM6;
    }

    public static class Settings {
        public static Boolean ServiceNotifications;
        public static Boolean SeverityExtreme;
        public static Boolean SeveritySevere;
        public static Boolean SeverityModerate;
        public static Boolean SeverityMinor;
        public static Boolean SeverityUnknown;
        public static Boolean RadarL0;
        public static Boolean RadarL2;
        public static Boolean RadarL3;
        public static Boolean RadarL4;
        public static Boolean RadarL5;
        public static Boolean RadarL6;
        public static Boolean RadarL7;
    }

}