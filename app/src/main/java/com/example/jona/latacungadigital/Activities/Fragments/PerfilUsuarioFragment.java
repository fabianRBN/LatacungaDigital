package com.example.jona.latacungadigital.Activities.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jona.latacungadigital.Activities.MainActivity;
import com.example.jona.latacungadigital.Activities.Parser.CircleTransform;
import com.example.jona.latacungadigital.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;


public class PerfilUsuarioFragment extends Fragment {

    private FirebaseUser user;

    private TextView txt_nombre, txt_correo, txt_numero_visitas, txt_numero_comentarios;

    private Button btn_salir;

    private ImageView imagen_usuario;

    private DialogAppFragment dialogAppFragment; // Varibale para controlar el Dialogo de cerrar sesion.

    private static final int DIALOG_SIGN_OFF = 1; // Para saber que tipo de Dialogo es.

    public PerfilUsuarioFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        user = FirebaseAuth.getInstance().getCurrentUser();
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_perfil_usuario, container, false);
        txt_correo = view.findViewById(R.id.txt_correo);
        txt_nombre = view.findViewById(R.id.txt_nombre);
        txt_numero_comentarios = view.findViewById(R.id.txt_numero_comentarios);
        txt_numero_visitas = view.findViewById(R.id.txt_numero_visitas);
        imagen_usuario = view.findViewById(R.id.img_foto_usuario);
        btn_salir = view.findViewById(R.id.btn_salir);
        if(user != null){
            txt_correo.setText(user.getEmail());
            txt_nombre.setText(user.getDisplayName());
            Picasso.get().load(user.getPhotoUrl()).transform(new CircleTransform()).into(imagen_usuario);
            System.out.println("Url imagen:"+user.getPhotoUrl());
            btn_salir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


}
