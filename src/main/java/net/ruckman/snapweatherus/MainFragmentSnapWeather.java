package net.ruckman.snapweatherus;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.ruckman.snapweatherus.NOAAWeatherAPI.JSONWeatherTaskNOAA;
import net.ruckman.snapweatherus.NOAAWeatherAPI.ModelWeatherNOAA;

import static android.view.View.VISIBLE;
import static net.ruckman.snapweatherus.MainActivitySnapWeather.LATITUDE;
import static net.ruckman.snapweatherus.MainActivitySnapWeather.LONGITUDE;

public class MainFragmentSnapWeather extends Fragment {

    public static TextView cityText;
    public static TextView condDescr;
    public static TextView temp;
    public static TextView feelslikelab;
    public static TextView feelslike;
    public static ImageView heatcoldWarningimgView;
    public static TextView press;
    public static TextView windSpeed;
    public static TextView windDeg;
    public static TextView hum;
    public static ImageView imgView;
    public static ImageView compassView;
    public static RelativeLayout AlertView;
    public static RelativeLayout currentWeatherLayout;
    public static TextView AlertList;
    public static TextView observationStation;
    public static TextView lastUpdated;
    public static Matrix matrix = new Matrix();
    public static Bitmap compass;
    public static Bitmap img;
    public static final String ModelWeatherNOAA_PREFERENCES = "ModelWeatherNOAA";
    public static ProgressBar loadingSpin;
    public static Bitmap heatcoldwarningimg;

    public static TextView extendedForecastlastupdated;
    public static RelativeLayout extendedForecastLayoutMain;
    public static RelativeLayout extendedForecastRelativeLayout1;
    public static RelativeLayout extendedForecastRelativeLayout2;
    public static RelativeLayout extendedForecastRelativeLayout3;
    public static RelativeLayout extendedForecastRelativeLayout4;
    public static RelativeLayout extendedForecastRelativeLayout5;
    public static RelativeLayout extendedForecastRelativeLayout6;
    public static RelativeLayout extendedForecastRelativeLayout7;
    public static RelativeLayout extendedForecastRelativeLayout8;
    public static RelativeLayout extendedForecastRelativeLayout9;
    public static RelativeLayout extendedForecastRelativeLayout10;
    public static RelativeLayout extendedForecastRelativeLayout11;
    public static RelativeLayout extendedForecastRelativeLayout12;
    public static RelativeLayout extendedForecastRelativeLayout13;
    public static RelativeLayout extendedForecastRelativeLayout14;
    public static TextView extendedForcastDay1;
    public static ImageView extendedForecastIcon1;
    public static TextView extendedForecastShortDesc1;
    public static TextView extendedForecasttemp1;
    public static TextView extendedForecastwindSpeed1;
    public static TextView extendedForecastwindDirection1;
    public static TextView extendedForecastDetailed1;
    public static TextView extendedForcastDay2;
    public static ImageView extendedForecastIcon2;
    public static TextView extendedForecastShortDesc2;
    public static TextView extendedForecasttemp2;
    public static TextView extendedForecastwindSpeed2;
    public static TextView extendedForecastwindDirection2;
    public static TextView extendedForecastDetailed2;
    public static TextView extendedForcastDay3;
    public static ImageView extendedForecastIcon3;
    public static TextView extendedForecastShortDesc3;
    public static TextView extendedForecasttemp3;
    public static TextView extendedForecastwindSpeed3;
    public static TextView extendedForecastwindDirection3;
    public static TextView extendedForecastDetailed3;
    public static TextView extendedForcastDay4;
    public static ImageView extendedForecastIcon4;
    public static TextView extendedForecastShortDesc4;
    public static TextView extendedForecasttemp4;
    public static TextView extendedForecastwindSpeed4;
    public static TextView extendedForecastwindDirection4;
    public static TextView extendedForecastDetailed4;
    public static TextView extendedForcastDay5;
    public static ImageView extendedForecastIcon5;
    public static TextView extendedForecastShortDesc5;
    public static TextView extendedForecasttemp5;
    public static TextView extendedForecastwindSpeed5;
    public static TextView extendedForecastwindDirection5;
    public static TextView extendedForecastDetailed5;
    public static TextView extendedForcastDay6;
    public static ImageView extendedForecastIcon6;
    public static TextView extendedForecastShortDesc6;
    public static TextView extendedForecasttemp6;
    public static TextView extendedForecastwindSpeed6;
    public static TextView extendedForecastwindDirection6;
    public static TextView extendedForecastDetailed6;
    public static TextView extendedForcastDay7;
    public static ImageView extendedForecastIcon7;
    public static TextView extendedForecastShortDesc7;
    public static TextView extendedForecasttemp7;
    public static TextView extendedForecastwindSpeed7;
    public static TextView extendedForecastwindDirection7;
    public static TextView extendedForecastDetailed7;
    public static TextView extendedForcastDay8;
    public static ImageView extendedForecastIcon8;
    public static TextView extendedForecastShortDesc8;
    public static TextView extendedForecasttemp8;
    public static TextView extendedForecastwindSpeed8;
    public static TextView extendedForecastwindDirection8;
    public static TextView extendedForecastDetailed8;
    public static TextView extendedForcastDay9;
    public static ImageView extendedForecastIcon9;
    public static TextView extendedForecastShortDesc9;
    public static TextView extendedForecasttemp9;
    public static TextView extendedForecastwindSpeed9;
    public static TextView extendedForecastwindDirection9;
    public static TextView extendedForecastDetailed9;
    public static TextView extendedForcastDay10;
    public static ImageView extendedForecastIcon10;
    public static TextView extendedForecastShortDesc10;
    public static TextView extendedForecasttemp10;
    public static TextView extendedForecastwindSpeed10;
    public static TextView extendedForecastwindDirection10;
    public static TextView extendedForecastDetailed10;
    public static TextView extendedForcastDay11;
    public static ImageView extendedForecastIcon11;
    public static TextView extendedForecastShortDesc11;
    public static TextView extendedForecasttemp11;
    public static TextView extendedForecastwindSpeed11;
    public static TextView extendedForecastwindDirection11;
    public static TextView extendedForecastDetailed11;
    public static TextView extendedForcastDay12;
    public static ImageView extendedForecastIcon12;
    public static TextView extendedForecastShortDesc12;
    public static TextView extendedForecasttemp12;
    public static TextView extendedForecastwindSpeed12;
    public static TextView extendedForecastwindDirection12;
    public static TextView extendedForecastDetailed12;
    public static TextView extendedForcastDay13;
    public static ImageView extendedForecastIcon13;
    public static TextView extendedForecastShortDesc13;
    public static TextView extendedForecasttemp13;
    public static TextView extendedForecastwindSpeed13;
    public static TextView extendedForecastwindDirection13;
    public static TextView extendedForecastDetailed13;
    public static TextView extendedForcastDay14;
    public static ImageView extendedForecastIcon14;
    public static TextView extendedForecastShortDesc14;
    public static TextView extendedForecasttemp14;
    public static TextView extendedForecastwindSpeed14;
    public static TextView extendedForecastwindDirection14;
    public static TextView extendedForecastDetailed14;
    public static TextView updateStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("OnCreateViewFrag","Called");
        View rootView = inflater.inflate(R.layout.fragment_main_snapweather, container, false);
        cityText = rootView.findViewById(R.id.cityText);
        observationStation = rootView.findViewById(R.id.observationsLoc);
        condDescr = rootView.findViewById(R.id.condDescr);
        temp = rootView.findViewById(R.id.temp);
        hum = rootView.findViewById(R.id.hum);
        press = rootView.findViewById(R.id.press);
        windSpeed = rootView.findViewById(R.id.windSpeed);
        windDeg = rootView.findViewById(R.id.windDeg);
        imgView = rootView.findViewById(R.id.condIcon);
        compassView = rootView.findViewById(R.id.windIcon);
        AlertView = rootView.findViewById(R.id.AlertLayout);
        currentWeatherLayout = rootView.findViewById(R.id.currentWeatherLayout);
        AlertList = rootView.findViewById(R.id.weatherAlertList);
        lastUpdated = rootView.findViewById(R.id.lastupdated);
        loadingSpin = rootView.findViewById(R.id.loadingPanel);
        feelslikelab = rootView.findViewById(R.id.feelslikeLab);
        feelslike = rootView.findViewById(R.id.feelslike);
        heatcoldWarningimgView = rootView.findViewById(R.id.heatcoldwarnIcon);
        updateStatus = rootView.findViewById(R.id.updateStatus);

        extendedForecastlastupdated = rootView.findViewById(R.id.extendedForecastlastupdated);
        extendedForecastLayoutMain = rootView.findViewById(R.id.extendedForecastLayoutMain);
        extendedForecastRelativeLayout1 = rootView.findViewById(R.id.extendedForecastRelativeLayout1);
        extendedForecastRelativeLayout2 = rootView.findViewById(R.id.extendedForecastRelativeLayout2);
        extendedForecastRelativeLayout3 = rootView.findViewById(R.id.extendedForecastRelativeLayout3);
        extendedForecastRelativeLayout4 = rootView.findViewById(R.id.extendedForecastRelativeLayout4);
        extendedForecastRelativeLayout5 = rootView.findViewById(R.id.extendedForecastRelativeLayout5);
        extendedForecastRelativeLayout6 = rootView.findViewById(R.id.extendedForecastRelativeLayout6);
        extendedForecastRelativeLayout7 = rootView.findViewById(R.id.extendedForecastRelativeLayout7);
        extendedForecastRelativeLayout8 = rootView.findViewById(R.id.extendedForecastRelativeLayout8);
        extendedForecastRelativeLayout9 = rootView.findViewById(R.id.extendedForecastRelativeLayout9);
        extendedForecastRelativeLayout10 = rootView.findViewById(R.id.extendedForecastRelativeLayout10);
        extendedForecastRelativeLayout11 = rootView.findViewById(R.id.extendedForecastRelativeLayout11);
        extendedForecastRelativeLayout12 = rootView.findViewById(R.id.extendedForecastRelativeLayout12);
        extendedForecastRelativeLayout13 = rootView.findViewById(R.id.extendedForecastRelativeLayout13);
        extendedForecastRelativeLayout14 = rootView.findViewById(R.id.extendedForecastRelativeLayout14);
        extendedForcastDay1 = rootView.findViewById(R.id.extendedForcastDay1);
        extendedForecastIcon1 = rootView.findViewById(R.id.extendedForecastIcon1);
        extendedForecastShortDesc1 = rootView.findViewById(R.id.extendedForecastShortDesc1);
        extendedForecasttemp1 = rootView.findViewById(R.id.extendedForecasttemp1);
        extendedForecastwindSpeed1 = rootView.findViewById(R.id.extendedForecastwindSpeed1);
        extendedForecastwindDirection1 = rootView.findViewById(R.id.extendedForecastwindDirection1);
        extendedForecastDetailed1 = rootView.findViewById(R.id.extendedForecastDetailed1);
        extendedForcastDay2 = rootView.findViewById(R.id.extendedForcastDay2);
        extendedForecastIcon2 = rootView.findViewById(R.id.extendedForecastIcon2);
        extendedForecastShortDesc2 = rootView.findViewById(R.id.extendedForecastShortDesc2);
        extendedForecasttemp2 = rootView.findViewById(R.id.extendedForecasttemp2);
        extendedForecastwindSpeed2 = rootView.findViewById(R.id.extendedForecastwindSpeed2);
        extendedForecastwindDirection2 = rootView.findViewById(R.id.extendedForecastwindDirection2);
        extendedForecastDetailed2 = rootView.findViewById(R.id.extendedForecastDetailed2);
        extendedForcastDay3 = rootView.findViewById(R.id.extendedForcastDay3);
        extendedForecastIcon3 = rootView.findViewById(R.id.extendedForecastIcon3);
        extendedForecastShortDesc3 = rootView.findViewById(R.id.extendedForecastShortDesc3);
        extendedForecasttemp3 = rootView.findViewById(R.id.extendedForecasttemp3);
        extendedForecastwindSpeed3 = rootView.findViewById(R.id.extendedForecastwindSpeed3);
        extendedForecastwindDirection3 = rootView.findViewById(R.id.extendedForecastwindDirection3);
        extendedForecastDetailed3 = rootView.findViewById(R.id.extendedForecastDetailed3);
        extendedForcastDay4 = rootView.findViewById(R.id.extendedForcastDay4);
        extendedForecastIcon4 = rootView.findViewById(R.id.extendedForecastIcon4);
        extendedForecastShortDesc4 = rootView.findViewById(R.id.extendedForecastShortDesc4);
        extendedForecasttemp4 = rootView.findViewById(R.id.extendedForecasttemp4);
        extendedForecastwindSpeed4 = rootView.findViewById(R.id.extendedForecastwindSpeed4);
        extendedForecastwindDirection4 = rootView.findViewById(R.id.extendedForecastwindDirection4);
        extendedForecastDetailed4 = rootView.findViewById(R.id.extendedForecastDetailed4);
        extendedForcastDay5 = rootView.findViewById(R.id.extendedForcastDay5);
        extendedForecastIcon5 = rootView.findViewById(R.id.extendedForecastIcon5);
        extendedForecastShortDesc5 = rootView.findViewById(R.id.extendedForecastShortDesc5);
        extendedForecasttemp5 = rootView.findViewById(R.id.extendedForecasttemp5);
        extendedForecastwindSpeed5 = rootView.findViewById(R.id.extendedForecastwindSpeed5);
        extendedForecastwindDirection5 = rootView.findViewById(R.id.extendedForecastwindDirection5);
        extendedForecastDetailed5 = rootView.findViewById(R.id.extendedForecastDetailed5);
        extendedForcastDay6 = rootView.findViewById(R.id.extendedForcastDay6);
        extendedForecastIcon6 = rootView.findViewById(R.id.extendedForecastIcon6);
        extendedForecastShortDesc6 = rootView.findViewById(R.id.extendedForecastShortDesc6);
        extendedForecasttemp6 = rootView.findViewById(R.id.extendedForecasttemp6);
        extendedForecastwindSpeed6 = rootView.findViewById(R.id.extendedForecastwindSpeed6);
        extendedForecastwindDirection6 = rootView.findViewById(R.id.extendedForecastwindDirection6);
        extendedForecastDetailed6 = rootView.findViewById(R.id.extendedForecastDetailed6);
        extendedForcastDay7 = rootView.findViewById(R.id.extendedForcastDay7);
        extendedForecastIcon7 = rootView.findViewById(R.id.extendedForecastIcon7);
        extendedForecastShortDesc7 = rootView.findViewById(R.id.extendedForecastShortDesc7);
        extendedForecasttemp7 = rootView.findViewById(R.id.extendedForecasttemp7);
        extendedForecastwindSpeed7 = rootView.findViewById(R.id.extendedForecastwindSpeed7);
        extendedForecastwindDirection7 = rootView.findViewById(R.id.extendedForecastwindDirection7);
        extendedForecastDetailed7 = rootView.findViewById(R.id.extendedForecastDetailed7);
        extendedForcastDay8 = rootView.findViewById(R.id.extendedForcastDay8);
        extendedForecastIcon8 = rootView.findViewById(R.id.extendedForecastIcon8);
        extendedForecastShortDesc8 = rootView.findViewById(R.id.extendedForecastShortDesc8);
        extendedForecasttemp8 = rootView.findViewById(R.id.extendedForecasttemp8);
        extendedForecastwindSpeed8 = rootView.findViewById(R.id.extendedForecastwindSpeed8);
        extendedForecastwindDirection8 = rootView.findViewById(R.id.extendedForecastwindDirection8);
        extendedForecastDetailed8 = rootView.findViewById(R.id.extendedForecastDetailed8);
        extendedForcastDay9 = rootView.findViewById(R.id.extendedForcastDay9);
        extendedForecastIcon9 = rootView.findViewById(R.id.extendedForecastIcon9);
        extendedForecastShortDesc9 = rootView.findViewById(R.id.extendedForecastShortDesc9);
        extendedForecasttemp9 = rootView.findViewById(R.id.extendedForecasttemp9);
        extendedForecastwindSpeed9 = rootView.findViewById(R.id.extendedForecastwindSpeed9);
        extendedForecastwindDirection9 = rootView.findViewById(R.id.extendedForecastwindDirection9);
        extendedForecastDetailed9 = rootView.findViewById(R.id.extendedForecastDetailed9);
        extendedForcastDay10 = rootView.findViewById(R.id.extendedForcastDay10);
        extendedForecastIcon10 = rootView.findViewById(R.id.extendedForecastIcon10);
        extendedForecastShortDesc10 = rootView.findViewById(R.id.extendedForecastShortDesc10);
        extendedForecasttemp10 = rootView.findViewById(R.id.extendedForecasttemp10);
        extendedForecastwindSpeed10 = rootView.findViewById(R.id.extendedForecastwindSpeed10);
        extendedForecastwindDirection10 = rootView.findViewById(R.id.extendedForecastwindDirection10);
        extendedForecastDetailed10 = rootView.findViewById(R.id.extendedForecastDetailed10);
        extendedForcastDay11 = rootView.findViewById(R.id.extendedForcastDay11);
        extendedForecastIcon11 = rootView.findViewById(R.id.extendedForecastIcon11);
        extendedForecastShortDesc11 = rootView.findViewById(R.id.extendedForecastShortDesc11);
        extendedForecasttemp11 = rootView.findViewById(R.id.extendedForecasttemp11);
        extendedForecastwindSpeed11 = rootView.findViewById(R.id.extendedForecastwindSpeed11);
        extendedForecastwindDirection11 = rootView.findViewById(R.id.extendedForecastwindDirection11);
        extendedForecastDetailed11 = rootView.findViewById(R.id.extendedForecastDetailed11);
        extendedForcastDay12 = rootView.findViewById(R.id.extendedForcastDay12);
        extendedForecastIcon12 = rootView.findViewById(R.id.extendedForecastIcon12);
        extendedForecastShortDesc12 = rootView.findViewById(R.id.extendedForecastShortDesc12);
        extendedForecasttemp12 = rootView.findViewById(R.id.extendedForecasttemp12);
        extendedForecastwindSpeed12 = rootView.findViewById(R.id.extendedForecastwindSpeed12);
        extendedForecastwindDirection12 = rootView.findViewById(R.id.extendedForecastwindDirection12);
        extendedForecastDetailed12 = rootView.findViewById(R.id.extendedForecastDetailed12);
        extendedForcastDay13 = rootView.findViewById(R.id.extendedForcastDay13);
        extendedForecastIcon13 = rootView.findViewById(R.id.extendedForecastIcon13);
        extendedForecastShortDesc13 = rootView.findViewById(R.id.extendedForecastShortDesc13);
        extendedForecasttemp13 = rootView.findViewById(R.id.extendedForecasttemp13);
        extendedForecastwindSpeed13 = rootView.findViewById(R.id.extendedForecastwindSpeed13);
        extendedForecastwindDirection13 = rootView.findViewById(R.id.extendedForecastwindDirection13);
        extendedForecastDetailed13 = rootView.findViewById(R.id.extendedForecastDetailed13);
        extendedForcastDay14 = rootView.findViewById(R.id.extendedForcastDay14);
        extendedForecastIcon14 = rootView.findViewById(R.id.extendedForecastIcon14);
        extendedForecastShortDesc14 = rootView.findViewById(R.id.extendedForecastShortDesc14);
        extendedForecasttemp14 = rootView.findViewById(R.id.extendedForecasttemp14);
        extendedForecastwindSpeed14 = rootView.findViewById(R.id.extendedForecastwindSpeed14);
        extendedForecastwindDirection14 = rootView.findViewById(R.id.extendedForecastwindDirection14);
        extendedForecastDetailed14 = rootView.findViewById(R.id.extendedForecastDetailed14);

        if (LONGITUDE == 0.0 && LATITUDE == 0.0) {
            // Apply weather data to view
            MainFragmentSnapWeather.updateStatus.setText("Status: Updating");
            MainFragmentSnapWeather.cityText.setText("REFRESHING WEATHER DATA");
            MainFragmentSnapWeather.observationStation.setText("FINDING LOCATION, PLEASE WAIT");
            MainFragmentSnapWeather.condDescr.setText("--");
            MainFragmentSnapWeather.temp.setText("--" + "°F (" + "--" + "°C)");
            MainFragmentSnapWeather.feelslike.setText("--" + "°F (" + "--" + "°C)");
            MainFragmentSnapWeather.hum.setText("--" + "%");
            MainFragmentSnapWeather.press.setText("-- in (-- mb)");
            MainFragmentSnapWeather.windSpeed.setText("--" + " mph (" + "--" + " kph)");
            MainFragmentSnapWeather.windDeg.setText("--" + "°");
            img = BitmapFactory.decodeResource(this.getResources(), R.raw.black);
            MainFragmentSnapWeather.imgView.setImageBitmap(Bitmap.createBitmap(img));
            compass = BitmapFactory.decodeResource(this.getResources(), R.raw.compass);
            MainFragmentSnapWeather.compassView.setVisibility(View.GONE);
            MainFragmentSnapWeather.compassView.setImageBitmap(Bitmap.createBitmap(compass));
            MainFragmentSnapWeather.lastUpdated.setText("--");
            MainFragmentSnapWeather.loadingSpin.setVisibility(VISIBLE);
            MainFragmentSnapWeather.heatcoldWarningimgView.setVisibility(View.GONE);
            heatcoldwarningimg = BitmapFactory.decodeResource(this.getResources(), R.raw.blue_heat);
            MainFragmentSnapWeather.heatcoldWarningimgView.setImageBitmap(Bitmap.createBitmap(heatcoldwarningimg));

            //Hide extended forecast
            MainFragmentSnapWeather.extendedForecastlastupdated.setText("Not Updated");
            MainFragmentSnapWeather.extendedForecastLayoutMain.setVisibility(View.GONE);
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

        AlertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View rootView) {

                //setup transaction for click and allow to go back and pop stack
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new AlertsFragmentSnapWeather(), "AlertLists")
                        .addToBackStack(null)
                        .commit();
            }
        });

        //Return the fragments view
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();


        }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("OnResumeFrag","Called");
        MainActivitySnapWeather.RefreshWeatherDataOnLoad();
    }

    //Create the action bar menus
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.

        Log.d("OptionsMenu", "onCreateOptionsMenu() called");
        inflater.inflate(R.menu.menu_snapweather, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MainActivitySnapWeather.Radaritem = menu.findItem(R.id.action_radar);
        MainActivitySnapWeather.Radaritem.setVisible(false);

        MainActivitySnapWeather.RefreshonFail = menu.findItem(R.id.action_RefreshonFail);
        MainActivitySnapWeather.RefreshonFail.setVisible(false);

        //Enable radar if station is found
        MainActivitySnapWeather.EnableDisableRadarOption();

        //Enable refresh is failure occurred
        MainActivitySnapWeather.EnableDisableRefreshOption();

    }

    //Run these tasks when options in the action bar are selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Log.d("OptionsMenu", "onOptionsItemsSelected() called");

        int id = item.getItemId();

        if (id == R.id.action_radar) {
            Log.d("OptionsMenu", "Radar application called");

            //setup transaction for click and allow to go back and pop stack
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new RadarWebFragmentSnapWeather(), "RadarTask")
                    .addToBackStack(null)
                    .commit();
        }

        if (id == R.id.action_settings) {
            Log.d("OptionsMenu", "Settings application called");
            //setup transaction for click and allow to go back and pop stack
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new SettingsFragmentSnapWeather(), "SettingsTask")
                    .addToBackStack(null)
                    .commit();
        }

        if (id == R.id.action_RefreshonFail) {
            Log.d("OptionsMenu", "Refresh called");

            if (ModelWeatherNOAA.WeatherPoints.longitude!=null && ModelWeatherNOAA.WeatherPoints.latitude!=null) {
                JSONWeatherTaskNOAA task = new JSONWeatherTaskNOAA();
                MainFragmentSnapWeather.loadingSpin.setVisibility(VISIBLE);
                task.execute(String.valueOf(ModelWeatherNOAA.WeatherPoints.longitude),String.valueOf(ModelWeatherNOAA.WeatherPoints.latitude));
            }

        }

        if (id == R.id.action_about) {
            Log.d("OptionsMenu", "About application called");
            //setup transaction for click and allow to go back and pop stack
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new AboutFragmentSnapWeather(), "AboutTask")
                    .addToBackStack(null)
                    .commit();
        }

        return super.onOptionsItemSelected(item);
    }

}

