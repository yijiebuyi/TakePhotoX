package com.camerax.lib.core;

/**
 * Copyright (C) 2017
 * 版权所有
 * <p>
 * 功能描述：预览比例位置变化监听器
 * <p>
 * 作者：yijiebuyi
 * 创建时间：2020/7/30
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */

interface OnPreviewLayoutListener {
    /**
     * 位置比例发生变化时
     * @param width  view的宽
     * @param height view的高
     * @param leftMargin view距离父布局的左边距
     * @param topMargin view距离父布局的右边距
     */
    public void onLayoutSizeChange(int width, int height, int leftMargin, int topMargin);
}
