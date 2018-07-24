package com.example.jona.latacungadigital.Activities.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.example.jona.latacungadigital.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.w3c.dom.Text;

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
        TextView txt_Descriptcion = (TextView) view.findViewById(R.id.str_descripcion);
        TextView txt_rating = (TextView) view.findViewById(R.id.txt_rating);
        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rb_rating);

        final ImageView imgSnppet = (ImageView) view.findViewById(R.id.img_atractivo);
        if(!snippet.equals("")){ // Valida que el marcador posea snippet " en este parametro se concateno la informacion necesaria para los info windows"
            String[] parametros = snippet.split("&##");
            txt_Descriptcion.setText(parametros[0]);
            txt_rating.setText(parametros[3]);
            float rating =  Float.parseFloat (parametros[3]);
            ratingBar.setRating(rating);
            // Set imagen en infowindows google maps
            Glide.with(mContext).load(parametros[1]).asBitmap().listener(new RequestListener<String, Bitmap>() {
                @Override
                public boolean onException(Exception e, String model, com.bumptech.glide.request.target.Target<Bitmap> target, boolean isFirstResource) {
                    e.printStackTrace();
                    return false;
                }

                @Override
                public boolean onResourceReady(Bitmap resource, String model, com.bumptech.glide.request.target.Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    if(!isFromMemoryCache) marker.showInfoWindow();
                    return false;
                }

            }).into(imgSnppet);


        }

    }

    @Override
    public View getInfoWindow(Marker marker) {
        if(marker.getTag() != null){
            return null;
        }
        rendowWindowsText(marker,mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        if (marker.getTag() != null){
            return null;
        }
        rendowWindowsText(marker,mWindow);
        System.out.println("GetInfoConten windows");
        return mWindow;
    }




    }
