package com.example.jona.latacungadigital.Activities.Clases;

import com.example.jona.latacungadigital.R;
import com.google.gson.JsonElement;

public class ServiceClass {
    // Variables de la clase obtenidas de la BDD
    private Boolean state;
    private String key;
    private String alias;
    private String category;
    private String contact;
    private String address;
    private String name;
    private String typeOfActivity;
    private String web;

    private double latitude;
    private double longitude;
    private JsonElement position;
    private int icon;

    // Constructor
    ServiceClass() {
    }

    // Metodos Get and Set
    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeOfActivity() {
        return typeOfActivity;
    }

    public void setTypeOfActivity(String typeOfActivity) {
        this.typeOfActivity = typeOfActivity;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public JsonElement getPosition() {
        return position;
    }

    public void setPosition(JsonElement position) {
        this.position = position;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon() {
        switch (this.category){
            case "Alojamiento":
                this.icon = R.drawable.ic_marker_hotel_red;
                break;
            default:
                this.icon = 0;
                break;
        }
    }

    // Método para leer el JSON de servicios consultados que se obtiene de Dialogflow.
    public void readJSONDialogflow(String key, JsonElement values) {
        if (!values.isJsonNull() && !key.isEmpty()) {
            setState(true);
            setKey(key);
            setAlias(values.getAsJsonObject().get("alias").toString().replace("\"", ""));
            setCategory(values.getAsJsonObject().get("categoria").toString().replace("\"", ""));
            setContact(values.getAsJsonObject().get("contacto").toString().replace("\"", ""));
            setAddress(values.getAsJsonObject().get("direccion").toString().replace("\"", ""));
            setName(values.getAsJsonObject().get("nombre").toString().replace("\"", ""));
            setPosition(values.getAsJsonObject().get("posicion"));
            setLatitude(Double.parseDouble(getPosition().getAsJsonObject().get("lat").toString()));
            setLongitude(Double.parseDouble(getPosition().getAsJsonObject().get("lng").toString()));
            setTypeOfActivity(values.getAsJsonObject().get("tipoDeActividad").toString().replace("\"", ""));
            setWeb(values.getAsJsonObject().get("web").toString().replace("\"", ""));
            setIcon();
        } else {
            setState(false); // Para saber si el JSON esta vacio.
        }
    }

}
