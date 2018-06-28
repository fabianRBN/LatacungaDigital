package com.example.jona.latacungadigital;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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


    }


}
