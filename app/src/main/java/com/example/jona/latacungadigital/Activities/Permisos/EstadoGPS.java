package com.example.jona.latacungadigital.Activities.Permisos;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by fabia on 28/05/2018.
 */

public class EstadoGPS {

    Context context;
    private GoogleMap googleMap;
    LocationManager locationManager;
    boolean gps_enabled = false;
    boolean network_enabled = false;
    double longitudeGPS, latitudeGPS;
    public LatLng puntoOrigen = new LatLng(-0.9337192,-78.6174786);
    private LatLng currentLatLng;

    public EstadoGPS(Context context, GoogleMap googleMap) {
        this.context = context;
        this.googleMap = googleMap;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        PuntodeOrigenGoogleMap();
    }

    // Getters and Setters.
    public LatLng getCurrentLatLng() { return currentLatLng; }

    public void setCurrentLatLng(LatLng currentLatLng) { this.currentLatLng = currentLatLng; }

    public void PuntodeOrigenGoogleMap(){
        googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                puntoOrigen = new LatLng(location.getLatitude(),location.getLongitude());
                getCurrentLocation();
            }
        });
    }

    // Obtener la locacion actual del usuario.
    public void getCurrentLocation() {
        Criteria criteria = new Criteria();
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(criteria, false);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            float latitude = (float) location.getLatitude();
            float longitude = (float) location.getLongitude();
            LatLng currentUser = new LatLng(latitude, longitude);
            setCurrentLatLng(currentUser);
        }
    }

    public boolean GpsStado() {
        gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!gps_enabled){
            return false;
        }
        return true;
    }
    public boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Enable Location")
                .setMessage("Su ubicación esta desactivada.\npor favor active su ubicación " +
                        "usa esta app")
                .setPositiveButton("Configuración de ubicación", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }
    public void toggleGPSUpdates() {
        if (!checkLocation())
            return;

        else {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 2 * 20 * 10000, 5, locationListenerGPS);

        }
    }

    private final LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitudeGPS = location.getLongitude();
            latitudeGPS = location.getLatitude();

            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    puntoOrigen = new LatLng(latitudeGPS,longitudeGPS);
                    Toast.makeText(context, "GPS listo", Toast.LENGTH_SHORT).show();
                }
            });
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
    };
}
