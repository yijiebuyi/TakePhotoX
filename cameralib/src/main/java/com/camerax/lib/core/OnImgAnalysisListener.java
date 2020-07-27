package com.camerax.lib.core;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageProxy;

/**
 * Copyright (C) 2017
 * 版权所有
 * <p>
 * 功能描述：图片分析
 * <p>
 * 作者：yijiebuyi
 * 创建时间：2020/7/24
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */

public interface OnImgAnalysisListener {
    /**
     * 图片分析
     * @param image
     * @param elapseTime 流逝时间(耗时)
     */
    void onImageAnalysis(@NonNull ImageProxy image, long elapseTime);
}
