package com.iotbox;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Bitmap.Config;
import android.view.Gravity;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class BarcodeUtil {

	/**
	 * 生成条形码
	 * 
	 * @param context
	 * @param contents
	 *            需要生成的内容
	 * @param desiredWidth
	 *            生成条形码的宽带
	 * @param desiredHeight
	 *            生成条形码的高度
	 * @param displayCode
	 *            是否在条形码下方显示内容
	 * @return
	 */
	public static Bitmap creatBarcode(Context context, String contents,
			int desiredWidth, int desiredHeight, boolean displayCode) {
		Bitmap ruseltBitmap = null;
		/**
		 * 图片两端所保留的空白的宽度
		 */
		int marginW = 10;
		/**
		 * 条形码的编码类型
		 */
		BarcodeFormat barcodeFormat = BarcodeFormat.CODE_128;

		if (displayCode) {
			Bitmap barcodeBitmap = encodeAsBitmap(contents, barcodeFormat,
					100, 80);
			Bitmap codeBitmap = creatCodeBitmap(contents, 110, 25, context);
			ruseltBitmap = mixtureBitmap(barcodeBitmap, codeBitmap, new PointF(0, 80));
		} else {
			ruseltBitmap = encodeAsBitmap(contents, barcodeFormat,
					desiredWidth, desiredHeight);
		}

		return ruseltBitmap;
	}

	/**
	 * 生成条形码的Bitmap
	 * 
	 * @param contents
	 *            需要生成的内容
	 * @param format
	 *            编码格式
	 * @param desiredWidth
	 * @param desiredHeight
	 * @return
	 * @throws WriterException
	 */
	protected static Bitmap encodeAsBitmap(String contents,
			BarcodeFormat format, int desiredWidth, int desiredHeight) {
		final int WHITE = 0xFFFFFFFF;
		final int BLACK = 0xFF000000;

		MultiFormatWriter writer = new MultiFormatWriter();
		BitMatrix result = null;
		try {
			result = writer.encode(contents, format, desiredWidth,
					desiredHeight, null);
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int width = result.getWidth();
		int height = result.getHeight();
		int[] pixels = new int[width * height];
		// All are 0, or black, by default
		for (int y = 0; y < height; y++) {
			int offset = y * width;
			for (int x = 0; x < width; x++) {
				pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
			}
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	/**
	 * 生成显示编码的Bitmap
	 * 
	 * @param contents
	 * @param width
	 * @param height
	 * @param context
	 * @return
	 */
	protected static Bitmap creatCodeBitmap(String contents, int width,
			int height, Context context) {
		TextView tv = new TextView(context);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tv.setLayoutParams(layoutParams);
		tv.setText(contents);
		tv.setHeight(height);
		tv.setGravity(Gravity.CENTER);
		tv.setWidth(width);
		tv.setTextSize(14.f);
		tv.setDrawingCacheEnabled(true);
		tv.setTextColor(Color.BLACK);
		tv.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());

		tv.buildDrawingCache();
		Bitmap bitmapCode = tv.getDrawingCache();
		return bitmapCode;
	}

	/**
	 * 将两个Bitmap合并成一个
	 * 
	 * @param first
	 * @param second
	 * @param fromPoint
	 *            第二个Bitmap开始绘制的起始位置（相对于第一个Bitmap）
	 * @return
	 */
	protected static Bitmap mixtureBitmap(Bitmap first, Bitmap second,
			PointF fromPoint) {
		if (first == null || second == null || fromPoint == null) {
			return null;
		}
		Bitmap newBitmap = Bitmap.createBitmap(220,120, Config.ARGB_4444);
		Canvas cv = new Canvas(newBitmap);
		cv.drawBitmap(first, 0, 0, null);
		cv.drawBitmap(second, fromPoint.x, fromPoint.y, null);
		cv.save(Canvas.ALL_SAVE_FLAG);
		cv.restore();

		return newBitmap;
	}
}
