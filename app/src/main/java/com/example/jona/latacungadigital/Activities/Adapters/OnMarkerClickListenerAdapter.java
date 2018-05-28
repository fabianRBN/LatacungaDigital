package com.example.jona.latacungadigital.Activities.Adapters;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;

import com.example.jona.latacungadigital.Activities.AsyncTask.TaskRequestDirections;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;

/**
 * Created by fabia on 27/05/2018.
 */

public class OnMarkerClickListenerAdapter implements GoogleMap.OnMarkerClickListener {

    private Context mContext;
    private GoogleMap googleMap;
    private GoogleMap googleMapTemporal;
    private ArrayList<MarkerOptions> listaMarkers = new ArrayList<>();
    LocationManager locationManager;
    Criteria criteria = new Criteria();
    private Polyline polyline;
    public Polyline polylineAux;
    int contador = 0;

    Location location;
    LatLng puntoOrigen = new LatLng(-0.9337192,-78.6174786);

    public ArrayList<MarkerOptions> getListaMarkers() {
        return listaMarkers;
    }

    public void setListaMarkers(ArrayList<MarkerOptions> listaMarkers) {
        this.listaMarkers = listaMarkers;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }

    public void setGoogleMapTemporal(GoogleMap googleMapTemporal) {
        this.googleMapTemporal = googleMapTemporal;
    }




    public OnMarkerClickListenerAdapter(Context mContext, GoogleMap googleMap) {
        this.mContext = mContext;
        this.googleMap = googleMap;
        locationManager =  (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return ;
        }
        location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        LatLng puntoFinal = new LatLng(marker.getPosition().latitude,marker.getPosition().longitude);
        puntoOrigen = new LatLng(location.getLatitude(), location.getLongitude());
        marker.showInfoWindow();

        distanciaGoogle(puntoOrigen,puntoFinal);
        return true;
    }

    public void distanciaGoogle(LatLng puntoOrigen, LatLng punto){

        //googleMap.clear();
        //googleMap = googleMapTemporal;
        if(polyline != null){
            System.out.println("Pililine eliminado");
        }
        googleMap.setInfoWindowAdapter(new CustomInfoWindowsAdapter(mContext));
        googleMap.setOnInfoWindowClickListener( new MyOnInfoWindowsClickListener(mContext));
        TaskRequestDirections taskRequestDirections = new TaskRequestDirections(googleMap,mContext);
        //googleMap.clear();
        /*googleMap = googleMapTemporal;
        if(listaMarkers.size() >0) {

            for (MarkerOptions markerOptions : listaMarkers) {
                googleMap.addMarker(markerOptions);

            }

            googleMap.setInfoWindowAdapter(new CustomInfoWindowsAdapter(mContext));
            googleMap.setOnInfoWindowClickListener( new MyOnInfoWindowsClickListener(mContext));
        }*/


        String url = taskRequestDirections.getRequestUrl(puntoOrigen,punto);
        System.out.println("Url hilo:"+url);
        taskRequestDirections.execute(url);
        setPolyline(taskRequestDirections.getPolyline());


    }



}
