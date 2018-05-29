package com.example.jona.latacungadigital.Activities.AsyncTask;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by fabia on 27/05/2018.
 */

public class TaskRequestDirections  extends AsyncTask<String,Void,String> implements TaskParser.AsyncResponse {

    GoogleMap googleMap;
    Context context;
    Polyline polyline;

    public TaskRequestDirections(GoogleMap googleMap, Context context) {
        this.googleMap = googleMap;
        this.context = context;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }

    public String getRequestUrl(LatLng origen, LatLng destino){
        // Valor de origen
        String str_origen = "origin="+origen.latitude+","+origen.longitude;
        // Valor de destino
        String str_destino = "destination="+destino.latitude+","+destino.longitude;
        // Valor de sensor
        String sensor = "sensor=false";
        // Modo de direccion
        String mode = "mode=walking";
        // Construir parametros google api
        String param = str_origen+"&"+str_destino+"&"+sensor+"&"+mode;
        // Output api
        String output = "json";
        //Crear url request google api destino
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+param;

        return  url;
    }

    @Override
    protected String doInBackground(String... strings) {
        String responseString = "";
        try {
            responseString = requestDirection(strings[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  responseString;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        TaskParser taskParser = new TaskParser(googleMap,context,this);
        taskParser.execute(s);



    }

    public String requestDirection(String reqUrl) throws IOException {
        String responseString="";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try{
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            // Get request
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer  stringBuffer = new StringBuffer();
            String line = "";
            while((line = bufferedReader.readLine())!= null){
                stringBuffer.append(line);
            }
            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(inputStream != null){
                inputStream.close();
            }
            httpURLConnection.disconnect();

        }
        return  responseString;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }


    @Override
    public void processFinish(Polyline output) {

            setPolyline(output);
    }
}
