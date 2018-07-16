package com.example.jona.latacungadigital.Activities.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidadvance.topsnackbar.TSnackbar;
import com.example.jona.latacungadigital.Activities.Adapters.ListAtractivoAdapter;
import com.example.jona.latacungadigital.Activities.Clases.NetworkReceiverClass;
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
import java.util.Objects;


public class ListAtractivosFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener  {

    private String FRAGMENT_TAG = "ListaAtractivos"; // TAG de identificacion de Fragmento

    private ListView listView; //  listview donde se cargaran la lista de atractivos

    public ProgressBar progressBar; // Spinner indicador de la carga de atractivos

    private SwipeRefreshLayout swipeContainer; // permite recargar los datos

    private DatabaseReference mDatabase;

    private NetworkReceiverClass networkReceiverClass;
    private boolean receiversRegistered; // Variable para controlar el brodcast de la conexion a internet.


    private TSnackbar snackbar; // muestra la notificacion de  conneccion a internet o wifi
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

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_atractivos, container, false);


        listView = (ListView) view.findViewById(R.id.listViewAtractivos);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar_atractivos);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.srlContainer);

        swipeContainer.setOnRefreshListener(this);
        //Podemos espeficar si queremos, un patron de colores diferente al patrón por defecto.
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // Se envia false para no mostrar el mensaje de "Conexión exitosa" al inicio de la actividad.
        SetupActionBar(false); // Para dar el titulo y el subtitulo que va a tener el Action Bar.

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

    @Override
    public void onResume() {
        super.onResume();
        if (!receiversRegistered) {
            if (networkReceiverClass.isStatusTSnackbar()) { // Para saber el estado actual de internet.
                SetupActionBar(false); // Si existe internet no mostramos el TSnackbar.
            } else {
                SetupActionBar(true); // Si no hay internet mostramos el TSnackbar con su respectivo mensaje.
            }
        }
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
                    if(child.child("funcionAR").getValue() != null){
                        atractivoModel.funcionAR = true;
                    }else{
                        atractivoModel.funcionAR = false;
                    }
                    if(child.child("imagen360").getValue() != null){
                        atractivoModel.funcionVR = true;
                    }else{
                        atractivoModel.funcionVR = false;
                    }
                    atractivoModel.setSubtipo(child.child("subtipo").getValue().toString());
                    atractivoModel.setRating(Float.parseFloat(child.child("rating").getValue().toString()));
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
    public void onDestroy() {
        super.onDestroy();
        if (receiversRegistered) Objects.requireNonNull(getActivity()).unregisterReceiver(networkReceiverClass);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Para parar la comunicación del broadcast cuando se pare la actividad.
        if (receiversRegistered) {
            Objects.requireNonNull(getActivity()).unregisterReceiver(networkReceiverClass);
            receiversRegistered = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(manager!= null){
            manager = (LocationManager) getContext().getSystemService( Context.LOCATION_SERVICE );
        }

    }
    private void SetupActionBar(boolean statusTSnackbar) {

            // Registra la actividad del la conexion a internet
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            // Registra la actividad del GPS
            IntentFilter filterGPS = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);

            networkReceiverClass = new NetworkReceiverClass(null, getActivity(), statusTSnackbar);

            if (!receiversRegistered) {
                //Registro de los filtros
                getActivity().registerReceiver(networkReceiverClass, filter);
                getActivity().registerReceiver(networkReceiverClass, filterGPS);
                receiversRegistered = true;
            }
            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    int filaSuperior = (
                            listaAtractivo == null//Si la lista esta vacía ó
                                    || listView.getChildCount() == 0) ? 0 : listView.getChildAt(0).getTop();//Estamos en el elemento superior
                    swipeContainer.setEnabled(filaSuperior >= 0); //Activamos o desactivamos el swipe layout segun corresponda
                }
            });
    }



    @Override
    public void onRefresh() {
        //Aqui ejecutamos el codigo necesario para refrescar nuestra interfaz grafica.
        //Antes de ejecutarlo, indicamos al swipe layout que muestre la barra indeterminada de progreso.
        swipeContainer.setRefreshing(true);

        //Vamos a simular un refresco con un handle.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                //Se supone que aqui hemos realizado las tareas necesarias de refresco, y que ya podemos ocultar la barra de progreso
                swipeContainer.setRefreshing(false);

            }
        }, 3000);

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


