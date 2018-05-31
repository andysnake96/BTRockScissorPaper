package com.example.bboss.btrockscissorpaper;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.renderscript.ScriptGroup;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by BBOSS on 24/05/2018.
 */
/*

 */                                                 //TODO REMOVE?
public class ClientGetConnection extends AsyncTask<BluetoothDevice,Void,BluetoothSocket> {
    /*
    th to get socket (android os will wrap pairing and estamblish rtf?? comunication channel between hosts
    TODO WRAP SOMETHING ELSE?
     */
    BluetoothSocket actualSocket ;
    MainActivity mainActivity;
    public ClientGetConnection(BluetoothDevice target,MainActivity mainActivity){
        System.out.println("target2connect"+target.getAddress());
        try {
            if(IOForRSPGame.SECURE)
                this.actualSocket=target.createRfcommSocketToServiceRecord(MainActivity.uuid);
            else
                this.actualSocket=target.createInsecureRfcommSocketToServiceRecord(MainActivity.uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.mainActivity=mainActivity;
    }


    private BluetoothSocket getSocket(BluetoothDevice targetDev) {
        //get socket from pairing with btdevice debugTargetDevice...
        System.out.println("\n\nBEFORE"+actualSocket.isConnected());
        try {

            //actualSocket = targetDev.createInsecureRfcommSocketToServiceRecord(MainActivity.uuid);
            actualSocket.connect(); //TODO BLOCKING UNTIL CLIENT AND SERVER HAVE PAIRED...
            //IOEXEPTION FOR TIMEOUT---ERRORS
        } catch (IOException e) {
            BTHandler.setupAllert("ERROR IN CREATE COMUNICATION CHANNEL CLIENT");
            e.printStackTrace();
        }
        System.out.println("\n\nAFTER"+actualSocket.isConnected());

        return actualSocket;
    }


    @Override
    protected BluetoothSocket doInBackground(BluetoothDevice... bluetoothDevice) {
        //only case to return streams

        BluetoothSocket btsock = this.getSocket(bluetoothDevice[0]);
        return btsock;
    }

    @Override
    protected void onPostExecute(BluetoothSocket bluetoothSocket) {
        mainActivity.takeSocket(bluetoothSocket);

    }
}