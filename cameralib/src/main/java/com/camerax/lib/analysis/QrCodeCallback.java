package com.camerax.lib.analysis;

/**
 * Copyright (C) 2017
 * 版权所有
 * <p>
 * 功能描述：
 * <p>
 * 作者：yijiebuyi
 * 创建时间：2020/9/11
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */

public interface QrCodeCallback {
    /**
     * @param succ 返回是否扫描成功
     * @param result 扫描后返回的信息
     */
    public void onQrScanResult(boolean succ, String result);
}
