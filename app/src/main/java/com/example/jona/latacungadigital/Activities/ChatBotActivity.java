package com.example.jona.latacungadigital.Activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.example.jona.latacungadigital.Activities.Clases.CharacterClass;
import com.example.jona.latacungadigital.Activities.Fragments.ChatTextFragment;
import com.example.jona.latacungadigital.Activities.Fragments.DialogAppFragment;
import com.example.jona.latacungadigital.Activities.Fragments.MapaFragment;
import com.example.jona.latacungadigital.Activities.References.ChatBotReferences;
import com.example.jona.latacungadigital.Activities.modelos.TextMessageModel;
import com.example.jona.latacungadigital.R;

import java.util.ArrayList;

public class ChatBotActivity extends AppCompatActivity implements ChatTextFragment.OnFragmentInteractionListener, MapaFragment.OnFragmentInteractionListener,
        DialogAppFragment.NoticeDialogListener {

    ChatTextFragment chatFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);
        chatFragment = new ChatTextFragment();
        ChatTextFragment(); // Inicializamos la actividad con el fragmento que tiene para interactuar con el chatbot.
    }

    // Métodos para abrir el fragmento del chatbot.
    private void ChatTextFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.chatScreen, chatFragment);

        // Commit a la transacción
        transaction.commit();
    }

    // Métodos para cambiar el fragment
    public void changeFragmente(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.animation_slide_in_down,R.anim.animation_fade_out,R.anim.animation_fade_in,R.anim.animation_slide_out_down);
        transaction.replace(R.id.chatScreen, fragment);
        transaction.addToBackStack(null);
        // Commit a la transacción
        transaction.commit();
    }

    public void changeFragmentToChatbotAndSendMessage(String message) {
        chatFragment.setMessageToSend(message);
        chatFragment.setIsMessageToSend(true);
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() { super.onResume(); }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDialogConfirmClick(DialogFragment dialog) {

        switch (chatFragment.getTypeDialog()) {
            case ChatBotReferences.DIALOG_DELETE_MESSAGE:
                // Para eliminar los mensajes del Chat Bot.
                chatFragment.getDialogflowClass().getRvListMessages().removeAllViewsInLayout();
                chatFragment.getDialogflowClass().getMessagesAdapter().notifyDataSetChanged();
                chatFragment.getDialogflowClass().setListMessagesText(new ArrayList<TextMessageModel>());
                chatFragment.getDialogflowClass().addMessagesAdapter(new ArrayList<TextMessageModel>());
                break;
            case ChatBotReferences.DIALOG_CHANGE_CHARACTER:
                // Proceso para cambiar de personaje del ChatBot.
                String keyCharacter = chatFragment.getDialogAppFragment().getCharacterAdapter().getKeyCharacter();
                CharacterClass characterClass = new CharacterClass();
                characterClass.updateKeyCharacterSeletedFormUser(keyCharacter);

                // Proceso para obtener el genero del personaje cuando el usuario decida cambiar este.
                characterClass.getGenreCharacterFormChanged(keyCharacter, new CharacterClass.GenreCharacterFromChanged() {
                    @Override
                    public void genreCharacterFromChanged(String genreCharacter) {
                        chatFragment.getDialogflowClass().setGenreCharacter(genreCharacter);
                    }
                });
                break;
        }
    }

    @Override
    public void onDialogCancelClick(DialogFragment dialog) {
        dialog.getDialog().cancel();
    }
}
