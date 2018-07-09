package com.example.jona.latacungadigital.Activities.Permisos;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by fabia on 28/05/2018.
 */

public class AccesoInternet {

    // Variables de la clase
    private static Context context;
    private static AccesoInternet instance = new AccesoInternet();

    // Varaibles para revisar la coneccion
    private ConnectivityManager connectivityManager;
    private NetworkInfo activeNetwork;
    private boolean isConnected  = false;
    private boolean hasInternet = false;
    private boolean isOnline = false;

    public static AccesoInternet getInstance(Context ctx){
        context = ctx.getApplicationContext();
        return instance;
    }

    // Revisa si tiene conexión con alguna red o datos y con Internet
    public boolean isOnline(){
        isConnectedToNetwork();
        if(isConnected){
            isConectedToInternet();
        }
        isOnline = (isConnected && hasInternet);
        return isOnline;
    }

    // Revisa si tiene conexión con Internet
    public Boolean isConectedToInternet() {
        try {
            Process ipProcess = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");
            int exitValue      = ipProcess.waitFor();
            hasInternet = (exitValue == 0);
            if(!hasInternet){
                //Toast.makeText(context,"No tienes conexión con internet",Toast.LENGTH_SHORT).show();
            }
            return hasInternet;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return hasInternet;
    }

    // Revisa si tiene conexión con alguna red o datos
    public boolean isConnectedToNetwork() {
        try {
            connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            activeNetwork = connectivityManager.getActiveNetworkInfo();
            isConnected = activeNetwork != null && activeNetwork.isConnected();
            if(!isConnected){
                //Toast.makeText(context,"Error de conexión",Toast.LENGTH_SHORT).show();
            }
            return isConnected;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return isConnected;
    }
}
