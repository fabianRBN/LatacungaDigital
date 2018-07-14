package com.example.jona.latacungadigital.Activities.Tabs_atractivo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jona.latacungadigital.Activities.AtractivoActivity;
import com.example.jona.latacungadigital.R;
import com.firebase.geofire.GeoQuery;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by fabia on 13/07/2018.
 */

public class Tab_vr_atractivo extends Fragment {

    public CardView card_view_360;
    private DatabaseReference mDatabase;
    private FirebaseDatabase mFirebaseInstance;

    private ProgressBar progressBar;
    // Variables para Imagenes 360
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ImageButton descargar360;
    //private ProgressDialog mProgressDialog; // Para controlar el proseso de la descarga

    private VrPanoramaView vr_pan_view;
    private String NameOfFolder = "LatacungaDigitalImagenes"; // Directorio en donde se guardan las imagenes 360
    private String NameOfFile = "vr"; // Nombre de la imagen que se guardara en storage
    // Url de la imagen 360  desde firebase
    private String imageHttpAddress ;

    private File file; // Variable para  manipular la descarga y lectura de la imagen

    private String keyAtractivo;


    private TextView txtPorsentaje;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_vr_atractivo, container, false);
        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes
        card_view_360 = (CardView) view.findViewById(R.id.card_view_360);
        vr_pan_view = (VrPanoramaView) view.findViewById(R.id.vr_pan_view);
        descargar360 = (ImageButton) view.findViewById(R.id.btnDescarga);
        progressBar = (ProgressBar) view.findViewById(R.id.progressCircle);
        txtPorsentaje = (TextView) view.findViewById(R.id.txtPorsentaje);
        NameOfFile = getKeyAtractivo();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("atractivo").child(keyAtractivo);


        if(getKeyAtractivo()!= null){
            getAtractivo();

                imagenes360();



        }

        return view;
    }

    public String getKeyAtractivo() {
        return keyAtractivo;
    }

    public void setKeyAtractivo(String keyAtractivo) {
        this.keyAtractivo = keyAtractivo;
    }

    public void  getAtractivo(){

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                // Verifica si existen imagenes 360
                if(dataSnapshot.child("imagen360").child("imagenURL").getValue() == null){
                    card_view_360.setVisibility(View.GONE);
                }else{
                    imageHttpAddress = dataSnapshot.child("imagen360").child("imagenURL").getValue().toString();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void imagenes360(){
        String file_path = "/sdcard/" + NameOfFolder; // direccion en donde se guardaran las imagenes
        File dir = new File(file_path); // Referencia del directorio

        if (!dir.exists()) { // Validacion de la existencia del directorio
            dir.mkdirs(); // creaccion del direcctorio
            Toast.makeText(getContext(),"Directorio de imagenes creado",Toast.LENGTH_LONG).show();
        }
        file = new File(dir, NameOfFile  + ".jpg"); // referencia de a imagen 360

        descargar360.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                startDownload();
            }
        });

        if(file.exists()){ // verificacion si  la imagen 360 del atractivo ya esta descargada
            //Toast.makeText(getApplicationContext(),"Ya existe 1",Toast.LENGTH_LONG).show();
            descargar360.setVisibility(View.GONE);
            load360Image();

        }else{
            // vr_pan_view.setVisibility(View.GONE);
        }






    }

    private void load360Image() {


        InputStream open = null;
        FileInputStream open2 = null;
        try {
            open = new FileInputStream(file);

        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(open);
        //Bitmap bitmap= getBitmapFromURL("");
        final VrPanoramaView.Options options = new VrPanoramaView.Options();
        options.inputType = VrPanoramaView.Options.TYPE_MONO;
        vr_pan_view.setEventListener(new VrPanoramaEventListener() {
            @Override
            public void onDisplayModeChanged(int newDisplayMode) {
                super.onDisplayModeChanged(newDisplayMode);
            }

            @Override
            public void onLoadError(String errorMessage) {
                super.onLoadError(errorMessage);
            }
            @Override
            public void onLoadSuccess() {
                super.onLoadSuccess();
            }
            @Override
            public void onClick() {
                super.onClick();
            }
        });


      /*  vr_pan_view.setVisibility(View.VISIBLE);
        descargar360.setVisibility(View.GONE);
        card_view_360.getLayoutParams().height = 300;*/

        vr_pan_view.loadImageFromBitmap(bitmap, options);




    }

    private void startDownload() {
        String url = imageHttpAddress;
        Toast.makeText(getContext(),"Descargando ....!!"+NameOfFile+" ..  "+NameOfFolder,Toast.LENGTH_LONG).show();
        progressBar.setVisibility(View.VISIBLE);
        txtPorsentaje.setVisibility(View.VISIBLE);
        descargar360.setVisibility(View.GONE);
        new DownloadFileAsync().execute(url,NameOfFile,NameOfFolder);
    }
/*
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DOWNLOAD_PROGRESS:
                mProgressDialog = new ProgressDialog(getContext());
                mProgressDialog.setMessage("Downloading file..");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
                return mProgressDialog;
            default:
                return null;
        }
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (vr_pan_view != null) {
            vr_pan_view.shutdown();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (vr_pan_view != null) {
            vr_pan_view.pauseRendering();
        }
    }

    class DownloadFileAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //showDialog(DIALOG_DOWNLOAD_PROGRESS);
        }

        @Override
        protected String doInBackground(String... aurl) {
            int count;

            try {
                URL url = new URL(aurl[0]);
                String nameFile = aurl[1];
                String nameFolder = aurl[2];
                URLConnection conexion = url.openConnection();
                conexion.connect();

                int lenghtOfFile = conexion.getContentLength();
                Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream("/sdcard/" + nameFolder+"/"+nameFile+".jpg");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress(""+(int)((total*100)/lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
                load360Image();

            } catch (Exception e) {
                descargar360.setVisibility(View.VISIBLE);
            }
            return null;

        }
        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC",progress[0]);
            txtPorsentaje.setText(Integer.parseInt(progress[0]) +"%");
            progressBar.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String unused) {
            progressBar.setVisibility(View.GONE);
            txtPorsentaje.setVisibility(View.GONE);
        }
    }


}
