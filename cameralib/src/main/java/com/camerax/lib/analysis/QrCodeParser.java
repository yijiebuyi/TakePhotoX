package com.camerax.lib.analysis;

import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Size;

import androidx.camera.core.ImageProxy;

import com.camerax.lib.util.Future;
import com.camerax.lib.util.FutureListener;
import com.camerax.lib.util.ThreadPool;

/**
 * Copyright (C) 2017
 * 版权所有
 * <p>
 * 功能描述：二维码解析解
 * <p>
 * 作者：yijiebuyi
 * 创建时间：2020/7/24
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */

public class QrCodeParser {
    private final static String TAG = "QrScanParser";
    private QRCallback mQRCallback;
    private final static int TIME_OUT = 120 * 1000;

    private ScannerFrameOption mScannerFrameOption;
    private Size mPreviewSize;

    private Rect mAnalysisRect;

    public QrCodeParser() {

    }

    public QrCodeParser(Size previewSize, ScannerFrameOption option) {
        mScannerFrameOption = option;
        mPreviewSize = previewSize;
    }

    public void setQRCallback(QRCallback callback) {
        mQRCallback = callback;
    }

    /**
     * 识别二维码
     *
     * @param image
     * @return
     */
    public void execute(final ImageProxy image, final long elapseTime) {
        checkAnalysisRect(image);

        ThreadPool.getInstance().submit(new QRCodeTask(image, mAnalysisRect),
                new FutureListener<String>() {
                    @Override
                    public void onFutureDone(Future<String> future) {
                        String qrText = future.get();
                        checkNextFrame(qrText, image, elapseTime);
                    }
                });
    }

    /**
     * 计算扫描框区域，相对于image计算
     * 当扫描框维全屏是，默认识别全图
     *
     * @param image
     */
    private void checkAnalysisRect(ImageProxy image) {
        if (mScannerFrameOption == null ||
                mScannerFrameOption.getFrameMode() == ScannerFrameOption.FrameMode.MODE_FRAME_NO) {
            mAnalysisRect = null;
            return;
        } else {
            if (mAnalysisRect != null) {
                return;
            }

            if (mScannerFrameOption == null || mScannerFrameOption.getFrameRatio() < 0) {
                return;
            }

            mAnalysisRect = new Rect();
            mAnalysisRect = AnalysisRect.build(image, mScannerFrameOption.getFrameRatio(), mAnalysisRect);
        }
    }

    private void checkNextFrame(String qrText, ImageProxy image, long elapseTime) {
        if (TextUtils.isEmpty(qrText)) {
            if (mQRCallback != null && elapseTime > TIME_OUT) {
                mQRCallback.onFail();
            } else {
                //继续扫描下一张图片
                image.close();
            }
        } else {
            if (mQRCallback != null) {
                mQRCallback.onSucc(qrText);
            }
        }
    }

    public static interface QRCallback {
        void onSucc(String result);

        void onFail();
    }
}
