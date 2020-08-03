package com.camerax.lib.analysis;

import android.graphics.Rect;

import androidx.camera.core.ImageProxy;

/**
 * Copyright (C) 2017
 * 版权所有
 * <p>
 * 功能描述：图片待分析区域
 * <p>Ø
 * 作者：yijiebuyi
 * 创建时间：2020/7/31
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */

public class AnalysisRect {
    public static Rect build(ImageProxy image, float ratio, Rect src) {
        if (image == null || ratio < 0) {
            return null;
        }

        int imgW = image.getWidth();
        int imgH = image.getHeight();

        int l = (int)(imgW - imgW * ratio) / 2;
        int t = (int)(imgH - imgH * ratio) / 2;

        int r = l + (int)(imgW * ratio);
        int b = t + (int)(imgH * ratio);

        if (src == null) {
            src.set(l, t, r, b);
            return src;
        }

        return new Rect(l, t, r, b);
    }
}
