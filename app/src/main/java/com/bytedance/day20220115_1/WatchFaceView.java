package com.bytedance.day20220115_1;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * 自定义表盘
 */
public class WatchFaceView extends View {

    /**
     * 秒针画笔
     */
    private Paint secondPaint;

    /**
     * 分针画笔
     */
    private Paint minutePaint;

    /**
     * 时针画笔
     */
    private Paint hourPaint;

    /**
     * 刻度画笔
     */
    private Paint scalePaint;

    /**
     * 秒针颜色
     */
    private int secondColor;

    /**
     * 分针颜色
     */
    private int minuteColor;

    /**
     * 时针颜色
     */
    private int hourColor;

    /**
     * 刻度颜色
     */
    private int scaleColor;

    /**
     * 表盘背景
     */
    private int watchFaceBg;

    /**
     * 是否展示刻度
     */
    private boolean showScale;

    /**
     * 表盘背景图
     */
    private Bitmap watchFaceBgImage;

    /**
     * 源坑
     */
    private Rect srcRect;

    /**
     * 目标坑
     */
    private Rect srcDesRect;

    /**
     * 日历对象
     */
    private Calendar calendar;

    /**
     * 是否更新的标志字段
     */
    private boolean isUpdate = false;


    /**
     * 构造方法1
     *
     * @param context
     */
    public WatchFaceView(Context context) {
        this(context, null);
    }

    /**
     * 构造方法2
     *
     * @param context
     * @param attrs
     */
    public WatchFaceView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 构造方法3
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public WatchFaceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 初始化属性
        initAttrs(context, attrs);
        // 初始化画笔
        initPaints();
        // 获取日历时间
        calendar = Calendar.getInstance();
        // 设置时区
        calendar.setTimeZone(TimeZone.getDefault());
    }

    /**
     * 初始化Rect（设置源坑和目标坑）
     * 由于方法中使用了测量后的值，所以该方法应该在onMeasure方法后调用
     */
    private void initRect() {
        if (watchFaceBgImage != null) {
            // 源坑：从图片中截取，如果跟图片大小一样，那么就截取图片所有内容（相对于图片）
            srcRect = new Rect();
            srcRect.left = 0;
            srcRect.top = 0;
            srcRect.right = watchFaceBgImage.getWidth();
            srcRect.bottom = watchFaceBgImage.getHeight();
            // 目标坑：填防源坑内容的地方（相对于View）
            srcDesRect = new Rect();
            srcDesRect.left = 0;
            srcDesRect.top = 0;
            srcDesRect.right = getMeasuredWidth();
            srcDesRect.bottom = getMeasuredHeight();
        }
    }

    /**
     * 初始化画笔
     */
    private void initPaints() {
        // 初始化秒针画笔
        initSecondPaint();
        // 初始化分针画笔
        initMinutePaint();
        // 初始化时针画笔
        initHourPaint();
        // 初始化刻度画笔
        initScalePaint();
    }

    /**
     * 初始化刻度画笔
     */
    private void initScalePaint() {
        scalePaint = new Paint();
        scalePaint.setColor(scaleColor);
        scalePaint.setStyle(Paint.Style.STROKE);
        scalePaint.setStrokeWidth(3f);
        scalePaint.setAntiAlias(true);
    }

    /**
     * 初始化时针画笔
     */
    private void initHourPaint() {
        hourPaint = new Paint();
        hourPaint.setColor(hourColor);
        hourPaint.setStyle(Paint.Style.STROKE);
        hourPaint.setStrokeWidth(25f);
        hourPaint.setAntiAlias(true);
    }

    /**
     * 初始化分针画笔
     */
    private void initMinutePaint() {
        minutePaint = new Paint();
        minutePaint.setColor(minuteColor);
        minutePaint.setStyle(Paint.Style.STROKE);
        minutePaint.setStrokeWidth(8f);
        minutePaint.setAntiAlias(true);
    }

    /**
     * 初始化秒针画笔
     */
    private void initSecondPaint() {
        secondPaint = new Paint();
        secondPaint.setColor(secondColor);
        secondPaint.setStyle(Paint.Style.STROKE);
        secondPaint.setStrokeWidth(5f);
        secondPaint.setAntiAlias(true);
    }

    /**
     * 初始化属性
     *
     * @param context
     * @param attrs
     */
    private void initAttrs(Context context, AttributeSet attrs) {
        // 获取相关属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WatchFaceView);
        // 秒针颜色
        secondColor = typedArray.getColor(R.styleable.WatchFaceView_secondColor, getResources().getColor(R.color.secondDefaultColor));
        // 分针颜色
        minuteColor = typedArray.getColor(R.styleable.WatchFaceView_minuteColor, getResources().getColor(R.color.secondDefaultColor));
        // 时针颜色
        hourColor = typedArray.getColor(R.styleable.WatchFaceView_hourColor, getResources().getColor(R.color.hourDefaultColor));
        // 刻度颜色
        scaleColor = typedArray.getColor(R.styleable.WatchFaceView_scaleColor, getResources().getColor(R.color.scaleDefaultColor));
        // 表盘背景
        watchFaceBg = typedArray.getResourceId(R.styleable.WatchFaceView_watchFaceBg, -1);
        if (watchFaceBg != -1) {
            watchFaceBgImage = BitmapFactory.decodeResource(getResources(), watchFaceBg);
        }
        // 是否展示刻度
        showScale = typedArray.getBoolean(R.styleable.WatchFaceView_showScale, true);
        typedArray.recycle();
    }

    /**
     * 测量（测量自己）
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int parentWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        int parentHeightSize = MeasureSpec.getSize(heightMeasureSpec);
        // 减去外边距
        int widthTargetSize = parentWidthSize - getPaddingLeft() - getPaddingRight();
        int heightTargetSize = parentHeightSize - getPaddingTop() - getPaddingBottom();
        // 表盘View为正方形，所以长和宽需要一致。即：判断大小，取最小的值作为长宽
        int targetSize = widthTargetSize < heightMeasureSpec ? widthTargetSize : heightTargetSize;
        setMeasuredDimension(targetSize, targetSize);
        // 初始化Rect
        initRect();
    }

    /**
     * 绘制
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        long currentMillis = System.currentTimeMillis();
        calendar.setTimeInMillis(currentMillis);
        // 绘制表盘背景
        if (watchFaceBgImage != null) {
            canvas.drawBitmap(watchFaceBgImage, srcRect, srcDesRect, scalePaint);
        }
        // 绘制刻度
        if (showScale) {
            drawScale1(canvas);
        }
        // 绘制时针
        drawHour(canvas);
        // 绘制分针
        drawMinute(canvas);
        // 绘制秒针
        drawSecond(canvas);
    }

    /**
     * 当View附加到Window时调用
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isUpdate = true;
        post(new Runnable() {
            @Override
            public void run() {
                if (isUpdate) {
                    invalidate();
                    postDelayed(this, 1000);
                } else {
                    removeCallbacks(this);
                }

            }
        });
    }

    /**
     * 当View与Window分离时调用
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.isUpdate = false;
    }

    /**
     * 绘制秒针
     *
     * @param canvas
     */
    private void drawSecond(Canvas canvas) {
        // 获取当前秒钟值
        int secondValue = calendar.get(Calendar.SECOND);
        // 计算秒针总偏移角度
        float secondRoute = secondValue * 6;
        canvas.save();
        // 旋转
        canvas.rotate(secondRoute, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
        // 绘制秒针
        canvas.drawLine(getMeasuredWidth() / 2, getMeasuredHeight() / 2, getMeasuredWidth() / 2, 0, secondPaint);
        canvas.restore();
    }

    /**
     * 绘制分针
     *
     * @param canvas
     */
    private void drawMinute(Canvas canvas) {
        // 获取当前分钟值
        int minuteValue = calendar.get(Calendar.MINUTE);
        // 获取当前秒钟值
        int secondValue = calendar.get(Calendar.SECOND);
        // 计算秒针引起的分针偏移角度
        float minuteOffsetRoute = secondValue / 60f * 6f;
        // 计算分针总偏移角度
        float minuteRoute = minuteValue * 6 + minuteOffsetRoute;
        canvas.save();
        // 旋转
        canvas.rotate(minuteRoute, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
        // 绘制分针
        canvas.drawLine(getMeasuredWidth() / 2, getMeasuredHeight() / 2, getMeasuredWidth() / 2, 0, minutePaint);
        canvas.restore();
    }

    /**
     * 绘制时针
     *
     * @param canvas
     */
    private void drawHour(Canvas canvas) {
        // 获取当前小时值
        int hourValue = calendar.get(Calendar.HOUR);
        // 获取当前分钟值
        int minuteValue = calendar.get(Calendar.MINUTE);
        // 计算分针引起的时针偏移角度
        float hourOffsetRotate = minuteValue / 60f * 30f;
        // 计算时针总偏移角度
        float hourRoute = hourValue * 30 + hourOffsetRotate;
        canvas.save();
        // 旋转
        canvas.rotate(hourRoute, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
        // 绘制时针
        canvas.drawLine(getMeasuredWidth() / 2, getMeasuredHeight() / 2, getMeasuredWidth() / 2, 0, hourPaint);
        canvas.restore();
    }

    /**
     * 绘制刻度
     * 方法1：逐个绘制
     *
     * @param canvas
     */
    private void drawScale1(Canvas canvas) {
        // 表盘半径
        int watchR = getMeasuredWidth() / 2;
        // 内环半径
        int innerR = (int) (watchR * 0.8);
        // 外环半径
        int outerR = (int) (watchR * 0.9);
        // 绘制表盘轮廓
        canvas.drawCircle(watchR, watchR, watchR, scalePaint);
        // 绘制表盘中心点
        canvas.drawCircle(watchR, watchR, 15f, scalePaint);
        // 绘制刻度
        for (int i = 0; i < 12; i++) {
            double th = i * Math.PI * 2 / 12;
            // 计算内环坐标点
            int innerB = (int) (Math.cos(th) * innerR);
            int innerY = watchR - innerB;
            int innerA = (int) (Math.sin(th) * innerR);
            int innerX = watchR + innerA;
            // 计算外环坐标点
            int outerB = (int) (Math.cos(th) * outerR);
            int outerY = watchR - outerB;
            int outerA = (int) (Math.sin(th) * outerR);
            int outerX = watchR + outerA;
            // 绘制刻度
            canvas.drawLine(innerX, innerY, outerX, outerY, scalePaint);
        }
    }

    /**
     * 绘制刻度
     * 方法2：旋转绘制
     *
     * @param canvas
     */
    private void drawScale2(Canvas canvas) {
        // 表盘半径
        int watchR = getMeasuredWidth() / 2;
        // 内环半径
        int innerR = (int) (watchR * 0.8);
        // 外环半径
        int outerR = (int) (watchR * 0.9);
        // 绘制表盘轮廓
        canvas.drawCircle(watchR, watchR, watchR, scalePaint);
        // 绘制表盘中心点
        canvas.drawCircle(watchR, watchR, 15f, scalePaint);
        // 绘制刻度
        // 方法二：旋转绘制
        canvas.save();
        for (int i = 0; i < 12; i++) {
            canvas.drawLine(watchR, watchR - outerR, watchR, watchR - innerR, scalePaint);
            canvas.rotate(30, watchR, watchR);
        }
        canvas.restore();
    }
}
