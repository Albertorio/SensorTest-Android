package com.chamas.luis.sensortest;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.concurrent.TimeoutException;


public class MainActivity extends Activity implements SensorEventListener {
    private TextView accelText;
    private TextView gyroText;
    private Button accelBut;
    private SensorManager mSensorMan;
    private Sensor mAccel;
    private Sensor gyro;
    private TextView YtextView;
    private TextView ZtextView;



    //int accSensor = SensorManager.SENSOR_ACCELEROMETER;

    public MainActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gyroText = (TextView)findViewById(R.id.GyroTextView);
        accelText = (TextView)findViewById(R.id.AcceltextView);
        YtextView = (TextView)findViewById(R.id.YtextView4);
        ZtextView = (TextView)findViewById(R.id.ZtextView5);

        mSensorMan = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccel = mSensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyro = mSensorMan.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

//        accelText.setText(String.valueOf(mAccel));
//        gyroText.setText(String.valueOf(gyro));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onResume(){
        super.onResume();
        mSensorMan.registerListener(this,mAccel,SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause(){
        super.onPause();
        mSensorMan.unregisterListener(this);
    }


    public void onSensorChanged(SensorEvent event){
        if(event.sensor.getType() != Sensor.TYPE_ACCELEROMETER){
            return;
        }else{
            double mSensorX, mSensorY, mSensorZ;
            double alpha = 0.8;
            double[] gravity = new double[3];

            // Isolate the force of gravity with the low-pass filter.
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            // Remove the gravity contribution with the high-pass filter.
            mSensorX = event.values[0] - gravity[0];
            mSensorY = event.values[1] - gravity[1];
            mSensorZ = event.values[2] - gravity[2];

            accelText.setText(String.valueOf(mSensorX));
            YtextView.setText(String.valueOf(mSensorY));
            ZtextView.setText(String.valueOf(mSensorZ));

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}
