package com.camerax.lib;

import android.content.Context;
import android.os.Environment;
import android.util.Size;

import java.io.File;

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

    /**
     * 获取输出文件
     * @param context
     * @return
     */
    public static File getOutFile(Context context) {
        String dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        return new File(dir + "/" + System.currentTimeMillis() + ".jpg");
    }
}
