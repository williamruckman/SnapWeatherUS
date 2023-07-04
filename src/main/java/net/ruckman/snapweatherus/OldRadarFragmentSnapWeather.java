package net.ruckman.snapweatherus;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import net.ruckman.snapweatherus.NOAAWeatherAPI.ModelWeatherNOAA;
import java.util.Timer;
import java.util.TimerTask;

public class OldRadarFragmentSnapWeather extends Fragment {

    public static Bitmap radarImage;
    public static ImageView radarimgView;
    public static ImageView radarimgViewL0;
    public static ImageView radarimgViewL1;
    public static ImageView radarimgViewL2;
    public static ImageView radarimgViewL3;
    public static ImageView radarimgViewL4;
    public static ImageView radarimgViewL5;
    public static  ImageView radarimgViewL6;
    public static ImageView radarimgViewL7;
    public static ImageView radarimgViewM1;
    public static ImageView radarimgViewM2;
    public static ImageView radarimgViewM3;
    public static ImageView radarimgViewM4;
    public static ImageView radarimgViewM5;
    public static ImageView radarimgViewM6;
    public static Button radarAnimationButton;
    public static ProgressBar radarAnimationProgressBar;
    public static TextView radarAnimationStatusText;
    RelativeLayout currentRadarLayout;
    MenuItem actionL0;
    MenuItem actionL2;
    MenuItem actionL3;
    MenuItem actionL4;
    MenuItem actionL5;
    MenuItem actionL6;
    MenuItem actionL7;
    MenuItem actionSettings;
    MenuItem actionRefresh;
    MenuItem actionAnimate;
    MenuItem actionKeepScreenOn;

    TextView radarstationTextView;
    public static ProgressBar radarProgressBar;
    public static Boolean IsAnimated=false;
    public static Boolean WasAnimated=false;
    Timer timer;

    ScaleGestureDetector mScaleGestureDetector;
    float mScaleFactor = 1.0f;

    private static final int INVALID_POINTER_ID = -1;

    private float mPosX;
    private float mPosY;

    private float mLastTouchX;
    private float mLastTouchY;
    private float mLastGestureX;
    private float mLastGestureY;
    private int mActivePointerId = INVALID_POINTER_ID;
    private float centerX;
    private float centerY;

    private float trackerX;
    private float trackerY;
    private float trackerZ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        Log.d("OnCreateFrag","Called");
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("OnCreateViewFrag","Called");

        //TODO Inflate Layout
        View rootView = inflater.inflate(R.layout.fragment_radar_snapweather, container, false);

        radarstationTextView = rootView.findViewById(R.id.radarLoc);
        radarstationTextView.setText(ModelWeatherNOAA.WeatherPoints.radarstation);

        currentRadarLayout = rootView.findViewById(R.id.currentRadarLayout);

        //TODO Setup Image Views
        radarimgView = rootView.findViewById(R.id.radarImageBase);
        radarimgViewL0 = rootView.findViewById(R.id.radarImageL0);
        radarimgViewL1 = rootView.findViewById(R.id.radarImageL1);
        radarimgViewL2 = rootView.findViewById(R.id.radarImageL2);
        radarimgViewL3 = rootView.findViewById(R.id.radarImageL3);
        radarimgViewL4 = rootView.findViewById(R.id.radarImageL4);
        radarimgViewL5 = rootView.findViewById(R.id.radarImageL5);
        radarimgViewL6 = rootView.findViewById(R.id.radarImageL6);
        radarimgViewL7 = rootView.findViewById(R.id.radarImageL7);

        if(ModelWeatherNOAA.Settings.RadarL0) {
            radarimgViewL0.setVisibility(View.VISIBLE);
        } else {
            radarimgViewL0.setVisibility(View.INVISIBLE);
        }

        if(ModelWeatherNOAA.Settings.RadarL2) {
            radarimgViewL2.setVisibility(View.VISIBLE);
        } else {
            radarimgViewL2.setVisibility(View.INVISIBLE);
        }

        if(ModelWeatherNOAA.Settings.RadarL3) {
            radarimgViewL3.setVisibility(View.VISIBLE);
        } else {
            radarimgViewL3.setVisibility(View.INVISIBLE);
        }

        if(ModelWeatherNOAA.Settings.RadarL4) {
            radarimgViewL4.setVisibility(View.VISIBLE);
        } else {
            radarimgViewL4.setVisibility(View.INVISIBLE);
        }

        if(ModelWeatherNOAA.Settings.RadarL5) {
            radarimgViewL5.setVisibility(View.VISIBLE);
        } else {
            radarimgViewL5.setVisibility(View.INVISIBLE);
        }

        if(ModelWeatherNOAA.Settings.RadarL6) {
            radarimgViewL6.setVisibility(View.VISIBLE);
        } else {
            radarimgViewL6.setVisibility(View.INVISIBLE);
        }

        if(ModelWeatherNOAA.Settings.RadarL7) {
            radarimgViewL7.setVisibility(View.VISIBLE);
        } else {
            radarimgViewL7.setVisibility(View.INVISIBLE);
        }

        radarimgViewM1 = rootView.findViewById(R.id.radarImageAnimatedM1);
        radarimgViewM2 = rootView.findViewById(R.id.radarImageAnimatedM2);
        radarimgViewM3 = rootView.findViewById(R.id.radarImageAnimatedM3);
        radarimgViewM4 = rootView.findViewById(R.id.radarImageAnimatedM4);
        radarimgViewM5 = rootView.findViewById(R.id.radarImageAnimatedM5);
        radarimgViewM6 = rootView.findViewById(R.id.radarImageAnimatedM6);
        radarAnimationButton = rootView.findViewById(R.id.buttonAnimate);
        radarAnimationProgressBar = rootView.findViewById(R.id.progressBarAnimate);
        radarAnimationStatusText = rootView.findViewById(R.id.textProgressBarStatus);

        radarImage = BitmapFactory.decodeResource(this.getResources(), R.raw.r0base);
        radarimgView.setImageBitmap(Bitmap.createBitmap(radarImage));

        centerX = currentRadarLayout.getX() + currentRadarLayout.getWidth() / 2;
        centerY = currentRadarLayout.getY() + currentRadarLayout.getHeight() / 2;

        trackerX =0;
        trackerY =0;
        trackerZ=1.0f;

        //Begin progress bar for loading
        radarProgressBar = rootView.findViewById(R.id.radarloadingPanel);
        radarProgressBar.setVisibility(View.VISIBLE);

        radarAnimationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                if (IsAnimated) {
                    IsAnimated = false;
                    radarAnimationButton.setText("    Toggle Animation On    ");
                } else {
                    IsAnimated = true;
                    radarAnimationButton.setText("    Toggle Animation Off    ");
                    AnimateRadarTask animatedradar = new AnimateRadarTask();
                    animatedradar.execute(getContext());
                }
            }
        });


        rootView.findViewById(R.id.currentRadarLayout).setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d("MotionAction", String.valueOf(event));
                    mScaleGestureDetector.onTouchEvent(event);

                    final int action = event.getAction();
                    switch (action & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN: {
                            if (!mScaleGestureDetector.isInProgress()) {
                                final float x = event.getX();
                                final float y = event.getY();

                                mLastTouchX = x;
                                mLastTouchY = y;
                                mActivePointerId = event.getPointerId(0);
                            }
                            break;
                        }
                        case MotionEvent.ACTION_POINTER_1_DOWN: {
                            if (mScaleGestureDetector.isInProgress()) {
                                final float gx = mScaleGestureDetector.getFocusX();
                                final float gy = mScaleGestureDetector.getFocusY();
                                mLastGestureX = gx;
                                mLastGestureY = gy;
                            }
                            break;
                        }
                        case MotionEvent.ACTION_MOVE: {

                            // Only move if the ScaleGestureDetector isn't processing a gesture.
                            if (!mScaleGestureDetector.isInProgress()) {
                                final int pointerIndex = event.findPointerIndex(mActivePointerId);
                                final float x = event.getX(pointerIndex);
                                final float y = event.getY(pointerIndex);

                                final float dx = x - mLastTouchX;
                                final float dy = y - mLastTouchY;

                                mPosX += dx;
                                mPosY += dy;

                                //half speed /2
                                int scrollByX = (int) dx/2;
                                int scrollByY = (int) dy/2;

                                trackerX = trackerX + -scrollByX;
                                trackerY = trackerY + -scrollByY;
                                Log.d("XposCounter", String.valueOf(trackerX));
                                Log.d("YposCounter", String.valueOf(trackerY));
                                Log.d("LayoutSizeX", String.valueOf(currentRadarLayout.getX() + currentRadarLayout.getWidth() / 2));
                                Log.d("LayoutSizeY", String.valueOf(currentRadarLayout.getY() + currentRadarLayout.getHeight() / 2));

                                float LayoutSizeX = (currentRadarLayout.getX() + currentRadarLayout.getWidth() / 2)/1.54f;
                                float LayoutSizeY = (currentRadarLayout.getY() + currentRadarLayout.getHeight() / 2)/2.56f;

                                if (trackerZ != 1.0f) {
                                if (trackerX <= LayoutSizeX && trackerX >= -LayoutSizeX && trackerY <= LayoutSizeY && trackerY >= -LayoutSizeY) {
                                        radarimgView.scrollBy(-scrollByX, -scrollByY);
                                        radarimgViewL0.scrollBy(-scrollByX, -scrollByY);
                                        radarimgViewL1.scrollBy(-scrollByX, -scrollByY);
                                        radarimgViewL2.scrollBy(-scrollByX, -scrollByY);
                                        radarimgViewL3.scrollBy(-scrollByX, -scrollByY);
                                        radarimgViewL4.scrollBy(-scrollByX, -scrollByY);
                                        radarimgViewL5.scrollBy(-scrollByX, -scrollByY);
                                        radarimgViewL6.scrollBy(-scrollByX, -scrollByY);
                                        //radarimgViewL7.scrollBy(scrollByX, scrollByY);
                                        radarimgViewM1.scrollBy(-scrollByX, -scrollByY);
                                        radarimgViewM2.scrollBy(-scrollByX, -scrollByY);
                                        radarimgViewM3.scrollBy(-scrollByX, -scrollByY);
                                        radarimgViewM4.scrollBy(-scrollByX, -scrollByY);
                                        radarimgViewM5.scrollBy(-scrollByX, -scrollByY);
                                        radarimgViewM6.scrollBy(-scrollByX, -scrollByY);

                                } else {
                                    if (trackerY > LayoutSizeY) {
                                        trackerY = LayoutSizeY;
                                        radarimgView.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewL0.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewL1.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewL2.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewL3.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewL4.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewL5.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewL6.scrollTo((int)trackerX,(int)trackerY);
                                        //radarimgViewL7.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewM1.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewM2.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewM3.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewM4.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewM5.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewM6.scrollTo((int)trackerX,(int)trackerY);

                                    }
                                    if (trackerY < -LayoutSizeY) {
                                        trackerY = -LayoutSizeY;
                                        radarimgView.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewL0.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewL1.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewL2.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewL3.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewL4.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewL5.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewL6.scrollTo((int)trackerX,(int)trackerY);
                                        //radarimgViewL7.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewM1.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewM2.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewM3.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewM4.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewM5.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewM6.scrollTo((int)trackerX,(int)trackerY);
                                    }
                                    if (trackerX > LayoutSizeX) {
                                        trackerX = LayoutSizeX;
                                        radarimgView.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewL0.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewL1.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewL2.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewL3.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewL4.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewL5.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewL6.scrollTo((int)trackerX,(int)trackerY);
                                        //radarimgViewL7.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewM1.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewM2.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewM3.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewM4.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewM5.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewM6.scrollTo((int)trackerX,(int)trackerY);
                                    }
                                    if (trackerX < -LayoutSizeX) {
                                        trackerX = -LayoutSizeX;
                                        radarimgView.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewL0.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewL1.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewL2.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewL3.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewL4.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewL5.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewL6.scrollTo((int)trackerX,(int)trackerY);
                                        //radarimgViewL7.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewM1.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewM2.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewM3.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewM4.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewM5.scrollTo((int)trackerX,(int)trackerY);
                                        radarimgViewM6.scrollTo((int)trackerX,(int)trackerY);
                                    }
                                }
                            } else {
                                    trackerX =0;
                                    trackerY =0;
                                }

                                mLastTouchX = x;
                                mLastTouchY = y;
                            }


                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            mActivePointerId = INVALID_POINTER_ID;
                            break;
                        }
                        case MotionEvent.ACTION_CANCEL: {
                            mActivePointerId = INVALID_POINTER_ID;
                            break;
                        }
                        case MotionEvent.ACTION_POINTER_UP: {

                            final int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
                                    >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                            final int pointerId = event.getPointerId(pointerIndex);
                            if (pointerId == mActivePointerId) {
                                // This was our active pointer going up. Choose a new
                                // active pointer and adjust accordingly.
                                final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                                mLastTouchX = event.getX(newPointerIndex);
                                mLastTouchY = event.getY(newPointerIndex);
                                mActivePointerId = event.getPointerId(newPointerIndex);
                            }
                            else{
                                final int tempPointerIndex = event.findPointerIndex(mActivePointerId);
                                mLastTouchX = event.getX(tempPointerIndex);
                                mLastTouchY = event.getY(tempPointerIndex);
                            }

                            break;
                        }
                    }

                    return true;
                }
            });


        //Return the fragments view
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("OnPauseRadarFrag","Called");
        IsAnimated = false;
        OldRadarFragmentSnapWeather.radarAnimationButton.setText("    Toggle Animation On    ");
        timer.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("OnResumeRadarFrag","Called");
        timer = new Timer();
        setRepeatingAsyncTask();

    }

    //Create the action bar menus
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.

        Log.d("OptionsMenu", "onCreateOptionsMenu() called");
        inflater.inflate(R.menu.menu_radar, menu);
        super.onCreateOptionsMenu(menu, inflater);
        actionL0 = menu.findItem(R.id.action_L0);
        actionL2 = menu.findItem(R.id.action_L2);
        actionL3 = menu.findItem(R.id.action_L3);
        actionL4 = menu.findItem(R.id.action_L4);
        actionL5 = menu.findItem(R.id.action_L5);
        actionL6 = menu.findItem(R.id.action_L6);
        actionL7 = menu.findItem(R.id.action_L7);
        actionSettings = menu.findItem(R.id.action_settings);
        actionRefresh = menu.findItem(R.id.action_Refresh);
        actionAnimate = menu.findItem(R.id.action_animate);
        actionKeepScreenOn = menu.findItem(R.id.action_KeepScreenOn);

    }

    //Run these tasks when options in the action bar are selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Log.d("OptionsMenu", "onOptionsItemsSelected() called");

        int id = item.getItemId();

        if (id == R.id.action_Refresh) {
            if (IsAnimated) {
                //Cancel animation if running
                AnimateRadarTask animatedradar = new AnimateRadarTask();
                animatedradar.cancel();

                WasAnimated=true;

                //Get Radar Images Task
                GetRadarImagesTask radarimagestask = new GetRadarImagesTask();
                radarimagestask.execute(getContext());

            } else {
                //Get Radar Images Task
                GetRadarImagesTask radarimagestask = new GetRadarImagesTask();
                radarimagestask.execute(getContext());
            }

            return true;
        }

        if (id == R.id.action_animate) {
            if (IsAnimated) {
                IsAnimated = false;
            } else {
                IsAnimated = true;
                AnimateRadarTask animatedradar = new AnimateRadarTask();
                animatedradar.execute(getContext());
            }
            return true;
        }

        if (id == R.id.action_KeepScreenOn) {
            if (radarimgView.getKeepScreenOn()) {
                Log.d("Screen","KeepingOff");
                radarimgView.setKeepScreenOn(false);
                actionKeepScreenOn.setTitle("Keep Screen On");
            } else {
                Log.d("Screen","KeepingOn");
                radarimgView.setKeepScreenOn(true);
                actionKeepScreenOn.setTitle("Allow Screen Off");
            }
            return true;
        }

        if (id == R.id.action_L0) {
            if (radarimgViewL0.getVisibility() == View.VISIBLE ) {
                radarimgViewL0.setVisibility(View.INVISIBLE);
            } else {
                radarimgViewL0.setVisibility(View.VISIBLE);
            }
            return true;
        }
        if (id == R.id.action_L2) {
            if (radarimgViewL2.getVisibility() == View.VISIBLE ) {
                radarimgViewL2.setVisibility(View.INVISIBLE);
            } else {
                radarimgViewL2.setVisibility(View.VISIBLE);
            }
            return true;
        }
        if (id == R.id.action_L3) {
            if (radarimgViewL3.getVisibility() == View.VISIBLE ) {
                radarimgViewL3.setVisibility(View.INVISIBLE);
            } else {
                radarimgViewL3.setVisibility(View.VISIBLE);
            }
            return true;
        }
        if (id == R.id.action_L4) {
            if (radarimgViewL4.getVisibility() == View.VISIBLE ) {
                radarimgViewL4.setVisibility(View.INVISIBLE);
            } else {
                radarimgViewL4.setVisibility(View.VISIBLE);
            }
            return true;
        }
        if (id == R.id.action_L5) {
            if (radarimgViewL5.getVisibility() == View.VISIBLE ) {
                radarimgViewL5.setVisibility(View.INVISIBLE);
            } else {
                radarimgViewL5.setVisibility(View.VISIBLE);
            }
            return true;
        }
        if (id == R.id.action_L6) {
            if (radarimgViewL6.getVisibility() == View.VISIBLE ) {
                radarimgViewL6.setVisibility(View.INVISIBLE);
            } else {
                radarimgViewL6.setVisibility(View.VISIBLE);
            }
            return true;
        }
        if (id == R.id.action_L7) {
            if (radarimgViewL7.getVisibility() == View.VISIBLE ) {
                radarimgViewL7.setVisibility(View.INVISIBLE);
            } else {
                radarimgViewL7.setVisibility(View.VISIBLE);
            }
            return true;
        }

        if (id == R.id.action_settings) {
            Log.d("OptionsMenu", "Settings application called");
            //setup transaction for click and allow to go back and pop stack
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new SettingsFragmentSnapWeather(), "RadarTask")
                    .addToBackStack(null)
                    .commit();
        }

        return super.onOptionsItemSelected(item);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(1.0f,Math.min(mScaleFactor, 3.0f));
            radarimgView.setScaleX(mScaleFactor);
            radarimgView.setScaleY(mScaleFactor);
            radarimgViewL0.setScaleX(mScaleFactor);
            radarimgViewL0.setScaleY(mScaleFactor);
            radarimgViewL1.setScaleX(mScaleFactor);
            radarimgViewL1.setScaleY(mScaleFactor);
            radarimgViewL2.setScaleX(mScaleFactor);
            radarimgViewL2.setScaleY(mScaleFactor);
            radarimgViewL3.setScaleX(mScaleFactor);
            radarimgViewL3.setScaleY(mScaleFactor);
            radarimgViewL4.setScaleX(mScaleFactor);
            radarimgViewL4.setScaleY(mScaleFactor);
            radarimgViewL5.setScaleX(mScaleFactor);
            radarimgViewL5.setScaleY(mScaleFactor);
            radarimgViewL6.setScaleX(mScaleFactor);
            radarimgViewL6.setScaleY(mScaleFactor);
            //radarimgViewL7.setScaleX(mScaleFactor);
            //radarimgViewL7.setScaleY(mScaleFactor);
            radarimgViewM1.setScaleX(mScaleFactor);
            radarimgViewM1.setScaleY(mScaleFactor);
            radarimgViewM2.setScaleX(mScaleFactor);
            radarimgViewM2.setScaleY(mScaleFactor);
            radarimgViewM3.setScaleX(mScaleFactor);
            radarimgViewM3.setScaleY(mScaleFactor);
            radarimgViewM4.setScaleX(mScaleFactor);
            radarimgViewM4.setScaleY(mScaleFactor);
            radarimgViewM5.setScaleX(mScaleFactor);
            radarimgViewM5.setScaleY(mScaleFactor);
            radarimgViewM6.setScaleX(mScaleFactor);
            radarimgViewM6.setScaleY(mScaleFactor);
            if (mScaleFactor <= 1.0) {
                radarimgView.scrollTo((int)centerX,(int)centerY);
                radarimgViewL0.scrollTo((int)centerX,(int)centerY);
                radarimgViewL1.scrollTo((int)centerX,(int)centerY);
                radarimgViewL2.scrollTo((int)centerX,(int)centerY);
                radarimgViewL3.scrollTo((int)centerX,(int)centerY);
                radarimgViewL4.scrollTo((int)centerX,(int)centerY);
                radarimgViewL5.scrollTo((int)centerX,(int)centerY);
                radarimgViewL6.scrollTo((int)centerX,(int)centerY);
                //radarimgViewL7.scrollTo((int)centerX,(int)centerY);
                radarimgViewM1.scrollTo((int)centerX,(int)centerY);
                radarimgViewM2.scrollTo((int)centerX,(int)centerY);
                radarimgViewM3.scrollTo((int)centerX,(int)centerY);
                radarimgViewM4.scrollTo((int)centerX,(int)centerY);
                radarimgViewM5.scrollTo((int)centerX,(int)centerY);
                radarimgViewM6.scrollTo((int)centerX,(int)centerY);

                trackerX =0;
                trackerY =0;
                trackerZ=1.0f;
            }
            trackerZ=mScaleFactor;
            return true;
        }
    }

    //Refresh Radar Automatically
    private void setRepeatingAsyncTask() {

        final Handler handler = new Handler();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {

                            if (IsAnimated) {
                                //Cancel animation if running
                                AnimateRadarTask animatedradar = new AnimateRadarTask();
                                animatedradar.cancel();
                                OldRadarFragmentSnapWeather.radarAnimationButton.setText("    Toggle Animation Off    ");
                                WasAnimated=true;

                                //Get Radar Images Task
                                GetRadarImagesTask radarimagestask = new GetRadarImagesTask();
                                radarimagestask.execute(getContext());

                            } else {
                                //Get Radar Images Task
                                GetRadarImagesTask radarimagestask = new GetRadarImagesTask();
                                radarimagestask.execute(getContext());
                            }

                        } catch (Exception e) {
                            // error, do something
                            Log.d("TimerTaskError", String.valueOf(e));
                        }
                    }
                });
            }
        };

        try {
            timer.schedule(task, 0, 300*1000);  // interval of five minutes
        } catch (Exception e) {
            Log.d("TimerTaskError", String.valueOf(e));
        }

    }

}

