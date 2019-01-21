package rkkeep.keep.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
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

    private int nowColor;
    private float nowStokeWidth;

    /**
     * 表示是否为清除
     **/
    private boolean isClear;

    private DrawPath downPath = null;

    private Bitmap mBackBitmap;

    private Bitmap newBitmap;


    private List<Paint> paints;


    /**
     * 画出来的path
     **/
    private List<DrawPath> mPathInfos;

    /**
     * 被删除的path
     **/
    private List<DrawPath> removePath;

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
     * 实例化一个画笔，并且添加到画笔之中
     **/
    private void initPaint(int color, float width) {
        Paint mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setColor(color);
        mPaint.setStrokeWidth(width);
        if (paints == null) {
            paints = new ArrayList<>();
        }
        paints.add(mPaint);
        nowColor = color;
        nowStokeWidth = width;
    }

    /**
     * 处理背景图片，将其等比例放大到当前手机的分辨率
     **/
    private Bitmap initBackBitmap(Bitmap bitmap) {
        //将要放大缩小的比例
        float size = 1.0f;
        Matrix matrix = new Matrix();
        //计算根据高还是宽来放大缩小
        //标准的高宽比
        float defaultSize = ((float) getMaxHeight()) / getMaxWidth();
        //图片的高宽比
        float bitmapSize = ((float) bitmap.getHeight()) / bitmap.getWidth();
        boolean hasBitHeight = bitmapSize > defaultSize;
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
        measure(mBackBitmap.getWidth(), mBackBitmap.getHeight());
        //布局至中心
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins((getMaxWidth() - mBackBitmap.getWidth()) / 2, (getMaxHeight() - mBackBitmap.getHeight()) / 2, 0, 0);
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

    public Bitmap getDrawBitmap() {
        //原始的背景保持不变，只改变为新的背景
        if (newBitmap != null) {//回收上次使用的
            newBitmap.recycle();
            newBitmap = null;
        }
        newBitmap = Bitmap.createBitmap(mBackBitmap, 0, 0, mBackBitmap.getWidth(), mBackBitmap.getHeight()).copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(newBitmap);
        //从后面的往前画
        for (int i = mPathInfos.size() - 1; i >= 0; i--) {
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
        DrawPath path = removePath.get(0);
        removePath.remove(path);
        mPathInfos.add(0, path);
        invalidate();
    }

    public void drawBack() {
        DrawPath path = mPathInfos.get(0);
        mPathInfos.remove(path);
        removePath.add(0, path);
        invalidate();
    }

    private void onDrawLine(Canvas canvas, DrawPath info) {
        canvas.drawPath(info.path, paints.get(info.position));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mBackBitmap == null) {
            return super.onTouchEvent(event);
        }
        if (nowStokeWidth != paints.get(paints.size() - 1).getStrokeWidth()) {
            initPaint(nowColor, nowStokeWidth);
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            pointX = event.getX();
            pointY = event.getY();

            //每次点击下去的时候都会生成一个path
            if (isClear) {
                choosePath(event.getX(), event.getY());
            } else {
                downPath = new DrawPath(new Path(), paints.size() - 1);
                mPathInfos.add(0, downPath);
                downPath.path.moveTo(pointX, pointY);
                downPath.points.add(new Point(pointX, pointY));
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (isClear) {
                choosePath(event.getX(), event.getY());
            } else {
                final float x = event.getX();
                final float y = event.getY();

                final float previousX = pointX;
                final float previousY = pointY;

                final float dx = Math.abs(x - previousX);
                final float dy = Math.abs(y - previousY);

                //两点之间的距离大于等于3时，生成贝塞尔绘制曲线
                if (dx >= 3 || dy >= 3) {
                    //设置贝塞尔曲线的操作点为起点和终点的一半
                    float cX = (x + previousX) / 2;
                    float cY = (y + previousY) / 2;

                    //二次贝塞尔，实现平滑曲线；previousX, previousY为操作点，cX, cY为终点
                    downPath.path.quadTo(previousX, previousY, cX, cY);

                    //第二次执行时，第一次结束调用的坐标值将作为第二次调用的初始坐标值
                    pointX = x;
                    pointY = y;

                    downPath.points.add(new Point(pointX, pointY));
                }
            }
        }
        invalidate();
        return true;
    }

    public void setNowStokeWidth(float width) {
        this.nowStokeWidth = width;

    }

    /**
     * 橡皮擦删除,实现方法有点蛋疼，没办法，暂时这样了
     **/
    private void choosePath(float x, float y) {
        List<DrawPath> drawPaths = new ArrayList<>();
        List<Point> points = null;
        float width;
        RectF rf = null;
        Point firstPoint = null;
        Point secondPoint = null;
        //最大最小值
        float minX;
        float maxX;
        float minY;
        float maxY;
        for (int i = 0; i < mPathInfos.size(); i++) {
            //获取当前路径上的点
            points = mPathInfos.get(i).points;
            //获取绘制当前路径的画笔的宽的一半
            width = paints.get(mPathInfos.get(i).position).getStrokeWidth() / 2;
            //遍历判断按下去的点是否在path的点里面
            for (int j = 0; j < points.size() - 1; j++) {
                firstPoint = points.get(j);
                secondPoint = points.get(j + 1);
                //计算两点间的距离
                minX = Math.min(firstPoint.pointX, secondPoint.pointX);
                maxX = Math.max(firstPoint.pointX, secondPoint.pointX);
                minY = Math.min(firstPoint.pointY, secondPoint.pointY);
                maxY = Math.max(firstPoint.pointY, secondPoint.pointY);
                //取相连的两个点连接进行判断
                if ((minX < x && x < maxX) && (minY < y && y < maxY)) {
                    drawPaths.add(mPathInfos.get(i));
                    break;
                }
            }
        }
        mPathInfos.removeAll(drawPaths);
        removePath.addAll(0, drawPaths);
    }

    public void setIsClear(boolean isClear) {
        this.isClear = isClear;
    }

    public void clearDraw() {
        mPathInfos.clear();
        removePath.clear();
        invalidate();
    }

    public void setPaint(int color, float size) {
        initPaint(color, size);
    }

    private onDrawingListener monDrawingListener;

    public void setonDrawingListener(onDrawingListener monDrawingListener) {
        this.monDrawingListener = monDrawingListener;
    }

    public interface onDrawingListener {

        void canBack(boolean canBack);

        void canFront(boolean canFront);
    }

    class DrawPath {
        public Path path;
        public int position;
        public List<Point> points;

        public DrawPath(Path path, int position) {
            this.path = path;
            this.position = position;
            this.points = new ArrayList<>();
        }
    }

    class Point {
        public float pointX;
        public float pointY;

        public Point(float pointX, float pointY) {
            this.pointX = pointX;
            this.pointY = pointY;
        }
    }

    /**
     * 判断时候含有绘图
     **/
    public boolean hasDrawBitmap() {
        return (mPathInfos != null && mPathInfos.size() > 0);
    }

}