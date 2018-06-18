package com.example.jona.latacungadigital.Activities.Clases;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.EditText;

import com.example.jona.latacungadigital.Activities.Adapters.MessagesAdapter;
import com.example.jona.latacungadigital.Activities.Permisos.AccesoInternet;
import com.example.jona.latacungadigital.Activities.References.ChatBotReferences;
import com.example.jona.latacungadigital.Activities.modelos.TextMessageModel;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

    public RecyclerView getRvListMessages() {return rvListMessages; }

    public MessagesAdapter getMessagesAdapter() { return messagesAdapter; }

    // Metodo de configuración para conectarse con Dialogflow.
    public void ConfigurationDialogflow() {
        final AIConfiguration configurationAI = new AIConfiguration(ChatBotReferences.ACCESS_CLIENT_TOKEN,
                AIConfiguration.SupportedLanguages.Spanish,
                AIConfiguration.RecognitionEngine.System);

        // Agregar la configuración para conectarse con Dialogflow
        aiDataService = new AIDataService(configurationAI);
    }

    // Método para enviar el mensaje a Dialogflow.
    @SuppressLint("StaticFieldLeak")
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
            switch (action){
                case "weatherAction":
                    final WeatherClass weatherModel = new WeatherClass(context);
                    weatherModel.CurrentWeather(new WeatherClass.WeatherCallback() {
                        @Override
                        public void getResponseWeather(String response) {
                            MessageSendToDialogflow(response);
                        }
                    });
                    break;
                case "churchInformationAction":
                    sendAttractiveToCardViewInformation(result);
                    break;
                case "hotelInformationAction":
                    sendServiceToDatailServiceMessage(result, true);
                    MessageSendToDialogflow(result.getFulfillment().getSpeech().split("\\. ")[1]);
                    break;
                case "hotel_information_intent.hotel_information_intent-yes":
                    sendServiceToMapMessage(result);
                    break;
                case "church_information_intent.church_information_intent-yes":
                    sendAttractiveToMapMessage(result);
                    break;
                case "consultarAtractivoEnElArea":
                    sendAttractiveListToMapMessage(result, "");
                    break;
                case "consultarAgenciasDeViajeEnElArea":
                    sendServicesListToMapMessage(result, "Agencia de viajes");
                    break;
                case "consultarAlojamientoEnElArea":
                    sendServicesListToMapMessage(result, "Alojamiento");
                    break;
                case "consultarComidaYBebidaEnElArea":
                    sendServicesListToMapMessage(result, "Comidas y bebidas");
                    break;
                case "consultarRecreacionDiversionEsparcimientoEnElArea":
                    sendServicesListToMapMessage(result, "Recreación, diversión, esparcimiento");
                    break;
                default:
                    String speech = result.getFulfillment().getSpeech();
                    MessageSendToDialogflow(speech);
                    break;
            }
        }
    }

    private void RemoveMessageTypingToDialogflow() {
        listMessagesText.remove(listMessagesText.size() - 1);
        addMessagesAdapter(listMessagesText);
    }

    // Método para enviar la respuesta al usuario.
    private void MessageSendToDialogflow(final String message) {
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
    private void sendAttractiveToCardViewInformation(Result result) {
        TextMessageModel textMessageModel = new TextMessageModel();
        AttractiveClass attractiveClass = new AttractiveClass();
        attractiveClass.readJSONDialogflow(result);

        if (attractiveClass.getState()) { // Para saber si el JSON no esta vacio.
            // Para decir al usuario que se encontro la información dicha por el.
            MessageSendToDialogflow(result.getFulfillment().getSpeech().split("\\. ")[0]);

            // Asignamos los valores leidos del JSON que envia Dialogflow y los asignamos a las varibales del Modelo TextMessageModel.
            textMessageModel.setViewTypeMessage(ChatBotReferences.VIEW_TYPE_MESSAGE_ATTRACTIVE_CHATBOT);
            textMessageModel.setNameAttractive(attractiveClass.getNameAttractive());
            textMessageModel.setCategoryAttactive(attractiveClass.getCategory());
            textMessageModel.setDescriptionAttractive(attractiveClass.getDescription());
            textMessageModel.setListImagesURL(attractiveClass.getListImages());
            textMessageModel.setAction(result.getAction());
            listMessagesText.add(textMessageModel);
            addMessagesAdapter(listMessagesText);

            // Para preguntar sobre si desea visitar la ruta.
            MessageSendToDialogflow(result.getFulfillment().getSpeech().split("\\. ")[1]);
        } else { // Si el JSON esta vacio enviamos la respuesta por defecto de Dialogflow.
            String speech = result.getFulfillment().getSpeech();
            MessageSendToDialogflow(speech);
        }
    }

    // Método para enviar la respuesta de la informacion del servicio al usuario.
    private void sendServiceToDatailServiceMessage(Result result, boolean splitSpeech) {
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
        // Enviamos la respuesta obtenida de Dialogflow
        String speech;
        if(splitSpeech){
            speech = result.getFulfillment().getSpeech().split("\\. ")[0];
        }else {
            speech = result.getFulfillment().getSpeech();
        }
        MessageSendToDialogflow(speech);
        if(!listService.isEmpty()){ // Si la lista no esta vacia se envia el tipo de mensaje designado
            // Asignamos los valores leidos del JSON que envia Dialogflow y los asignamos a las varibales del Modelo TextMessageModel.
            textMessageModel.setViewTypeMessage(ChatBotReferences.VIEW_TYPE_MESSAGE_CARD_VIEW_DETAIL_SERVICE);
            textMessageModel.setService(listService.get(0));
            textMessageModel.setAction(result.getAction());
            listMessagesText.add(textMessageModel);
            addMessagesAdapter(listMessagesText);
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
    private void sendServiceToMapMessage(Result result) {
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
        // Enviamos la respuesta obtenida de Dialogflow
        String speech = result.getFulfillment().getSpeech();
        MessageSendToDialogflow(speech);
        if (!listService.isEmpty()) { // Para saber si el JSON no esta vacio.
            // Asignamos los valores leidos del JSON que envia Dialogflow y los asignamos a las varibales del Modelo TextMessageModel.
            textMessageModel.setViewTypeMessage(ChatBotReferences.VIEW_TYPE_MESSAGE_CARD_VIEW_MAP);
            textMessageModel.setService(listService.get(0));
            textMessageModel.setTitulo(listService.get(0).getName());
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
        // Enviamos la respuesta obtenida de Dialogflow
        String speech = result.getFulfillment().getSpeech();
        MessageSendToDialogflow(speech);
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
        // Enviamos la respuesta obtenida de Dialogflow
        String speech = result.getFulfillment().getSpeech();
        MessageSendToDialogflow(speech);
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
        rvListMessages.setAdapter(messagesAdapter);
        messagesAdapter.notifyDataSetChanged();
        setScrollbarChat();
    }

    // Método para hacer hablar al Chat Bot.
    private void TextToSpeechChatBot(final String message) {
        Thread threadSpeakChatBot = new Thread(new Runnable() {
            @Override
            public void run() {
                toSpeechChatBot = new TextToSpeech();
                toSpeechChatBot.setUsernameAndPassword(ChatBotReferences.USERNAME_API_WATSON, ChatBotReferences.PASSWORD_API_WATSON);
                toSpeechChatBot.setEndPoint(ChatBotReferences.END_POINT_API_WATSON);
                StreamPlayerClass streamPlayerClass = new StreamPlayerClass();
                streamPlayerClass.playStream(toSpeechChatBot.synthesize(message, Voice.ES_ENRIQUE).execute());
            }
        });
        threadSpeakChatBot.start();
    }
}
