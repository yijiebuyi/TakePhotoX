package com.camerax.lib.core;

/**
 * Copyright (C) 2017
 * 版权所有
 * <p>
 * 功能描述：闪光功能
 * <p>
 * 作者：yijiebuyi
 * 创建时间：2020/7/27
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */

public interface IFlashLight {

    public final static int CLOSE = 0;
    public final static int OPEN = 1;
    public final static int AUTO = 2;
    public final static int FILL = 3;

    /**
     * 关闭闪光
     */
    public void closeFlashLight();

    /**
     * 打卡闪光
     */
    public void openFlashLight();

    /**
     * 自动闪光
     */
    public void autoFlashLight();

    /**
     * 补光，（打开灯光模式）
     */
    public void fillLight();
}
