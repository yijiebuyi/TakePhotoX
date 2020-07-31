package com.camerax.lib.analysis;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;

import java.util.Hashtable;
import java.util.Vector;

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

public class MultiFormatReaderFactory {

    /**
     * 获取二维码扫描格式
     * @return
     */
    public final static MultiFormatReader getQRCodeMultiFormat() {
        MultiFormatReader multiFormatReader = new MultiFormatReader();
        // 解码的参数
        Hashtable<DecodeHintType, Object> hints = new Hashtable<>(3);
        // 可以解析的编码类型
        Vector<BarcodeFormat> decodeFormats = new Vector<>();

        // 矩阵二维码
        decodeFormats.add(BarcodeFormat.QR_CODE);
        // 二维码（防伪、统筹等）
        decodeFormats.add(BarcodeFormat.DATA_MATRIX);
        decodeFormats.add(BarcodeFormat.AZTEC);
        decodeFormats.add(BarcodeFormat.MAXICODE);

        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        // 设置继续的字符编码格式为UTF8
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8");
        // 花更多的时间用于寻找图上的编码，优化准确性，但不优化速度
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        // 设置解析配置参数
        multiFormatReader.setHints(hints);

        return multiFormatReader;
    }

    public final static MultiFormatReader getDefaultMultiFormat() {
        MultiFormatReader multiFormatReader = new MultiFormatReader();
        // 解码的参数
        Hashtable<DecodeHintType, Object> hints = new Hashtable<>(3);
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
        // 花更多的时间用于寻找图上的编码，优化准确性，但不优化速度
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        // 设置解析配置参数
        multiFormatReader.setHints(hints);

        return multiFormatReader;
    }
}
