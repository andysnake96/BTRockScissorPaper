package com.example.bboss.btrockscissorpaper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.Thread.sleep;

public class ActivityGame extends Activity implements  View.OnClickListener {
    private ImageButton scissor;
    private TextView result;
    private TextView winTw;
    private TextView drawTw;
    private TextView loseTw;
    private ImageButton paper;
    private ImageView imagineMove;
    private TextView opponentMoveTv;
    private ImageButton stone;
    private ProgressBar pb;

    private RSPSocket rspSocket;
    private String myMove;
    private String opponentMove=null;
    private Button restart;
    private ImageButton imageButtonPressed;
    private Integer win=0;
    private Integer lose=0;
    private Integer draw=0;
    final float fixedScale=1.5f;
    protected  static Activity contexG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contexG=this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_game);
        /*Bundle extras = getIntent().getExtras();
        if (extras != null) {
            rspSocket = (RSPSocket) extras.getParcelable("rspSocket");
        }*/
        rspSocket = MainActivity.rspSocket;
        opponentMoveTv= findViewById(R.id.opponentInfo);
        winTw= findViewById(R.id.win);
        loseTw= findViewById(R.id.lose);
        drawTw= findViewById(R.id.draw);
        imagineMove = findViewById(R.id.move);
        result = findViewById(R.id.result);
        pb = findViewById(R.id.waitOpponent);
        scissor = findViewById(R.id.scissor);
        paper = findViewById(R.id.paper);
        stone = findViewById(R.id.stone);
        stone.setOnClickListener(this);
        paper.setOnClickListener(this);
        scissor.setOnClickListener(this);

        restart = findViewById(R.id.restart);
        restart.setEnabled(false);
        IntentFilter filterMSGSocket = new IntentFilter(IOForRSPGame.READY_BT_MSG);
        registerReceiver(mReceiver, filterMSGSocket);
        registerReceiver(mReceiver,new IntentFilter(BTHandler.actionErrorToast));  //for errors toast
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartMetch();
            }
        });

    }


    private void restartMetch(){
        stone.setEnabled(true);
        paper.setEnabled(true);
        scissor.setEnabled(true);
        result.setText("");
        myMove = null;
        opponentMove= null;
        //reset setting button pressed
        imagineMove.setVisibility(View.INVISIBLE);
        opponentMoveTv.setVisibility(View.INVISIBLE);
        imageButtonPressed.setAlpha(1f);
        imageButtonPressed.setScaleX(1);
        imageButtonPressed.setScaleY(1);

        restart.setEnabled(false);
    }
    @Override
    public void onClick(View v) {
        imageButtonPressed = (ImageButton)v;

        imageButtonPressed.setScaleX(fixedScale);
        imageButtonPressed.setScaleY(fixedScale);
        imageButtonPressed.setAlpha(0.7f);
        stone.setEnabled(false);
        paper.setEnabled(false);
        scissor.setEnabled(false);
        pb.setVisibility(View.VISIBLE);

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

                    opponentMove = intent.getStringExtra("move");

                    System.out.println(opponentMove + "ON BROADCAST RECEIVER\n\n ");

                    if(opponentMove.equals(IOForRSPGame.CLOSED_SOCKET_MSG)){
                        //socketClosed other side case=>close !
                        finish();
                    }
                    whoWin();
                    opponentMove = null;


            } else if (action.equals(BTHandler.actionErrorToast)) {
                String outputAllert = intent.getStringExtra(BTHandler.outputMSGKeyStr);
                System.err.println("ALLERT" + outputAllert);
                Toast toast = new Toast(ActivityGame.this).makeText(ActivityGame.this, outputAllert, 3);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                final String error2Show = outputAllert;

                new CountDownTimer(2000, 2000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        System.out.println("tik4");
                    }

                    @Override
                    public void onFinish() {
                        throw new RuntimeException("ALLERT..." + error2Show);
                    }
                }.start();


            }

        }
    };


         private void whoWin() {
            if (opponentMove == null) {
                System.err.println("error in receive move");
                return;
            }
            pb.setVisibility(View.INVISIBLE);

            //start with for padding inserted misteriusly from some devices
            if (opponentMove.startsWith("scissor"))
                imagineMove.setImageDrawable(getDrawable(R.drawable.forbice));

            else if (opponentMove.startsWith("paper"))
                imagineMove.setImageDrawable(getDrawable(R.drawable.carta));

            else if (opponentMove.startsWith("stone"))
                imagineMove.setImageDrawable(getDrawable(R.drawable.sasso));

            else
                System.err.println("invalid string received");
            opponentMoveTv.setVisibility(View.VISIBLE);
            imagineMove.setVisibility(View.VISIBLE);
            if (opponentMove.startsWith(myMove)) {
                result.setText(getString(R.string.draw));
                draw++;
            } else if ((myMove.startsWith("stone") && opponentMove.startsWith("scissor")) || (myMove.startsWith("paper") &&
                    opponentMove.startsWith("stone")) || (myMove.startsWith("scissor") && opponentMove.startsWith("paper"))) {
                result.setText(getString(R.string.resultOk));
                win++;
            } else {
                result.setText(getString(R.string.resultNotOk));
                lose++;
            }
            winTw.setText(win.toString());
            loseTw.setText(lose.toString());
            drawTw.setText(draw.toString());

            restart.setEnabled(true);
            //after midle time will be reset the match screen

            new CountDownTimer(2300, 2300) {
                @Override
                public void onTick(long millisUntilFinished) {
                    System.out.println("tik4");
                }

                @Override
                public void onFinish() {
                    System.out.println("tik4");
                    restartMetch();
                }
            }.start();
        }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mReceiver);
        this.rspSocket.abort();
        System.out.println("ACTIVITY GAME FINISHED...");
    }
}
























