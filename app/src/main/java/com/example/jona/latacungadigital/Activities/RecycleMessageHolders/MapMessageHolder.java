package com.example.jona.latacungadigital.Activities.RecycleMessageHolders;

import android.support.v7.widget.RecyclerView;

import com.example.jona.latacungadigital.Activities.Views.MessageCardMapListItemView;
import com.example.jona.latacungadigital.Activities.modelos.TextMessageModel;

public class MapMessageHolder extends RecyclerView.ViewHolder {
    // Declaracion de la clase MessageCardMapListItemView
    private MessageCardMapListItemView messageCardMapListItemView;

    // Constructor de la clase
    public MapMessageHolder(MessageCardMapListItemView messageCardMapListItemView) {
        super(messageCardMapListItemView);
        this.messageCardMapListItemView = messageCardMapListItemView;
    }

    private void mapViewListItemViewOnResume() {
        if (messageCardMapListItemView != null) {
            messageCardMapListItemView.mapViewOnResume();
        }
    }

    private void mapViewListItemViewSetMessage(TextMessageModel message) {
        if (messageCardMapListItemView != null) {
            messageCardMapListItemView.setMessage(message);
        }
    }

    public void bind(TextMessageModel message) { // Se asigna la informacion consultada a los componentes del layout.
        mapViewListItemViewSetMessage(message);
        mapViewListItemViewOnResume();
    }
}
