package com.example.jona.latacungadigital.Activities.Fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jona.latacungadigital.Activities.Adapters.CustomInfoWindowsAdapter;
import com.example.jona.latacungadigital.Activities.Adapters.MyOnInfoWindowsClickListener;
import com.example.jona.latacungadigital.Activities.Adapters.OnMarkerClickListenerAdapter;
import com.example.jona.latacungadigital.Activities.Clases.AttractiveClass;
import com.example.jona.latacungadigital.Activities.Clases.ServiceClass;
import com.example.jona.latacungadigital.Activities.Permisos.EstadoGPS;
import com.example.jona.latacungadigital.Activities.modelos.Coordenada;
import com.example.jona.latacungadigital.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapaFragment extends Fragment implements OnMapReadyCallback{

    // Variables de la clase
    private boolean isSerchFromChatBot = false;
    private ArrayList<ServiceClass> listService;
    private AttractiveClass attractive;
    private LatLng pointDestination;

    // Variables de mapa
    MapView mMapView;
    private GoogleMap googleMap;
    private LocationManager locationManager;
    private Marker myPositionMarker;


    // Variables de firebase
    private DatabaseReference mDatabase;
    private static ArrayList<MarkerOptions> listaMarkadores = new ArrayList<MarkerOptions>();


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
        return rootView;
    }

    public void setSerchFromChatBot(boolean serchFromChatBot) {
        isSerchFromChatBot = serchFromChatBot;
    }

    public void setListService(ArrayList<ServiceClass> listService) {
        this.listService = listService;
    }

    public void setAttractive(AttractiveClass attractive) {
        this.attractive = attractive;
    }

    public void dataFirebase(){
        final OnMarkerClickListenerAdapter onMarkerClickListenerAdapter = new OnMarkerClickListenerAdapter(getContext(),googleMap);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("atractivo");
        mDatabase.keepSynced(true);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child: dataSnapshot.getChildren()){

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

    // Crear un marcador en el mapa de acuerdo a un atraactivo
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
        EstadoGPS estadoGPS = new EstadoGPS(getContext(), googleMap); // Variable para obtener la locacion donde se encuentra el usuario.
        estadoGPS.getCurrentLocation(); // Leer las coordenadas actuales de donde se encunetra el usuario y almacenarlo en un metodo Setter.

        // Validar si la aplicacion tiene el permiso de Localizacion
        if ( ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            return;
        }
        googleMap.setMyLocationEnabled(true); // Habilitar el boton de "Mover a mi ubicacion"

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

        if (!isSerchFromChatBot) {
            googleMap.setInfoWindowAdapter(new CustomInfoWindowsAdapter(getContext()));
            googleMap.setOnInfoWindowClickListener( myOnInfoWindowsClickListener);
            googleMap.setOnInfoWindowLongClickListener(myOnInfoWindowsClickListener);
        } else {
            // Para dibjuar la ruta en el mapa.
            if (getPointDestination() != null) {

                if (estadoGPS.getCurrentLatLng() != null) { // Si esta permitido la ubicacion del usuario se pone las coordenadas de donde se encuentra.
                    myOnInfoWindowsClickListener.distanciaGoogle(estadoGPS.getCurrentLatLng(), getPointDestination()); // Dibujar la ruta en el mapa.
                } else { // Si no esta permitido la ubicacion actual del usario se pone un punto preestablecido.
                    myOnInfoWindowsClickListener.distanciaGoogle(estadoGPS.puntoOrigen, getPointDestination()); // Dibujar la ruta en el mapa.
                }

            }
        }

        // Agregar marcadores en puntos del mapa
        if (!isSerchFromChatBot) {
            dataFirebase(); // Agregar marcadores de atractivos
        } else {
            if (listService != null) {
                for (int cont=0; cont < listService.size(); cont++ ){ // Agregar un marcadores de servicios
                    createMarkerForService(listService.get(cont));
                }
            } else if (attractive != null) {
                createMarkerForAttractive(attractive);

                // Se dibuja los puntos del usuario.
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
            }
        }
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
