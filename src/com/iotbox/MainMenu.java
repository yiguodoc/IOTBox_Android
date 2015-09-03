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

import android.app.Activity;
import android.app.Dialog;
import android.app.AlertDialog;
import android.content.DialogInterface; 
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainMenu extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
   
        // barCode
	    final Button barcodeBtn = (Button)findViewById(R.id.btn_barcode);
	    barcodeBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(MainMenu.this, BarCodeActivity.class));
			}
		});
	    
	    // l_rfid	    
	    final Button lrfBtn = (Button)findViewById(R.id.btn_lrf);
	    lrfBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(MainMenu.this, LrfidActivity.class));
			}
		});
	    
        // h_rfid
	    final Button hrfBtn = (Button)findViewById(R.id.btn_hrf);
	    hrfBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(MainMenu.this, HrfidActivity.class));
			}
		});
	    
	    // u_rfid	    
	    final Button urfBtn = (Button)findViewById(R.id.btn_urf);
	    urfBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(MainMenu.this, UrfidActivity.class));
			}
		});
	    
		// GPS
		final Button gpsBtn = (Button)findViewById(R.id.btn_gps);
		gpsBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(MainMenu.this,GpsActivity.class));
			}
		});
		
		// GSM	    
		final Button gsmBtn = (Button)findViewById(R.id.btn_gsm);
		gsmBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(MainMenu.this, GsmActivity.class));
			}
		});
		
		// zigBee
		final Button zigbeeBtn = (Button)findViewById(R.id.btn_zigbee);
		zigbeeBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(MainMenu.this, ZigbeeActivity.class));
			}
		});
		
		// settings
		final Button settingsBtn = (Button)findViewById(R.id.btn_settings);
		settingsBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(MainMenu.this, SerialPortPreferences.class));
			}
		});
		
		// about	    
		final Button aboutBtn = (Button)findViewById(R.id.btn_about);
		aboutBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
//				startActivity(new Intent(MainMenu.this, prepsetup.class));
				
				AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
				builder.setTitle("版本信息");
				builder.setIcon(android.R.drawable.ic_dialog_info);
				builder.setMessage(R.string.about_msg);
				builder.setPositiveButton("确定",null);
				builder.show();
			}
		});	    
		
		// quit	    
		final Button quitBtn = (Button)findViewById(R.id.btn_quit);
		quitBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				 Dialog alertDialog = new AlertDialog.Builder(MainMenu.this).
						 setTitle("提示").
						 setMessage("您确定要退出程序吗？").
						 setIcon(android.R.drawable.ic_dialog_info).
						 setPositiveButton("确定", new DialogInterface.OnClickListener() {
								@Override 
								public void onClick(DialogInterface dialog, int which) {
									MainMenu.this.finish(); 
//									startActivity(new Intent(MainMenu.this, onestep.class));
								} 
						 }).
						 setNegativeButton("取消",null).create(); 
			        
				 alertDialog.show(); 
			}
		});	
    } 
     
}