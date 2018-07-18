package com.example.jona.latacungadigital.Activities.Fragments;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.jona.latacungadigital.Activities.Adapters.AttractiveInfoWindowsAdapter;
import com.example.jona.latacungadigital.Activities.Adapters.CustomInfoWindowsAdapter;
import com.example.jona.latacungadigital.Activities.Adapters.MyOnInfoWindowsClickListener;
import com.example.jona.latacungadigital.Activities.Adapters.OnMarkerClickListenerAdapter;
import com.example.jona.latacungadigital.Activities.Adapters.ServiceInfoWindowsAdapter;
import com.example.jona.latacungadigital.Activities.Clases.AreaPeligrosa;
import com.example.jona.latacungadigital.Activities.Clases.AttractiveClass;
import com.example.jona.latacungadigital.Activities.Clases.ServiceClass;
import com.example.jona.latacungadigital.Activities.Haversine.Haversine;
import com.example.jona.latacungadigital.Activities.MainActivity;
import com.example.jona.latacungadigital.Activities.Permisos.EstadoGPS;
import com.example.jona.latacungadigital.Activities.References.ChatBotReferences;
import com.example.jona.latacungadigital.Activities.modelos.AtractivoModel;
import com.example.jona.latacungadigital.Activities.modelos.Coordenada;
import com.example.jona.latacungadigital.Activities.modelos.TrackinModel;
import com.example.jona.latacungadigital.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class MapaFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMapLongClickListener {

    // Variables de la clase
    private boolean isSerchFromChatBot = false;
    private String chatBotAction;
    private ArrayList<AttractiveClass> listAttractive;
    private ArrayList<ServiceClass> listService;
    private AttractiveClass attractive;
    private ServiceClass service;
    private LatLng currentUserLatLng;
    private LatLng pointDestination;
    Button btnGuardar, btnCancelar;
    EditText edtNombre, edtDescripcion;
    String dataArg;
    String dataArgTrac;

    // Variables de mapa
    MapView mMapView;
    private GoogleMap googleMap;
    private Marker myPositionMarker;

    //Variables de posicion
    private static final int MY_PERMISSION_REQUEST_CODE = 7192;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 7193;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    Haversine haversine;

    private static int UPDATE_INTERVAL = 5000;
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    GeoFire geoFire;



    // Variables de firebase
    private DatabaseReference mDatabase;
    private FirebaseUser userFirebase = FirebaseAuth.getInstance().getCurrentUser();
    private static ArrayList<MarkerOptions> listaMarkadores = new ArrayList<MarkerOptions>();
    private static ArrayList<AreaPeligrosa> listaAreaPeligrosa = new ArrayList<AreaPeligrosa>();
    private ArrayList<String> listaFiltrarAtractivos = new ArrayList<>();
    private ArrayList<String> listaFiltrarServicios = new ArrayList<>();

    public static void setListaAreaPeligrosa(ArrayList<AreaPeligrosa> listaAreaPeligrosa) {
        MapaFragment.listaAreaPeligrosa = listaAreaPeligrosa;
    }

    private OnFragmentInteractionListener mListener;

    public MapaFragment() {
        // Required empty public constructor
    }

    // Getters and Setters para dibujar el punto de destino del atractivo turistico.
    public LatLng getPointDestination() { return pointDestination; }

    public void setPointDestination(LatLng pointDestination) { this.pointDestination = pointDestination; }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mapa, container, false);
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        // Flotante que muestra popup del clima
        FloatingActionButton fabClima = (FloatingActionButton) rootView.findViewById(R.id.fab_clima);
        fabClima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createClima();
            }
        });

        // Flotante que redirige al mapa
        FloatingActionButton fabFiltro = (FloatingActionButton) rootView.findViewById(R.id.fab_filtro);
        fabFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearPopupFiltro();
            }
        });

        // Establecer visibilidad
        if ((dataArgTrac!=null) || (dataArg !=null)){
            fabFiltro.setVisibility(View.INVISIBLE);
        }

        if (isSerchFromChatBot){
            fabClima.setVisibility(View.GONE);
            fabFiltro.setVisibility(View.GONE);
        }

        // Obtener argumentos de las notificaciones
        try { // Analiza argumento de notificacion de atractivos
            dataArg = getArguments().getString("dato");
        } catch (Exception ex){
            System.out.println("Error: "+ex);
        }
        try { // Analiza argumento de trackeo
            dataArgTrac = getArguments().getString("trackeo");
        } catch (Exception ex){
            System.out.println("Error: "+ex);
        }

//        try {
//            MapsInitializer.initialize(getActivity().getApplicationContext());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        mMapView.getMapAsync(this);

        //obtener permisos de ubicacion
        setUpLocation();
        mDatabase = FirebaseDatabase.getInstance().getReference("cliente").child(userFirebase.getUid());
        geoFire = new GeoFire(mDatabase);

        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (checkPlayServices()){
                        buildGoogleApiClient();
                        createLocationRequest();
                        displayLocation();
                    }
                }
                break;


        }
    }

    private void setUpLocation() {
        if ( ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED){
            //Request runtime Location
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);
        }
        else if (checkPlayServices()){
            buildGoogleApiClient();
            createLocationRequest();
            displayLocation();
        }

    }

    private void displayLocation() {
        //Validar si la aplicacion tiene el permiso de Localizacion
        if(getActivity() != null){
            if ( ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 200);
                return;
            }
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation != null){
            final double latitude = mLastLocation.getLatitude();
            final double longitude = mLastLocation.getLongitude();

            //Actualizar GeoFire
            mDatabase = FirebaseDatabase.getInstance().getReference().child("cliente");
            geoFire = new GeoFire(mDatabase.child(userFirebase.getUid()));
            geoFire.setLocation("GeoFire",new GeoLocation(latitude, longitude));

            /*geoFire.setLocation("GeoFire", new GeoLocation(latitude, longitude),
                    new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            //Add Marker
                            if (mCurrent != null)
                                mCurrent.remove(); //Remueve los marcadores anteriores
                            mCurrent = googleMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(latitude, longitude))
                                                .title("Tu"));
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude),15.0f));
                        }
                    });*/

        }
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if(resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(), PLAY_SERVICES_RESOLUTION_REQUEST).show();
            else {
                Toast.makeText(getActivity(), "This device es not supported", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
            return false;
        }
        return true;
    }

    public void setSerchFromChatBot(boolean serchFromChatBot) {
        isSerchFromChatBot = serchFromChatBot;
    }

    public void setChatBotAction(String chatBotAction) {
        this.chatBotAction = chatBotAction;
    }

    public void setListAttractive(ArrayList<AttractiveClass> listAttractive) {
        this.listAttractive = listAttractive;
    }

    public void setListService(ArrayList<ServiceClass> listService) {
        this.listService = listService;
    }

    public void setAttractive(AttractiveClass attractive) {
        this.attractive = attractive;
    }

    public void setService(ServiceClass service) {
        this.service = service;
    }

    public void dataFirebase(){
        final OnMarkerClickListenerAdapter onMarkerClickListenerAdapter = new OnMarkerClickListenerAdapter(getContext(),googleMap);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Recupera lista de Areas Peligrosas
                for (DataSnapshot child: dataSnapshot.child("areaPeligrosa").getChildren()){

                    String nombreArea = child.child("nombre").getValue().toString();
                    String idArea = child.child("id").getValue().toString();
                    Double radio = Double.parseDouble(child.child("radio").getValue().toString());
                    Double latitud = Double.parseDouble(child.child("latitud").getValue().toString());
                    Double longitud = Double.parseDouble(child.child("longitud").getValue().toString());
                    AreaPeligrosa areaPeligrosa = new AreaPeligrosa(nombreArea, idArea, radio, latitud, longitud);
                    listaAreaPeligrosa.add(areaPeligrosa);
                    final LatLng dangerousArea = new LatLng(areaPeligrosa.getLatitud(), areaPeligrosa.getLongitud());
                    googleMap.addCircle(new CircleOptions()
                            .center(dangerousArea)
                            .radius(areaPeligrosa.getRadio())
                            .strokeColor(Color.RED)
                            .fillColor(0x220000FF)
                            .strokeWidth(5.0f));
                    //Equivalencias de distancia de GeoFire
                    // 0.1f = 0.1km = 100m
                    GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(dangerousArea.latitude, dangerousArea.longitude), 0.1f);
                    geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                        @Override
                        public void onKeyEntered(String key, GeoLocation location) {
                            sendNotification("Alerta", String.format("Entraste en un área Peligrosa"),dangerousArea);
                        }

                        @Override
                        public void onKeyExited(String key) {
                            sendNotification("Alerta", String.format(" Esta cerca un área Peligrosa"), dangerousArea);

                        }

                        @Override
                        public void onKeyMoved(String key, GeoLocation location) {
                            Log.d("Alerta", String.format(" moved within the dangerous area "));
                        }

                        @Override
                        public void onGeoQueryReady() {

                        }

                        @Override
                        public void onGeoQueryError(DatabaseError error) {
                            Log.e("ERROR", ""+error);
                        }
                    });

                }
                setListaAreaPeligrosa(listaAreaPeligrosa);

                for (DataSnapshot child: dataSnapshot.child("atractivo").getChildren()){

                    String nombreAtractivo = child.child("nombre").getValue().toString();
                    String descripcionAtractivo = child.child("descripcion").getValue().toString();
                    String snippit ="";
                    String pathImagen= "";
                            for(DataSnapshot galeria: child.child("galeria").getChildren()){
                                pathImagen = galeria.child("imagenURL").getValue().toString();
                            }
                            snippit = descripcionAtractivo +"&##"+pathImagen+"&##"+child.getKey();
                    Coordenada coordenada =  child.child("posicion").getValue(Coordenada.class);
                    LatLng punto = new LatLng( coordenada.getLat(), coordenada.getLng());
                    MarkerOptions markerOptions = new  MarkerOptions().position(punto)
                            .title(nombreAtractivo)
                            .snippet(snippit)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_building_blue));
                    listaMarkadores.add(markerOptions);
                    googleMap.addMarker(markerOptions);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });
        // For zooming automatically to the location of the marker
        googleMap.setOnMarkerClickListener(onMarkerClickListenerAdapter);
    }

    // Crear un marcador en el mapa de acuerdo a un servicio
    private void createMarkerForService(ServiceClass service){
        MarkerOptions markerOptions =  new MarkerOptions();
        markerOptions.position(new LatLng(service.getLatitude(),service.getLongitude()));
        markerOptions.title(service.getName());
        markerOptions.snippet(service.getSubTypeOfActivity());
        markerOptions.draggable(false);
        if(service.getIcon() != 0){ // Validar si existe un icono predefinido del servicio
            markerOptions.icon(BitmapDescriptorFactory.fromResource(service.getIcon()));
        }
        googleMap.addMarker(markerOptions).setTag(service);
    }

    // Crear un marcador en el mapa de acuerdo a un atractivo
    private void createMarkerForAttractive(AttractiveClass attractive){
        MarkerOptions markerOptions =  new MarkerOptions();
        markerOptions.position(new LatLng(attractive.getLatitude(),attractive.getLongitude()));
        markerOptions.title(attractive.getNameAttractive());
        markerOptions.snippet(attractive.getAddress());
        markerOptions.draggable(false);
        if (attractive.getIcon() != 0) { // Validar si existe un icono predefinido del atractivo
            markerOptions.icon(BitmapDescriptorFactory.fromResource(attractive.getIcon()));
        }
        googleMap.addMarker(markerOptions).setTag(attractive);
    }

    // Crear un marcador en el mapa de acuerdo a usuarios amigos
    private void createMarkerForUsers(){
        final ArrayList<TrackinModel> listaidUsuarios = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("meAutorizaron").child(userFirebase.getUid()).exists()){
                    for (DataSnapshot child: dataSnapshot.child("meAutorizaron").child(userFirebase.getUid()).getChildren()){
                        boolean autorizado = Boolean.parseBoolean(child.child("autorizacion").getValue().toString());
                        listaidUsuarios.add(new TrackinModel(child.getKey(),autorizado));
                    }
                    for (TrackinModel trac: listaidUsuarios){
                        if (trac.isAutorizacion()){
                            if (dataSnapshot.child("cliente").child(trac.getKey()).child("GeoFire").exists()){
                                String nombre = dataSnapshot.child("cliente").child(trac.getKey()).child("nombre").getValue().toString();
                                double latitud = Double.parseDouble(dataSnapshot.child("cliente").child(trac.getKey())
                                        .child("GeoFire").child("l").child("0").getValue().toString());
                                double longitud = Double.parseDouble(dataSnapshot.child("cliente").child(trac.getKey())
                                        .child("GeoFire").child("l").child("1").getValue().toString());
                                MarkerOptions markerOptions =  new MarkerOptions();
                                markerOptions.position(new LatLng(latitud,longitud));
                                markerOptions.title(nombre);
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_user_dark_blue));

                                //Calculo de la distancia
                                haversine = new Haversine();
                                Coordenada inicial = new Coordenada(currentUserLatLng.latitude,currentUserLatLng.longitude);//mi coordenada
                                Coordenada end = new Coordenada(latitud,longitud);// la coordenada del trackeado
                                LatLng latlong = new LatLng(end.getLat(), end.getLng());
                                double distanciah = (haversine.distance(inicial,end));
                                String formatoDistancia = String.format("%.02f", distanciah);

                                markerOptions.snippet("Distancia: "+formatoDistancia+" km");
                                markerOptions.draggable(false);
                                googleMap.addMarker(markerOptions).setTag("userMarker");
                                double distmax = 1;
                                if (dataArgTrac != null){
                                    distmax = Double.parseDouble(getArguments().getString("trackeo"))/1000;
                                }

                                if (distanciah >= distmax){
                                    if (dataArg == null)
                                        sendNotification("Alerta", String.format(nombre+" se alejo a "+formatoDistancia+" km de distancia de ti"), latlong);

                                }

                            }
                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // Crear un marcador en el mapa de acuerdo a usuarios amigos
    private void createMarkerForMySites(){
        final ArrayList<TrackinModel> listaidUsuarios = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference("cliente").child(userFirebase.getUid());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("misSitios").exists()){
                    for (DataSnapshot child: dataSnapshot.child("misSitios").getChildren()){
                        String nombreAtractivo = child.child("nombre").getValue().toString();
                        Coordenada coordenada =  child.child("posicion").getValue(Coordenada.class);
                        LatLng punto = new LatLng( coordenada.getLat(), coordenada.getLng());
                        //Calculo de la distancia
                        haversine = new Haversine();
                        Coordenada inicial = new Coordenada(currentUserLatLng.latitude,currentUserLatLng.longitude);//mi coordenada
                        double distanciah = (haversine.distance(inicial,coordenada))*1000;
                        String formatoDistancia = String.format("%.00f", distanciah);

                        MarkerOptions markerOptions = new  MarkerOptions().position(punto)
                                .title(nombreAtractivo)
                                .snippet("Distancia: "+formatoDistancia+" m")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        googleMap.addMarker(markerOptions).setTag("MyMarkers");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // Crear un marcador en el mapa de acuerdo al usuario
    private void createMarkerForUser(LatLng location, String address) {
        if (location == null)
            return;

        // Agregar marcador de la ubicacion del usuario
        if (myPositionMarker == null) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(location.latitude, location.longitude));
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_user_dark_blue));
            markerOptions.title("Mi Ubicación");
            markerOptions.snippet(address);
            myPositionMarker = googleMap.addMarker(markerOptions);
            myPositionMarker.setTag("userMarker");
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap mMap) {
        googleMap = mMap;
        googleMap.clear();
        // Validar si la aplicacion tiene el permiso de Localizacion
        if ( ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            return;
        }

        //Agregar sitio
        googleMap.setOnMapLongClickListener(this);

        // Habilitar el boton de "Mover a mi ubicacion"
        googleMap.setMyLocationEnabled(true);

        // Definir la posicion de la camara en el map
        LatLngBounds centroHistorico = new LatLngBounds(
                new LatLng(-0.9364, -78.6163), new LatLng(-0.9301, -78.6129));
        if (!isSerchFromChatBot) {
            if (dataArg != null){//Si es una solicitud de la Notificacion
                String[] latlong =  getArguments().getString("dato").split(",");
                double latitude = Double.parseDouble(latlong[0]);
                double longitude = Double.parseDouble(latlong[1]);
                LatLng datatoLatLon = new LatLng(latitude, longitude);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(datatoLatLon, 13);
                googleMap.animateCamera(cameraUpdate);
            } else
            {
                CameraPosition cameraPosition = new CameraPosition.Builder().target(centroHistorico.getCenter()).zoom(15).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        } else {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centroHistorico.getCenter(), 15));
        }

        // Habilitar las ventanas de información sobre los marcadores
        MyOnInfoWindowsClickListener myOnInfoWindowsClickListener= new MyOnInfoWindowsClickListener(getContext(),googleMap);

        // Encontrar la posicion del usuario
        EstadoGPS estadoGPS = new EstadoGPS(getContext(), googleMap); // Variable para obtener la locacion donde se encuentra el usuario.
        estadoGPS.getCurrentLocation(); // Leer las coordenadas actuales de donde se encunetra el usuario y almacenarlo en un metodo Setter.
        if (estadoGPS.getCurrentLatLng() != null) { // Si esta permitido la ubicacion del usuario se pone las coordenadas de donde se encuentra.
            currentUserLatLng = estadoGPS.getCurrentLatLng();
        } else { // Si no esta permitido la ubicacion actual del usario se pone un punto preestablecido.
            currentUserLatLng = estadoGPS.puntoOrigen;
        }

        // Crear un marcador para la posicion del usuario
        createMarkerForUser(currentUserLatLng,
                myOnInfoWindowsClickListener.getCompleteAddressString(currentUserLatLng.latitude, currentUserLatLng.longitude)); // Para obtener la direccion en donde se encuentra el usuario.

        if (!isSerchFromChatBot) { // Si NO es una solicitud de consulta del chatbot
            // Habilitar las ventanas de información sobre los marcadores
            googleMap.setInfoWindowAdapter(new CustomInfoWindowsAdapter(getContext()));
            googleMap.setOnInfoWindowClickListener( myOnInfoWindowsClickListener);
            googleMap.setOnInfoWindowLongClickListener(myOnInfoWindowsClickListener);
            if ((dataArgTrac != null) || (dataArg != null)){//Si es una solicitud de trackeo
                createMarkerForUsers();//Agregar marcadores de usuarios amigos en el mapa
            } else {
                dataFirebase(); // Agregar marcadores de atractivos y areas peligrosas
                createMarkerForMySites(); //Agrega los marcadores personales
            }
        } else { // Si es una solicitud de consulta del chatbot
            switch (chatBotAction){
                case "consultarAtractivoEnElArea":
                    // Establecer la venta de informacion info_window_attractive
                    AttractiveInfoWindowsAdapter attractiveInfoWindowsAdapter = new AttractiveInfoWindowsAdapter(getContext(),googleMap);
                    attractiveInfoWindowsAdapter.setMapaFragment(this);
                    googleMap.setInfoWindowAdapter(attractiveInfoWindowsAdapter);
                    googleMap.setOnInfoWindowClickListener(attractiveInfoWindowsAdapter);
                    googleMap.setOnInfoWindowLongClickListener(attractiveInfoWindowsAdapter);

                    // Agregar un marcadores de atractivos
                    for (int cont=0; cont < listAttractive.size(); cont++ ){
                        createMarkerForAttractive(listAttractive.get(cont));
                    }
                    googleMap.setOnMarkerClickListener(new OnMarkerClickListenerAdapter(getContext(),googleMap));
                    break;
                case "consultarAgenciasDeViajeEnElArea":
                case "consultarAlojamientoEnElArea":
                case "consultarComidaYBebidaEnElArea":
                case "consultarRecreacionDiversionEsparcimientoEnElArea":
                case "attractionOutsideHistoricCenterAction":
                    // Establecer la venta de informacion info_window_service
                    ServiceInfoWindowsAdapter serviceInfoWindowsAdapter = new ServiceInfoWindowsAdapter(getContext(),googleMap);
                    serviceInfoWindowsAdapter.setMapaFragment(this);
                    googleMap.setInfoWindowAdapter(serviceInfoWindowsAdapter);
                    googleMap.setOnInfoWindowClickListener(serviceInfoWindowsAdapter);
                    googleMap.setOnInfoWindowLongClickListener(serviceInfoWindowsAdapter);

                    // Agregar marcadores de servicios
                    for (int cont=0; cont < listService.size(); cont++ ){
                        createMarkerForService(listService.get(cont));
                    }
                    googleMap.setOnMarkerClickListener(new OnMarkerClickListenerAdapter(getContext(),googleMap));
                    break;
                case "attraction_information_intent.attraction_information_intent-yes":
                case "service_information_intent.service_information_intent-yes":
                    // Crear el marcador del punto Destino
                    if(attractive != null){
                        // Establecer la venta de informacion info_window_attractive
                        AttractiveInfoWindowsAdapter attractiveInfoWindows = new AttractiveInfoWindowsAdapter(getContext(),googleMap);
                        googleMap.setInfoWindowAdapter(attractiveInfoWindows);
                        // Crear marcador para la posicion del atractivo de destino
                        createMarkerForAttractive(attractive);
                    }else {
                        // Establecer la venta de informacion info_window_service
                        ServiceInfoWindowsAdapter serviceInfoWindows = new ServiceInfoWindowsAdapter(getContext(),googleMap);
                        googleMap.setInfoWindowAdapter(serviceInfoWindows);
                        // Crear marcador para la posicion del servicio de destino
                        createMarkerForService(service);
                    }

                    // Dibjuar la ruta en el mapa.
                    myOnInfoWindowsClickListener.distanciaGoogle(currentUserLatLng, getPointDestination());

                    // Posicionar la camara segun la ruta de donde se encuentre el usuario con el punto de destino.
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(currentUserLatLng);
                    builder.include(pointDestination);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 15));
                    break;
            }
        }
    }
    //Se muestra el popup para filtrar los actrativos
    private void crearPopupFiltro(){
        //We need to get the instance of the LayoutInflater, use the context of this activity
        LayoutInflater inflaterT = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Inflate the view from a predefined XML layout (no need for root id, using entire layout)
        final View popupfiltro = inflaterT.inflate(R.layout.popup_filtroatractivos,null);
        //Get the devices screen density to calculate correct pixel sizes
        float density= getContext().getResources().getDisplayMetrics().density;
        // create a focusable PopupWindow with the given layout and correct size
        final PopupWindow pw = new PopupWindow(popupfiltro, (int)density*300, (int)density*400, true);
        pw.showAtLocation(popupfiltro, Gravity.LEFT, 0, 0);
        listaFiltrarServicios.clear();
        listaFiltrarAtractivos.clear();
        //Actualizar lista de atractivos
        Switch sw_ManiCul = (Switch) popupfiltro.findViewById(R.id.sw_atractivo1);
        sw_ManiCul.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    listaFiltrarAtractivos.add("Manifestaciones Culturales");
                }else
                {
                    listaFiltrarAtractivos.remove("Manifestaciones Culturales");
                }
            }
        });
        Switch sw_Museo = (Switch) popupfiltro.findViewById(R.id.sw_atractivo2);
        sw_Museo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    listaFiltrarAtractivos.add("Museo");
                }else
                {
                    listaFiltrarAtractivos.remove("Museo");
                }
            }
        });
        Switch sw_Arqui = (Switch) popupfiltro.findViewById(R.id.sw_atractivo3);
        sw_Arqui.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    listaFiltrarAtractivos.add("Arquitectura");
                }else
                {
                    listaFiltrarAtractivos.remove("Arquitectura");
                }
            }
        });
        Switch sw_Parque = (Switch) popupfiltro.findViewById(R.id.sw_atractivo4);
        sw_Parque.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    listaFiltrarAtractivos.add("Parque");
                }else
                {
                    listaFiltrarAtractivos.remove("Parque");
                }
            }
        });

        //Actualizar lista de servicios
        Switch sw_Agencias = (Switch) popupfiltro.findViewById(R.id.sw_atractivo5);
        sw_Agencias.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    listaFiltrarServicios.add("Agencia de viajes");
                }else
                {
                    listaFiltrarServicios.remove("Agencia de viajes");
                }
            }
        });
        Switch sw_Alojamiento = (Switch) popupfiltro.findViewById(R.id.sw_atractivo6);
        sw_Alojamiento.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    listaFiltrarServicios.add("Alojamiento");
                }else
                {
                    listaFiltrarServicios.remove("Alojamiento");
                }
            }
        });
        Switch sw_ComiBebidas = (Switch) popupfiltro.findViewById(R.id.sw_atractivo7);
        sw_ComiBebidas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    listaFiltrarServicios.add("Comidas y bebidas");
                }else
                {
                    listaFiltrarServicios.remove("Comidas y bebidas");
                }
            }
        });
        Switch sw_Recreacion = (Switch) popupfiltro.findViewById(R.id.sw_atractivo8);
        sw_Recreacion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    listaFiltrarServicios.add("Recreación, diversión, esparcimiento");
                }else
                {
                    listaFiltrarServicios.remove("Recreación, diversión, esparcimiento");
                }
            }
        });

        //Estado de los switch
       /* if (!listaFiltrarAtractivos.isEmpty()){
            for (String itemlis: listaFiltrarAtractivos) {
                if (itemlis.equals("Manifestaciones Culturales")) {
                    sw_ManiCul.setChecked(true);
                }else
                if (itemlis.equals("Museo")){
                    sw_Museo.setChecked(true);
                }else
                if (itemlis.equals("Arquitectura")){
                    sw_Arqui.setChecked(true);
                }else
                if (itemlis.equals("Parque"))
                    sw_Parque.setChecked(true);
            }
        }
        if (!listaFiltrarServicios.isEmpty()){
            for (String itemlis: listaFiltrarServicios) {
                if (itemlis.equals("Agencia de viajes")) {
                    sw_Agencias.setChecked(true);
                }else
                if (itemlis.equals("Alojamiento")) {
                    sw_Alojamiento.setChecked(true);
                }else
                if (itemlis.equals("Comidas y bebidas")) {
                    sw_ComiBebidas.setChecked(true);
                }else
                if (itemlis.equals("Recreación, diversión, esparcimiento"))
                    sw_Recreacion.setChecked(true);
            }
        }*/

        Button btnCancelarFil = (Button) popupfiltro.findViewById(R.id.btn_cancelarfil);
        btnCancelarFil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listaFiltrarServicios.clear();
                listaFiltrarAtractivos.clear();
                pw.dismiss();
            }
        });
        Button btnFiltrar = (Button) popupfiltro.findViewById(R.id.btn_filtrar);
        btnFiltrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtrarMarkers(listaFiltrarAtractivos, listaFiltrarServicios);
                pw.dismiss();
            }
        });
    }

    //Filtra los markers con las preferencias del usuario
    private void filtrarMarkers(final ArrayList<String> listAtrac, final ArrayList<String> listServ){
        final OnMarkerClickListenerAdapter onMarkerClickListenerAdapter = new OnMarkerClickListenerAdapter(getContext(),googleMap);
        googleMap.clear();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Recupera lista de Areas Peligrosas
                for (DataSnapshot child: dataSnapshot.child("areaPeligrosa").getChildren()){

                    String nombreArea = child.child("nombre").getValue().toString();
                    String idArea = child.child("id").getValue().toString();
                    Double radio = Double.parseDouble(child.child("radio").getValue().toString());
                    Double latitud = Double.parseDouble(child.child("latitud").getValue().toString());
                    Double longitud = Double.parseDouble(child.child("longitud").getValue().toString());
                    AreaPeligrosa areaPeligrosa = new AreaPeligrosa(nombreArea, idArea, radio, latitud, longitud);
                    listaAreaPeligrosa.add(areaPeligrosa);
                    final LatLng dangerousArea = new LatLng(areaPeligrosa.getLatitud(), areaPeligrosa.getLongitud());
                    googleMap.addCircle(new CircleOptions()
                            .center(dangerousArea)
                            .radius(areaPeligrosa.getRadio())
                            .strokeColor(Color.RED)
                            .fillColor(0x220000FF)
                            .strokeWidth(5.0f));
                    //Equivalencias de distancia de GeoFire
                    // 0.1f = 0.1km = 100m
                    GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(dangerousArea.latitude, dangerousArea.longitude), 0.1f);
                    geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                        @Override
                        public void onKeyEntered(String key, GeoLocation location) {
                            sendNotification("Alerta", String.format("Entraste en un área Peligrosa"),dangerousArea);
                        }

                        @Override
                        public void onKeyExited(String key) {
                            sendNotification("Alerta", String.format(" Esta cerca un área Peligrosa"), dangerousArea);

                        }

                        @Override
                        public void onKeyMoved(String key, GeoLocation location) {
                            Log.d("Alerta", String.format(" moved within the dangerous area "));
                        }

                        @Override
                        public void onGeoQueryReady() {

                        }

                        @Override
                        public void onGeoQueryError(DatabaseError error) {
                            Log.e("ERROR", ""+error);
                        }
                    });

                }
                setListaAreaPeligrosa(listaAreaPeligrosa);
                //Crea marcadores de los atractivos
                for (DataSnapshot child: dataSnapshot.child("atractivo").getChildren()){
                    if (child.child("categoria").exists()){
                        String categoria = child.child("categoria").getValue().toString();
                        for (String itemlist: listAtrac) {
                            if (categoria.equals(itemlist)){
                                String nombreAtractivo = child.child("nombre").getValue().toString();
                                String descripcionAtractivo = child.child("descripcion").getValue().toString();
                                String snippit ="";
                                String pathImagen= "";
                                for(DataSnapshot galeria: child.child("galeria").getChildren()){
                                    pathImagen = galeria.child("imagenURL").getValue().toString();
                                }
                                snippit = descripcionAtractivo +"&##"+pathImagen+"&##"+child.getKey();
                                Coordenada coordenada =  child.child("posicion").getValue(Coordenada.class);
                                LatLng punto = new LatLng( coordenada.getLat(), coordenada.getLng());
                                MarkerOptions markerOptions = new  MarkerOptions().position(punto)
                                        .title(nombreAtractivo)
                                        .snippet(snippit)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_building_blue));
                                listaMarkadores.add(markerOptions);
                                googleMap.addMarker(markerOptions);
                            }
                        }
                    }
                }
                //Crea marcadores de los servicios
                for (DataSnapshot child: dataSnapshot.child("servicio").getChildren()){
                    if (child.child("categoria").exists()){
                        String categoria = child.child("categoria").getValue().toString();
                        for (String itemlist: listServ) {
                            if (categoria.equals(itemlist)){
                                String nombreAtractivo = child.child("nombre").getValue().toString();
                                String actividad = "Actividad: "+child.child("tipoDeActividad").getValue().toString();
                                Coordenada coordenada =  child.child("posicion").getValue(Coordenada.class);
                                LatLng punto = new LatLng( coordenada.getLat(), coordenada.getLng());
                                MarkerOptions markerOptions = new  MarkerOptions().position(punto)
                                        .title(nombreAtractivo)
                                        .snippet(actividad)
                                        .draggable(false)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_building_blue));
                                listaMarkadores.add(markerOptions);
                                googleMap.addMarker(markerOptions).setTag("service");
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });
        // For zooming automatically to the location of the marker
        googleMap.setOnMarkerClickListener(onMarkerClickListenerAdapter);
    }

    //Muestra el popup del clima actual
    private void createClima() {
        RequestQueue queue = Volley.newRequestQueue(getContext().getApplicationContext());
        String urlCurrentWeather = "https://api.apixu.com/v1/current.json?key=" + ChatBotReferences.APIXU_API_CLIENT + "&q=Latacunga&lang=es";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlCurrentWeather, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsonObjectCurrent = response.getJSONObject("current");
                    String humidity = jsonObjectCurrent.getInt("humidity") + "%"; // Porcentaje de la humedad.
                    String degreesC = Math.round(jsonObjectCurrent.getDouble("temp_c")) + "°C";
                    String currentCondition = jsonObjectCurrent.getJSONObject("condition").getString("text"); // Condición en la que se encuentra el clima.
                    //We need to get the instance of the LayoutInflater, use the context of this activity
                    LayoutInflater inflaterT = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    //Inflate the view from a predefined XML layout (no need for root id, using entire layout)
                    View popupclima = inflaterT.inflate(R.layout.popup_clima,null);
                    //Get the devices screen density to calculate correct pixel sizes
                    float density= getContext().getResources().getDisplayMetrics().density;
                    // create a focusable PopupWindow with the given layout and correct size
                    final PopupWindow pw = new PopupWindow(popupclima, (int)density*300, (int)density*100, true);
                    pw.showAtLocation(popupclima, Gravity.LEFT, 0, 0);
                    //Elementos para guardar nuevo sitio
                    TextView nombreCiudad = (TextView) popupclima.findViewById(R.id.tv_ciudad);
                    nombreCiudad.setText("Latacunga");
                    TextView climaCiudad = (TextView) popupclima.findViewById(R.id.tv_clima);
                    climaCiudad.setText("Clima:"+currentCondition);
                    TextView temCiudad = (TextView) popupclima.findViewById(R.id.tv_temperatura);
                    temCiudad.setText("Humedad: "+humidity+" ,"+degreesC);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error"+error);
            }
        });
        queue.add(jsonObjectRequest);
    }

    private void sendNotification(String title, String content, LatLng latlong) {
        Notification.Builder builder = new Notification.Builder(getActivity())
                                    .setSmallIcon(R.mipmap.ic_launcher_round)
                                    .setContentTitle(title)
                                    .setContentText(content);
        NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("OpenMapFragment", latlong.latitude+","+latlong.longitude);

        PendingIntent contentIntent = PendingIntent.getActivity(getActivity(),0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;

        manager.notify(new Random().nextInt(), notification);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();
    }

    @Override
    public void onMapLongClick(final LatLng point) {

        //We need to get the instance of the LayoutInflater, use the context of this activity
        LayoutInflater inflaterT = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Inflate the view from a predefined XML layout (no need for root id, using entire layout)
        View popupNuevoSitio = inflaterT.inflate(R.layout.popup_nuevositio,null);
        //Get the devices screen density to calculate correct pixel sizes
        float density= this.getResources().getDisplayMetrics().density;
        // create a focusable PopupWindow with the given layout and correct size
        final PopupWindow pw = new PopupWindow(popupNuevoSitio, (int)density*240, (int)density*285, true);
        pw.showAtLocation(popupNuevoSitio, Gravity.CENTER, 0, 0);
        //Elementos para guardar nuevo sitio
        edtNombre = (EditText) popupNuevoSitio.findViewById(R.id.txt_nombreSitio);
        edtDescripcion = (EditText) popupNuevoSitio.findViewById(R.id.txt_descripcionSitio);
        btnCancelar = (Button) popupNuevoSitio.findViewById(R.id.btn_cancelar);
        btnGuardar = (Button) popupNuevoSitio.findViewById(R.id.btn_guardar);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
            }
        });
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase = FirebaseDatabase.getInstance().getReference("cliente").child(userFirebase.getUid());
                String key = mDatabase.push().getKey();
                Coordenada coordenada = new Coordenada(point.latitude, point.longitude);
                mDatabase.child("misSitios").child(key).setValue(new AtractivoModel(edtNombre.getText().toString(),
                                                            edtDescripcion.getText().toString(), coordenada,key));
                pw.dismiss();
                createMarkerForMySites();
                Toast.makeText(getActivity(),
                        "Nuevo Sitio " +edtNombre.getText().toString()+" creado ", Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
