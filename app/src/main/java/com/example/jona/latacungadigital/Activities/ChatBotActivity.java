package com.example.jona.latacungadigital.Activities;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.jona.latacungadigital.Activities.Clases.CharacterClass;
import com.example.jona.latacungadigital.Activities.Clases.NetworkReceiverClass;
import com.example.jona.latacungadigital.Activities.Fragments.ChatTextFragment;
import com.example.jona.latacungadigital.Activities.Fragments.MapaFragment;
import com.example.jona.latacungadigital.R;

public class ChatBotActivity extends AppCompatActivity implements ChatTextFragment.OnFragmentInteractionListener, MapaFragment.OnFragmentInteractionListener {

    Toolbar toolBarChatBot;
    NetworkReceiverClass networkReceiverClass;
    ActionBar actionBar;
    ChatTextFragment chatFragment;

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
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            CharacterClass characterClass = new CharacterClass();
            characterClass.ReadCharacterFromDatabase(new CharacterClass.DataOfCharacters() {
                @Override
                public void nameCharacter(String nameCharacter, String imageCharacterURL) {
                    actionBar.setTitle(nameCharacter);
                }
            });

            boolean showMessageToStartActivity = false; // Para no mostrar el mensaje de "Conexión exitosa" al inicio de la actividad.
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            networkReceiverClass = new NetworkReceiverClass(actionBar, ChatBotActivity.this, showMessageToStartActivity);
            registerReceiver(networkReceiverClass, filter);

        }
    }

    // Métodos para abrir el fragmento del chatbot.
    private void ChatTextFragment() {
        chatFragment = new ChatTextFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.chatScreen, chatFragment);

        // Commit a la transacción
        transaction.commit();
    }

    // Métodos para cambiar el fragment
    public void changeFragmente(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.animation_slide_in_down,R.anim.animation_fade_out,R.anim.animation_fade_in,R.anim.animation_slide_out_down);
        transaction.add(R.id.chatScreen, fragment);
        transaction.addToBackStack(null);
        // Commit a la transacción
        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {}

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(networkReceiverClass); // Para destruir la comunicacion cuando se cierra la actividad.
    }
}
