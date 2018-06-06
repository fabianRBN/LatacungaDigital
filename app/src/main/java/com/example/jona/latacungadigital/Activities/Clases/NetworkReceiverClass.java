package com.example.jona.latacungadigital.Activities.Clases;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
import com.example.jona.latacungadigital.Activities.Permisos.AccesoInternet;
import com.example.jona.latacungadigital.R;

public class NetworkReceiverClass extends BroadcastReceiver {

    ActionBar actionBar;
    TSnackbar snackbar;
    Activity activityChatBot;
    private boolean showMessageToStartActivity;

    public NetworkReceiverClass(ActionBar actionBar, Activity activityChatBot, boolean showMessageToStartActivity) {
        this.actionBar = actionBar;
        this.activityChatBot = activityChatBot;
        this.showMessageToStartActivity = showMessageToStartActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ShowMessagesConection(context);
    }

    // Método para verificar el estado de conexion y segun eso mostrar un mensaje al usuario.
    private void ShowMessagesConection(Context context) {
        // Declaracion de parametros para colocarlos en el SnackBar segun la conexion.
        String messageStatus;
        String colorSnackBarSatats;
        int timeToShowMessage;

        // Para saber si esta conectado a internet.
        AccesoInternet accesoInternet = new AccesoInternet();

        if (accesoInternet.isNetDisponible(context)) { // Si esl dispositivo esta conectado al Internet.
            actionBar.setSubtitle("Activo(a) ahora");
            messageStatus = "Conexión exitosa.";
            colorSnackBarSatats = "FF04C607";
            timeToShowMessage = TSnackbar.LENGTH_SHORT;
        } else {
            actionBar.setSubtitle("Desconectado");
            messageStatus = "No hay conexión a Internet.";
            colorSnackBarSatats = "FFD90214";
            timeToShowMessage = TSnackbar.LENGTH_INDEFINITE;
            showMessageToStartActivity = true;
        }

        if (showMessageToStartActivity) { // No se va a mostrar el mensaje al inicio de la actividad.
            // Se crea el SnackBar con los parametros de la vista.
            snackbar = TSnackbar.make(activityChatBot.findViewById(android.R.id.content),
                    messageStatus, timeToShowMessage);

            // Para calcular la altura del Action Bar.
            TypedValue typedValue = new TypedValue();
            int actionBarHeight = 0;

            if (context.getTheme().resolveAttribute(R.attr.actionBarSize, typedValue, true)) {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(typedValue.data, context.getResources().getDisplayMetrics());
            }

            // Creamos una varibales para obtener la vista del snackBar.
            View snackbarView = snackbar.getView();
            FrameLayout.LayoutParams parameters = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
            parameters.setMargins(0, actionBarHeight,0,0); // Le colocamos al SnackBar debajo del Action Bar.
            snackbarView.setLayoutParams(parameters); // Asignamos los parametros de la vista modificada del SnackBar.

            snackbarView.setBackgroundColor(Color.parseColor("#" + colorSnackBarSatats)); // Le asiganmos el color de fondo del SnackBar.
            TextView textView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE); // Damos un color blanco a las letras del Text View.

            snackbar.show(); // Mostramos el mensaje de estado de conexión.
        }
    }
}
