package com.example.jona.latacungadigital.Activities.RecycleMessageHolders;

import android.support.v7.widget.RecyclerView;

import com.example.jona.latacungadigital.Activities.Clases.ServiceClass;
import com.example.jona.latacungadigital.Activities.Views.MessageCardDetailServiceView;
import com.example.jona.latacungadigital.Activities.modelos.TextMessageModel;

public class DetailServiceMessageHolder extends RecyclerView.ViewHolder {
    // Declaracion de la clase MessageCardDetailServiceView
    private MessageCardDetailServiceView messageCardDetailServiceView;

    public DetailServiceMessageHolder(MessageCardDetailServiceView messageCardDetailServiceView) {
        super(messageCardDetailServiceView);
        this.messageCardDetailServiceView = messageCardDetailServiceView;
    }

    private void messageCardDetailServiceViewSetService(ServiceClass service) {
        if (messageCardDetailServiceView != null) {
            messageCardDetailServiceView.setService(service);
        }
    }

    public void bind(TextMessageModel message) { // Se asigna la informacion consultada a los componentes del layout.
        messageCardDetailServiceViewSetService(message.getService());
    }
}
