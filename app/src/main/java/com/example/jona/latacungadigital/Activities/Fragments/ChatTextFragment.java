package com.example.jona.latacungadigital.Activities.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jona.latacungadigital.Activities.Adapters.MessagesAdapter;
import com.example.jona.latacungadigital.Activities.Clases.DialogflowClass;
import com.example.jona.latacungadigital.Activities.Clases.MessageCardMapListItemView;
import com.example.jona.latacungadigital.Activities.Clases.MessageMapAttractiveHowToGet;
import com.example.jona.latacungadigital.Activities.Clases.SaveListMessageClass;
import com.example.jona.latacungadigital.Activities.References.ChatBotReferences;
import com.example.jona.latacungadigital.Activities.modelos.TextMessageModel;
import com.example.jona.latacungadigital.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatTextFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatTextFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatTextFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    // Declaración de variables para poder controlar los mensajes del usuario y del ChatBot.
    private RecyclerView rvListMessages;
    private List<TextMessageModel> listMessagesText;
    private List<MessageCardMapListItemView> listMessageCardMapView;
    private List<MessageMapAttractiveHowToGet> listMessageAttractiveHowToGet;
    private FloatingActionButton btnSendMessage;
    private EditText txtMessageUserSend;
    private MessagesAdapter messagesAdapter;
    private TextView txtMessageWelcome;
    private boolean shouldRecreate = true; // Variable para controlar el onActivityResult() con onResume().

    public static DialogflowClass dialogflowClass;
    private View view;

    public ChatTextFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatTextFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatTextFragment newInstance(String param1, String param2) {
        ChatTextFragment fragment = new ChatTextFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        listMessagesText = new ArrayList<>(); // Incializar la lista de los mensajes de texto.
        listMessageCardMapView = new ArrayList<>();
        listMessageAttractiveHowToGet = new ArrayList<>();

        view = inflater.inflate(R.layout.fragment_chat_text, container, false);

        rvListMessages = view.findViewById(R.id.listOfMessages); // Instanciar la variable con el Id del RecicleView.

        btnSendMessage = view.findViewById(R.id.btnSendMessage); // Instanciar la varibale con el id del Button.

        txtMessageUserSend = view.findViewById(R.id.txtUserMessageSend); // Instanciar la varibale con el id del Edit Text.

        txtMessageWelcome = view.findViewById(R.id.txtMessageWelcome); // Instanciar la varibale con el id del Text View.

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setStackFromEnd(true);
        rvListMessages.setLayoutManager(linearLayoutManager);

        ValidateAudioRecord(view); // Permitimos la entrada de audio al chat.

        messagesAdapter = new MessagesAdapter(listMessagesText, view.getContext());
        messagesAdapter.setChatTextFragment(this);
        messagesAdapter.setListMessageCardMapView(listMessageCardMapView);
        messagesAdapter.setListMessageAttractiveHowToGet(listMessageAttractiveHowToGet);
        rvListMessages.setAdapter(messagesAdapter); // Adaptamos el Recicle View a al adaptador que contendran los mensajes.

        dialogflowClass = new DialogflowClass(view, listMessagesText, rvListMessages, messagesAdapter, txtMessageUserSend);
        dialogflowClass.ConfigurationDialogflow(); // Para configurar el API de Dialogflow.

        GiveWelcomeMessage(); // Dar el mensaje de Bienvenida al usuario.

        ChangeIconButton(); // Se llama al método de cambiar de icono.

        // Acción del boton para Enviar Mensaje.
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!txtMessageUserSend.getText().toString().equals("")) { // Si el mensaje es diferente de nulo significa que es un mensaje de texto.
                    dialogflowClass.CreateMessage(txtMessageUserSend.getText().toString()); // Para enviar un mensaje del usuario o de Dialogflow.
                    GiveWelcomeMessage();
                } else { // Caso contrario es un mensaje de voz.
                    startSpeech();
                    GiveWelcomeMessage();
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

    private void ReadListMessageFromSharedPreferences() {
        // Solo se va a restablecer la lista de mensajes si la varible es verdadera y con ese evitamos problemas cuando el usuario manda un mensaje hablado.
        if (shouldRecreate) {
            // Para leer la lista de mensajes del Shared Preferences.
            SaveListMessageClass saveListMessageClass = new SaveListMessageClass(dialogflowClass.getListMessagesText(), view.getContext());
            // Asiganamos la lista cargada a la lista de la clase DialogflowClass.
            dialogflowClass.setListMessagesText(saveListMessageClass.ReadListMessages());
            // Adaptamos la lista de mensajes leidos al adaptador del Recycle View para poner los mensajes.
            dialogflowClass.addMessagesAdapter(dialogflowClass.getListMessagesText());
        }

        GiveWelcomeMessage(); // Verificamos si la lista esta vacia para dar la bienvenida al usuario.
    }

    private void UpdateListMessageFromSharedPreferences() {
        // Para guardar la lista de mensajes en el Shared Preferences.
        SaveListMessageClass saveListMessageClass = new SaveListMessageClass(dialogflowClass.getListMessagesText(), view.getContext());
        saveListMessageClass.SaveListMessage();

        GiveWelcomeMessage(); // Verificamos si la lista esta vacia para dar la bienvenida al usuario.
    }

    // Método para obtener el usuario Logeado de la aplicación.
    private String getCurrentUserSigned() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String nameUser = "";

        if (user != null) { // Si el usuario esta logeado.
            nameUser = user.getDisplayName(); // Obtenemos el nombre del usuario logeado.
        }

        return nameUser; // Retornamos el nombre del usuario.
    }

    // Método para establecer el mensaje de bienvenida.
    private String setWelcomeMessage() {
        return "Saludos " + getCurrentUserSigned() + ", aquí usted puede preguntar acerca de los lugares turísticos, " +
                "relacionados al centro histórico de Latacunga.";
    }

    // Método para dar el mensaje unicamente cuando recien entra al chat bot.
    private void GiveWelcomeMessage() {
        if (dialogflowClass.getListMessagesText().size() == 0) {
            txtMessageWelcome.setVisibility(View.VISIBLE);
            txtMessageWelcome.setText(setWelcomeMessage());
        } else {
            txtMessageWelcome.setVisibility(View.GONE);
        }
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

    // Método para permitir la entrada de audio.
    private void ValidateAudioRecord(View view) {
        if (ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1);
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
        if(listMessageCardMapView !=null){
            for (MessageCardMapListItemView view : listMessageCardMapView) {
                view.mapViewOnResume();
            }
        }

        if(listMessageAttractiveHowToGet != null){
            for (MessageMapAttractiveHowToGet view : listMessageAttractiveHowToGet) {
                view.mapViewOnResume();
            }
        }

        ReadListMessageFromSharedPreferences(); // Leer lista de mensajes.
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(listMessageCardMapView != null){
            for (MessageCardMapListItemView view : listMessageCardMapView) {
                view.mapViewOnSaveInstanceState(outState);
            }
        }

        if(listMessageAttractiveHowToGet != null){
            for (MessageMapAttractiveHowToGet view : listMessageAttractiveHowToGet) {
                view.mapViewOnSaveInstanceState(outState);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(listMessageCardMapView != null){
            for (MessageCardMapListItemView view : listMessageCardMapView) {
                view.mapViewOnPause();
            }
        }

        if(listMessageAttractiveHowToGet != null){
            for (MessageMapAttractiveHowToGet view : listMessageAttractiveHowToGet) {
                view.mapViewOnPause();
            }
        }

        UpdateListMessageFromSharedPreferences(); // Modificamos la lista de mensajes.
    }

    @Override
    public void onStop() {
        super.onStop();
        if(listMessageCardMapView != null){
            for (MessageCardMapListItemView view : listMessageCardMapView) {
                view.mapViewOnStop();
            }
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if(listMessageCardMapView != null){
            for (MessageCardMapListItemView view : listMessageCardMapView) {
                view.mapViewOnLowMemory();
            }
        }

        if(listMessageAttractiveHowToGet != null){
            for (MessageMapAttractiveHowToGet view : listMessageAttractiveHowToGet) {
                view.mapViewOnLowMemory();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(listMessageCardMapView != null){
            for (MessageCardMapListItemView view : listMessageCardMapView) {
                view.mapViewOnDestroy();
            }
        }

        if(listMessageAttractiveHowToGet != null){
            for (MessageMapAttractiveHowToGet view : listMessageAttractiveHowToGet) {
                view.mapViewOnDestroy();
            }
        }
    }
}
