package com.example.jona.latacungadigital.Activities.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.example.jona.latacungadigital.Activities.References.ChatBotReferences;
import com.example.jona.latacungadigital.R;

public class DialogAppFragment extends DialogFragment {

    /* La actividad que crea una instancia de este fragmento de diálogo debe
      implementar esta interfaz para recibir devoluciones de llamadas de eventos.
      Cada método pasa por el DialogFragment en caso de que el host necesite consultarlo.
   */
    public interface NoticeDialogListener {
        void onDialogConfirmClick(DialogFragment dialog);
        void onDialogCancelClick(DialogFragment dialog);
    }

    // Se usa esta instancia de la interfaz para entregar eventos de acción.
    NoticeDialogListener mListener;

    private static final int DIALOG_SIGN_OFF = 1; // Para saber que tipo de Dialogo es.

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        // Se crea el diálogo y se configura los manejadores de clic del botón.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String titleDialog = "", bodyDialog = ""; // Variables para almacenar el titulo y el mensaje del dialogo.
        int typeDialog = 0; // Variable para controlar que tipo de Dialogo es.
        int drawableIcon = 0; // Variable para establecer el icono del titulo del dialogo.

        if (getArguments() != null) {
            typeDialog = getArguments().getInt("Type_Dialog");
        }

        switch (typeDialog) {
            case DIALOG_SIGN_OFF:
                drawableIcon = R.drawable.ic_exit_to_app;
                titleDialog = "Cerrar Sesión";
                bodyDialog = "¿Desea cerrar la sesión?";
                break;
            case ChatBotReferences.DIALOG_DELETE_MESSAGE:
                drawableIcon = R.drawable.ic_delete_messages;
                titleDialog = "Eliminar Mensajes";
                bodyDialog = "¿Desea eliminar los mensajes?";
                break;
        }

        // Se le añaden el icono, titulo y el mensaje que va a tener el dialogo.
        builder.setIcon(drawableIcon);
        builder.setTitle(titleDialog);
        builder.setMessage(bodyDialog);

        // Se añaden las acciones de los botones.
        builder.setPositiveButton(R.string.dialog_confirm_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Se envia el evento del botón salir a la actividad principal.
                mListener.onDialogConfirmClick(DialogAppFragment.this);
            }
        });

        builder.setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Se enviaa el evento del botón cancelar a la actividad principal.
                mListener.onDialogCancelClick(DialogAppFragment.this);
            }
        });

        return builder.create();
    }

    // Este método invalida el Fragment.onAttach() para crear una instancia del NoticeDialogListener.
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verifica que la actividad del host implemente la interfaz de devolución de llamada.
        try {
            // Crea una instancia del NoticeDialogListener para que podamos enviar eventos al host.
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // La actividad no implementa la interfaz, en este caso se debe arrojar una excepción.
            throw new ClassCastException(activity.toString()
                    + " se debe implementar con NoticeDialogListener.");
        }
    }
}
