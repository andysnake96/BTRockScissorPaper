package com.example.bboss.btrockscissorpaper;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by BBOSS on 22/05/2018.
 */

public class BTHandler {
    BluetoothAdapter bluetoothAdapter;
    public static String actionErrorToast = "ERRORTOAST";
    public static String outputMSGKeyStr = "errormsg";

    public void findDevices() {
    }

    ;

    public void discovery() {
    }

    ;

    public void connect() {
    }

    ;

    public static void setupAllert(String outputAllert, Activity activity) {
        Intent sendError = new Intent(BTHandler.actionErrorToast);
        sendError.putExtra(outputMSGKeyStr, outputAllert);
        activity.sendBroadcast(sendError);

    }
}
