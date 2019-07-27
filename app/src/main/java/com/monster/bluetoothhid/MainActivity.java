package com.monster.bluetoothhid;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.RequiresApi;

public class MainActivity extends Activity implements OnClickListener {
    public String TAG = "wlj_debug";
    // private String HID_NAME = "Bluetooth Mouse"; // 连接的蓝牙设备名
    // private String HID_ADDR = "20:14:18:19:00:27"; //连接的蓝牙设备地址

    private String HID_NAME = "MONSTER"; // 连接的蓝牙设备名
    private String HID_ADDR = "34:03:DE:4D:27:C1"; // 连接的蓝牙设备地址
    private Button mBtnConnect, mBtnDisconnect, mBtnTest;
    private BluetoothAdapter mBluetoothAdapter;
    private BlueBroadcastReceiver mBroadcastReceiver;
    private BluetoothDevice mConnectDevice;
    private HidUtil mHidUtil;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] PERMISSIONS = {Manifest.permission.BLUETOOTH,Manifest.permission.BLUETOOTH_ADMIN,Manifest.permission.RECEIVE_BOOT_COMPLETED,Manifest.permission.WRITE_SETTINGS};

        initView();
        initBlue();

    }

    private void initView() {
        mBtnConnect = (Button) findViewById(R.id.btn_connect);
        mBtnConnect.setOnClickListener(this);
        mBtnDisconnect = (Button) findViewById(R.id.btn_dis);
        mBtnDisconnect.setOnClickListener(this);
        mBtnTest = (Button) findViewById(R.id.btn_test);
        mBtnTest.setOnClickListener(this);
        mBtnTest.requestFocus();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        if (mConnectDevice == null) {
            // 刚进入activity可能获取不到，因为getProfileProxy是异步的，可能还没有成功。
//			mConnectDevice = mHidUtil.getConnectedDevice(HID_ADDR);
//			Log.i(TAG, "getConnected device:" + mConnectDevice);
        }
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this,"승인이 허가되어 있습니다.",Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(this,"아직 승인받지 않았습니다.",Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//		unregisterReceiver(mBroadcastReceiver);
        mHidUtil.close();
    }

    private void initBlue() {
        // 초기화
        mBroadcastReceiver = new BlueBroadcastReceiver();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "블루투스 기능이 지원되지 않음", Toast.LENGTH_LONG).show();
            // 블투 ㄴ 지원
            return;
        }
        // 블루투스가 꺼져있으면 실행
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
        // 블투4.0이상 기기부터 지원됨
        if (Build.VERSION.SDK_INT >= 17) {
            mHidUtil = HidUtil.getInstance(this);
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter
                .addAction("android.bluetooth.input.profile.action.CONNECTION_STATE_CHANGED");
		this.registerReceiver(mBroadcastReceiver, intentFilter);
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.btn_connect:
                mConnectDevice = mHidUtil.getConnectedDevice(HID_NAME);

                if (mConnectDevice == null){
                    Log.e("asdfasdf", "Asdfasdfasdf");
                mBluetoothAdapter.startDiscovery();
                }else {
                Log.e("asdfasdf","1234141234");
				if(mHidUtil.connect1(mConnectDevice)){
                    Toast.makeText(this,"연결완료",Toast.LENGTH_LONG).show();
                }else{
				    Toast.makeText(this,"오류발생",Toast.LENGTH_LONG).show();
                }
			}
                break;
            case R.id.btn_dis:
			mConnectDevice = mHidUtil.getConnectedDevice(HID_NAME);// 先获取连接的蓝牙设备
			Log.d(TAG, "mConnectDevice ... " + mConnectDevice);
			if (mConnectDevice != null) {
				mHidUtil.disConnect(mConnectDevice);
			}
                stopService(new Intent(this, HIDAutoConnectedService.class));
                break;
            case R.id.btn_test:
			Toast.makeText(MainActivity.this, "테스트 버튼이 클릭되었다",
					Toast.LENGTH_SHORT).show();
			Toast.makeText(this, "search", Toast.LENGTH_SHORT).show();
			startService(new Intent(this, HIDAutoConnectedService.class));
                startActivity(new Intent(this, HIDConnectedSuccessActivity.class));
                break;
            default:
                break;
        }
    }

    private class BlueBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive:" + action);
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                // 通过广播接收到了BluetoothDevice
                final BluetoothDevice device = (BluetoothDevice) intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device == null)
                    return;
                String btname = device.getName();
                String address = device.getAddress();
                // 09-01 14:36:27.151: I/wlj_debug(4025): bluetooth
                // name:KMSWand,address:00:E0:4C:00:04:D6
                Log.i(TAG, "bluetooth ww name:" + btname + ",address:"
                        + address);
                if ((address != null && address.equals(HID_ADDR))
                        || (btname != null && btname.equals(HID_NAME))) {
                    mConnectDevice = device;
                    mBluetoothAdapter.cancelDiscovery();
                    if (!mHidUtil.isBonded(device)) {
                        mHidUtil.pair(device);
                    } else {
                        mHidUtil.connect(device);
                    }
                }
            } else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name = device.getName();
                String address = device.getAddress();
                Log.i(TAG, "name:" + name + ",address:" + address
                        + ",bondstate:" + device.getBondState());
                if ((address != null && address.equals(HID_ADDR))
                        || (name != null && name.equals(HID_NAME))) {
                    if (device.getBondState() == BluetoothDevice.BOND_BONDED)
                        mHidUtil.connect(device);
                }
            } else if (action
                    .equals("android.bluetooth.input.profile.action.CONNECTION_STATE_CHANGED")) {
                int state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, 0);
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.i(TAG, "state=" + state + ",device=" + device);
                if (state == BluetoothProfile.STATE_CONNECTED) {
                    Toast.makeText(MainActivity.this,
                            "연결 성공", Toast.LENGTH_LONG)
                            .show();
                } else if (state == BluetoothProfile.STATE_DISCONNECTED) {
                    Toast.makeText(MainActivity.this,"연결 실패",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}