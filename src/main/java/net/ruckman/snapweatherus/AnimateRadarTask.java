package net.ruckman.snapweatherus;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

public class AnimateRadarTask extends AsyncTask<Object, Void, Void> {

    @SuppressLint("WrongThread")
    @Override
    protected Void doInBackground(Object[] params) {

        while (OldRadarFragmentSnapWeather.IsAnimated) {

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    OldRadarFragmentSnapWeather.radarimgViewL1.setVisibility(View.INVISIBLE);
                    OldRadarFragmentSnapWeather.radarimgViewM1.setVisibility(View.INVISIBLE);
                    OldRadarFragmentSnapWeather.radarimgViewM2.setVisibility(View.INVISIBLE);
                    OldRadarFragmentSnapWeather.radarimgViewM3.setVisibility(View.INVISIBLE);
                    OldRadarFragmentSnapWeather.radarimgViewM4.setVisibility(View.INVISIBLE);
                    OldRadarFragmentSnapWeather.radarimgViewM5.setVisibility(View.INVISIBLE);
                    OldRadarFragmentSnapWeather.radarimgViewM6.setVisibility(View.VISIBLE);
                    OldRadarFragmentSnapWeather.radarAnimationProgressBar.setProgress(0);
                    OldRadarFragmentSnapWeather.radarAnimationStatusText.setText("(Displayed: -25min)");
                }
            });

            if (!OldRadarFragmentSnapWeather.IsAnimated) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        OldRadarFragmentSnapWeather.radarimgViewL1.setVisibility(View.VISIBLE);
                        OldRadarFragmentSnapWeather.radarimgViewM1.setVisibility(View.INVISIBLE);
                        OldRadarFragmentSnapWeather.radarimgViewM2.setVisibility(View.INVISIBLE);
                        OldRadarFragmentSnapWeather.radarimgViewM3.setVisibility(View.INVISIBLE);
                        OldRadarFragmentSnapWeather.radarimgViewM4.setVisibility(View.INVISIBLE);
                        OldRadarFragmentSnapWeather.radarimgViewM5.setVisibility(View.INVISIBLE);
                        OldRadarFragmentSnapWeather.radarimgViewM6.setVisibility(View.INVISIBLE);
                        OldRadarFragmentSnapWeather.radarAnimationProgressBar.setProgress(100);
                        OldRadarFragmentSnapWeather.radarAnimationStatusText.setText("(Displayed: Now)");
                    }
                });

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        OldRadarFragmentSnapWeather.IsAnimated=false;
                        OldRadarFragmentSnapWeather.radarAnimationProgressBar.setProgress(100);
                        OldRadarFragmentSnapWeather.radarAnimationStatusText.setText("(Displayed: Now)");
                        OldRadarFragmentSnapWeather.radarAnimationButton.setText("    Toggle Animation On    ");
                    }
                });
                return null;

            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d("AnimateRadarTask","Sleep failure: " + e);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        OldRadarFragmentSnapWeather.IsAnimated=false;
                        OldRadarFragmentSnapWeather.radarAnimationProgressBar.setProgress(100);
                        OldRadarFragmentSnapWeather.radarAnimationStatusText.setText("(Displayed: Now)");
                        OldRadarFragmentSnapWeather.radarAnimationButton.setText("    Toggle Animation On    ");
                    }
                });
                return null;
            }

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    OldRadarFragmentSnapWeather.radarimgViewL1.setVisibility(View.INVISIBLE);
                    OldRadarFragmentSnapWeather.radarimgViewM1.setVisibility(View.INVISIBLE);
                    OldRadarFragmentSnapWeather.radarimgViewM2.setVisibility(View.INVISIBLE);
                    OldRadarFragmentSnapWeather.radarimgViewM3.setVisibility(View.INVISIBLE);
                    OldRadarFragmentSnapWeather.radarimgViewM4.setVisibility(View.INVISIBLE);
                    OldRadarFragmentSnapWeather.radarimgViewM5.setVisibility(View.VISIBLE);
                    OldRadarFragmentSnapWeather.radarimgViewM6.setVisibility(View.INVISIBLE);
                    OldRadarFragmentSnapWeather.radarAnimationProgressBar.setProgress(20);
                    OldRadarFragmentSnapWeather.radarAnimationStatusText.setText("(Displayed: -20min)");

                }
            });
            if (!OldRadarFragmentSnapWeather.IsAnimated) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        OldRadarFragmentSnapWeather.radarimgViewL1.setVisibility(View.VISIBLE);
                        OldRadarFragmentSnapWeather.radarimgViewM1.setVisibility(View.INVISIBLE);
                        OldRadarFragmentSnapWeather.radarimgViewM2.setVisibility(View.INVISIBLE);
                        OldRadarFragmentSnapWeather.radarimgViewM3.setVisibility(View.INVISIBLE);
                        OldRadarFragmentSnapWeather.radarimgViewM4.setVisibility(View.INVISIBLE);
                        OldRadarFragmentSnapWeather.radarimgViewM5.setVisibility(View.INVISIBLE);
                        OldRadarFragmentSnapWeather.radarimgViewM6.setVisibility(View.INVISIBLE);
                        OldRadarFragmentSnapWeather.radarAnimationProgressBar.setProgress(100);
                        OldRadarFragmentSnapWeather.radarAnimationStatusText.setText("(Displayed: Now)");
                    }
                });

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        OldRadarFragmentSnapWeather.IsAnimated=false;
                        OldRadarFragmentSnapWeather.radarAnimationProgressBar.setProgress(100);
                        OldRadarFragmentSnapWeather.radarAnimationStatusText.setText("(Displayed: Now)");
                        OldRadarFragmentSnapWeather.radarAnimationButton.setText("    Toggle Animation On    ");
                    }
                });
                return null;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d("AnimateRadarTask","Sleep failure: " + e);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        OldRadarFragmentSnapWeather.IsAnimated=false;
                        OldRadarFragmentSnapWeather.radarAnimationProgressBar.setProgress(100);
                        OldRadarFragmentSnapWeather.radarAnimationStatusText.setText("(Displayed: Now)");
                        OldRadarFragmentSnapWeather.radarAnimationButton.setText("    Toggle Animation On    ");
                    }
                });
                return null;
            }

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    OldRadarFragmentSnapWeather.radarimgViewL1.setVisibility(View.INVISIBLE);
                    OldRadarFragmentSnapWeather.radarimgViewM1.setVisibility(View.INVISIBLE);
                    OldRadarFragmentSnapWeather.radarimgViewM2.setVisibility(View.INVISIBLE);
                    OldRadarFragmentSnapWeather.radarimgViewM3.setVisibility(View.INVISIBLE);
                    OldRadarFragmentSnapWeather.radarimgViewM4.setVisibility(View.VISIBLE);
                    OldRadarFragmentSnapWeather.radarimgViewM5.setVisibility(View.INVISIBLE);
                    OldRadarFragmentSnapWeather.radarimgViewM6.setVisibility(View.INVISIBLE);
                    OldRadarFragmentSnapWeather.radarAnimationProgressBar.setProgress(40);
                    OldRadarFragmentSnapWeather.radarAnimationStatusText.setText("(Displayed: -15min)");

                }
            });
            if (!OldRadarFragmentSnapWeather.IsAnimated) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        OldRadarFragmentSnapWeather.radarimgViewL1.setVisibility(View.VISIBLE);
                        OldRadarFragmentSnapWeather.radarimgViewM1.setVisibility(View.INVISIBLE);
                        OldRadarFragmentSnapWeather.radarimgViewM2.setVisibility(View.INVISIBLE);
                        OldRadarFragmentSnapWeather.radarimgViewM3.setVisibility(View.INVISIBLE);
                        OldRadarFragmentSnapWeather.radarimgViewM4.setVisibility(View.INVISIBLE);
                        OldRadarFragmentSnapWeather.radarimgViewM5.setVisibility(View.INVISIBLE);
                        OldRadarFragmentSnapWeather.radarimgViewM6.setVisibility(View.INVISIBLE);
                        OldRadarFragmentSnapWeather.radarAnimationProgressBar.setProgress(100);
                        OldRadarFragmentSnapWeather.radarAnimationStatusText.setText("(Displayed: Now)");
                    }
                });

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        OldRadarFragmentSnapWeather.IsAnimated=false;
                        OldRadarFragmentSnapWeather.radarAnimationProgressBar.setProgress(100);
                        OldRadarFragmentSnapWeather.radarAnimationStatusText.setText("(Displayed: Now)");
                        OldRadarFragmentSnapWeather.radarAnimationButton.setText("    Toggle Animation On    ");
                    }
                });
                return null;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d("AnimateRadarTask","Sleep failure: " + e);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        OldRadarFragmentSnapWeather.IsAnimated=false;
                        OldRadarFragmentSnapWeather.radarAnimationProgressBar.setProgress(100);
                        OldRadarFragmentSnapWeather.radarAnimationStatusText.setText("(Displayed: Now)");
                        OldRadarFragmentSnapWeather.radarAnimationButton.setText("    Toggle Animation On    ");
                    }
                });
                return null;
            }

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    OldRadarFragmentSnapWeather.radarimgViewL1.setVisibility(View.INVISIBLE);
                    OldRadarFragmentSnapWeather.radarimgViewM1.setVisibility(View.INVISIBLE);
                    OldRadarFragmentSnapWeather.radarimgViewM2.setVisibility(View.INVISIBLE);
                    OldRadarFragmentSnapWeather.radarimgViewM3.setVisibility(View.VISIBLE);
                    OldRadarFragmentSnapWeather.radarimgViewM4.setVisibility(View.INVISIBLE);
                    OldRadarFragmentSnapWeather.radarimgViewM5.setVisibility(View.INVISIBLE);
                    OldRadarFragmentSnapWeather.radarimgViewM6.setVisibility(View.INVISIBLE);
                    OldRadarFragmentSnapWeather.radarAnimationProgressBar.setProgress(60);
                    OldRadarFragmentSnapWeather.radarAnimationStatusText.setText("(Displayed: -10min)");
                }
            });
            if (!OldRadarFragmentSnapWeather.IsAnimated) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        OldRadarFragmentSnapWeather.radarimgViewL1.setVisibility(View.VISIBLE);
                        OldRadarFragmentSnapWeather.radarimgViewM1.setVisibility(View.INVISIBLE);
                        OldRadarFragmentSnapWeather.radarimgViewM2.setVisibility(View.INVISIBLE);
                        OldRadarFragmentSnapWeather.radarimgViewM3.setVisibility(View.INVISIBLE);
                        OldRadarFragmentSnapWeather.radarimgViewM4.setVisibility(View.INVISIBLE);
                        OldRadarFragmentSnapWeather.radarimgViewM5.setVisibility(View.INVISIBLE);
                        OldRadarFragmentSnapWeather.radarimgViewM6.setVisibility(View.INVISIBLE);
                        OldRadarFragmentSnapWeather.radarAnimationProgressBar.setProgress(100);
                        OldRadarFragmentSnapWeather.radarAnimationStatusText.setText("(Displayed: Now)");
                    }
                });

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        OldRadarFragmentSnapWeather.IsAnimated=false;
                        OldRadarFragmentSnapWeather.radarAnimationProgressBar.setProgress(100);
                        OldRadarFragmentSnapWeather.radarAnimationStatusText.setText("(Displayed: Now)");
                        OldRadarFragmentSnapWeather.radarAnimationButton.setText("    Toggle Animation On    ");
                    }
                });
                return null;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d("AnimateRadarTask","Sleep failure: " + e);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        OldRadarFragmentSnapWeather.IsAnimated=false;
                        OldRadarFragmentSnapWeather.radarAnimationProgressBar.setProgress(100);
                        OldRadarFragmentSnapWeather.radarAnimationStatusText.setText("(Displayed: Now)");
                        OldRadarFragmentSnapWeather.radarAnimationButton.setText("    Toggle Animation On    ");
                    }
                });
                return null;
            }

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    OldRadarFragmentSnapWeather.radarimgViewL1.setVisibility(View.INVISIBLE);
                    OldRadarFragmentSnapWeather.radarimgViewM1.setVisibility(View.INVISIBLE);
                    OldRadarFragmentSnapWeather.radarimgViewM2.setVisibility(View.VISIBLE);
                    OldRadarFragmentSnapWeather.radarimgViewM3.setVisibility(View.INVISIBLE);
                    OldRadarFragmentSnapWeather.radarimgViewM4.setVisibility(View.INVISIBLE);
                    OldRadarFragmentSnapWeather.radarimgViewM5.setVisibility(View.INVISIBLE);
                    OldRadarFragmentSnapWeather.radarimgViewM6.setVisibility(View.INVISIBLE);
                    OldRadarFragmentSnapWeather.radarAnimationProgressBar.setProgress(80);
                    OldRadarFragmentSnapWeather.radarAnimationStatusText.setText("(Displayed: -5min)");
                }
            });
            if (!OldRadarFragmentSnapWeather.IsAnimated) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        OldRadarFragmentSnapWeather.radarimgViewL1.setVisibility(View.VISIBLE);
                        OldRadarFragmentSnapWeather.radarimgViewM1.setVisibility(View.INVISIBLE);
                        OldRadarFragmentSnapWeather.radarimgViewM2.setVisibility(View.INVISIBLE);
                        OldRadarFragmentSnapWeather.radarimgViewM3.setVisibility(View.INVISIBLE);
                        OldRadarFragmentSnapWeather.radarimgViewM4.setVisibility(View.INVISIBLE);
                        OldRadarFragmentSnapWeather.radarimgViewM5.setVisibility(View.INVISIBLE);
                        OldRadarFragmentSnapWeather.radarimgViewM6.setVisibility(View.INVISIBLE);
                        OldRadarFragmentSnapWeather.radarAnimationProgressBar.setProgress(100);
                        OldRadarFragmentSnapWeather.radarAnimationStatusText.setText("(Displayed: Now)");
                    }
                });

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        OldRadarFragmentSnapWeather.IsAnimated=false;
                        OldRadarFragmentSnapWeather.radarAnimationProgressBar.setProgress(100);
                        OldRadarFragmentSnapWeather.radarAnimationStatusText.setText("(Displayed: Now)");
                        OldRadarFragmentSnapWeather.radarAnimationButton.setText("    Toggle Animation On    ");
                    }
                });
                return null;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d("AnimateRadarTask","Sleep failure: " + e);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        OldRadarFragmentSnapWeather.IsAnimated=false;
                        OldRadarFragmentSnapWeather.radarAnimationProgressBar.setProgress(100);
                        OldRadarFragmentSnapWeather.radarAnimationStatusText.setText("(Displayed: Now)");
                        OldRadarFragmentSnapWeather.radarAnimationButton.setText("    Toggle Animation On    ");
                    }
                });
                return null;
            }

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    OldRadarFragmentSnapWeather.radarimgViewL1.setVisibility(View.INVISIBLE);
                    OldRadarFragmentSnapWeather.radarimgViewM1.setVisibility(View.VISIBLE);
                    OldRadarFragmentSnapWeather.radarimgViewM2.setVisibility(View.INVISIBLE);
                    OldRadarFragmentSnapWeather.radarimgViewM3.setVisibility(View.INVISIBLE);
                    OldRadarFragmentSnapWeather.radarimgViewM4.setVisibility(View.INVISIBLE);
                    OldRadarFragmentSnapWeather.radarimgViewM5.setVisibility(View.INVISIBLE);
                    OldRadarFragmentSnapWeather.radarimgViewM6.setVisibility(View.INVISIBLE);
                    OldRadarFragmentSnapWeather.radarAnimationProgressBar.setProgress(100);
                    OldRadarFragmentSnapWeather.radarAnimationStatusText.setText("(Displayed: Now)");
                }
            });
            if (!OldRadarFragmentSnapWeather.IsAnimated) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        OldRadarFragmentSnapWeather.radarimgViewL1.setVisibility(View.VISIBLE);
                        OldRadarFragmentSnapWeather.radarimgViewM1.setVisibility(View.INVISIBLE);
                        OldRadarFragmentSnapWeather.radarimgViewM2.setVisibility(View.INVISIBLE);
                        OldRadarFragmentSnapWeather.radarimgViewM3.setVisibility(View.INVISIBLE);
                        OldRadarFragmentSnapWeather.radarimgViewM4.setVisibility(View.INVISIBLE);
                        OldRadarFragmentSnapWeather.radarimgViewM5.setVisibility(View.INVISIBLE);
                        OldRadarFragmentSnapWeather.radarimgViewM6.setVisibility(View.INVISIBLE);
                        OldRadarFragmentSnapWeather.radarAnimationProgressBar.setProgress(100);
                        OldRadarFragmentSnapWeather.radarAnimationStatusText.setText("(Displayed: Now)");
                    }
                });

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        OldRadarFragmentSnapWeather.IsAnimated=false;
                        OldRadarFragmentSnapWeather.radarAnimationProgressBar.setProgress(100);
                        OldRadarFragmentSnapWeather.radarAnimationStatusText.setText("(Displayed: Now)");
                        OldRadarFragmentSnapWeather.radarAnimationButton.setText("    Toggle Animation On    ");
                    }
                });
                return null;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d("AnimateRadarTask","Sleep failure: " + e);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        OldRadarFragmentSnapWeather.IsAnimated=false;
                        OldRadarFragmentSnapWeather.radarAnimationProgressBar.setProgress(100);
                        OldRadarFragmentSnapWeather.radarAnimationStatusText.setText("(Displayed: Now)");
                        OldRadarFragmentSnapWeather.radarAnimationButton.setText("    Toggle Animation On    ");
                    }
                });
                return null;
            }

        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                OldRadarFragmentSnapWeather.radarimgViewL1.setVisibility(View.VISIBLE);
                OldRadarFragmentSnapWeather.radarimgViewM1.setVisibility(View.INVISIBLE);
                OldRadarFragmentSnapWeather.radarimgViewM2.setVisibility(View.INVISIBLE);
                OldRadarFragmentSnapWeather.radarimgViewM3.setVisibility(View.INVISIBLE);
                OldRadarFragmentSnapWeather.radarimgViewM4.setVisibility(View.INVISIBLE);
                OldRadarFragmentSnapWeather.radarimgViewM5.setVisibility(View.INVISIBLE);
                OldRadarFragmentSnapWeather.radarimgViewM6.setVisibility(View.INVISIBLE);            }
        });

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                OldRadarFragmentSnapWeather.IsAnimated=false;
                OldRadarFragmentSnapWeather.radarAnimationProgressBar.setProgress(100);
                OldRadarFragmentSnapWeather.radarAnimationStatusText.setText("(Displayed: Now)");
                OldRadarFragmentSnapWeather.radarAnimationButton.setText("    Toggle Animation On    ");
            }
        });

        return null;
    }


    public void cancel() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                OldRadarFragmentSnapWeather.IsAnimated=false;
                OldRadarFragmentSnapWeather.radarAnimationButton.setText("    Toggle Animation On    ");
            }
        });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
