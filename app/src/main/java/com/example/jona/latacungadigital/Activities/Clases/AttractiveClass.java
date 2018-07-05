package com.example.jona.latacungadigital.Activities.Clases;

import com.example.jona.latacungadigital.R;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ai.api.model.Result;

public class AttractiveClass {

    private boolean state;
    private String nameAttractive;
    private String alias;
    private String category;
    private String description;
    private List<String> imagenURL;
    private String address;
    private String rating;
    private double latitude;
    private double longitude;

    private JsonElement gallery;
    private JsonElement position;
    private int icon = 0;

    AttractiveClass() {
        imagenURL = new ArrayList<>();
        setIcon();
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getNameAttractive() {
        return nameAttractive;
    }

    public void setNameAttractive(String nameAttractive) {
        this.nameAttractive = nameAttractive;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public JsonElement getGallery() {
        return gallery;
    }

    public void setGallery(JsonElement gallery) {
        this.gallery = gallery;
    }

    public JsonElement getPosition() {
        return position;
    }

    public void setPosition(JsonElement position) {
        this.position = position;
    }

    public List<String> getImagenURL() {
        return imagenURL;
    }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon() {
        this.icon = R.drawable.ic_marker_building_blue;
    }

    // Método para leer el JSON de atractivos consultados que se obtiene de Dialogflow.
    public void readJSONDialogflow(Result resultAI) {
        final Map<String, JsonElement> JSONDialogflowResult = resultAI.getFulfillment().getData();

        // Leemos el JSON obtenido de Dialogflow si esque no esta nulo o vacío.
        if (JSONDialogflowResult != null && !JSONDialogflowResult.isEmpty()) {
            Set dataDialogFlowResult = JSONDialogflowResult.entrySet();
            for (Object objectValuesResult : dataDialogFlowResult) {
                Map.Entry attractiveEntry = (Map.Entry) objectValuesResult; // Obtenemos el servicio dentro del JSon del mapa
                Gson gson = new Gson();
                JsonParser jsonParser = new JsonParser();
                JsonElement values = jsonParser.parse(gson.toJson(attractiveEntry.getValue())); // Obtenemos los valores del servicio
                setState(true);
                setValuesFromJson(values);
            }
        } else {
            setState(false); // Para saber si el JSON esta vacio.
        }
    }

    // Método para leer el JSON de atractivos consultados que se obtiene de Dialogflow.
    public void readJSONDialogflow(String key, JsonElement values) {
        if (!values.isJsonNull() && !key.isEmpty()) {
            setState(true);
            setValuesFromJson(values);
        } else {
            setState(false); // Para saber si el JSON esta vacio.
        }
    }

    private void setValuesFromJson(JsonElement values) {
        setDescription(values.getAsJsonObject().get("descripcion").toString().replace("\"", ""));
        setNameAttractive(values.getAsJsonObject().get("nombre").toString().replace("\"", ""));
        setAlias(values.getAsJsonObject().get("alias").toString().replace("\"", "") );
        setCategory(values.getAsJsonObject().get("categoria").toString().replace("\"", ""));
        setAddress(values.getAsJsonObject().get("direccion").toString().replace("\"", ""));
        setRating(values.getAsJsonObject().get("rating").toString().replace("\"", ""));
        setGallery(values.getAsJsonObject().get("galeria"));
        setPosition(values.getAsJsonObject().get("posicion"));
        setLatitude(Double.parseDouble(getPosition().getAsJsonObject().get("lat").toString()));
        setLongitude(Double.parseDouble(getPosition().getAsJsonObject().get("lng").toString()));
        setIcon();
    }

    // Método para guardar todas las imagenes en un ArrayList.
    public List<String> getListImages() {
        try {
            JSONObject jsonObject = new JSONObject(getGallery().getAsJsonObject().toString());
            Iterator<?> keys = jsonObject.keys();
            while(keys.hasNext() ) {
                String key = (String)keys.next();
                if (jsonObject.get(key) instanceof JSONObject) {
                    JSONObject jsonResult = new JSONObject(jsonObject.get(key).toString());
                    imagenURL.add(jsonResult.getString("imagenURL"));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getImagenURL();
    }
}
