package com.example.jona.latacungadigital.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jona.latacungadigital.Activities.Adapters.ListaComentarioAdapter;
import com.example.jona.latacungadigital.Activities.Adapters.ViewPagerAdapter;
import com.example.jona.latacungadigital.Activities.Fragments.MapaFragment;
import com.example.jona.latacungadigital.Activities.Parser.CircleTransform;
import com.example.jona.latacungadigital.Activities.modelos.AtractivoModel;
import com.example.jona.latacungadigital.Activities.modelos.ComentarioModel;
import com.example.jona.latacungadigital.Activities.modelos.Coordenada;
import com.example.jona.latacungadigital.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class AtractivoActivity extends AppCompatActivity {

    public TextView txtTitulo, txtDescripcion, txtCategoria, txtRatingTotal;
    public ViewPager viewPager;

    public LinearLayout layout_comentario, layout_editar_comentario, layout_lista_comentarios;
    public CardView card_view_360;

    String atractivoKey;
    String usuarioKey;
    String distancia;

    private DatabaseReference mDatabase;
    private FirebaseDatabase mFirebaseInstance;

    private static ArrayList<String> listaImagenes = new ArrayList<String>();

    AppBarLayout Appbar;
    CollapsingToolbarLayout CoolToolbar;
    Toolbar toolbar;

    private Button btn_guarda, btn_cancelar;
    private ImageButton btn_menu;
    private TextView txt_nombre_usuario, txt_comentario, txt_fecha;
    private ImageView imageView_usuario;
    private EditText edit_comentario;
    private RatingBar mBar,mBar2, mBarTotal;

    private double valor_rating_bar = 0;

    Calendar fecha = new GregorianCalendar();

    private RecyclerView listView;
    public ArrayList<ComentarioModel> listaComentarios = new ArrayList<>();


    // Variables para Imagenes 360
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ImageButton descargar360;
    private ProgressDialog mProgressDialog; // Para controlar el proseso de la descarga

    private VrPanoramaView vr_pan_view;
    private String NameOfFolder = "LatacungaDigitalImagenes"; // Directorio en donde se guardan las imagenes 360
    private String NameOfFile = "vr"; // Nombre de la imagen que se guardara en storage
    // Url de la imagen 360  desde firebase
    private String imageHttpAddress = "https://firebasestorage.googleapis.com/v0/b/turismoar-72e69.appspot.com/o/imagenes%2Fatractivos%2F-LD9cbfJWj7MleOqIau7-0661?alt=media&token=2b7905da-82c5-40e5-af45-f951fef0468a";

    private File file; // Variable para  manipular la descarga y lectura de la imagen


    public double getvalor_rating_bar() {
        return valor_rating_bar;
    }

    public void setvalor_rating_bar(double valor_rating_bar) {
        this.valor_rating_bar = valor_rating_bar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_atractivo);

        Appbar = (AppBarLayout)findViewById(R.id.appbar);
        viewPager = (ViewPager) findViewById(R.id.viewPager);


        CoolToolbar = (CollapsingToolbarLayout)findViewById(R.id.ctolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        layout_comentario = (LinearLayout) findViewById(R.id.layou_comentario);
        layout_editar_comentario = (LinearLayout ) findViewById(R.id.layout_editar_coemtario);
        card_view_360 = (CardView) findViewById(R.id.card_view_360);


        txt_nombre_usuario = (TextView) findViewById(R.id.txt_nombre_usuario);
        txt_comentario = (TextView) findViewById(R.id.txt_comentario);
        txt_fecha = (TextView) findViewById(R.id.txt_fecha);
        txtRatingTotal = (TextView) findViewById(R.id.txtRatingToatal);

        imageView_usuario = (ImageView) findViewById(R.id.imgUusuario);

        btn_guarda = (Button) findViewById(R.id.btn_guardar);
        btn_cancelar = (Button) findViewById(R.id.btn_cancelar);
        btn_menu = (ImageButton)  findViewById(R.id.btn_submenu);

        edit_comentario = (EditText) findViewById(R.id.edit_comentario);


        mBar = (RatingBar) findViewById(R.id.ratingBar);
        mBar2 = (RatingBar) findViewById(R.id.ratingBar2);

        mBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                setvalor_rating_bar(ratingBar.getRating());
            }
        });

        mBarTotal = (RatingBar) findViewById(R.id.ratingBar_total);

        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_comentario.setVisibility(View.VISIBLE);
                layout_editar_comentario.setVisibility(View.GONE);
            }
        });
        // Menu para editar
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(AtractivoActivity.this,btn_menu);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu_atractivo,popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.option_editar_comentario){
                            layout_comentario.setVisibility(View.GONE);
                            layout_editar_comentario.setVisibility(View.VISIBLE);
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        
        layout_lista_comentarios = (LinearLayout) findViewById(R.id.layout_lista_comentarios);
        listView = (RecyclerView) findViewById(R.id.listViewComentarios);
        listView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        
        txtTitulo = (TextView) findViewById(R.id.txtTituloAtractivo);
        txtCategoria= (TextView) findViewById(R.id.txtCategoriaAtractivo);
        txtDescripcion = (TextView) findViewById(R.id.txtDescripcionAtractivo);
        vr_pan_view = (VrPanoramaView) findViewById(R.id.vr_pan_view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            txtDescripcion.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        }

        Bundle parametros = this.getIntent().getExtras();
        if(parametros !=null){

            this.atractivoKey = getIntent().getExtras().getString("atractivoKey");
            this.distancia = getIntent().getExtras().getString("distancia");
            NameOfFile = this.atractivoKey.toString();
            mDatabase = FirebaseDatabase.getInstance().getReference().child("atractivo").child(this.atractivoKey);
            // Recupera toda la informacion del atractivo
            getAtractivo();
            // Implementa funciones para que el usuario pueda comentar el atractivo
            setComentario();
            // Recupera la informacion del commentario que a realizado el usuario
            comentarioUsuario();
            // Recupera todos los comentarios sobre el atractivo
            getComentarios();
            // Cargala image 360 o permite la descargar de la misma
            imagenes360();


        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (vr_pan_view != null) {
            vr_pan_view.pauseRendering();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (vr_pan_view != null) {
            vr_pan_view.shutdown();
        }
    }



    public void setComentario(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            txt_nombre_usuario.setText(user.getDisplayName());
            Picasso.get().load(user.getPhotoUrl()).transform(new CircleTransform()).into(imageView_usuario);

            this.usuarioKey = user.getUid();

            btn_guarda.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    guardarComentario();
                }
            });


        }
    }

    public void  getAtractivo(){
        listaImagenes.clear();// Limpia el arreglo de imagenes

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        final int width = metrics.widthPixels; // ancho absoluto en pixels
        final int height = metrics.heightPixels; // alto absoluto en pixels


        mDatabase.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //atractivoModel = dataSnapshot.getValue(AtractivoModel.class)
                listaImagenes.clear();
               txtTitulo.setText(dataSnapshot.child("nombre").getValue().toString());
               txtDescripcion.setText(dataSnapshot.child("descripcion").getValue().toString());
               txtCategoria.setText(dataSnapshot.child("categoria").getValue().toString() +" - "+ distancia);
                // Recupera las imagenes del atractivo
                for(DataSnapshot galeria: dataSnapshot.child("galeria").getChildren()){
                    listaImagenes.add(galeria.child("imagenURL").getValue().toString());
                }
                // Verifica si existen imagenes 360
                if(dataSnapshot.child("imagen360").child("imagenURL").getValue() == null){
                    card_view_360.setVisibility(View.GONE);
                }else{
                    imageHttpAddress = dataSnapshot.child("imagen360").child("imagenURL").getValue().toString();
                }
                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getApplicationContext(), listaImagenes , width,height);
                viewPager.setAdapter(viewPagerAdapter);

                if(dataSnapshot.child("posicion").getValue() != null){
                    final Coordenada coordenada = dataSnapshot.child("posicion").getValue(Coordenada.class);
                    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Uri gmmIntentUri = Uri.parse("google.navigation:q="+coordenada.getLat()+","+coordenada.getLng());
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            startActivity(mapIntent);


                        }
                    });
                }
             }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void guardarComentario(){

        int año = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH);
        int dia = fecha.get(Calendar.DAY_OF_MONTH);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mDatabase = mFirebaseInstance.getReference();
        ComentarioModel comentarioModel = new ComentarioModel(
                this.usuarioKey,
                edit_comentario.getText().toString(),
                getvalor_rating_bar() ,
                dia + "/" + (mes+1) + "/" + año
                );
        mDatabase.child("comentario").child(this.atractivoKey).child(this.usuarioKey).setValue(comentarioModel);//guarda la informacion en firebase

        layout_comentario.setVisibility(View.VISIBLE);
        layout_editar_comentario.setVisibility(View.GONE);

        comentarioUsuario();
    }

    public void comentarioUsuario(){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("comentario").child(this.atractivoKey).child(this.usuarioKey);


        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue() != null){

                    ComentarioModel comentarioModel = dataSnapshot.getValue(ComentarioModel.class);
                    txt_comentario.setText(comentarioModel.getContenido());
                    txt_fecha.setText(comentarioModel.getFecha());
                    mBar2.setRating((float) comentarioModel.getCalificacion());
                    mBar.setRating((float) comentarioModel.getCalificacion());
                    edit_comentario.setText(comentarioModel.getContenido().toString());
                    btn_cancelar.setVisibility(View.VISIBLE);

                }else
                {
                    layout_comentario.setVisibility(View.GONE);
                    layout_editar_comentario.setVisibility(View.VISIBLE);
                    btn_cancelar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getComentarios(){

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("comentario").child(this.atractivoKey);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    listaComentarios.clear();
                    double ratingTotal = 0;
                    double rating = 0;
                    int contador = 0;
                    for(DataSnapshot child: dataSnapshot.getChildren()){
                        contador++;
                        ComentarioModel comentarioModel = child.getValue(ComentarioModel.class);
                        listaComentarios.add(comentarioModel);
                        ratingTotal = ratingTotal + comentarioModel.getCalificacion();

                    }
                    mBarTotal.setRating((float) (ratingTotal/contador));

                    if(contador>0){
                        rating = (ratingTotal/contador);
                    }

                    txtRatingTotal.setText(rating+"");
                    setRatingTotalAtractivo(rating);
                    listView.setAdapter(new ListaComentarioAdapter(getApplicationContext(), listaComentarios));
                    
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void setRatingTotalAtractivo(double rating){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("atractivo").child(this.atractivoKey);
        mDatabase.child("rating").setValue(rating);

    }

    public void imagenes360(){
        String file_path = "/sdcard/" + NameOfFolder; // direccion en donde se guardaran las imagenes
        File dir = new File(file_path); // Referencia del directorio

        if (!dir.exists()) { // Validacion de la existencia del directorio
            dir.mkdirs(); // creaccion del direcctorio
            Toast.makeText(getApplicationContext(),"Directorio de imagenes creado",Toast.LENGTH_LONG).show();
        }
        file = new File(dir, NameOfFile  + ".jpg"); // referencia de a imagen 360

        descargar360 = (ImageButton)findViewById(R.id.btnDescarga);
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
        options.inputType = VrPanoramaView.Options.TYPE_STEREO_OVER_UNDER;
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
        new DownloadFileAsync().execute(url,NameOfFile,NameOfFolder);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DOWNLOAD_PROGRESS:
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("Downloading file..");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
                return mProgressDialog;
            default:
                return null;
        }
    }

    class DownloadFileAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(DIALOG_DOWNLOAD_PROGRESS);
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
                descargar360.setVisibility(View.GONE);
            } catch (Exception e) {}
            return null;

        }
        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC",progress[0]);
            mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String unused) {
            dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
        }
    }


}
