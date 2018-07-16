package com.example.jona.latacungadigital.Activities.modelos;

import com.example.jona.latacungadigital.Activities.Clases.AttractiveClass;
import com.example.jona.latacungadigital.Activities.Clases.ServiceClass;

import java.util.ArrayList;
import java.util.List;

public class TextMessageModel {

    // Declaración de variables de los mensajes de texto.
    private String message;
    private int viewTypeMessage ; // Para saber si el usuario o el chatbot envio el mensaje.
    private List<String> listImagesURL; // Para almacenar todas las imagenes en un array.
    private String titulo;

    // Declaracion de variables de la información de los atractivos turisticos.
    private String nameAttractive;
    private String categoryAttactive;
    private String descriptionAttractive;
    private String addressAttractive;
    private String subTypeAttractive;
    private double latitude;
    private double longitude;
    private float ratingAttractive;

    // Declaracion de variables para la consulta.
    private String action;
    private ArrayList<ServiceClass> listService;
    private ArrayList<AttractiveClass> listAttractive;
    private ServiceClass service;
    private AttractiveClass attractive;

    // Constructores.
    public TextMessageModel(String message) {
        this.message = message;
    }

    public TextMessageModel() {}

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getViewTypeMessage() { return viewTypeMessage; }

    public void setViewTypeMessage(int viewTypeMessage) { this.viewTypeMessage = viewTypeMessage; }

    public List<String> getListImagesURL() { return listImagesURL; }

    public void setListImagesURL(List<String> listImagesURL) { this.listImagesURL = listImagesURL; }

    public String getNameAttractive() { return nameAttractive; }

    public void setNameAttractive(String nameAttractive) { this.nameAttractive = nameAttractive; }

    public String getCategoryAttactive() { return categoryAttactive; }

    public void setCategoryAttactive(String categoryAttactive) { this.categoryAttactive = categoryAttactive; }

    public String getDescriptionAttractive() { return descriptionAttractive; }

    public void setDescriptionAttractive(String descriptionAttractive) { this.descriptionAttractive = descriptionAttractive; }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public ArrayList<ServiceClass> getListService() {
        return listService;
    }

    public void setListService(ArrayList<ServiceClass> listService) {
        this.listService = listService;
    }

    public ArrayList<AttractiveClass> getListAttractive() {
        return listAttractive;
    }

    public void setListAttractive(ArrayList<AttractiveClass> listAttractive) {
        this.listAttractive = listAttractive;
    }

    public ServiceClass getService() {
        return service;
    }

    public void setService(ServiceClass service) {
        this.service = service;
    }

    public AttractiveClass getAttractive() {
        return attractive;
    }

    public void setAttractive(AttractiveClass attractive) {
        this.attractive = attractive;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public double getLatitude() { return latitude; }

    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }

    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getAddressAttractive() { return addressAttractive; }

    public void setAddressAttractive(String addressAttractive) { this.addressAttractive = addressAttractive; }

    public float getRatingAttractive() { return ratingAttractive; }

    public void setRatingAttractive(float ratingAttractive) { this.ratingAttractive = ratingAttractive; }

    public String getSubTypeAttractive() { return subTypeAttractive; }

    public void setSubTypeAttractive(String subTypeAttractive) { this.subTypeAttractive = subTypeAttractive; }
}
