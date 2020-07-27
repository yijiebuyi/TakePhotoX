package com.camerax.lib.core;

/**
 * Copyright (C) 2017
 * 版权所有
 * <p>
 * 功能描述：相机参数
 * <p>
 * 作者：yijiebuyi
 * 创建时间：2020/7/27
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */

public class CameraParam {
    /**
     * 预览尺寸 宽、高
     */
    public int previewWidth;
    /**
     * 预览尺寸 高
     */
    public int previewHeight;

    /**
     * 输出尺寸 宽、高
     */
    public int outWidth;
    /**
     * 输出尺寸 高
     */
    public int outHeight;

    /**
     * 缩放比例
     */
    public float scale;

    /**
     * 是否是前置（前置、后置）
     */
    public boolean faceFront;

    /**
     * 对焦点
     */
    public int focusPoint;

    /**
     * 预览比例
     */
    public int asRatio;

    /**
     * 闪光灯状态
     */
    public int lightState;
}
