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

    public String pathImagen;

    public String key;

    public boolean funcionAR;

    public boolean funcionVR;

    public float rating;

    public String subtipo;

    public AtractivoModel() {

    }

    //Para guardar en firebase nuevos sitios
    public AtractivoModel(String nombre, String descripcion, Coordenada posicion, String key) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.posicion = posicion;
        this.key = key;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getSubtipo() {
        return subtipo;
    }

    public void setSubtipo(String subtipo) {
        this.subtipo = subtipo;
    }

    public boolean isFuncionVR() {
        return funcionVR;
    }

    public void setFuncionVR(boolean funcionVR) {
        this.funcionVR = funcionVR;
    }

    public boolean isFuncionAR() {
        return funcionAR;
    }

    public void setFuncionAR(boolean funcionAR) {
        this.funcionAR = funcionAR;
    }

    public String getPathImagen() {
        return pathImagen;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setPathImagen(String pathImagen) {
        this.pathImagen = pathImagen;
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
