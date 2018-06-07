package com.example.jona.latacungadigital.Activities.Clases;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jona.latacungadigital.Activities.modelos.TextMessageModel;
import com.example.jona.latacungadigital.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MessageMapAttractiveHowToGet extends LinearLayout implements OnMapReadyCallback {

    GoogleMap googleMapAttractive;
    private TextMessageModel message;

    // Varaibles de acuerdo a los componentes que comprenden el layout: attractive_how_to_get.xml
    TextView txtAttractiveHowToGet, txtAddressHowToGet;
    MapView mapViewAttractive;

    public MessageMapAttractiveHowToGet(@NonNull Context context) {
        super(context, null);
        setView(context);
    }

    public MessageMapAttractiveHowToGet(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setView(context);
    }

    // Instanciar y dar valores a los componetes de la vista
    private void setView(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.attractive_how_to_get, this);
        txtAttractiveHowToGet = view.findViewById(R.id.txtAttractiveHowToGet);
        txtAddressHowToGet = view.findViewById(R.id.txtAttractiveAddressHowToGet);
        mapViewAttractive = view.findViewById(R.id.mapAttractiveHowToGet);
    }

    private void setValues() {
        if (message != null) {
            txtAttractiveHowToGet.setText(message.getNameAttractive());
            txtAddressHowToGet.setText(Html.fromHtml("<b>Dirección: </b>" +
                    "Se encuentra ubicado en las calles " + message.getAddressAttractive()));
            mapViewAttractive.getMapAsync(this);
        }
    }

    public void setMessage(TextMessageModel message) {
        this.message = message;
        setValues();
    }

    // Metodos del ciclo de vida del mapViewAttractive.
    public void mapViewOnCreate(Bundle savedInstanceState) {
        if (mapViewAttractive != null) {
            mapViewAttractive.onCreate(savedInstanceState);
        }
    }

    public void mapViewOnSaveInstanceState(Bundle outState){
        if (mapViewAttractive != null) {
            mapViewAttractive.onSaveInstanceState(outState);
        }
    }

    public void mapViewOnResume() {
        if (mapViewAttractive != null) {
            mapViewAttractive.onResume();
        }
    }

    public void mapViewOnDestroy() {
        if (mapViewAttractive != null) {
            mapViewAttractive.onDestroy();
        }
    }

    public void mapViewOnPause() {
        if (mapViewAttractive != null) {
            mapViewAttractive.onPause();
        }
    }

    public void mapViewOnLowMemory() {
        if (mapViewAttractive != null) {
            mapViewAttractive.onLowMemory();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMapAttractive = googleMap;

        googleMapAttractive.clear();

        PolylineOptions optionsAttractive = new PolylineOptions()
                .add(new LatLng(-0.9332111, -78.6131404)) // Coordenadas de donde se encuentra el usuario.
                .add(new LatLng(message.getLatitude(), message.getLongitude())) // Coordenadas del destino.
                .color(Color.BLUE);

        MarkerOptions markerUser = new MarkerOptions()
                .position(new LatLng(-0.9332111, -78.6131404))
                .title("Usted esta aquí.")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_user_dark_blue));

        MarkerOptions markerDestination = new MarkerOptions()
                .position(new LatLng(message.getLatitude(), message.getLongitude()))
                .title(message.getNameAttractive());

        googleMapAttractive.addPolyline(optionsAttractive);
        googleMapAttractive.addMarker(markerUser);
        googleMapAttractive.addMarker(markerDestination);

        // Definir la posicion de la camara en el map
        LatLngBounds centroHistorico = new LatLngBounds(
                new LatLng(-0.9364, -78.6163), new LatLng(-0.9301, -78.6129));
        googleMapAttractive.moveCamera(CameraUpdateFactory.newLatLngZoom(centroHistorico.getCenter(), 15));
    }
}
