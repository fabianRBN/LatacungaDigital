package com.example.jona.latacungadigital.Activities.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.jona.latacungadigital.Activities.ChatBotActivity;
import com.example.jona.latacungadigital.Activities.Clases.ServiceClass;
import com.example.jona.latacungadigital.Activities.Fragments.MapaFragment;
import com.example.jona.latacungadigital.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class ServiceInfoWindowsAdapter implements GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnInfoWindowLongClickListener {

    // Variables de la clase
    private final View mWindow;
    private Context mContext;
    private GoogleMap googleMap;
    private ServiceClass service;
    private MapaFragment mapaFragment;

    // Constructor
    public ServiceInfoWindowsAdapter(Context mContext, GoogleMap googleMap) {
        this.mContext = mContext;
        this.googleMap = googleMap;
        this.mWindow = LayoutInflater.from(mContext).inflate(R.layout.info_window_service,null);
    }

    // Metodos get and ser
    public void setMapaFragment(MapaFragment mapaFragment) {
        this.mapaFragment = mapaFragment;
    }

    private void renderWindow(Marker marker, View view){
        TextView txtTitulo = (TextView) view.findViewById(R.id.str_titulo);
        TextView txtCategoria = (TextView) view.findViewById(R.id.str_categoria);
        TextView txtDireccion = (TextView) view.findViewById(R.id.str_direccion);
        TextView txtContacto = (TextView) view.findViewById(R.id.str_contacto);

        if(marker.getTag() != null){
            service = (ServiceClass) marker.getTag();
            txtTitulo.setText(service.getName());
            txtCategoria.setText(service.getTypeOfActivity());
            txtDireccion.setText(service.getAddress());
            txtContacto.setText(service.getContact());
        } else {
            txtTitulo.setText("No se pudo leer el servicio");
            txtCategoria.setText("");
            txtDireccion.setText("");
            txtContacto.setText("");
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindow(marker,mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindow(marker,mWindow);
        return mWindow;
    }

    // Metodo llamado al dar click en la ventana de informacion
    @Override
    public void onInfoWindowClick(Marker marker) {
        if(mapaFragment.getActivity() instanceof ChatBotActivity){
            ChatBotActivity chatBotActivity = (ChatBotActivity) mapaFragment.getActivity();
            chatBotActivity.changeFragmentToChatbotAndSendMessage("Buscar hotel " + service.getAlias());
        }
    }

    // Metodo llamado al mantener el click en la ventana de informacion
    @Override
    public void onInfoWindowLongClick(Marker marker) {

    }
}
