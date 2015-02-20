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

import java.util.Set;
import java.util.Vector;

import org.cocos2dx.lib.Cocos2dxActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

public class AppActivity extends Cocos2dxActivity {
	public static Activity _activiy;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    //Save activity instance
	    _activiy = this;
	}

	Set<String> mArrayAdapter;
	// Create a BroadcastReceiver for ACTION_FOUND
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// Add the name and address to an array adapter to show in a
				// ListView
				//mArrayAdapter.add(device.getName() + "/n" + device.getAddress());
				Log.d("OnReceive", device.getName());
			}
		}
	};
	
	public static Object alertJNI() {
	    String tag = "JniTest";
	    String message = "I've been called from C++";
	    Log.d(tag, "Showing alert dialog: " + message);
	    return _activiy;	      
	}
	
	int REQUEST_ENABLE_BT = 0x01;
	@SuppressWarnings("null")
	public void openBlueTooth() {
		String tag = "Open Blue Tooth.";
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
	    if(adapter.isEnabled()) {
	      Log.d(tag, "BlueTooth enabled.");
	    }
	    searchBlueToothDevices();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){  
	       if(requestCode == REQUEST_ENABLE_BT){  
	              if(resultCode == RESULT_OK){  
	                  //blueTooth turned on.
	            	  Log.d("Turned on.", "BlueTooth enabled.");
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
			// Add the name and address to an array adapter to show in a ListView
	        //mArrayAdapter.add(device.getName() + "/n" + device.getAddress());
	         Log.d("Paired Devices ", device.getName());
	      }
	    }
	    //search devices to connect.
	    	  
	    // Register the BroadcastReceiver
	    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
	    registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
	    adapter.startDiscovery();
		
	}
	

}


