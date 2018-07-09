package com.example.jona.latacungadigital.Activities.Adapters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.example.jona.latacungadigital.Activities.AtractivoActivity;
import com.example.jona.latacungadigital.Activities.Haversine.Haversine;
import com.example.jona.latacungadigital.Activities.modelos.AtractivoModel;
import com.example.jona.latacungadigital.Activities.modelos.Coordenada;
import com.example.jona.latacungadigital.R;

import java.util.ArrayList;

/**
 * Created by fabia on 03/06/2018.
 */

public class ListAtractivoAdapter extends BaseAdapter {

    Context context;
    ArrayList<AtractivoModel> listAtractivo = new ArrayList<>();
    private static LayoutInflater inflater = null;
    TextView txtTituloAtractivo, txtdistancia;
    ImageView imageView;
    LocationManager locationManager;
    Location location;

    Haversine haversine;
    Coordenada start;

    @SuppressLint("MissingPermission")
    public ListAtractivoAdapter(Context context, ArrayList<AtractivoModel> listAtractivo) {
        this.context = context;
        this.listAtractivo = listAtractivo;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        haversine = new Haversine();

        location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

        if(location!= null){
            start = new Coordenada(location.getLatitude(),location.getLongitude());
        }else
        {
            start = new Coordenada(0,0);
        }

    }



    @Override
    public int getCount() {
        return listAtractivo.size();
    }

    @Override
    public Object getItem(int position) {
        return listAtractivo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if(view == null)
            view = inflater.inflate(R.layout.atractivo_item,null);

        Coordenada end = listAtractivo.get(position).getPosicion();

        // Calculo de la distancia entre usuario y atractivo

        double distancia = (haversine.distance(start,end));
        txtdistancia = view.findViewById(R.id.txt_distancia);
        String distanciaStr = "";

        if(distancia > 1.1){
            distanciaStr = String.format("%.2f",distancia)+"km";
        }else{
            distanciaStr =String.format("%.2f",( distancia*1000))+"m";
        }

        txtdistancia.setText(distanciaStr);

        txtTituloAtractivo = view.findViewById(R.id.titulo_item_atractivo);
        txtTituloAtractivo.setText(listAtractivo.get(position).getNombre());
        imageView = (ImageView) view.findViewById(R.id.imgView_item_atractivo);
        Glide.with(context).load(listAtractivo.get(position).getPathImagen()).centerCrop().into(imageView);

        final String finalDistanciaStr = distanciaStr;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AtractivoActivity.class);
                intent.putExtra("atractivoKey", listAtractivo.get(position).getKey());
                intent.putExtra("atractivoNombre", listAtractivo.get(position).getNombre());
                intent.putExtra("distancia", finalDistanciaStr);
                context.startActivity(intent);
            }
        });
        return view;
    }
}
