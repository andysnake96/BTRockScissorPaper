package com.example.bboss.btrockscissorpaper;

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
    private static final int FIX_LEN = 7 ;
    private ImageButton scissor;
    private TextView result;
    private ImageButton paper;
    private ImageView imagineMove;
    private ImageButton stone;
    private ProgressBar pb;
    private TextView info;
    private RSPSocket rspSocket;
    private String myMove;
    private String opponentMove;
    private Button restart;

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
                restart.setEnabled(false);
            }
        });

    }




    @Override
    public void onClick(View v) {
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
            rspSocket.sendMove(myMove+rspSocket.END_MSG);
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
                    opponentMove = intent.getStringExtra("move").replace(rspSocket.END_MSG,"");

                    System.out.println(opponentMove + "ON BROADCAST RECEIVER\n\n ");
                    try {
                        rspSocket.abort();
                    }
                    catch(Exception e) {}
                    whoWin();

                } catch (Exception e) {
                    e.printStackTrace();
                    BTHandler.setupAllert("ERROR IN RECEIVE!");
                }
            }
        }
    };

    private void whoWin() {
        pb.setVisibility(View.INVISIBLE);
        info.setText("");
        switch(opponentMove) {
            case "scissor":
                imagineMove.setImageDrawable(getDrawable(R.id.scissor));
                break;
            case "paper":
                imagineMove.setImageDrawable(getDrawable(R.id.paper));
                break;
            case "stone":
                imagineMove.setImageDrawable(getDrawable(R.id.stone));
                break;
        }
        imagineMove.setVisibility(View.VISIBLE);
        if(opponentMove.equals(myMove)) {
            result.setText(getString(R.string.draw));
        }
        else if((myMove.equals("stone") && opponentMove.equals("scissor")) || (myMove.equals("paper") &&
                    opponentMove.equals("stone")) || (myMove.equals("scissor") && opponentMove.equals("paper"))) {
            result.setText(getString(R.string.resultOk));
        }
        else {
            result.setText(getString(R.string.resultNotOk));
        }
        restart.setEnabled(true);
    }


}
























