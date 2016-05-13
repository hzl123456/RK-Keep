package rkkeep.keep.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import cn.xmrk.rkandroid.utils.CommonUtil;

/**
 * Created by Au61 on 2016/5/13.
 */
public class HandWritingView extends View {


    private Path downPath = null;

    private Bitmap mBackBitmap;

    private Bitmap newBitmap;

    /**
     * 绘画的画笔
     **/
    private Paint mPaint = null;

    /**
     * 画笔的大小
     **/
    private float strokeWidth = 5.0f;

    /**
     * 画出来的path
     **/
    private List<Path> mPathInfos;

    /**
     * 被删除的path
     **/
    private List<Path> removePath;

    /**
     * 画图使用
     **/
    private float pointX;
    private float pointY;


    public HandWritingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HandWritingView(Context context) {
        super(context);
    }

    /**
     * 实例化画笔
     **/
    private void initPaint() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(strokeWidth);
    }

    /**
     * 处理背景图片，将其等比例放大到当前手机的分辨率
     **/
    private Bitmap initBackBitmap(Bitmap bitmap) {
        //将要放大缩小的比例
        float size = 1.0f;
        Matrix matrix = new Matrix();
        boolean hasBitHeight = bitmap.getHeight() > bitmap.getWidth();
        if (hasBitHeight) {//此时图片更高
            size = ((float) getMaxHeight()) / bitmap.getHeight();
        } else {//此时图片更宽
            size = ((float) getMaxWidth()) / bitmap.getWidth();
        }
        matrix.setScale(size, size);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        return bitmap;
    }

    public int getMaxHeight() {
        return CommonUtil.getScreenDisplay().getHeight() - CommonUtil.getStateBarHeight() - CommonUtil.dip2px(56) * 2;
    }

    public int getMaxWidth() {
        return CommonUtil.getScreenDisplay().getWidth();
    }


    public void setBitmap(Bitmap backBitmap) {
        if (backBitmap != null) {//判断是否存在背景图，来判断大小
            //背景采用导入的图片作为背景
            this.mBackBitmap = initBackBitmap(backBitmap);
        } else {
            this.mBackBitmap = getWhiteBitmap();
        }
        //初始实例化
        mPathInfos = new ArrayList<>();
        removePath = new ArrayList<>();
        initPaint();
        measure(mBackBitmap.getWidth(), mBackBitmap.getHeight());
        //布局至中心
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, (getMaxHeight() - mBackBitmap.getHeight()) / 2, 0, 0);
        setLayoutParams(layoutParams);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBackBitmap == null) {
            return;
        }
        canvas.drawBitmap(getDrawBitmap(), 0, 0, null);
    }

    private Bitmap getWhiteBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getMaxWidth(), getMaxHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        return bitmap;
    }

    private Bitmap getDrawBitmap() {
        //原始的背景保持不变，只改变为新的背景
        if (newBitmap != null) {//回收上次使用的
            newBitmap.recycle();
            newBitmap = null;
        }
        newBitmap = Bitmap.createBitmap(mBackBitmap, 0, 0, mBackBitmap.getWidth(), mBackBitmap.getHeight());
        Canvas canvas = new Canvas(newBitmap);
        Log.i("size-->", mPathInfos.size() + "");
        for (int i = 0; i < mPathInfos.size(); i++) {
            onDrawLine(canvas, mPathInfos.get(i));
        }
        //判断是否可以后退
        if (mPathInfos.size() == 0) {
            monDrawingListener.canBack(false);
        } else {
            monDrawingListener.canBack(true);
        }
        //判断时候可以前进
        if (removePath.size() == 0) {
            monDrawingListener.canFront(false);
        } else {
            monDrawingListener.canFront(true);
        }
        return newBitmap;
    }

    public void drawFront() {
        Path path = removePath.get(0);
        removePath.remove(path);
        mPathInfos.add(0, path);
        invalidate();
    }

    public void drawBack() {
        Path path = mPathInfos.get(0);
        mPathInfos.remove(path);
        removePath.add(0, path);
        invalidate();
    }

    private void onDrawLine(Canvas canvas, Path info) {
        canvas.drawPath(info, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mBackBitmap == null) {
            return super.onTouchEvent(event);
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //每次点击下去的时候都会生成一个path
            downPath = new Path();
            mPathInfos.add(0, downPath);
            downPath.moveTo(event.getX(), event.getY());
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            pointX = event.getX();
            pointY = event.getY();
            downPath.lineTo(pointX, pointY);
            downPath.moveTo(pointX, pointY);
            invalidate();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {//起来的时候结束一个path
            pointX = event.getX();
            pointY = event.getY();
            downPath.lineTo(pointX, pointY);
            downPath.moveTo(pointX, pointY);
            invalidate();
        }
        return true;
    }

    public void setPaintColor(String color) {
        mPaint.setColor(Color.parseColor(color));
    }

    private onDrawingListener monDrawingListener;

    public void setonDrawingListener(onDrawingListener monDrawingListener) {
        this.monDrawingListener = monDrawingListener;
    }

    public interface onDrawingListener {

        void canBack(boolean canBack);

        void canFront(boolean canFront);

    }
}
