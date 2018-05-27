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
API x gestione immagini vettoriali (drowable ..)
 */
public class ClientGetConnection extends AsyncTask<BluetoothDevice,Void,BluetoothSocket> {
    /*
    th to get socket (android os will wrap pairing and estamblish rtf?? comunication channel between hosts
    TODO WRAP SOMETHING ELSE?
     */

    private BluetoothSocket getSocket(BluetoothDevice targetDev) {
        //get socket from pairing with btdevice debugTargetDevice...
        BluetoothSocket actualSocket = null;

        try {

            actualSocket = targetDev.createInsecureRfcommSocketToServiceRecord(MainActivity.uuid);
            actualSocket.connect(); //TODO BLOCKING UNTIL CLIENT AND SERVER HAVE PAIRED...
            //IOEXEPTION FOR TIMEOUT---ERRORS
        } catch (IOException e) {
            BTHandler.setupAllert("ERROR IN CREATE COMUNICATION CHANNEL CLIENT");
            e.printStackTrace();
        }
        return actualSocket;
    }


    @Override
    protected BluetoothSocket doInBackground(BluetoothDevice... bluetoothDevice) {
        //only case to return streams

        BluetoothSocket btsock = this.getSocket(bluetoothDevice[0]);
        return btsock;
    }


}