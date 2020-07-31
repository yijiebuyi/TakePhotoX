package com.camerax.lib.analysis;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.util.Log;

import androidx.camera.core.ImageProxy;

import com.camerax.lib.util.ThreadPool;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;

import java.nio.ByteBuffer;

/**
 * Copyright (C) 2017
 * 版权所有
 * <p>
 * 功能描述：
 * <p>
 * 作者：yijiebuyi
 * 创建时间：2020/7/31
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */

public class QRCodeTask implements ThreadPool.Job<String> {
    private final static String TAG = "QRCodeTask";

    private ImageProxy mImage;
    private Rect mAnalysisRect;

    private MultiFormatReader mMultiFormatReader;

    public QRCodeTask(ImageProxy image) {
        this(image, null);
    }

    public QRCodeTask(ImageProxy image, Rect analysisRect) {
        mImage = image;
        mAnalysisRect =analysisRect;
        mMultiFormatReader = MultiFormatReaderFactory.getQRCodeMultiFormat();
    }

    @Override
    public String run(ThreadPool.JobContext jc) {
        Result rawResult = null;

        if ((mImage.getFormat() == ImageFormat.YUV_420_888
                || mImage.getFormat() == ImageFormat.YUV_422_888
                || mImage.getFormat() == ImageFormat.YUV_444_888)
                && mImage.getPlanes().length == 3) {

            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            int width = mImage.getWidth();
            int height = mImage.getHeight();

            PlanarYUVLuminanceSource source = null;

            if (mAnalysisRect == null) {
                source = new PlanarYUVLuminanceSource(data, width, height,
                        0, 0, width, height, false);
            } else {
                source = new PlanarYUVLuminanceSource(data, width, height,
                        mAnalysisRect.left, mAnalysisRect.top, mAnalysisRect.width(), mAnalysisRect.height(), false);
            }

            String qrText = "";
            try {
                rawResult = mMultiFormatReader.decodeWithState(new BinaryBitmap(new GlobalHistogramBinarizer(source)));
                if (rawResult == null) {
                    rawResult = mMultiFormatReader.decodeWithState(new BinaryBitmap(new HybridBinarizer(source)));
                    if (rawResult != null) {
                        Log.d(TAG, "==GlobalHistogramBinarizer 没识别到，HybridBinarizer 能识别到");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (rawResult != null) {
                qrText = rawResult.getText();
            }

            Log.i("aaa", "====qrText=" + qrText);

            return qrText;
        }

        return "";
    }
}
