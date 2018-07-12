package com.example.jona.latacungadigital.Activities.modelos;

import java.util.ArrayList;

public class CategoriaModel {
    private String nombre, pathImagen, key;
    ArrayList<CategoriaModel> listaCategoriasSeleccionadas = new ArrayList<>();

    public ArrayList<CategoriaModel> getListaCategoriasSeleccionadas() {
        return listaCategoriasSeleccionadas;
    }

    public void setListaCategoriasSeleccionadas(ArrayList<CategoriaModel> listaCategoriasSeleccionadas) {
        this.listaCategoriasSeleccionadas = listaCategoriasSeleccionadas;
    }

    public CategoriaModel() {
    }

    public CategoriaModel(String nombre, String pathImagen, String key) {
        this.nombre = nombre;
        this.pathImagen = pathImagen;
        this.key = key;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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
