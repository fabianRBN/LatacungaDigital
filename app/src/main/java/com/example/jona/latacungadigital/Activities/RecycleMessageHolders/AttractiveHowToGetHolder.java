package com.example.jona.latacungadigital.Activities.RecycleMessageHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.jona.latacungadigital.Activities.Clases.MessageMapAttractiveHowToGet;
import com.example.jona.latacungadigital.Activities.modelos.TextMessageModel;

public class AttractiveHowToGetHolder extends RecyclerView.ViewHolder {

    private MessageMapAttractiveHowToGet messageMapAttractiveHowToGet;

    public AttractiveHowToGetHolder(MessageMapAttractiveHowToGet messageMapAttractiveHowToGet) {
        super(messageMapAttractiveHowToGet);
        this.messageMapAttractiveHowToGet = messageMapAttractiveHowToGet;
    }

    private void mapViewListItemViewOnResume() {
        if (messageMapAttractiveHowToGet != null) {
            messageMapAttractiveHowToGet.mapViewOnResume();
        }
    }

    private void mapViewListItemViewSetMessage(TextMessageModel message) {
        if (messageMapAttractiveHowToGet != null) {
            messageMapAttractiveHowToGet.setMessage(message);
        }
    }

    public void bind(TextMessageModel messageModel) {
        mapViewListItemViewSetMessage(messageModel);
        mapViewListItemViewOnResume();
    }
}
