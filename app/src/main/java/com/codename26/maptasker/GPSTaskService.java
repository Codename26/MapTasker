package com.codename26.maptasker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import com.google.android.gms.maps.model.LatLng;

public class GPSTaskService extends Service {
    private LocationListener mLocationListener;
    private LocationManager mLocationManager;
    public GPSTaskService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Intent intent = new Intent("location_update");
                intent.putExtra("coordinates", new LatLng(location.getLatitude(), location.getLongitude()));
                sendBroadcast(intent);

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        };
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        if (mLocationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
            //noinspection MissingPermission
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, mLocationListener);
        }

        if (mLocationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
            //noinspection MissingPermission
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 0, mLocationListener);
        }
        }


    //Unregister Location Manager from Location Listener to prevent memory leaks
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationManager != null){
            mLocationManager.removeUpdates(mLocationListener);
        }
    }
}
