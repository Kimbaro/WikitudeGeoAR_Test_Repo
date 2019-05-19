package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.example.myapplication.util.LocationProvider;
import com.example.myapplication.util.SampleData;
import com.wikitude.architect.ArchitectStartupConfiguration;
import com.wikitude.architect.ArchitectView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Console;
import java.io.IOException;
import java.util.HashMap;

public class GeoArActivity extends AppCompatActivity implements LocationListener {

    //웹뷰를 통한 증강현실을 레이아웃 단에서 띄우기 위한 객체
    ArchitectView architectView;

    //설정한 시간 마다 GPS 또는 네트워크를 통해 사용자의 유스케이스에 따라 위치정보를 다룬다
    //위치정보를 세부하게 다루기 위해선 해당 클래스를 수정하면 된다.
    LocationProvider locationProvider;
    private final LocationProvider.ErrorCallback errorCallback = new LocationProvider.ErrorCallback() {
        @Override
        public void noProvidersEnabled() {
            Toast.makeText(getApplicationContext(), "GPS및 네트워크 설정이 필요합니다", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebView.setWebContentsDebuggingEnabled(true);

//        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        double longitude = location.getLongitude();
//        double latitude = location.getLatitude();
//        double altitude = location.getAltitude();
//        float accuracy = location.hasAccuracy() ? location.getAccuracy() : 1000;

        //  architectView.setLocation(latitude, longitude, altitude, accuracy);

        final Intent intent = getIntent();
        SampleData sampleData = (SampleData) intent.getSerializableExtra("sampleData");

        final ArchitectStartupConfiguration config = new ArchitectStartupConfiguration(); // Creates a config with its default values.
        config.setLicenseKey(getString(R.string.wikitude_license_key)); // Has to be set, to get a trial license key visit http://www.wikitude.com/developer/licenses.
        config.setCameraPosition(sampleData.getCameraPosition());       // The default camera is the first camera available for the system.
        config.setCameraResolution(sampleData.getCameraResolution());   // The default resolution is 640x480.
        config.setCameraFocusMode(sampleData.getCameraFocusMode());     // The default focus mode is continuous focusing.
        config.setCamera2Enabled(sampleData.isCamera2Enabled());

        architectView = new ArchitectView(this);
        architectView.onCreate(config);
        setContentView(architectView);

        locationProvider = new LocationProvider(this, this, errorCallback);
    }

    //LocationListener interface START
    @Override
    public void onLocationChanged(Location location) {
        if (location != null && architectView != null) {
            // check if location has altitude at certain accuracy level & call right architect method (the one with altitude information)
            if (location.hasAltitude() && location.hasAccuracy() && location.getAccuracy() < 7) {
                architectView.setLocation(location.getLatitude(), location.getLongitude(), location.getAltitude(), location.getAccuracy());
            } else {
                Toast.makeText(getApplicationContext(), "위치정보 로딩중", Toast.LENGTH_SHORT).show();
                architectView.setLocation(location.getLatitude(), location.getLongitude(), location.hasAccuracy() ? location.getAccuracy() : 1000);
            }
//            Toast.makeText(getApplicationContext(), location.getLatitude() + "||" + location.getLongitude() + "||" + location.getAltitude(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
    //LocationListener interface END


    private final ArchitectView.SensorAccuracyChangeListener sensorAccuracyChangeListener = new ArchitectView.SensorAccuracyChangeListener() {
        @Override
        public void onCompassAccuracyChanged(int accuracy) {
            if (accuracy < SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM) { // UNRELIABLE = 0, LOW = 1, MEDIUM = 2, HIGH = 3
                Toast.makeText(getApplicationContext(), "-", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        architectView.onPostCreate();
        try {
            architectView.load("09_ObtainPoiData_2_FromLocalResource/index.html");
            Toast.makeText(getApplicationContext(), "index 실행", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "error!!", Toast.LENGTH_SHORT).show();
            Log.e("KIM", "Exception while loading arExperience ", e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        architectView.onResume();
        architectView.registerSensorAccuracyChangeListener(sensorAccuracyChangeListener);

        locationProvider.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        architectView.onPause();
        // The SensorAccuracyChangeListener has to be unregistered from the Architect view before ArchitectView.onDestroy.
        architectView.unregisterSensorAccuracyChangeListener(sensorAccuracyChangeListener);

        locationProvider.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        architectView.unregisterSensorAccuracyChangeListener(sensorAccuracyChangeListener);
        architectView.clearCache();
        architectView.onDestroy(); // Mandatory ArchitectView lifecycle call
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        architectView.onResume();
    }
}
