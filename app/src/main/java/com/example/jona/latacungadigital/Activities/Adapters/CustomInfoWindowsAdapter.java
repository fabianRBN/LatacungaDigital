package com.example.jona.latacungadigital.Activities.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jona.latacungadigital.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by fabia on 25/05/2018.
 */

public class CustomInfoWindowsAdapter implements GoogleMap.InfoWindowAdapter  {

    private final View mWindow;
    private Context mContext;




    public CustomInfoWindowsAdapter(Context context) {
        mContext =  context;
        this.mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window,null);


    }

    @SuppressLint("InflateParams")
    private void rendowWindowsText(final Marker marker, final View view){

        String titulo = marker.getTitle();
        TextView tvTitulo = (TextView) view.findViewById(R.id.str_titulo);
        if(!titulo.equals("")){
            tvTitulo.setText(titulo);
        }
        String snippet = marker.getSnippet();
        TextView tvSnippet = (TextView) view.findViewById(R.id.str_descripcion);
        final ImageView imgSnppet = (ImageView) view.findViewById(R.id.img_atractivo);
        if(!snippet.equals("")){
            String[] parametros = snippet.split("&##");

            tvSnippet.setText(parametros[0]);
            final String pathImagen = parametros[1];
            Picasso.get().load(parametros[1]).networkPolicy(NetworkPolicy.OFFLINE).into(imgSnppet, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(pathImagen).into(imgSnppet);
                }
            });


        }

    }

    @Override
    public View getInfoWindow(Marker marker) {
        rendowWindowsText(marker,mWindow);
        System.out.println("GetInfo windows");
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        rendowWindowsText(marker,mWindow);
        System.out.println("GetInfoConten windows");
        return mWindow;
    }



}
