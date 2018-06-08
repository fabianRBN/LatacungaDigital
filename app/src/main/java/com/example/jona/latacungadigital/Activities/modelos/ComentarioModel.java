package com.example.jona.latacungadigital.Activities.modelos;

/**
 * Created by fabia on 08/06/2018.
 */

public class ComentarioModel {
    private String uidUsuario;
    private String contenido;
    private double calificacion;
    private String fecha;

    public ComentarioModel() {
    }

    public ComentarioModel(String uidUsuario, String contenido, double calificacion, String fecha) {
        this.uidUsuario = uidUsuario;
        this.contenido = contenido;
        this.calificacion = calificacion;
        this.fecha = fecha;
    }

    public String getUidUsuario() {
        return uidUsuario;
    }

    public void setUidUsuario(String uidUsuario) {
        this.uidUsuario = uidUsuario;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public double getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(double calificacion) {
        this.calificacion = calificacion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
