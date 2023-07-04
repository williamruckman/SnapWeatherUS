package net.ruckman.snapweatherus;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import net.ruckman.snapweatherus.NOAAWeatherAPI.ModelWeatherNOAA;

import static net.ruckman.snapweatherus.MainActivitySnapWeather.myWebView;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Created by William Ruckman on 6/7/2015.
 */
public class RadarWebFragmentSnapWeather extends Fragment {

    public String mUrl;
    public String query;
    public String buildQueryString;
    ProgressBar spinner;
    ImageView loadinglogo;
    WebView mWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        //todo query string for NOAA
        buildQueryString = "{\"agenda\":{\"id\":\"weather\",\"center\":[" + ModelWeatherNOAA.WeatherPoints.longitude + "," + ModelWeatherNOAA.WeatherPoints.latitude + "],\"zoom\":8,\"location\":[" + ModelWeatherNOAA.WeatherPoints.longitude + "," + ModelWeatherNOAA.WeatherPoints.latitude + "]},\"base\":\"standard\",\"county\":true,\"cwa\":true,\"state\":false,\"menu\":false,\"shortFusedOnly\":false}";
        Log.d("DecodedQuery",buildQueryString);
        //Encode URL query
        query = Base64.encodeToString(buildQueryString.getBytes(), Base64.NO_WRAP);
        Log.d("EncodedQuery",query);

        //todo NOAA URL
        mUrl = "https://radar.weather.gov/?settings=v1_" + query + "#/";

        //todo windy.com
        mUrl = "https://www.windy.com/-Weather-radar-radar?radar,"+ModelWeatherNOAA.WeatherPoints.latitude+","+ModelWeatherNOAA.WeatherPoints.longitude+",9";

        Log.d("ConstructedURL",mUrl);

    }

    public static RadarWebFragmentSnapWeather newInstance() {
        return new RadarWebFragmentSnapWeather();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_snapweather_radarweb, parent, false);

        mWebView = v.findViewById(R.id.webView);
        spinner = v.findViewById(R.id.webprogressBar);
        loadinglogo = v.findViewById(R.id.loadinglogo);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setDomStorageEnabled(false);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.getSettings().setUserAgentString("Mozilla/5.0 (Linux;) Chrome/83.0.4103.106 Safari/537.36");

        mWebView.setInitialScale(100);
        mWebView.setKeepScreenOn(true);

        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //If needed, deny and allow only certain domains
                Log.d("LOADING-URL", url);

                //todo windy block urls
                if (url.contains("play.google.com")) {
                    return true;
                }

                if (url.contains("windy.com/sedlina/ga/")) {
                    return true;
                }

                if (!url.contains(("windy.com")) || !url.contains("cloudfront.net")) {
                    return true;
                }

                return false;
            }

            //TODO REMOVE IF NOT WORKING
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view,
                                                              WebResourceRequest request) {
                super.shouldInterceptRequest(view, request);
                Uri loadingurl = request.getUrl();
                String url = loadingurl.toString();
                Log.d("-shouldInterceptRequest","UrlLoading Launched: "+loadingurl);

                //todo block resource

                //todo windy.com logic
                if (url.contains("windy.com/sedlina/ga/")) {
                    Log.d("javascripturl", " blocked: "+url);
                    return new WebResourceResponse("text/javascript", "UTF-8", null);
                }

                if (url.contains(("windy.com")) || url.contains("cloudfront.net")) {
                    return null;
                }

                //TODO grab request and manipulate before returning it
//                if (url.contains("radar.weather.gov/?settings=v1_")) {
//                    Log.d("-URLINTERCEPT","URLIntercept Launched: "+url);
//                    URL url2 = null;
//                    try {
//                        url2 = new URL(url);
//                    } catch (MalformedURLException e) {
//                        Log.d("-URLINTERCEPT","EXCEPTION OCCURRED");
//                        return null;
//                    }
//                    String WebReturnString;
//                    WebReturnString = getWebData(url2);
//                    Log.d("HTTPBefore",WebReturnString);
//                    WebReturnString = WebReturnString.replace("<div class=\"banner\">(.*)</div>","");
//                    Log.d("HTTPAfter",WebReturnString);
//
//                    InputStream stream = new ByteArrayInputStream(WebReturnString.getBytes(StandardCharsets.UTF_8));
//                    return new WebResourceResponse("text/html", "UTF-8", stream);
//                }
                return null;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                super.onPageStarted(view, url, favicon);
//                mWebView.setVisibility(View.GONE);
//                spinner.setVisibility(View.VISIBLE);
//                loadinglogo.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                // hide element by class name

                //todo NOAA
//                myWebView.loadUrl("javascript:(function() { " +
//                        "document.getElementsByClassName('cmi-radar-menu-container')[0].style.display='none'; })()");

//                myWebView.loadUrl("javascript:(function() { " +
//                        "const element = document.getElementsByTagName('header')[0]; element.innerHTML = \"\"; })()");

//                myWebView.loadUrl("javascript:(function() { " +
//                        "const element = document.getElementsByClassName('menu')[0]; element.remove(); })()");

//                myWebView.loadUrl("javascript:(function() { " +
//                        "const element = document.getElementsByClassName('content-message')[0]; element.innerHTML = \"\"; })()");

                //todo windy.com
                myWebView.loadUrl("javascript:(function() { " +
                        "document.getElementById('open-in-app').style.display='none'; })()");

                myWebView.loadUrl("javascript:(function() { " +
                        "const element = document.getElementsyId('articles'); element.innerHTML = \"\"; })()");

                myWebView.loadUrl("javascript:(function() { " +
                        "document.getElementById('articles').style.display='none'; })()");

                myWebView.loadUrl("javascript:(function() { " +
                        "const element = document.getElementById('articles'); element.remove(); })()");

//                mWebView.setVisibility(View.VISIBLE);
//                spinner.setVisibility(View.GONE);
//                loadinglogo.setVisibility(View.GONE);

            }

        });

        //mWebView.clearCache(true);
        mWebView.loadUrl(mUrl);

        //set reference in main activity for go back requests
        myWebView = mWebView;

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_snapweather_radarweb, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //this method is used for handling menu items' events
        // Handle item selection

        int id = item.getItemId();

        if (id == R.id.goHome) {
            Toast.makeText(getActivity(), "Loading home",
                    Toast.LENGTH_SHORT).show();
            myWebView.loadUrl(mUrl);
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    //TODO Fetch any given webpage manually
    public String getWebData(URL urlFetch) {
        HttpURLConnection con = null ;
        InputStream is = null;
        int httpTimeout = 4000;
        String userAgent = ModelWeatherNOAA.WeatherPoints.userAgent;
        //String myCookie = "";

        try {
            Log.d("URL", String.valueOf(urlFetch));
            con = (HttpURLConnection) urlFetch.openConnection();
            con.setReadTimeout(httpTimeout);
            con.setConnectTimeout(httpTimeout);
            con.setRequestProperty("User-Agent",userAgent);
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(false);
            //con.setRequestProperty("Cookie", myCookie);
            con.connect();

            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Let's read the response
                StringBuffer buffer = new StringBuffer();
                is = con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line = null;
                while ((line = br.readLine()) != null)
                    buffer.append(line + "\r\n");

                is.close();
                con.disconnect();
                return buffer.toString();
            } else {
                String hostname = con.getURL().getHost();
                String ipAddress = InetAddress.getByName(hostname).getHostAddress();

                Log.d("HTTPFailed", con.getResponseMessage());
                Log.d("HTTPFailedHost", hostname);
                Log.d("HTTPFailedIP", ipAddress);
                return null;
            }
        } catch (Throwable t) {

        } finally {
            try {
                is.close();
            } catch (Throwable t) {
            }
            try {
                con.disconnect();
            } catch (Throwable t) {
            }
        }

        return null;

    }

}

