package com.example.jona.latacungadigital;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.jona.latacungadigital.Activities.PreferenciasActivity;
import com.example.jona.latacungadigital.Activities.Services.AtractivoService;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

/**
 * Created by fabia on 28/05/2018.
 */

public class LatacungaDigital  extends Application {

    SharedPreferences prefs;
    @Override
    public void onCreate() {
        super.onCreate();
        //Database persisten
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean datalocal = prefs.getBoolean("databaselocal", false);
        FirebaseDatabase.getInstance().setPersistenceEnabled(datalocal);

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean notificacionAtractivo = prefs.getBoolean("notificacionAtractivo", false);
        if(notificacionAtractivo){
            activarServicio();
        }


    }
    public void activarServicio(){
        if (!isMyServiceRunning(AtractivoService.class)){ //método que determina si el servicio ya está corriendo o no
            Intent serv = new Intent(getApplicationContext(),AtractivoService.class); //serv de tipo Intent
            getApplicationContext().startService(serv); //ctx de tipo Context
            Toast.makeText(getApplicationContext(),"App Service started",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(),"App Service already running",Toast.LENGTH_LONG).show();


        }
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}
