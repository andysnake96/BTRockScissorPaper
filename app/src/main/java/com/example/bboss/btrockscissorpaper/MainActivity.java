package com.example.bboss.btrockscissorpaper;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView textViewInfos;
    private TextView debugTv;
    private Button debugSendBtn;
    private Button btnServer;
    private Button btnClient;
    private String hostKind;
    private ArrayAdapter<String> adapter = null;
    private ListView listViewDiscovered =null;
    private TextView textDiscoveredInfo = null;
    public static final String CLIENT="CLIENT";
    private ProgressBar pb = null;
    private List<BluetoothDevice> discovered = new ArrayList<>();
    private BluetoothDevice debugTargetDevice;  //DEBUG ONLY
    public static final String SERVER="SERVER";
    private static final int REQUEST_ENABLE_BT=1;   //passed with intent to bt os handler...retrived in onREsult...
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private final int DURATION=300;
    protected static final  UUID uuid= UUID.fromString("b8319a04-3632-4d0d-8bd5-47238a404a28");
    protected static RSPSocket rspSocket;
    protected static Activity activityM ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //activityM=this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        btnClient = findViewById(R.id.btnClient);
        btnServer = findViewById(R.id.btnServer);
        listViewDiscovered = findViewById(R.id.discovered);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        listViewDiscovered.setAdapter(adapter);
        textViewInfos = findViewById((R.id.infotx));
        listViewDiscovered.setOnItemClickListener(discoverListener);
        pb = findViewById(R.id.progressBar2);
        textDiscoveredInfo = findViewById(R.id.discoveryInfo);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btnServer.setOnClickListener(this);
        btnClient.setOnClickListener(this);
        this.setUpBT();
        //debug send & receive string on bt socket
        debugSendBtn=findViewById(R.id.debugSendBtn);
        debugTv=findViewById(R.id.debugBTSocket);
        registerReceiver(mReceiver,new IntentFilter(BTHandler.actionErrorToast));  //for errors toast

    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("resume called");
    }
    @Override
    public void onClick(View view){
        btnClient.setEnabled(false);
        btnServer.setEnabled(false);
        switch (view.getId()){
            case R.id.btnClient:
                hostKind=CLIENT;
                break;
            case R.id.btnServer:
                hostKind=SERVER;
                break;
            default:
                throw new IllegalArgumentException("CLICK ONLY SERVER OR CLIENT BTN... :(");

        }
        this.connectTry();
    }
    private void setUpBT(){
        System.out.println("SELECTED..."+hostKind);
        if(bluetoothAdapter==null){
            BTHandler.setupAllert("BT NOT SUPPORTED FROM DEVICE",this );
            finish();
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
        }
    }
    private void connectTry(){

        //start BT and switch in server host logic
        if (hostKind.equals(SERVER)) {  //server
            connectTryServer();

        }
        else {
            connectTryClient();
        }
    }
    private void SampleMSGExcange(){
        try {
            this.rspSocket.sendMove("hello fucking BT word....");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void takeSocket(BluetoothSocket bluetoothSocket){
        System.out.println("SOCKET TAKED!");
        adapter.clear();
        this.textDiscoveredInfo.setVisibility(View.INVISIBLE);
        this.bluetoothSocket = bluetoothSocket;
        this.btnServer.setEnabled(true);
        this.btnClient.setEnabled(true);
        if(this.bluetoothSocket==null){
            BTHandler.setupAllert("SOMETHING HAS GONE WRONG...NULL SOCKET...",this );
            return;   }

        try {
            rspSocket=new RSPSocket(this.bluetoothSocket,this); //debug msg exange
        } catch (IOException e) {
            e.printStackTrace();
        }

            Intent intent = new Intent(this, ActivityGame.class);
          //  intent.putExtra("rspSocket", this.rspSocket);

             startActivity(intent);
    }
    private void connectTryServer() {
        // andrea!
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DURATION);
        startActivityForResult(discoverableIntent, DURATION);


    }

    //BROADCAST RECEIVER 4 CLIENT
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getName() != null) {
                    System.out.println(device.getName());
                    if(discovered.contains(device))
                        return;
                    adapter.add(device.getName());
                    discovered.add(device);
                }
            }
            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
                pb.setVisibility(View.INVISIBLE);
                if(discovered.size() == 0) {
                    textDiscoveredInfo.append(" no device discovered");

                }
                btnClient.setEnabled(true);


            }
            else if (action.equals(BTHandler.actionErrorToast)){
                String outputAllert=intent.getStringExtra(BTHandler.outputMSGKeyStr);
                System.err.println("ALLERT"+outputAllert);
                Toast toast = new Toast(MainActivity.this).makeText(MainActivity.this, outputAllert,3);
                toast.setGravity(Gravity.CENTER,0 ,0);
                toast.show();
                final String error2Show=outputAllert;

                new CountDownTimer(2000, 2000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        System.out.println("tik4");
                    }

                    @Override
                    public void onFinish() {
                        throw new RuntimeException("ALLERT..."+error2Show);
                    }
                }.start();


            }

        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void connectTryClient(){
       /* BluetoothDevice targetDevice=null;
        //getting paierd devices...
        System.out.println("PAIRED DEVICES...");
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        */

        //DISCOVERING OTHER DEVICE...
        // Create a BroadcastReceiver for ACTION_FOUND.


        IntentFilter filterFound = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter filterDiscChanged = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filterFound);
        registerReceiver(mReceiver, filterDiscChanged);

        //getting runtime permission to access course location NEEDED FROM ANDROID IMPLEMENTATION


        adapter.clear();
        discovered.clear();
        int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

        doDiscovery();


    }

    //at click I take device by name
    private AdapterView.OnItemClickListener discoverListener
            = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            bluetoothAdapter.cancelDiscovery();
            BluetoothDevice targetDevice = null;
            String targetDeviceName = (String) adapter.getItem(position);
            for(BluetoothDevice bd: discovered) {
                if(targetDeviceName.equals(bd.getName())) {
                    targetDevice = bd;
                    break;
                }
            }
            System.out.println(targetDevice.getAddress() + "\n"+ targetDevice.getName());
            ClientGetConnection clientGetConnection=new ClientGetConnection(targetDevice,MainActivity.this);
            clientGetConnection.execute(targetDevice);
            BluetoothSocket clientSocket = null;

        }

    };



    private void doDiscovery() {
        // If we're already discovering, stop it
        textDiscoveredInfo.setVisibility(View.VISIBLE);

        pb.setVisibility(View.VISIBLE);
        listViewDiscovered.setVisibility(View.VISIBLE);
        textDiscoveredInfo.setText("device discovered:");
        bluetoothAdapter.startDiscovery();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_ENABLE_BT:
                //result from enabling BT..:D
                if (resultCode== Activity.RESULT_OK){
                    System.out.println("BT ENABLED SUSSFULY");
                }
                else if(resultCode==Activity.RESULT_CANCELED){      //pressed no on enabling bt..
                    BTHandler.setupAllert("ERROR IN ENABLING...:'((", this);
                }
                break;
            case (DURATION):{ //DOCS SAY RESULT OF BT DISCOVERABLE SWITCH USE THIS RET CODE...
                //server calling
                if(resultCode==RESULT_CANCELED){
                    BTHandler.setupAllert("ERROR IN DISCOVERABILITY",this );
                }
                else if (resultCode==DURATION){
                    System.out.println("OK DISCOVERABILITY SWITCH");
                    //ok discoverability=> wait connections
                    new ServerGetConnection(this).execute();    //will be callen with onPostExec takeSocket method

                }
            }


        }

    }







    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
    }
}