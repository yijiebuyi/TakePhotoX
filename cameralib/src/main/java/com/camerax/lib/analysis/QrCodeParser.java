package com.camerax.lib.analysis;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.ImageProxy;

import com.camerax.lib.core.CameraImageSaver;
import com.camerax.lib.util.Future;
import com.camerax.lib.util.FutureListener;
import com.camerax.lib.util.ThreadPool;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Hashtable;
import java.util.Vector;

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
     *
     * @param image
     */
    private void checkAnalysisRect(ImageProxy image) {
        if (mAnalysisRect != null) {
            return;
        }

        if (mScannerFrameOption == null || mScannerFrameOption.getFrameRatio() < 0) {
            return;
        }

        mAnalysisRect = new Rect();
        mAnalysisRect = AnalysisRect.build(image, mScannerFrameOption.getFrameRatio(), mAnalysisRect);

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
