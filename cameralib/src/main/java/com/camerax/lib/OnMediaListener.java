package com.camerax.lib;

import android.net.Uri;

/**
 * Copyright (C) 2017
 * 版权所有
 * <p>
 * 功能描述：
 * <p>
 * 作者：yijiebuyi
 * 创建时间：2020/7/24
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */

public interface OnMediaListener {
    /**
     * 媒体（图片，视频）加载完成回调
     * @param succ
     */
    void onMediaLoad(boolean succ);

    /**
     * 媒体（图片，视频）被选择
     */
    void onPhotoSelect(Uri uri);

    /**
     * 取消
     */
    void onCancel();
}
