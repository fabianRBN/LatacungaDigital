package com.example.jona.latacungadigital.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;

import android.view.View;
import android.widget.Toast;

import com.example.jona.latacungadigital.Activities.Adapters.TabPagerAdapter;
import com.example.jona.latacungadigital.Activities.Adapters.ViewPagerAdapter;

import com.example.jona.latacungadigital.Activities.modelos.Coordenada;
import com.example.jona.latacungadigital.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


import me.relex.circleindicator.CircleIndicator;


public class AtractivoActivity extends AppCompatActivity  implements TabLayout.OnTabSelectedListener{

    public ViewPager viewPager;
    public CircleIndicator circleIndicator; // Indicador de viewpager imagenes

    ViewPagerAdapter viewPagerAdapter ;


    private TabLayout tabLayout; // Este es nuestro tabLayout

    private ViewPager viewPagerTab; // en este viepager se muestra el contenido de los tabs



    String atractivoKey;

    String nombreAtractivo="";
    String distancia;

    private DatabaseReference mDatabase;
    private FirebaseDatabase mFirebaseInstance;

    private static ArrayList<String> listaImagenes = new ArrayList<>();

    private boolean imagen360= false;

    AppBarLayout Appbar;
    CollapsingToolbarLayout CoolToolbar;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_atractivo);

        Appbar = (AppBarLayout)findViewById(R.id.appbar);
        Appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0)
                {
                    // Fully expanded
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
                else
                {
                    // Not fully expanded or collapsed
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }
            }
        });
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        circleIndicator = (CircleIndicator) findViewById(R.id.ciViewPagerImages);


        CoolToolbar = (CollapsingToolbarLayout)findViewById(R.id.ctolbar);
        toolbar = (Toolbar) findViewById(R.id.toolAtractivo);

        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        parametros();

    }
    public void parametros(){
        Bundle parametros = this.getIntent().getExtras();
        if(parametros !=null){

            this.atractivoKey = getIntent().getExtras().getString("atractivoKey");
            this.distancia = getIntent().getExtras().getString("distancia");
            this.nombreAtractivo = getIntent().getExtras().getString("atractivoNombre");

            if(getSupportActionBar() != null){
                getSupportActionBar().setTitle(nombreAtractivo);
            }

            // Recupera toda la informacion del atractivo
            getAtractivo();


            setTabs();// INICALIZAMOS Y COLOCAPOS LOS VIEWPAGER EN EL TABLAYOUT

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDatabase = null;
        viewPagerTab = null;
        viewPagerAdapter = null;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabase = null;
        viewPagerTab = null;
        viewPagerAdapter = null;


    }



    public void  getAtractivo(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("atractivo").child(this.atractivoKey);
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
                // Recupera las imagenes del atractivo
                for(DataSnapshot galeria: dataSnapshot.child("galeria").getChildren()){
                    listaImagenes.add(galeria.child("imagenURL").getValue().toString());
                }
                viewPagerAdapter = new ViewPagerAdapter(getApplicationContext(), listaImagenes , width,height);
                viewPager.setAdapter(viewPagerAdapter);
                circleIndicator.setViewPager(viewPager);
                viewPagerAdapter.registerDataSetObserver(circleIndicator.getDataSetObserver());

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









    // Funciones de TAB SELECT

    public void setTabs(){
        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        //Adding the tabs using addTab() method
        tabLayout.addTab(tabLayout.newTab().setText("Detalle"));
        tabLayout.addTab(tabLayout.newTab().setText("Comentarios"));
           tabLayout.addTab(tabLayout.newTab().setText("VR"));


        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        //Initializing viewPager
        viewPagerTab = (ViewPager) findViewById(R.id.pager);

        //Creating our pager adapter
        TabPagerAdapter adapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(),atractivoKey);
        //Adding adapter to pager
        viewPagerTab.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        viewPagerTab.setAdapter(adapter);

        //Adding onTabSelectedListener to swipe views

        tabLayout.addOnTabSelectedListener(this);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPagerTab.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
