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
import com.camerax.lib.core.CameraOption;
import com.camerax.lib.core.CameraView;
import com.camerax.lib.core.ExAspectRatio;
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

public class QrCodeFragment extends Fragment implements OnFocusListener, OnImgAnalysisListener {
    private QRCodeView mQRCodeView;
    private QrCodeParser mQrCodeParser;

    private Context mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mQRCodeView = new QRCodeView(mContext);
        mQRCodeView.setOnFocusListener(this);
        mQRCodeView.setOnImgAnalysisListener(this);
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
    public void onStartFocus(float x, float y, float rawX, float rawY) {

    }

    @Override
    public void onEndFocus(boolean succ) {

    }

    @Override
    public void onImageAnalysis(@NonNull ImageProxy image, long elapseTime) {
        if (mQrCodeParser == null) {
            mQrCodeParser = new QrCodeParser(mQRCodeView.getPreviewSize(), mQRCodeView.getOptions());
            mQrCodeParser.setQRCallback(new QrCodeParser.QRCallback() {
                @Override
                public void onSucc(final String result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onFail() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, R.string.qr_code_fail, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }

       mQrCodeParser.execute(image, elapseTime);
    }

}
