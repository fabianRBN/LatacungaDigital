package com.example.jona.latacungadigital.Activities.modelos;

public class TrackeadosModel {

    private String nombre, distancia;
    public String pathImagen;
    public String key;

    public TrackeadosModel(String nombre, String distancia, String pathImagen, String key) {
        this.nombre = nombre;
        this.distancia = distancia;
        this.pathImagen = pathImagen;
        this.key = key;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDistancia() {
        return distancia;
    }

    public void setDistancia(String distancia) {
        this.distancia = distancia;
    }

    public String getPathImagen() {
        return pathImagen;
    }

    public void setPathImagen(String pathImagen) {
        this.pathImagen = pathImagen;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
