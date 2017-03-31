package com.ttqeducation.tools;

import com.ttqeducation.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;

/**
 * 圆弧计分
 * @author Administrator
 *
 */
public class KnowledgePointCountArc extends View { 
	
	private Context context;
	
	private Paint paint_back, paint_fore, paint_text; // 背景圆环和前景圆环
	private RectF rectf;
	private float tb;
	private int backgroundColor = 0xFFAEAEAE; // 底黑色
	private int foregroundColor = 0x000000; // 白色
	private int score;
	private float arc_y = 0f;
	private int score_text;
	private float arcDia = 0;
	public KnowledgePointCountArc(Context context, int foregroundColor, int score) {
		super(context);
		
		this.context = context;
		this.foregroundColor = foregroundColor;
		this.score = score;
		init();
	}

	public void init() {
		Resources res = getResources();
		tb = res.getDimension(R.dimen.arc_tb);

		DisplayMetrics metrics = getResources().getDisplayMetrics(); // 用来获取屏幕的分辨率；
		int screenWidthPX = metrics.widthPixels;
		float zoomPrecent = screenWidthPX / 1080;
		float screenWidthDP = DensityUtils.px2dp(context, screenWidthPX);
		arcDia = DensityUtils.dp2px(context, (screenWidthDP - 48) / 3);
		
		paint_back = new Paint();
		paint_back.setAntiAlias(true);
		paint_back.setColor(backgroundColor);
		paint_back.setStrokeWidth(tb * 0.633f);
		paint_back.setStyle(Style.STROKE);

		paint_fore = new Paint();
		paint_fore.setAntiAlias(true); // 设置画笔为抗锯齿
		paint_fore.setColor(foregroundColor); // 设置画笔颜色
		paint_fore.setStrokeWidth(tb * 0.633f); // 线宽
		paint_fore.setStyle(Style.STROKE); // STROKE:空心，FULL:实心

		paint_text = new Paint();
		paint_text.setAntiAlias(true);
		paint_text.setColor(foregroundColor);
		paint_text.setTextSize(arcDia / 4);
		paint_text.setStrokeWidth(tb * 0.2f);
		paint_text.setTextAlign(Align.CENTER);
		paint_text.setStyle(Style.FILL);
				
		rectf = new RectF();
//		rectf.set(tb * 0.5f, tb * 0.5f, tb * 7.1f, tb * 7.1f);
//		setLayoutParams(new LayoutParams((int) (tb * 7.6f), (int) (tb * 7.6f)));
		rectf.set(tb * 0.5f, tb * 0.5f, arcDia - tb * 0.5f, arcDia - tb * 0.5f);
		setLayoutParams(new LayoutParams((int)arcDia, (int)arcDia));
		
		this.getViewTreeObserver().addOnPreDrawListener(
				new OnPreDrawListener() {
					public boolean onPreDraw() {
						new thread();
						getViewTreeObserver().removeOnPreDrawListener(this);
						return false;
					}
				});
	}

	protected void onDraw(Canvas c) {
		super.onDraw(c);
		c.drawArc(rectf, -90, 360, false, paint_back);
		c.drawArc(rectf, -90, arc_y, false, paint_fore);
		
		// 文字居中
		FontMetrics fontMetrics = paint_text.getFontMetrics();
		float fontHeight = fontMetrics.bottom - fontMetrics.top;
		float fontBaseY = arcDia - (arcDia - fontHeight) / 2 -fontMetrics.bottom;
		c.drawText(score_text + "", arcDia / 2, fontBaseY, paint_text);
	}

	class thread implements Runnable {
		private Thread thread;
		private int statek;
		int count;

		public thread() {
			thread = new Thread(this);
			thread.start();
		}

		public void run() {
			while (true) {
				switch (statek) {
				case 0:
					try {
						Thread.sleep(200);
						statek = 1;
					} catch (InterruptedException e) {
					}
					break;
				case 1:
					try {
						Thread.sleep(15);
						arc_y += 3.6f;
						score_text++;
						count++;
						postInvalidate();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					break;
				}
				if (count >= score)
					break;
			}
		}
	}

}
