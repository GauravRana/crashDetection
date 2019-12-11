package com.example.biker112.ui.utils;

import android.location.Location;

public class CLocation extends Location {

    private boolean bUseMetricUnits = false;


    public CLocation(Location location) {
        // TODO Auto-generated constructor stub
        super(location);
    }



    @Override
    public float distanceTo(Location dest) {
        // TODO Auto-generated method stub
        float nDistance = super.distanceTo(dest);
            //Convert meters to feet
            //nDistance = nDistance * 3.28083989501312f;
        return nDistance;
    }

    @Override
    public float getAccuracy() {
        // TODO Auto-generated method stub
        float nAccuracy = super.getAccuracy();
            //Convert meters to feet
            //nAccuracy = nAccuracy * 3.28083989501312f;
        return nAccuracy;
    }

    @Override
    public double getAltitude() {
        // TODO Auto-generated method stub
        double nAltitude = super.getAltitude();

            //Convert meters to feet
            //nAltitude = nAltitude * 3.28083989501312d;

        return nAltitude;
    }

    @Override
    public float getSpeed() {
        // TODO Auto-generated method stub
        float nSpeed = super.getSpeed() * 3.6f;
            //Convert meters/second to miles/hour
            //nSpeed = nSpeed * 2.2369362920544f / 3.6f;

        return nSpeed;
    }
}
