package com.ian.android.signingboard.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 签名板控件
 * 
 * @author Ian(https://github.com/Ian-Li)
 * @version 1.0
 * 
 */
public class SigningBoardView extends View {

    private static final float TOUCH_TOLERANCE = 4f;

    private Paint strokePaint;// 画笔
    private List<Path> strokePaths = new ArrayList<Path>(50);// 笔划
    private PointF lastPoint = new PointF();

    public SigningBoardView(Context context) {
        super(context);
        onCreate();
    }

    public SigningBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onCreate();
    }

    public SigningBoardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        onCreate();
    }

    /**
     * 初始化
     * 
     */
    protected void onCreate() {
        setFocusableInTouchMode(true);

        strokePaint = new Paint();
        strokePaint.setColor(Color.BLACK);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setAntiAlias(true);
        strokePaint.setDither(true);
        strokePaint.setStrokeWidth(5f);
        strokePaint.setStrokeJoin(Paint.Join.ROUND);
        strokePaint.setStrokeCap(Paint.Cap.ROUND);
    }

    /**
     * 触屏事件转化为绘图Path
     * 
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();

        Path currentStrokePath;
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            currentStrokePath = new Path();
            currentStrokePath.moveTo(x, y);

            strokePaths.add(currentStrokePath);
            break;
        case MotionEvent.ACTION_MOVE:
        case MotionEvent.ACTION_UP:
            currentStrokePath = strokePaths.get(strokePaths.size() - 1);

            float dx = Math.abs(x - lastPoint.x);
            float dy = Math.abs(y - lastPoint.y);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                /* 利用二次贝赛尔曲线画平滑曲线 */
                currentStrokePath.quadTo(lastPoint.x, lastPoint.y, (x + lastPoint.x) / 2,
                        (y + lastPoint.y) / 2);
            }
            break;
        }

        lastPoint.x = x;
        lastPoint.y = y;

        invalidate();
        return true;
    }

    /**
     * 绘制当前笔划
     * 
     */
    @Override
    protected void onDraw(Canvas canvas) {
        int strokeSize = strokePaths.size();
        for (int i = 0; i < strokeSize; i++) {
            canvas.drawPath(strokePaths.get(i), strokePaint);
        }
    }

    /**
     * 获取签名图像
     * 
     */
    public Bitmap getSignDrawable() {
        Drawable preBackground = getBackground();
        setBackgroundColor(0xff888888);

        Bitmap signDrawableBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(signDrawableBitmap);
        draw(canvas);

        setBackgroundDrawable(preBackground);

        return signDrawableBitmap;
    }

    /**
     * 清除签名
     * 
     */
    public void clear() {
        strokePaths.clear();
        invalidate();
    }
}
