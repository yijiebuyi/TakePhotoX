package com.camerax.lib;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import androidx.camera.core.ImageProxy;

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

import java.nio.ByteBuffer;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Copyright (C) 2017
 * 版权所有
 * <p>
 * 功能描述：二维码解析解
 * https://stackoverflow.com/questions/58113159/how-to-use-zxing-with-android-camerax-to-decode-barcode-and-qr-codes
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
    private final static int TIME_OUT = 3000 * 1000;

    public void setQRCallback(QRCallback callback) {
        mQRCallback = callback;
    }

    /**
     * 识别二维码
     * @param image
     * @return
     */
    public void start(final ImageProxy image, final long elapseTime) {
        /*if(true) {
            String qrText = execute(image, elapseTime);
            checkNextFrame(qrText, image, elapseTime);
            return;
        }*/

        ThreadPool.getInstance().submit(new ThreadPool.Job<String>() {
            @Override
            public String run(ThreadPool.JobContext jc) {
                String qrText = execute(image, elapseTime);
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

    private String execute(ImageProxy image, long elapseTime) {
        if ((image.getFormat() == ImageFormat.YUV_420_888
                || image.getFormat() == ImageFormat.YUV_422_888
                || image.getFormat() == ImageFormat.YUV_444_888)
                && image.getPlanes().length == 3) {

            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);

            PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data, image.getWidth(), image.getHeight(), 0, 0, image.getWidth(), image.getHeight(), false);
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

    public static interface QRCallback {
        void onSucc(String result);

        void onFail();
    }
}
