package com.example.bluetoothscan;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.TypedArrayUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private static final String TAG = "MainActivity";
    UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothAdapter mBluetoothAdapter;
    Button btnEnableDisable_Discoverable;
    private TextView Box;
    private Button sendtextbtn;
    private List<Byte> SENDBLUETOOTH = new ArrayList<>();
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    private BluetoothSocket myBTsocket;
    public DeviceListAdapter mDeviceListAdapter;
    private Transferthread mTrans;
    private final int PERMISSION_REQUEST_COARSE_LOCATION = 0xb01;
    private Handler mHandler;
    private ConnectThread mconnect;
    ListView lvNewDevices;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentTransaction fragmentTransaction;
    private SharedModelMode1 Mymodel;


    public void Mode(View v){
        Fragment fragment = null;
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (v.getId()) {
            case R.id.mode1:
                fragment = new Mode1Fragment();
                break;
            case R.id.mode2:
                fragment = new Mode2Fragment();
                break;
            case R.id.mode3:
                fragment = new Mode2Fragment_();
                break;
            case R.id.mode4:
                fragment = new Mode4Fragment();
                break;
            case R.id.mode5:
                fragment = new Mode5Fragment();
                break;
            default:
                fragment = null;
                break;
        }

        fragmentTransaction.replace(R.id.fragment, fragment);
        fragmentTransaction.commit();
    }
    private class Runable2 implements Runnable{

        private ConnectThread myconnect;
        private Transferthread myTrans;
        private Handler myHandler;
        Runable2(ConnectThread myconnect, Transferthread myTrans, Handler myHandler){
            this.myconnect = myconnect;
            this.myTrans = myTrans;
            this.myHandler = myHandler;
        }
        @Override

        public void run() {
            while(myconnect.getbTSocket() == null);
            myTrans = new Transferthread(myconnect.getbTSocket(), myHandler);
            mTrans = myTrans;
            mTrans.start();
        }
    }
    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    /**
     * Broadcast Receiver for changes made to bluetooth states such as:
     * 1) Discoverability mode on/off or expire.
     */
    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    //Device is in Discoverable Mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
                        break;
                    //Device not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2: Connecting....");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        break;
                }

            }
        }
    };




    /**
     * Broadcast Receiver for listing devices that are not yet paired
     * -Executed by btnDiscover() method.
     */
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
                if(mBTDevices.indexOf(device) == -1) {
                    mBTDevices.add(device);
                }
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                lvNewDevices.setAdapter(mDeviceListAdapter);
            }
        }
    };

    /**
     * Broadcast Receiver that detects bond state changes (Pairing status changes)
     */
    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");
                }
                //case2: creating a bone
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
                }
                //case3: breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
                }
            }
        }
    };



    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver1);
        unregisterReceiver(mBroadcastReceiver2);
        unregisterReceiver(mBroadcastReceiver3);
        unregisterReceiver(mBroadcastReceiver4);
        //mBluetoothAdapter.cancelDiscovery();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        Mymodel = new ViewModelProvider(this).get(SharedModelMode1.class);
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what){
                    case Transferthread.MessageConstants.MESSAGE_WRITE:
                        byte[] writeBuf = (byte[]) msg.obj;
                        // construct a string from the buffer
                        String writeMessage = new String(writeBuf);
                        break;
                    case Transferthread.MessageConstants.MESSAGE_READ:
                        byte[] readBuf = (byte[]) msg.obj;
                        // construct a string from the valid bytes in the buffer
                        String readMessage = new String(readBuf, 0, msg.arg1);
                        Box.setText(readMessage);
                        Log.d(TAG, "handleMessage: " + readMessage);
                        break;
                }
            }
        };
        Mymodel.getShared().observe(this, new Observer<List<Byte>>() {
            @Override
            public void onChanged(List<Byte> bytes) {
                SENDBLUETOOTH = bytes;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Byte[] bytes = SENDBLUETOOTH.toArray(new Byte[SENDBLUETOOTH.size()]);
                        byte[] set = new byte[bytes.length];
                        for(int i=0; i<bytes.length;i++){
                            set[i] = bytes[i].byteValue();
                        }
                        try {
                            mTrans.write(set);
                        }catch (Exception e){
                        }
                    }
                }).run();
            }
    });

        setContentView(R.layout.activity_main);
        Button btnONOFF = (Button) findViewById(R.id.btnONOFF);
        Box = findViewById(R.id.box);
        sendtextbtn = (Button)findViewById(R.id.sendtext);
        btnEnableDisable_Discoverable = (Button) findViewById(R.id.btnDiscoverable_on_off);
        lvNewDevices = (ListView) findViewById(R.id.lvNewDevices);
        mBTDevices = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }
            //Broadcasts when bond state changes (ie:pairing)
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver4, filter);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBTDevices.addAll(mBluetoothAdapter.getBondedDevices());
        Log.d(TAG, "onCreate: " + mBTDevices.toString());
        lvNewDevices.setOnItemClickListener(MainActivity.this);


        btnONOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: enabling/disabling bluetooth.");
                enableDisableBT();
            }
        });


        sendtextbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Byte[] bytes = SENDBLUETOOTH.toArray(new Byte[SENDBLUETOOTH.size()]);
                byte[] set = new byte[bytes.length];
                for(int i=0; i<bytes.length;i++){
                    set[i] = bytes[i].byteValue();
                }
                Log.d(TAG, "onClick: sending massage" + set);
                try {
                    mTrans.write(set);
                    Log.d(TAG, "SEND: "+ set);
                }catch (Exception e){
                    Log.e(TAG, "onClick: ",e);
                }
            }
        });

    }



    public void enableDisableBT(){
        if(mBluetoothAdapter == null){
            Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
        }
        if(!mBluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT: enabling BT.");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
        if(mBluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT: disabling BT.");
            mBluetoothAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }

    }


    public void btnEnableDisable_Discoverable(View view) {
        Log.d(TAG, "btnEnableDisable_Discoverable: Making device discoverable for 300 seconds.");

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        IntentFilter intentFilter = new IntentFilter(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mBroadcastReceiver2,intentFilter);

    }


    public void btnDiscover(View view) {
        checkBTPermissions();
        Log.d(TAG, "btnDiscover: Looking for unpaired devices.");

        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Canceling discovery.");


            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
        if(!mBluetoothAdapter.isDiscovering()){



            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
    }
    /**
     * This method is required for all devices running API23+
     * Android must programmatically check the permissions for bluetooth. Putting the proper permissions
     * in the manifest is not enough.
     *
     * NOTE: This will only execute on versions > LOLLIPOP because it is not needed otherwise.
     */

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //first cancel discovery because its very memory intensive.


        Log.d(TAG, "onItemClick: You Clicked on a device.");
        String deviceName = mBTDevices.get(i).getName();
        String deviceAddress = mBTDevices.get(i).getAddress();

        Log.d(TAG, "onItemClick: deviceName = " + deviceName);
        Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);
        //create the bond.
        //NOTE: Requires API 17+? I think this is JellyBean
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
            Log.d(TAG, "Trying to pair with " + deviceName);
                mBTDevices.get(i).createBond();
                mconnect = new ConnectThread(mBTDevices.get(i), myUUID, mBluetoothAdapter);
            myBTsocket = mconnect.getbTSocket();
            Log.d(TAG, "onItemClick: " + mBTDevices.get(i));


            mconnect.start();
            new Thread(new Runable2(mconnect, mTrans, mHandler)).start();

        }
    }
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }

                break;
        }
    }



    private void checkBTPermissions() {
        LocationManager lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}
        if(!gps_enabled ) {
            Toast.makeText(getApplicationContext(), "Vui lòng mở vị trí", Toast.LENGTH_SHORT).show();
        }
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = super.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_BACKGROUND_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.BLUETOOTH_ADMIN");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.BLUETOOTH_ADMIN}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }


    }





}

