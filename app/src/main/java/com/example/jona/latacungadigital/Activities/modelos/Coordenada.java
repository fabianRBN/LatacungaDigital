package com.example.jona.latacungadigital.Activities.modelos;

/**
 * Created by fabia on 23/05/2018.
 */

public class Coordenada {
    double lat;
    double lng;

    public Coordenada(){

    }
    public Coordenada(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}