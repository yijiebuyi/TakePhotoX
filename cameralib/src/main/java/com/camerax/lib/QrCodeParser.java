package com.camerax.lib;

import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.concurrent.Executors;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

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

    public void setQRCallback(QRCallback callback) {
        mQRCallback = callback;
    }

    /**
     * 识别二维码
     *
     * @param image
     * @return
     */
    public void start(final ImageProxy image, final long elapseTime) {
       start(image, elapseTime, 0, null, null);
    }

    /**
     * 识别二维码
     *
     * @param image
     * @return
     */
    public void start(final ImageProxy image, final long elapseTime, int scrOri, Rect scanRect, Size previewSize) {
        /*if(true) {
            String qrText = execute(image, elapseTime);
            checkNextFrame(qrText, image, elapseTime);
            return;
        }*/

        final Rect analysisRect = getImageAnalysisRect(scrOri, image, scanRect, previewSize);
        ThreadPool.getInstance().submit(new ThreadPool.Job<String>() {
            @Override
            public String run(ThreadPool.JobContext jc) {
                String qrText = execute(image, analysisRect, elapseTime);
                saveImg(image);
                return qrText;
            }
        }, new FutureListener<String>() {
            @Override
            public void onFutureDone(Future<String> future) {
                String qrText = future.get();
                checkNextFrame(qrText, image, elapseTime);
            }
        });
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

    private String execute(ImageProxy image, Rect analysisRect, long elapseTime) {
        if ((image.getFormat() == ImageFormat.YUV_420_888
                || image.getFormat() == ImageFormat.YUV_422_888
                || image.getFormat() == ImageFormat.YUV_444_888)
                && image.getPlanes().length == 3) {

            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);


            byte[] destData = new byte[image.getPlanes()[0].getBuffer().remaining()];
            int width = image.getWidth();
            int height = image.getHeight();
            /*for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    destData[x * height + height - y - 1] = data[x + y * width];
                }
            }
            int tmp = width;
            width = height;
            height = tmp;*/


            PlanarYUVLuminanceSource source = null;
            if (analysisRect == null) {
                source = new PlanarYUVLuminanceSource(data, width, height,
                        0, 0, image.getWidth(), image.getHeight(), false);
            } else {
                source = new PlanarYUVLuminanceSource(data, width, height,
                        analysisRect.left, analysisRect.top, analysisRect.width(), analysisRect.height(), false);
            }

            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
            String qrText = "";
            try {
                Result result = getDefaultMultiFormatReader().decode(binaryBitmap);
                if (result != null && (TextUtils.isEmpty(qrText) || !TextUtils.equals(qrText, result.getText()))) {
                    qrText = result.getText();
                    Log.e(TAG, qrText);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.i("aaa", "====qrText="+qrText);
            return qrText;
        }

        return "";
    }

    /**
     * 获取MultiFormatReader
     */
    public static MultiFormatReader getDefaultMultiFormatReader() {
        MultiFormatReader multiFormatReader = new MultiFormatReader();

        // 解码的参数
        Hashtable<DecodeHintType, Object> hints = new Hashtable<>(2);
        // 可以解析的编码类型
        Vector<BarcodeFormat> decodeFormats = new Vector<>();

        decodeFormats.add(BarcodeFormat.UPC_A);
        decodeFormats.add(BarcodeFormat.UPC_E);
        decodeFormats.add(BarcodeFormat.UPC_EAN_EXTENSION);
        decodeFormats.add(BarcodeFormat.CODABAR);
        decodeFormats.add(BarcodeFormat.RSS_14);
        decodeFormats.add(BarcodeFormat.RSS_EXPANDED);
        // 商品码
        decodeFormats.add(BarcodeFormat.EAN_13);
        decodeFormats.add(BarcodeFormat.EAN_8);
        decodeFormats.add(BarcodeFormat.CODE_39);
        decodeFormats.add(BarcodeFormat.CODE_93);
        // CODE128码是广泛应用在企业内部管理、生产流程、物流控制系统方面的条码码制
        decodeFormats.add(BarcodeFormat.CODE_128);
        // 主要用于运输包装，是印刷条件较差，不允许印刷EAN-13和UPC-A条码时应选用的一种条码。
        decodeFormats.add(BarcodeFormat.ITF);
        // 矩阵二维码
        decodeFormats.add(BarcodeFormat.QR_CODE);
        // 二维码（防伪、统筹等）
        decodeFormats.add(BarcodeFormat.DATA_MATRIX);

        decodeFormats.add(BarcodeFormat.AZTEC);
        decodeFormats.add(BarcodeFormat.MAXICODE);
        decodeFormats.add(BarcodeFormat.PDF_417);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        // 设置继续的字符编码格式为UTF8
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8");
        // 设置解析配置参数
        multiFormatReader.setHints(hints);

        return multiFormatReader;
    }

    private Rect getImageAnalysisRect(int scrOri, ImageProxy image, Rect scanRect, Size previewSize) {
        if (scanRect == null || previewSize == null) {
            return null;
        }

        //分别获取短边，求缩放比例
        int imgMinEdge = Math.min(image.getWidth(), image.getHeight());
        int previewMinEdge = Math.min(previewSize.getWidth(), previewSize.getHeight());


        int degree = image.getImageInfo().getRotationDegrees();
        if (degree % 180 != 0 && scrOri == ORIENTATION_PORTRAIT) {
            float scale = imgMinEdge / (float)previewMinEdge;
            int l = (int)(scanRect.top * scale);
            int t = (int)(scanRect.left * scale);

            int r = (int)(scanRect.bottom * scale);
            int b = (int)(scanRect.right * scale);
            return new Rect(l, t, r, b);
        } else {
            float scale = imgMinEdge / (float)previewMinEdge;
            int l = (int)(scanRect.left * scale);
            int t = (int)(scanRect.top * scale);

            int r = (int)(scanRect.right * scale);
            int b = (int)(scanRect.bottom * scale);
            return new Rect(l, t, r, b);
        }
    }

    private void saveImg(ImageProxy image) {
        String dir = Environment.getExternalStorageDirectory() + "/AAAA";
        if (!(new File(dir).exists())) {
            new File(dir).mkdirs();
        }
        File path = new File(dir + "/" + System.currentTimeMillis() + ".jpeg");
        CameraImageSaver saver = new CameraImageSaver(image, path, 0, false, null,
                new CameraImageSaver.OnImageSavedListener() {
                    @Override
                    public void onImageSaved(@NonNull Uri outputFileResults) {

                    }

                    @Override
                    public void onError(int saveError, String message, @Nullable Throwable cause) {
                    }
                });

        saver.run();
    }

    public static interface QRCallback {
        void onSucc(String result);

        void onFail();
    }
}
