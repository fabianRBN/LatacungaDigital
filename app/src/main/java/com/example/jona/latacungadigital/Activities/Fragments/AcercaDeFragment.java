package com.example.jona.latacungadigital.Activities.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.jona.latacungadigital.R;

public class AcercaDeFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private TextView txtContenidoBeneficiosAcercaDe, txtContenidoDependeciasAcercaDe, txtContenidoColaboradoresAcercaDe;
    private ImageButton btnFacebook, btnTwitter, btnYoutube, btnWebPage;

    // Required empty public constructor
    public AcercaDeFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_acerca_de, container, false);

        txtContenidoBeneficiosAcercaDe = view.findViewById(R.id.txtContenidoBeneficiosAcercaDe);
        txtContenidoDependeciasAcercaDe = view.findViewById(R.id.txtContenidoDependeciasAcercaDe);
        txtContenidoColaboradoresAcercaDe = view.findViewById(R.id.txtContenidoColaboradoresAcercaDe);

        btnFacebook = view.findViewById(R.id.btnFacebookColaborador);
        btnTwitter= view.findViewById(R.id.btnTwitterColaborador);
        btnYoutube = view.findViewById(R.id.btnYotubeColaborador);
        btnWebPage = view.findViewById(R.id.btnPaginaWebColaborador);

        setInformationAboutPage();
        openSocialLinks();

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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void setInformationAboutPage() {
        txtContenidoBeneficiosAcercaDe.setText(Html.fromHtml("&#8226" + " Tecnología de realidad aumentada." + "<br>" + "&#8226"
                + " Inteligencia artificial." + "<br>" + "&#8226" + " Información de los Atractivos y servicios." + "<br>" + "&#8226"
                + " Inclusión de mapas." + "<br>" + "&#8226" + " Ubicación del usuario, atractivos y servicios."));

        txtContenidoDependeciasAcercaDe.setText(Html.fromHtml("&#8226" + " Conexión a internet o plan de datos." + "<br>" + "&#8226"
                + " Permitir ubicación del usuario." + "<br>" + "&#8226" + " Permitir acceso a la cámara."));

        txtContenidoColaboradoresAcercaDe.setText(Html.fromHtml("&#8226" + " Dirección de turismo."));
    }

    private Intent socialIntent(String url) {
        Uri uri = Uri.parse(url);
        return new Intent(Intent.ACTION_VIEW, uri);
    }

    private void openSocialLinks() {

        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent facebookLink = socialIntent("https://www.facebook.com/AmaLatacunga/");
                startActivity(facebookLink);
            }
        });

        btnWebPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent webPageLink = socialIntent("http://amalatacunga.com/");
                startActivity(webPageLink);
            }
        });

        btnYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent youtubeLink = socialIntent("https://www.youtube.com/channel/UC9VpemTyg53ko7RFUPye9MA");
                startActivity(youtubeLink);
            }
        });

        btnTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent twitterLink = socialIntent("https://twitter.com/amalatacunga");
                startActivity(twitterLink);
            }
        });
    }
}
