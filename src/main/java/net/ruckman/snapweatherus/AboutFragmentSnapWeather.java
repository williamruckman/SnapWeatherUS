package net.ruckman.snapweatherus;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import net.ruckman.snapweatherus.NOAAWeatherAPI.JSONWeatherTaskNOAA;
import net.ruckman.snapweatherus.NOAAWeatherAPI.ModelWeatherNOAA;

import static android.view.View.VISIBLE;
import static net.ruckman.snapweatherus.MainActivitySnapWeather.LATITUDE;
import static net.ruckman.snapweatherus.MainActivitySnapWeather.LONGITUDE;

public class AboutFragmentSnapWeather extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("OnCreateViewFrag", "Called");
        View rootView = inflater.inflate(R.layout.fragment_about_snapweather, container, false);
        TextView aboutWebsite = rootView.findViewById(R.id.aboutWebsite);
        TextView aboutEmail = rootView.findViewById(R.id.aboutEmail);
        ImageView aboutNWS = rootView.findViewById(R.id.nwsImage);
        ImageView aboutNOAA = rootView.findViewById(R.id.noaaImage);

        aboutWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View rootView) {
                //Load Website
                String url = "https://ruckman.net";

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        aboutNWS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View rootView) {
                //Load Website
                String url = "https://www.weather.gov/";

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        aboutNOAA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View rootView) {
                //Load Website
                String url = "https://www.weather.gov/";

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        aboutEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View rootView) {
                String uriText =
                        "mailto:william@ruckman.net" +
                                "?subject=" + Uri.encode("SnapWeatherUS");

                Uri uri = Uri.parse(uriText);

                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setData(uri);
                try {
                    startActivity(Intent.createChooser(sendIntent, "Send message"));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
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

    }

}

