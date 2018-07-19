package com.example.jona.latacungadigital.Activities.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.jona.latacungadigital.Activities.Adapters.MessagesAdapter;
import com.example.jona.latacungadigital.Activities.Clases.CharacterClass;
import com.example.jona.latacungadigital.Activities.Clases.DialogflowClass;
import com.example.jona.latacungadigital.Activities.Clases.NetworkReceiverClass;
import com.example.jona.latacungadigital.Activities.Clases.SaveListMessageClass;
import com.example.jona.latacungadigital.Activities.References.ChatBotReferences;
import com.example.jona.latacungadigital.Activities.References.PermissionsReferences;
import com.example.jona.latacungadigital.Activities.Views.MessageCardMapListItemView;
import com.example.jona.latacungadigital.Activities.modelos.TextMessageModel;
import com.example.jona.latacungadigital.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ChatTextFragment extends Fragment{

    private OnFragmentInteractionListener mListener;

    // Declaración de variables para poder controlar los mensajes del usuario y del ChatBot.
    private DialogAppFragment dialogAppFragment; // Variable para controlar el Dialogo de eliminar mensajes.
    private boolean shouldRecreate = true; // Variable para controlar el onActivityResult() con onResume().
    private boolean isMessageToSend = false;
    private boolean isSwicthChanged = false; // Varible para controlar el switch de activar la voz del personaje.
    private String messageToSend;
    private int typeDialog;
    private Switch switchTextToSpeech;
    private boolean receiversRegistered; // Variable para controlar el brodcast de la conexion a internet.

    private NetworkReceiverClass networkReceiverClass;
    private ActionBar actionBar;

    // Variables obtenidas de la vista fragment_chat_text.xml
    private View view;
    private View mainLayout;
    private Toolbar toolBarChatBot;
    private RecyclerView rvListMessages;
    private EditText txtMessageUserSend;
    private FloatingActionButton btnSendMessage;

    // Variables para manejar el RecyclerView de mensajes
    private LinearLayoutManager linearLayoutManager;
    private MessagesAdapter messagesAdapter;

    // Variables para comunicarse con Dialogflow
    public DialogflowClass dialogflowClass;

    // Variables para el manejo de mensajes
    private List<TextMessageModel> listMessagesText;
    private List<MessageCardMapListItemView> listMessageCardMapView;

    // --------------------------------------------- //
    // Constructor                                   //
    // --------------------------------------------- //
    public ChatTextFragment() { }

    // --------------------------------------------- //
    // Métodos get and set del fragmento             //
    // --------------------------------------------- //
    public DialogflowClass getDialogflowClass() { return dialogflowClass; }

    public boolean getIsMessageToSend() { return isMessageToSend; }

    public int getTypeDialog() { return typeDialog; }

    public void setIsMessageToSend(boolean isMessageToSend) { this.isMessageToSend = isMessageToSend; }

    public String getMessageToSend() { return messageToSend; }

    public void setMessageToSend(String messageToSend) { this.messageToSend = messageToSend; }

    public DialogAppFragment getDialogAppFragment() { return dialogAppFragment; }

    public View getMainLayout() {
        return mainLayout;
    }

    // --------------------------------------------- //
    // Métodos de la  clase Fragment                 //
    // --------------------------------------------- //
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listMessagesText = new ArrayList<>(); // Incializar la lista de los mensajes de texto.
        listMessageCardMapView = new ArrayList<>(); // Incializar la lista de los mensajes con mapas.
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Instanciar las variables de la vista fragment_chat_text.xml
        view = inflater.inflate(R.layout.fragment_chat_text, container, false);
        mainLayout = view.findViewById(R.id.fragment_chat_linear_layout);
        toolBarChatBot = view.findViewById(R.id.toolBarChatBot);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolBarChatBot);
        rvListMessages = view.findViewById(R.id.listOfMessages);
        txtMessageUserSend = view.findViewById(R.id.txtUserMessageSend);
        btnSendMessage = view.findViewById(R.id.btnSendMessage);

        // Instanciar la lista de mensajes
        linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setStackFromEnd(true);
        rvListMessages.setLayoutManager(linearLayoutManager);

        messagesAdapter = new MessagesAdapter(listMessagesText, view.getContext());
        messagesAdapter.setChatTextFragment(this);
        messagesAdapter.setListMessageCardMapView(listMessageCardMapView);
        rvListMessages.setAdapter(messagesAdapter); // Adaptamos el Recicle View a al adaptador que contendran los mensajes.


        dialogflowClass = new DialogflowClass(view.getContext(), listMessagesText, rvListMessages, messagesAdapter, txtMessageUserSend);
        dialogflowClass.ConfigurationDialogflow(); // Para configurar el API de Dialogflow.

        dialogAppFragment = new DialogAppFragment();

        // Se envia false para no mostrar el mensaje de "Conexión exitosa" al inicio de la actividad.
        SetupActionBar(false); // Para dar el titulo y el subtitulo que va a tener el Action Bar.

        setHasOptionsMenu(true); // Para habilitar las opciones del Toolbar.

        ChangeIconButton(); // Se llama al método de cambiar de icono.

        // Acción del boton para Enviar Mensaje.
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!txtMessageUserSend.getText().toString().equals("")) { // Si el mensaje es diferente de nulo significa que es un mensaje de texto.
                    dialogflowClass.CreateMessage(txtMessageUserSend.getText().toString()); // Para enviar un mensaje del usuario o de Dialogflow.
                } else { // Caso contrario es un mensaje de voz.
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        requestMicrophonePermission();
                    }else{
                        startSpeech();
                    }
                }

            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Se infla el menu_chatbot.
        inflater.inflate(R.menu.menu_chatbot, menu);

        // Se infla el Switch para activar la voz del personaje.
        MenuItem menuItem = menu.findItem(R.id.navChatbotTextToSpeech);
        menuItem.setActionView(R.layout.switch_text_to_speech);

        switchTextToSpeech = menuItem.getActionView().findViewById(R.id.swicthTextToSpeech);

        // Para obtener el estado del Switch de la voz del personaje.
        if (isSwicthChanged) {
            dialogflowClass.setTextToSpeech(readStatusSwitchTextToSpeech());
            switchTextToSpeech.setChecked(readStatusSwitchTextToSpeech());
        }

        switchTextToSpeech.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dialogflowClass.setTextToSpeech(true);
                } else {
                    dialogflowClass.setTextToSpeech(false);
                    dialogflowClass.getStreamPlayerClass().interrupt(); // Para interrumpir si esque el usuario desactiva la Opción de Voz.
                }
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.navChatbotDeleteMessages:
                openDialogDeleteMessages();
                return true;

            case R.id.navChatbotChangeCharacter:
                openDialogChangeCharacter();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void openDialogDeleteMessages() {
        // Se crea una instancia de la clase DialogAppFragement y se la muestra.
        Bundle bundle = new Bundle();
        typeDialog = ChatBotReferences.DIALOG_DELETE_MESSAGE;
        bundle.putInt("Type_Dialog", typeDialog);
        dialogAppFragment.setArguments(bundle);

        dialogAppFragment.show(Objects.requireNonNull(getFragmentManager()), "NoticeDialogFragment");
    }

    private void openDialogChangeCharacter() {
        // Se crea una instancia de la clase DialogAppFragement y se la muestra.
        Bundle bundle = new Bundle();
        typeDialog = ChatBotReferences.DIALOG_CHANGE_CHARACTER;
        bundle.putInt("Type_Dialog", typeDialog);
        dialogAppFragment.setArguments(bundle);

        dialogAppFragment.show(Objects.requireNonNull(getFragmentManager()), "NoticeDialogFragment");
    }

    // Método para establecer el nombre del personaje y si esta activo de acuerdo a la conexión a internet.
    private void SetupActionBar(boolean statusTSnackbar) {
        actionBar = ((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("No Definido"); // Se le da un titulo ya establecido.

            CharacterClass characterClass = new CharacterClass();
            characterClass.ReadCharacterFromDatabase(new CharacterClass.DataOfCharacters() {
                @Override
                public void dataCharacterSelected(String nameCharacter, String imageCharacterURL) {
                    actionBar.setTitle(nameCharacter);
                }
            });

            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

            networkReceiverClass = new NetworkReceiverClass(actionBar, getActivity(), statusTSnackbar);
            // Se le asigna la clase DialogflowClass para controlar la lista de mensajes.
            networkReceiverClass.setDialogflowClass(dialogflowClass);
            if (!receiversRegistered) {
                getActivity().registerReceiver(networkReceiverClass, filter);
                receiversRegistered = true;
            }
        }
    }

    // Método para leer la lista de mensajes del Shared Preferences.
    private void ReadListMessageFromSharedPreferences() {
        // Solo se va a restablecer la lista de mensajes si la varible es verdadera y con ese evitamos problemas cuando el usuario manda un mensaje hablado.
        if (shouldRecreate) {
            // Para leer la lista de mensajes del Shared Preferences.
            SaveListMessageClass saveListMessageClass = new SaveListMessageClass(dialogflowClass.getListMessagesText(), view.getContext());
            // Asiganamos la lista cargada a la lista de la clase DialogflowClass.
            dialogflowClass.setListMessagesText(saveListMessageClass.ReadListMessages());
            // Para remover la vista de chat is bot is typing si esque se queda guardado en la lista de mensajes.
            if (dialogflowClass.getListMessagesText().size() != 0) dialogflowClass.RemoveMessageTypingToDialogflow();
            // Adaptamos la lista de mensajes leidos al adaptador del Recycle View para poner los mensajes.
            dialogflowClass.addMessagesAdapter(dialogflowClass.getListMessagesText());
        }
    }

    // Método para actualizar la lista de mensajes del Shared Preferences.
    private void UpdateListMessageFromSharedPreferences() {
        // Para guardar la lista de mensajes en el Shared Preferences.
        SaveListMessageClass saveListMessageClass = new SaveListMessageClass(dialogflowClass.getListMessagesText(), view.getContext());
        saveListMessageClass.SaveListMessage();
    }

    // Método para saber si se permite guardar los mensajes en el chat.
    public boolean isPermitSaveMessages() {
        SaveListMessageClass saveListMessageClass = new SaveListMessageClass(getContext());
        return saveListMessageClass.isPermitSaveMessages();
    }

    // Método para guardar el estado del Switch de la voz del personaje.
    private void saveStatusSwitchTextToSpeech() {
        SaveListMessageClass saveListMessageClass = new SaveListMessageClass(getContext());
        saveListMessageClass.saveStateSwitch(switchTextToSpeech.isChecked());
    }

    // Método para obtener el estado del Switch de la voz del personaje.
    private boolean readStatusSwitchTextToSpeech() {
        SaveListMessageClass saveListMessageClass = new SaveListMessageClass(getContext());
        return saveListMessageClass.readStateSwitch();
    }

    public void getGenreCharacter() {
        // Cuando cambie el switch leemos el genero del personaje.
        CharacterClass characterClass = new CharacterClass();
        characterClass.getGenreCharacter(new CharacterClass.GenreCharacter() {
            @Override
            public void genreCharacter(String genreCharacter) {
                dialogflowClass.setGenreCharacter(genreCharacter);
            }
        });
    }

    // Método para cambiar el icono del boton segun la longitud del texto del usuario.
    private void ChangeIconButton() {
        txtMessageUserSend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtMessageUserSend.getText().toString().trim().length() != 0) { // Si la longuitud del texto es diferente de 0 entonces es un mensaje de texto
                    btnSendMessage.setImageResource(R.drawable.ic_send_message); // Se cambia el icono.
                } else { // Caso contrario es un mensaje de voz.
                    btnSendMessage.setImageResource(R.drawable.ic_keyboard_voice); // Se cambia el icono.
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Método para solicitar el permiso de uso del micrófono
    private void requestMicrophonePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.RECORD_AUDIO)) {
            Snackbar.make(mainLayout,R.string.permission_microphone_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(ChatTextFragment.this.getActivity(),
                                    new String[]{Manifest.permission.RECORD_AUDIO},
                                    PermissionsReferences.REQUEST_CODE_RECORD_AUDIO);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    PermissionsReferences.REQUEST_CODE_RECORD_AUDIO);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionsReferences.REQUEST_CODE_RECORD_AUDIO: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startSpeech();
                }
                break;
            }
            case PermissionsReferences.REQUEST_CODE_ACCESS_FINE_LOCATION: {
                this.listMessageCardMapView.get(this.listMessageCardMapView.size() - 1)
                        .onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }

    }

    // Métodos para enviar mensajes por voz.
    private void startSpeech() {
        // Definimos un intent para realizar en análisis del mensaje por parte del usuario.
        Intent intentGoogleSpeech = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        // Indicamos el modelo de lenguaje para el intent.
        intentGoogleSpeech.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intentGoogleSpeech.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        // Definimos el mensaje que aparecerá cuando salga el popup de Google para que hable el usuario.
        intentGoogleSpeech.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Escuchando...");
        try {
            // Lanzamos la actividad esperando resultados
            startActivityForResult(intentGoogleSpeech, ChatBotReferences.SPEECH_RECOGNITION_CODE);
        } catch (ActivityNotFoundException a) {
            // Caso contrario indicamos que el reconocimiento de voz no es compatible con este dispositivo.
            Toast.makeText(view.getContext(),
                    "¡Lo siento! El reconocimiento de voz no es compatible con este dispositivo.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // Método para recibir lo que habla el usuario.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ChatBotReferences.SPEECH_RECOGNITION_CODE: {
                shouldRecreate = false; // establecemos en false para que se muestre el mensaje del usuario en la lista de mensajes.
                if (resultCode == Activity.RESULT_OK && null != data) { //Si el reconocimiento a sido exitoso guardamos lo que dice el usuario.
                    //El intent nos envia un ArrayList aunque en este caso solo utilizaremos la posición 0 porque ahi esta el mensaje.
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String speech = result.get(0); // Guardamos el mensaje en un String.
                    dialogflowClass.CreateMessage(speech); // Enviamos el mensaje que dijo el usuario a Dialogflow.
                }
                break;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(listMessageCardMapView !=null){
            for (MessageCardMapListItemView view : listMessageCardMapView) {
                view.mapViewOnStart();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Se leen los mensajes si el usuario a marcado la opcion "Mensajes en el Chat" en la parte de configuracion de la app.
        if (isPermitSaveMessages()) ReadListMessageFromSharedPreferences(); // Se leen los mensajes guardados del chat.

        getGenreCharacter(); // Obtenemos el genero del personaje.

        if (!receiversRegistered) {
            if (networkReceiverClass.isStatusTSnackbar()) { // Para saber el estado actual de internet.
                SetupActionBar(false); // Si existe internet no mostramos el TSnackbar.
            } else {
                SetupActionBar(true); // Si no hay internet mostramos el TSnackbar con su respectivo mensaje.
            }
        }

        isSwicthChanged = true; // Para que al momento de que la app se resuma se pueda controlar el estado del Switch de la voz del personaje.

        if(listMessageCardMapView !=null){
            for (MessageCardMapListItemView view : listMessageCardMapView) {
                view.mapViewOnResume();
            }
        }
        if (isMessageToSend) {
            getDialogflowClass().CreateMessage(messageToSend);
            isMessageToSend = false;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(listMessageCardMapView != null){
            for (MessageCardMapListItemView view : listMessageCardMapView) {
                view.mapViewOnSaveInstanceState(outState);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (listMessageCardMapView != null) {
            for (MessageCardMapListItemView view : listMessageCardMapView) {
                view.mapViewOnPause();
            }
        }
        dialogflowClass.getStreamPlayerClass().interrupt(); // Para interrumpir si esque el usuario se sale del ChatBot.

        saveStatusSwitchTextToSpeech(); // Guardamos el estado del Switch de la voz del personaje.

        // Se guardan los mensajes si el usuario a marcado la opcion "Mensajes en el Chat" en la parte de configuracion de la app.
        if (isPermitSaveMessages()) UpdateListMessageFromSharedPreferences(); // Modificamos la lista de mensajes.
    }

    @Override
    public void onStop() {
        super.onStop();
        if (listMessageCardMapView != null) {
            for (MessageCardMapListItemView view : listMessageCardMapView) {
                view.mapViewOnStop();
            }
        }

        // Para parar la comunicación del broadcast cuando se pare la actividad.
        if (receiversRegistered) {
            Objects.requireNonNull(getActivity()).unregisterReceiver(networkReceiverClass);
            receiversRegistered = false;
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (listMessageCardMapView != null) {
            for (MessageCardMapListItemView view : listMessageCardMapView) {
                view.mapViewOnLowMemory();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (listMessageCardMapView != null) {
            for (MessageCardMapListItemView view : listMessageCardMapView) {
                view.mapViewOnDestroy();
            }
        }
        dialogflowClass.getStreamPlayerClass().interrupt(); // Para interrumpir si esque el usuario se sale del ChatBot.
        // Para destruir la comunicación cuando se cierra la actividad.
        if (receiversRegistered) Objects.requireNonNull(getActivity()).unregisterReceiver(networkReceiverClass);
    }
}
