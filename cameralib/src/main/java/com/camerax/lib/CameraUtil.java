package com.camerax.lib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;
import android.util.Size;

import androidx.exifinterface.media.ExifInterface;

import com.camerax.lib.core.ExAspectRatio;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Copyright (C) 2017
 * 版权所有
 * <p>
 * 功能描述：
 * <p>
 * 作者：yijiebuyi
 * 创建时间：2020/7/23
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */

public class CameraUtil {
    private CameraUtil() {}

    /**
     * 根据比例，计算图片的分辨率
     * @param ratio
     * @param screenWidth
     * @return
     */
    public static Size computeSize(@ExAspectRatio.ExRatio int ratio, int screenWidth) {
        Size size = null;
        switch (ratio) {
            case ExAspectRatio.RATIO_1_1:
                size = new Size(screenWidth, screenWidth);
                break;
            case ExAspectRatio.RATIO_4_3:
                size = new Size(screenWidth, screenWidth * 4 / 3);
                break;
            case ExAspectRatio.RATIO_16_9:
                size = new Size(screenWidth, screenWidth * 16 / 9);
                break;
        }

        return size;
    }

    public static Bitmap getBitmap(InputStream in) {
        Bitmap image = null;
        try {
            image = BitmapFactory.decodeStream(in);
        } catch (OutOfMemoryError err) {

            err.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return image;
    }

    public static File cropSquare(File file, boolean mirror) {
        File temp = file;
        try {
            long s = System.currentTimeMillis();
            Bitmap srcBitmap = getBitmap(new FileInputStream(file));
            Log.i("aaa", "===gb===" + (System.currentTimeMillis() - s));
            int w = srcBitmap.getWidth();
            int h = srcBitmap.getHeight();
            if (w == h) {
                return file;
            }

            int size = Math.min(w, h);
            
            int offsetX = (w - size) / 2;
            int offsetY = (h - size) / 2;

            Bitmap target = null;
            if (mirror) {
                Matrix m = new Matrix();
                m.postScale(-1, 1);   //镜像水平翻转
                target = Bitmap.createBitmap(srcBitmap, offsetX, offsetY, size, size, m, true);
            } else {
                target = Bitmap.createBitmap(srcBitmap, offsetX, offsetY, size, size);
            }
            Log.i("aaa", "===cb===" + (System.currentTimeMillis() - s));

            temp = new File(file.getAbsoluteFile() + ".temp");
            OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
            target.compress(Bitmap.CompressFormat.JPEG, 100, os);
            temp.renameTo(file);

            if (srcBitmap != null && !srcBitmap.isRecycled()) {
                srcBitmap.recycle();
            }
            if (target != null && !target.isRecycled()) {
                target.recycle();
            }
            Log.i("aaa", "===cf===" + (System.currentTimeMillis() - s));
        } catch (FileNotFoundException e) {

        }

        return file;
    }

    private static Bitmap.Config getConfig(Bitmap bitmap) {
        Bitmap.Config config = bitmap.getConfig();
        if (config == null) {
            config = Bitmap.Config.ARGB_8888;
        }
        return config;
    }

    /**
     * 获取输出文件
     * @param context
     * @return
     */
    public static File getOutFile(Context context) {
        String dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        return new File(dir + "/" + System.currentTimeMillis() + ".jpg");
    }

    /**
     * 获取视频输出文件
     * @param context
     * @return
     */
    public static File getVideoOutFile(Context context) {
        String dir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES).getAbsolutePath();
        return new File(dir + "/" + System.currentTimeMillis() + ".mp4");
    }

    /**
     * dip 转px
     *
     * @param context
     * @param dip
     * @return
     */
    public static int dip2px(Context context, float dip) {
        return (int) (context.getResources().getDisplayMetrics().density * dip + 0.5f);
    }

    /**
     * px 转dip
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getOrientation(String path) {
        int orientation = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        } catch (IOException var4) {
            var4.printStackTrace();
        }

        return orientation;
    }
}
