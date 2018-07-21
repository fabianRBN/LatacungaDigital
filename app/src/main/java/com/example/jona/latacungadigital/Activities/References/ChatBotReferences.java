package com.example.jona.latacungadigital.Activities.References;

public class ChatBotReferences {

    // Para saber que vista se debe adaptar al RecycleView.
    public static final int VIEW_TYPE_MESSAGE_USER = 1;
    public static final int VIEW_TYPE_MESSAGE_CHATBOT = 2;
    public static final int VIEW_TYPE_MESSAGE_CHATBOT_TYPING = 3;
    public static final int VIEW_TYPE_MESSAGE_ATTRACTIVE_CHATBOT = 4;
    public static final int VIEW_TYPE_MESSAGE_CARD_VIEW_MAP = 5;
    public static final int VIEW_TYPE_MESSAGE_CARD_VIEW_DETAIL_SERVICE = 6;

    // Codigo de confirmacion del usuario cuando habla.
    public static final int SPEECH_RECOGNITION_CODE = 1;

    // Codigo del cliente de Dialogflow.
    public static final String ACCESS_CLIENT_TOKEN = "70cabdb5e2594ff0b81f86520564fbd6";

    // API del Clima.
    public static final String APIXU_API_CLIENT = "0b65639864ea4bbdb93195540182604";

    // Para saber a que dialogo llamar en la actividad.
    public static final int DIALOG_DELETE_MESSAGE = 2;
    public static final int DIALOG_CHANGE_CHARACTER = 3;

    // Autentificacion a la API de IBM Watson para el asistente de voz.
    public static final String USERNAME_API_WATSON = "49e24466-6ffb-4c0d-9c8f-45d2963cd742";
    public static final String PASSWORD_API_WATSON = "4zAPrpNm41L0";
    public static final String END_POINT_API_WATSON = "https://stream.watsonplatform.net/text-to-speech/api";

    // Para saber que tipo intencion es segun el tipo de atractivo o servicio.
    public static final int ATTRACTIVE_INTENT = 1;
    public static final int SERVICE_INTENT = 2;
    public static final int ATTRACTIVE_INTENT_YES = 3;
    public static final int SERVICE_INTENT_YES = 4;

    // Para saber que sub tipo de atractivo es.
    public static final String ARQUITECTURA_CIVIL = "Arquitectura Civil";
    public static final String ARQUITECTURA_RELIGIOSA = "Arquitectura Religiosa";
    public static final String MUSEO = "Museo";
    public static final String MUSEO_HISTORICO= "Museos históricos";

    // Para saber que tipo de actividad es según el servicio.
    public static final String AGENCIA_DE_VIAJE = "Agencia de viajes";
    public static final String ALOJAMIENTO = "Alojamiento";
    public static final String COMIDAS_Y_BEBIDAS = "Comidas y bebidas";
    public static final String RECREACION = "Recreación, diversión, esparcimiento";
}
