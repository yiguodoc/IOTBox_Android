package com.iotbox;
 
import android.os.Bundle;
import android.view.View; 

import android.widget.AdapterView;    
import android.widget.Button;
import android.widget.EditText;  
import android.widget.Spinner;  
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException; 

public class HrfidActivity extends SerialPortActivity {
	Spinner spinner;
	EditText mReception;
	EditText mResolve;
	long selectedProtocolId;  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//打开串口
		openPort(SerialPortType.port_hrf);
		
		setContentView(R.layout.console_hrfid);
		TextView titleView = (TextView)findViewById(R.id.widget_navbar_ref).findViewById(R.id.nav_title);
		titleView.setText("高频RFID实验");
		
		// back
		final Button backBtn = (Button)findViewById(R.id.widget_navbar_ref).findViewById(R.id.btn_back);
		backBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				HrfidActivity.this.finish();
			}
		});
		
		mReception = (EditText) findViewById(R.id.EditTextReception);
		mResolve = (EditText)findViewById(R.id.EditTextResolve);
		
		 //根据id获取对象  
        spinner=(Spinner) findViewById(R.id.spinner1);  
        selectedProtocolId = spinner.getItemIdAtPosition(spinner.getSelectedItemPosition());
        
        //注册事件  
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {  
  
            @Override  
            public void onItemSelected(AdapterView<?> parent, View view,  
                    int position, long id) {  
            	 selectedProtocolId = spinner.getItemIdAtPosition(spinner.getSelectedItemPosition());  
            }  
  
            @Override  
            public void onNothingSelected(AdapterView<?> parent) {  
                Toast.makeText(getApplicationContext(), "没有改变的处理", Toast.LENGTH_LONG).show();  
            }  
  
        });  
	} 
 
	public void protocolSetClick(View v) {
		int i;
		CharSequence setpro;		
		switch((int)selectedProtocolId) {
			case 1:
				setpro = Constant.rfid14443b_setPro;
				break;
			case 2:
				setpro = Constant.rfid15693_setPro;
				break;
			default:
				setpro = Constant.rfid14443a_setPro;
				break;			
		}
		
		char[] text = new char[setpro.length()];
		for (i=0; i<setpro.length(); i++) {
			text[i] = setpro.charAt(i);
		}				
		try {
			if(mOutputStream != null) {
				mOutputStream.write(new String(text).getBytes());
			}
		}
		catch(IOException e) { 
			Toast.makeText(getApplicationContext(),e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();	
		}
	} 
  
	public void protocolReadClick(View v) {
		int i; 
		
		CharSequence readpro;		
		switch((int)selectedProtocolId) {
			case 1:
				readpro = Constant.rfid14443b_readPro;
				break;
			case 2:
				readpro = Constant.rfid15693_readPro;
				break;
			default:
				readpro = Constant.rfid14443a_readPro;
				break;			
		}
		
		char[] text = new char[readpro.length()];
		for (i=0; i<readpro.length(); i++) {
			text[i] = readpro.charAt(i);
		}				 
		try {  					
			if(mOutputStream != null) {
				mOutputStream.write(new String(text).getBytes());
			}
		} 
		catch (Exception e) {
			Toast.makeText(getApplicationContext(),e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
	
	public void clearRawClick(View v) {
		mReception.setText("");
	}
	
	public void clearResolveClick(View v) {
		mResolve.setText("");
	} 
}
