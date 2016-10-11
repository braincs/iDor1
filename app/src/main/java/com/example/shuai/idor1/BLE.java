package com.example.shuai.idor1;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.DateFormat;
import android.os.Handler;
import android.support.annotation.BoolRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Shuai on 16/10/10.
 */
public class BLE extends AppCompatActivity {


    private static BluetoothAdapter mBluetoothAdapter;

    private static boolean SCAN_STATE = false;

    private static boolean SCAN_RESULT = false;
    private static int SCAN_PERIOD = 500;

    private static Handler mHandler = new Handler();
    /**
     * BLE_Init(): initialisation of BLE scan
     */
    public static Boolean isSupported(Context context) {

        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {

            SC_Utils.toastMessage(context, "蓝牙设备不支持");
            return false;
        }

        return true;
    }

    /**
     * 检测蓝牙状态
     * @param context 环境
     * @return 蓝牙当前状态
     */
    public static boolean isON(Context context){

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            return false;
        }

        return true;
    }

    public static void scanDevices(boolean enable){
        if (enable){
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    SCAN_STATE = false;

                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                    invalidateOptionsMenu();
//                    setTitle("扫描完成");
                    Log.d("TAg","bleScanList = "+bleScanList.toString());
                    SCAN_RESULT = true;
                }
            }, SCAN_PERIOD);
            SCAN_STATE = true;
            SCAN_RESULT = false;
            mBluetoothAdapter.startLeScan(mLeScanCallback);

        } else {
            SCAN_STATE = false;
            SCAN_RESULT = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    // Device scan callback.
    static HashMap<String, Integer> bleScanList = new HashMap<String, Integer>();
    public static HashMap<String, Integer> tagList = new HashMap<String, Integer>();
    private static BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    String MAC = device.getAddress();
////                    String Name = device.getName();
//                    ArrayList<String[]> list = new ArrayList();
//                    String[] macRssi = new String[]{MAC,Integer.toString(rssi)};
//                    list.add(macRssi);

//                    SC_Utils.debug(macRssi[0]+", "+ macRssi[1]);


                    if(bleScanList.containsKey(MAC)){//有历史存储MAC
                        bleScanList.remove(MAC);
                        bleScanList.put(MAC,rssi);
                    }else {
                        bleScanList.put(MAC,rssi);
                    }
                    /*runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            mLeDeviceListAdapter.addDevice(device);
//                            mLeDeviceListAdapter.notifyDataSetChanged();
                            Log.d("debug",device.toString());

                            if (!bleList.contains(device.toString()))
                                bleList.add(device.toString());

                            bleListView.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                                    android.R.layout.simple_list_item_1,bleList));
                        }
                    });*/
                }
            };


    public static boolean isResultsAvailable(){
        return SCAN_RESULT;
    }
}
