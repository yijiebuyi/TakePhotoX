package com.camerax.lib;

import android.net.Uri;

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

public interface OnCameraListener {

    /**
     * 拍照回调
     * @param uri
     */
    void onTaken(Uri uri);

    /**
     * 拍照取消
     */
    void onCancel();
}
