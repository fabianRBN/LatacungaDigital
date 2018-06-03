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
  /*  private ArrayList<MarkerOptions> listaMarkers = new ArrayList<>();

    AccesoInternet accesoInternet;
    EstadoGPS estadoGPS;


    LatLng puntoOrigen = new LatLng(-0.9337192,-78.6174786);

    public ArrayList<MarkerOptions> getListaMarkers() {
        return listaMarkers;
    }

    public void setListaMarkers(ArrayList<MarkerOptions> listaMarkers) {
        this.listaMarkers = listaMarkers;
    }
*/


    public OnMarkerClickListenerAdapter(Context mContext, GoogleMap googleMap) {
        this.mContext = mContext;
        this.googleMap = googleMap;
        //accesoInternet = new AccesoInternet();
        //estadoGPS = new EstadoGPS(mContext,googleMap);
        //posicionPorGps();

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

        /*LatLng puntoFinal = new LatLng(marker.getPosition().latitude,marker.getPosition().longitude);

        puntoOrigen = estadoGPS.puntoOrigen;
        if(accesoInternet.isOnlineNet()) {
            distanciaGoogle(puntoOrigen, puntoFinal);
        }else{
            Toast.makeText(mContext,"No tiene acceso a internet",Toast.LENGTH_SHORT).show();
        }*/
        return true;
    }

   /* public void distanciaGoogle(LatLng puntoOrigen, LatLng punto){

        TaskRequestDirections taskRequestDirections = new TaskRequestDirections(googleMap,mContext);
        String url = taskRequestDirections.getRequestUrl(puntoOrigen,punto);

        taskRequestDirections.execute(url);



    }
*/
  /*  public void posicionPorGps(){

        if(estadoGPS.checkLocation()){
            estadoGPS.toggleGPSUpdates();
        }
    }*/



}
