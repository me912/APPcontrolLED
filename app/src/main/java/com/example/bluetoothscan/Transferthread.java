package com.example.bluetoothscan;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Transferthread extends Thread{
    String TAG = "IN Tranfertheard";
    private Handler mHandler;
    public Transferthread(BluetoothSocket mmSocket, Handler myHandler) {
        this.mmSocket = mmSocket;
        this.mHandler = myHandler;
        InputStream tempinput = null;
        OutputStream tempoutput = null;
        try {
            tempinput = mmSocket.getInputStream();
        } catch (IOException e){
            Log.e(TAG, "Transferthread: Get Inputstream failed!");
        }
        this.mmInStream = tempinput;
        try {
            tempoutput = mmSocket.getOutputStream();
        }catch (IOException e){
            Log.e(TAG, "Transferthread: Get Outputstream failed !" );
        }
        this.mmOutStream = tempoutput;
    }

      static interface MessageConstants {
        int MESSAGE_READ = 0;
        int MESSAGE_WRITE = 1;
        int MESSAGE_TOAST = 2;

    }
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private byte[] mmBuffer;

    @Override

    public void run() {
        mmBuffer = new byte[1024];
        int numBytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs.
        while (true) {
            try {
                // Read from the InputStream.
                numBytes = mmInStream.read(mmBuffer);
                // Send the obtained bytes to the UI activity.
                Message readMsg = mHandler.obtainMessage(
                        MessageConstants.MESSAGE_READ, numBytes, -1,
                        mmBuffer);
                readMsg.sendToTarget();
            } catch (IOException e) {
                Log.d(TAG, "Input stream was disconnected", e);
                break;
            }
        }
        super.run();
    }
    public void write(byte[] bytes) {

                try {
                    for(int i=0 ; i<bytes.length; i++) {
                        doSomething(i, bytes);
                        Thread.sleep(10);
                    }
                } catch (Exception e){
                    Log.e(TAG, "write: ", e);
                }


    }
    public void doSomething(int pos,  byte[] bytes){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mmOutStream.write(bytes[pos]);
                    Log.d(TAG, "run: " + bytes[pos]);
                    Message writtenMsg = mHandler.obtainMessage(
                            MessageConstants.MESSAGE_WRITE, -1, -1, bytes);
                    writtenMsg.sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "Error occurred when sending data", e);

                    // Send a failure message back to the activity.
                    Message writeErrorMsg =
                            mHandler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                    Bundle bundle = new Bundle();
                    bundle.putString("toast",
                            "Couldn't send data to the other device");
                    writeErrorMsg.setData(bundle);
                    mHandler.sendMessage(writeErrorMsg);
                }
            }
        }).run();


    }

}
