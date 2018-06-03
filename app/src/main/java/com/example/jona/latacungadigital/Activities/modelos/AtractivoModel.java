package com.example.jona.latacungadigital.Activities.modelos;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabia on 01/06/2018.
 */
@IgnoreExtraProperties
public  class AtractivoModel {
    private String nombre;

    private String categoria;

    private String descripcion;

    private String observacion;

    private Coordenada posicion;

    private List<ImagenModel> galeria;

    public String creadorUid;




    public AtractivoModel() {

    }

    public String getCreadorUid() {
        return creadorUid;
    }

    public void setCreadorUid(String creadorUid) {
        this.creadorUid = creadorUid;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getObservacion() {
        return observacion;
    }

    public Coordenada getPosicion() {
        return posicion;
    }

    public List<ImagenModel> getGaleria() {
        return galeria;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public void setPosicion(Coordenada posicion) {
        this.posicion = posicion;
    }

    public void setGaleria(List<ImagenModel> galeria) {
        this.galeria = galeria;
    }
}
