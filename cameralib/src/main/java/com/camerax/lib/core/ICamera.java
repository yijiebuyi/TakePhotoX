package com.camerax.lib.core;

/**
 * Copyright (C) 2017
 * 版权所有
 * <p>
 * 功能描述：
 * <p>
 * 作者：yijiebuyi
 * 创建时间：2020/7/27
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */

interface ICamera {
    /**
     * 拍照
     */
    public void takePhoto();

    /**
     * 录像
     */
    public void takeVideo();

    /**
     * 对焦
     */
    public void focus(float x, float y, float rawX, float rawY);

    /**
     * 切换前置后置
     */
    public void switchFace();

    /**
     * 相机切换预览比例和拍照比例
     * @param ratio
     */
    public void switchAspect(@ExAspectRatio.ExRatio int ratio);

    /**
     * 相机缩放
     */
    public void scale(float scale);

    /**
     *  重置相机
     */
    public void reset();

    /**
     * 取消拍照
     */
    public void cancel();

    /**
     * 获取相机参数
     * @return
     */
    public CameraParam getCameraParam();
}
