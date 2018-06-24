package com.example.jona.latacungadigital.Activities.RecycleMessageHolders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.jona.latacungadigital.Activities.Clases.CharacterClass;
import com.example.jona.latacungadigital.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatBotTypingHolder extends RecyclerView.ViewHolder {

    private CircleImageView circleImageViewTyping;
    private Context context;

    public ChatBotTypingHolder(View itemView, Context context) {
        super(itemView);

        circleImageViewTyping = itemView.findViewById(R.id.chatBotTypingImage);
        this.context = context;
    }

    public void bind() {
        // Se coloca la imagen del personaje en el chatbot.
        CharacterClass characterClass = new CharacterClass();
        characterClass.ReadCharacterFromDatabase(new CharacterClass.DataOfCharacters() {
            @Override
            public void dataCharacterSelected(String nameCharacter, String imageCharacterURL) {
                Glide.with(context).load(imageCharacterURL).crossFade().centerCrop().into(circleImageViewTyping);
            }
        });
    }
}