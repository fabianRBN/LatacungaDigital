package com.example.jona.latacungadigital.Activities.Clases;

import com.google.gson.JsonElement;

public class HorarioClass {
    private boolean siempreAbierto;
    private HorarioDayClass lunes;
    private HorarioDayClass martes;
    private HorarioDayClass miercoles;
    private HorarioDayClass jueves;
    private HorarioDayClass viernes;
    private HorarioDayClass sabado;
    private HorarioDayClass domingo;

    public HorarioClass() {
    }

    public boolean isSiempreAbierto() {
        return siempreAbierto;
    }

    public void setSiempreAbierto(boolean siempreAbierto) {
        this.siempreAbierto = siempreAbierto;
    }

    public HorarioDayClass getLunes() {
        return lunes;
    }

    public void setLunes(HorarioDayClass lunes) {
        this.lunes = lunes;
    }

    public HorarioDayClass getMartes() {
        return martes;
    }

    public void setMartes(HorarioDayClass martes) {
        this.martes = martes;
    }

    public HorarioDayClass getMiercoles() {
        return miercoles;
    }

    public void setMiercoles(HorarioDayClass miercoles) {
        this.miercoles = miercoles;
    }

    public HorarioDayClass getJueves() {
        return jueves;
    }

    public void setJueves(HorarioDayClass jueves) {
        this.jueves = jueves;
    }

    public HorarioDayClass getViernes() {
        return viernes;
    }

    public void setViernes(HorarioDayClass viernes) {
        this.viernes = viernes;
    }

    public HorarioDayClass getSabado() {
        return sabado;
    }

    public void setSabado(HorarioDayClass sabado) {
        this.sabado = sabado;
    }

    public HorarioDayClass getDomingo() {
        return domingo;
    }

    public void setDomingo(HorarioDayClass domingo) {
        this.domingo = domingo;
    }

    public void setValuesFromJson(JsonElement jsonElement){
        setSiempreAbierto(jsonElement.getAsJsonObject().get("siempreAbierto").getAsBoolean());
        setLunes(new HorarioDayClass());
        getLunes().setValuesFromJson(jsonElement.getAsJsonObject().get("Lunes"));
        setMartes(new HorarioDayClass());
        getMartes().setValuesFromJson(jsonElement.getAsJsonObject().get("Martes"));
        setMiercoles(new HorarioDayClass());
        getMiercoles().setValuesFromJson(jsonElement.getAsJsonObject().get("Miercoles"));
        setJueves(new HorarioDayClass());
        getJueves().setValuesFromJson(jsonElement.getAsJsonObject().get("Jueves"));
        setViernes(new HorarioDayClass());
        getViernes().setValuesFromJson(jsonElement.getAsJsonObject().get("Viernes"));
        setSabado(new HorarioDayClass());
        getSabado().setValuesFromJson(jsonElement.getAsJsonObject().get("Sabado"));
        setDomingo(new HorarioDayClass());
        getDomingo().setValuesFromJson(jsonElement.getAsJsonObject().get("Domingo"));
    }

    public boolean isHorarioDefinido(){
        if(!isSiempreAbierto()){
            if(!getLunes().isAbierto() &&
                    !getMartes().isAbierto() &&
                    !getMiercoles().isAbierto() &&
                    !getJueves().isAbierto() &&
                    !getViernes().isAbierto() &&
                    !getSabado().isAbierto() &&
                    !getDomingo().isAbierto()){
                return false;
            } else {
                return true;
            }
        }else{
            return true;
        }
    }
}
