package org.cocos2dx.cpp;

import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;

public class ConnectThread extends Thread {
// 此处变量略
public ConnectThread(BluetoothDevice device, BluetoothAdapter mAdapterr) {
        this.mAdapter = mAdapter;
        int sdk = Build.VERSION.SDK_INT;
        if (sdk >= 10) {
            try {
                mySocket = device
                        .createInsecureRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                mySocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public void run() {
        //
        mAdapter.cancelDiscovery();
        try {
             mySocket.connect();
             // 启动接收远程设备发送过来的数据
            connectBluetooth = new ReceiveDatas(mySocket,receiveHandler);
            connectBluetooth.start();
        //输出流         
            mmOutStream = mySocket.getOutputStream();
        } catch (IOException e) {
            // TODO Auto-generated catch block

            try {
                mySocket.close();
             } catch (IOException ee) {
                // TODO Auto-generated catch block
                ee.printStackTrace();
            }

        }

    }

    // 写数据
    /* Call this from the main Activity to send data to the remote device */
    public void sendMessage(String msg) {
        byte[] buffer = new byte[16];
        try {
            if (mmOutStream == null) {
                Log.i("info", "输出流为空");
                return;
            }
            // 写数据
              buffer = msg.getBytes();
             mmOutStream.write(buffer);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (mmOutStream != null) {
                    mmOutStream.flush();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}