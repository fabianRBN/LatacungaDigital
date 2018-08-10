package com.example.jona.latacungadigital.Activities.Fragments;

/**
 * Created by fabia on 16/07/2018.
 */


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.jona.latacungadigital.Activities.ARActivity;
import com.example.jona.latacungadigital.R;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    private LinearLayout linearLayoutAtractivo, linearLayoutServios, linearLayoutPanoramica;
    private ProgressDialog dialog;
    String contenido= "";

    public BottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_dialog, container, false);



        linearLayoutAtractivo = (LinearLayout) view.findViewById(R.id.linerLayoutAtractivos);
        linearLayoutServios = (LinearLayout) view.findViewById(R.id.linerLayoutServicios);
        linearLayoutPanoramica = (LinearLayout) view.findViewById(R.id.linerLayoutPanoramica);

        PackageManager packageManager = getContext().getPackageManager();

         final boolean acelrometro = packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);
         final boolean compass = packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS);
        if(!acelrometro){
            contenido = "\n     - Acelerometro ";
        }
        if(!compass){
            contenido = contenido +  "\n    - Compas ";
        }

        linearLayoutAtractivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actividadAR("atractivos");
            }
        });

        linearLayoutPanoramica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(acelrometro && compass){
                    actividadAR("panoramica");
                }else{
                    alert(contenido);
                }

            }
        });

        linearLayoutServios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(acelrometro && compass) {
                    actividadAR("servicios");
                }else{
                    alert(contenido);
                }
            }
        });

        dialog = new ProgressDialog(getActivity());
        return view;
    }
    public void actividadAR(final String tipo){
        dialog.setMessage("Cargando realidad aumentada");
        dialog.show();
        new Handler().postDelayed(new Runnable(){
            public void run(){
                // Cuando pasen los 3 segundos, pasamos a la actividad principal de la aplicación
                Intent intent = new Intent(getContext(), ARActivity.class);
                intent.putExtra("tipoAR", tipo);

                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.animation_fade_in,R.anim.animation_fade_out);


            };
        }, 1010);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        },2020);
    }

    public void alert(String contenido){
        String mensaje = "El dispositivo no cuenta con:" + contenido;
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(getContext());
        }
        builder.setTitle("Alerta")
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }




}