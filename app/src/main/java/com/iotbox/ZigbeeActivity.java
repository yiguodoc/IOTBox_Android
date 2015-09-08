package com.iotbox;
  
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Iterator;

import com.github.mikephil.charting.charts.LineChart; 
import com.github.mikephil.charting.components.Legend; 
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.Legend.LegendForm; 
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ValueFormatter;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.LinearLayout;

public class ZigbeeActivity  extends SerialPortActivity {
	EditText mReception;
	EditText mResolve; 
	LinearLayout mainLayer;
	LinearLayout chatLayer;
	
	Button tempBtn,humiBtn,luxBtn;
	
	StringBuilder recBuffer; // 数据接收缓存区 
	boolean hasData;
	boolean isChatShow;
	int MAX_SIZE = 30;

	public enum ChartType {TEMPE_CHART, HUMI_CHART, LUX_CHART};
	
	private LineChart mChart;
	private HashMap<String,ArrayList<Float>> tempListMap = new HashMap<String,ArrayList<Float>>();
	private HashMap<String,ArrayList<Float>> humiListMap = new HashMap<String,ArrayList<Float>>();
	private HashMap<String,ArrayList<Float>> luxListMap  = new HashMap<String,ArrayList<Float>>();
	
	private Timer timer = new Timer();
//	private TimerTask task;
//	private Handler handler;
	private ChartType currentChart;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		//打开串口
		openPort(SerialPortType.port_zigbee);
		setContentView(R.layout.console_zigbee);
		TextView titleView = (TextView)findViewById(R.id.widget_navbar_ref).findViewById(R.id.nav_title);
		titleView.setText("传感器读取实验");  
		recBuffer = new StringBuilder(); //初始化
		hasData = false;
		isChatShow = false;
		currentChart = ChartType.TEMPE_CHART;
		
		// back
		final Button backBtn = (Button)findViewById(R.id.widget_navbar_ref).findViewById(R.id.btn_back);
		backBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ZigbeeActivity.this.finish();
			}
		});
		 
		mReception = (EditText)findViewById(R.id.EditTextReception);
		mResolve = (EditText)findViewById(R.id.EditTextResolve);
		
		mainLayer = (LinearLayout)findViewById(R.id.layer_main);
		chatLayer = (LinearLayout) findViewById(R.id.layer_chat);
		
		tempBtn = (Button)findViewById(R.id.btn_temp);
		tempBtn.setEnabled(false);
		humiBtn = (Button)findViewById(R.id.btn_humi);
		luxBtn = (Button)findViewById(R.id.btn_lux);

		// 图
        mChart = (LineChart) findViewById(R.id.chart1);
        mChart.setHighlightEnabled(true); 
        mChart.setTouchEnabled(true); 
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);  
        mChart.setPinchZoom(true); 
        mChart.setDescriptionTextSize(24f);
        Legend l = mChart.getLegend();
        l.setForm(LegendForm.LINE);
     
        
        // 这里的Handler实例将配合下面的Timer实例，完成定时更新图表的功能
// 		handler = new Handler() {
// 			@Override
// 			public void handleMessage(Message msg) {
// 				int b = (int)(Math.random()*50+50);
// 				int a = (int)(b%2);
//
// 				String rec = "AA4401FFFFFFFFFFFFFFFF00124B0006110B0805001910010100000000";
// 				if(0 == a){  
// 					 rec = "AA4401FFFFFFFFFFFFFFFF00124B0006110B4F44001910010118"+b+"0000";
// 				}
// 				else { 
//					 rec = "AA4401FFFFFFFFFFFFFFFF00124B0006110C5741001910010118"+b+"0000";
// 				}
// 				
// 				recBuffer.append(rec); 
//				mReception.append(rec); 
//				if(mReception.getText().toString().length()>10000) {
//					mReception.setText("");
//				}
//				
//				String recBufferString = recBuffer.toString();
//				if(recBufferString.startsWith("AA") &&
//						recBufferString.endsWith("0000") && 
//						recBufferString.length() == 58)  {
//					mReception.append("\r\n"); 
//					decodeData(recBuffer.toString());
//					recBuffer.delete(0, 58);
//				} 
//				
// 				super.handleMessage(msg);
// 			}
// 		};
//
// 		task = new TimerTask() {
// 			@Override
// 			public void run() {
// 				Message message = new Message();
// 				message.what = 1;
// 				handler.sendMessage(message);
// 			}
// 		};

// 		timer.schedule(task, 500, 500); 
 	}

 	@Override
 	public void onDestroy() {
 		// 当结束程序时关掉Timer
 		timer.cancel();
 		super.onDestroy();
 	}
	
	
	@Override
	protected void onDataReceived(final byte[] buffer, final int size) {
		runOnUiThread(new Runnable() {
			public void run() { 
				if(mReception != null) { 
					String rec = CHexConver.byte2HexStr(buffer, size, false); 
					recBuffer.append(rec); 
					mReception.append(rec); 
					if(mReception.getText().toString().length()>3000) {
						mReception.setText("");
					} 
					
					String recBufferString = recBuffer.toString();
					int flag = recBufferString.indexOf("AA44");
					if(flag > -1 && recBufferString.length() > flag+57) {
						recBufferString = recBufferString.substring(flag,flag+58);
						decodeData(recBufferString);
						mReception.append("\r\n"); 
						recBuffer.delete(0, recBuffer.toString().length()); 
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
		hasData = false;
	}
	
	public void viewChangeClick(View v) {
		if(isChatShow) {
			this.mainLayer.setVisibility(View.VISIBLE);
			this.chatLayer.setVisibility(View.GONE);
			isChatShow = false;
		}
		else { 
			this.mainLayer.setVisibility(View.GONE);
			this.chatLayer.setVisibility(View.VISIBLE); 
            updateChart();
			isChatShow = true;
		}
	} 
	
	public void viewTempClick(View v) {
		currentChart = ChartType.TEMPE_CHART;
		tempBtn.setEnabled(false);
		humiBtn.setEnabled(true);
		luxBtn.setEnabled(true);
		updateChart(); 
	} 
	
	public void viewHumiClick(View v) {
		currentChart = ChartType.HUMI_CHART; 
		tempBtn.setEnabled(true);
		humiBtn.setEnabled(false);
		luxBtn.setEnabled(true);
        updateChart();
	} 	
	
	public void viewLuxClick(View v) {
		currentChart = ChartType.LUX_CHART; 
		tempBtn.setEnabled(true);
		humiBtn.setEnabled(true);
		luxBtn.setEnabled(false);
        updateChart();
	} 
	
//
	public void decodeData(String rawdata)
    {  
        int type = Integer.parseInt(rawdata.substring(48, 50));
		String data = rawdata.substring(50, 54);
        String zigbeeId = rawdata.substring(22,38);

		if(!hasData) { 
	        mResolve.append( "      	【时间】		     " 
    				+"          【传感器ID】              "
    				+"            【温度】    "
    				+"            【湿度】       " 
    				+"            【光照度】\r\n");
	        hasData = true;
		}
		
        mResolve.append(Util.getNowTime() +"           "+ zigbeeId);
		
        double y = 0;
        switch (type) {
        	//温度 
            case 1:{
		        y = Util.convertTotemp(data);   
            	mResolve.append(" 			     	 "); 
		        mResolve.append(String.format("%.1f C", y)); 
		        
		        ArrayList<Float> list;
		        if(tempListMap.containsKey(zigbeeId)) {
		        	list = tempListMap.get(zigbeeId);
		        }
		        else {
		        	list = new ArrayList<Float>();
		        }
		        
		        if(list.size() >= MAX_SIZE) {
		        	list.clear();
		        }
		        float t = (float)Math.round(y*10)/10;
		        list.add(t);
		        tempListMap.put(zigbeeId, list);
                break;
            }
 
            //湿度
            case 2: {
            	y = Util.convertTohumi(data); 
            	for(int i=0; i<3;i++) {
            		mResolve.append("                  ");
            	}
		        mResolve.append(String.format("%.1f Rh", y));
		        
		        ArrayList<Float> list;
		        if(humiListMap.containsKey(zigbeeId)) {
		        	list = humiListMap.get(zigbeeId);
		        }
		        else {
		        	list = new ArrayList<Float>();
		        }
		        
		        if(list.size() >= MAX_SIZE) {
		        	list.clear();
		        }
		        float t = (float)Math.round(y*10)/10;
		        list.add(t);
		        humiListMap.put(zigbeeId, list);
            	break;
            }
         
            //光照
            case 3:{
            	y = Util.convertTolit(data);
            	for(int i=0; i<3;i++) {
            		mResolve.append("					    ");
            	}
        		mResolve.append("			  ");
		        mResolve.append(String.format("%.1f Lx", y));
		        
		        ArrayList<Float> list;
		        if(luxListMap.containsKey(zigbeeId)) {
		        	list = luxListMap.get(zigbeeId);
		        }
		        else {
		        	list = new ArrayList<Float>();
		        }
		        
		        if(list.size() >= MAX_SIZE) {
		        	list.clear();
		        }
		        float t = (float)Math.round(y*10)/10;
		        list.add(t);
		        luxListMap.put(zigbeeId, list);
                break;
            }
        } 
        
        mResolve.append("\n");
        if(mResolve.getText().toString().length() > 5000) {
        	mResolve.setText("");
    		hasData = false;
        }

        if(this.isChatShow) {
            updateChart();
        }
    }  
	
	//////////////////////////////////////////////////////////////////
 	private void updateChart() {  
 		mChart.clear();
 		HashMap<String,ArrayList<Float>> listMap = new HashMap<String,ArrayList<Float>>(); 
 		float Range = 1.0f; //Y轴范围
 		
 		ValueFormatter dataFormatter = new TempFormatter();
 		switch(currentChart) {
 			case TEMPE_CHART: {
 		 		listMap = tempListMap;
 		        mChart.setDescription("温度");
 		 		Range = 0.5f;
 		        dataFormatter = new TempFormatter();
 				break;
 			} 
 			case HUMI_CHART:{
 		 		listMap = humiListMap;
 		        mChart.setDescription("湿度");
 		 		Range = 10f;
 		        dataFormatter = new HumiFormatter();
 				break;
 			} 
 			case LUX_CHART:{
 		 		listMap = luxListMap;
 		        mChart.setDescription("光照度");
 		 		Range = 10f;
 		        dataFormatter = new LuxFormatter();
 				break;
 			} 
 			default:
 				break;
 		}
 		
 
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < MAX_SIZE; i++) {
            xVals.add((i) + "");
        }

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();  
        
 		Iterator<Map.Entry<String, ArrayList<Float>>> iter = listMap.entrySet().iterator();
 		int iColor = 0;
 		
 		float maxValue = 0;
 		float minValue = 0;
 		
		while (iter.hasNext()) {
			Map.Entry<String, ArrayList<Float>> entry = (Map.Entry<String, ArrayList<Float>>) iter.next();
			String key = entry.getKey(); 
			ArrayList<Float> val = entry.getValue(); 
	        ArrayList<Entry> yVals = new ArrayList<Entry>();
	        	
	        
	        if(Math.abs(maxValue - 0) < 1e-6) maxValue = val.get(0);
	        if(Math.abs(minValue - 0) < 1e-6) minValue = val.get(0);
	        
			for (int i = 0; i < val.size(); i++) { 
				float value = val.get(i); 
				if(value > maxValue) {
					maxValue = value;
				} 
				else if(value < minValue) {
					minValue = value;
				}
	            yVals.add(new Entry(value, i));
			}
			
			LineDataSet set = new LineDataSet(yVals, key); 
			if(0 == iColor) {
				set.setColor(Color.RED);
				set.setCircleColor(Color.RED);
			}
			else {
				set.setColor(Color.BLUE);
				set.setCircleColor(Color.BLUE);
			} 
			iColor++;
			
			set.setLineWidth(2f);
			set.setCircleSize(0f);
			set.setDrawCircleHole(true);
			set.setValueTextColor(Color.alpha(0));
			set.setFillAlpha(65); 
			set.setFillColor(Color.WHITE); 
	        dataSets.add(set);
		} 
          
		XAxis xAxis = mChart.getXAxis(); 
		xAxis.setDrawGridLines(false); //不显示表格线
		xAxis.setEnabled(false);//不显示X轴
		
        LineData data = new LineData(xVals, dataSets);  
        YAxis leftAxis = mChart.getAxisLeft(); 
        leftAxis.setDrawGridLines(false); //不显示表格线
        leftAxis.setAxisMaxValue(maxValue + Range);
        leftAxis.setAxisMinValue(minValue - Range);
        leftAxis.setValueFormatter(dataFormatter);
        leftAxis.setStartAtZero(false);  
         
        mChart.getAxisRight().setEnabled(false); 
        mChart.setData(data);
    }
}
	
