package com.example.jona.latacungadigital.Activities.RecycleMessageHolders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.jona.latacungadigital.Activities.ChatBotActivity;
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
        if(isValidContextForGlide(context)){
            characterClass.ReadCharacterFromDatabase(new CharacterClass.DataOfCharacters() {
                @Override
                public void dataCharacterSelected(String nameCharacter, String imageCharacterURL) {
                    Glide.with(context.getApplicationContext()).load(imageCharacterURL).crossFade().centerCrop().into(circleImageViewTyping);
                }
            });
        }
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