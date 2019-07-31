package com.intel.location.ma.app.kidlock;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Window;

import com.intel.location.indoor.app.kidlock.R;

/**
 * Created by majeffrx on 6/21/2016.
 */
public class loadingScreen extends Activity {


    public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.loading);
        FamilyInfo.getInstance().Initialize(this);
        if (FamilyInfo.getInstance().contains("pin") && !isMyServiceRunning(KidLock.class)) {
            Thread welcomeThread = new Thread() {
                @Override
                public void run() {
                    try {
                        super.run();
                        sleep(2000);  //Delay of 1 seconds
                    } catch (Exception e) {

                    } finally {
                        if (!isNetworkConnected()) {
                            new AlertDialog.Builder(loadingScreen.this)
                                    .setMessage("No network connection.")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();

                            return;
                        }


                        Intent i2 = new Intent(loadingScreen.this, KidLock.class);
                        i2.putExtra("child",FamilyInfo.getInstance().getString("child",""));
                        i2.putExtra("phone",FamilyInfo.getInstance().getString("phone",""));
                        if(FamilyInfo.getInstance().contains("lockedLocation")){
                            i2.putExtra("lockedLocation",FamilyInfo.getInstance().getLocation("lockedLocation",null));
                        }
                        Intent i = new Intent(loadingScreen.this, a2.class);
                        startService(i2);
                        startActivity(i);



                        //Finish splash activity so user cant go back to it.
                        loadingScreen.this.finish();

                        //Apply splash exit (fade out) and main entry (fade in) animation transitions.
                        overridePendingTransition(0, R.anim.splashfadeout);

                    }
                }
            };

            welcomeThread.start();
        } else if (FamilyInfo.getInstance().contains("phone")) {
            Thread welcomeThread = new Thread() {
                @Override
                public void run() {
                    try {
                        super.run();
                        sleep(2000);  //Delay of 1 seconds
                    } catch (Exception e) {

                    } finally {
                        if (!isNetworkConnected()) {
                            new AlertDialog.Builder(loadingScreen.this)
                                    .setMessage("No network connection.")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();

                            return;
                        }


                        Intent i = new Intent(loadingScreen.this, a2.class);

                        startActivity(i);

                        //Finish splash activity so user cant go back to it.
                        loadingScreen.this.finish();

                        //Apply splash exit (fade out) and main entry (fade in) animation transitions.
                        overridePendingTransition(0, R.anim.splashfadeout);

                    }
                }
            };

            welcomeThread.start();
        } else {

            Thread welcomeThread = new Thread() {
                @Override
                public void run() {
                    try {
                        super.run();
                        sleep(2000);  //Delay of 2 second
                    } catch (Exception e) {

                    } finally {
                        if (!isNetworkConnected()) {
                            new AlertDialog.Builder(loadingScreen.this)
                                    .setMessage("No network connection.")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();

                            return;
                        }


                        Intent i = new Intent(loadingScreen.this, AutocompleteMain.class);
                        startActivity(i);

                        //Finish splash activity so user cant go back to it.
                        loadingScreen.this.finish();

                        //Apply splash exit (fade out) and main entry (fade in) animation transitions.
                        overridePendingTransition(0, R.anim.splashfadeout);


                    }
                }
            };
            welcomeThread.start();
        }

    }

    public boolean isNetworkConnected() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        Context context = this;
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }





    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
