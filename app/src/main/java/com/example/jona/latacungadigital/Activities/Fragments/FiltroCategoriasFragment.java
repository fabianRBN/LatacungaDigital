package com.example.jona.latacungadigital.Activities.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jona.latacungadigital.Activities.Adapters.CategoriasAdapter;
import com.example.jona.latacungadigital.Activities.modelos.CategoriaModel;
import com.example.jona.latacungadigital.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FiltroCategoriasFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private static ArrayList<CategoriaModel> listaCategorias = new ArrayList<>();
    DatabaseReference mdatabase;
    RecyclerView recyclerView;

    public FiltroCategoriasFragment() {
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
        View view = inflater.inflate(R.layout.fragment_filtro_categorias, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_categoriasAtractivos);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        ConsultaCategorias();

        //Flotante que redirige al mapa
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab_irMapa);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*ArrayList<String> milista = new ArrayList<String>();
                Intent intent = new Intent(getContext(), MapaFragment.class);
                intent.putExtra("miLista", listaCategorias);
                startActivity(intent);*/

                MapaFragment mf = new MapaFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.main_fragment, mf);
                transaction.addToBackStack(null);

                // Commit a la transacción
                transaction.commit();
            }
        });

        return view;
    }

    private void ConsultaCategorias() {
        listaCategorias.clear();
        mdatabase = FirebaseDatabase.getInstance().getReference("categoria");
        mdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot chil: dataSnapshot.getChildren()) {
                        String nombre = chil.child("nombre").getValue().toString();
                        String pathImagen = chil.child("nombre").getValue().toString();
                        String key = chil.getKey();
                        listaCategorias.add(new CategoriaModel(nombre, pathImagen, key));
                    }
                    recyclerView.setAdapter(new CategoriasAdapter(listaCategorias));
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
