package com.intel.location.ma.app.kidlock;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;


/**
 * Created by majeffrx on 6/27/2016.
 */
public class FamilyInfo {
    private static FamilyInfo mInstance;
    private Context mContext;
    //
    private SharedPreferences myPreferences;

    private FamilyInfo(){ }

    public static FamilyInfo getInstance(){
        if (mInstance == null) mInstance = new FamilyInfo();
        return mInstance;
    }

    public void Initialize(Context ctxt){
        mContext = ctxt;
        //
        myPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public void setString(String key, String value){
        SharedPreferences.Editor e = myPreferences.edit();
        e.putString(key, value);
        e.commit();
    }

    public String getString(String key, String value) {
        return myPreferences.getString(key, value);
    }

    public void setBoolean(String key, Boolean value) {
        SharedPreferences.Editor e = myPreferences.edit();
        e.putBoolean(key,value);
        e.commit();
    }

    public boolean getBoolean(String key, Boolean value) {
        return myPreferences.getBoolean(key, value);
    }

    public void removePref(String key){
        if (myPreferences.contains(key)) {
            SharedPreferences.Editor e = myPreferences.edit();
            e.remove(key);
            e.commit();
        }
    }

    public boolean contains(String key){
        return myPreferences.contains(key);
    }

    public void setDouble(String key, double value){
        SharedPreferences.Editor e = myPreferences.edit();
        e.putLong(key, Double.doubleToLongBits(value));
        e.commit();
    }

    public double getDouble(String key, double defValue) {
        return Double.longBitsToDouble(myPreferences.getLong(key,Double.doubleToLongBits(defValue)));
    }

    public void setInt(String key, int value) {
        SharedPreferences.Editor e = myPreferences.edit();
        e.putInt(key, value);
        e.commit();
    }

    public int getInt(String key, int defValue) {
        return myPreferences.getInt(key, defValue);
    }

    public void removeLocationData() {
        mInstance.removePref("pin");
        mInstance.removePref("alerted");
        mInstance.removePref("lockedLocation");
        mInstance.removePref("lockedLocationLatitude");
        mInstance.removePref("lockedLocationLongitude");
        mInstance.removePref("lockedFloorNum");

    }

    public void clear(){
        SharedPreferences.Editor e = myPreferences.edit();
        e.clear();
        e.commit();
    }

    public void setLocation(String key, Location myLocation) {

        SharedPreferences.Editor e = myPreferences.edit();

        e.putString(key, "exists");
        mInstance.setDouble(key + "Longitude",myLocation.getLongitude());
        mInstance.setDouble(key + "Latitude",myLocation.getLatitude());
        //implement floor location later
        e.commit();
    }

    public Location getLocation(String key, Location defLocation) {
        if (myPreferences.contains(key)){
            Location temp = new Location("");
            temp.setLongitude(mInstance.getDouble(key + "Longitude",0.0));
            temp.setLatitude(mInstance.getDouble(key + "Latitude",0.0));
            //set floor location later
            return temp;
        }
        else {
            return defLocation;
        }
    }
}
