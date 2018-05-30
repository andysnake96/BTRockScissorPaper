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

public class RSPSocket implements IOForRSPGame, Parcelable{

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    private class Reader extends Thread  {
        /*
        implement reader service... always attemp read from socket
        TODO fix long string send on socket... other data sent...problem to handle
        send broadcast msg with code MSG_RECEV when read return >0
         */
        int MAX_2READ=7;
        @Override
        public void run() {
            int readed,r;
            readed=r=0;
            //now receive from socket
            byte[] bytes = new byte[MAX_2READ];

            try {
                int c=0;
                do {

                    while (inputStream.available() > 0) {
                        r = inputStream.read(bytes, readed, MAX_2READ - readed);
                        readed += r;
                        System.out.println("in readeder..." + new String(bytes));

                    }
                }while(readed==0);  //retry read until something has been readed on socket
                    Intent intent = new Intent(IOForRSPGame.READY_BT_MSG);
                    intent.putExtra("move", new String(bytes));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    RSPSocket.bufferedMove = new String(bytes);  //TODO ANONYMUS CLASS STATIC NOT NEEDED??

                    context.sendBroadcast(intent);


                //Thread.currentThread().sleep(256);

            } catch (IOException e) {
                e.printStackTrace();
                BTHandler.setupAllert("ERROR IN WRITE");
            } /*catch (InterruptedException e) {
                e.printStackTrace();
                System.err.println("INTERRUPTED");
            }*/


        }};


    public void abort(){
        this.ioTh.interrupt();
    }


    @Override
    public void sendMove(String s) throws Exception {
        //called
        final String toWrite = s;
        String a= new String(
                s.getBytes("UTF-8"));

        new Thread() {

            @Override
            public void run() {
                int i=0;

                try {
                    outputStream.write(toWrite.getBytes("UTF-8"));
                } catch (IOException e) {
                    e.printStackTrace();
                    BTHandler.setupAllert("ERROR IN WRITE");
                }
                System.out.println(toWrite+"writed");

            }
        }.start();
    }


    @Override
    public String receiveMove() throws Exception {
        return this.bufferedMove;
    }


}
