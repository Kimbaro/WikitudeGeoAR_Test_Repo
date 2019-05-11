package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication.util.SampleData;
import com.wikitude.architect.ArchitectStartupConfiguration;
import com.wikitude.common.camera.CameraSettings;

public class MainActivity extends AppCompatActivity {
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView.setWebContentsDebuggingEnabled(true);

        final int PERMISSION = 1;

        if ((Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) || (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) || (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) || (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) || (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION);
        }


        Button button = (Button) findViewById(R.id.ar_start);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SampleData sampleData = new SampleData.Builder("SAMPLE_NAME", "PATH_TO_AR_EXPERIENCE")
                        .cameraFocusMode(CameraSettings.CameraFocusMode.CONTINUOUS)
                        .cameraPosition(CameraSettings.CameraPosition.BACK)
                        .cameraResolution(CameraSettings.CameraResolution.HD_1280x720)
                        .camera2Enabled(false)
                        .build();

                Intent intent = new Intent(getApplicationContext(), GeoArActivity.class);
                intent.putExtra("sampleData", sampleData);
                startActivity(intent);
            }
        });
        Button button1 = findViewById(R.id.ar_exit);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
