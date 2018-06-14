package com.example.jona.latacungadigital.Activities.Fragments;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.jona.latacungadigital.Activities.Adapters.CustomInfoWindowsAdapter;
import com.example.jona.latacungadigital.Activities.Adapters.MyOnInfoWindowsClickListener;
import com.example.jona.latacungadigital.Activities.Adapters.OnMarkerClickListenerAdapter;
import com.example.jona.latacungadigital.Activities.Clases.AttractiveClass;
import com.example.jona.latacungadigital.Activities.Clases.ServiceClass;
import com.example.jona.latacungadigital.Activities.Permisos.EstadoGPS;
import com.example.jona.latacungadigital.Activities.modelos.Coordenada;
import com.example.jona.latacungadigital.EstructuraDatos.AreaPeligrosa;
import com.example.jona.latacungadigital.EstructuraDatos.Cliente;

import com.example.jona.latacungadigital.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class MapaFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    // Variables de la clase
    private boolean isSerchFromChatBot = false;
    private String chatBotAction;
    private ArrayList<AttractiveClass> listAttractive;
    private ArrayList<ServiceClass> listService;
    private AttractiveClass attractive;
    private LatLng pointDestination;

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

    private static int UPDATE_INTERVAL = 5000;
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    GeoFire geoFire;



    // Variables de firebase
    private DatabaseReference mDatabase;
    private FirebaseUser userFirebase = FirebaseAuth.getInstance().getCurrentUser();
    private static ArrayList<MarkerOptions> listaMarkadores = new ArrayList<MarkerOptions>();
    private static ArrayList<AreaPeligrosa> listaAreaPeligrosa = new ArrayList<AreaPeligrosa>();

    public static ArrayList<AreaPeligrosa> getListaAreaPeligrosa() {
        return listaAreaPeligrosa;
    }

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

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);

        //obtener permisos de ubicacion

        /*System.out.println("empezo1");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String key = mDatabase.child("areaPeligrosa").push().getKey();
        AreaPeligrosa arePe= new AreaPeligrosa("La Estacion",key, 100, -0.932349, -78.620495);
        //Cliente cliente = new Cliente( user.getDisplayName(),user.getEmail(),user.getUid(),user.getPhotoUrl().toString()); //instancia de cliente
        FirebaseDatabase mFirebaseInstance = FirebaseDatabase.getInstance();
        final DatabaseReference mFirebaseDatabase = mFirebaseInstance.getReference();
        mFirebaseDatabase.child("areaPeligrosa").child(key).setValue(arePe);//guarda la informacion en firebase
        System.out.println("empezo2");*/
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
        if ( ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED){
            //Request runtime Location
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
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
        /*if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }*/
        //Validar si la aplicacion tiene el permiso de Localizacion
        if ( ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            return;
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
                    System.out.println("empezo"+areaPeligrosa.getNombre());
                    listaAreaPeligrosa.add(areaPeligrosa);


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
        markerOptions.snippet(service.getTypeOfActivity());
        markerOptions.draggable(false);
        if(service.getIcon() != 0){ // Validar si existe un icono predefinido del servicio
            markerOptions.icon(BitmapDescriptorFactory.fromResource(service.getIcon()));
        }
        googleMap.addMarker(markerOptions);
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
        googleMap.addMarker(markerOptions);
    }

    //Crear un area Peligrosa en el mapa
    private void crearAreaPeligrosa(AreaPeligrosa area){
        //Crear una Area Peligrosa

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

        // Validar si la aplicacion tiene el permiso de Localizacion
        if ( ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            return;
        }

        googleMap.setMyLocationEnabled(true); // Habilitar el boton de "Mover a mi ubicacion"

        EstadoGPS estadoGPS = new EstadoGPS(getContext(), googleMap); // Variable para obtener la locacion donde se encuentra el usuario.
        estadoGPS.getCurrentLocation(); // Leer las coordenadas actuales de donde se encunetra el usuario y almacenarlo en un metodo Setter.

        // Definir la posicion de la camara en el map
        LatLngBounds centroHistorico = new LatLngBounds(
                new LatLng(-0.9364, -78.6163), new LatLng(-0.9301, -78.6129));
        if (!isSerchFromChatBot) {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(centroHistorico.getCenter()).zoom(15).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } else {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centroHistorico.getCenter(), 15));
        }

        // Habilitar las ventanas de información sobre los marcadores
        MyOnInfoWindowsClickListener myOnInfoWindowsClickListener= new MyOnInfoWindowsClickListener(getContext(),googleMap);

        if (!isSerchFromChatBot) { // Si NO es una solicitud de consulta del chatbot
            // Habilitar las ventanas de información sobre los marcadores
            googleMap.setInfoWindowAdapter(new CustomInfoWindowsAdapter(getContext()));
            googleMap.setOnInfoWindowClickListener( myOnInfoWindowsClickListener);
            googleMap.setOnInfoWindowLongClickListener(myOnInfoWindowsClickListener);

            dataFirebase(); // Agregar marcadores de atractivos

            //Instanciar las areas de peligro
            for (AreaPeligrosa areaP: getListaAreaPeligrosa()) {
                LatLng dangerousArea = new LatLng(areaP.getLatitud(), areaP.getLongitud());
                googleMap.addCircle(new CircleOptions()
                        .center(dangerousArea)
                        .radius(areaP.getRadio())
                        .strokeColor(Color.RED)
                        .fillColor(0x220000FF)
                        .strokeWidth(5.0f)
                );
                //Equivalencias de distancia de GeoFire
                // 0.1f = 0.1km = 100m
                GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(dangerousArea.latitude, dangerousArea.longitude), 0.1f);
                geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                    @Override
                    public void onKeyEntered(String key, GeoLocation location) {
                        sendNotification("Alerta", String.format("Entraste en un área Peligrosa"));
                    }

                    @Override
                    public void onKeyExited(String key) {
                        sendNotification("Alerta", String.format(" Esta cerca un área Peligrosa"));

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
        } else { // SI es una solicitud de consulta del chatbot
            switch (chatBotAction){
                case "consultarAtractivoEnElArea":
                    // Agregar un marcadores de atractivos
                    for (int cont=0; cont < listAttractive.size(); cont++ ){
                        createMarkerForAttractive(listAttractive.get(cont));
                    }
                    break;
                case "consultarAlojamientoEnElArea":
                case "consultarComidaYBebidaEnElArea":
                    // Agregar un marcadores de servicios
                    for (int cont=0; cont < listService.size(); cont++ ){
                        createMarkerForService(listService.get(cont));
                    }
                    break;
                case "churchShowLocationAction":
                    // Dibjuar la ruta en el mapa.
                    if (getPointDestination() != null) {
                        if (estadoGPS.getCurrentLatLng() != null) { // Si esta permitido la ubicacion del usuario se pone las coordenadas de donde se encuentra.
                            myOnInfoWindowsClickListener.distanciaGoogle(estadoGPS.getCurrentLatLng(), getPointDestination()); // Dibujar la ruta en el mapa.
                        } else { // Si no esta permitido la ubicacion actual del usario se pone un punto preestablecido.
                            myOnInfoWindowsClickListener.distanciaGoogle(estadoGPS.puntoOrigen, getPointDestination()); // Dibujar la ruta en el mapa.
                        }
                    }
                    createMarkerForAttractive(attractive); // Crear el marcador del punto Destino
                    // Crear un marcador en la posicion del usuario
                    if (getPointDestination() != null) {
                        // Si esta permitido la ubicacion del usuario se pone las coordenadas de donde se encuentra.
                        if (estadoGPS.getCurrentLatLng() != null) {
                            createMarkerForUser(estadoGPS.getCurrentLatLng(),
                                    myOnInfoWindowsClickListener.getCompleteAddressString(estadoGPS.getCurrentLatLng().latitude,
                                            estadoGPS.getCurrentLatLng().longitude)); // Para obtener la direccion en donde se encuentra el usuario.
                        } else { // Si no esta permitido la ubicacion actual del usario se pone un punto preestablecido.
                            createMarkerForUser(estadoGPS.puntoOrigen,
                                    myOnInfoWindowsClickListener.getCompleteAddressString(estadoGPS.puntoOrigen.latitude,
                                            estadoGPS.puntoOrigen.longitude)); // Para obtener la direccion en donde se encuentra el usuario.
                        }
                    }

                    // Posicionar la camara segun la ruta de donde se encuentre el usuario con el punto de destino.
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(estadoGPS.getCurrentLatLng());
                    builder.include(new LatLng(attractive.getLatitude(), attractive.getLongitude()));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 15));
                    break;
            }
        }
    }

    private void sendNotification(String title, String content) {
        Notification.Builder builder = new Notification.Builder(getActivity())
                                    .setSmallIcon(R.mipmap.ic_launcher_round)
                                    .setContentTitle(title)
                                    .setContentText(content);
        NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(getActivity(),MapaFragment.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getActivity(),0,intent,PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(contentIntent);
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;

        manager.notify(new Random().nextInt(),notification);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
