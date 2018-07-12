package com.example.jona.latacungadigital.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import com.example.jona.latacungadigital.Activities.Fragments.DialogAppFragment;
import com.example.jona.latacungadigital.Activities.Fragments.ListAtractivosFragment;
import com.example.jona.latacungadigital.Activities.Fragments.MapaFragment;
import com.example.jona.latacungadigital.Activities.Fragments.MenuARFragment;
import com.example.jona.latacungadigital.Activities.Fragments.TrackeadosFragment;
import com.example.jona.latacungadigital.Activities.Fragments.TrackinFragment;
import com.example.jona.latacungadigital.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.common.api.ResultCallback;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, MapaFragment.OnFragmentInteractionListener,
        ListAtractivosFragment.OnFragmentInteractionListener, DialogAppFragment.NoticeDialogListener, TrackinFragment.OnFragmentInteractionListener,
        TrackeadosFragment.OnFragmentInteractionListener {

    private GoogleApiClient googleApiClient; // Variable para manejar los datos de Google.

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    private DialogAppFragment dialogAppFragment; // Varibale para controlar el Dialogo de cerrar sesion.
    private static final int DIALOG_SIGN_OFF = 1; // Para saber que tipo de Dialogo es.

    private MapaFragment mapaFragment = new MapaFragment();
    private ListAtractivosFragment listAtractivosFragment = new ListAtractivosFragment();

    // Variables para identificar  en el fragmento que nos encontramos

    private Bundle args = new Bundle();



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //mTextMessage.setText(R.string.title_home);

                    setFragment(listAtractivosFragment);

                    return true;
                case R.id.navigation_mapa:
                   // mTextMessage.setText(R.string.title_dashboard);
                    setFragment(mapaFragment);

                    return true;
                case R.id.navigation_chat:
                    // mTextMessage.setText(R.string.title_notifications);
                    OpenChatBotActivity();

                    return true;

                case R.id.navigation_ar:
                    // mTextMessage.setText(R.string.title_notifications);
                    setFragment(new MenuARFragment());

                    //actividadAR();

                    return true;
            }
            return false;
        }
    };

    public void actividadAR(){
        Intent intent = new Intent(this, ARActivity.class);

        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setFragment(listAtractivosFragment);



        //mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Proceso para realizar el LoginIn con Google.
        GoogleSignInOptions googleSingInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, googleSingInOptions).build();

        // Proceso para saber si el usuario Auntentificado de Fireabse.
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {




                } else {
                    goLogInScreen();
                }
            }
        };

    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);

        MenuItem menuItem = menu.findItem(R.id.action_buscar);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText != "") {
                    setFragment(listAtractivosFragment);
                    listAtractivosFragment.listaAtractivo.clear();
                    listAtractivosFragment.ConsultarAtractivos(newText);

                }
                return true;
            }
        });

        MenuItem menuRastreo = menu.findItem(R.id.navigation_rastrear);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.navigation_salir) {
            openSignOffDialog();
            return true;
        }else
        if(id == R.id.navigation_configuraciones){
            startActivity(new Intent(MainActivity.this, PreferenciasActivity.class));

        }else
        if(id == R.id.navigation_prefrencia){
           /* Gestor de preferiencias
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

            System.out.println("Prefencias servicio:"+pref.getBoolean("notificacionAtractivo",false));
*/

        }else
        if (id == R.id.navigation_rastrear){
            setFragment(new TrackinFragment());
        }



        return super.onOptionsItemSelected(item);
    }

    private void goLogInScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void OpenChatBotActivity() {
        Intent intent = new Intent(this, ChatBotActivity.class);
        startActivity(intent);
    }


    public void openSignOffDialog() {
        // Se crea una instancia de la clase DialogAppFragement y se la muestra.
        dialogAppFragment = new DialogAppFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("Type_Dialog", DIALOG_SIGN_OFF);
        dialogAppFragment.setArguments(bundle);

        dialogAppFragment.show(getSupportFragmentManager(), "NoticeDialogFragment");
    }

    // Método para cerrar sesión de la aplicacion.
    private void logOut() {
        // Cerrar sesión con Firebase.
        firebaseAuth.signOut();

        // Proceso para cerrar la sesión de Google.
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    goLogInScreen();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.not_close_session, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseAuth.addAuthStateListener(firebaseAuthListener);

    }

    @Override
    public void onResume() {
        super.onResume();
        openMapFragmentFromNotification();
    }

    // Metodo para detener los procesos de Firebase.
    @Override
    protected void onStop() {
        super.onStop();

        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment, fragment);
        fragmentTransaction.commit();
    }

    private void openMapFragmentFromNotification() {
        String extras = getIntent().getStringExtra("OpenMapFragment");
        if (extras != null && extras.equals("mapa")) {
            setFragment(new MapaFragment());
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onDialogConfirmClick(DialogFragment dialog) { logOut(); }

    @Override
    public void onDialogCancelClick(DialogFragment dialog) { // Para cancelar la acción de cerrar sesión.
        dialogAppFragment.getDialog().cancel();
    }
}
