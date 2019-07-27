package com.monster.bluetoothhid;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class HIDConnectedSuccessActivity extends Activity{

    String mDeviceName;
    String mDeviceAdresss;
    private static final int MSG_FINISH = 0x10;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_FINISH:
                    finish();
                    break;
                default:
                    break;
            }
        };
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDeviceName = getIntent().getStringExtra("name");
        mDeviceAdresss = getIntent().getStringExtra("address");
        String msg = "이름 : " + mDeviceName + "   맥주소 :  "+ mDeviceAdresss;
        setTitle(msg);
        Message m = Message.obtain(mHandler, MSG_FINISH);
        mHandler.sendMessageDelayed(m, 5000);
    }


    @Override
    protected void onResume() {
        super.onResume();
        AlertDialog.Builder buidler = new AlertDialog.Builder(this);
        buidler.setTitle("HID 연결 성공");
        String msg = "이름 : " + mDeviceName + "   맥주소 :  "+ mDeviceAdresss;
        buidler.setMessage(msg);
        buidler.setCancelable(true);
//		buidler.show();
    }
}
