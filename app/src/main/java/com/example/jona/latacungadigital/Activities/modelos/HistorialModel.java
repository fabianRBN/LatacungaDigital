package com.example.jona.latacungadigital.Activities.modelos;

/**
 * Created by fabia on 30/06/2018.
 */

public class HistorialModel {
    public int hora;
    public int dia;
    public int mes;
    public int Anio;

    public HistorialModel() {
    }

    public HistorialModel(int hora, int dia, int mes, int anio) {
        this.hora = hora;
        this.dia = dia;
        this.mes = mes;
        Anio = anio;
    }

    public int getHora() {
        return hora;
    }

    public void setHora(int hora) {
        this.hora = hora;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAnio() {
        return Anio;
    }

    public void setAnio(int anio) {
        Anio = anio;
    }
}
