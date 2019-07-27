package com.monster.bluetoothhid;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;


@SuppressLint("NewApi")
public class HidUtil {

    //	protected String TAG = "HidUtil";
    protected String TAG = HIDAutoConnectedService.TAG;
    Context context;

    private BluetoothAdapter mBtAdapter;
    private static HidUtil instance;
    private BluetoothProfile mBluetoothProfile;
    private final int INPUT_DEVICE = 4;

    public static HidUtil getInstance(Context context) {
        if (null == instance) {
            Log.e("Asdfasdf","Asdfasdfasdkfjhqweyukrgqwuyerg");
            instance = new HidUtil(context);
        }
        return instance;
    }

    private HidUtil(Context context) {
        this.context = context;
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        try {
            mBtAdapter.getProfileProxy(context, mListener,INPUT_DEVICE);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "HidUtil Exception: " + e.getMessage());
        }
    }






    void close() {
        if (null != mBtAdapter && null != mBluetoothProfile) {
            try {
                mBtAdapter.closeProfileProxy(INPUT_DEVICE, mBluetoothProfile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private BluetoothProfile.ServiceListener mListener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            Log.i(TAG, "mConnectListener onServiceConnected " + "," + profile);
            // BluetoothProfile proxy这个已经是BluetoothInputDevice类型了
            try {
                //if (profile == INPUT_DEVICE) {
                    mBluetoothProfile = proxy;
//                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "mListener Exception:" + e.getMessage());
            }
        }

        @Override
        public void onServiceDisconnected(int profile) {
            Log.i(TAG, "mConnectListener onServiceDisconnected");
        }
    };

    /**
     * 获取BluetoothProfile中hid的profile，"INPUT_DEVICE"类型隐藏，需反射获取
     *
     * @return
     */
    @SuppressLint("NewApi")
    public static int getInputDeviceHiddenConstant() {
        Class<BluetoothProfile> clazz = BluetoothProfile.class;
        for (Field f : clazz.getFields()) {
            int mod = f.getModifiers();
            if (Modifier.isStatic(mod) && Modifier.isPublic(mod)
                    && Modifier.isFinal(mod)) {
                try {
                    if (f.getName().equals("INPUT_DEVICE")) {
                        return f.getInt(null);
                    }
                } catch (Exception e) {
                }
            }
        }
        return -1;
    }

    public boolean isBonded(BluetoothDevice device) {
        int state = device.getBondState();
        if (state == BluetoothDevice.BOND_BONDED /*|| state == BluetoothDevice.BOND_BONDING*/) {
            return true;
        }
        return false;
    }

    public boolean isConnected(BluetoothDevice device) {
        Log.i(TAG, "isconneted device:" + device + ",mBluetoothProfile:"
                + mBluetoothProfile);
        if (mBluetoothProfile == null)
            return false;
        List<BluetoothDevice> connectedDevices = mBluetoothProfile
                .getConnectedDevices();
        if (connectedDevices.contains(device)) {
            return true;
        }
        return false;
    }

    public BluetoothDevice getConnectedDevice(String name) {

        Log.i(TAG, "getConnectedDevice name:" + name);

        if (mBluetoothProfile == null || name == null)
            return null;

        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            String addr = device.getName();

            if (addr != null && addr.equals(name)) {
                return device;
            }
        }
        return null;
    }


    public boolean connect(final BluetoothDevice device) {
        Log.i(TAG, "connect device:" + device);
        try {


            Method method = mBluetoothProfile.getClass().getMethod("connect",
                    new Class[] { BluetoothDevice.class });
            return (Boolean) method.invoke(mBluetoothProfile, device);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "connect device Exception:" + e.getMessage());
            return false;
        }
    }

    public boolean connect1(final BluetoothDevice device) {
        Log.i(TAG, "connect device:" + device);
        try {
//            Method declaredMethod = Class.forName(" android.bluetooth.BluetoothHidHost").getDeclaredClasses()[0].getDeclaredMethod("connect", new Class[]{BluetoothDevice.class});
//            declaredMethod.setAccessible(true);

            Method method = mBluetoothProfile.getClass().getMethod("connect", new Class[]{BluetoothDevice.class});

            Log.e("etst", String.valueOf((Boolean)method.invoke(mBluetoothProfile, device)));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "connect device Exception:" + e.getMessage());
            return false;
        }
    }


    public void disConnect(BluetoothDevice device) {
        Log.i(TAG, "disConnect device:" + device);
        try {
            if (device != null) {
                Method method = mBluetoothProfile.getClass().getMethod(
                        "disconnect", new Class[] { BluetoothDevice.class });
                method.invoke(mBluetoothProfile, device);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "disConnect device Exception:" + e.getMessage());
        }
    }

    public void setPriority(BluetoothDevice device, int priority) {
        Log.i(TAG, "setPriority device:" + device);
        try {
            if (device != null) {
                Method method = mBluetoothProfile.getClass().getMethod(
                        "setPriority",
                        new Class[] { BluetoothDevice.class, int.class });
                method.invoke(mBluetoothProfile, device, priority);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "setPriority device Exception:" + e.getMessage());
        }
    }


    public void pair(BluetoothDevice device) {
        Log.i(TAG, "pair device:" + device);
        Method createBondMethod;
        try {
            createBondMethod = BluetoothDevice.class.getMethod("createBond");
            createBondMethod.invoke(device);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "pair device Exception:" + e.getMessage());
        }

    }


    public void unPair(BluetoothDevice device) {
        Method createBondMethod;
        try {
            createBondMethod = BluetoothDevice.class.getMethod("removeBond");
            createBondMethod.invoke(device);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "unPair device Exception:" + e.getMessage());
        }

    }
}
