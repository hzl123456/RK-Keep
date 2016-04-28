package cn.xmrk.rkandroid.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author	思落羽
 * 2014年4月14日 下午3:04:31
 */
public class ImageUtil {
	
	/**
	 * Bitmap转Drawable
	 * @param bitmap 
	 * @return
	 */
	public static final Drawable bitmap2Drawable(Bitmap bitmap) {
		@SuppressWarnings("deprecation")
		BitmapDrawable bd = new BitmapDrawable(bitmap);
		return bd;
	}
	
	/**
	 * Bitmap转byte数组
	 * @param bitmap
	 * @return
	 */
	public static final byte[] bitmap2Bytes(Bitmap bitmap) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		/* 直接压缩就可以获得字节数组 */
		bitmap.compress(CompressFormat.JPEG, 100, bos);
		byte[] result = bos.toByteArray();
		try {
			bos.close();
		} catch (IOException e) {}
		bos = null;
		return result;
	}
	
	/**
	 * Drawable转Bitmap
	 * @param drawable
	 * @return
	 */
	public static final Bitmap drawable2Bitmap(Drawable drawable) {
		if (drawable == null) {
			return null;
		}
		/* ARGB_8888 支持透明 */
		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.draw(canvas);
		return bitmap;
	}
	
	/**
	 * byte数组转Bitmap
	 * @param imageBytes
	 * @return
	 */
	public static final Bitmap bytes2Bitmap(byte[] imageBytes) {
		return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
	}
	
	/**
	 * 比例缩放防止OOM
	 * @param imageBytes
	 * @param scale
	 * @return
	 */
	public static final Bitmap bytes2BitmapByScale(byte[] imageBytes, int scale) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inScaled = true;
		opts.inSampleSize = scale;
		return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, opts);
	}
	
	/**
	 * Drawable转字节数组，可以先把Drawable转Bitmap再转数组
	 * @param drawable
	 * @return
	 */
	public static final byte[] drawable2Bytes(Drawable drawable) {
		return bitmap2Bytes(drawable2Bitmap(drawable));
	}
	
	public static final Drawable bytes2Drawable(byte[] imageBytes) {
		Bitmap bitmap = bytes2Bitmap(imageBytes);
		Drawable result = bitmap2Drawable(bitmap);
		bitmap.recycle();
		bitmap = null;
		return result;
	}
	
}
