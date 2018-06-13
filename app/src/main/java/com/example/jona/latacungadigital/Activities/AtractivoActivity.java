package com.example.jona.latacungadigital.Activities;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.DisplayMetrics;
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

import com.example.jona.latacungadigital.Activities.Adapters.ListaComentarioAdapter;
import com.example.jona.latacungadigital.Activities.Adapters.ViewPagerAdapter;
import com.example.jona.latacungadigital.Activities.Parser.CircleTransform;
import com.example.jona.latacungadigital.Activities.modelos.AtractivoModel;
import com.example.jona.latacungadigital.Activities.modelos.ComentarioModel;
import com.example.jona.latacungadigital.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class AtractivoActivity extends AppCompatActivity {

    public TextView txtTitulo, txtDescripcion, txtCategoria;
    public ImageView imgAtractivo;
    public ViewPager viewPager;

    public LinearLayout layout_comentario, layout_editar_comentario, layout_lista_comentarios;

    String atractivoKey;
    String usuarioKey;

    private DatabaseReference mDatabase;
    private FirebaseDatabase mFirebaseInstance;


    public AtractivoModel atractivoModel;
    private static ArrayList<String> listaImagenes = new ArrayList<String>();

    AppBarLayout Appbar;
    CollapsingToolbarLayout CoolToolbar;
    Toolbar toolbar;

    private Button btn_guarda, btn_cancelar;
    private ImageButton btn_menu;
    private TextView txt_nombre_usuario, txt_comentario, txt_fecha;
    private ImageView imageView_usuario;
    private EditText edit_comentario;

    private boolean atractivoComentado = false;

    private RatingBar mBar,mBar2;

    private double valor_ratinf_bar = 0;

    Calendar fecha = new GregorianCalendar();

    private RecyclerView listView;
    public ArrayList<ComentarioModel> listaComentarios = new ArrayList<>();


    public double getValor_ratinf_bar() {
        return valor_ratinf_bar;
    }

    public void setValor_ratinf_bar(double valor_ratinf_bar) {
        this.valor_ratinf_bar = valor_ratinf_bar;
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


        txt_nombre_usuario = (TextView) findViewById(R.id.txt_nombre_usuario);
        txt_comentario = (TextView) findViewById(R.id.txt_comentario);
        txt_fecha = (TextView) findViewById(R.id.txt_fecha);

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

                setValor_ratinf_bar(ratingBar.getRating());

            }
        });

        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_comentario.setVisibility(View.VISIBLE);
                layout_editar_comentario.setVisibility(View.GONE);
            }
        });


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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            txtDescripcion.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        }







        Bundle parametros = this.getIntent().getExtras();
        if(parametros !=null){

            this.atractivoKey = getIntent().getExtras().getString("atractivoKey");
            mDatabase = FirebaseDatabase.getInstance().getReference().child("atractivo").child(this.atractivoKey);
            getAtractivo();


        }

        setComentario();
        comentarioUsuario();
        getComentarios();  




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
        listaImagenes.clear();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        final int width = metrics.widthPixels; // ancho absoluto en pixels
        final int height = metrics.heightPixels; // alto absoluto en pixels


        mDatabase.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //atractivoModel = dataSnapshot.getValue(AtractivoModel.class);
               txtTitulo.setText(dataSnapshot.child("nombre").getValue().toString());
               txtDescripcion.setText(dataSnapshot.child("descripcion").getValue().toString());

               txtCategoria.setText(dataSnapshot.child("categoria").getValue().toString());

                for(DataSnapshot galeria: dataSnapshot.child("galeria").getChildren()){
                    listaImagenes.add(galeria.child("imagenURL").getValue().toString());
                }
                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getApplicationContext(), listaImagenes , width,height);
                viewPager.setAdapter(viewPagerAdapter);




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
                getValor_ratinf_bar() ,
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

                    for(DataSnapshot child: dataSnapshot.getChildren()){
                        ComentarioModel comentarioModel = child.getValue(ComentarioModel.class);
                        listaComentarios.add(comentarioModel);


                    }



                    listView.setAdapter(new ListaComentarioAdapter(getApplicationContext(), listaComentarios));




                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
