package com.intel.location.ma.app.kidlock.KalmanFilter;

import android.location.Location;

/**
 * Created by majeffrx on 8/4/2016.
 */
public class LocationKalman {
    SimpleKalman locationLong;
    SimpleKalman locationLat;

    public LocationKalman(Location loc) {
        locationLong = new SimpleKalman(loc.getLongitude());

        locationLong.setQ(0.00001);
        locationLong.setR(0.000005437d);

        locationLat = new SimpleKalman(loc.getLatitude());

        locationLat.setQ(0.00001);
        locationLat.setR(0.0000083539d);
    }

    public Location updateLocation(Location loc){
        double tempLong = locationLong.getPredicted_Value(loc.getLongitude());
        double tempLat = locationLat.getPredicted_Value(loc.getLatitude());

        Location tempLocation = new Location("");
        tempLocation.setLongitude(tempLong);
        tempLocation.setLatitude(tempLat);

        return tempLocation;
    }

    public String debugLon(){
        return "Lon Q: " + locationLong.returnQ() + "\nLon R: " + locationLong.returnR();
    }

    public String debugLat(){
        return "Lat Q: " + locationLat.returnQ() + "\nLat R: " + locationLat.returnR();

    }

}
