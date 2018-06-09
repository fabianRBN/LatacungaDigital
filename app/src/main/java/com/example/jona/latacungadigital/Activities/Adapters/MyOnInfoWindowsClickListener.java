package com.example.jona.latacungadigital.Activities.Adapters;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;

import com.example.jona.latacungadigital.Activities.AsyncTask.TaskRequestDirections;
import com.example.jona.latacungadigital.Activities.AtractivoActivity;
import com.example.jona.latacungadigital.Activities.Permisos.AccesoInternet;
import com.example.jona.latacungadigital.Activities.Permisos.EstadoGPS;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;
import java.util.Locale;

/**
 * Created by fabia on 26/05/2018.
 */

public class MyOnInfoWindowsClickListener implements GoogleMap.OnInfoWindowClickListener ,GoogleMap.OnInfoWindowLongClickListener {

    private Context context;
    LatLng puntoOrigen = new LatLng(-0.9337192,-78.6174786);
    GoogleMap googleMap;
    AccesoInternet accesoInternet;
    EstadoGPS estadoGPS;

    public MyOnInfoWindowsClickListener(Context context, GoogleMap googleMap) {
        this.context = context;
        this.googleMap = googleMap;
        accesoInternet = new AccesoInternet();
        estadoGPS = new EstadoGPS(context,googleMap);
        posicionPorGps();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        LatLng puntoFinal = new LatLng(marker.getPosition().latitude,marker.getPosition().longitude);

        puntoOrigen = estadoGPS.puntoOrigen;
        if(accesoInternet.isOnlineNet()) {
            distanciaGoogle(puntoOrigen, puntoFinal);
        }else{
            Toast.makeText(context,"No tiene acceso a internet",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onInfoWindowLongClick(Marker marker) {
        String[] parametros = marker.getSnippet().split("&##");
        String key = parametros[2];
        System.out.println("Dtaos key:"+key);

        Intent intent = new Intent(context, AtractivoActivity.class);
        intent.putExtra("atractivoKey", key);
        context.startActivity(intent);

    }

    // Método para dibujar la distancia entre el punto de origen y el punto de destino.
    public void distanciaGoogle(LatLng puntoOrigen, LatLng punto){

        TaskRequestDirections taskRequestDirections = new TaskRequestDirections(googleMap,context);
        String url = taskRequestDirections.getRequestUrl(puntoOrigen,punto);

        taskRequestDirections.execute(url);

    }

    // Método para obtener las calles en donde se encuentra el usuario.
    public String getCompleteAddressString(double latitude, double longitude) {
        String address = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                address = strReturnedAddress.toString();
            } else {
                address = "No se puede obtener su locación actual";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return address;
    }

    public void posicionPorGps(){

        if(estadoGPS.checkLocation()){
            estadoGPS.toggleGPSUpdates();
        }
    }
}
