package com.example.jona.latacungadigital.Activities.Tabs_atractivo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.example.jona.latacungadigital.Activities.Adapters.ViewPagerAdapter;
import com.example.jona.latacungadigital.Activities.Haversine.Haversine;
import com.example.jona.latacungadigital.Activities.modelos.Coordenada;
import com.example.jona.latacungadigital.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import org.w3c.dom.Text;

/**
 * Created by fabia on 13/07/2018.
 */

public class Tab_detalle_atractivo extends Fragment {

    public TextView txtTitulo, txtCategoria, txtRatingTotal, txtTipo, txtSubtipo,txtDistancia, txtImpactoPositivo, txtImpactoNegativo;

    private DatabaseReference mDatabase;
    private FirebaseDatabase mFirebaseInstance;
    LocationManager locationManager;
    Location location;
    public String keyatractivo ;

    ReadMoreTextView readMoreTextView;

    Coordenada start;

    private RatingBar ratingBar;
    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_detalle_atractivo, container, false);

        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar_total);

        txtRatingTotal = (TextView) view.findViewById(R.id.txtRatingToatal);
        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes
        txtTitulo = (TextView) view.findViewById(R.id.txtTituloAtractivo);
        txtCategoria= (TextView) view.findViewById(R.id.txtCategoriaAtractivo);

        txtTipo = (TextView) view.findViewById(R.id.txtTipo);
        txtSubtipo = (TextView) view.findViewById(R.id.txtSubtipo);
        txtDistancia = (TextView) view.findViewById(R.id.txtDistancia);

        txtImpactoPositivo = (TextView) view.findViewById(R.id.txtImpactoPositivo);
        txtImpactoNegativo = (TextView) view.findViewById(R.id.txtImpactoNegativo);
        readMoreTextView = (ReadMoreTextView) view.findViewById(R.id.text_view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            readMoreTextView.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        }

        if(keyatractivo != null) {
            getAtractivo();
            locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if(location!= null){
                start = new Coordenada(location.getLatitude(),location.getLongitude());
            }else
            {
                start = new Coordenada(0,0);
            }
        }
        return view;
    }

    public void ratingTotal(String rating){
        txtRatingTotal.setText(rating);
    }


    public void  getAtractivo(){

        mDatabase = FirebaseDatabase.getInstance().getReference().child("atractivo").child(keyatractivo);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                txtTitulo.setText(dataSnapshot.child("nombre").getValue().toString());
                txtCategoria.setText(dataSnapshot.child("categoria").getValue().toString() );
                txtTipo.setText(dataSnapshot.child("tipo").getValue().toString());
                txtSubtipo.setText(dataSnapshot.child("subtipo").getValue().toString());
                txtImpactoNegativo.setText(dataSnapshot.child("impactoNegativo").getValue().toString());
                txtImpactoPositivo.setText(dataSnapshot.child("impactoPositivo").getValue().toString());
                float rating =  Float.parseFloat (dataSnapshot.child("rating").getValue().toString());
                ratingTotal(rating+"");
                ratingBar.setRating(rating);
                Haversine haversine = new Haversine();
                Double distancia =  haversine.distance(start,new Coordenada(
                    Double.parseDouble(dataSnapshot.child("posicion").child("lat").getValue().toString()),
                    Double.parseDouble(dataSnapshot.child("posicion").child("lng").getValue().toString())
                ));
                String distanciaStr = "0km";
                if(distancia > 1.1){
                    distanciaStr = String.format("%.2f",distancia)+"km";
                }else{
                    distanciaStr =String.format("%.2f",( distancia*1000))+"m";
                }
                txtDistancia.setText(distanciaStr);
                readMoreTextView.setText(dataSnapshot.child("descripcion").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
