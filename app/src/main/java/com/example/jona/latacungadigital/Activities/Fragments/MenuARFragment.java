package com.example.jona.latacungadigital.Activities.Fragments;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jona.latacungadigital.Activities.ARActivity;
import com.example.jona.latacungadigital.R;
import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;
import com.hitomi.cmlibrary.OnMenuStatusChangeListener;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuARFragment extends Fragment {

    CircleMenu circleMenu;
    TextView txtAtrativo, txtServicios, txtPanoramica;

    public MenuARFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu_ar, container, false);

        txtAtrativo = (TextView) view.findViewById(R.id.txt_atractivos);
        txtServicios= (TextView) view.findViewById(R.id.txt_servicios);
        txtPanoramica = (TextView) view.findViewById(R.id.txt_panoramica);
        circleMenu = (CircleMenu) view.findViewById(R.id.circle_menu);



        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        circleMenu.setMainMenu(Color.parseColor("#CDCDCD"), R.mipmap.icon_menu, R.mipmap.icon_cancel);
        circleMenu.addSubMenu(Color.parseColor("#FF4B32"), R.drawable.ic_photo_size_select_actual_black_24dp)
                .addSubMenu(Color.parseColor("#8A39FF"), R.drawable.ic_free_breakfast_black_24dp)
                .addSubMenu(Color.parseColor("#FF6A00"), R.drawable.ic_location_city_black_24dp);



        circleMenu.setOnMenuSelectedListener(new OnMenuSelectedListener() {

                                                 @Override
                                                 public void onMenuSelected(int index) {
                                                     switch (index) {
                                                         case 0:
                                                             actividadAR("panoramica");
                                                             break;
                                                         case 1:
                                                             actividadAR("servicios");
                                                             break;
                                                         case 2:
                                                             actividadAR("atractivos");
                                                             break;

                                                     }
                                                 }
                                             }

        );

        circleMenu.setOnMenuStatusChangeListener(new OnMenuStatusChangeListener() {

                                                     @Override
                                                     public void onMenuOpened() {
                                                         //Toast.makeText(getContext(), "Menu Opend", Toast.LENGTH_SHORT).show();
                                                         txtAtrativo.setVisibility(View.VISIBLE);
                                                         txtAtrativo.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
                                                         txtPanoramica.setVisibility(View.VISIBLE);
                                                         txtPanoramica.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
                                                         txtServicios.setVisibility(View.VISIBLE);
                                                         txtServicios.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
                                                     }

                                                     @Override
                                                     public void onMenuClosed() {
                                                         //Toast.makeText(getContext(), "Menu Closed", Toast.LENGTH_SHORT).show();
                                                         txtAtrativo.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));
                                                         txtAtrativo.setVisibility(View.GONE);
                                                         txtPanoramica.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));
                                                         txtPanoramica.setVisibility(View.GONE);
                                                         txtServicios.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));
                                                         txtServicios.setVisibility(View.GONE);
                                                     }
                                                 }
        );
    }

    public void onBackPressed() {
        if (circleMenu.isOpened()) {
            circleMenu.closeMenu();
        }else{

        }

    }
    public void actividadAR(final String tipo){
        new Handler().postDelayed(new Runnable(){
            public void run(){
                // Cuando pasen los 3 segundos, pasamos a la actividad principal de la aplicación
                Intent intent = new Intent(getContext(), ARActivity.class);
                intent.putExtra("tipoAR", tipo);

                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.animation_fade_in,R.anim.animation_fade_out);


            };
        }, 1010);

    }



}
