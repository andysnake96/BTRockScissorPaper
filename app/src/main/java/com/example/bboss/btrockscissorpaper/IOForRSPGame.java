package com.example.bboss.btrockscissorpaper;

/**
 * Created by BBOSS on 25/05/2018.
 */

public interface IOForRSPGame {
    public static String PAPER="PAPER";
    public static String ROCK="ROCK";
    public static String SCISSOR="SCISSOR";
    //COSTANT STRING IN SEND&RECEIVE
    public static String READY_BT_MSG="READY_BT_MSG";
    public void sendMove(String s) throws Exception;
    /*
    SEND => write on socket async!
    TODO IMPLEMENT BROADCASTRECEIVER with READY_BT_MSG req code
    when ready msg on socket (from oth player)==> incoming msg on BroadCastReceiver
    =>switch state in game Activity (waiting->ready!)
    call receive (ONLY WHEN RECEIVED CALLING IN BROADCAST RECEIVER
     */
    public String receiveMove() throws Exception; // RETURN STRING MOVE ONLY IF READY TO READ ON SOCKET
    //TODO FOR CONSTRUCTOR NEEDED BT SOCKET
}
