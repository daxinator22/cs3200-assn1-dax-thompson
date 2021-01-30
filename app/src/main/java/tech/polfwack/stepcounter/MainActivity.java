package tech.polfwack.stepcounter;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public int stepCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView steps = findViewById(R.id.steps);
        final TextView rate = findViewById(R.id.rate);

        LinearLayout layout = findViewById(R.id.layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stepCount = 0;
                steps.setText("0");
                rate.setText("BPM: 0" );
            }
        });

        SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Sensor accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        final Sensor heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);

        sensorManager.registerListener(
                new SensorEventListener() {

                    private long lastStepTimeStamp = 0;

                    @Override
                    public void onSensorChanged(SensorEvent sensorEvent) {
                        double x = sensorEvent.values[0];
                        double y = sensorEvent.values[1];
                        double z = sensorEvent.values[2];

                        double normalizedGForce = Math.sqrt(x * x + y * y + z * z) / SensorManager.GRAVITY_EARTH;

                        if (normalizedGForce > 1.5){
                            long now = System.currentTimeMillis();
                            if(lastStepTimeStamp + 500 > now) {
                                return;
                            }
                            else{
                                stepCount++;
                                steps.setText("" + stepCount);
                                lastStepTimeStamp = now;
                            }
                        }
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int i) {

                    }
                },
                accel,
                SensorManager.SENSOR_DELAY_UI);

        sensorManager.registerListener(
                new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent sensorEvent) {
                        double heartRate = sensorEvent.values[0];
                        rate.setText("BPM: " + heartRate);
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int i) {

                    }
                },

                heartRateSensor,
                SensorManager.SENSOR_DELAY_UI);
        
    }
}
