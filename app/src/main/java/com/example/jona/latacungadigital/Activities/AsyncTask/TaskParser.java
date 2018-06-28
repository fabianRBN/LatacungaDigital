package com.example.jona.latacungadigital.Activities.AsyncTask;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.jona.latacungadigital.Activities.Parser.DirectionsParser;
import com.example.jona.latacungadigital.Activities.Permisos.AccesoInternet;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by fabia on 27/05/2018.
 */

public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>> >{

    GoogleMap googleMap;
    Context context;

    Polyline polyline;

    AccesoInternet accesoInternet;

    public AsyncResponse delegate = null;

    public TaskParser(GoogleMap googleMap, Context context, AsyncResponse delegate) {
        this.googleMap = googleMap;
        this.context = context;
        this.delegate = delegate;
        accesoInternet = new AccesoInternet();
    }



    public Polyline getPolyline() {
        return polyline;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }

    @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                if (accesoInternet.isNetDisponible(context)) {
                    jsonObject = new JSONObject(strings[0]);
                    DirectionsParser directionsParser = new DirectionsParser();
                    routes = directionsParser.parse(jsonObject);
                } else {
                    this.cancel(true); // Cancelamos el hilo si no hay internet existe un error.
                }

            } catch (JSONException e) {
                e.printStackTrace();
                this.cancel(true); // Cancelamos el hilo si hay un error un error.
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            //Get list route and display it into the map

            ArrayList points = null;

            PolylineOptions polylineOptions = null;

            if (lists != null) {
                for (List<HashMap<String, String>> path : lists) {
                    points = new ArrayList();
                    polylineOptions = new PolylineOptions();

                    for (HashMap<String, String> point : path) {
                        double lat = Double.parseDouble(point.get("lat"));

                        double lon = Double.parseDouble(point.get("lon"));
                        points.add(new LatLng(lat,lon));
                    }
                    polylineOptions.addAll(points);
                    polylineOptions.width(15);
                    polylineOptions.color(Color.BLUE);
                    polylineOptions.geodesic(true);
                }
            }

            if (polylineOptions!=null) {
                if(this.polyline != null){
                    polyline.remove();
                }

                Polyline polyline = googleMap.addPolyline(polylineOptions);
                this.polyline = polyline;
                delegate.processFinish(polyline);



            } else {
                Toast.makeText(context, "Direction not found!", Toast.LENGTH_SHORT).show();
            }

        }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    public interface AsyncResponse {
        void processFinish(Polyline output);
    }
}
