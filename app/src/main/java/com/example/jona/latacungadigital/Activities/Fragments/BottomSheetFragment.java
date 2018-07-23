package com.example.jona.latacungadigital.Activities.Fragments;

/**
 * Created by fabia on 16/07/2018.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.jona.latacungadigital.Activities.ARActivity;
import com.example.jona.latacungadigital.R;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    private LinearLayout linearLayoutAtractivo, linearLayoutServios, linearLayoutPanoramica;
    private ProgressDialog dialog;
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

        linearLayoutAtractivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actividadAR("atractivos");
            }
        });

        linearLayoutPanoramica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                actividadAR("panoramica");
            }
        });

        linearLayoutServios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actividadAR("servicios");
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
}