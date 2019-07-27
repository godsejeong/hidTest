package com.monster.bluetoothhid;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;

public class BluetoothHidHost {

//    private void BluetoothHidHost(Context context, BluetoothProfile.ServiceListener l) {
//        mContext = context;
//        mServiceListener = l;
//        mAdapter = BluetoothAdapter.getDefaultAdapter();
//
//        IBluetoothManager mgr = mAdapter.getBluetoothManager();
//        if (mgr != null) {
//            try {
//                mgr.registerStateChangeCallback(mBluetoothStateChangeCallback);
//            } catch (RemoteException e) {
//                Log.e(TAG, "", e);
//            }
//        }
//
//        doBind();
//    }
//
//    boolean doBind() {
//        Intent intent = new Intent(IBluetoothHidHost.class.getName());
//        ComponentName comp = intent.resolveSystemService(mContext.getPackageManager(), 0);
//        intent.setComponent(comp);
//        if (comp == null || !mContext.bindServiceAsUser(intent, mConnection, 0,
//                mContext.getUser())) {
//            Log.e(TAG, "Could not bind to Bluetooth HID Service with " + intent);
//            return false;
//        }
//        return true;
//    }

}
