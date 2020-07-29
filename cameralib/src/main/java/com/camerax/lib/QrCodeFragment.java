package com.camerax.lib;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.ImageProxy;
import androidx.fragment.app.Fragment;

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
    private CameraView mCameraView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_qrcode_scan, null);

        mCameraView = view.findViewById(R.id.camera_preview);
        mCameraView.setOnFocusListener(this);
        mCameraView.setOnImgAnalysisListener(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CameraOption option = new CameraOption.Builder(ExAspectRatio.RATIO_16_9)
                .analysisImg(true)
                .build();

        mCameraView.initCamera(option, this);
    }


    @Override
    public void onStartFocus(float x, float y, float rawX, float rawY) {

    }

    @Override
    public void onEndFocus(boolean succ) {

    }

    @Override
    public void onImageAnalysis(@NonNull ImageProxy image, long elapseTime) {

    }
}
