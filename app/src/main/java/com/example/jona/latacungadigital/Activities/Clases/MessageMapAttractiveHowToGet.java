package com.example.jona.latacungadigital.Activities.Clases;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jona.latacungadigital.Activities.Adapters.MessagesAdapter;
import com.example.jona.latacungadigital.Activities.Adapters.MyOnInfoWindowsClickListener;
import com.example.jona.latacungadigital.Activities.ChatBotActivity;
import com.example.jona.latacungadigital.Activities.Fragments.MapaFragment;
import com.example.jona.latacungadigital.Activities.Permisos.EstadoGPS;
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

public class MessageMapAttractiveHowToGet extends LinearLayout implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    // Variables de la clase
    private MessagesAdapter messagesAdapter;
    private TextMessageModel message;

    //Variables para editar el mapa
    AttractiveClass attractive;
    GoogleMap gMap;
    Context context;

    // Varaibles de acuerdo a los componentes que comprenden el layout: attractive_how_to_get.xml
    TextView txtAttractiveHowToGet, txtAddressHowToGet;
    MapView mapViewAttractive;

    // Constructor de la clase
    public MessageMapAttractiveHowToGet(@NonNull Context context) {
        super(context, null);
        this.context = context;
        setView(context);
    }

    public MessageMapAttractiveHowToGet(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setView(context);
    }

    // Instanciar y dar valores a los componetes de la vista
    private void setView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.attractive_how_to_get, this);
        txtAttractiveHowToGet = view.findViewById(R.id.txtAttractiveHowToGet);
        txtAddressHowToGet = view.findViewById(R.id.txtAttractiveAddressHowToGet);
        mapViewAttractive = view.findViewById(R.id.mapAttractiveHowToGet);
    }

    private void setValues() {
        if (message != null) {
            attractive = message.getAttractive();
            txtAttractiveHowToGet.setText(attractive.getNameAttractive());
            txtAddressHowToGet.setText(
                    Html.fromHtml("<b>Dirección: </b>" + "Se encuentra ubicado en " + attractive.getAddress()));
            mapViewAttractive.getMapAsync(this);
        }
    }

    public void setMessage(TextMessageModel message) {
        this.message = message;
        setValues();
    }

    public void setMessagesAdapter(MessagesAdapter messagesAdapter) {
        this.messagesAdapter = messagesAdapter;
    }

    // Metodos del ciclo de vida del mapViewAttractive.
    public void mapViewOnCreate(Bundle savedInstanceState) {
        if (mapViewAttractive != null) {
            mapViewAttractive.onCreate(savedInstanceState);
        }
    }

    public void mapViewOnSaveInstanceState(Bundle outState) {
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

    // Crear un marcador en el mapa de acuerdo a un atraactivo
    private void createMarker(AttractiveClass attractive) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(attractive.getLatitude(), attractive.getLongitude()));
        if (attractive.getIcon() != 0) { // Validar si existe un icono predefinido del atractivo
            markerOptions.icon(BitmapDescriptorFactory.fromResource(attractive.getIcon()));
        }
        gMap.addMarker(markerOptions);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.clear();
        EstadoGPS estadoGPS = new EstadoGPS(context, gMap); // Variable para obtener la locacion donde se encuentra el usuario.

        // Validar si la aplicacion tiene el permiso de Localizacion
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            return;
        }

        gMap.setMyLocationEnabled(true); // Habilitar el boton de "Mover a mi ubicacion"

        // Dibujar la ruta de como llegar al lugar turistico.
        estadoGPS.getCurrentLocation(); // Leer las coordenadas actuales de donde se encunetra el usuario y almacenarlo en un metodo Setter.
        MyOnInfoWindowsClickListener myOnInfoWindowsClickListener = new MyOnInfoWindowsClickListener(context, gMap);
        myOnInfoWindowsClickListener.distanciaGoogle(estadoGPS.getCurrentLatLng(), new LatLng(attractive.getLatitude(), attractive.getLongitude()));

        // Crear un marcador para la posicion del usuario
        MarkerOptions markerUser = new MarkerOptions()
                .position(estadoGPS.getCurrentLatLng()) // Coordenadas actuales de donde se encunetra el usuario.
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_user_dark_blue));
        gMap.addMarker(markerUser);

        // Crear marcador para la posicion del atractivo de destino
        createMarker(attractive);

        // Posicionar la camara segun la ruta de donde se encuentre el usuario con el punto de destino.
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(estadoGPS.getCurrentLatLng());
        builder.include(new LatLng(attractive.getLatitude(), attractive.getLongitude()));
        gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 15));

        // Definir la funcion de click en el mapa.
        gMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (this.messagesAdapter != null) {
            ChatBotActivity chatBotActivity = (ChatBotActivity) this.messagesAdapter.getChatTextFragment().getActivity();
            MapaFragment mapaFragment = new MapaFragment();
            mapaFragment.setSerchFromChatBot(true);
            //Enviamos el punto de destino para dibujar en el mapa.
            mapaFragment.setPointDestination(new LatLng(attractive.getLatitude(), attractive.getLongitude()));
            mapaFragment.setAttractive(this.attractive);
            chatBotActivity.changeFragmente(mapaFragment);
        } else {
            Log.d("messageAdapterNUll", "MessageAdapter es Null");
        }
    }
}
