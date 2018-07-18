package com.example.jona.latacungadigital.Activities.References;

import android.Manifest;

public class PermissionsReferences {

    //Lista de permisos
    public static final String[] arrayPermission  =new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //Posision de los permisos en el array
    public static final int POSITION_ON_ARRAY_ACCESS_FINE_LOCATION = 0;
    public static final int POSITION_ON_ARRAY_CAMERA = 1;
    public static final int POSITION_ON_ARRAY_RECORD_AUDIO = 2;
    public static final int POSITION_ON_ARRAY_WRITE_EXTERNAL_STORAGE = 3;

    // Codigos de respuesta de permisos
    public static final int REQUEST_CODE_ACCESS_FINE_LOCATION = 100;
    public static final int REQUEST_CODE_CAMERA = 200;
    public static final int REQUEST_CODE_RECORD_AUDIO = 300;
    public static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 400;
    public static final int REQUEST_CODE_ALL_PERMISSIONS = 500;




}
