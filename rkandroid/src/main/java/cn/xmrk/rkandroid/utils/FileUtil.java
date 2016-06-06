package cn.xmrk.rkandroid.utils;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.xmrk.rkandroid.application.RKApplication;

public class FileUtil {

    private static final Logger log = Logger.getLogger(FileUtil.class);

    public static void writeBitmapToFile(final Bitmap bitmap, final String filePath, final WriteCallback callback) {
        writeBitmapToFile(bitmap, CompressFormat.PNG, filePath, callback);
    }

    public static void writeBitmapToFile(final Bitmap bitmap, final CompressFormat format, final String filePath, final WriteCallback callback) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... arg0) {

                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(filePath);
                    bitmap.compress(format, 100, fos);
                } catch (FileNotFoundException e) {
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {

                if (callback != null) {
                    callback.onFinish(filePath);
                }
            }
        }.execute();

    }

    /**
     * 把data写入到文件当中
     *
     * @param data
     * @param filePath
     * @param append   {@code true}时，将文字添加到文件末尾
     */
    public static void write2File(String data, String filePath, boolean append) {

        File file = new File(filePath);
        if (!file.getParentFile().isDirectory()) {
            file.getParentFile().mkdirs();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, append);
            fos.write(data.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }

    public static void copy(String sourcePath, String targetPath) throws IOException {
        try {
            File file = new File(sourcePath);
            if (file.isDirectory()) {
                // 拷贝的是文件夹，目标地址也应该设置为地址
                File targetDir = new File(targetPath);
                targetDir.mkdir();
                for (File subFile : file.listFiles()) {
                    String subFilePath = subFile.getAbsolutePath();
                    String targetFilePath = targetPath + File.separator + subFile.getName();
                    copy(subFilePath, targetFilePath);
                    log.info("拷贝 : " + subFile + "  -->  " + targetFilePath);
                }
            } else {
                inputStream2File(new FileInputStream(sourcePath), targetPath);
            }
        } catch (FileNotFoundException e) {
            log.error("文件拷贝错误", e);
            throw e;
        } catch (IOException e) {
            log.error("文件拷贝错误", e);
            throw e;
        }
    }

    public static void inputStream2File(InputStream is, String filePath) throws IOException {

        if (is == null) {
            return;
        }
        int len = 0;
        byte[] buf = new byte[2048];
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
        } catch (FileNotFoundException e) {
            log.error(e);
            throw e;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }

    }

    /**
     * 确保文件路径存在
     *
     * @param path
     */
    public static void checkedFileReachable(String path) {
        File file = new File(path);
        if (!file.getParentFile().isDirectory()) {
            boolean cs = file.getParentFile().mkdirs();
            log.debug("创建成功 -> " + cs + ", 路径 -> " + file.getParentFile().getAbsolutePath());
        }
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 文件是否存在
     *
     * @param path
     * @return 文件存在时返回 {@code true}
     */
    public static boolean fileExists(String path) {
        File file = new File(path);
        return file.exists();
    }

    public static void openFile(String path) {
        Intent intent = new Intent(Intent.ACTION_DEFAULT);
        path = path.replaceFirst("/file:", "file://");
//		intent.setData(Uri.parse(path));
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(path));
//		mimeType = mimeType.substring(0, mimeType.indexOf('/')) + "/*";
        intent.setDataAndType(Uri.parse(path), mimeType);
        log.info("mimeType -> " + mimeType + ", path -> " + path);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            RKApplication.getInstance().startActivity(intent);
        } catch (Exception e) {
            intent.setType("*/*");
            RKApplication.getInstance().startActivity(intent);
        }
    }

    /**
     * uri转化为绝对路径地址
     *
     * @param uri
     * @return
     */
    public static String uri2Path(Uri uri) {
        if (uri == null) {
            return null;
        }
        if (StringUtil.isEqualsString(uri.getScheme(), "file")) {
            return uri.getPath();
        }
        // 查询，返回cursor
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = RKApplication.getInstance().getContentResolver().query(uri, projection, null, null, null);
        try {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            cursor.close();
        }
    }

    public interface WriteCallback {
        void onFinish(String filePath);
    }

    // 图片按比例大小压缩方法（根据路径获取图片并压缩）
    public static Bitmap getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是1280*720分辨率，所以高和宽我们设置为
        Point _screenSize = PhoneUtil.getScreenDisplay(RKApplication.getInstance());
        float hh = _screenSize.x; // 1280f;// 这里设置高度为1280f
        float ww = _screenSize.y; // 720f;// 这里设置宽度为720f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
    }

    // 质量压缩法
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 300) { // 循环判断如果压缩后图片是否大于300kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 5;// 每次都减少5
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    // 将图片保存为文件,png格式
    public static void saveBmpToFilePng(Bitmap bmp, File file) {
        if (!file.isFile()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.PNG, 100, baos);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 回收图片
     **/
    public static void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            // 回收并且置为null
            bitmap.recycle();
            bitmap = null;
        }
        System.gc();
    }
}
