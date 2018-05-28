package com.example.jona.latacungadigital.Activities.Fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.example.jona.latacungadigital.Activities.Parser.DirectionsParser;
import com.example.jona.latacungadigital.Activities.modelos.Coordenada;
import com.example.jona.latacungadigital.Manifest;
import com.example.jona.latacungadigital.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapaFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Variables de mapa
    MapView mMapView;
    private GoogleMap googleMap;



    // Variables de firebase
    private DatabaseReference mDatabase;
    private static ArrayList<MarkerOptions> listaMarkadores = new ArrayList<MarkerOptions>();



    private OnFragmentInteractionListener mListener;

    public MapaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapaFragment newInstance(String param1, String param2) {
        MapaFragment fragment = new MapaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_mapa, container, false);







        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }




        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button


                if ( ContextCompat.checkSelfPermission( getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 200);
                    return;
                    //googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                }
                googleMap.setMyLocationEnabled(true);
                googleMap.setInfoWindowAdapter(new CustomInfoWindowsAdapter(getContext()));
                googleMap.setOnInfoWindowClickListener( new MyOnInfoWindowsClickListener(getContext()));

                // For dropping a marker at a point on the Map

                final OnMarkerClickListenerAdapter onMarkerClickListenerAdapter = new OnMarkerClickListenerAdapter(getContext(),googleMap);


                mDatabase = FirebaseDatabase.getInstance().getReference();

                mDatabase.child("atractivo").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        int contador = 0;

                        LatLng puntoOrigen = new LatLng(-0.9337192,-78.6174786);

                        for (DataSnapshot child: dataSnapshot.getChildren()){

                            String nombreAtractivo = child.child("nombre").getValue().toString();
                            String descripcionAtractivo = child.child("descripcion").getValue().toString();
                            String snippit ="";
                            String pathImagen= "";
                            /*for(DataSnapshot galeria: child.child("galeria").getChildren()){
                                pathImagen = galeria.child("imagenURL").getValue().toString();

                            }

                            snippit = descripcionAtractivo +"&##"+pathImagen;*/
                            Coordenada coordenada =  child.child("posicion").getValue(Coordenada.class);
                            LatLng punto = new LatLng( coordenada.getLat(), coordenada.getLng());
                            System.out.println("Posiciones:"+ puntoOrigen+" "+punto );
                            MarkerOptions markerOptions = new  MarkerOptions().position(punto)
                                    .title(nombreAtractivo)
                                    .snippet(descripcionAtractivo);
                            listaMarkadores.add(markerOptions);
                            googleMap.addMarker(markerOptions);





                                /*String url = getRequestUrl(puntoOrigen,punto);
                                System.out.println("Url :"+url);
                                TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                                taskRequestDirections.execute(url);*/

                            puntoOrigen = punto;





                        }
                        onMarkerClickListenerAdapter.setListaMarkers(listaMarkadores);
                        onMarkerClickListenerAdapter.setGoogleMapTemporal(googleMap);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }


                });

                LatLng ultimopunto = new LatLng(-0.9337192,-78.6174786);


                // For zooming automatically to the location of the marker
                googleMap.setOnMarkerClickListener(onMarkerClickListenerAdapter);
                CameraPosition cameraPosition = new CameraPosition.Builder().target(ultimopunto).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));





            }
        });


        return rootView;
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
