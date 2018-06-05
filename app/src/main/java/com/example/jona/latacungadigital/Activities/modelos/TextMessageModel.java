package com.example.jona.latacungadigital.Activities.modelos;

import com.example.jona.latacungadigital.Activities.Clases.ServiceClass;

import java.util.ArrayList;
import java.util.List;

public class TextMessageModel {

    // Declaración de variables de los mensajes de texto.
    private String message;
    private int viewTypeMessage ; // Para saber si el usuario o el chatbot envio el mensaje.
    private List<String> listImagesURL; // Para almacenar todas las imagenes en un array.

    // Declaracion de variables de la información de los atractivos turisticos.
    private String nameAttractive;
    private String categoryAttactive;
    private String descriptionAttractive;

    // Declaracion de variables para la consulta de servicios.
    private ArrayList<ServiceClass> listService;

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

    public ArrayList<ServiceClass> getListService() {
        return listService;
    }

    public void setListService(ArrayList<ServiceClass> listService) {
        this.listService = listService;
    }
}
