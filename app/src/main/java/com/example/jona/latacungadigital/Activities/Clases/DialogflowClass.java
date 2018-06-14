package com.example.jona.latacungadigital.Activities.Clases;

import android.content.Context;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jona.latacungadigital.Activities.Adapters.MessagesAdapter;
import com.example.jona.latacungadigital.Activities.Permisos.AccesoInternet;
import com.example.jona.latacungadigital.Activities.References.ChatBotReferences;
import com.example.jona.latacungadigital.Activities.modelos.TextMessageModel;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import ai.api.AIDataService;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;

public class DialogflowClass {

    // Declaración de variables para interactuar con Dialogflow.
    private AIDataService aiDataService;

    // Declaración de variables para hacer hablar al Chat Bot.
    private TextToSpeech toSpeechChatBot;

    // Declaración de variables para inicializar este modelo.
    private Context context;
    private List<TextMessageModel> listMessagesText;
    private RecyclerView rvListMessages;
    private MessagesAdapter messagesAdapter;
    private EditText txtMessageUserSend;
    private boolean isTextToSpeech = false;

    private AccesoInternet accesoInternet; // Variable para controlar que el usuario este conectado a Internet.

    public DialogflowClass(Context context, List<TextMessageModel> listMessagesText, RecyclerView rvListMessages, MessagesAdapter messagesAdapter, EditText txtMessageUserSend) {
        this.context = context;
        this.listMessagesText = listMessagesText;
        this.rvListMessages = rvListMessages;
        this.messagesAdapter = messagesAdapter;
        this.txtMessageUserSend = txtMessageUserSend;
        accesoInternet = new AccesoInternet();
    }

    // Getters and Setters
    public List<TextMessageModel> getListMessagesText() { return listMessagesText; }

    public void setListMessagesText(List<TextMessageModel> listMessagesText) { this.listMessagesText = listMessagesText; }

    public boolean isTextToSpeech() { return isTextToSpeech; }

    public void setTextToSpeech(boolean textToSpeech) { isTextToSpeech = textToSpeech; }

    // Metodo de configuración para conectarse con Dialogflow.
    public void ConfigurationDialogflow() {
        final AIConfiguration configurationAI = new AIConfiguration(ChatBotReferences.ACCESS_CLIENT_TOKEN,
                AIConfiguration.SupportedLanguages.Spanish,
                AIConfiguration.RecognitionEngine.System);

        // Agregar la configuración para conectarse con Dialogflow
        aiDataService = new AIDataService(configurationAI);
    }

    // Método para enviar el mensaje a Dialogflow.
    private void SendMessageTextToDialogflow(final String message) {
        final AIRequest aiRequest = new AIRequest();
        aiRequest.setQuery(message); // Enviamos la pregunta del usuario a Dialogflow.

        new AsyncTask<AIRequest, Void, AIResponse>() {

            // Método que se ejecuta antes de que comience el proceso doInBackground().
            @Override
            protected void onPreExecute() {
                // Se envia el mensaje solo si hay internet.
                if (accesoInternet.isNetDisponible(context)) {
                    MessageTypingToDialogflow();
                }
            }

            /* Método que se ejecutara despues de onPreExecute(). Este método recibe los parámetros de entrada para ejecutar las instrucciones
               especificas que irán en segundo plano. */
            @Override
            protected AIResponse doInBackground(AIRequest... aiRequests) {
                //final AIRequest request = aiRequests[0];
                if (accesoInternet.isNetDisponible(context)) { // Se envia el mensaje cuando haya solo internet en la aplicacion.
                    try {
                        final AIResponse response = aiDataService.request(aiRequest);
                        return response;
                    } catch (AIServiceException e) {
                        e.printStackTrace();
                        RemoveMessageTypingToDialogflow(); // Eliminamos la vista de chat bot is typing si existe un error.
                        this.cancel(true); // Cancelamos el envio de mensaje si existe un error.
                    }
                } else {
                    this.cancel(true); // Cancelamos el envio de mensaje sin internet.
                }

                return null;
            }

            // Método para cancelar la pregunta que se hace a Dialogflow en caso de que exista un error o no haya conexion a internet.
            @Override
            protected void onCancelled() {
                super.onCancelled();
            }

            // Método que se ejecutara cuando finalize el metodo doInBackground() y pasamos como parametro el resultado que retorna el mismo.
            @Override
            protected void onPostExecute(AIResponse response) {
                if (response != null) {
                    RemoveMessageTypingToDialogflow(); // Cuando el proceso termine se elimina la vista del chatbot is typing.
                    ResponseToDialogflow(response);
                }
            }
        }.execute(aiRequest);
    }

    // Método para posicionar el último elemento del Recycle View.
    private void setScrollbarChat() {
        rvListMessages.scrollToPosition(messagesAdapter.getItemCount() - 1);
    }

    // Método para crear un nuevo mensaje del usuario y del ChatBot.
    public void CreateMessage(String message) {
        if (!message.equals("")) { // Se valida el mensaje que sea diferente de nulo para que no envie un texto vació a Dialogflow ni al chat.
            TextMessageModel textMessageModel = new TextMessageModel(message); // Inicializamos la clase con el mensaje y quien envia el mensaje.
            textMessageModel.setViewTypeMessage(ChatBotReferences.VIEW_TYPE_MESSAGE_USER);
            listMessagesText.add(textMessageModel);
        }

        SendMessageTextToDialogflow(message); // Para enviar un mensaje a Dialogflow y para recibir el mensaje.
        txtMessageUserSend.setText(""); // Para limpiar el texto al momento de que envia un mensaje el usuario.
        messagesAdapter.notifyDataSetChanged();
        setScrollbarChat();
    }

    // Método para enviar la respuesta
    private void ResponseToDialogflow(AIResponse response) {
        if (response != null) {
            Result result = response.getResult();

            String action = result.getAction(); // Variable para reconocer la acción según la pregunta del usuario.

            if(action.equals("weatherAction")) { // Accion cuando es del clima

                final WeatherClass weatherModel = new WeatherClass(context);
                weatherModel.CurrentWeather(new WeatherClass.WeatherCallback() {
                    @Override
                    public void getResponseWeather(String response) {
                        MessageSendToDialogflow(response);
                    }
                });
            } else if (action.equals("churchInformationAction")) { // Accion cuando es una consulta de un atractivo turistico
                AttractiveClass attractiveClass = new AttractiveClass();
                CardDialogflow(attractiveClass, result);

            } else if (action.equals("churchShowLocationAction")) { // Accion para mostrar como llegar al lugar turistico.
                sendAttractiveToMapMessage(result);
            } else if (action.equals("consultarAtractivoEnElArea")) { // Accion cuando es una consulta sobre servicios de alojamiento cercanos
                String speech = result.getFulfillment().getSpeech();
                MessageSendToDialogflow(speech);
                sendAttractiveListToMapMessage(result, "Atractivos");
            } else if (action.equals("consultarAlojamientoEnElArea")) { // Accion cuando es una consulta sobre servicios de alojamiento cercanos
                String speech = result.getFulfillment().getSpeech();
                MessageSendToDialogflow(speech);
                sendServicesListToMapMessage(result, "Alojamiento");
            } else if (action.equals("consultarComidaYBebidaEnElArea")) { // Accion cuando es una consulta sobre servicios de alojamiento cercanos
                String speech = result.getFulfillment().getSpeech();
                MessageSendToDialogflow(speech);
                sendServicesListToMapMessage(result, "Comidas y bebidas");
            } else {

                String speech = result.getFulfillment().getSpeech();
                MessageSendToDialogflow(speech);
            }
        }
    }

    private void RemoveMessageTypingToDialogflow() {
        listMessagesText.remove(listMessagesText.size() - 1);
        addMessagesAdapter(listMessagesText);
    }

    // Método para enviar la respuesta al usuario.
    private void MessageSendToDialogflow(String message) {
        TextMessageModel textMessageModel = new TextMessageModel(message);
        textMessageModel.setViewTypeMessage(ChatBotReferences.VIEW_TYPE_MESSAGE_CHATBOT);
        listMessagesText.add(textMessageModel);

        if (isTextToSpeech()) { // Si el usuario inidica que el Chat Bot deba hablar.
            TextToSpeechChatBot(message);
        }

        addMessagesAdapter(listMessagesText);
    }

    // Método para que el usuario sepa que el chatbot esta escribiendo el mensaje.
    private void MessageTypingToDialogflow() {
        TextMessageModel textMessageModel = new TextMessageModel();
        textMessageModel.setViewTypeMessage(ChatBotReferences.VIEW_TYPE_MESSAGE_CHATBOT_TYPING);
        listMessagesText.add(textMessageModel);

        addMessagesAdapter(listMessagesText);
    }

    // Método para enviar la respuesta de la informacion de los atractivos turisticos al usuario.
    private void CardDialogflow(AttractiveClass attractiveModel, Result result) {
        TextMessageModel textMessageModel = new TextMessageModel();
        attractiveModel.readJSONDialogflow(result);

        if (attractiveModel.getState()) { // Para saber si el JSON no esta vacio.
            // Asignamos los valores leidos del JSON que envia Dialogflow y los asignamos a las varibales del Modelo TextMessageModel.
            textMessageModel.setViewTypeMessage(ChatBotReferences.VIEW_TYPE_MESSAGE_ATTRACTIVE_CHATBOT);
            textMessageModel.setNameAttractive(attractiveModel.getNameAttractive());
            textMessageModel.setCategoryAttactive(attractiveModel.getCategory());
            textMessageModel.setDescriptionAttractive(attractiveModel.getDescription());
            textMessageModel.setListImagesURL(attractiveModel.getListImages());
            textMessageModel.setAction(result.getAction());
            listMessagesText.add(textMessageModel);
            addMessagesAdapter(listMessagesText);

            MessageSendToDialogflow(result.getFulfillment().getSpeech()); // Para preguntar si quiere ver la ubicacion del atractivo.
        } else { // Si el JSON esta vacio enviamos la respuesta por defecto de Dialogflow.
            String speech = result.getFulfillment().getSpeech();
            MessageSendToDialogflow(speech);
        }
    }

    // Método para enviar la respuesta del fullfiltment de Dialogflow en mensaje del tipo Mapa
    private void sendAttractiveToMapMessage(Result result) {
        AttractiveClass attractive = new AttractiveClass();
        TextMessageModel textMessageModel = new TextMessageModel();
        attractive.readJSONDialogflow(result); // Asignamos los valores del Json al objeto atractivo
        if (attractive.getState()) { // Para saber si el JSON no esta vacio.
            // Asignamos los valores leidos del JSON que envia Dialogflow y los asignamos a las varibales del Modelo TextMessageModel.
            textMessageModel.setViewTypeMessage(ChatBotReferences.VIEW_TYPE_MESSAGE_CARD_VIEW_MAP);
            textMessageModel.setAttractive(attractive);
            textMessageModel.setTitulo(attractive.getNameAttractive());
            textMessageModel.setAction(result.getAction());
            listMessagesText.add(textMessageModel);
            addMessagesAdapter(listMessagesText);
        }
    }

    // Método para enviar la respuesta del fullfiltment de Dialogflow en mensaje del tipo Mapa
    private void sendAttractiveListToMapMessage(Result result, String titulo){
        Map<String, JsonElement> JSONDialogflowResult = result.getFulfillment().getData(); // Obtenemos el nodo Data del Json
        ArrayList<AttractiveClass> listAttractive =  new ArrayList<AttractiveClass>(); // Lista de atractivos
        TextMessageModel textMessageModel = new TextMessageModel();
        // Recorremos el resultado obtenido de dialogflow
        Set mapDialogFlowResult = JSONDialogflowResult.entrySet();
        Iterator iterator = mapDialogFlowResult.iterator();
        while(iterator.hasNext()){
            AttractiveClass tempAttractive = new AttractiveClass();
            Map.Entry mapService = (Map.Entry) iterator.next(); // Obtenemos el atractivo dentro del JSon del mapa
            Gson gson = new Gson();
            JsonParser jsonParser = new JsonParser();
            String key = mapService.getKey().toString(); // Obtenemos la clave del servicio
            JsonElement values = jsonParser.parse(gson.toJson(mapService.getValue())); // Obtenemos los valores del atractivo
            tempAttractive.readJSONDialogflow(key,values); // Asignamos los valores del Json al objeto atractivo
            if(tempAttractive.getState()){
                listAttractive.add(tempAttractive); // Añadimo el objeto atractivo a la lista
            }else{
                Log.e("ERROR DE LECTURA JSON","Error al transformar Json a AttractiveClass");
            }
        }
        if(!listAttractive.isEmpty()){
            // Asignamos los valores leidos del JSON que envia Dialogflow y los asignamos a las varibales del Modelo TextMessageModel.
            textMessageModel.setViewTypeMessage(ChatBotReferences.VIEW_TYPE_MESSAGE_CARD_VIEW_MAP);
            textMessageModel.setListAttractive(listAttractive);
            textMessageModel.setTitulo(titulo);
            textMessageModel.setAction(result.getAction());
            listMessagesText.add(textMessageModel);
            addMessagesAdapter(listMessagesText);
        }
    }

    // Método para enviar la respuesta del fullfiltment de Dialogflow en mensaje del tipo Mapa
    private void sendServicesListToMapMessage(Result result, String titulo){
        Map<String, JsonElement> JSONDialogflowResult = result.getFulfillment().getData(); // Obtenemos el nodo Data del Json
        ArrayList<ServiceClass> listService =  new ArrayList<ServiceClass>(); // Lista de servicios
        TextMessageModel textMessageModel = new TextMessageModel();
        // Recorremos el resultado obtenido de dialogflow
        Set mapDialogFlowResult = JSONDialogflowResult.entrySet();
        Iterator iterator = mapDialogFlowResult.iterator();
        while(iterator.hasNext()){
            ServiceClass tempService = new ServiceClass();
            Map.Entry mapService = (Map.Entry) iterator.next(); // Obtenemos el servicio dentro del JSon del mapa
            Gson gson = new Gson();
            JsonParser jsonParser = new JsonParser();
            String key = mapService.getKey().toString(); // Obtenemos la clave del servicio
            JsonElement values = jsonParser.parse(gson.toJson(mapService.getValue())); // Obtenemos los valores del servicio
            tempService.readJSONDialogflow(key,values); // Asignamos los valores del Json al objeto servicio
            if(tempService.getState()){
                listService.add(tempService); // Añadimo el objeto servicio a la lista
            }else{
                Log.e("ERROR DE LECTURA JSON","Error al transformar Json a ServiceClass");
            }
        }
        if(!listService.isEmpty()){
            // Asignamos los valores leidos del JSON que envia Dialogflow y los asignamos a las varibales del Modelo TextMessageModel.
            textMessageModel.setViewTypeMessage(ChatBotReferences.VIEW_TYPE_MESSAGE_CARD_VIEW_MAP);
            textMessageModel.setListService(listService);
            textMessageModel.setTitulo(titulo);
            textMessageModel.setAction(result.getAction());
            listMessagesText.add(textMessageModel);
            addMessagesAdapter(listMessagesText);
        }
    }

    // Método para adaptar la lista de mensajes a la clase MessagesAdapter.
    public void addMessagesAdapter(List<TextMessageModel> listMessages) {
        MessagesAdapter messagesAdapter = new MessagesAdapter(listMessages, context);
        messagesAdapter.setChatTextFragment(this.messagesAdapter.getChatTextFragment());
        messagesAdapter.setListMessageCardMapView(this.messagesAdapter.getListMessageCardMapView());
        messagesAdapter.setListMessageAttractiveHowToGet(this.messagesAdapter.getListMessageAttractiveHowToGet());

        rvListMessages.setAdapter(messagesAdapter);
        messagesAdapter.notifyDataSetChanged();
        setScrollbarChat();
    }

    // Método para hacer hbalar al Chat Bot.
    private void TextToSpeechChatBot(final String speech) {
        toSpeechChatBot = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {

                    int result = toSpeechChatBot.setLanguage(Locale.getDefault());

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(context, "Este idioma no es compatible.", Toast.LENGTH_SHORT).show();
                    } else {
                        toSpeechChatBot.speak(speech, TextToSpeech.QUEUE_FLUSH,null);
                    }
                } else {
                    Toast.makeText(context, "Esta característica no está admitida en su dispositivo", Toast.LENGTH_SHORT).show();
                }
            }
        }, "com.google.android.tts");
    }

    public void onDestroyToSpeech() {
        if (toSpeechChatBot != null) {
            toSpeechChatBot.stop();
            toSpeechChatBot.shutdown();
        }
    }
}
