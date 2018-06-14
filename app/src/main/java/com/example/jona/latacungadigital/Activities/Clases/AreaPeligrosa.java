package com.example.jona.latacungadigital.Activities.Clases;

public class AreaPeligrosa {
    private String nombre, id;
    private double radio, latitud, longitud;

    public AreaPeligrosa() {
    }

    public AreaPeligrosa(String nombre, String id, double radio, double latitud, double longitud) {
        this.nombre = nombre;
        this.id = id;
        this.radio = radio;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getRadio() {
        return radio;
    }

    public void setRadio(double radio) {
        this.radio = radio;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }
}
