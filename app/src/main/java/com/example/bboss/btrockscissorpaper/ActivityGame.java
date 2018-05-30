package com.example.bboss.btrockscissorpaper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.bboss.btrockscissorpaper.R;

import org.w3c.dom.Text;

import static java.lang.Thread.sleep;

public class ActivityGame extends Activity implements  View.OnClickListener {
    private ImageButton scissor;
    private TextView result;
    private ImageButton paper;
    private ImageView imagineMove;
    private ImageButton stone;
    private ProgressBar pb;
    private TextView info;
    private RSPSocket rspSocket;
    private String myMove;
    private String opponentMove=null;
    private Button restart;
    private ImageButton imageButtonPressed;
    private int win=0;
    private int lose=0;
    private int draw=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        /*Bundle extras = getIntent().getExtras();
        if (extras != null) {
            rspSocket = (RSPSocket) extras.getParcelable("rspSocket");
        }*/
        rspSocket = MainActivity.rspSocket;
        info = findViewById(R.id.infoText);
        imagineMove = findViewById(R.id.move);
        result = findViewById(R.id.result);
        pb = findViewById(R.id.waitOpponent);
        scissor = findViewById(R.id.scissor);
        paper = findViewById(R.id.paper);
        stone = findViewById(R.id.stone);
        stone.setOnClickListener(this);
        paper.setOnClickListener(this);
        scissor.setOnClickListener(this);
        info.setText("wait move");
        restart = findViewById(R.id.restart);
        restart.setEnabled(false);
        IntentFilter filterMSGSocket = new IntentFilter(IOForRSPGame.READY_BT_MSG);
        registerReceiver(mReceiver, filterMSGSocket);

        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stone.setEnabled(true);
                paper.setEnabled(true);
                scissor.setEnabled(true);
                result.setText("");
                myMove = null;
                opponentMove= null;
                imagineMove.setVisibility(View.INVISIBLE);
                imageButtonPressed.clearColorFilter();
                restart.setEnabled(false);
            }
        });

    }




    @Override
    public void onClick(View v) {
        imageButtonPressed = (ImageButton)v;
        imageButtonPressed.setBackgroundColor(android.R.color.holo_green_light);

        stone.setEnabled(false);
        paper.setEnabled(false);
        scissor.setEnabled(false);
        pb.setVisibility(View.VISIBLE);
        info.setText(getString(R.id.waitOpponent));
        switch (v.getId()) {
            case R.id.scissor:
                myMove = "scissor";
                break;
            case R.id.stone:
                myMove = "stone";
                break;
            case R.id.paper:
                myMove = "paper";
                break;
        }
        sendMove(myMove);
    }

    private void sendMove(String myMove) {

        try {
            rspSocket.sendMove(myMove);
            rspSocket.read();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(IOForRSPGame.READY_BT_MSG)) {   //received msg from socket (reader service has sent
                //broadcast msg... debug set text view...
                try {
                    opponentMove = intent.getStringExtra("move");

                    System.out.println(opponentMove + "ON BROADCAST RECEIVER\n\n ");


                    whoWin();
                    opponentMove=null;
                } catch (Exception e) {
                    e.printStackTrace();
                    BTHandler.setupAllert("ERROR IN RECEIVE!");
                }
            }
        }
    };

    @SuppressLint("ResourceType")
    private void whoWin() {
        if(opponentMove==null){
            System.err.println("error in receive move");
            return;
        }
        pb.setVisibility(View.INVISIBLE);
        info.setText("");
        //start with for padding inserted misteriusly from some devices
        if(opponentMove.startsWith("scissor"))
                imagineMove.setImageDrawable(getDrawable(R.drawable.forbice));

        else if(opponentMove.startsWith("paper"))
                imagineMove.setImageDrawable(getDrawable(R.drawable.carta));

        else if(opponentMove.startsWith("stone"))
                imagineMove.setImageDrawable(getDrawable(R.drawable.sasso));

        else
            System.err.println("invalid string received");
        imagineMove.setVisibility(View.VISIBLE);
        if(opponentMove.startsWith(myMove)) {
            result.setText(getString(R.string.draw));
            draw++;
        }
        else if((myMove.startsWith("stone") && opponentMove.startsWith("scissor")) || (myMove.startsWith("paper") &&
                    opponentMove.startsWith("stone")) || (myMove.startsWith("scissor") && opponentMove.startsWith("paper"))) {
            result.setText(getString(R.string.resultOk));
            win++;
        }
        else {
            result.setText(getString(R.string.resultNotOk));
            lose++;
        }
        restart.setEnabled(true);
    }


}
























