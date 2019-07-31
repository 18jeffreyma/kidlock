package com.intel.location.ma.app.kidlock;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;

import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;

import android.os.Bundle;
import android.os.IBinder;

import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;


import com.intel.location.indoor.app.kidlock.R;
import com.intel.location.ma.app.kidlock.KalmanFilter.LocationKalman;


import android.os.Handler;


public class KidLock extends Service implements LocationListener {

    public static String STARTFOREGROUND_ACTION = "com.intel.location.indoor.kidlock.startforeground";

    private static final String TAG = "kidlock";

    Handler mHandler;


    LocationManager locationManager = null;

    Location lockedLocation;


    Intent i2;

    Boolean locationSet;
    Boolean floorSet;
    Boolean alerted;
    String phoneString;
    String childName;

    Intent intent;

    double radius;
    double kRadius;


    MovingAverage kRad;

    Criteria criteria;

    Location debugTemp;

    float time1;

    double speed1;
    double speed2;

    Location tempLocation;
    Location kTempLocation;

    double kTempRad;

    Double maxRadAverage;

    double tempMaxSpeed;


    int counter;
    double countSum;

    LocationKalman kFilter;


    @Override
    public void onCreate() {
        super.onCreate();


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


        double radius = 0.0;
        locationSet = false;
        floorSet = false;
        alerted = false;

        counter = 0;


        mHandler = new Handler();

        kRad = new MovingAverage();
        kRad.setLimit(3);

        tempMaxSpeed = 0.0;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "Service onStartCommand");
        if (intent != null) {
            phoneString = intent.getStringExtra("phone");
            childName = intent.getStringExtra("child");
            i2 = intent;
        }
        showShortToast("Location Service Started");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return Service.START_NOT_STICKY;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        // define sound URI, the sound to be played when there's a notification
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // intent triggered, you can add other intent for other actions
        Intent nextInte = new Intent(KidLock.this, loadingScreen.class);
        PendingIntent pIntent = PendingIntent.getActivity(KidLock.this, 0, nextInte, 0);

        // this is it, we'll build the notification!
        // in the addAction method, if you don't want any icon, just set the first param to 0
        Notification mNotification = new Notification.Builder(this)

                .setContentTitle("KidLock is Running")
                .setContentText("Child is locked in this location.")
                .setSmallIcon(R.drawable.lock_white)
                .setContentIntent(pIntent)
                .setSound(soundUri)
                .setOngoing(true)


                .build();

        mNotification.flags |= Notification.FLAG_NO_CLEAR;

        startForeground(1234, mNotification);
        return Service.START_STICKY;
    }


    private class ToastRunnable implements Runnable {

        private int mLength = -1;
        private String mString;

        public ToastRunnable(String str, int length) {
            mLength = length;
            mString = str;
        }

        @Override
        public void run() {
            if (mLength == 1) {
                Toast.makeText(getBaseContext(), mString, Toast.LENGTH_SHORT).show();
            } else if (mLength == 2) {
                Toast.makeText(getBaseContext(), mString, Toast.LENGTH_LONG).show();
            }

        }
    }


    @Override
    public void onLocationChanged(final Location location) {
        showLongToast("onLocationChange called");
        if (location != null) {

            if (!locationSet) {
                if (i2 != null && i2.getParcelableExtra("lockedLocation") != null) {

                    lockedLocation = i2.getParcelableExtra("lockedLocation");
                    radius = location.distanceTo(lockedLocation);


                    //Kalman Filter
                    kFilter = new LocationKalman(location);


                    kRad.addValue(location);
                    tempLocation = location;
                    kTempLocation = location;
                    time1 = System.nanoTime();
                    locationSet = true;

                    kTempRad = 0.0;

                } else {
                    FamilyInfo.getInstance().Initialize(this);
                    FamilyInfo.getInstance().setLocation("lockedLocation", location);
                    lockedLocation = location;
                    radius = 0.0;

                    //Kalman Filter
                    kFilter = new LocationKalman(location);


                    kRad.addValue(location);
                    tempLocation = location;
                    kTempLocation = location;
                    time1 = System.nanoTime();
                    locationSet = true;

                    kTempRad = 0.0;
                }
            } else {
                kTempRad = location.distanceTo(kTempLocation);
                speed1 = kRad.getAvgSpeed();
                if (getSpeed(tempLocation, location, time1, System.nanoTime()) < 12.5) {
                    tempLocation = location;
                    time1 = System.nanoTime();
                    kTempLocation = kFilter.updateLocation(location);

                    //Currently adding the filtered location to a MovingAverage

                    kRad.updateValues(kTempLocation);
                    //kRad.updateValues(location);

                }
                speed2 = kRad.getAvgSpeed();
                radius = kRad.getRadius();

                time1 = System.nanoTime();

                showShortToast("kAvgSp: " + Double.toString(speed2) + "\nkRadius: " + Double.toString(radius));

                /*
                if ((((speed1 > 0.5 && speed2 > 0.5 && radius > 2.0) || (speed1 > 0.01 && speed2 > 0.01) && radius > 7.0) && (!alerted)) && (phoneString != null && !phoneString.isEmpty())) {
                    showNotification();


                    alerted = true;
                    String message = "ALERT: Your child, " + childName + ", has left your assigned area!";
                    SmsManager sm = SmsManager.getDefault();

                    sm.sendTextMessage(phoneString, null, message, null, null);

                }
                */
            }


        }


    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    public void showLongToast(final String s) {

        mHandler.post(new ToastRunnable(s, 2));

    }

    public void showShortToast(final String s) {

        mHandler.post(new ToastRunnable(s, 1));
    }


    public void dismissAllNotifications() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel("Status", 0);
        notificationManager.cancel("Movement", 0);
    }


    @Override
    public void onDestroy() {

        super.onDestroy();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(this);

        //mIndoorLocationController.removeUpdates(this);

        dismissAllNotifications();
        Log.i(TAG, "Service onDestroy");
        locationSet = false;
        i2.removeExtra("lockedLocation");

        stopForeground(true);
    }


    public double getSpeed(Location location1, Location location2, float time1, float time2) {
        return (double)((location1.distanceTo(location2)/(time2-time1)) * 1000000000);
    }

    public void showNotification(){

        // define sound URI, the sound to be played when there's a notification
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // intent triggered, you can add other intent for other actions
        Intent intent = new Intent(KidLock.this, loadingScreen.class);
        PendingIntent pIntent = PendingIntent.getActivity(KidLock.this, 0, intent, 0);

        // this is it, we'll build the notification!
        // in the addAction method, if you don't want any icon, just set the first param to 0
        Notification mNotification = new Notification.Builder(this)

                .setContentTitle("ALERT!")
                .setContentText(FamilyInfo.getInstance().getString("child","") + " has moved. Texting Parents....")
                .setSmallIcon(R.drawable.lock_white)
                .setContentIntent(pIntent)
                .setSound(soundUri)


                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // If you want to hide the notification after it was selected, do the code below
        // myNotification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify("Movement",0, mNotification);
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }







}
