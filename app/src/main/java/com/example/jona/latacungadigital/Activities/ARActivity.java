package com.example.jona.latacungadigital.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.jona.latacungadigital.R;
import com.wikitude.architect.ArchitectJavaScriptInterfaceListener;
import com.wikitude.architect.ArchitectStartupConfiguration;
import com.wikitude.architect.ArchitectView;

import org.json.JSONException;
import org.json.JSONObject;


public class ARActivity extends AppCompatActivity implements LocationListener ,ArchitectJavaScriptInterfaceListener {

    FloatingActionButton btn_salir ;

    private ArchitectView architectView;

    private LocationManager lm;

    private double latitude;
    private double longitude;
    private double altitude = 0;
    private float accuracy;
    private String tipoAR= "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ar);

       // btn_salir =  (FloatingActionButton) findViewById(R.id.fab);

       /* btn_salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/
        Bundle parametros = this.getIntent().getExtras();
        if(parametros !=null){
            tipoAR = parametros.getString("tipoAR");

        }

        this.architectView = (ArchitectView) this.findViewById(R.id.architectView);
        final ArchitectStartupConfiguration config = new ArchitectStartupConfiguration();
        config.setLicenseKey( "oWjZeBxLExDsXbbef5ylW6vZePQkJDE62FKnOp31hsz+EfEx6k4y8XJo3ir+3B9Vu3X8DAa60VH5gW/y5SjFsLZ4TWoHLcyLL/VFfmGtZNAPpN7jOk/xJ8rmTnVjE3W0iCbBJiD2WDqidCjSz5xRhWirT23wICtyPTCC05qyViRTYWx0ZWRfXwUhvhR3yQlpKY5VdWG6M+BLgaOMYQKgyMEMgJY51vVHJLaRW+KH9v7EhIONDB6xZXmIY87GCFPNhbPB5x8uHrm+beLEjBQvOHcwTg82m7mAg0TCWpw4Unh6VK8cLIruFHE1POkwGzUYevkYli1qYdjOi/wnpe/0S2RnAw36G5OQsVbgi/J7cxLoA8jGPr57IoKkBiP7nC+/BECQxtW8DryPFByZ6T/dyFK9Q0VBi2NGe2Ib4cOUC01Yt/kfqiDli5837Qn3Ii1pssMIbwK1zxHj0AO0ylfxiZ4l8pvb1RqieqilzSg2M7szwPSboI4WRLTqORGS9mPsW15A8MKpboAIuCI/g+oXAqtmuG0bw/iDvF6i5IevNDLRLsx22ipZtIqxZqQxxUOjmNo9YIjCpLp+UnhUHD2Zz9tzMFf9xwSuztwcLq89SCT3YZ55TvaycMztr9iouellYj6N/GB1C4231qvpMiPPZvf/sAaxpDueJP1QTjUkZEmbaAIRMOnoUMMciaRgxkOsJ+pVHFGwPnUvE9X7h0j7xQn8+BkDSsRVForVWPPd6MJahngefEyYR0wqpMUIVe5N" );
        this.architectView.addArchitectJavaScriptInterfaceListener(this);
        this.architectView.onCreate(config);


    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        architectView.onPostCreate();

        try {


            switch (tipoAR) {
                case "servicios":
                    this.architectView.load("file:///android_asset/servicios/index.html");
                    break;
                case "atractivos":
                    this.architectView.load("file:///android_asset/Lugares/index.html");
                    break;
                case "panoramica":
                    this.architectView.load("file:///android_asset/mirador/index.html");
                    break;
                default:
                    this.architectView.load("file:///android_asset/Lugares/index.html");
                    break;

            }
            architectView.setLocation(latitude, longitude, altitude, accuracy);
        } catch (Exception e) {
            System.out.println("Error al cargar el asset de Wikitude");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, this);

        architectView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(lm != null){
            lm = null;
        }
        architectView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.removeUpdates(this);

        architectView.onPause();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                               funciones de ubicación                                            //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        altitude = 0;
        accuracy = location.getAccuracy();
        architectView.setLocation(latitude, longitude, altitude, accuracy); //actualización de posición

    }

    //Método que proporciona el estado de la función de ubicación (Disponible, temporalmente no disponible, fuera de servicio)
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        String newStatus = "";
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                newStatus = "Fuera de servicio";
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                newStatus = "No Disponible";
                break;
            case LocationProvider.AVAILABLE:
                newStatus = "Disponible";
                break;
        }
        String msg = String.format((getResources().getString(R.string.provider_new_status)), provider, newStatus);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderEnabled(String provider) {
        //String msg = String.format(getResources().getString(R.string.provider_enabled, provider);
        Toast.makeText(this, "Ubicación activada", Toast.LENGTH_SHORT).show();
        architectView.setLocation(latitude, longitude, altitude, accuracy);
    }

    @Override
    public void onProviderDisabled(String provider) {
        //String msg = String.format(getResources().getString(R.string.provider_disabled), provider);
        Toast.makeText(this, "Ubicación deshabilitada", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onJSONObjectReceived(JSONObject jsonObject) {
        try {
            switch (jsonObject.getString("action")) {
                case "markerselected":
                    String id = jsonObject.getString("id");
                    System.out.println("Si funciona:" + id);
                    break;
                case "facebook":
                    String urlPage = jsonObject.getString("url");
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlPage)));
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("No funciona:");
        }



    }
}
