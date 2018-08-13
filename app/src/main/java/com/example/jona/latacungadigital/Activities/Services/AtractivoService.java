package com.example.jona.latacungadigital.Activities.Services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.jona.latacungadigital.Activities.AtractivoActivity;
import com.example.jona.latacungadigital.Activities.modelos.AtractivoModel;
import com.example.jona.latacungadigital.Activities.modelos.Coordenada;
import com.example.jona.latacungadigital.LatacungaDigital;
import com.example.jona.latacungadigital.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AtractivoService extends Service {

    GeoFire geoFire;

    // Referencia para el usuario (cliente)
    private DatabaseReference mDatabase;
    // REferencia para atractivos
    private DatabaseReference mDatabaseAtractivo;
    // REferencia para historial
    private DatabaseReference mDatabaseHistorial;
    // Localizacion del usuario en listener
    private LocationListener listener;
    private LocationManager locationManager;
    private FirebaseUser userFirebase = FirebaseAuth.getInstance().getCurrentUser();
    //Minimo tiempo para updates en Milisegundos
    private static final long MIN_TIEMPO_ENTRE_UPDATES = 1000 * 30 * 1; // 1 minuto
    //Minima distancia para updates en metros.
    private static final long MIN_CAMBIO_DISTANCIA_PARA_UPDATES = (long) 0.5; // 1.5 metros


    //Para leer y escribir en la base de datos, necesitas una instancia de DatabaseReference:
    private DatabaseReference databaseReference;

    SharedPreferences preferences;

    private boolean ESTADO_SERVICIO = false;
    // Referencia de la fecha
    Calendar fecha = new GregorianCalendar();
    int año,mes,dia ;

    private NotificationManager notificationManager;
    // Identificador de notificacion
    private static  final int ID_NOTIFICATION =1234;

    private ArrayList<AtractivoModel> lista = new ArrayList<>();

    private NotificationManager mNotificationManager;

    public AtractivoService() {
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        año = fecha.get(Calendar.YEAR);
        mes = fecha.get(Calendar.MONTH);
        dia = fecha.get(Calendar.DAY_OF_MONTH);


        // seguimiento de la ubicacion del cliente por medio del locationlistener
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                geoFire.setLocation("GeoFire",new GeoLocation(location.getLatitude(), location.getLongitude()));
                // Carga de atractivos, cada uno posee un geofire listener
                // Nota: no sobrecargar el servicio y tener encuenta  los tiempos y recursos de consumo
                listaAtractivos();

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                sendNotification("Alerta", String.format(" La búsqueda de atractivos requiere el uso de GPS "),"",1234);


            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        //noinspection MissingPermission
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                MIN_TIEMPO_ENTRE_UPDATES ,
                MIN_CAMBIO_DISTANCIA_PARA_UPDATES,
                listener);

        // Referencia de firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabase = databaseReference.child("cliente").child(userFirebase.getUid());
        mDatabaseAtractivo = databaseReference.child("atractivo");
        mDatabaseHistorial = databaseReference.child("historial");
        geoFire = new GeoFire(mDatabase);



        Toast.makeText(this,"Servicio creado",Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        if(preferences.getBoolean("notificacionAtractivo",false)){

            System.out.println("Re activado");
            Intent serv = new Intent(getApplicationContext(),AtractivoService.class); //serv de tipo Intent
            sendBroadcast(serv);

        }else {

            Toast.makeText(this, "Servicio terminado", Toast.LENGTH_SHORT).show();
            super.onDestroy();
            //  evita que se creen varios resgistros de location listener al terminar destruir el servicio
            System.out.println("Buscando terminado !!!");
            if (locationManager != null) {
                locationManager.removeUpdates(listener);
            }
            notificationManager.cancel(ID_NOTIFICATION);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("No yet ");
    }

    // Metodo para realizar la carga de atractivos
    public void listaAtractivos(){

        mDatabaseAtractivo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    lista.clear();
                    int contador = 1234;
                    for(DataSnapshot child: dataSnapshot.getChildren()){
                        if(child.getValue() != null) { // Validacion para verificar si existe informacion en el child
                            double latitud = (double) child.child("posicion").child("lat").getValue();
                            double longitud = (double) child.child("posicion").child("lng").getValue();
                            String nombreAtractivo = child.child("nombre").getValue().toString();
                            String key = child.getKey().toString();
                            //geofireAtractivo((new LatLng(latitud, longitud)), nombreAtractivo, key, contador);// Metodo que agrega geofire listener a cada atractivo
                            lista.add(new AtractivoModel(nombreAtractivo,"",(new Coordenada( latitud, longitud)),key));

                        }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

  /*      lista.clear();
        lista.add(new AtractivoModel("La merced","",(new Coordenada( -0.837376, -78.669212)),"-LDcfos4UvolSFqWrpK1"));
        lista.add(new AtractivoModel("San agustin","",(new Coordenada( -0.837966, -78.670038)),"-LDcgS8X1lDN6ryGhdwu"));
        lista.add(new AtractivoModel("San salto","",(new Coordenada( -0.836831, -78.669183)),"-LDcheFJwHROPzPfztAG"));
        lista.add(new AtractivoModel("San fransisco","",(new Coordenada( -0.836831, -78.669183)),"-LDcgyVV2aZ7JGsXCHeA"));

*/

        for(int x=0;x<lista.size();x++) {

            geofireAtractivo(new LatLng(lista.get(x).getPosicion().getLat(), lista.get(x).getPosicion().getLng()), lista.get(x).getNombre(), lista.get(x).getKey(), x);// Metodo que agrega geofire listener a cada atractivo

        }
    }

    private void sendNotification(String title, String content, final String key, int id) {
        // Crea una isntancia del systema principal de notificaciones
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Validacion de la vercion de SDK   versiones sueriores a 7v requieren la declaracion de un channel
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Creaccion de channel
            NotificationChannel channel = new NotificationChannel("default",
                    "LATACUNGA_CHANNEL",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("LATACUNGA_NOTIFICATION_CHANNEL");
            mNotificationManager.createNotificationChannel(channel);
        }
        // Creacion de la notificacion

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                .setContentTitle(title) // title for notification
                .setContentText(content)// message for notification
                .setAutoCancel(true); // clear notification after click
        if(key.equals("")){

            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Long.toString(System.currentTimeMillis()));
            PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pi);
            mNotificationManager.notify(id, mBuilder.build());

        }else {
            final Intent intent = new Intent(getApplicationContext(), AtractivoActivity.class);
            intent.putExtra("atractivoKey", key);
            intent.setAction(Long.toString(System.currentTimeMillis()));
            PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pi);
            mNotificationManager.notify(id, mBuilder.build());
        }




    }

    public void geofireAtractivo(LatLng location , final String nombreAtractivo, final String keyAtractivo, final int id_notificacion){
        System.out.println("Buscando posicion: "+keyAtractivo );

        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(location.latitude, location.longitude), 0.2);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Date fecha = new Date();
                sendNotification("Alerta", String.format("Te encuentras  en el atractivo:" + nombreAtractivo+"."),keyAtractivo, id_notificacion);

                mDatabaseHistorial.child(keyAtractivo).child(userFirebase.getUid()).child(fecha.getYear()+""+fecha.getMonth()+""+fecha.getDay()).setValue(fecha);

            }

            @Override
            public void onKeyExited(String key) {
                sendNotification("Alerta", String.format(" Esta cerca del atractivo "+nombreAtractivo),keyAtractivo,id_notificacion);

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                Log.d("Alerta", String.format(" moved within the dangerous area "));
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.e("ERROR", ""+error);
            }
        });
    }
}

