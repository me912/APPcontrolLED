package com.example.bluetoothscan;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Adapter;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.UUID;

public class ConnectThread extends Thread  {
    private BluetoothSocket bTSocket;
    private BluetoothDevice btDevice;
    private BluetoothAdapter btAdapter;
    private static Boolean Isconnect = false;
    private UUID mUUID;
    public ConnectThread(BluetoothDevice btDevice, UUID mUUID, BluetoothAdapter mAdapter) {
        this.btDevice = btDevice;
        this.mUUID = mUUID;
        this.btAdapter = mAdapter;
    }

    public BluetoothSocket getbTSocket() {
        return bTSocket;
    }

    public void Thread(BluetoothDevice bTDevice, UUID mUUID){
        this.btDevice = btDevice;
        this.mUUID = mUUID;
    }

    @Override
    public void run() {
        connect();
        super.run();
    }

    private boolean connect() {
        BluetoothSocket temp;
        btAdapter.cancelDiscovery();
        try {
            temp =(BluetoothSocket)btDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(btDevice,1);
        } catch (Exception e) {
            Log.e("CONNECTTHREAD","Could not create RFCOMM socket:" + e.toString());
            return false;
        }

        try {

            temp.connect();
        } catch(IOException e) {
            Log.e("CONNECTTHREAD", "Could not connect: " + e.toString());
            Log.e("CONNECTTHREAD", btDevice.toString() + mUUID.toString());
            try {
                temp.close();
            } catch(IOException close) {
                Log.d("CONNECTTHREAD", "Could not close connection:" + e.toString());
                return false;
            }
        }
        bTSocket = temp;
        return true;
    }


    public boolean cancel() {
        try {
            bTSocket.close();
        } catch(IOException e) {
            Log.d("CONNECTTHREAD","Could not close connection:" + e.toString());
            return false;
        }
        return true;
    }
}
