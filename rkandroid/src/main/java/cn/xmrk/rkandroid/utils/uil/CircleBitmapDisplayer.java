package cn.xmrk.rkandroid.utils.uil;

/**
 * 创建日期： 2015/11/12.
 */

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class CircleBitmapDisplayer implements BitmapDisplayer {
    private float radius, centerX, centerY, borderWidth = 0;
    private int borderColor = Color.BLACK;
    private boolean biggestCircle = false, isCentered = true;

    public CircleBitmapDisplayer() {
        this.biggestCircle = true;
    }

    public CircleBitmapDisplayer(float centerX, float centerY) {
        this();
        this.centerX = centerX;
        this.centerY = centerY;
        this.isCentered = false;
    }

    public CircleBitmapDisplayer(float borderWidth, int borderColor) {
        this();
        this.borderWidth = borderWidth;
        this.borderColor = borderColor;
    }

    public CircleBitmapDisplayer(float radius) {
        this.radius = radius;
    }

    public CircleBitmapDisplayer(float radius, float borderWidth,
                                 int borderColor) {
        this(radius);
        this.borderWidth = borderWidth;
        this.borderColor = borderColor;
    }

    public CircleBitmapDisplayer(float radius, float centerX, float centerY) {
        this(radius);
        this.centerX = centerX;
        this.centerY = centerY;
        this.isCentered = false;
    }

    public CircleBitmapDisplayer(float radius, float centerX, float centerY,
                                 float borderWidth, int borderColor) {
        this(radius, centerX, centerY);
        this.borderWidth = borderWidth;
        this.borderColor = borderColor;
    }

    @Override
    public void display(Bitmap bitmap, ImageAware imageAware,
                        LoadedFrom loadedFrom) {
        if (!(imageAware instanceof ImageViewAware)) {
            throw new IllegalArgumentException(
                    "ImageAware should wrap ImageView. ImageViewAware is expected.");
        }
        int ivWidth = imageAware.getWidth();
        int ivHeight = imageAware.getHeight();
        int bmWidth = bitmap.getWidth();
        int bmHeight = bitmap.getHeight();

        if (isCentered) {
            centerX = (float) ivWidth / 2;
            centerY = (float) ivHeight / 2;
        }
        if (biggestCircle) {
            if (isCentered) {
                radius = ivWidth < ivHeight ? (float) ivWidth / 2
                        : (float) ivHeight / 2;
            } else {
                radius = Math.min(centerX < ivWidth - centerX ? centerX
                                : ivWidth - centerX,
                        centerY < ivHeight - centerY ? centerX : ivHeight
                                - centerY);
            }
        }
        Rect srcRect;
        if (bmWidth < bmHeight) {
            srcRect = new Rect(0, (bmHeight - bmWidth) / 2, bmWidth, bmWidth
                    + (bmHeight - bmWidth) / 2);
        } else {
            srcRect = new Rect((bmWidth - bmHeight) / 2, 0, bmHeight
                    + (bmWidth - bmHeight) / 2, bmHeight);
        }
        RectF destRectF = new RectF(0, 0, ivWidth, ivHeight);
        imageAware.setImageBitmap(getCircledBitmap(bitmap, centerX, centerY,
                radius, srcRect, destRectF, ivWidth, ivHeight, borderWidth,
                borderColor));

    }

    public static Bitmap getCircledBitmap(Bitmap bitmap, float centerX,
                                          float centerY, float radius, Rect srcRect, RectF destRectF,
                                          int width, int height, float borderWidth, int borderColor) {

        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        paint.setAntiAlias(true);

        // if 1 pixel is missing, do: radius - borderWidth + 1
        canvas.drawCircle(centerX, centerY, radius - borderWidth, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, srcRect, destRectF, paint);
        if (0 < borderWidth) {
            paint.setXfermode(null);
            paint.setStyle(Style.STROKE);
            paint.setColor(borderColor);
            paint.setStrokeWidth(borderWidth);
            canvas.drawCircle(centerX, centerY, radius - borderWidth / 2, paint);
        }
        return output;
    }
}