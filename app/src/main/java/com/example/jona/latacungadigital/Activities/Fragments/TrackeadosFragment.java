package com.example.jona.latacungadigital.Activities.Fragments;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jona.latacungadigital.Activities.Adapters.TrackeadosAdapter;
import com.example.jona.latacungadigital.Activities.Adapters.TrackingAdapter;
import com.example.jona.latacungadigital.Activities.Haversine.Haversine;
import com.example.jona.latacungadigital.Activities.modelos.Coordenada;
import com.example.jona.latacungadigital.Activities.modelos.TrackeadosModel;
import com.example.jona.latacungadigital.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TrackeadosFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private OnFragmentInteractionListener mListener;
    private static ArrayList<TrackeadosModel> listaAmigosAutrizados = new ArrayList<>();
    DatabaseReference mdatabase;
    private FirebaseAuth userFirebase;
    RecyclerView recyclerView;
    private LatLng currentUserLatLng;
    Haversine haversine;

    public TrackeadosFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trackeados, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_friendsAutorizados);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        userFirebase = FirebaseAuth.getInstance();
        final String uid = userFirebase.getCurrentUser().getUid();
        ConsultaAmigosAutorizados(uid);

        //Flotante que redirige al mapa
        FloatingActionButton fabMapa = (FloatingActionButton) view.findViewById(R.id.fab_autorizados);
        fabMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Se presionó el FAB", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                MapaFragment mf = new MapaFragment();
                //fr.setArguments(bn);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.main_fragment, mf);
                transaction.addToBackStack(null);

                // Commit a la transacción
                transaction.commit();
            }
        });


        return view;
    }

    private void ConsultaAmigosAutorizados(final String uid) {
        final ArrayList<String> listaidUsuarios = new ArrayList<>();
        listaidUsuarios.clear();
        listaAmigosAutrizados.clear();
        mdatabase = FirebaseDatabase.getInstance().getReference();
        //System.out.println("uid: "+userFirebase.getCurrentUser().getUid());
        mdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.child("meAutorizaron").child(uid).getChildren()){
                    listaidUsuarios.add(child.getKey());
                }
                for (String key: listaidUsuarios) {
                    String nombre = dataSnapshot.child("cliente").child(key).child("nombre").getValue().toString();
                    String pathimage = dataSnapshot.child("cliente").child(key).child("pathImagen").getValue().toString();
                    double latitud = Double.parseDouble(dataSnapshot.child("cliente").child(key)
                            .child("GeoFire").child("l").child("0").getValue().toString());
                    double longitud = Double.parseDouble(dataSnapshot.child("cliente").child(key)
                            .child("GeoFire").child("l").child("1").getValue().toString());
                    double milatitud = Double.parseDouble(dataSnapshot.child("cliente").child(uid)
                            .child("GeoFire").child("l").child("0").getValue().toString());
                    double milongitud = Double.parseDouble(dataSnapshot.child("cliente").child(uid)
                            .child("GeoFire").child("l").child("1").getValue().toString());

                    //Calculo de la distancia
                    haversine = new Haversine();
                    Coordenada inicial = new Coordenada(milatitud,milongitud);//mi coordenada
                    Coordenada end = new Coordenada(latitud,longitud);// la coordenada del trackeado
                    double distanciah = (haversine.distance(inicial,end));
                    String formatoDistancia = String.format("%.02f", distanciah);
                    String distancia = "distancia de ti: "+formatoDistancia+" km";

                    TrackeadosModel trac = new TrackeadosModel(nombre, distancia, pathimage, key);
                    listaAmigosAutrizados.add(trac);
                }
                recyclerView.setAdapter(new TrackeadosAdapter( listaAmigosAutrizados));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
}
