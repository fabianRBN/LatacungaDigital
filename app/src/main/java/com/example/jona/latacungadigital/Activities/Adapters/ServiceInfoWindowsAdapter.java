package com.example.jona.latacungadigital.Activities.Adapters;

import android.content.Context;
import android.graphics.Color;
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
        this.mContext = mContext.getApplicationContext();
        this.googleMap = googleMap;
        this.mWindow = LayoutInflater.from(mContext).inflate(R.layout.info_window_service,null);
    }

    // Metodos get and ser
    public void setMapaFragment(MapaFragment mapaFragment) {
        this.mapaFragment = mapaFragment;
    }

    private void renderWindow(Marker marker, View view){
        TextView txtTitulo = (TextView) view.findViewById(R.id.str_titulo);
        TextView txtSubTipoDeActividad = (TextView) view.findViewById(R.id.str_subTipoActividad);
        TextView txtDireccion = (TextView) view.findViewById(R.id.str_direccion);
        TextView txtCategoria = (TextView) view.findViewById(R.id.str_categoria);
        TextView txtAbierto = (TextView) view.findViewById(R.id.str_abierto);

        if(marker.getTag() != null){
            service = (ServiceClass) marker.getTag();
            txtTitulo.setText(service.getName());
            txtSubTipoDeActividad.setText(service.getSubTypeOfActivity());
            String dirr = mContext.getString(R.string.adress) + ": " + service.getAddress();
            String cat = mContext.getString(R.string.category) + ": " + service.getCategory();
            txtDireccion.setText(dirr);
            txtCategoria.setText(cat);
            if (service.getHorario().isHorarioDefinido()) {
                if(service.isOpen()){
                    txtAbierto.setText("Abierto");
                    txtAbierto.setTextColor(Color.GREEN);
                } else {
                    txtAbierto.setText("Cerrado");
                    txtAbierto.setTextColor(Color.RED);
                }
            }else{
                txtAbierto.setText("Horario no definido");
                txtAbierto.setTextColor(Color.DKGRAY);
            }
        } else {
            txtTitulo.setText("No se pudo leer el servicio");
            txtSubTipoDeActividad.setText("");
            txtDireccion.setText("");
            txtCategoria.setText("");
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        if(marker.getTag().equals("userMarker")){
            return null;
        }
        renderWindow(marker,mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        if(marker.getTag().equals("userMarker")){
            return null;
        }
        renderWindow(marker,mWindow);
        return mWindow;
    }

    // Metodo llamado al dar click en la ventana de informacion
    @Override
    public void onInfoWindowClick(Marker marker) {
        if(mapaFragment.getActivity() instanceof ChatBotActivity){
            ChatBotActivity chatBotActivity = (ChatBotActivity) mapaFragment.getActivity();
            chatBotActivity.changeFragmentToChatbotAndSendMessage("Buscar " + service.getSubTypeOfActivity() + " " + service.getAlias());
        }
    }

    // Metodo llamado al mantener el click en la ventana de informacion
    @Override
    public void onInfoWindowLongClick(Marker marker) {

    }
}
