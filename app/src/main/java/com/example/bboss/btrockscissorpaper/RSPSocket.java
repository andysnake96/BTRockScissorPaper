package com.example.bboss.btrockscissorpaper;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.IBinder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;

import javax.crypto.Cipher;

/**
 * Created by BBOSS on 27/05/2018.
 */

public class RSPSocket extends Service implements IOForRSPGame  {

    BluetoothSocket bluetoothSocket ;
    InputStream inputStream;
    OutputStream outputStream;
    private static String  bufferedMove;                //move received from socket...buffered returned to app with call receive
    public RSPSocket(BluetoothSocket btSocket) throws IOException {
        this.bluetoothSocket=btSocket;
        if(btSocket==null){
            System.err.println("NULL SOCKET PASSED ! ");
            throw new NullPointerException();
        }
        this.inputStream=btSocket.getInputStream();
        this.outputStream=btSocket.getOutputStream();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;            //DUMMY SERVICE
    }

    @Override
    public void sendMove(String s) throws Exception {

        final String toWrite=s;
        new Thread() {

            @Override
            public void run() {

                //now receive from socket
                byte[] bytes = new byte[10];
                try {
                    outputStream.write(toWrite.getBytes());

                    inputStream.read(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                    BTHandler.setupAllert("ERROR IN WRITE");
                }
                RSPSocket.bufferedMove = bytes.toString();  //TODO ANONYMUS CLASS STATIC NOT NEEDED??
                Intent intent = new Intent(IOForRSPGame.READY_BT_MSG);
                intent.putExtra("move",bytes.toString());
                sendBroadcast(intent);

            }
        }.start();

        //TODO NOT BLOCK!=
    }

    @Override
    public String receiveMove() throws Exception {
        return this.bufferedMove;
    }

}
