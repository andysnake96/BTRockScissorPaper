package com.example.bboss.btrockscissorpaper;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;

/**
 * Created by BBOSS on 22/05/2018.
 */

public class BTHandler   {
    BluetoothAdapter bluetoothAdapter;


    public void enableBT(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter==null){
            setupAllert("BT NOT SUPPORTED FROM DEVICE");
        }

    };
    public void findDevices(){};
    public void discovery(){};
    public void connect(){};
    public static void setupAllert(String outputAllert){  //TODO AGGIUSTAMI!
        System.err.println("ALLERT"+outputAllert);
        throw new RuntimeException("ALLERT..."+outputAllert);   //todo remove and update under
        /*
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(outputAllert);
        //alertDialog.setIcon(R.drawable.welcome);

//        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
//            }
//        });

        alertDialog.show();
        */
    }
}
