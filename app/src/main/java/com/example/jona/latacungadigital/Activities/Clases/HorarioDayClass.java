package com.example.jona.latacungadigital.Activities.Clases;

import com.google.gson.JsonElement;

public class HorarioDayClass {
    private boolean abierto;
    private String horaInicio;
    private String horaFinal;

    public HorarioDayClass() {
    }


    public boolean isAbierto() {
        return abierto;
    }

    public void setAbierto(boolean abierto) {
        this.abierto = abierto;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFinal() {
        return horaFinal;
    }

    public void setHoraFinal(String horaFinal) {
        this.horaFinal = horaFinal;
    }

    public void setValuesFromJson(JsonElement jsonElement){
        setAbierto(jsonElement.getAsJsonObject().get("abierto").getAsBoolean());
        setHoraInicio(jsonElement.getAsJsonObject().get("horaInicio").toString().replace("\"", ""));
        setHoraFinal(jsonElement.getAsJsonObject().get("horaSalida").toString().replace("\"", ""));
    }

    // Retorna de 17:30 = int 1730
    public int getSumaHoraInicio(){
        int hora = Integer.parseInt(getHoraInicio().split(":")[0]) * 100;
        int minute = Integer.parseInt(getHoraInicio().split(":")[0]);
        return hora + minute;
    }

    // Retorna de 17:30 = int 1730
    public int getSumaHoraFinal() {
        int hora = Integer.parseInt(getHoraFinal().split(":")[0]) * 100;
        int minute = Integer.parseInt(getHoraFinal().split(":")[0]);
        return hora + minute;
    }

    // Retorna 08:00 - 20:00
    public String getHoraInicioAndHoraFinal(){
        return getHoraInicio()  + " - " + getHoraFinal();
    }
}
