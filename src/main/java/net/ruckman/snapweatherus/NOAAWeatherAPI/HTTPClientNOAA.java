package net.ruckman.snapweatherus.NOAAWeatherAPI;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class HTTPClientNOAA {

    private static String BASE_URL = "https://api.weather.gov";
    private static int httpTimeout = 30000;

    public String getWeatherPoints(String longitude, String latitude) {
        HttpURLConnection con = null ;
        InputStream is = null;

        Log.d("LONGSTART",longitude);
        Log.d("LONGEND",longitude);
        try {
            URL urlPoints = new URL(BASE_URL + "/points/" + latitude + "," + longitude);
            Log.d("URL", String.valueOf(urlPoints));
            con = (HttpURLConnection) urlPoints.openConnection();
            con.setReadTimeout(httpTimeout);
            con.setConnectTimeout(httpTimeout); //set timeout to 5 seconds
            con.setRequestProperty("User-Agent",ModelWeatherNOAA.WeatherPoints.userAgent);
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(false);
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
                Log.d("LastCheckFailedSet", "false");
                ModelWeatherNOAA.WeatherPoints.LastCheckFailedWeatherPoints ="false";
                return buffer.toString();
            } else {
                String hostname = con.getURL().getHost();
                String ipAddress = InetAddress.getByName(hostname).getHostAddress();

                Log.d("HTTPFailed", con.getResponseMessage());
                Log.d("HTTPFailedHost", hostname);
                Log.d("HTTPFailedIP", ipAddress);
                Log.d("LastCheckFailedSet", "true");
                ModelWeatherNOAA.WeatherPoints.LastCheckFailedWeatherPoints ="true";
                return null;
            }

        } catch (Throwable t) {
            t.printStackTrace();
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

    public String getStationData() {
        HttpURLConnection con = null ;
        InputStream is = null;

        try {
            URL urlStation = new URL(BASE_URL + "/gridpoints/" + ModelWeatherNOAA.WeatherPoints.office + "/" + ModelWeatherNOAA.WeatherPoints.gridx + "," + ModelWeatherNOAA.WeatherPoints.gridy + "/stations");
            Log.d("URL", String.valueOf(urlStation));
            con = (HttpURLConnection) urlStation.openConnection();
            con.setReadTimeout(httpTimeout);
            con.setConnectTimeout(httpTimeout); //set timeout to 5 seconds
            con.setRequestProperty("User-Agent",ModelWeatherNOAA.WeatherPoints.userAgent);
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(false);
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
                Log.d("LastCheckFailed2Set", "false");
                ModelWeatherNOAA.WeatherPoints.LastCheckFailedStationData ="false";
                return buffer.toString();
            } else {
                String hostname = con.getURL().getHost();
                String ipAddress = InetAddress.getByName(hostname).getHostAddress();

                Log.d("HTTPFailed", con.getResponseMessage());
                Log.d("HTTPFailedHost", hostname);
                Log.d("HTTPFailedIP", ipAddress);
                Log.d("LastCheckFailed2Set", "true");
                ModelWeatherNOAA.WeatherPoints.LastCheckFailedStationData ="true";
                return null;
            }
        } catch (Throwable t) {
            t.printStackTrace();
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

    public String getObservationData() {
        HttpURLConnection con = null ;
        InputStream is = null;

        try {
            URL urlObservation = new URL(BASE_URL + "/stations/" + ModelWeatherNOAA.WeatherPoints.stationidentifier1 + "/observations/latest");
            Log.d("URL", String.valueOf(urlObservation));
            con = (HttpURLConnection) urlObservation.openConnection();
            con.setReadTimeout(httpTimeout);
            con.setConnectTimeout(httpTimeout); //set timeout to 5 seconds
            con.setRequestProperty("User-Agent",ModelWeatherNOAA.WeatherPoints.userAgent);
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(false);
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
            t.printStackTrace();
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

    public byte[] getImage(String IMG_URL) {
        HttpURLConnection con = null ;
        InputStream is = null;
        try {
            Log.d("FetchingURL", IMG_URL);
            con = (HttpURLConnection) ( new URL(IMG_URL)).openConnection();
            con.setReadTimeout(httpTimeout);
            con.setConnectTimeout(httpTimeout); //set timeout to 5 seconds
            con.setRequestProperty("User-Agent",ModelWeatherNOAA.WeatherPoints.userAgent);
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(false);
            con.connect();

            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
            // Let's read the response
            is = con.getInputStream();
            byte[] buffer = new byte[16384];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }

            return baos.toByteArray();
            } else {
                String hostname = con.getURL().getHost();
                String ipAddress = InetAddress.getByName(hostname).getHostAddress();

                Log.d("HTTPImageFailed", con.getResponseMessage());
                Log.d("HTTPImageFailedHost", hostname);
                Log.d("HTTPImageFailedIP", ipAddress);

                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch(Throwable t) {
            t.printStackTrace();
        }

        finally {
            try { is.close(); } catch(Throwable t) {}
            try { con.disconnect(); } catch(Throwable t) {}
        }

        return null;

    }

    public String getForecastZoneData() {
        HttpURLConnection con = null ;
        InputStream is = null;

        try {
            URL urlForcastZone = new URL(ModelWeatherNOAA.WeatherPoints.forcastzoneurl);
            Log.d("URL", String.valueOf(urlForcastZone));
            con = (HttpURLConnection) urlForcastZone.openConnection();
            con.setReadTimeout(httpTimeout);
            con.setConnectTimeout(httpTimeout); //set timeout to 5 seconds
            con.setRequestProperty("User-Agent",ModelWeatherNOAA.WeatherPoints.userAgent);
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(false);
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
            t.printStackTrace();
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

    public String getDetailedExtendedForecast() {
        HttpURLConnection con = null ;
        InputStream is = null;

        try {
            Log.d("URLString", String.valueOf(ModelWeatherNOAA.WeatherPoints.forecastGridURL));
            URL forecastGridURL = new URL(ModelWeatherNOAA.WeatherPoints.forecastGridURL);
            Log.d("URL", String.valueOf(forecastGridURL));
            con = (HttpURLConnection) forecastGridURL.openConnection();
            con.setReadTimeout(httpTimeout);
            con.setConnectTimeout(httpTimeout); //set timeout to 5 seconds
            con.setRequestProperty("User-Agent",ModelWeatherNOAA.WeatherPoints.userAgent);
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(false);
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
            t.printStackTrace();
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

    public String getWeatherAlerts() {
        HttpURLConnection con = null ;
        InputStream is = null;

        try {
            URL urlWeatherAlerts = new URL(BASE_URL + "/alerts/active/zone/" + ModelWeatherNOAA.WeatherPoints.forcastzoneid);
            Log.d("URL", String.valueOf(urlWeatherAlerts));
            con = (HttpURLConnection) urlWeatherAlerts.openConnection();
            con.setReadTimeout(httpTimeout);
            con.setConnectTimeout(httpTimeout); //set timeout to 5 seconds
            con.setRequestProperty("User-Agent",ModelWeatherNOAA.WeatherPoints.userAgent);
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(false);
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
            t.printStackTrace();
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

    public void getRadarImageLayers(String RADARSITE)  {

        //initialize variables
        ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL0 = new byte[0];
        ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL1 = new byte[0];
        ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL2 = new byte[0];
        ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL3 = new byte[0];
        ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL4 = new byte[0];
        ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL5 = new byte[0];
        ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL6 = new byte[0];
        ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL7 = new byte[0];

        //get topography L0
        HttpURLConnection con = null ;
        InputStream is = null;
        try {
            con = (HttpURLConnection) ( new URL("https://radar.weather.gov/Overlays/Topo/Short/" + RADARSITE + "_Topo_Short.jpg")).openConnection();
            con.setReadTimeout(httpTimeout);
            con.setConnectTimeout(httpTimeout); //set timeout to 5 seconds
            con.setRequestProperty("User-Agent",ModelWeatherNOAA.WeatherPoints.userAgent);
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(false);
            con.connect();

            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Let's read the response
                is = con.getInputStream();
                byte[] buffer = new byte[8192];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }

                ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL0 = baos.toByteArray();

            } else {
                String hostname = con.getURL().getHost();
                String ipAddress = InetAddress.getByName(hostname).getHostAddress();

                Log.d("HTTPImageFailed", con.getResponseMessage());
                Log.d("HTTPImageFailedHost", hostname);
                Log.d("HTTPImageFailedIP", ipAddress);

                ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL0 = null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL0 = null;
        } finally {
            try { is.close(); } catch(Throwable t) {ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL0 = null;}
            try { con.disconnect(); } catch(Throwable t) {ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL0 = null;}
        }

        //get radar L1
        HttpURLConnection con1 = null ;
        InputStream is1 = null;
        try {
            con1 = (HttpURLConnection) ( new URL("https://radar.weather.gov/RadarImg/N0R/" + RADARSITE + "_N0R_0.gif")).openConnection();
            con1.setReadTimeout(httpTimeout);
            con1.setConnectTimeout(httpTimeout); //set timeout to 5 seconds
            con1.setRequestProperty("User-Agent",ModelWeatherNOAA.WeatherPoints.userAgent);
            con1.setRequestMethod("GET");
            con1.setDoInput(true);
            con1.setDoOutput(false);
            con1.connect();

            if (con1.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Let's read the response
                is1 = con1.getInputStream();
                byte[] buffer = new byte[8192];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                int bytesRead;
                while ((bytesRead = is1.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }

                ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL1 = baos.toByteArray();

            } else {
                String hostname = con1.getURL().getHost();
                String ipAddress = InetAddress.getByName(hostname).getHostAddress();

                Log.d("HTTPImageFailed", con1.getResponseMessage());
                Log.d("HTTPImageFailedHost", hostname);
                Log.d("HTTPImageFailedIP", ipAddress);

                ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL1 = null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL1 = null;
        } finally {
            try { is1.close(); } catch(Throwable t) {ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL1 = null;}
            try { con1.disconnect(); } catch(Throwable t) {ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL1 = null;}
        }

        //get radar L2
        HttpURLConnection con2 = null ;
        InputStream is2 = null;
        try {
            con2 = (HttpURLConnection) ( new URL("https://radar.weather.gov/Overlays/County/Short/" + RADARSITE + "_County_Short.gif")).openConnection();
            con2.setReadTimeout(httpTimeout);
            con2.setConnectTimeout(httpTimeout); //set timeout to 5 seconds
            con2.setRequestProperty("User-Agent",ModelWeatherNOAA.WeatherPoints.userAgent);
            con2.setRequestMethod("GET");
            con2.setDoInput(true);
            con2.setDoOutput(false);
            con2.connect();

            if (con2.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Let's read the response
                is2 = con2.getInputStream();
                byte[] buffer = new byte[8192];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                int bytesRead;
                while ((bytesRead = is2.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }

                ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL2 = baos.toByteArray();

            } else {
                String hostname = con2.getURL().getHost();
                String ipAddress = InetAddress.getByName(hostname).getHostAddress();

                Log.d("HTTPImageFailed", con2.getResponseMessage());
                Log.d("HTTPImageFailedHost", hostname);
                Log.d("HTTPImageFailedIP", ipAddress);

                ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL2 = null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL2 = null;
        } finally {
            try { is2.close(); } catch(Throwable t) {ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL2 = null;}
            try { con2.disconnect(); } catch(Throwable t) {ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL2 = null;}
        }

        //get radar L3
        HttpURLConnection con3 = null ;
        InputStream is3 = null;
        try {
            con3 = (HttpURLConnection) ( new URL("https://radar.weather.gov/Overlays/Rivers/Short/" + RADARSITE + "_Rivers_Short.gif")).openConnection();
            con3.setReadTimeout(httpTimeout);
            con3.setConnectTimeout(httpTimeout); //set timeout to 5 seconds
            con3.setRequestProperty("User-Agent",ModelWeatherNOAA.WeatherPoints.userAgent);
            con3.setRequestMethod("GET");
            con3.setDoInput(true);
            con3.setDoOutput(false);
            con3.connect();

            if (con3.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Let's read the response
                is3 = con3.getInputStream();
                byte[] buffer = new byte[8192];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                int bytesRead;
                while ((bytesRead = is3.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }

                ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL3 = baos.toByteArray();

            } else {
                String hostname = con3.getURL().getHost();
                String ipAddress = InetAddress.getByName(hostname).getHostAddress();

                Log.d("HTTPImageFailed", con3.getResponseMessage());
                Log.d("HTTPImageFailedHost", hostname);
                Log.d("HTTPImageFailedIP", ipAddress);

                ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL3 = null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL3 = null;
        } finally {
            try { is3.close(); } catch(Throwable t) {ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL3 = null;}
            try { con3.disconnect(); } catch(Throwable t) {ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL3 = null;}
        }

        //get radar L4
        HttpURLConnection con4 = null ;
        InputStream is4 = null;
        try {
            con4 = (HttpURLConnection) ( new URL("https://radar.weather.gov/Overlays/Highways/Short/" + RADARSITE + "_Highways_Short.gif")).openConnection();
            con4.setReadTimeout(httpTimeout);
            con4.setConnectTimeout(httpTimeout); //set timeout to 5 seconds
            con4.setRequestProperty("User-Agent",ModelWeatherNOAA.WeatherPoints.userAgent);
            con4.setRequestMethod("GET");
            con4.setDoInput(true);
            con4.setDoOutput(false);
            con4.connect();

            if (con4.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Let's read the response
                is4 = con4.getInputStream();
                byte[] buffer = new byte[8192];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                int bytesRead;
                while ((bytesRead = is4.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }

                ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL4 = baos.toByteArray();

            } else {
                String hostname = con4.getURL().getHost();
                String ipAddress = InetAddress.getByName(hostname).getHostAddress();

                Log.d("HTTPImageFailed", con4.getResponseMessage());
                Log.d("HTTPImageFailedHost", hostname);
                Log.d("HTTPImageFailedIP", ipAddress);

                ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL4 = null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL4 = null;
        } finally {
            try { is4.close(); } catch(Throwable t) {ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL4 = null;}
            try { con4.disconnect(); } catch(Throwable t) {ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL4 = null;}
        }

        //get radar L5
        HttpURLConnection con5 = null ;
        InputStream is5 = null;
        try {
            con5 = (HttpURLConnection) ( new URL("https://radar.weather.gov/Overlays/Cities/Short/" + RADARSITE + "_City_Short.gif")).openConnection();
            con5.setReadTimeout(httpTimeout);
            con5.setConnectTimeout(httpTimeout); //set timeout to 5 seconds
            con5.setRequestProperty("User-Agent",ModelWeatherNOAA.WeatherPoints.userAgent);
            con5.setRequestMethod("GET");
            con5.setDoInput(true);
            con5.setDoOutput(false);
            con5.connect();

            if (con5.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Let's read the response
                is5 = con5.getInputStream();
                byte[] buffer = new byte[8192];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                int bytesRead;
                while ((bytesRead = is5.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }

                ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL5 = baos.toByteArray();

            } else {
                String hostname = con5.getURL().getHost();
                String ipAddress = InetAddress.getByName(hostname).getHostAddress();

                Log.d("HTTPImageFailed", con5.getResponseMessage());
                Log.d("HTTPImageFailedHost", hostname);
                Log.d("HTTPImageFailedIP", ipAddress);

                ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL5 = null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL5 = null;
        } finally {
            try { is5.close(); } catch(Throwable t) {ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL5 = null;}
            try { con5.disconnect(); } catch(Throwable t) {ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL5 = null;}
        }

        //get radar L6
        HttpURLConnection con6 = null ;
        InputStream is6 = null;
        try {
            con6 = (HttpURLConnection) ( new URL("https://radar.weather.gov/Warnings/Short/" + RADARSITE + "_Warnings_0.gif")).openConnection();
            con6.setReadTimeout(httpTimeout);
            con6.setConnectTimeout(httpTimeout); //set timeout to 5 seconds
            con6.setRequestProperty("User-Agent",ModelWeatherNOAA.WeatherPoints.userAgent);
            con6.setRequestMethod("GET");
            con6.setDoInput(true);
            con6.setDoOutput(false);
            con6.connect();

            if (con6.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Let's read the response
                is6 = con6.getInputStream();
                byte[] buffer = new byte[8192];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                int bytesRead;
                while ((bytesRead = is6.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }

                ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL6 = baos.toByteArray();

            } else {
                String hostname = con6.getURL().getHost();
                String ipAddress = InetAddress.getByName(hostname).getHostAddress();

                Log.d("HTTPImageFailed", con6.getResponseMessage());
                Log.d("HTTPImageFailedHost", hostname);
                Log.d("HTTPImageFailedIP", ipAddress);

                ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL6 = null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL6 = null;
        } finally {
            try { is6.close(); } catch(Throwable t) {ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL6 = null;}
            try { con6.disconnect(); } catch(Throwable t) {ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL6 = null;}
        }

        //get radar L7
        HttpURLConnection con7 = null ;
        InputStream is7 = null;
        try {
            con7 = (HttpURLConnection) ( new URL("https://radar.weather.gov/Legend/N0R/" + RADARSITE + "_N0R_Legend_0.gif")).openConnection();
            con7.setReadTimeout(httpTimeout);
            con7.setConnectTimeout(httpTimeout); //set timeout to 5 seconds
            con7.setRequestProperty("User-Agent",ModelWeatherNOAA.WeatherPoints.userAgent);
            con7.setRequestMethod("GET");
            con7.setDoInput(true);
            con7.setDoOutput(false);
            con7.connect();

            if (con7.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Let's read the response
                is7 = con7.getInputStream();
                byte[] buffer = new byte[8192];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                int bytesRead;
                while ((bytesRead = is7.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }

                ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL7 = baos.toByteArray();

            } else {
                String hostname = con7.getURL().getHost();
                String ipAddress = InetAddress.getByName(hostname).getHostAddress();

                Log.d("HTTPImageFailed", con7.getResponseMessage());
                Log.d("HTTPImageFailedHost", hostname);
                Log.d("HTTPImageFailedIP", ipAddress);

                ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL7 = null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL7 = null;
        } finally {
            try { is7.close(); } catch(Throwable t) {ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL7 = null;}
            try { con7.disconnect(); } catch(Throwable t) {ModelWeatherNOAA.RadarImagesCache.RadarImageArrayL7 = null;}
        }

        //finish

    }

    public void getRadarAnimatedList(String RADARSITE) throws IOException {

        //initialize variables
        ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM1 = new byte[0];
        ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM2 = new byte[0];
        ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM3 = new byte[0];
        ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM4 = new byte[0];
        ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM5 = new byte[0];
        ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM6 = new byte[0];

        Log.d("AnimatedRadarList","STARTED!");
        Document doc = Jsoup.connect("https://radar.weather.gov/RadarImg/N0R/" + RADARSITE + "/").get();

        List<String> RadarFileList = new LinkedList<>();

        for (Element file : doc.select("td a")) {
            RadarFileList.add(file.attr("href"));
            Log.d("AnimatedRadarList",file.attr("href"));
        }

        String[] RadarFileArray = new String[RadarFileList.size()];
        for (int i = 0; i < RadarFileList.size(); i++) {
            RadarFileArray[i] = RadarFileList.get(i);
        }

        int ArraySize = RadarFileArray.length-1;
        int ArrayEnd = (ArraySize - 6);

        int counter = 0;
        //get last 6 radar images
        for (int i = ArraySize; i > ArrayEnd; i--) {
            Log.d("AnimatedRadarFinalList","Position: " + i + " Value: " + RadarFileArray[i]);

            //get topography
            HttpURLConnection con = null ;
            InputStream is = null;
            try {
                Log.d("AnimatedRadarURL","https://radar.weather.gov/RadarImg/N0R/" + RADARSITE + "/" + RadarFileArray[i]);
                con = (HttpURLConnection) ( new URL("https://radar.weather.gov/RadarImg/N0R/" + RADARSITE + "/" + RadarFileArray[i])).openConnection();
                con.setReadTimeout(httpTimeout);
                con.setConnectTimeout(httpTimeout); //set timeout to 5 seconds
                con.setRequestProperty("User-Agent",ModelWeatherNOAA.WeatherPoints.userAgent);
                con.setRequestMethod("GET");
                con.setDoInput(true);
                con.setDoOutput(false);
                con.connect();

                if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // Let's read the response
                    is = con.getInputStream();
                    byte[] buffer = new byte[8192];
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        baos.write(buffer, 0, bytesRead);
                    }

                    if (counter == 0) {
                        ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM1 = baos.toByteArray();
                    }
                    if (counter == 1) {
                        ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM2 = baos.toByteArray();
                    }
                    if (counter == 2) {
                        ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM3 = baos.toByteArray();
                    }
                    if (counter == 3) {
                        ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM4 = baos.toByteArray();
                    }
                    if (counter == 4) {
                        ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM5 = baos.toByteArray();
                    }
                    if (counter == 5) {
                        ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM6 = baos.toByteArray();
                    }

                } else {
                    String hostname = con.getURL().getHost();
                    String ipAddress = InetAddress.getByName(hostname).getHostAddress();

                    Log.d("HTTPImageFailed", con.getResponseMessage());
                    Log.d("HTTPImageFailedHost", hostname);
                    Log.d("HTTPImageFailedIP", ipAddress);

                    if (counter == 0) {
                        ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM1 = null;
                    }
                    if (counter == 1) {
                        ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM2 = null;
                    }
                    if (counter == 2) {
                        ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM3 = null;
                    }
                    if (counter == 3) {
                        ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM4 = null;
                    }
                    if (counter == 4) {
                        ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM5 = null;
                    }
                    if (counter == 5) {
                        ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM6 = null;
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
                if (counter == 0) {
                    ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM1 = null;
                }
                if (counter == 1) {
                    ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM2 = null;
                }
                if (counter == 2) {
                    ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM3 = null;
                }
                if (counter == 3) {
                    ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM4 = null;
                }
                if (counter == 4) {
                    ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM5 = null;
                }
                if (counter == 5) {
                    ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM6 = null;
                }
            } finally {
                try { is.close(); } catch(Throwable t) {
                    if (counter == 0) {
                        ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM1 = null;
                    }
                    if (counter == 1) {
                        ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM2 = null;
                    }
                    if (counter == 2) {
                        ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM3 = null;
                    }
                    if (counter == 3) {
                        ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM4 = null;
                    }
                    if (counter == 4) {
                        ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM5 = null;
                    }
                    if (counter == 5) {
                        ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM6 = null;
                    }
                }
                try { con.disconnect(); } catch(Throwable t) {
                    if (counter == 0) {
                        ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM1 = null;
                    }
                    if (counter == 1) {
                        ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM2 = null;
                    }
                    if (counter == 2) {
                        ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM3 = null;
                    }
                    if (counter == 3) {
                        ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM4 = null;
                    }
                    if (counter == 4) {
                        ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM5 = null;
                    }
                    if (counter == 5) {
                        ModelWeatherNOAA.RadarImagesCache.AnimatedRadarImageArrayM6 = null;
                    }
                }
            }
            counter++;
        }

    }

//end
}