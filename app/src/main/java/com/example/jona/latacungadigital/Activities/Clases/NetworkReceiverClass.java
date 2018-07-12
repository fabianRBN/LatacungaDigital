package com.example.jona.latacungadigital.Activities.Clases;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
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
    Context context;  // Guarda el contexto del fragmento o actividad en la que se muestra el snackbar
    LocationManager manager;
    private boolean showMessageToStartActivity, statusTSnackbar = false;
    private DialogflowClass dialogflowClass;

    public NetworkReceiverClass(ActionBar actionBar, Activity activityChatBot, boolean showMessageToStartActivity) {
        this.actionBar = actionBar;
        this.activityChatBot = activityChatBot;
        this.showMessageToStartActivity = showMessageToStartActivity;
    }

    // Getters and Setters.
    public boolean isStatusTSnackbar() { return statusTSnackbar; }

    public DialogflowClass getDialogflowClass() { return dialogflowClass; }

    public void setDialogflowClass(DialogflowClass dialogflowClass) { this.dialogflowClass = dialogflowClass; }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        ShowMessagesConection(context);
    }
    // Método de verificacion de GPS Nota: se debe registrar el IntenFilter
    private boolean verificarGPS() {
        manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) // Verifica si el gps esta activo
        {return false; }
        else{ return true; }
    }
    // Método para verificar el estado de conexion y segun eso mostrar un mensaje al usuario.
    private void ShowMessagesConection(Context context) {
        if(actionBar != null) {
            // Para remover la vista de chat is bot is typing si esque existe un error al momento de que el disposito no tenga Internet.
            if (getDialogflowClass().getListMessagesText().size() != 0)
                getDialogflowClass().RemoveMessageTypingToDialogflow();
        }
        // Declaracion de parametros para colocarlos en el SnackBar segun la conexion.
        String messageStatus;
        String colorSnackBarSatats;
        int timeToShowMessage;

        if (AccesoInternet.getInstance(context).isOnline()) { // Si el dispositivo esta conectado al Internet.
            // Si el fragmento que lo llama es listaAtractivos no requiere editar titulo debido al buscador que cuenta en el actionBar
            // Debido a eso se coloco la condicional para el funcionamiento unicamnete en el chatbot
            if(actionBar!=null){ actionBar.setSubtitle("En línea");}
            messageStatus = "Conexión exitosa.";
            colorSnackBarSatats = "FF04C607";
            timeToShowMessage = TSnackbar.LENGTH_SHORT;
            statusTSnackbar = true; // Para saber que esta conectado a internet.
            // Verifica el GPS.
            // Nota: se coloco la condicon del actionBar para el funcionamiento unicamente en Lista de atractivos
            // del requerise en otro fragmento o actividad  eliminar esa condicional y registrar el IntentFilter
            if(!verificarGPS() && actionBar == null){
                messageStatus = "No esta activado el GPS";
                colorSnackBarSatats = "FFD90214";
                timeToShowMessage = TSnackbar.LENGTH_INDEFINITE;
                showMessageToStartActivity = true;
                statusTSnackbar = false; // Para saber que no esta conectado a internet.
            }

            if(actionBar!=null) {
                // El chat bot enviara un mensaje de bienvenida al usuario si esque no hay internet.
                if (dialogflowClass.getListMessagesText().size() == 0)
                    dialogflowClass.SendMessageTextToDialogflow("Hola");
            }
        } else {
            // Si el fragmento que lo llama es listaAtractivos no requiere editar titulo debido al buscador que cuenta en el actionBar
            // Debido a eso se coloco la condicional para el funcionamiento unicamnete en el chatbot
            if(actionBar!=null){ actionBar.setSubtitle("Desconectado");}
            messageStatus = "No hay conexión a Internet.";
            colorSnackBarSatats = "FFD90214";
            timeToShowMessage = TSnackbar.LENGTH_INDEFINITE;
            showMessageToStartActivity = true;
            statusTSnackbar = false; // Para saber que no esta conectado a internet.
        }

        if (showMessageToStartActivity) { // No se va a mostrar el mensaje al inicio de la actividad.
            // Se crea el SnackBar con los parametros de la vista.
            snackbar = TSnackbar.make(activityChatBot.findViewById(android.R.id.content),
                    messageStatus, timeToShowMessage);

            // Para calcular la altura del Action Bar.
            TypedValue typedValue = new TypedValue();
            int actionBarHeight = 0;
            // Si el fragmento que lo llama es listaAtractivos no requiere editar titulo debido al buscador que cuenta en el actionBar
            // Debido a eso se coloco la condicional para el funcionamiento unicamnete en el chatbot
            if(actionBar!=null) {
                if (context.getTheme().resolveAttribute(R.attr.actionBarSize, typedValue, true)) {
                    actionBarHeight = TypedValue.complexToDimensionPixelSize(typedValue.data, context.getResources().getDisplayMetrics());
                }
            }

            // Creamos una varibales para obtener la vista del snackBar.
            View snackbarView = snackbar.getView();
            // Si el fragmento que lo llama es listaAtractivos no requiere editar titulo debido al buscador que cuenta en el actionBar
            // Debido a eso se coloco la condicional para el funcionamiento unicamnete en el chatbot
            if(actionBar!=null) {
                FrameLayout.LayoutParams parameters = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
                parameters.setMargins(0, actionBarHeight, 0, 0); // Le colocamos al SnackBar debajo del Action Bar.
                snackbarView.setLayoutParams(parameters); // Asignamos los parametros de la vista modificada del SnackBar.
            }
            snackbarView.setBackgroundColor(Color.parseColor("#" + colorSnackBarSatats)); // Le asiganmos el color de fondo del SnackBar.
            TextView textView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE); // Damos un color blanco a las letras del Text View.

            snackbar.show(); // Mostramos el mensaje de estado de conexión.
        }
    }
}
