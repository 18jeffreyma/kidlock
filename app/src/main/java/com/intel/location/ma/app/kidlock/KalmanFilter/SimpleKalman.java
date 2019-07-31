package com.intel.location.ma.app.kidlock.KalmanFilter;

/**
 * Created by majeffrx on 8/4/2016.
 */
public class SimpleKalman {
    /** process noise COVARIANCE */
    private double q = 0.00001d;
    /** measurement noise COVARIANCE */
    private double r = 0.000005437d;

    /** Measurement value */
    private double x = 0d;
    /** estimation error COVARIANCE */
    private double p = 2d;
    /** KALMAN gain */
    private double k = 0.5d;

    /**
     * Init the filter with initial value of signal.
     * @param initial_measurement
     */
    public SimpleKalman(double initial_measurement) {
        this.x = initial_measurement;
    }

    /**
     * Method to get the predicted value of the measured signal value.
     *
     * @param measurement
     *            value from real time data.
     * @return predicted measurement.
     */
    public double getPredicted_Value(double measurement) {
        p = p + q;

        k = p / (p + r);
        x = x + k * (measurement - x);
        p = (1 - k) * p;
        return x;
    }

    public double returnQ(){
        return q;
    }

    public double returnR(){
        return r;
    }

    public void setX(double myX){
        x = myX;
    }

    public void setP(double myP){
        x = myP;
    }

    public void setQ(double myQ){
        q = myQ;
    }

    public void setR(double myR){
        r = myR;
    }

}