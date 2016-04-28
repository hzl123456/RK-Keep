package cn.xmrk.rkandroid.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import cn.xmrk.rkandroid.R;
import cn.xmrk.rkandroid.application.RKApplication;
import cn.xmrk.rkandroid.config.RKConfigHelper;

/**
 * 创建日期： 2015/11/12.
 */
public class RKUtil {

    public static void displayFileImage(String url, ImageView iv) {
        if (url != null) {
            url = url.startsWith("file://") ? url : "file://" + url;
        }
        ImageLoader.getInstance().displayImage(url, iv);
    }

    public static void displayImage(String url, ImageView iv) {
        if (url != null) {
            url = url.startsWith("http://") ? url : RKConfigHelper.getInstance().getBaseUrl() + url;
        }
        ImageLoader.getInstance().displayImage(url, iv);
    }

    public static void displayImage(String url, ImageView iv, DisplayImageOptions dio) {
        if (url != null) {
            url = url.startsWith("http://") ? url : RKConfigHelper.getInstance().getBaseUrl() + url;
        }
        ImageLoader.getInstance().displayImage(url, iv, dio);
    }

    public static void loadImage(String url, ImageLoadingListener listener) {
        url = url.startsWith("http://") ? url : RKConfigHelper.getInstance().getBaseUrl() + url;
        ImageLoader.getInstance().loadImage(url, listener);
    }

    /**
     * @param def 默认图片
     * @return
     */
    public static DisplayImageOptions generateDisplayImageOptions(int def) {
        return new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageForEmptyUri(def)
                .showImageOnFail(def)
                .showImageForEmptyUri(def)
                .build();
    }

    public static int getValueOfColorAttr(int attr) {
        TypedValue typedValue = new TypedValue();
        Context _context = RKApplication.getInstance();
        _context.getTheme().resolveAttribute(android.R.attr.textAppearanceLarge, typedValue, true);
        int[] attribute = new int[]{attr};
        TypedArray array = _context.obtainStyledAttributes(typedValue.resourceId, attribute);
        int _color = array.getColor(0 /* index */, _context.getResources().getColor(R.color.bg_title_bar) /* default size */);
        array.recycle();
        return _color;
    }

    public static int getValueOfSizeAttr(int attr) {
        TypedValue typedValue = new TypedValue();
        Context _context = RKApplication.getInstance();
        _context.getTheme().resolveAttribute(attr, typedValue, true);
        int[] attribute = new int[]{android.R.attr.textSize};
        TypedArray array = _context.obtainStyledAttributes(typedValue.resourceId, attribute);
        int _textSize = array.getDimensionPixelSize(0 /* index */, -1 /* default size */);
        array.recycle();
        return _textSize;
    }

}
