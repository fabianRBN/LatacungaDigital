package com.example.jona.latacungadigital.Activities;

import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.jona.latacungadigital.Activities.Clases.CharacterClass;
import com.example.jona.latacungadigital.Activities.Fragments.ChatTextFragment;
import com.example.jona.latacungadigital.Activities.Permisos.AccesoInternet;
import com.example.jona.latacungadigital.R;

public class ChatBotActivity extends AppCompatActivity implements ChatTextFragment.OnFragmentInteractionListener {

    Toolbar toolBarChatBot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);

        toolBarChatBot = findViewById(R.id.toolBarChatBot);
        setSupportActionBar(toolBarChatBot);

        SetupActionBar(); // Para dar el titulo y el subtitulo que va a tener el Action Bar.
        ChatTextFragment(); // Inicializamos la actividad con el fragmento que tiene para interactuar con el chatbot.
    }

    private void SetupActionBar() {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            CharacterClass characterClass = new CharacterClass();
            characterClass.ReadCharacterFromDatabase(new CharacterClass.DataOfCharacters() {
                // Llamamos a este metodo para obtener el nombre consultado de la base de datos.
                @Override
                public void nameCharacter(String nameCharacter, String imageCharacterURL) {
                    actionBar.setTitle(nameCharacter); // Le asignamos el nombre al Action Bar de la aplicacion.
                }
            });

            // Para saber si esta conectado a internet.
            AccesoInternet accesoInternet = new AccesoInternet();
            if (accesoInternet.isNetDisponible(getApplicationContext())) {
                actionBar.setSubtitle("Activo(a) ahora");
            } else {
                actionBar.setSubtitle("Desconectado");
            }

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
