package com.example.jona.latacungadigital.Activities.Adapters;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.example.jona.latacungadigital.Activities.AsyncTask.TaskRequestDirections;
import com.example.jona.latacungadigital.Activities.Permisos.AccesoInternet;
import com.example.jona.latacungadigital.Activities.Permisos.EstadoGPS;
import com.google.android.gms.maps.GoogleMap;
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
    AccesoInternet accesoInternet;
    EstadoGPS estadoGPS;

    Location location;
    LatLng puntoOrigen = new LatLng(-0.9337192,-78.6174786);

    public ArrayList<MarkerOptions> getListaMarkers() {
        return listaMarkers;
    }

    public void setListaMarkers(ArrayList<MarkerOptions> listaMarkers) {
        this.listaMarkers = listaMarkers;
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
        accesoInternet = new AccesoInternet();
        estadoGPS = new EstadoGPS(mContext,googleMap);
        posicionPorGps();

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {



        marker.showInfoWindow();
        if(marker.isInfoWindowShown()){
            marker.hideInfoWindow();
            marker.showInfoWindow();
        }
        // Delay para dibujar la imagen en el lienso de infowindows google maps
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                marker.showInfoWindow();
                            }
        }, 200);

        LatLng puntoFinal = new LatLng(marker.getPosition().latitude,marker.getPosition().longitude);
        /*if(estadoGPS.GpsStado()) {

                puntoOrigen = new LatLng(location.getLatitude(), location.getLongitude());

        }else{
            Toast.makeText(mContext,"Active GPS",Toast.LENGTH_SHORT).show();
        }*/
        puntoOrigen = estadoGPS.puntoOrigen;
        if(accesoInternet.isOnlineNet()) {
            distanciaGoogle(puntoOrigen, puntoFinal);
        }else{
            Toast.makeText(mContext,"No tiene acceso a internet",Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    public void distanciaGoogle(LatLng puntoOrigen, LatLng punto){

        //googleMap.clear();
        //googleMap = googleMapTemporal;

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

        taskRequestDirections.execute(url);
        setPolyline(taskRequestDirections.getPolyline());


    }

    public void posicionPorGps(){
        /*locationManager =  (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return ;
        }
        location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));*/

        if(estadoGPS.checkLocation()){
            estadoGPS.toggleGPSUpdates();
        }
    }



}
