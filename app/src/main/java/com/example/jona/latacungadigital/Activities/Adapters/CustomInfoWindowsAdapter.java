package com.example.jona.latacungadigital.Activities.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jona.latacungadigital.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

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


    private void rendowWindowsText(final Marker marker, View view){
        String titulo = marker.getTitle();
        TextView tvTitulo = (TextView) view.findViewById(R.id.str_titulo);
        if(!titulo.equals("")){
            tvTitulo.setText(titulo);
        }
        String snippet = marker.getSnippet();
        TextView tvSnippet = (TextView) view.findViewById(R.id.str_descripcion);
        ImageView imgSnppet = (ImageView) view.findViewById(R.id.img_atractivo);
        if(!snippet.equals("")){


            tvSnippet.setText(snippet);
        }

    }

    @Override
    public View getInfoWindow(Marker marker) {
        rendowWindowsText(marker,mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        rendowWindowsText(marker,mWindow);
        return mWindow;
    }
}
