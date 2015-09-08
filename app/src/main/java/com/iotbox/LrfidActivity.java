package com.iotbox;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView; 
import java.text.SimpleDateFormat;

import com.iotbox.SerialPortActivity.SerialPortType;

public class LrfidActivity  extends SerialPortActivity {
	EditText mReception;
	EditText mResolve; 
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		//打开串口
		openPort(SerialPortType.port_lrf);
		
		setContentView(R.layout.console_lrfid);
		TextView titleView = (TextView)findViewById(R.id.widget_navbar_ref).findViewById(R.id.nav_title);
		titleView.setText("低频RFID读取实验"); 
 
		// back
		final Button backBtn = (Button)findViewById(R.id.widget_navbar_ref).findViewById(R.id.btn_back);
		backBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				LrfidActivity.this.finish();
			}
		});
		 
		mReception = (EditText)findViewById(R.id.EditTextReception);
		mResolve = (EditText)findViewById(R.id.EditTextResolve);
	}
	
	@Override
	protected void onDataReceived(final byte[] buffer, final int size) {
		runOnUiThread(new Runnable() {
			public void run() {
				if (mReception != null) { 
					String rec = CHexConver.byte2HexStr(buffer, size, false); 
					mReception.append(rec);
					if(4 == size) {
						resolve(rec);
					}
					else { 
						mReception.append("    ");
					}
				}
			}
		});
	}
	
	public void resolve(String rawString){
		long a = Long.parseLong(rawString, 16);
		long b = Long.parseLong(rawString.substring(0,4),16);
		long c = Long.parseLong(rawString.substring(4,8),16);
		mResolve.append(" "+ Util.getNowTime()+"	                标签ID号:【"+ Long.toString(a)
					   +"   "+ Long.toString(b)+","+Long.toString(c) +"】"
							+"\r\n");
	}
	
	public void clearRawClick(View v) {
		mReception.setText("");
	}
	
	public void clearResolveClick(View v) {
		mResolve.setText("");
	} 
}
