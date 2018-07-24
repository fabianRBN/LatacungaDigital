package com.example.jona.latacungadigital.Activities.Adapters;

import android.content.Context;
import android.os.Handler;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by fabia on 27/05/2018.
 */

public class OnMarkerClickListenerAdapter implements GoogleMap.OnMarkerClickListener {

    private Context mContext;
    private GoogleMap googleMap;



    public OnMarkerClickListenerAdapter(Context mContext, GoogleMap googleMap) {
        this.mContext = mContext;
        this.googleMap = googleMap;
        //accesoInternet = new AccesoInternet();
        //estadoGPS = new EstadoGPS(mContext,googleMap);
        //posicionPorGps();

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        //Mover la camara a la posicion del marcador
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

        // Mostrar y ocultar la ventana de informacion sobre el marcador
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


        return true;
    }


}
