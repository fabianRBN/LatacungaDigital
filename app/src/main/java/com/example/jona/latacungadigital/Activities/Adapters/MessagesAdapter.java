package com.example.jona.latacungadigital.Activities.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jona.latacungadigital.Activities.Clases.MessageMapAttractiveHowToGet;
import com.example.jona.latacungadigital.Activities.Fragments.ChatTextFragment;
import com.example.jona.latacungadigital.Activities.RecycleMessageHolders.AttractiveHowToGetHolder;
import com.example.jona.latacungadigital.Activities.RecycleMessageHolders.AttractiveMessageHolder;
import com.example.jona.latacungadigital.Activities.RecycleMessageHolders.ChatBotMessageHolder;
import com.example.jona.latacungadigital.Activities.RecycleMessageHolders.ChatBotTypingHolder;
import com.example.jona.latacungadigital.Activities.RecycleMessageHolders.DetailServiceMessageHolder;
import com.example.jona.latacungadigital.Activities.RecycleMessageHolders.MapMessageHolder;
import com.example.jona.latacungadigital.Activities.RecycleMessageHolders.UserMessageHolder;
import com.example.jona.latacungadigital.Activities.References.ChatBotReferences;
import com.example.jona.latacungadigital.Activities.Views.MessageCardDetailServiceView;
import com.example.jona.latacungadigital.Activities.Views.MessageCardMapListItemView;
import com.example.jona.latacungadigital.Activities.modelos.TextMessageModel;
import com.example.jona.latacungadigital.R;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter {

    private List<TextMessageModel> listChatModel;
    private List<MessageCardMapListItemView> listMessageCardMapView;
    private List<MessageMapAttractiveHowToGet> listMessageAttractiveHowToGet;
    private ChatTextFragment chatTextFragment;
    private Context context;

    public MessagesAdapter(List<TextMessageModel> listChatModels, Context context) {
        this.listChatModel = listChatModels;
        this.context = context;
    }

    // Getters and Setters.
    public List<MessageMapAttractiveHowToGet> getListMessageAttractiveHowToGet() { return listMessageAttractiveHowToGet; }

    public void setListMessageAttractiveHowToGet(List<MessageMapAttractiveHowToGet> listMessageAttractiveHowToGet) {
        this.listMessageAttractiveHowToGet = listMessageAttractiveHowToGet;
    }

    public List<MessageCardMapListItemView> getListMessageCardMapView() { return listMessageCardMapView; }

    public void setListMessageCardMapView(List<MessageCardMapListItemView> listMessageCardMapView) {
        this.listMessageCardMapView = listMessageCardMapView;
    }

    public ChatTextFragment getChatTextFragment() {
        return chatTextFragment;
    }

    public void setChatTextFragment(ChatTextFragment chatTextFragment) { this.chatTextFragment = chatTextFragment; }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == ChatBotReferences.VIEW_TYPE_MESSAGE_USER) { // Si el mensaje es del usuario se añade el view de esta al view de la lista de mensajes.
            Log.d("ViewType 6", "int: "+ viewType);
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_user, parent, false);
            return new UserMessageHolder(view);
        } else if (viewType == ChatBotReferences.VIEW_TYPE_MESSAGE_CHATBOT) { // Si el mensaje es del ChatBot se añade el view de esta al view de la lista de mensajes.
            Log.d("ViewType 5", "int: "+ viewType);
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_chatbot, parent, false);
            return new ChatBotMessageHolder(view, context);
        } else if (viewType == ChatBotReferences.VIEW_TYPE_MESSAGE_CHATBOT_TYPING) { // Si el mensaje es del ChatBotTyping se añade el view de esta al view de la lista de mensajes.
            Log.d("ViewType 4", "int: "+ viewType);
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chatbot_is_typing, parent, false);
            return new ChatBotTypingHolder(view, context);
        } else if (viewType == ChatBotReferences.VIEW_TYPE_MESSAGE_ATTRACTIVE_CHATBOT) { // Si el mensaje es sobre la informacion de un atractivo turistico.
            Log.d("ViewType 3", "int: "+ viewType);
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.attractive_information, parent, false);
            return new AttractiveMessageHolder(view);
        } else if (viewType == ChatBotReferences.VIEW_TYPE_MESSAGE_CARD_VIEW_MAP) { // Si el mensaje muestra un mapa se añade el view message_cv_map
            Log.d("ViewType 2", "int: "+ viewType);
            MessageCardMapListItemView messageCardMapListItemView = new MessageCardMapListItemView(parent.getContext());
            messageCardMapListItemView.setMessagesAdapter(this);
            messageCardMapListItemView.mapViewOnCreate(null);
            listMessageCardMapView.add(messageCardMapListItemView);
            return new MapMessageHolder(messageCardMapListItemView);
        } else if (viewType == ChatBotReferences.VIEW_TYPE_MESSAGE_MAP_ATTRACTIVE_HOW_TO_GET) { // Si el mensaje es de como llegar a un sitio turistico.
            Log.d("ViewType 1", "int: "+ viewType);
            MessageMapAttractiveHowToGet messageAttractiveHowToGet = new MessageMapAttractiveHowToGet(parent.getContext());
            messageAttractiveHowToGet.setMessagesAdapter(this);
            messageAttractiveHowToGet.mapViewOnCreate(null);
            listMessageAttractiveHowToGet.add(messageAttractiveHowToGet);
            return new AttractiveHowToGetHolder(messageAttractiveHowToGet);
        } else if (viewType == ChatBotReferences.VIEW_TYPE_MESSAGE_CARD_VIEW_DETAIL_SERVICE) { // Si el mensaje es de como llegar a un sitio turistico.
            Log.d("ViewType 0", "int: "+ viewType);
            MessageCardDetailServiceView messageCardDetailServiceView = new MessageCardDetailServiceView(parent.getContext());
            return new DetailServiceMessageHolder(messageCardDetailServiceView);
        }
        Log.d("ViewType null", "int: "+ viewType);
        return null;
    }

    // Método para pasar el objeto del mensaje a un ViewHolder para que los contenidos puedan vincularse a la IU.
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TextMessageModel message = listChatModel.get(position);

        switch (holder.getItemViewType()) {
            case ChatBotReferences.VIEW_TYPE_MESSAGE_USER:
                ((UserMessageHolder) holder).bind(message);
                break;
            case ChatBotReferences.VIEW_TYPE_MESSAGE_CHATBOT:
                ((ChatBotMessageHolder) holder).bind(message);
                break;
            case ChatBotReferences.VIEW_TYPE_MESSAGE_CHATBOT_TYPING:
                ((ChatBotTypingHolder) holder).bind();
                break;
            case ChatBotReferences.VIEW_TYPE_MESSAGE_ATTRACTIVE_CHATBOT:
                AttractiveAdpater attractiveAdpater = new AttractiveAdpater(context, listChatModel.get(position).getListImagesURL());
                ((AttractiveMessageHolder) holder).getViewPager().setAdapter(attractiveAdpater);
                ((AttractiveMessageHolder) holder).bind(message);
                break;
            case ChatBotReferences.VIEW_TYPE_MESSAGE_CARD_VIEW_MAP:
                MapMessageHolder mapMessageHolder = (MapMessageHolder) holder;
                mapMessageHolder.bind(message);
                break;
            case ChatBotReferences.VIEW_TYPE_MESSAGE_MAP_ATTRACTIVE_HOW_TO_GET:
                AttractiveHowToGetHolder attractiveHowToGetHolder = (AttractiveHowToGetHolder) holder;
                attractiveHowToGetHolder.bind(message);
                break;
            case ChatBotReferences.VIEW_TYPE_MESSAGE_CARD_VIEW_DETAIL_SERVICE:
                DetailServiceMessageHolder detailServiceMessageHolder = (DetailServiceMessageHolder) holder;
                detailServiceMessageHolder.bind(message);
                break;
        }
    }

    // Método para obtener la posición de donde se encuentra el mensaje.
    @Override
    public int getItemCount() {
        return listChatModel.size();
    }

    // Método para determinar el ViewType de acuerdo a quien envio el mensaje.
    @Override
    public int getItemViewType(int position) {
        return listChatModel.get(position).getViewTypeMessage();
//        if (listChatModel.get(position).getViewTypeMessage() == ChatBotReferences.VIEW_TYPE_MESSAGE_USER) {
//            // Si el usuario mando el mensaje.
//            return ChatBotReferences.VIEW_TYPE_MESSAGE_USER;
//        } else if (listChatModel.get(position).getViewTypeMessage() == ChatBotReferences.VIEW_TYPE_MESSAGE_CHATBOT) {
//            // Si el ChatBot envio el mensaje.
//            return ChatBotReferences.VIEW_TYPE_MESSAGE_CHATBOT;
//        } else if (listChatModel.get(position).getViewTypeMessage() == ChatBotReferences.VIEW_TYPE_MESSAGE_CHATBOT_TYPING) {
//            // Si el ChatBot esta escribiendo el mensaje.
//            return ChatBotReferences.VIEW_TYPE_MESSAGE_CHATBOT_TYPING;
//        } else if (listChatModel.get(position).getViewTypeMessage() == ChatBotReferences.VIEW_TYPE_MESSAGE_ATTRACTIVE_CHATBOT){
//            // El chatbot envia un mensaje con informacion de un atractivo turistico
//            return ChatBotReferences.VIEW_TYPE_MESSAGE_ATTRACTIVE_CHATBOT;
//        } else if (listChatModel.get(position).getViewTypeMessage() == ChatBotReferences.VIEW_TYPE_MESSAGE_CARD_VIEW_MAP) {
//            // El chatbot envia un mensaje con un mapa
//            return ChatBotReferences.VIEW_TYPE_MESSAGE_CARD_VIEW_MAP;
//        } else if (listChatModel.get(position).getViewTypeMessage() == ChatBotReferences.VIEW_TYPE_MESSAGE_CARD_VIEW_DETAIL_SERVICE) {
//            // El chatbot envia un mensaje con informacion de un servicio
//            return ChatBotReferences.VIEW_TYPE_MESSAGE_CARD_VIEW_DETAIL_SERVICE;
//        } else {
//            // El chat bot muestra como llegar al lugar turistico.
//            return ChatBotReferences.VIEW_TYPE_MESSAGE_MAP_ATTRACTIVE_HOW_TO_GET;
//        }
    }
}
