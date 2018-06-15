package com.example.jona.latacungadigital.Activities.Clases;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.jona.latacungadigital.Activities.modelos.TextMessageModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SaveListMessageClass {

    private List<TextMessageModel> listMessagesText;
    private Context context;

    public SaveListMessageClass(List<TextMessageModel> listMessagesText, Context context) {
        this.listMessagesText = listMessagesText;
        this.context = context;
    }

    public SaveListMessageClass(Context context) {
        this.context = context;
    }

    public void SaveListMessage() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editorPreferences = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(listMessagesText);
        editorPreferences.putString("Lista_De_Mensajes", json);
        editorPreferences.apply();
    }

    public List<TextMessageModel> ReadListMessages() {
        List<TextMessageModel> messageModel;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("Lista_De_Mensajes", "");
        if (json.isEmpty()) {
            messageModel = new ArrayList<>();
        } else {
            Type type = new TypeToken<List<TextMessageModel>>() {}.getType();
            messageModel = gson.fromJson(json, type);
        }

        return messageModel;
    }
}
