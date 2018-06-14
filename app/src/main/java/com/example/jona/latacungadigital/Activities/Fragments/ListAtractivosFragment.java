package com.example.jona.latacungadigital.Activities.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.jona.latacungadigital.Activities.Adapters.ListAtractivoAdapter;
import com.example.jona.latacungadigital.Activities.modelos.AtractivoModel;
import com.example.jona.latacungadigital.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ListAtractivosFragment extends Fragment {

    private String FRAGMENT_TAG = "ListaAtractivos";

    private ListView listView;
    private DatabaseReference mDatabase;
    private OnFragmentInteractionListener mListener;
    public ArrayList<AtractivoModel> listaAtractivo = new ArrayList<>();
    private String filter = "";

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

        ConsultarAtractivos("");

        return view;
    }



    public void ConsultarAtractivos(String arg){

        mDatabase = FirebaseDatabase.getInstance().getReference().child("atractivo");
        mDatabase.keepSynced(true);
        Query query = mDatabase.orderByChild("nombre").startAt(arg).endAt(arg+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



                for (DataSnapshot child: dataSnapshot.getChildren()){
                    AtractivoModel atractivoModel = new AtractivoModel();
                    atractivoModel.setNombre(child.child("nombre").getValue().toString());
                    atractivoModel.setKey(child.getKey().toString());
                    for(DataSnapshot child_galeria: child.child("galeria").getChildren()){
                        atractivoModel.setPathImagen(child_galeria.child("imagenURL").getValue().toString());
                    }

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
