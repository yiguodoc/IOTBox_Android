package com.iotbox;

import com.iotbox.SerialPortActivity.SerialPortType;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class GpsActivity extends GpsSerialPortActivity {

	EditText mReception;
	EditText weidu;
	EditText jingdu;
	EditText weixingshu;
	EditText weixinguse;
	
	String gpss;
	int i=0;
	GpsData gpsdata=new GpsData();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//打开串口
		openPort();
		
		setContentView(R.layout.console_gps);
		TextView titleView = (TextView)findViewById(R.id.widget_navbar_ref).findViewById(R.id.nav_title);
		titleView.setText("GPS空间实验");	
		
		// back
		final Button backBtn = (Button)findViewById(R.id.widget_navbar_ref).findViewById(R.id.btn_back);
		backBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				GpsActivity.this.finish();
			}
		});

		
		mReception = (EditText) findViewById(R.id.editText1);
		weidu = (EditText) findViewById(R.id.editText2);
		jingdu = (EditText) findViewById(R.id.editText3);
		weixingshu = (EditText) findViewById(R.id.editText4);
		weixinguse = (EditText) findViewById(R.id.editText5);	
 
	}	
  	@Override
	protected void onDataReceived(final String s) 
  	{
  		//Runnable匿名函数类
		runOnUiThread(new Runnable() {
			public void run()
			{
				if (mReception != null)
				{
					
					mReception.append(s+"\n");
					
					if(gpsdata.parseGPRMC(s)==3)
					{
						weidu.setText(""+gpsdata.lats+":"+gpsdata.lat+"");
						jingdu.setText(""+gpsdata.lngs+":"+gpsdata.lng);											
					} 
				}   				
				
			}
		});
	}
  	
	public void backToHome(View v) {
		this.finish();
	}
}

 
/*
 * 解析串口接收到的字节串（RMC）推荐定位信息
 * $GPRMC,013946,A,3202.1855,N,11849.0769,E,0.05,218.30,111105,4.5,W,A*20..
 * $GPRMC,<1> ,2,<3> ,4,<5> ,6,<7> ,<8> ,<9> ,10,11,12*hh<CR><LF>
 * <1>UTC时间，hhmmss（时分秒）格式 <2> 定位状态，A=有效定位，V=无效定位
 * <3>纬度ddmm.mmmm（度分）格式（前面的0也将被传输） <4> 纬度半球N（北半球）或S（南半球）
 * <5>经度dddmm.mmmm（度分）格式（前面的0也将被传输） <6> 经度半球E（东经）或W（西经）
 * <7>地面速率（000.0~999.9节，前面的0也将被传输） <8> 地面航向（000.0~359.9度，以真北为参考基准，前面的0也将被传输）
 * <9> UTC日期，ddmmyy（日月年）格式 <10> 磁偏角（000.0~180.0度，前面的0也将被传输）
 * <11>磁偏角方向，E（东）或W（西） <12> 模式指示（仅NMEA0183 3.00版本输出，A=自主定位，D=差分，E=估算，N=数据无效）
 * 
 * 
 * 返回值 0 正确 1校验失败 2非GPRMC信息 3无效定位 4格式错误 5校验错误
 */
class GpsData {	
	short hour,minute,second;
	double lat,lng,speed;
	char lats,lngs;
	boolean valid;
	
	public int parseGPRMC(String by) {
		if(by==null||by.isEmpty())//判断非空
			return 4;
		if(checksum(by.getBytes())==false)//计算校验和并与语句中的校验和比较
			return 5;
		MyStringTokenizer str=new MyStringTokenizer(new String(by),',');
		String temp=null;
		temp=str.nextToken();//取第一个子串即标记
		if(!temp.equals("$GPRMC"))// 确定是$GPRMC
			return 2;
	
		temp = str.nextToken();// 时间
		hour=(short)(Integer.parseInt(temp.substring(0,2))+8);
		minute=(short)Integer.parseInt(temp.substring(2,4));
		second=(short)Float.parseFloat(temp.substring(4));
		temp = str.nextToken();// 定位状态
		if(temp.length()!=1)//无
			return 42;
		else if(temp.charAt(0)=='A')//为A则有效 为V则无效
			return 3;
		temp = str.nextToken();// 纬度
		lat = Double.parseDouble(temp.substring(0,2));// 纬度-度
		lat += Double.parseDouble(temp.substring(2))/60;// 纬度-分
		temp = str.nextToken();// 纬度半球
		if(temp.length()!=1)
			return 44;
		else if(temp.charAt(0)=='N')
			lats='N';
		else if(temp.charAt(0)=='S')
			lats='S';
		else   //错误信息
			return 45;
		
		temp = str.nextToken();// 经度
		lng = Double.parseDouble(temp.substring(0,3));// 经度-度
		lng += Double.parseDouble(temp.substring(3))/60;// 经度-分
		temp = str.nextToken();// 经度半球
		if(temp.length()!=1)
			return 47;
		else if(temp.charAt(0)=='E')
			lngs='E';
		else if(temp.charAt(0)=='W')
			lngs='W';
		else
			return 48;
		
		temp = str.nextToken();// 地面速率
		if(!temp.isEmpty()) {
			speed = Double.parseDouble(temp)*1.852;//速度单位转换为千米每小时
		}
		
		this.valid=true;//转换成功，产生的数据有效
		return 0;
	}

	private  boolean checksum(byte[] b) {
		byte chk = 0;// 校验和
		byte cb = b[1];// 当前字节
		int i = 0;
		if(b[0] != '$')
			return false;
		for(i=2;i<b.length;i++){//计算校验和 
			if(b[i] == '*')
				break;
			cb = (byte)(cb^b[i]);
		}
		
		if(i!=b.length-3)//校验位不正常
			return false;
		
		i++;
		byte[] bb=new byte[2];//用于存放语句后两位
		bb[0]=b[i++];bb[1]=b[i];
		try {
			chk = (byte)Integer.parseInt(new String(bb),16);//后两位转换为一个字节
		}
		catch(Exception e){//后两位无法转换为一个字节，格式错误 
			return false;
		}
	
		return chk==cb;//计算出的校验和与语句的数据是否一致
	}
}

class MyStringTokenizer {
	private String ss;
	String split;
	String[] msg;
	int i=-1;
	MyStringTokenizer(String s,char a) {
		ss=s;
		split=""+a;
		msg=ss.split(split);
	}
	
	public String nextToken() {
		i++;
		return msg[i];
		
	}

}
