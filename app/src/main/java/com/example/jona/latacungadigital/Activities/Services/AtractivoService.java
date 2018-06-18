package com.example.jona.latacungadigital.Activities.Services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.jona.latacungadigital.Activities.AtractivoActivity;
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

public class AtractivoService extends Service {

    GeoFire geoFire;
    // Referencia para el usuario (cliente)
    private DatabaseReference mDatabase;
    // REferencia para atractivos
    private DatabaseReference mDatabaseAtractivo;
    // Localizacion del usuario en listener
    private LocationListener listener;
    private LocationManager locationManager;
    private FirebaseUser userFirebase = FirebaseAuth.getInstance().getCurrentUser();
    //Minimo tiempo para updates en Milisegundos
    private static final long MIN_TIEMPO_ENTRE_UPDATES = 1000 * 60 * 1; // 1 minuto
    //Minima distancia para updates en metros.
    private static final long MIN_CAMBIO_DISTANCIA_PARA_UPDATES = (long) 1.5; // 1.5 metros


    private NotificationManager notificationManager;
    // Identificador de notificacion
    private static  final int ID_NOTIFICATION =1234;

    public AtractivoService() {
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();


        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

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
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        //noinspection MissingPermission
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIEMPO_ENTRE_UPDATES,MIN_CAMBIO_DISTANCIA_PARA_UPDATES,listener);

        // Referencia de firebase
        mDatabase = FirebaseDatabase.getInstance().getReference("cliente").child(userFirebase.getUid());
        mDatabaseAtractivo = FirebaseDatabase.getInstance().getReference("atractivo");
        geoFire = new GeoFire(mDatabase);



        Toast.makeText(this,"Servicio creado",Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this,"Servicio iniciado",Toast.LENGTH_SHORT).show();


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this,"Servicio terminado",Toast.LENGTH_SHORT).show();
        super.onDestroy();
        //  evita que se creen varios resgistros de location listener al terminar destruir el servicio
        if(locationManager!=null){
            locationManager.removeUpdates(listener);
        }
        notificationManager.cancel(ID_NOTIFICATION);


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


                    for(DataSnapshot child: dataSnapshot.getChildren()){
                        if(child.getValue() != null) { // Validacion para verificar si existe informacion en el child
                            double latitud = (double) child.child("posicion").child("lat").getValue();
                            double longitud = (double) child.child("posicion").child("lng").getValue();
                            String nombreAtractivo = child.child("nombre").getValue().toString();
                            String key = child.getKey().toString();
                            geofireAtractivo((new LatLng(latitud, longitud)), nombreAtractivo, key);// Metodo que agrega geofire listener a cada atractivo

                        }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendNotification(String title, String content, String key) {
        // Crea una isntancia del systema principal de notificaciones
        NotificationManager mNotificationManager =
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
                .setContentTitle("Atractivo encontrado") // title for notification
                .setContentText(content)// message for notification
                .setAutoCancel(true); // clear notification after click
        Intent intent = new Intent(getApplicationContext(), AtractivoActivity.class);
        intent.putExtra("atractivoKey", key);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        mNotificationManager.notify(0, mBuilder.build());



    }

    public void geofireAtractivo(LatLng location , final String nombreAtractivo, final String keyAtractivo){
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(location.latitude, location.longitude), 0.5f);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                sendNotification("Alerta", String.format("Te encuantras en el atractivo:" + nombreAtractivo+"."),keyAtractivo);
            }

            @Override
            public void onKeyExited(String key) {
                sendNotification("Alerta", String.format(" Esta cerca del atractivo "+nombreAtractivo),keyAtractivo);

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
