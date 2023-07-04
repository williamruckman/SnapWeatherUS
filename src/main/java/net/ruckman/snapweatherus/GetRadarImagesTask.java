package net.ruckman.snapweatherus;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import net.ruckman.snapweatherus.NOAAWeatherAPI.HTTPClientNOAA;
import net.ruckman.snapweatherus.NOAAWeatherAPI.ModelWeatherNOAA;

import java.io.IOException;

import static java.security.AccessController.getContext;
import static net.ruckman.snapweatherus.OldRadarFragmentSnapWeather.IsAnimated;
import static net.ruckman.snapweatherus.OldRadarFragmentSnapWeather.WasAnimated;
import static net.ruckman.snapweatherus.OldRadarFragmentSnapWeather.radarProgressBar;
import static net.ruckman.snapweatherus.OldRadarFragmentSnapWeather.radarimgViewL0;
import static net.ruckman.snapweatherus.OldRadarFragmentSnapWeather.radarimgViewL1;
import static net.ruckman.snapweatherus.OldRadarFragmentSnapWeather.radarimgViewL2;
import static net.ruckman.snapweatherus.OldRadarFragmentSnapWeather.radarimgViewL3;
import static net.ruckman.snapweatherus.OldRadarFragmentSnapWeather.radarimgViewL4;
import static net.ruckman.snapweatherus.OldRadarFragmentSnapWeather.radarimgViewL5;
import static net.ruckman.snapweatherus.OldRadarFragmentSnapWeather.radarimgViewL6;
import static net.ruckman.snapweatherus.OldRadarFragmentSnapWeather.radarimgViewL7;
import static net.ruckman.snapweatherus.OldRadarFragmentSnapWeather.radarimgViewM1;
import static net.ruckman.snapweatherus.OldRadarFragmentSnapWeather.radarimgViewM2;
import static net.ruckman.snapweatherus.OldRadarFragmentSnapWeather.radarimgViewM3;
import static net.ruckman.snapweatherus.OldRadarFragmentSnapWeather.radarimgViewM4;
import static net.ruckman.snapweatherus.OldRadarFragmentSnapWeather.radarimgViewM5;
import static net.ruckman.snapweatherus.OldRadarFragmentSnapWeather.radarimgViewM6;

public class GetRadarImagesTask extends AsyncTask<Object, Void, Void> {

    @SuppressLint("WrongThread")
    @Override
    protected Void doInBackground(Object[] params) {

        //Start
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                radarProgressBar.setVisibility(View.VISIBLE);
            }
        });

        //create radar site name
        ModelWeatherNOAA.RadarImagesCache.RadarImageSiteName = ModelWeatherNOAA.WeatherPoints.radarstation.substring(1);

        //TODO download and set image layers
        (new HTTPClientNOAA()).getRadarImageLayers(ModelWeatherNOAA.RadarImagesCache.RadarImageSiteName);

        //TODO download animated images test
        try {
            (new HTTPClientNOAA()).getRadarAnimatedList(ModelWeatherNOAA.RadarImagesCache.RadarImageSiteName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                if (ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL0 != null && ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL0.length > 0) {
                    Bitmap img0 = BitmapFactory.decodeByteArray(ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL0, 0, ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL0.length);
                    radarimgViewL0.setImageBitmap(Bitmap.createBitmap(img0));
                }

                if (ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL1 != null && ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL1.length > 0) {
                    Bitmap img1 = BitmapFactory.decodeByteArray(ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL1, 0, ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL1.length);
                    radarimgViewL1.setImageBitmap(Bitmap.createBitmap(img1));
                }

                if (ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM1 != null && ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM1.length > 0 ) {
                    Bitmap imgM1 = BitmapFactory.decodeByteArray(ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM1, 0, ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM1.length);
                    radarimgViewM1.setImageBitmap(Bitmap.createBitmap(imgM1));
                }

                if (ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM2 != null && ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM2.length > 0) {
                    Bitmap imgM2 = BitmapFactory.decodeByteArray(ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM2, 0, ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM2.length);
                    radarimgViewM2.setImageBitmap(Bitmap.createBitmap(imgM2));
                }

                if (ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM3 != null && ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM3.length > 0) {
                    Bitmap imgM3 = BitmapFactory.decodeByteArray(ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM3, 0, ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM3.length);
                    radarimgViewM3.setImageBitmap(Bitmap.createBitmap(imgM3));
                }

                if (ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM4 != null && ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM4.length > 0) {
                    Bitmap imgM4 = BitmapFactory.decodeByteArray(ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM4, 0, ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM4.length);
                    radarimgViewM4.setImageBitmap(Bitmap.createBitmap(imgM4));
                }

                if (ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM5 != null && ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM5.length > 0) {
                    Bitmap imgM5 = BitmapFactory.decodeByteArray(ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM5, 0, ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM5.length);
                    radarimgViewM5.setImageBitmap(Bitmap.createBitmap(imgM5));
                }

                if (ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM6 != null && ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM6.length > 0) {
                    Bitmap imgM6 = BitmapFactory.decodeByteArray(ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM6, 0, ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM6.length);
                    radarimgViewM6.setImageBitmap(Bitmap.createBitmap(imgM6));
                }

                if (ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL2 != null && ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL2.length > 0) {
                    Bitmap img2 = BitmapFactory.decodeByteArray(ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL2, 0, ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL2.length);
                    radarimgViewL2.setImageBitmap(Bitmap.createBitmap(img2));
                }

                if (ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL3 != null && ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL3.length > 0) {
                    Bitmap img3 = BitmapFactory.decodeByteArray(ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL3, 0, ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL3.length);
                    radarimgViewL3.setImageBitmap(Bitmap.createBitmap(img3));
                }

                if (ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL4 != null && ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL4.length > 0) {
                    Bitmap img4 = BitmapFactory.decodeByteArray(ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL4, 0, ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL4.length);
                    radarimgViewL4.setImageBitmap(Bitmap.createBitmap(img4));
                }

                if (ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL5 != null && ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL5.length > 0) {
                    Bitmap img5 = BitmapFactory.decodeByteArray(ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL5, 0, ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL5.length);
                    radarimgViewL5.setImageBitmap(Bitmap.createBitmap(img5));
                }

                if (ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL6 != null && ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL6.length > 0) {
                    Bitmap img6 = BitmapFactory.decodeByteArray(ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL6, 0, ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL6.length);
                    radarimgViewL6.setImageBitmap(Bitmap.createBitmap(img6));
                }

                if (ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL7 != null && ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL7.length > 0) {
                    Bitmap img7 = BitmapFactory.decodeByteArray(ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL7, 0, ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL7.length);
                    radarimgViewL7.setImageBitmap(Bitmap.createBitmap(img7));
                }

            }
        });

        //End
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                //Initial loading complete
                OldRadarFragmentSnapWeather.radarProgressBar.setVisibility(View.GONE);
            }
        });

        //Start animation if it was running
        if (WasAnimated) {
            IsAnimated = true;
            WasAnimated = false;
            AnimateRadarTask animatedradar = new AnimateRadarTask();
            animatedradar.execute(getContext());
        }

        return null;
    }



}
