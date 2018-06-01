package com.example.bboss.btrockscissorpaper;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by BBOSS on 27/05/2018.
 */

public class RSPSocket implements IOForRSPGame{

    BluetoothSocket bluetoothSocket ;
    InputStream inputStream;
    OutputStream outputStream;
    Reader ioTh;
    Context context;
    int MAX_2READ=8;


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
        implement reader service...

        send broadcast msg with code MSG_RECEV when read return >0
        string readed in intent sended to broadcast receiver
        HANDLED CALL READ NOT READ ALL MSG IN 1 CALL
         */


        @Override
        public void run() {
            int readed,r;
            readed=r=0;

            //now receive from socket
            byte[] bytes = new byte[MAX_2READ];

            try {
                int c=0;
                do {
                    // cycle until a msg has arrived on socket or socket has benne closed ...
                    if(!bluetoothSocket.isConnected()){     //check socket alive
                        System.out.println("socket close before msg arrived(or send)..");
                        bytes= IOForRSPGame.CLOSED_SOCKET_MSG.getBytes();   //set closed socket str
                        break;
                    }
                    while (inputStream.available() > 0) {
                        //HANDLED CALL READ NOT READ ALL MSG IN 1 CALL

                        r = inputStream.read(bytes, readed, MAX_2READ - readed);

                        if(r==-1)   //stream closed || EOF reatched ... :(
                        {
                            System.err.println("stream closed or finished");
                            bytes= IOForRSPGame.CLOSED_SOCKET_MSG.getBytes();   //set closed socket str
                            break;
                        }
                        readed += r;
                        System.out.println("in readeder..." + new String(bytes));

                    }
                }while(readed==0);  //try read until something has been written on socket

                //Thread.currentThread().sleep(256);

            } catch (IOException e) {
                e.printStackTrace();

                BTHandler.setupAllert("ERROR IN READ",ActivityGame.contexG);

            }
            Intent intent = new Intent(IOForRSPGame.READY_BT_MSG);
            intent.putExtra("move", new String(bytes));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.sendBroadcast(intent);
        }};


    public void abort(){
        //this.ioTh.interrupt();
        try {
/*
            this.inputStream.close();
            this.outputStream.close();*/
            this.bluetoothSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("error in close... now socket=");

        }
        finally {
            this.bluetoothSocket=null;
        }
        System.out.println("succesful closed socket bt");
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
                    if(!bluetoothSocket.isConnected()) {
                        //bt socket closed caused other player.. return back
                        System.out.println("bt socket closed (probably other player disconnected..");
                        Intent intent = new Intent(IOForRSPGame.READY_BT_MSG);
                        intent.putExtra("move",
                                new String(IOForRSPGame.CLOSED_SOCKET_MSG.getBytes()));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.sendBroadcast(intent);
                        return;
                    }
                    BTHandler.setupAllert("ERROR IN WRITE",ActivityGame.contexG );
                }
                System.out.println(toWrite+"writed");

            }
        }.start();
    }




}
