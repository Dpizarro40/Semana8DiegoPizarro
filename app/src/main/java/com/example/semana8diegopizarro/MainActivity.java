package com.example.semana8diegopizarro;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    Button botonDescarga;
    ImageView imagen;
    Button botonMover;
    private SensorManager sensorManager;
    private Sensor sensor;
    private float currentAzimuth;
    private float currentPitch;
    private float currentRoll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        botonDescarga = findViewById(R.id.btnDescarga);
        botonMover = findViewById(R.id.btnMover);
        imagen = findViewById(R.id.imagenDescarga);

        botonDescarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final Bitmap bitmap = loadImageFromNetwork("https://th.bing.com/th/id/R.f3a7c0dbbf8a613d6ce4f125dc9ec42d?rik=3CaNqxMDdXrkrg&riu=http%3a%2f%2fpm1.narvii.com%2f6282%2f3759d808211db1494ef847d9ffb86daa8b7536bf_hq.jpg&ehk=WyZALhd52KVJ5HWbivoklHDguyhakyw%2frCeZlEVN9Mw%3d&risl=&pid=ImgRaw&r=0");
                        imagen.post(new Runnable() {
                            @Override
                            public void run() {
                                imagen.setImageBitmap(bitmap);
                            }
                        });
                    }
                }).start();
            }
        });

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        SensorEventListener rotationSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float[] rotationMatrix = new float[9];
                float[] orientationValues = new float[3];

                if (sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, orientationValues);
                    SensorManager.getOrientation(rotationMatrix, orientationValues);
                    float azimuth = (float) Math.toDegrees(orientationValues[0]);
                    float pitch = (float) Math.toDegrees(orientationValues[1]);
                    float roll = (float) Math.toDegrees(orientationValues[2]);

                    float deltaAzimuth = azimuth - currentAzimuth;
                    float deltaPitch = pitch - currentPitch;
                    float deltaRoll = roll - currentRoll;

                    imagen.setTranslationX(imagen.getTranslationX() + deltaRoll);
                    imagen.setTranslationY(imagen.getTranslationY() + deltaPitch);

                    currentAzimuth = azimuth;
                    currentPitch = pitch;
                    currentRoll = roll;
                }
            }

                @Override
                public void onAccuracyChanged (Sensor sensor,int i){

                }
            };
            sensorManager.registerListener(rotationSensorListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
            botonMover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sensorManager.registerListener(rotationSensorListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                }
            });
    }
    private Bitmap loadImageFromNetwork(String url) {
        try {
            URL imagenUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imagenUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream inputStream = conn.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}