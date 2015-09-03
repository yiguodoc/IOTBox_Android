package com.iotbox;
 
import java.lang.StringBuilder;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View; 
import java.io.IOException;
 

public class UrfidActivity extends  SerialPortActivity {

	EditText mReception;
	EditText mResolve; 
	ProgressBar progressBarScan;
	
	StringBuilder recBuffer; // 数据接收缓存区 
	boolean isScanRunning; // 是否在扫描 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//打开串口
		openPort(SerialPortType.port_urf);
		setContentView(R.layout.console_urfid);
		mReception = (EditText)findViewById(R.id.EditTextReception);
		mResolve = (EditText)findViewById(R.id.EditTextResolve); 
		progressBarScan = (ProgressBar)findViewById(R.id.progressBarScan); 
		
		TextView titleView = (TextView)findViewById(R.id.widget_navbar_ref).findViewById(R.id.nav_title);
		titleView.setText("超高频RFID读取实验");

		recBuffer = new StringBuilder(); //初始化
		isScanRunning = false;
		
		// back
		final Button backBtn = (Button)findViewById(R.id.widget_navbar_ref).findViewById(R.id.btn_back);
		backBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				UrfidActivity.this.finish();
			}
		});   
	}
	
	public void beginScan() { 
		byte[] instruction = CHexConver.hexStr2Bytes(Constant.RFIDCommand_RMU_InventoryAnti9);
        //发送数据 
		try {
			if(mOutputStream != null) {
				mOutputStream.write(instruction); 
			}
		} 
		catch (IOException e) { 
			Toast.makeText(getApplicationContext(),e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
			endScan();
		}
		
		Button btn = (Button)findViewById(R.id.btnScan); 
		btn.setText("停止读取"); 
		isScanRunning = true;
		progressBarScan.setVisibility(View.VISIBLE);
	}
	
	public void endScan() {
		byte[] instruction = CHexConver.hexStr2Bytes(Constant.RFIDCommand_RMU_StopGet);
		try {
			if(mOutputStream != null) {
				mOutputStream.write(instruction); 
			}
		}
		catch (IOException e) {
			Toast.makeText(getApplicationContext(),e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
		}
		
		Button btn = (Button)findViewById(R.id.btnScan);
		btn.setText("开启读取"); 
		isScanRunning = false;
		progressBarScan.setVisibility(View.GONE);
	}
	
	public void sendClick(View v) {	
		if(!isScanRunning) {
			beginScan();
		}
		else {
			endScan();
		}		
	} 
	
	@Override
	protected void onDataReceived(final byte[] buffer, final int size) {
		runOnUiThread(new Runnable() {
			public void run() {
				if (mReception != null) {
					String rec = CHexConver.byte2HexStr(buffer, size, false);
					mReception.append(rec);
					recBuffer.append(rec); 
					if(rec.endsWith("55")) {  
						mReception.append("\r\n");
						String str = recBuffer.toString().replace("FFAA", "AA"); 
						int length = str.length(); 
						if(str.startsWith("AA") && length > 14) { 
							mResolve.append(" "+ Util.getNowTime()
											   +"	     标签ID号:【"
											   +str.substring(12,length-2)+"】\r\n");
						} 
						//清空缓存区
						recBuffer.delete(0, length);
					}
				}
			}
		});
	}
	
	public void clearRawClick(View v) {
		mReception.setText("");
	}
	
	public void clearResolveClick(View v) {
		mResolve.setText("");
	} 
} 
