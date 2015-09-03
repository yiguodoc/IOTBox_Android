/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package com.iotbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.InvalidParameterException;

import com.iotbox.SerialPortActivity.SerialPortType;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.widget.Toast;
import android_serialport_api.SerialPort;

public abstract class GpsSerialPortActivity extends Activity {

	protected Application mApplication;
	protected SerialPort mSerialPort;
	protected OutputStream mOutputStream;
	private InputStream mInputStream;
	private ReadThread mReadThread;

	private DatagramPacket dataPacket;
	private DatagramSocket udpSocket;
	private String serverIP;
	private int serverPort;
	private boolean enableUpload; 

	private class ReadThread extends Thread {

		@Override
		public void run() {
			super.run();
			while(!isInterrupted()) {
				try { 
					if (mInputStream == null) 
						return;
					
					BufferedReader dr = new BufferedReader(new InputStreamReader(mInputStream));
					String line = null;
					while((line = dr.readLine())!= null) {
						onDataReceived(line);
						
						if(enableUpload) {
							//将读取的data发送到服务端 
							byte data[] = line.getBytes();
							sendToUDP(data, data.length);
						}
					}
					/*
					size = mInputStream.read(buffer);
					if (size > 0) {
						onDataReceived(buffer, size);
					}
					*/ 
				} 
				catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	} 
	
	//将读取的Data发送到服务端
	private void sendToUDP(byte[] buffer,int size) { 
		try {
			InetAddress serverAddress = InetAddress.getByName(serverIP); 
			dataPacket = new DatagramPacket (buffer, size, serverAddress, serverPort);
			udpSocket.send(dataPacket);
		}
		catch (Exception e) { 
			e.printStackTrace();
		}
	}

	private void DisplayError(int resourceId) {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("提示");
		b.setIcon(android.R.drawable.ic_dialog_alert);
		b.setMessage(resourceId);
		b.setPositiveButton("OK", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
//				GpsSerialPortActivity.this.finish();
			}
		});
		b.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences shared = getSharedPreferences(
				"com.iotbox_preferences", MODE_PRIVATE); 
		serverIP = shared.getString("SERVER_IP", "127.0.0.1");
		serverPort =  Integer.parseInt(shared.getString("SERVER_PORT", "8080"));
		enableUpload = shared.getBoolean("ENABLE_UPLOAD", false);
		
		try {
			udpSocket = new  DatagramSocket (); 
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void openPort() {
		mApplication = (Application) getApplication();
		try  {
			mSerialPort = mApplication.getSerialPort(Constant.baudrate_gps);//获取串口
			mOutputStream = mSerialPort.getOutputStream();//获取文件输出流
			mInputStream = mSerialPort.getInputStream();//获取文件输入流

			/* Create a receiving thread */
			mReadThread = new ReadThread();//创建一个读线程
			mReadThread.start();//开启读线程
		} catch (SecurityException e) {
			DisplayError(R.string.error_security);
		} catch (IOException e) {
			DisplayError(R.string.error_unknown);
		} catch (InvalidParameterException e) {
			DisplayError(R.string.error_configuration);
		}
	}

	//protected abstract void onDataReceived(final byte[] buffer, final int size);
	protected abstract void onDataReceived(String s);

	@Override
	protected void onDestroy() {
		if (mReadThread != null)
			mReadThread.interrupt();
		mApplication.closeSerialPort();
		mSerialPort = null;
		super.onDestroy();
	}
}
