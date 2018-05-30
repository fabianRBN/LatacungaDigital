package com.example.jona.latacungadigital.Activities;

import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.jona.latacungadigital.Activities.Fragments.ChatTextFragment;
import com.example.jona.latacungadigital.R;

public class ChatBotActivity extends AppCompatActivity implements ChatTextFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);

        SetupActionBar(); // Para colocar la flecha de volver al menu prinicipal.
        ChatTextFragment(); // Inicializamos la actividad con el fragmento que tiene para interactuar con el chatbot.
    }

    private void SetupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Chat Bot");
        }
    }

    // Métodos para abrir el fragmento del chatbot.
    private void ChatTextFragment() {
        ChatTextFragment chatFragment = new ChatTextFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.chatBotContent, chatFragment);

        // Commit a la transacción
        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
