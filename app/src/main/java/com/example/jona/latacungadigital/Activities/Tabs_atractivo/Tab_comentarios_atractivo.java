package com.example.jona.latacungadigital.Activities.Tabs_atractivo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.jona.latacungadigital.Activities.Adapters.ListaComentarioAdapter;
import com.example.jona.latacungadigital.Activities.Parser.CircleTransform;
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

/**
 * Created by fabia on 13/07/2018.
 */

public class Tab_comentarios_atractivo extends Fragment {

    public LinearLayout layout_comentario, layout_editar_comentario, layout_lista_comentarios;

    public LinearLayout comentarioTexto; // Muestra el layout que contiene el edittext y los botones

    private DatabaseReference mDatabase;
    private FirebaseDatabase mFirebaseInstance;

    private Button btn_guarda, btn_cancelar;
    private ImageButton btn_menu;
    private TextView txt_nombre_usuario, txt_comentario, txt_fecha;
    private ImageView imageView_usuario;
    private EditText edit_comentario;

    private RatingBar mBar,mBar2, mBarTotal;

    private double valor_rating_bar = 0;

    private FirebaseUser user;

    public ArrayList<ComentarioModel> listaComentarios = new ArrayList<>();
    Calendar fecha = new GregorianCalendar();
    private String usuarioKey;
    private String keyAtractivo;

    private RecyclerView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_comentarios_atractivo, container, false);
        layout_comentario = (LinearLayout) view.findViewById(R.id.layou_comentario);
        layout_editar_comentario = (LinearLayout ) view.findViewById(R.id.layout_editar_coemtario);
        layout_lista_comentarios = (LinearLayout) view.findViewById(R.id.layout_lista_comentarios);

        txt_nombre_usuario = (TextView) view.findViewById(R.id.txt_nombre_usuario);
        txt_comentario = (TextView) view.findViewById(R.id.txt_comentario);
        txt_fecha = (TextView) view.findViewById(R.id.txt_fecha);


        imageView_usuario = (ImageView) view.findViewById(R.id.imgUusuario);

        btn_guarda = (Button) view.findViewById(R.id.btn_guardar);
        btn_cancelar = (Button) view.findViewById(R.id.btn_cancelar);
        btn_menu = (ImageButton)  view.findViewById(R.id.btn_submenu);

        edit_comentario = (EditText) view.findViewById(R.id.edit_comentario);


        listView = (RecyclerView) view.findViewById(R.id.listViewComentarios);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));

        comentarioTexto = (LinearLayout) view.findViewById(R.id.LayoutComentario);

        mBar = (RatingBar) view.findViewById(R.id.ratingBar);
        mBar2 = (RatingBar) view.findViewById(R.id.ratingBar2);

        mBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(comentarioTexto.getVisibility() == View.GONE){
                    comentarioTexto.setVisibility(View.VISIBLE);
                }
                setvalor_rating_bar(ratingBar.getRating());

            }
        });


        mBarTotal = (RatingBar) view.findViewById(R.id.ratingBar_total);

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
                PopupMenu popupMenu = new PopupMenu(getContext(),btn_menu);
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

        user = FirebaseAuth.getInstance().getCurrentUser();

        if(keyAtractivo!= null){
            // Implementa funciones para que el usuario pueda comentar el atractivo
            setComentario();
            // Recupera la informacion del commentario que a realizado el usuario
            comentarioUsuario();
            // Recupera todos los comentarios sobre el atractivo
            getComentarios();
            // Cargala image 360 o permite la descargar de la misma
        }

        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes
        return view;
    }

    public double getvalor_rating_bar() {
        return valor_rating_bar;
    }

    public void setvalor_rating_bar(double valor_rating_bar) {
        this.valor_rating_bar = valor_rating_bar;
    }

    public String getKeyAtractivo() {
        return keyAtractivo;
    }

    public void setKeyAtractivo(String keyAtractivo) {
        this.keyAtractivo = keyAtractivo;
    }

    public void setComentario(){

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
        mDatabase.child("comentario").child(keyAtractivo).child(this.usuarioKey).setValue(comentarioModel);//guarda la informacion en firebase

        layout_comentario.setVisibility(View.VISIBLE);
        layout_editar_comentario.setVisibility(View.GONE);

        comentarioUsuario();
        comentarioTexto.setVisibility(View.GONE);
    }
    public void comentarioUsuario(){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("comentario").child(keyAtractivo).child(this.usuarioKey);


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

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("comentario").child(keyAtractivo);
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
                    //mBarTotal.setRating((float) (ratingTotal/contador));

                    if(contador>0){
                        rating = (ratingTotal/contador);
                    }


                    setRatingTotalAtractivo(rating);
                    listView.setAdapter(new ListaComentarioAdapter(getContext(), listaComentarios));

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void setRatingTotalAtractivo(double rating){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("atractivo").child(keyAtractivo);
        mDatabase.child("rating").setValue(rating);

    }

}
