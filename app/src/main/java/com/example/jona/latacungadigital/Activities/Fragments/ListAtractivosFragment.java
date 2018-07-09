package com.example.jona.latacungadigital.Activities.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jona.latacungadigital.Activities.Adapters.ListAtractivoAdapter;
import com.example.jona.latacungadigital.Activities.modelos.AtractivoModel;
import com.example.jona.latacungadigital.Activities.modelos.Coordenada;
import com.example.jona.latacungadigital.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ListAtractivosFragment extends Fragment {

    private String FRAGMENT_TAG = "ListaAtractivos"; // TAG de identificacion de Fragmento

    private ListView listView; //  listview donde se cargaran la lista de atractivos

    public ProgressBar progressBar; // Spinner indicador de la carga de atractivos

    private SwipeRefreshLayout swipeContainer; // permite recargar los datos

    private DatabaseReference mDatabase;
    private OnFragmentInteractionListener mListener;
    public ArrayList<AtractivoModel> listaAtractivo = new ArrayList<>(); // Array de atractivos
    private String filter = "";
    final AlertDialog alert = null;
    LocationManager manager;


    public ListAtractivosFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_atractivos, container, false);

        listView = (ListView) view.findViewById(R.id.listViewAtractivos);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar_atractivos);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.srlContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Update data in ListView
                        verificarGPS();
                        swipeContainer.setRefreshing(false);
                    }
                }, 3000);

            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Permite la verificacion del gps


        verificarGPS();

    }

    private void verificarGPS(){
        manager = (LocationManager) getContext().getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {


            showAlert();
        }else{
            ConsultarAtractivos("");
        }
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Localizacion desactivada")
                .setMessage("Su ubicación esta desactivada.\npor favor active su ubicación " +
                        "usa esta app")
                .setPositiveButton("Configuración de ubicación", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        getContext().startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    public void ConsultarAtractivos(String arg){

        progressBar.setVisibility(View.GONE);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("atractivo");
        mDatabase.keepSynced(true);
        Query query = mDatabase.orderByChild("nombre").startAt(arg).endAt(arg+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                listaAtractivo.clear();
                for (DataSnapshot child: dataSnapshot.getChildren()){
                    AtractivoModel atractivoModel = new AtractivoModel();
                    atractivoModel.setNombre(child.child("nombre").getValue().toString());
                    atractivoModel.setKey(child.getKey().toString());
                    for(DataSnapshot child_galeria: child.child("galeria").getChildren()){
                        atractivoModel.setPathImagen(child_galeria.child("imagenURL").getValue().toString());
                    }
                    atractivoModel.setPosicion(child.child("posicion").getValue(Coordenada.class));

                    listaAtractivo.add(atractivoModel);
                }

                if(getContext()!= null) {
                    listView.setAdapter(new ListAtractivoAdapter(getContext(), listaAtractivo));
                }

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


    @Override
    public void onPause() {
        super.onPause();
        if(manager!= null){
            manager = (LocationManager) getContext().getSystemService( Context.LOCATION_SERVICE );
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
}
