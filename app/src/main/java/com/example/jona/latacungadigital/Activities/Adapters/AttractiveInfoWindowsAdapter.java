package com.example.jona.latacungadigital.Activities.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.example.jona.latacungadigital.Activities.ChatBotActivity;
import com.example.jona.latacungadigital.Activities.Clases.AttractiveClass;
import com.example.jona.latacungadigital.Activities.Fragments.MapaFragment;
import com.example.jona.latacungadigital.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class AttractiveInfoWindowsAdapter implements GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnInfoWindowLongClickListener {

    // Variables de la clase
    private final View mWindow;
    private Context mContext;
    private GoogleMap googleMap;
    private AttractiveClass attractive;
    private MapaFragment mapaFragment;

    public AttractiveInfoWindowsAdapter(Context mContext, GoogleMap googleMap) {
        this.mContext = mContext;
        this.googleMap = googleMap;
        this.mWindow = LayoutInflater.from(mContext).inflate(R.layout.info_window_attractive,null);
    }

    // Metodos get and ser
    public void setMapaFragment(MapaFragment mapaFragment) {
        this.mapaFragment = mapaFragment;
    }

    private void renderWindow(final Marker marker, View view){
        ImageView imgAtractivo = (ImageView) view.findViewById(R.id.img_atractivo);
        TextView txtTitulo = (TextView) view.findViewById(R.id.str_titulo);
        TextView txtCategoria = (TextView) view.findViewById(R.id.str_categoria);
        TextView txtDireccion = (TextView) view.findViewById(R.id.str_direccion);
        RatingBar rbRating = (RatingBar) view.findViewById(R.id.rb_rating);
        TextView txtRating = (TextView) view.findViewById(R.id.txt_rating);

        if(marker.getTag() != null){
            attractive = (AttractiveClass) marker.getTag();
            txtTitulo.setText(attractive.getNameAttractive());
            txtCategoria.setText(attractive.getCategory());
            txtDireccion.setText(attractive.getAddress());
            rbRating.setRating(Float.parseFloat(attractive.getRating()));
            txtRating.setText(attractive.getRating());

            Glide.with(mContext).load(attractive.getListImages().get(0))
                    .asBitmap()
                    .override(100,100)
                    .listener(new RequestListener<String, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, String model, com.bumptech.glide.request.target.Target<Bitmap> target, boolean isFirstResource) {
                            e.printStackTrace();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, String model, com.bumptech.glide.request.target.Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            if(!isFromMemoryCache) marker.showInfoWindow();
                            return false;
                        }

                    })
                    .into(imgAtractivo);
        } else {
            txtTitulo.setText("No se pudo leer el attractivo");
            txtCategoria.setText("");
            txtDireccion.setText("");
            rbRating.setRating(0);
            txtRating.setText("");
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

    @Override
    public void onInfoWindowClick(Marker marker) {
        if(mapaFragment.getActivity() instanceof ChatBotActivity){
            ChatBotActivity chatBotActivity = (ChatBotActivity) mapaFragment.getActivity();
            chatBotActivity.changeFragmentToChatbotAndSendMessage("Buscar el atractivo " + attractive.getAlias());
        }
    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {

    }
}
