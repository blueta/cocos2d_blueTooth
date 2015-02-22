package org.cocos2dx.cpp;

import java.io.IOException;
import java.io.InputStream;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class ReceiveDatas extends Thread {
	private BluetoothSocket mmSocket = null;
	private InputStream mmInStream; 

	public ReceiveDatas(BluetoothSocket socket) {
		mmSocket = socket;
		InputStream tempIn = null;

		// 获取输入流
		try {
			tempIn = socket.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		mmInStream = tempIn;
	}

	@Override
	public void run() {
		byte[] buffer = new byte[1024];// 缓冲数据流
		int bytes;// 返回读取到的数据
		// 监听输入流
		while (true) {
			try {
				bytes = mmInStream.read(buffer);
				//String receivedMsg = buffer;
				//Log.d("received", buffer);
				// 此处处理数据……

				//} else {
				Log.i("info", "异常");
				//}
			} catch (IOException e) {
				try {
					if (mmInStream != null) {
						mmInStream.close();
					}
					Log.i("info", "异常");
					break;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

		try {
			Thread.sleep(50);// 延迟
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}