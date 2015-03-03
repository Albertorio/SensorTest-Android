package com.chamas.luis.sensortest;

import android.os.AsyncTask;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Luis on 3/3/2015.
 */
public class retriveTask extends AsyncTask {
    private double mSensorX;


    public void runSocket(){
        try {
            Socket client = new Socket("192.168.2.169", 8000);
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            out.writeDouble(mSensorX);
            client.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setmSensorX(double sensorX){
        sensorX = mSensorX;

    }

    @Override
    protected Object doInBackground(Object[] params) {
        return null;
    }
}
