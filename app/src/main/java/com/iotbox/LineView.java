package com.iotbox; 

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.view.View;
/**
 * @author Jesse
 * write this view for draw line,use it easy.
 */
public class LineView extends View {
	private final static String X_KEY = "Xpos";
	private final static String Y_KEY = "Ypos";

    private int XPoint = 60; 
    private int YPoint = 280; 
    private int XScale = 20;  //刻度长度 
    private int YScale = 40; 
    private int XLength = 400; 
    private int YLength = 280; 
    private int MaxDataSize = XLength / XScale;   
    
    private String[] YLabel = new String[YLength / YScale];  
	 
	private List<Integer> mListPoint1 = new ArrayList<Integer>();  
	private List<Integer> mListPoint2 = new ArrayList<Integer>();  
	
	Paint mPaint = new Paint();
	
	public LineView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public LineView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		for(int i=0; i<YLabel.length; i++){
			YLabel[i] = (i * 5) + "C";
		}  
	}
	
	public LineView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
		mPaint.setColor(Color.BLACK);
		mPaint.setStrokeWidth(3);
		mPaint.setAntiAlias(true);
		
		 //画Y轴 
        canvas.drawLine(XPoint, YPoint - YLength, XPoint, YPoint, mPaint); 
        
        //添加刻度和文字 
        for(int i = 0; i * YScale < YLength; i++) { 
            canvas.drawLine(XPoint, YPoint - i * YScale, XPoint + 5, YPoint - i * YScale, mPaint);  //刻度 
            canvas.drawText(YLabel[i], XPoint - 50, YPoint - i * YScale, mPaint);//文字 
        } 
                                                                         
        //画X轴 
        canvas.drawLine(XPoint, YPoint, XPoint + XLength, YPoint, mPaint); 
        
        mPaint.setColor(Color.RED);
		mPaint.setStrokeWidth(2);
		mPaint.setAntiAlias(true);
		
		for (int index=0; index<mListPoint1.size(); index++)
		{
			if (index > 0)
			{ 
				canvas.drawLine(XPoint +(index-1) * XScale, mListPoint1.get(index-1),
						XPoint +index* XScale, mListPoint1.get(index), mPaint);
				canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG)); 
			}
		}
		
		mPaint.setColor(Color.BLUE);
		mPaint.setStrokeWidth(2);
		mPaint.setAntiAlias(true);
			
		for (int index=0; index<mListPoint2.size(); index++)
		{
			if (index > 0)
			{ 
				canvas.drawLine(XPoint +(index-1) * XScale, mListPoint2.get(index-1),
						XPoint +index* XScale, mListPoint2.get(index), mPaint);
				canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG)); 
			}
		}
	}
	/**
	 * @param curX  which x position you want to draw.
	 * @param curY	which y position you want to draw.
	 * @see	all you put x-y position will connect to a line.
	 */
	public void setFirstLinePoint(int curValue)
	{
		mListPoint1.add(curValue);
		if(mListPoint1.size() > MaxDataSize) {
			mListPoint1.remove(0);
		}
		invalidate();
	}
	
	public void setSecondLinePoint(int curValue)
	{
		mListPoint2.add(curValue);
		if(mListPoint2.size() > MaxDataSize) {
			mListPoint2.remove(0);
		}
		invalidate();
	}
}
