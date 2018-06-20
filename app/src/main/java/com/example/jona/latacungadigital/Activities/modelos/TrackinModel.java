package com.example.jona.latacungadigital.Activities.modelos;

public class TrackinModel {
    private String nombre, email;
    public String pathImagen;
    public String key;
    public boolean autorizacion;

    public TrackinModel() {
    }

    public TrackinModel(String nombre, String email, String pathImagen, String key, boolean autorizacion) {
        this.nombre = nombre;
        this.email = email;
        this.pathImagen = pathImagen;
        this.key = key;
        this.autorizacion = autorizacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public boolean isAutorizacion() {
        return autorizacion;
    }

    public void setAutorizacion(boolean autorizacion) {
        this.autorizacion = autorizacion;
    }
}
