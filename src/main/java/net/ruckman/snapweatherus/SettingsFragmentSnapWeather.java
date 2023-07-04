package net.ruckman.snapweatherus;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import net.ruckman.snapweatherus.NOAAWeatherAPI.ModelWeatherNOAA;

public class SettingsFragmentSnapWeather extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.prefs, rootKey);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        SwitchPreferenceCompat settings_Service_Switch = (SwitchPreferenceCompat) findPreference("settings_Service_Switch");
        SwitchPreferenceCompat settings_Extreme_Switch = (SwitchPreferenceCompat) findPreference("settings_Extreme_Switch");
        SwitchPreferenceCompat settings_Severe_Switch = (SwitchPreferenceCompat) findPreference("settings_Severe_Switch");
        SwitchPreferenceCompat settings_Moderate_Switch = (SwitchPreferenceCompat) findPreference("settings_Moderate_Switch");
        SwitchPreferenceCompat settings_Minor_Switch = (SwitchPreferenceCompat) findPreference("settings_Minor_Switch");
        SwitchPreferenceCompat settings_Unknown_Switch = (SwitchPreferenceCompat) findPreference("settings_Unknown_Switch");
        SwitchPreferenceCompat settings_Radar_L0 = (SwitchPreferenceCompat) findPreference("settings_Radar_L0");
        SwitchPreferenceCompat settings_Radar_L2 = (SwitchPreferenceCompat) findPreference("settings_Radar_L2");
        SwitchPreferenceCompat settings_Radar_L3 = (SwitchPreferenceCompat) findPreference("settings_Radar_L3");
        SwitchPreferenceCompat settings_Radar_L4 = (SwitchPreferenceCompat) findPreference("settings_Radar_L4");
        SwitchPreferenceCompat settings_Radar_L5 = (SwitchPreferenceCompat) findPreference("settings_Radar_L5");
        SwitchPreferenceCompat settings_Radar_L6 = (SwitchPreferenceCompat) findPreference("settings_Radar_L6");
        SwitchPreferenceCompat settings_Radar_L7 = (SwitchPreferenceCompat) findPreference("settings_Radar_L7");

        if (ModelWeatherNOAA.Settings.ServiceNotifications != null) {
            settings_Service_Switch.setChecked(ModelWeatherNOAA.Settings.ServiceNotifications);
        }
        if (ModelWeatherNOAA.Settings.SeverityExtreme != null) {
            settings_Extreme_Switch.setChecked(ModelWeatherNOAA.Settings.SeverityExtreme);
        }
        if (ModelWeatherNOAA.Settings.SeveritySevere != null) {
            settings_Severe_Switch.setChecked(ModelWeatherNOAA.Settings.SeveritySevere);
        }
        if (ModelWeatherNOAA.Settings.SeverityModerate != null) {
            settings_Moderate_Switch.setChecked(ModelWeatherNOAA.Settings.SeverityModerate);
        }
        if (ModelWeatherNOAA.Settings.SeverityMinor != null) {
            settings_Minor_Switch.setChecked(ModelWeatherNOAA.Settings.SeverityMinor);
        }
        if (ModelWeatherNOAA.Settings.SeverityUnknown != null) {
            settings_Unknown_Switch.setChecked(ModelWeatherNOAA.Settings.SeverityUnknown);
        }
        if (ModelWeatherNOAA.Settings.RadarL0 != null) {
            settings_Radar_L0.setChecked(ModelWeatherNOAA.Settings.RadarL0);
        }
        if (ModelWeatherNOAA.Settings.RadarL2 != null) {
            settings_Radar_L2.setChecked(ModelWeatherNOAA.Settings.RadarL2);
        }
        if (ModelWeatherNOAA.Settings.RadarL3 != null) {
            settings_Radar_L3.setChecked(ModelWeatherNOAA.Settings.RadarL3);
        }
        if (ModelWeatherNOAA.Settings.RadarL4 != null) {
            settings_Radar_L4.setChecked(ModelWeatherNOAA.Settings.RadarL4);
        }
        if (ModelWeatherNOAA.Settings.RadarL5 != null) {
            settings_Radar_L5.setChecked(ModelWeatherNOAA.Settings.RadarL5);
        }
        if (ModelWeatherNOAA.Settings.RadarL6 != null) {
            settings_Radar_L6.setChecked(ModelWeatherNOAA.Settings.RadarL6);
        }
        if (ModelWeatherNOAA.Settings.RadarL7 != null) {
            settings_Radar_L7.setChecked(ModelWeatherNOAA.Settings.RadarL7);
        }

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Log.d("SettingsChangedKey",s);

        if (s.equals("settings_Service_Switch")) {
            SwitchPreferenceCompat settings_Service_Switch = (SwitchPreferenceCompat) findPreference(s);
            SharedPreferences settings_switch = settings_Service_Switch.getSharedPreferences();
            Boolean settingsservice = settings_switch.getBoolean(s,true);
            Log.d("SettingsValue", String.valueOf(settingsservice));
            ModelWeatherNOAA.Settings.ServiceNotifications=settingsservice;
        }
        if (s.equals("settings_Extreme_Switch")) {
            SwitchPreferenceCompat settings_Extreme_Switch = (SwitchPreferenceCompat) findPreference(s);
            SharedPreferences settings_switch = settings_Extreme_Switch.getSharedPreferences();
            Boolean settingsservice = settings_switch.getBoolean(s,true);
            Log.d("SettingsValue", String.valueOf(settingsservice));
            ModelWeatherNOAA.Settings.SeverityExtreme=settingsservice;
        }
        if (s.equals("settings_Severe_Switch")) {
            SwitchPreferenceCompat settings_Severe_Switch = (SwitchPreferenceCompat) findPreference(s);
            SharedPreferences settings_switch = settings_Severe_Switch.getSharedPreferences();
            Boolean settingsservice = settings_switch.getBoolean(s,true);
            Log.d("SettingsValue", String.valueOf(settingsservice));
            ModelWeatherNOAA.Settings.SeveritySevere=settingsservice;
        }
        if (s.equals("settings_Moderate_Switch")) {
            SwitchPreferenceCompat settings_Moderate_Switch = (SwitchPreferenceCompat) findPreference(s);
            SharedPreferences settings_switch = settings_Moderate_Switch.getSharedPreferences();
            Boolean settingsservice = settings_switch.getBoolean(s,true);
            Log.d("SettingsValue", String.valueOf(settingsservice));
            ModelWeatherNOAA.Settings.SeverityModerate=settingsservice;
        }
        if (s.equals("settings_Minor_Switch")) {
            SwitchPreferenceCompat settings_Minor_Switch = (SwitchPreferenceCompat) findPreference(s);
            SharedPreferences settings_switch = settings_Minor_Switch.getSharedPreferences();
            Boolean settingsservice = settings_switch.getBoolean(s,true);
            Log.d("SettingsValue", String.valueOf(settingsservice));
            ModelWeatherNOAA.Settings.SeverityMinor=settingsservice;
        }
        if (s.equals("settings_Unknown_Switch")) {
            SwitchPreferenceCompat settings_Unknown_Switch = (SwitchPreferenceCompat) findPreference(s);
            SharedPreferences settings_switch = settings_Unknown_Switch.getSharedPreferences();
            Boolean settingsservice = settings_switch.getBoolean(s,true);
            Log.d("SettingsValue", String.valueOf(settingsservice));
            ModelWeatherNOAA.Settings.SeverityUnknown=settingsservice;
        }
        if (s.equals("settings_Radar_L0")) {
            SwitchPreferenceCompat settings_Radar_L0 = (SwitchPreferenceCompat) findPreference(s);
            SharedPreferences settings_switch = settings_Radar_L0.getSharedPreferences();
            Boolean settingsservice = settings_switch.getBoolean(s,false);
            Log.d("SettingsValL0", String.valueOf(settingsservice));
            ModelWeatherNOAA.Settings.RadarL0=settingsservice;
        }
        if (s.equals("settings_Radar_L2")) {
            SwitchPreferenceCompat settings_Radar_L2 = (SwitchPreferenceCompat) findPreference(s);
            SharedPreferences settings_switch = settings_Radar_L2.getSharedPreferences();
            Boolean settingsservice = settings_switch.getBoolean(s,true);
            Log.d("SettingsValL2", String.valueOf(settingsservice));
            ModelWeatherNOAA.Settings.RadarL2=settingsservice;
        }
        if (s.equals("settings_Radar_L3")) {
            SwitchPreferenceCompat settings_Radar_L3 = (SwitchPreferenceCompat) findPreference(s);
            SharedPreferences settings_switch = settings_Radar_L3.getSharedPreferences();
            Boolean settingsservice = settings_switch.getBoolean(s,false);
            Log.d("SettingsValL3", String.valueOf(settingsservice));
            ModelWeatherNOAA.Settings.RadarL3=settingsservice;
        }
        if (s.equals("settings_Radar_L4")) {
            SwitchPreferenceCompat settings_Radar_L4 = (SwitchPreferenceCompat) findPreference(s);
            SharedPreferences settings_switch = settings_Radar_L4.getSharedPreferences();
            Boolean settingsservice = settings_switch.getBoolean(s,true);
            Log.d("SettingsValL4", String.valueOf(settingsservice));
            ModelWeatherNOAA.Settings.RadarL4=settingsservice;
        }
        if (s.equals("settings_Radar_L5")) {
            SwitchPreferenceCompat settings_Radar_L5 = (SwitchPreferenceCompat) findPreference(s);
            SharedPreferences settings_switch = settings_Radar_L5.getSharedPreferences();
            Boolean settingsservice = settings_switch.getBoolean(s,true);
            Log.d("SettingsValL5", String.valueOf(settingsservice));
            ModelWeatherNOAA.Settings.RadarL5=settingsservice;
        }
        if (s.equals("settings_Radar_L6")) {
            SwitchPreferenceCompat settings_Radar_L6 = (SwitchPreferenceCompat) findPreference(s);
            SharedPreferences settings_switch = settings_Radar_L6.getSharedPreferences();
            Boolean settingsservice = settings_switch.getBoolean(s,true);
            Log.d("SettingsValL6", String.valueOf(settingsservice));
            ModelWeatherNOAA.Settings.RadarL6=settingsservice;
        }
        if (s.equals("settings_Radar_L7")) {
            SwitchPreferenceCompat settings_Radar_L7 = (SwitchPreferenceCompat) findPreference(s);
            SharedPreferences settings_switch = settings_Radar_L7.getSharedPreferences();
            Boolean settingsservice = settings_switch.getBoolean(s,true);
            Log.d("SettingsValL7", String.valueOf(settingsservice));
            ModelWeatherNOAA.Settings.RadarL7=settingsservice;
        }

    }
}

