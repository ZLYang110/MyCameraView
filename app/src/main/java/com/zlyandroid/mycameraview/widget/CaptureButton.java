package com.zlyandroid.mycameraview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;

import com.zlyandroid.mycameraview.R;


/**
 * Created by zhangliyang on 2019/10/14.
 * gethub https://github.com/ZLYang110
 * Email 1833309873@qq.com
 * Description:
 */

public class CaptureButton extends View {
    private static final int RING_WHAT = 101;
    private static final int MSG_RECORD_START = 102;
    private static final int MSG_RECORD_END = 103;


    private float circleWidth;//外圆环宽度
    private int outCircleColor;//外圆颜色
    private int innerCircleColor;//内圆颜色
    private int progressColor;//进度条颜色

    private Paint outRoundPaint = new Paint(); //外圆画笔
    private Paint mCPaint = new Paint();//进度画笔
    private Paint innerRoundPaint = new Paint();
    private float width; //自定义view的宽度
    private float height; //自定义view的高度
    private float outRaduis; //外圆半径
    private float innerRaduis;//内圆半径
    private GestureDetectorCompat mDetector;//手势识别
    private boolean isLongClick = false;//是否长按
    private float startAngle = -90;//开始角度
    private float progress;// 进度

    private boolean isRecording = false;


    private long lastActionDownTime = 0; // 记录点击录制视频时的时间
    private long lastActionUpTime = 0;  // 记录最后一次手指抬起的时间


    public CaptureButton(Context context) {
        this(context, null);
    }

    public CaptureButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public CaptureButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CaptureButton);
        outCircleColor = array.getColor(R.styleable.CaptureButton_outCircleColor, Color.parseColor("#E0E0E0"));
        innerCircleColor = array.getColor(R.styleable.CaptureButton_innerCircleColor, Color.WHITE);
        progressColor = array.getColor(R.styleable.CaptureButton_progressColor, Color.GREEN);
        mLoadingTime = array.getInteger(R.styleable.CaptureButton_maxSeconds, 15000);
    }

    private void resetParams() {
        width = getWidth();
        height = getHeight();
        circleWidth = width * 0.13f;
        outRaduis = (float) (Math.min(width, height) / 2.4);
        innerRaduis = outRaduis - circleWidth;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (width > height) {
            setMeasuredDimension(height, height);
        } else {
            setMeasuredDimension(width, width);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        resetParams();
        //画外圆
        outRoundPaint.setAntiAlias(true);
        outRoundPaint.setColor(outCircleColor);
        if (isLongClick) {
            outRaduis = (float) (Math.min(width, height) / 2.0f);
        } else {
        }
        canvas.drawCircle(width / 2, height / 2, outRaduis, outRoundPaint);
        //画内圆
        innerRoundPaint.setAntiAlias(true);
        innerRoundPaint.setColor(innerCircleColor);
        if (isLongClick) {
            // 内圆缩小为原来的一半
            canvas.drawCircle(width / 2, height / 2, innerRaduis / 2.0f, innerRoundPaint);
            //画外原环
            mCPaint.setAntiAlias(true);
            mCPaint.setColor(progressColor);
            mCPaint.setStyle(Paint.Style.STROKE);
            int ringWidth = 12;
            mCPaint.setStrokeWidth(ringWidth);
            RectF rectF = new RectF(ringWidth / 2.0f, ringWidth / 2.0f, width - ringWidth / 2.0f, height - ringWidth / 2.0f);
            canvas.drawArc(rectF, startAngle, 360 * (1.0f * progress / mLoadingTime), false, mCPaint);
        } else {
            canvas.drawCircle(width / 2, height / 2, innerRaduis, innerRoundPaint);
        }

    }

    private Runnable longPressRunnable = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessage(MSG_RECORD_START);
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mode != Mode.MODE_CAPTURE) {
                    postDelayed(longPressRunnable, ViewConfiguration.getLongPressTimeout());
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                removeCallbacks(longPressRunnable);
                long now = System.currentTimeMillis();
                // 先判断是否是点击
                if (!isLongClick && !isRecording && mode != Mode.MODE_RECORD) {
                    if (Math.abs(now - lastActionUpTime) > 1000) {
                        if (listener != null) {
                            listener.onCapture();
                        }
                    } else {
                        if (listener != null) {
                            listener.onCaptureError("您的操作太快了");
                        }
                    }
                }

                if (isLongClick && mode != Mode.MODE_CAPTURE) {
                    if (Math.abs(now - lastActionDownTime) > 1000) {
                        if (listener != null) {
                            listener.onCaptureRecordEnd();
                        }
                    } else {
                        // 录制时间太短
                        if (listener != null) {
                            listener.onCaptureError("录制时间太短");
                        }
                    }
                    handler.sendEmptyMessage(MSG_RECORD_END);
                }
                isRecording = false;
                lastActionUpTime = now;
                break;
        }
        return true;
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == RING_WHAT) {
                if (!isLongClick) {
                    return;
                }
                if (progress < mLoadingTime) {
                    progress = System.currentTimeMillis() - lastActionDownTime;
                    postInvalidate();
                    handler.sendEmptyMessageDelayed(RING_WHAT, 100);
                } else {
                    // 时间到了
                    if (listener != null) {
                        listener.onCaptureRecordEnd();
                    }
                    handler.sendEmptyMessage(MSG_RECORD_END);
                }
            } else if (msg.what == MSG_RECORD_START) {
                // 开始录制
                lastActionDownTime = System.currentTimeMillis();
                isRecording = true;
                if (Math.abs(lastActionUpTime - lastActionDownTime) <= 1000) {
                    if (listener != null) {
                        listener.onCaptureError("您的操作太快了");
                    }
                } else {
                    isLongClick = true;
                    if (listener != null) {
                        listener.onCaptureRecordStart();
                    }
                    handler.sendEmptyMessage(RING_WHAT);
                }
            } else if (msg.what == MSG_RECORD_END) {
                isLongClick = false;
                progress = 0;
                postInvalidate();
            }
        }
    };
    // ——————————————————————————————私有方法——————————————————————————————

    // ——————————————————————————————SET方法——————————————————————————————
    /**
     * 拍摄时长
     */
    private long mLoadingTime;

    public void setDuration(long duration) {
        mLoadingTime = duration;
    }

    /**
     * 拍摄模式
     */
    private int mode;

    public void setMode(int mode) {
        this.mode = mode;
    }

    /**
     * 操作监听
     */
    private OnProgressTouchListener listener;

    public void setOnProgressTouchListener(OnProgressTouchListener listener) {
        this.listener = listener;
    }


    /**
     * 进度触摸监听
     */
    public interface OnProgressTouchListener {
        // 拍照
        void onCapture();

        // 开始录像
        void onCaptureRecordStart();

        // 停止录像
        void onCaptureRecordEnd();

        // 拍摄异常
        void onCaptureError(String message);
    }

    /**
     * 拍摄模式
     */
    public interface Mode {
        // 拍照
        int MODE_CAPTURE = 1;

        // 录像
        int MODE_RECORD = 2;

        // 拍照+录像
        int MODE_CAPTURE_RECORD = 3;
    }

}