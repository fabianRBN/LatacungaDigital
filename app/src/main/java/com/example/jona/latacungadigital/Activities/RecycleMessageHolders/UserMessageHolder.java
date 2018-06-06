package com.example.jona.latacungadigital.Activities.RecycleMessageHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.jona.latacungadigital.Activities.modelos.TextMessageModel;
import com.example.jona.latacungadigital.R;
import com.github.library.bubbleview.BubbleTextView;

public class UserMessageHolder extends RecyclerView.ViewHolder {

    BubbleTextView messageText;

    public UserMessageHolder(View itemView) {
        super(itemView);

        messageText = itemView.findViewById(R.id.textMessageUser);
    }

    public void bind(TextMessageModel message) {
        messageText.setText(message.getMessage()); // Se envia el mensaje del Usuario.
    }
}
