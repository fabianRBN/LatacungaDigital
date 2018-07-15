package com.example.jona.latacungadigital.Activities.Clases;

import com.example.jona.latacungadigital.R;
import com.google.gson.JsonElement;

import java.util.Calendar;

public class ServiceClass {
    // Variables de la clase obtenidas de la BDD
    private Boolean state;

    private String key;
    private String alias;
    private String category;
    private String contact;
    private String email;
    private String address;
    private String facebookPage;
    private HorarioClass horario;
    private JsonElement position;
    private String name;
    private String SubTypeOfActivity;
    private String typeOfActivity;
    private String web;

    private double latitude;
    private double longitude;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFacebookPage() {
        return facebookPage;
    }

    public void setFacebookPage(String facebookPage) {
        this.facebookPage = facebookPage;
    }

    public HorarioClass getHorario() {
        return horario;
    }

    public void setHorario(HorarioClass horario) {
        this.horario = horario;
    }

    public JsonElement getPosition() {
        return position;
    }

    public void setPosition(JsonElement position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubTypeOfActivity() {
        return SubTypeOfActivity;
    }

    public void setSubTypeOfActivity(String subTypeOfActivity) {
        SubTypeOfActivity = subTypeOfActivity;
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
        switch (this.getTypeOfActivity()){
            case "Agencia de viajes":
                if(getHorario().isHorarioDefinido() && isOpen()){
                    this.icon = R.drawable.ic_marker_travel_is_open_purple;
                } else {
                    this.icon = R.drawable.ic_marker_travel_purple;
                }
                break;
            case "Alojamiento":
                if(getHorario().isHorarioDefinido() && isOpen()){
                    this.icon = R.drawable.ic_marker_hotel_is_open_red;
                } else {
                    this.icon = R.drawable.ic_marker_hotel_red;
                }
                break;
            case "Comidas y bebidas":
                if(getHorario().isHorarioDefinido() && isOpen()){
                    this.icon = R.drawable.ic_marker_restaurant_is_open_orange;
                } else {
                    this.icon = R.drawable.ic_marker_restaurant_orange;
                }
                break;
            case "Recreación, diversión, esparcimiento":
                if(getHorario().isHorarioDefinido() && isOpen()){
                    this.icon = R.drawable.ic_marker_star_is_open_yellow;
                } else {
                    this.icon = R.drawable.ic_marker_star_yellow;
                }
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
            setEmail(values.getAsJsonObject().get("correo").toString().replace("\"", ""));
            setAddress(values.getAsJsonObject().get("direccion").toString().replace("\"", ""));
            setFacebookPage(values.getAsJsonObject().get("facebookPage").toString().replace("\"", ""));
            setHorario(new HorarioClass());
            getHorario().setValuesFromJson(values.getAsJsonObject().get("horario"));
            setName(values.getAsJsonObject().get("nombre").toString().replace("\"", ""));
            setPosition(values.getAsJsonObject().get("posicion"));
            setLatitude(Double.parseDouble(getPosition().getAsJsonObject().get("lat").toString()));
            setLongitude(Double.parseDouble(getPosition().getAsJsonObject().get("lng").toString()));
            setSubTypeOfActivity(values.getAsJsonObject().get("subTipoDeActividad").toString().replace("\"", ""));
            setTypeOfActivity(values.getAsJsonObject().get("tipoDeActividad").toString().replace("\"", ""));
            setWeb(values.getAsJsonObject().get("web").toString().replace("\"", ""));
            setIcon();
        } else {
            setState(false); // Para saber si el JSON esta vacio.
        }
    }

    public boolean isOpen(){
        boolean isOpen = false;
        if(!getHorario().isSiempreAbierto()){
            Calendar calendar = Calendar.getInstance();
            int cDay = calendar.get(Calendar.DAY_OF_WEEK);
            int cHour = calendar.get(Calendar.HOUR_OF_DAY);
            int cMinute = calendar.get(Calendar.MINUTE);
            int cSuma =  (cHour * 100) + cMinute;
            switch (cDay){
                case Calendar.MONDAY:
                    if(getHorario().getLunes().isAbierto()){
                        int horaInicio = getHorario().getLunes().getSumaHoraInicio();
                        int horaFinal = getHorario().getLunes().getSumaHoraFinal();
                        if(horaInicio < horaFinal){ // Ejemplo De 6:00 AM a 9:00 PM
                            if(cSuma > horaInicio && cSuma < horaFinal){
                                isOpen = true;
                            }
                        } else { // Ejemplo De 9:00 PM a 2:00 AM
                            if(cSuma > horaInicio || cSuma < horaFinal){
                                isOpen = true;
                            }
                        }
                    } else {
                        isOpen = false;
                    }
                    break;
                case Calendar.TUESDAY:
                    if(getHorario().getMartes().isAbierto()){
                        int horaInicio = getHorario().getMartes().getSumaHoraInicio();
                        int horaFinal = getHorario().getMartes().getSumaHoraFinal();
                        if(horaInicio < horaFinal){ // Ejemplo De 6:00 AM a 9:00 PM
                            if(cSuma > horaInicio && cSuma < horaFinal){
                                isOpen = true;
                            }
                        } else { // Ejemplo De 9:00 PM a 2:00 AM
                            if(cSuma > horaInicio || cSuma < horaFinal){
                                isOpen = true;
                            }
                        }
                    } else {
                        isOpen = false;
                    }
                    break;
                case Calendar.WEDNESDAY:
                    if(getHorario().getMiercoles().isAbierto()){
                        int horaInicio = getHorario().getMiercoles().getSumaHoraInicio();
                        int horaFinal = getHorario().getMiercoles().getSumaHoraFinal();
                        if(horaInicio < horaFinal){ // Ejemplo De 6:00 AM a 9:00 PM
                            if(cSuma > horaInicio && cSuma < horaFinal){
                                isOpen = true;
                            }
                        } else { // Ejemplo De 9:00 PM a 2:00 AM
                            if(cSuma > horaInicio || cSuma < horaFinal){
                                isOpen = true;
                            }
                        }
                    } else {
                        isOpen = false;
                    }
                    break;
                case Calendar.THURSDAY:
                    if(getHorario().getJueves().isAbierto()){
                        int horaInicio = getHorario().getJueves().getSumaHoraInicio();
                        int horaFinal = getHorario().getJueves().getSumaHoraFinal();
                        if(horaInicio < horaFinal){ // Ejemplo De 6:00 AM a 9:00 PM
                            if(cSuma > horaInicio && cSuma < horaFinal){
                                isOpen = true;
                            }
                        } else { // Ejemplo De 9:00 PM a 2:00 AM
                            if(cSuma > horaInicio || cSuma < horaFinal){
                                isOpen = true;
                            }
                        }
                    } else {
                        isOpen = false;
                    }
                    break;
                case Calendar.FRIDAY:
                    if(getHorario().getViernes().isAbierto()){
                        int horaInicio = getHorario().getViernes().getSumaHoraInicio();
                        int horaFinal = getHorario().getViernes().getSumaHoraFinal();
                        if(horaInicio < horaFinal){ // Ejemplo De 6:00 AM a 9:00 PM
                            if(cSuma > horaInicio && cSuma < horaFinal){
                                isOpen = true;
                            }
                        } else { // Ejemplo De 9:00 PM a 2:00 AM
                            if(cSuma > horaInicio || cSuma < horaFinal){
                                isOpen = true;
                            }
                        }
                    } else {
                        isOpen = false;
                    }
                    break;
                case Calendar.SATURDAY:
                    if(getHorario().getSabado().isAbierto()){
                        int horaInicio = getHorario().getSabado().getSumaHoraInicio();
                        int horaFinal = getHorario().getSabado().getSumaHoraFinal();
                        if(horaInicio < horaFinal){ // Ejemplo De 6:00 AM a 9:00 PM
                            if(cSuma > horaInicio && cSuma < horaFinal){
                                isOpen = true;
                            }
                        } else { // Ejemplo De 9:00 PM a 2:00 AM
                            if(cSuma > horaInicio || cSuma < horaFinal){
                                isOpen = true;
                            }
                        }
                    } else {
                        isOpen = false;
                    }
                    break;
                case Calendar.SUNDAY:
                    if(getHorario().getDomingo().isAbierto()){
                        int horaInicio = getHorario().getDomingo().getSumaHoraInicio();
                        int horaFinal = getHorario().getDomingo().getSumaHoraFinal();
                        if(horaInicio < horaFinal){ // Ejemplo De 6:00 AM a 9:00 PM
                            if(cSuma > horaInicio && cSuma < horaFinal){
                                isOpen = true;
                            }
                        } else { // Ejemplo De 9:00 PM a 2:00 AM
                            if(cSuma > horaInicio || cSuma < horaFinal){
                                isOpen = true;
                            }
                        }
                    } else {
                        isOpen = false;
                    }
                    break;
            }
        } else {
            isOpen = true;
        }
        return isOpen;
    }

}
