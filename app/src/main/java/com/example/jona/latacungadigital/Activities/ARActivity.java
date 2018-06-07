package com.example.jona.latacungadigital.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.example.jona.latacungadigital.Activities.Permisos.LocationProvider;
import com.example.jona.latacungadigital.R;
import com.wikitude.architect.ArchitectStartupConfiguration;
import com.wikitude.architect.ArchitectView;

import java.io.IOException;

public class ARActivity extends AppCompatActivity {

    FloatingActionButton btn_salir ;
    private ArchitectView architectView;
    private LocationProvider locationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED) {

            SensorManager sensorManager =
                    (SensorManager) getSystemService(SENSOR_SERVICE);

            Sensor gyroscopeSensor =
                    sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

            SensorEventListener gyroscopeSensorListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {
                    // More code goes here
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {

                }
            };

            sensorManager.registerListener(gyroscopeSensorListener,
                    gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }


            btn_salir =  (FloatingActionButton) findViewById(R.id.fab);

        btn_salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        architectView = (ArchitectView)this.findViewById( R.id.architectView );
        final ArchitectStartupConfiguration config = new ArchitectStartupConfiguration();
        config.setFeatures(ArchitectStartupConfiguration.Features.Geo);
        config.setLicenseKey( "oWjZeBxLExDsXbbef5ylW6vZePQkJDE62FKnOp31hsz+EfEx6k4y8XJo3ir+3B9Vu3X8DAa60VH5gW/y5SjFsLZ4TWoHLcyLL/VFfmGtZNAPpN7jOk/xJ8rmTnVjE3W0iCbBJiD2WDqidCjSz5xRhWirT23wICtyPTCC05qyViRTYWx0ZWRfXwUhvhR3yQlpKY5VdWG6M+BLgaOMYQKgyMEMgJY51vVHJLaRW+KH9v7EhIONDB6xZXmIY87GCFPNhbPB5x8uHrm+beLEjBQvOHcwTg82m7mAg0TCWpw4Unh6VK8cLIruFHE1POkwGzUYevkYli1qYdjOi/wnpe/0S2RnAw36G5OQsVbgi/J7cxLoA8jGPr57IoKkBiP7nC+/BECQxtW8DryPFByZ6T/dyFK9Q0VBi2NGe2Ib4cOUC01Yt/kfqiDli5837Qn3Ii1pssMIbwK1zxHj0AO0ylfxiZ4l8pvb1RqieqilzSg2M7szwPSboI4WRLTqORGS9mPsW15A8MKpboAIuCI/g+oXAqtmuG0bw/iDvF6i5IevNDLRLsx22ipZtIqxZqQxxUOjmNo9YIjCpLp+UnhUHD2Zz9tzMFf9xwSuztwcLq89SCT3YZ55TvaycMztr9iouellYj6N/GB1C4231qvpMiPPZvf/sAaxpDueJP1QTjUkZEmbaAIRMOnoUMMciaRgxkOsJ+pVHFGwPnUvE9X7h0j7xQn8+BkDSsRVForVWPPd6MJahngefEyYR0wqpMUIVe5N" );

        architectView.onCreate( config );

        locationProvider = new LocationProvider(this, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location!=null && ARActivity.this.architectView != null ) {
                    // check if location has altitude at certain accuracy level & call right architect method (the one with altitude information)
                    if ( location.hasAltitude() && location.hasAccuracy() && location.getAccuracy()<7) {
                        System.out.println("Location1: lat:"+location.getLatitude()+" , long:"+location.getLongitude()+" , altitud:"+location.getAltitude());
                        ARActivity.this.architectView.setLocation( location.getLatitude(), location.getLongitude(), location.getAltitude(), location.getAccuracy() );
                    } else {
                        System.out.println("Location2: lat:"+location.getLatitude()+" , long:"+location.getLongitude()+" , altitud:"+location.getAltitude());
                        ARActivity.this.architectView.setLocation( location.getLatitude(), location.getLongitude(), location.hasAccuracy() ? location.getAccuracy() : 1000 );
                    }


                }else{
                    System.out.println("No entro !!");
                }
            }

            @Override public void onStatusChanged(String s, int i, Bundle bundle) {}
            @Override public void onProviderEnabled(String s) {}
            @Override public void onProviderDisabled(String s) {}
        });



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        architectView.onPostCreate();
        try {
            architectView.load(  "file:///android_asset/geo/index.html"  );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        architectView.onResume();
        // start location updates
        locationProvider.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        architectView.onPause();
        // stop location updates
        locationProvider.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        architectView.onDestroy();
    }
}
