package com.camerax.lib.core;

/**
 * Copyright (C) 2017
 * 版权所有
 * <p>
 * 功能描述：相机对焦
 * <p>
 * 作者：yijiebuyi
 * 创建时间：2020/7/27
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */

public interface OnFocusListener {
    public void onStartFocus(float x, float y, float rawX, float rawY);

    public void onEndFocus(boolean succ);
}
