package com.ttqeducation.myViews;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.ttqeducation.R;
import com.ttqeducation.beans.TestInfo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class PickerView extends View {
	public static final String TAG = "PickerView";
	/**
	 * text之间间距和minTextSize之比
	 */
	public static final float MARGIN_ALPHA = 2.8f;
	/**
	 * 自动回滚到中间的速度
	 */
	public static final float SPEED = 2;

	private List<TestInfo> mDataList;
	/**
	 * 选中的位置，这个位置是mDataList的中心位置，一直不变
	 */
	private int mCurrentSelected;

	/**
	 * Android 中的画笔是 Paint类 Paint即画笔，在绘图过程中起到了极其重要的作用，画笔主要保存了颜色，
	 * 样式等绘制信息，指定了如何绘制文本和图形，画笔对象有很多设置方法， 大体上可以分为两类，一类与图形绘制相关，一类与文本绘制相关。
	 */
	private Paint mPaint;

	private float selectTextSize = 60;			// 选中的字体的大小；
	private float othersTestSize = 40;			// 其他地方字体的大小；
	
	private float mMaxTextSize = 80;
	private float mMinTextSize = 40;
	
	private float mMaxTextAlpha = 255;
	private float mMinTextAlpha = 120;

	private int mColorText = 0x333333; // 黑色;

	private int mViewHeight;
	private int mViewWidth;

	private float mLastDownY;
	/**
	 * 滑动的距离
	 */
	private float mMoveLen = 0;

	/**
	 * 初始时为false;
	 */
	private boolean isInit = false;
	private onSelectListener mSelectListener;
	private Timer timer;
	private MyTimerTask mTask;

	Handler updateHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (Math.abs(mMoveLen) < SPEED) // 返回 float 值的绝对值
			{
				mMoveLen = 0;
				if (mTask != null) {
					mTask.cancel();
					mTask = null;
					performSelect();
				}
			} else
				// 这里mMoveLen / Math.abs(mMoveLen)是为了保有mMoveLen的正负号，以实现上滚或下滚
				mMoveLen = mMoveLen - mMoveLen / Math.abs(mMoveLen) * SPEED;
			invalidate();
		}

	};

	public PickerView(Context context) {
		super(context);
		init();
	}

	public PickerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public void setOnSelectListener(onSelectListener listener) {
		mSelectListener = listener;
	}

	private void performSelect() {
		if (mSelectListener != null)
			mSelectListener.onSelect(mDataList.get(mCurrentSelected).getTestName());
	}

	/**
	 * 返回对应的值；也即testName
	 * 
	 * @return
	 */
	public String getTextValue() {
		return mDataList.get(mCurrentSelected).getTestName();
	}
	
	/**
	 * 返回对应的键；也即useID;
	 * @return
	 */
	public String getTextKey(){
		return mDataList.get(mCurrentSelected).getUseID();
	}
	
	/**
	 * 返回对应的对象；
	 * @param TestInfo
	 */
	public TestInfo getTestInfo(){
		return mDataList.get(mCurrentSelected);
	}
	
	/**
	 * 设置显示字体的大小；
	 * @param size
	 */
	public void setSelectTestSize(float size){
		this.selectTextSize = size;
	}
	
	/**
	 * 设置其他地方字体的大小；
	 * @param size
	 */
	public void setOthersTestSize(float size){
		this.othersTestSize = size;
	}

	public void setData(List<TestInfo> datas) {
		mDataList = datas;
		mCurrentSelected = datas.size() / 2;
		invalidate();
	}

	public void setSelected(int selected) {
		mCurrentSelected = selected;
	}

	private void moveHeadToTail() {
		
		if(this.mDataList.size() <= 0){
			return;
		}
		
		TestInfo testInfo = mDataList.get(0);
		mDataList.remove(0);
		mDataList.add(testInfo);
	}

	private void moveTailToHead() {
		
		if(this.mDataList.size() <= 0){
			return;
		}
		
		TestInfo testInfo = mDataList.get(mDataList.size() - 1);
		mDataList.remove(mDataList.size() - 1);
		mDataList.add(0, testInfo);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mViewHeight = getMeasuredHeight();
		mViewWidth = getMeasuredWidth();
		// 按照View的高度计算字体大小
		mMaxTextSize = mViewHeight / 4.0f;
		// mMinTextSize = mMaxTextSize / 2f;
		mMinTextSize = mMaxTextSize / 2f;
		Log.i("lvjie", "widthMeasureSpec=" + widthMeasureSpec
				+ "   heightMeasureSpec=" + heightMeasureSpec
				+ "  mViewHeight=" + mViewHeight + "  mViewWidth=" + mViewWidth
				+ "  mMaxTextSize=" + mMaxTextSize + "  mMinTextSize="
				+ mMinTextSize);
		isInit = true;
		invalidate();
	}

	private void init() {
		timer = new Timer();
		mDataList = new ArrayList<TestInfo>();
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setStyle(Style.FILL);
		mPaint.setTextAlign(Align.CENTER);
		mPaint.setColor(mColorText);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 根据index绘制view
		if (isInit)
			drawData(canvas);
	}

	/**
	 * 当我们调整好画笔之后，现在需要绘制到画布上，这就得用Canvas类了。在Android中既然把Canvas当做画布，
	 * 那么就可以在画布上绘制我们想要的任何东西。除了在画布上绘制之外，还需要设置一些关于画布的属性，
	 * 比如，画布的颜色、尺寸等。下面来分析Android中Canvas有哪些功能，Canvas提供了如下一些方法： Canvas():
	 * 创建一个空的画布，可以使用setBitmap()方法来设置绘制具体的画布。 Canvas(Bitmap bitmap):
	 * 以bitmap对象创建一个画布，则将内容都绘制在bitmap上，因此bitmap不得为null。 Canvas(GL gl):
	 * 在绘制3D效果时使用，与OpenGL相关。 drawColor: 设置Canvas的背景颜色。 setBitmap: 设置具体画布。
	 * clipRect: 设置显示区域，即设置裁剪区。 isOpaque:检测是否支持透明。 rotate: 旋转画布 setViewport:
	 * 设置画布中显示窗口。 skew: 设置偏移量。
	 * 
	 * @param canvas
	 */
	private void drawData(Canvas canvas) {
		
		if(this.mDataList.size()<=0){
			return;
		}
		
		// 先绘制选中的text再往上往下绘制其余的text
		float scale = parabola(mViewHeight / 4.0f, mMoveLen);
//		this.selectTextSize = (mMaxTextSize - mMinTextSize) * scale + mMinTextSize;
//		this.selectTextSize = this.selectTextSize * 0.6f;

		mPaint.setTextSize(this.selectTextSize); // 设置选中字体的大小；
		mPaint.setAlpha((int) ((mMaxTextAlpha - mMinTextAlpha) * scale + mMinTextAlpha));
		// text居中绘制，注意baseline的计算才能达到居中，y值是text中心坐标
		float x = (float) (mViewWidth / 2.0);
		float y = (float) (mViewHeight / 2.0 + mMoveLen);
		FontMetricsInt fmi = mPaint.getFontMetricsInt();
		float baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));

		canvas.drawText(mDataList.get(mCurrentSelected).getTestName() + "", x, baseline,
				mPaint); // 绘出选中文字； 可以再此修改要绘制的文字；
		// 绘制上方data
		for (int i = 1; (mCurrentSelected - i) >= 0; i++) {
			drawOtherText(canvas, i, -1);
		}
		// 绘制下方data
		for (int i = 1; (mCurrentSelected + i) < mDataList.size(); i++) {
			drawOtherText(canvas, i, 1);
		}

	}

	/**
	 * @param canvas
	 * @param position
	 *            距离mCurrentSelected的差值
	 * @param type
	 *            1表示向下绘制，-1表示向上绘制
	 */
	private void drawOtherText(Canvas canvas, int position, int type) {
		float d = (float) (MARGIN_ALPHA * mMinTextSize * position + type
				* mMoveLen);
		float scale = parabola(mViewHeight / 4.0f, d);
//		this.othersTestSize = (mMaxTextSize - mMinTextSize) * scale + mMinTextSize;
		mPaint.setTextSize(this.othersTestSize * 1.0f); // 设置 上方及下方字体的大小；
		mPaint.setAlpha((int) ((mMaxTextAlpha - mMinTextAlpha) * scale + mMinTextAlpha));
		float y = (float) (mViewHeight / 2.0 + type * d);
		FontMetricsInt fmi = mPaint.getFontMetricsInt();
		float baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));
		canvas.drawText(mDataList.get(mCurrentSelected + type * position).getTestName(),
				(float) (mViewWidth / 2.0), baseline, mPaint);
	}

	/**
	 * 抛物线
	 * 
	 * @param zero
	 *            零点坐标
	 * @param x
	 *            偏移量
	 * @return scale
	 */
	private float parabola(float zero, float x) {
		float f = (float) (1 - Math.pow(x / zero, 2));
		return f < 0 ? 0 : f;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			doDown(event);
			break;
		case MotionEvent.ACTION_MOVE:
			doMove(event);
			break;
		case MotionEvent.ACTION_UP:
			doUp(event);
			break;
		}
		return true;
	}

	private void doDown(MotionEvent event) {
		if (mTask != null) {
			mTask.cancel();
			mTask = null;
		}
		mLastDownY = event.getY();
	}

	private void doMove(MotionEvent event) {

		mMoveLen += (event.getY() - mLastDownY);

		if (mMoveLen > MARGIN_ALPHA * mMinTextSize / 2) {
			// 往下滑超过离开距离
			moveTailToHead();
			mMoveLen = mMoveLen - MARGIN_ALPHA * mMinTextSize;
		} else if (mMoveLen < -MARGIN_ALPHA * mMinTextSize / 2) {
			// 往上滑超过离开距离
			moveHeadToTail();
			mMoveLen = mMoveLen + MARGIN_ALPHA * mMinTextSize;
		}

		mLastDownY = event.getY();
		invalidate();
	}

	private void doUp(MotionEvent event) {
		// 抬起手后mCurrentSelected的位置由当前位置move到中间选中位置
		if (Math.abs(mMoveLen) < 0.0001) {
			mMoveLen = 0;
			return;
		}
		if (mTask != null) {
			mTask.cancel();
			mTask = null;
		}
		mTask = new MyTimerTask(updateHandler);
		timer.schedule(mTask, 0, 10);
	}

	class MyTimerTask extends TimerTask {
		Handler handler;

		public MyTimerTask(Handler handler) {
			this.handler = handler;
		}

		@Override
		public void run() {
			handler.sendMessage(handler.obtainMessage());
		}

	}

	public interface onSelectListener {
		void onSelect(String text);
	}
}
