package com.example.jona.latacungadigital.Activities.Fragments;

import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.jona.latacungadigital.Activities.Adapters.TrackingAdapter;
import com.example.jona.latacungadigital.Activities.Clases.TrackingClass;
import com.example.jona.latacungadigital.Activities.modelos.TrackinModel;
import com.example.jona.latacungadigital.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class TrackinFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters


    private OnFragmentInteractionListener mListener;
    private static ArrayList<TrackinModel> listaAmigos = new ArrayList<>();
    DatabaseReference mdatabase;
    private FirebaseAuth userFirebase;
    RecyclerView recyclerView;
    FloatingActionButton btnLista, btnMapa;
    private static final ArrayList<String> listaUsuarios = new ArrayList<>();

    public TrackinFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userFirebase = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trackin, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_friends);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        btnLista = (FloatingActionButton) view.findViewById(R.id.btn_listaAutorizados);
        btnMapa = (FloatingActionButton) view.findViewById(R.id.btn_irmapa);

        //Boton para llevar a lista de trackeados
        btnLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrackeadosFragment tf = new TrackeadosFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.main_fragment, tf);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        //Boton para ir al mapa
        btnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapaFragment mf = new MapaFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.main_fragment, mf).commit();
                Bundle data = new Bundle();
                data.putString("trackeo", "1000");
                mf.setArguments(data);
            }
        });

        userFirebase = FirebaseAuth.getInstance();
        final String uid = userFirebase.getCurrentUser().getUid();
        ConsultaAmigos(uid);
        ConsultaUsuarios(uid);

        //Autocompletar
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line, listaUsuarios);
        final AutoCompleteTextView textView = (AutoCompleteTextView)
                view.findViewById(R.id.tv_buscar);

        textView.setAdapter(adapter);
        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final String nombreLista = adapter.getItem(position);
                mdatabase = FirebaseDatabase.getInstance().getReference();
                mdatabase.child("cliente").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot chil: dataSnapshot.getChildren()) {
                            if (chil.child("nombre").exists()){
                                String nombreConsulta = chil.child("nombre").getValue().toString();
                                if (nombreConsulta.equals(nombreLista)){
                                    mdatabase.child("autorizados").child(uid).child(chil.getKey()).
                                            setValue(new TrackinModel(chil.getKey(), false));
                                    mdatabase.child("meAutorizaron").child(chil.getKey()).child(uid).
                                            setValue(new TrackinModel(uid, false));
                                }
                            }
                        }
                        textView.setText("");
                        ConsultaAmigos(uid);
                        view.refreshDrawableState();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });
            }
        });



        return view;


    }

    private void ConsultaUsuarios(final String uid) {
        listaUsuarios.clear();
        mdatabase = FirebaseDatabase.getInstance().getReference("cliente");
        mdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()){
                    if (child.child("nombre").exists()){
                        if (!child.getKey().equals(uid))
                            listaUsuarios.add(child.child("nombre").getValue().toString());
                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void ConsultaAmigos(final String uid) {
        final ArrayList<String> listaidUsuarios = new ArrayList<>();

        mdatabase = FirebaseDatabase.getInstance().getReference();
        mdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaidUsuarios.clear();
                listaAmigos.clear();
                for (DataSnapshot child: dataSnapshot.child("autorizados").child(uid).getChildren()){
                    listaidUsuarios.add(child.getKey());
                }
                for (String key: listaidUsuarios) {
                    String nombre = dataSnapshot.child("cliente").child(key).child("nombre").getValue().toString();
                    String email = dataSnapshot.child("cliente").child(key).child("email").getValue().toString();
                    String pathimage = dataSnapshot.child("cliente").child(key).child("pathImagen").getValue().toString();
                    boolean autorizacion = Boolean.parseBoolean(dataSnapshot.child("autorizados").child(uid).child(key).child("autorizacion").getValue().toString());
                    TrackinModel trac = new TrackinModel(nombre, email, pathimage, key, autorizacion);
                    listaAmigos.add(trac);
                }
                recyclerView.setAdapter(new TrackingAdapter( listaAmigos));
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
