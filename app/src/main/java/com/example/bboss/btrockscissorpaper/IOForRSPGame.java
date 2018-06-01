package com.example.bboss.btrockscissorpaper;

/**
 * Created by BBOSS on 25/05/2018.
 */

public interface IOForRSPGame {
    public static String READY_BT_MSG="READY_BT_MSG";
    public static String CLOSED_SOCKET_MSG="CLOSED";
    public void sendMove(String s) throws Exception;    //only write on socket..
    // another th to read and async notification by broadcast receiver
    public static boolean SECURE=true;
 }
