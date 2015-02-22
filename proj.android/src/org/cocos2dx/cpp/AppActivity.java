/****************************************************************************
Copyright (c) 2008-2010 Ricardo Quesada
Copyright (c) 2010-2012 cocos2d-x.org
Copyright (c) 2011      Zynga Inc.
Copyright (c) 2013-2014 Chukong Technologies Inc.
 
http://www.cocos2d-x.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
****************************************************************************/
package org.cocos2dx.cpp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

import org.cocos2dx.lib.Cocos2dxActivity;

import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AppActivity extends Cocos2dxActivity {
	public static Activity _activiy;

	private List<String> devices;
	private List<BluetoothDevice> deviceList;
	private final String lockName = "BOLUTEK";
	    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        deviceList = new ArrayList<BluetoothDevice>();
        devices = new ArrayList<String>();
      
	    _activiy = this;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
    
    
	//prevent 
	private boolean isLock(BluetoothDevice device) {
        boolean isLockName = (device.getName()).equals(lockName);
        boolean isSingleDevice = devices.indexOf(device.getName()) == -1;
        return isLockName && isSingleDevice;
    }
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				Log.d("OnReceive", device.getName());
				if (isLock(device)) {
                    devices.add(device.getName());
                }
                deviceList.add(device); 
                if(device.getName().equalsIgnoreCase("Windows Phone")) {
                	Log.d("connecting","connecting");
                	try {
						connectDevice(device);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
                
			} else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
				// 状态改变的广播  
	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);  
	            if (device.getName().equalsIgnoreCase("Windows Phone")) {   
	                int connectState = device.getBondState();  
	                switch (connectState) {  
	                    case BluetoothDevice.BOND_NONE:  
	                        break;  
	                    case BluetoothDevice.BOND_BONDING:  
	                        break;  
	                    case BluetoothDevice.BOND_BONDED:  
	                        try {  
	                            // 连接  
	                            connectDevice(device);  
	                        } catch (IOException e) {  
	                            e.printStackTrace();  
	                        }  
	                        break;  
	                }  
	            }  
				
			}                
		}
		
	};
	
	public void connect(BluetoothDevice device) throws IOException {
		BluetoothSocket socket = null;
		Method m;
		boolean connected = false;
        try {
            m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
            try {
                socket = (BluetoothSocket) m.invoke(device, 1);
            
                try {
                    socket.connect();
                    connected = true;
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
		//final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
		//UUID uuid = UUID.fromString(SPP_UUID);
	    //BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuid);
	    //socket.connect();
	    
	   Log.d("Connected","Connected");
	   if(connected) {
		   
	   }
	   socket.close();
	}
	
	
	//public synchronized void connect(BluetoothDevice device) {
	//      connectThread = new ConnectThread(device, this, adapter, handler,handlerUpdate);
 //       connectThread.start();
 //   }

	
	private byte[] getHexBytes(String message) {
	    int len = message.length() / 2;
	    char[] chars = message.toCharArray();
	    String[] hexStr = new String[len];
	    byte[] bytes = new byte[len];
	    for (int i = 0, j = 0; j < len; i += 2, j++) {
	        hexStr[j] = "" + chars[i] + chars[i + 1];
	        bytes[j] = (byte) Integer.parseInt(hexStr[j], 16);
	    }
	    return bytes;
	}
	
	public void connectDevice(BluetoothDevice device) throws IOException {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		adapter.cancelDiscovery();
		int connectState = device.getBondState();
		switch (connectState) {
		// 未配对
		case BluetoothDevice.BOND_NONE:
			// 配对
			try {
				Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
				createBondMethod.invoke(device);
			} catch (Exception e) { 
				e.printStackTrace();
			}
			break;
			// 已配对
		case BluetoothDevice.BOND_BONDED:
			try {
				// 连接
				connect(device);
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;

		}

	}
	
	public static Object alertJNI() {
	    String tag = "JniTest";
	    String message = "I've been called from C++";
	    Log.d(tag, "Showing alert dialog: " + message);
	    return _activiy;	      
	}
	
	
	private int REQUEST_ENABLE_BT = 0x01;
	private String tag = "blue tooth debug.";
	public void openBlueTooth() {
		//get device and start bluetooth service.
	    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
	    if(adapter == null) {
	    	Log.d(tag, "Device does not support bluetooth.");	    	
	    }
	    if(!adapter.isEnabled()) {
	      Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	      startActivityForResult(intent, REQUEST_ENABLE_BT);
	      //adapter.enable();
	    }	    
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){  
		if(requestCode == REQUEST_ENABLE_BT){  
			if(resultCode == RESULT_OK){  
				//blueTooth turned on.
				Log.d("Turned on.", "BlueTooth enabled.");
				//search devices.
				searchBlueToothDevices();	            	  
			}  
		}  
	}  
	
	public void searchBlueToothDevices() {
		Log.d("Searching ", "Searching devices.");
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		//get paired devices.	    
		Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
			// Loop through paired devices
			for (BluetoothDevice device : pairedDevices) {
				Log.d("Paired Devices ", device.getName());
				if (isLock(device)) {
					devices.add(device.getName());
				}
				deviceList.add(device);
				if(device.getName().equalsIgnoreCase("Windows Phone")) {
					Log.d("connecting","connecting");
					adapter.cancelDiscovery();
					// 获取蓝牙设备的连接状态
					int connectState = device.getBondState();
					switch (connectState) {
					// 未配对
					case BluetoothDevice.BOND_NONE:
						// 配对
						try {
							Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
							createBondMethod.invoke(device);
						} catch (Exception e) { 
							e.printStackTrace();
						}
						break;
						// 已配对
					case BluetoothDevice.BOND_BONDED:
						try {
							// 连接
							connectDevice(device);
							return;
						} catch (IOException e) {
							e.printStackTrace();
						}
						break;

					}	
				}

			}
		}
		//search devices to connect.
		//register filter
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		//filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
		//filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
		adapter.startDiscovery();
		Log.d("Started Discovery","Started Discovery");
	}

}


