package com.example.jona.latacungadigital.Activities.RecycleMessageHolders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.jona.latacungadigital.Activities.ChatBotActivity;
import com.example.jona.latacungadigital.Activities.Clases.CharacterClass;
import com.example.jona.latacungadigital.Activities.modelos.TextMessageModel;
import com.example.jona.latacungadigital.R;
import com.github.library.bubbleview.BubbleTextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatBotMessageHolder extends RecyclerView.ViewHolder {

    private BubbleTextView messageText;
    private CircleImageView circleImageView;
    private Context context;

    public ChatBotMessageHolder(View itemView, Context context) {
        super(itemView);

        messageText = itemView.findViewById(R.id.textMessageChatBot);
        circleImageView = itemView.findViewById(R.id.chatBotImage);
        this.context = context;
    }

    public void bind(TextMessageModel message) {
        messageText.setText(message.getMessage()); // Se envia el mensaje del ChatBot.
        // Se coloca la imagen del personaje en el chatbot.
        CharacterClass characterClass = new CharacterClass();
            characterClass.ReadCharacterFromDatabase(new CharacterClass.DataOfCharacters() {
                @Override
                public void dataCharacterSelected(String nameCharacter, String imageCharacterURL) {
                    if(isValidContextForGlide(context)) {
                        Glide.with(context).load(imageCharacterURL).crossFade().centerCrop().into(circleImageView);
                    }
                }
            });
    }

    public boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof ChatBotActivity) {
            final ChatBotActivity activity = (ChatBotActivity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }
}
