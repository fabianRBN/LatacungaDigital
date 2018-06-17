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
import com.example.jona.latacungadigital.Activities.Fragments.MapaFragment;
import com.example.jona.latacungadigital.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class AtractivoService extends Service {

    GeoFire geoFire;
    private DatabaseReference mDatabase;

    private LocationListener listener;
    private LocationManager locationManager;
    private FirebaseUser userFirebase = FirebaseAuth.getInstance().getCurrentUser();
    //Minimo tiempo para updates en Milisegundos
    private static final long MIN_TIEMPO_ENTRE_UPDATES = 1000 * 60 * 1; // 1 minuto
    //Minima distancia para updates en metros.
    private static final long MIN_CAMBIO_DISTANCIA_PARA_UPDATES = (long) 1.5; // 1.5 metros


    private NotificationManager notificationManager;
    private static  final int ID_NOTIFICATION =1234;

    public AtractivoService() {
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();


        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                geoFire.setLocation("GeoFire",new GeoLocation(location.getLatitude(), location.getLongitude()));

                GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(-0.837691,-78.6700539), 0.5f);
                geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                    @Override
                    public void onKeyEntered(String key, GeoLocation location) {
                        Toast.makeText(getApplicationContext(),"Servicio eter",Toast.LENGTH_SHORT).show();

                        sendNotification("Alerta", String.format("Entraste en un área Peligrosa"));

                    }

                    @Override
                    public void onKeyExited(String key) {


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

        mDatabase = FirebaseDatabase.getInstance().getReference("cliente").child(userFirebase.getUid());
        geoFire = new GeoFire(mDatabase);



        Toast.makeText(this,"Servicio creado",Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this,"Servicio iniciado",Toast.LENGTH_SHORT).show();





        /*mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child: dataSnapshot.getChildren()){
                    double latitud = (double) child.child("l").child("0").getValue();
                    double longitud = (double) child.child("l").child("1").getValue();
                    System.out.print("Valores:"+latitud+" "+longitud);
                    Toast.makeText(getApplicationContext(),"Valores:"+latitud+" "+longitud,Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/




        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this,"Servicio terminado",Toast.LENGTH_SHORT).show();
        super.onDestroy();

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

    private void sendNotification(String title, String content) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default",
                    "LATACUNGA_CHANNEL",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("LATACUNGA_NOTIFICATION_CHANNEL");
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                .setContentTitle("Titulo") // title for notification
                .setContentText(content)// message for notification
                .setAutoCancel(true); // clear notification after click
        Intent intent2 = new Intent(getApplicationContext(), AtractivoActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        mNotificationManager.notify(0, mBuilder.build());



    }

    public void geofireAtractivo(Location location){
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(location.getLatitude(), location.getLongitude()), 0.1f);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                sendNotification("Alerta", String.format("Entraste en un área Peligrosa"));
            }

            @Override
            public void onKeyExited(String key) {
                sendNotification("Alerta", String.format(" Esta cerca un área Peligrosa"));

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
