package com.example.jona.latacungadigital.Activities.Adapters;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by fabia on 26/05/2018.
 */

public class MyOnInfoWindowsClickListener implements GoogleMap.OnInfoWindowClickListener ,GoogleMap.OnInfoWindowLongClickListener {

    private Context context;

    public MyOnInfoWindowsClickListener(Context context) {
        this.context = context;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(context,
                "onInfoWindowClick():\n" +
                        marker.getPosition().latitude + "\n" +
                        marker.getPosition().longitude,
                Toast.LENGTH_LONG).show();
    }


    @Override
    public void onInfoWindowLongClick(Marker marker) {
        Toast.makeText(context,
                "Click largo:\n" +
                        marker.getPosition().latitude + "\n" +
                        marker.getPosition().longitude,
                Toast.LENGTH_LONG).show();
    }
}
