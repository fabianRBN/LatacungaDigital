package com.example.jona.latacungadigital.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.jona.latacungadigital.Activities.Services.AtractivoService;
import com.example.jona.latacungadigital.R;

import java.util.prefs.PreferenceChangeListener;

public class PreferenciasActivity extends PreferenceActivity {

    SharedPreferences prefs;
    private SharedPreferences.OnSharedPreferenceChangeListener mPreferenceListener ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);
        mPreferenceListener = new SharedPreferences.OnSharedPreferenceChangeListener(){

            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if(key.equals("notificacionAtractivo")) {
                    System.out.println("Preferencia listener:" + sharedPreferences.getBoolean(key,false));
                    if(sharedPreferences.getBoolean(key,false)){
                        System.out.println("Servicio intent start");
                        startService(new Intent(getApplicationContext(), AtractivoService.class));
                    }else{
                        System.out.println("Srvicio intent stop");
                        stopService(new Intent(getApplicationContext(), AtractivoService.class));
                    }
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(prefs == null){
            RegistrarPreferenciasenListener();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPreferenceListener !=null){
            prefs.unregisterOnSharedPreferenceChangeListener(mPreferenceListener);
        }
    }

    public void RegistrarPreferenciasenListener(){

        System.out.println("Preferencia activado:" );

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        prefs.registerOnSharedPreferenceChangeListener(mPreferenceListener);
    }


}
