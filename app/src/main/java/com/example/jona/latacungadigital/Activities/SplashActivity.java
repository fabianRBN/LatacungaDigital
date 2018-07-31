package com.example.jona.latacungadigital.Activities;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.example.jona.latacungadigital.Activities.References.PermissionsReferences;
import com.example.jona.latacungadigital.R;

public class SplashActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback  {
    //Duracion del Splash
    private final int DURACION_SPLASH = 2000;
    private View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.MyTheme_NoActionBar);
        super.onCreate(savedInstanceState);
     /*   setContentView(R.layout.activity_splash);
        mLayout = findViewById(R.id.splash_relative_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        //Pedir Permisos
        if(checkAllPermision()){
            ActivityCompat.requestPermissions(SplashActivity.this, PermissionsReferences.arrayPermission,
                    PermissionsReferences.REQUEST_CODE_ALL_PERMISSIONS);
        }else{
            //Handler Class. comunicarnos desde un subproceso con el hilo principal de la aplicación Android

                    //Iniciar LoginActivity
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.animation_fade_in,R.anim.animation_fade_out);
                    finish();

        }
    }

    private boolean checkAllPermision(){
        boolean hasAllPermision = true;
        for (int cont=0; cont < PermissionsReferences.arrayPermission.length; cont++ ){
            if (ContextCompat.checkSelfPermission(this, PermissionsReferences.arrayPermission[cont])
                    != PackageManager.PERMISSION_GRANTED) {
                hasAllPermision= false;
            }
        }
        return true;
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            Snackbar.make(mLayout,R.string.permission_location_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(SplashActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    PermissionsReferences.REQUEST_CODE_ACCESS_FINE_LOCATION);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PermissionsReferences.REQUEST_CODE_ACCESS_FINE_LOCATION);
        }
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            Snackbar.make(mLayout,R.string.permission_camera_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(SplashActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    PermissionsReferences.REQUEST_CODE_CAMERA);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PermissionsReferences.REQUEST_CODE_CAMERA);
        }
    }

    private void requestMicrophonePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO)) {
            Snackbar.make(mLayout,R.string.permission_microphone_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(SplashActivity.this,
                                    new String[]{Manifest.permission.RECORD_AUDIO},
                                    PermissionsReferences.REQUEST_CODE_RECORD_AUDIO);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    PermissionsReferences.REQUEST_CODE_RECORD_AUDIO);
        }
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(mLayout,R.string.permission_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(SplashActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    PermissionsReferences.REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PermissionsReferences.REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PermissionsReferences.REQUEST_CODE_ALL_PERMISSIONS){
            //Handler Class. comunicarnos desde un subproceso con el hilo principal de la aplicación Android
                    //Iniciar LoginActivity
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
        }
    }
}
