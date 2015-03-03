package com.chamas.luis.sensortest;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
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
    double mSensorX, mSensorY, mSensorZ;
    private Thread thread;



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

//        try {
//            Socket client = new Socket("192.168.2.169", 8000);
//            OutputStream outToServer = client.getOutputStream();
//            DataOutputStream out = new DataOutputStream(outToServer);
//            out.writeDouble(mSensorX);
//            client.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        new retriveTask().setmSensorX(mSensorX);
//        new retriveTask().runSocket();

        thread = new Thread(){
            @Override
            public void run(){
                int count =0;
                try {
                    Socket client = new Socket("192.168.2.169", 8000);
                    OutputStream outToServer = client.getOutputStream();
                    DataOutputStream out = new DataOutputStream(outToServer);
                    out.writeDouble(mSensorX);
                    client.close();

                    while(count < 5){
                        out.writeDouble(mSensorX);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
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


    public void send(View view) {
        thread.start();
    }

    public void getCont(View view) {
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        String aNameFromContacts[] = new String[cur.getCount()];
        String aNumberFromContacts[] = new String[cur.getCount()];
        int i=0;

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Toast.makeText(this, "Name: " + name + ", Phone No: " + phoneNo, Toast.LENGTH_SHORT).show();
                    }
                    pCur.close();
                }
            }
        }
    }
}
