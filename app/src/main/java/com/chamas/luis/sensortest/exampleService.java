package com.chamas.luis.sensortest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Luis on 3/4/2015.
 */
public class exampleService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        Toast.makeText(this, "Service created", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy(){
        Toast.makeText(this, "Service destroyed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart(Intent intent, int startid){
        double mSensorX;
        mSensorX = intent.getExtras().getDouble("sensorValue");
        Toast.makeText(this, "Service started by user", Toast.LENGTH_SHORT).show();
        startClient(mSensorX);
    }

    public void startClient(double mSensorX){
        try {
            Socket client = new Socket("192.168.2.169", 8000);
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            out.writeDouble(mSensorX);
            Toast.makeText(this, "sensor info sent", Toast.LENGTH_SHORT).show();
            client.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
