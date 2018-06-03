package com.example.jona.latacungadigital.Activities;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jona.latacungadigital.Activities.Adapters.ViewPagerAdapter;
import com.example.jona.latacungadigital.Activities.modelos.AtractivoModel;
import com.example.jona.latacungadigital.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AtractivoActivity extends AppCompatActivity {

    public TextView txtTitulo, txtDescripcion, txtCategoria;
    public ImageView imgAtractivo;
    public ViewPager viewPager;

    String atractivoKey;
    private DatabaseReference mDatabase;
    public AtractivoModel atractivoModel;
    private static ArrayList<String> listaImagenes = new ArrayList<String>();

    AppBarLayout Appbar;
    CollapsingToolbarLayout CoolToolbar;
    Toolbar toolbar;

    boolean ExpandedActionBar = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_atractivo);

        Appbar = (AppBarLayout)findViewById(R.id.appbar);
        CoolToolbar = (CollapsingToolbarLayout)findViewById(R.id.ctolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtTitulo = (TextView) findViewById(R.id.txtTituloAtractivo);
        txtCategoria= (TextView) findViewById(R.id.txtCategoriaAtractivo);
        txtDescripcion = (TextView) findViewById(R.id.txtDescripcionAtractivo);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            txtDescripcion.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        }

        viewPager = (ViewPager) findViewById(R.id.viewPager);





        Bundle parametros = this.getIntent().getExtras();
        if(parametros !=null){

            this.atractivoKey = getIntent().getExtras().getString("atractivoKey");
            mDatabase = FirebaseDatabase.getInstance().getReference().child("atractivo").child(this.atractivoKey);
            getAtractivo();


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

}
