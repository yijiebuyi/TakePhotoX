package com.camerax.lib.analysis;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.ImageProxy;
import androidx.fragment.app.Fragment;

import com.camerax.lib.R;
import com.camerax.lib.core.OnFocusListener;
import com.camerax.lib.core.OnImgAnalysisListener;

/**
 * Copyright (C) 2017
 * 版权所有
 * <p>
 * 功能描述：
 * <p>
 * 作者：yijiebuyi
 * 创建时间：2020/7/29
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */

public class QrCodeFragment extends Fragment implements QrCodeCallback{
    private QRCodeView mQRCodeView;

    private Context mContext;
    private QrCodeCallback mWrapQrCodeCallback;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mQRCodeView = new QRCodeView(mContext);
        mQRCodeView.setOnQrCodeCallback(this);
        mQRCodeView.setScannerFrameOption(new ScannerFrameOption.Builder()
                .frameMode(ScannerFrameOption.FrameMode.MODE_FRAME_SQUARE)
                .frameRatio(0.6f)
                .build());
        return mQRCodeView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mQRCodeView.startScan(this);
    }

    @Override
    public void onQrScanResult(boolean succ, String result) {
        if (mWrapQrCodeCallback != null) {
            mWrapQrCodeCallback.onQrScanResult(succ, result);
        }
    }

    public void setWrapQrCodeCallback(QrCodeCallback callback) {
        mWrapQrCodeCallback = callback;
    }
}
