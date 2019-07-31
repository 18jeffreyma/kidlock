package com.intel.location.ma.app.kidlock;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by majeffrx on 7/18/2016.
 */
public class MovingAverage {
    List<Location> locations;

    int count;
    int limit;
    List<Long> time;

    double radius;


    public MovingAverage() {
        locations = new ArrayList<Location>();
        time = new ArrayList<Long>();
        count = 0;
        radius = 0.0;
    }

    public void addValue(Location value){
        if (locations.size()>0 &&
                (value.distanceTo(locations.get(locations.size()-1)))/(System.nanoTime()-time.get(time.size()-1))*1000000000 > 5.0){
            //Do nothing
        } else {
            locations.add(value);
            time.add(System.nanoTime());
            count++;

            radius = value.distanceTo(FamilyInfo.getInstance().getLocation("lockedLocation",value));
        }

    }

    public void setLimit(int value) {
        limit = value;
    }

    public double getRadius(){
        return radius;
    }

    public void updateValues(Location value){
        if (count < limit) {
            this.addValue(value);
        } else {
            this.removeFirstValue();
            this.addValue(value);
        }
    }

    public double getAvgSpeed() {
        if (locations.size() >= limit) {
            return (double)((locations.get(0)).distanceTo(locations.get(locations.size()-1))/((time.get(time.size()-1))-time.get(0))*1000000000);
        } else {
            return 0.0;
        }
    }

    public double getLastSpeed() {
        if(locations.size()>1){
            return (double)((locations.get(locations.size()-2)).distanceTo(locations.get(locations.size()-1))/((time.get(time.size()-1))-time.get(time.size()-2))*1000000000);
        } else {
            return 0.0;
        }
    }


    public void removeFirstValue() {
        locations.remove(0);
        time.remove(0);
        count--;
    }

    public int getCount(){
        return count;
    }



    public void reset() {
        locations = new ArrayList<Location>();
        time = new ArrayList<Long>();
        count = 0;
    }

}
