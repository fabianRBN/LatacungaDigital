package com.example.jona.latacungadigital.Activities.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.widget.Toast;

import com.example.jona.latacungadigital.Activities.Tabs_atractivo.Tab_comentarios_atractivo;
import com.example.jona.latacungadigital.Activities.Tabs_atractivo.Tab_detalle_atractivo;
import com.example.jona.latacungadigital.Activities.Tabs_atractivo.Tab_vr_atractivo;

/**
 * Created by fabia on 13/07/2018.
 */

public class TabPagerAdapter  extends FragmentStatePagerAdapter {
    //integramss un contador del numero de tabs
    int tabCount;
    String keyAtractivo;

    public TabPagerAdapter(FragmentManager fm, int tabCount, String keyAtractivo) {
        super(fm);
        this.tabCount= tabCount; // Inicializamos el contador de tabs
        this.keyAtractivo= keyAtractivo;
    }

    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        switch (position) {
            case 0:
                Tab_detalle_atractivo tab_detalle_atractivo= new Tab_detalle_atractivo();
                tab_detalle_atractivo.keyatractivo = this.keyAtractivo;
                return tab_detalle_atractivo;
            case 1:
                Tab_comentarios_atractivo tab_comentarios_atractivo= new Tab_comentarios_atractivo();
                tab_comentarios_atractivo.setKeyAtractivo(this.keyAtractivo);
                return tab_comentarios_atractivo;
            case 2:
                Tab_vr_atractivo tab_vr_atractivo= new Tab_vr_atractivo();
                tab_vr_atractivo.setKeyAtractivo(this.keyAtractivo);
                return tab_vr_atractivo;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
