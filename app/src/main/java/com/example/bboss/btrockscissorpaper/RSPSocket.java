package com.example.bboss.btrockscissorpaper;

import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.Security;

import javax.crypto.Cipher;

/**
 * Created by BBOSS on 27/05/2018.
 */

public class RSPSocket implements IOForRSPGame, Serializable{

    BluetoothSocket bluetoothSocket ;
    InputStream inputStream;
    OutputStream outputStream;
    Reader ioTh;
    Context context;
    private static String  bufferedMove;                //move received from socket...buffered returned to app with call receive
    public RSPSocket(BluetoothSocket btSocket,Context context)  throws IOException {
        this.bluetoothSocket=btSocket;
        if(btSocket==null){
            System.err.println("NULL SOCKET PASSED ! ");
            throw new NullPointerException();
        }
        this.inputStream=btSocket.getInputStream();
        this.outputStream=btSocket.getOutputStream();



        this.context=context;
    }

    public void read() {
        this.ioTh=new Reader();
        ioTh.start();
    }

    private class Reader extends Thread  {
        /*
        implement reader service... always attemp read from socket
        TODO fix long string send on socket... other data sent...problem to handle
        send broadcast msg with code MSG_RECEV when read return >0
         */
        int MAX_2READ=22;
        @Override
        public void run() {
            int readed=0;
            //now receive from socket
            byte[] bytes = new byte[MAX_2READ];

            try {
                while (true) {
                    readed= inputStream.read(bytes);
                    System.out.print(new String(bytes));
                    if(readed>0){
                        int r=0;
                        while(readed<MAX_2READ){
                            r=inputStream.read(bytes,readed,MAX_2READ-readed);
                            readed+=r;
                            System.out.print("\r"+new String(bytes));

                        }

                        Intent intent = new Intent(IOForRSPGame.READY_BT_MSG);
                        intent.putExtra("move",new String(bytes));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        RSPSocket.bufferedMove = new String(bytes);  //TODO ANONYMUS CLASS STATIC NOT NEEDED??

                        context.sendBroadcast(intent);
                    }
                    //Thread.currentThread().sleep(256);
                }
            } catch (IOException e) {
                e.printStackTrace();
                BTHandler.setupAllert("ERROR IN WRITE");
            } /*catch (InterruptedException e) {
                e.printStackTrace();
                System.err.println("INTERRUPTED");
            }*/


        }
    };

    public void abort(){
        this.ioTh.interrupt();
    }


    @Override
    public void sendMove(String s) throws Exception {
        //called
        final String toWrite = s+"\0";
        String a= new String(
                s.getBytes("UTF-8"));

        new Thread() {

            @Override
            public void run() {
                try {
                    outputStream.write(toWrite.getBytes("UTF-8"));

                } catch (IOException e) {
                    e.printStackTrace();
                    BTHandler.setupAllert("ERROR IN WRITE");
                }

            }
        }.start();
    }


    @Override
    public String receiveMove() throws Exception {
        return this.bufferedMove;
    }


}
