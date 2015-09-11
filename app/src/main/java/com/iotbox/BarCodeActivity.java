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
 
import java.io.IOException; 

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView; 
import android.widget.TextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
 
public class BarCodeActivity extends SerialPortActivity {

	EditText mReception; 
	ProgressBar progressBarScan;
	ImageView mCreateBarView;
	
	Handler scanHandler;
	Runnable scanRunnable;
	byte[] instruction;
	boolean isScanRunning = false;
	String rawReceiveData = "";
	
    private int TIME = 1000;   //每隔1s执行 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//打开串口
		openPort(SerialPortType.port_barcode);
		setContentView(R.layout.console_barcode);
		mReception = (EditText)findViewById(R.id.EditTextReception);
		progressBarScan = (ProgressBar)findViewById(R.id.progressBarScan); 
		
		TextView titleView = (TextView)findViewById(R.id.widget_navbar_ref).findViewById(R.id.nav_title);
		titleView.setText("条码读取实验");

		mCreateBarView = (ImageView) findViewById(R.id.code_img);
		Bitmap ruseltBitmap = BarcodeUtil.creatBarcode(getApplicationContext(),
				"0000000000000",0, 0, true);
		mCreateBarView.setImageBitmap(ruseltBitmap); 
		
		// back
		final Button backBtn = (Button)findViewById(R.id.widget_navbar_ref).findViewById(R.id.btn_back);
		backBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				BarCodeActivity.this.finish();
			}
		});
		  
		instruction = new byte[9];
		instruction = CHexConver.hexStr2Bytes(Constant.barcodeSendCmd);
				
		scanHandler = new Handler();
		scanRunnable = new Runnable() {
		    @Override
		    public void run() { 
		        //发送数据 
				try {
					scanHandler.postDelayed(this, TIME);
					if(mOutputStream == null) {
						return;
					}
					mOutputStream.write(instruction); 
				} 
				catch (IOException e) {  
					Toast.makeText(getApplicationContext(),e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
					endScan();
				}				
		    }
		};	
	}
	
	public void beginScan() {
		scanHandler.postDelayed(scanRunnable, 0);
		Button btn = (Button)findViewById(R.id.btnScan); 
		btn.setText("停止扫描");
		isScanRunning = true;
		progressBarScan.setVisibility(View.VISIBLE);
	}
	
	public void endScan() {
		scanHandler.removeCallbacks(scanRunnable);  		
		Button btn = (Button)findViewById(R.id.btnScan);
		btn.setText("开启扫描"); 
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
	
	public void clearClick(View v) {
		mReception.setText("");
	}
	 
  	@Override
	protected void onDataReceived(final byte[] buffer, final int size) {
		runOnUiThread(new Runnable() {
			public void run() { 
				rawReceiveData = new String(buffer, 0, size);
				if(mReception != null) {
                    /*
					String code = rawReceiveData.replace(" ", "");
					code = CHexConver.hexStr2Str(code);
					if(Util.isNumeric(code)) {
						mCreateBarView.setImageBitmap(BarcodeUtil.creatBarcode(getApplicationContext(),
								code, 0, 0, true));
					}
					*/

					mReception.append(Util.getNowTime());
					mReception.append("             ");
					mReception.append(rawReceiveData);
					rawReceiveData = "";
				}  
			}
		});
	}
}
