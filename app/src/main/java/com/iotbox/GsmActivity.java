package com.iotbox;

import java.io.IOException;

import com.iotbox.SerialPortActivity.SerialPortType;
 
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.app.AlertDialog;
import android.content.DialogInterface; 


public class GsmActivity extends SerialPortActivity {
	EditText mReception; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//打开串口
		openPort(SerialPortType.port_gsm);
		
		setContentView(R.layout.console_gsm);
		mReception = (EditText) findViewById(R.id.editText1); 
		TextView  titleView = (TextView)findViewById(R.id.widget_navbar_ref).findViewById(R.id.nav_title);
		titleView.setText("GSM实验"); 
 
		// back
		final Button backBtn = (Button)findViewById(R.id.widget_navbar_ref).findViewById(R.id.btn_back);
		backBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				GsmActivity.this.finish();
			}
		});
		
		final Button jiance =(Button)findViewById(R.id.jiance);
		final Button zhizao =(Button)findViewById(R.id.zhizao);
		final Button shebei =(Button)findViewById(R.id.shebei);
		final Button boda =(Button)findViewById(R.id.boda);
		final Button guaduan =(Button)findViewById(R.id.guaduan);
		final Button jietong=(Button)findViewById(R.id.jietong);
		final Button qingkong=(Button)findViewById(R.id.qingkong); 
		 
		jiance.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				int i;
				CharSequence t ="AT%TSIM\r";
				char[] text = new char[t.length()];
				for (i=0; i<t.length(); i++) {
					text[i] = t.charAt(i);
				}
				try {
					if(mOutputStream != null) {
						mOutputStream.write(new String(text).getBytes());
					}
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		zhizao.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub 
				int i;
				CharSequence t ="AT+CGMI\r";
				char[] text = new char[t.length()];
				for (i=0; i<t.length(); i++) {
					text[i] = t.charAt(i);
				}
				try {
					if(mOutputStream != null) {
						mOutputStream.write(new String(text).getBytes());
					} 
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		});
		shebei.setOnClickListener(new View.OnClickListener() { 
			@Override
			public void onClick(View arg0) {
				int i;
				CharSequence t ="AT+CGMM\r";
				char[] text = new char[t.length()];
				for (i=0; i<t.length(); i++) {
					text[i] = t.charAt(i);
				}
				try {
					if(mOutputStream != null) {
						mOutputStream.write(new String(text).getBytes());
					}
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		});
 
		boda.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) { 
		        LayoutInflater factory = LayoutInflater.from(GsmActivity.this);   
		        final View textEntryView = factory.inflate(R.layout.teldialog, null);  
		        
		        new AlertDialog.Builder(GsmActivity.this) 
		       .setTitle("请输入您要拨打的电话号码:") 
		       .setView(textEntryView)  
		       .setNegativeButton("确定", new DialogInterface.OnClickListener() {  
		           public void onClick(DialogInterface dialog, int whichButton) {   
		        	   final EditText etPhoneNo = (EditText)textEntryView.findViewById(R.id.phoneNo); 
		        	   callPhone(etPhoneNo.getText().toString().trim());
		           }
		       }).setPositiveButton("取消", null).create().show();  
			}
		});
		guaduan.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub 
				int i;
				CharSequence t ="ATH\r";
				char[] text = new char[t.length()];
				for (i=0; i<t.length(); i++) {
					text[i] = t.charAt(i);
				}
				try {
					if(mOutputStream != null) {
						mOutputStream.write(new String(text).getBytes());
					}
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		jietong.setOnClickListener(new View.OnClickListener() { 
			@Override
			public void onClick(View arg0) {
				int i;
				CharSequence s ="AT^SWSPATH=1\r"; 
				char[] s_text = new char[s.length()];
				for (i=0; i<s.length(); i++) {
					s_text[i] = s.charAt(i);
				}
				try {
					if(mOutputStream != null) {
						mOutputStream.write(new String(s_text).getBytes());
					} 
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				
				CharSequence t ="ATA\r";
				char[] text = new char[t.length()];
				for (i=0; i<t.length(); i++) {
					text[i] = t.charAt(i);
				}
				try {
					if(mOutputStream != null) {
						mOutputStream.write(new String(text).getBytes());
					}
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		});  
		qingkong.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mReception.setText("");
			}
		}); 
	} 
		
	public void callPhone(String phoneNo){
		int i;
		CharSequence t ="ATD"+phoneNo+";\r";
		char[] text = new char[t.length()];
		for (i=0; i<t.length(); i++) {
			text[i] = t.charAt(i);
		}
		try {
			if(mOutputStream != null) {
				mOutputStream.write(new String(text).getBytes());
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
  	@Override
	protected void onDataReceived(final byte[] buffer, final int size) {
		runOnUiThread(new Runnable() {
			public void run() {
				if (mReception != null) {
					mReception.append(new String(buffer, 0, size));
				}
			}
		});
	}
}
