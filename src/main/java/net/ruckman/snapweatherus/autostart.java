package net.ruckman.snapweatherus;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import net.ruckman.snapweatherus.NOAAWeatherAPI.ModelWeatherNOAA;

public class autostart extends BroadcastReceiver
{
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    public void onReceive(Context context, Intent arg1)
    {
        String version;
        if (arg1.getAction().equals("android.intent.action.MY_PACKAGE_REPLACED")) {
            Log.i("Service", "Package Replaced");

            try {
                PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                version = pInfo.versionName;
                if (version.equals("1.04") ) {
                    Log.i("Service", "Update Data Model");
                    UpdateDataModel104.LoadDataModel(context);
                    MainActivitySnapWeather.SaveDataModel(context);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            MainActivitySnapWeather.LoadDataModel(context);
            if (ModelWeatherNOAA.Settings.ServiceNotifications) {
                Log.i("Service", "Enabled, Starting...");
                Intent intent = new Intent(context,ForegroundServiceNOAA.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent);
                } else {
                    context.startService(intent);
                }
            } else {
                Log.i("Service", "Disabled");
            }

            Log.i("Autostart", "finished");
        }

        if (arg1.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.i("Service", "Boot Completed");

            MainActivitySnapWeather.LoadDataModel(context);
            if (ModelWeatherNOAA.Settings.ServiceNotifications) {
                Log.i("Service", "Enabled, Starting...");
                Intent intent = new Intent(context,ForegroundServiceNOAA.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent);
                } else {
                    context.startService(intent);
                }
            } else {
                Log.i("Service", "Disabled");
            }

            Log.i("Autostart", "finished");

        }

    }
}
