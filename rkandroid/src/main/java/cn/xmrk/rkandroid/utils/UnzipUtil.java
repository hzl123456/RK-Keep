package cn.xmrk.rkandroid.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * ZIP文件压缩，解压工具类
 *
 * @author Jason
 *         来源：http://crazypawpaw.googlecode.com/svn/trunk/Android/CrazyEpub/src/com/gopawpaw/android/utils/GppZipUtils.java
 * @version 2010-12-18
 */
public class UnzipUtil {


    private static final int BUFFER = 2048;

    /**
     * 解压失败
     */
    public static final int UN_ZIP_FALSE = 0;

    /**
     * 解压成功
     */
    public static final int UN_ZIP_TRUE = 1;

    /**
     * 压缩失败
     */
    public static final int ZIP_FALSE = 2;

    /**
     * 压缩成功
     */
    public static final int ZIP_TURE = 3;

    /**
     * 不是一个有效的ZIP文件
     */
    public static final int IS_NOT_ZIP_FILE = 4;

    /**
     * 是一个有效的ZIP文件
     */
    public static final int IS_ZIP_FILE = 5;

    /**
     * 不是一个文件
     */
    public static final int IS_NOT_FILE = 6;

    /**
     * 是一个文件
     */
    public static final int IS_FILE = 7;

    /**
     * 文件不存在
     */
    public static final int FILE_NOT_EXIST = 8;

    /**
     * 文件存在
     */
    public static final int FILE_EXIST = 9;

    /**
     * 目标压缩文件已经存在
     */
    public static final int TARGET_ZIP_ALREADY_EXISTS = 10;

    /**
     * 目标解压目录已经存在
     */
    public static final int TARGET_UNZIP_DIR_ALREADY_EXISTS = 11;

    /**
     * 磁盘空间不足
     */
    public static final int NO_MORE_SPACE = 12;

    /**
     * 不可预料的压缩文件末端错误
     */
    public static final int UN_ZIP_UNANTICIPATED_ERRORS = -99;

    /**
     * 解压所需压缩文件大小的倍数
     */
    private static int UN_ZIP_SPACE_TIMES = 3;


    public UnzipUtil() {

    }

    /**
     * 解压文件，默认解压到当前压缩文件相同目录
     *
     * @param zipFile :压缩文件
     * @retrun UN_ZIP_TRUE :解压成功 <br>
     * IS_NOT_ZIP_FILE :解压文件不是一个有效的ZIP文件 <br>
     * TARGET_UNZIP_DIR_ALREADY_EXISTS :目标解压目录已经存在<br>
     * IS_NOT_FILE:不是一个文件<br>
     * NO_MORE_SPACE:sdcard空间不足<br>
     * UN_ZIP_FALSE:解压失败
     */
    public static int unZip(File zipFile) {
        if (zipFile == null) {
            return IS_NOT_FILE;
        }
        String filepath = zipFile.getAbsoluteFile().toString();
        int i = filepath.lastIndexOf(".");
        return unZip(zipFile, filepath.substring(0, i));

    }

    /**
     * 解压文件
     *
     * @param zipFile    :压缩文件
     * @param targetPath :解压目标路径
     * @retrun UN_ZIP_TRUE :解压成功 <br>
     * IS_NOT_ZIP_FILE :解压文件不是一个有效的ZIP文件 <br>
     * TARGET_UNZIP_DIR_ALREADY_EXISTS :目标解压目录已经存在
     * NO_MORE_SPACE:sdcard空间不足<br>
     * UN_ZIP_FALSE:解压失败
     */
    public static int unZip(File zipFile, String targetPath) {

        return unZip(zipFile, targetPath, false);
    }

    /**
     * 解压文件
     *
     * @param zipFile    :压缩文件
     * @param targetPath :解压目标路径
     * @param reUnZip    :重新解压，reUnZip=true 若目标文件已存在，则删除原解压文件后再重新解压; reUnZip=false
     *                   若目标文件已存在,则不重新解压。
     * @retrun UN_ZIP_TRUE :解压成功 <br>
     * IS_NOT_ZIP_FILE :解压文件不是一个有效的ZIP文件 <br>
     * TARGET_UNZIP_DIR_ALREADY_EXISTS :目标解压目录已经存在<br>
     * NO_MORE_SPACE:sdcard空间不足<br>
     * UN_ZIP_FALSE:解压失败
     */
    public static int unZip(File zipFile, String targetPath,
                            boolean reUnZip) {
        //不是ZIP文件
        if (isZipFile(zipFile.getAbsolutePath()) == IS_NOT_ZIP_FILE) {
            return IS_NOT_ZIP_FILE;
        }

        //删除已经存在的解压目录
        if (reUnZip && isExistTargetDir(targetPath) == TARGET_UNZIP_DIR_ALREADY_EXISTS) {
            File f = new File(targetPath);
            f.delete();
            //不覆盖已经存在的解压目录
        } else if (!reUnZip && isExistTargetDir(targetPath) == TARGET_UNZIP_DIR_ALREADY_EXISTS) {
            return TARGET_UNZIP_DIR_ALREADY_EXISTS;
        }

        //sdcard空间不足
        if (getFreeSize() < getZipFileSize(zipFile) * getUN_ZIP_SPACE_TIMES()) {
            return NO_MORE_SPACE;
        }

        targetPath = targetPath.trim();
        try {
            BufferedOutputStream dest = null;
            FileInputStream fis = new FileInputStream(zipFile.getAbsoluteFile());
            ZipInputStream zis = new ZipInputStream(
                    new BufferedInputStream(fis));

            File outDir = new File(targetPath);
            if (!outDir.exists()) {
                outDir.mkdir();

            }

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    File file = new File(targetPath + File.separator
                            + entry.getName());
                    file.mkdirs();
                } else {
                    int count;
                    byte data[] = new byte[BUFFER];
                    // write the files to the disk
                    FileOutputStream fos = new FileOutputStream(targetPath
                            + File.separator + entry.getName());
                    dest = new BufferedOutputStream(fos, BUFFER);
                    while ((count = zis.read(data, 0, BUFFER)) != -1) {

                        dest.write(data, 0, count);
                        if (count == 0) {//不可预料的压缩文件末端错误
                            return UN_ZIP_UNANTICIPATED_ERRORS;
                        }
                    }
                    dest.flush();
                    dest.close();
                }

            }
            zis.close();
        } catch (Exception e) {
            e.printStackTrace();
            return UN_ZIP_FALSE;
        }
        return UN_ZIP_TRUE;
    }

    /**
     * 判断是否为ZIP文件
     *
     * @return IS_ZIP_FILE或IS_NOT_ZIP_FILE
     */
    public static int isZipFile(String zipFilePath) {

        return IS_ZIP_FILE;
    }

    /**
     * 是否存在解压目录
     *
     * @return TARGET_UNZIP_DIR_ALREADY_EXISTS 或 0
     */
    public static int isExistTargetDir(String targetPath) {
        File f = new File(targetPath);
        if (f.exists()) {
            return TARGET_UNZIP_DIR_ALREADY_EXISTS;
        } else {
            return 0;
        }

    }

    /**
     * 获取可用空间大小B
     */
    public static long getFreeSize() {

        android.os.StatFs statfs = new android.os.StatFs(PhoneUtil.getSDCardPath());

        // 获取SDCard上BLOCK总数
//	    long nTotalBlocks = statfs.getBlockCount();

        // 获取SDCard上每个block的SIZE
        long nBlocSize = statfs.getBlockSize();

        // 获取可供程序使用的Block的数量
        long nAvailaBlock = statfs.getAvailableBlocks();

        // 获取剩下的所有Block的数量(包括预留的一般程序无法使用的块)
//	    long nFreeBlock = statfs.getFreeBlocks();

        // 计算SDCard 总容量大小MB
//	    long nSDTotalSize = nTotalBlocks * nBlocSize / 1024 / 1024;

        // 计算 SDCard 剩余大小B
        long nSDFreeSize = nAvailaBlock * nBlocSize;
        return nSDFreeSize;
    }

    /**
     * 获取压缩文件大小B
     *
     * @throws IOException
     */
    public static long getZipFileSize(File file) {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                size = fis.available();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            return (long) 0;
        }
        return size;
    }

    /**
     * 获取解压比例倍数
     */
    public static int getUN_ZIP_SPACE_TIMES() {
        return UN_ZIP_SPACE_TIMES;
    }

    public static void setUN_ZIP_SPACE_TIMES(int uN_ZIP_SPACE_TIMES) {
        UN_ZIP_SPACE_TIMES = uN_ZIP_SPACE_TIMES;
    }

    public static void extractZipFile(String zipPath, String targetPath) throws IOException {
        int _c = UnzipUtil.unZip(new File(zipPath), targetPath);
        if (_c == UnzipUtil.NO_MORE_SPACE) {
            throw new IOException("没有更多空间");
        } else if (_c == UnzipUtil.UN_ZIP_UNANTICIPATED_ERRORS) {
            throw new IOException("未知的文件末端");
        } else if (_c == UnzipUtil.UN_ZIP_FALSE) {
            throw new IOException("未知错误");
        }
    }

}
