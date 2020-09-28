package com.camerax.lib;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.VideoCapture;
import androidx.camera.view.CameraView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.camerax.lib.core.CameraOption;
import com.camerax.lib.core.ExAspectRatio;
import com.camerax.lib.core.OnCameraListener;

import java.io.File;
import java.util.concurrent.Executor;

/**
 * Copyright (C) 2017
 * 版权所有
 * <p>
 * 功能描述：
 * <p>
 * 作者：yijiebuyi
 * 创建时间：2020/8/21
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */

public class VideoFragment extends Fragment implements View.OnClickListener {
    private final int MAX_VIDEO_DURATION = 0; // <=0代表无限制

    private CameraView mVideoView;
    private CameraOption mCameraOption;

    private ImageView mSwitchCameraBtn;
    private ImageView mCancelBtn;
    private ImageView mTakeVideoBtn;

    private TextView mCounterTv;
    private int mStartTime;
    private StringBuilder mStringBuilder;

    private Executor mExecutor;

    private OnCameraListener mOnCameraWrapListener;
    private int mMaxVideoDuration = MAX_VIDEO_DURATION;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_videox, null);

        mVideoView = view.findViewById(R.id.preview_view);
        mVideoView.enableTorch(true);
        mVideoView.setCaptureMode(CameraView.CaptureMode.VIDEO);

        mTakeVideoBtn = view.findViewById(R.id.take_video);
        mSwitchCameraBtn = view.findViewById(R.id.switch_camera);
        mCancelBtn = view.findViewById(R.id.cancel);
        mCounterTv = view.findViewById(R.id.counter_view);
        return view;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mExecutor = ContextCompat.getMainExecutor(getContext());
        mCameraOption = initOption();
        mStringBuilder = new StringBuilder();

        mVideoView.bindToLifecycle(this);
        mVideoView.setCameraLensFacing(mCameraOption.isFaceFront() ? CameraSelector.LENS_FACING_FRONT
                : CameraSelector.LENS_FACING_BACK);
        mTakeVideoBtn.setOnClickListener(this);
        mCancelBtn.setOnClickListener(this);
        mSwitchCameraBtn.setOnClickListener(this);
    }

    public void setOnCameraListener(OnCameraListener listener) {
        mOnCameraWrapListener = listener;
    }

    private CameraOption initOption() {
        Bundle data = getArguments();
        mMaxVideoDuration = data != null ? data.getInt(CameraConstant.KEY_MAX_VIDEO_DURATION, MAX_VIDEO_DURATION) : MAX_VIDEO_DURATION;
        CameraOption option = null;
        Object obj = null;
        if (data != null && (obj = data.getSerializable(CameraConstant.KEY_CAMERA_OPTION)) != null) {
            option = (CameraOption) obj;
        } else {
            option = new CameraOption.Builder(ExAspectRatio.RATIO_16_9)
                    .build();
        }

        return option;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.take_video) {
            if (mVideoView.isRecording()) {
                mVideoView.stopRecording();

                mCounterTv.setVisibility(View.GONE);
                mCancelBtn.setVisibility(View.VISIBLE);
                mSwitchCameraBtn.setVisibility(View.VISIBLE);
            } else {
                String outPath = mCameraOption != null ? mCameraOption.getOutPath() : null;
                final File file = !TextUtils.isEmpty(outPath) ? new File(outPath)
                        : CameraUtil.getVideoOutFile(getContext());

                mCancelBtn.setVisibility(View.GONE);
                mSwitchCameraBtn.setVisibility(View.GONE);
                startTimeCounter();

                mVideoView.startRecording(file, mExecutor,
                        new VideoCapture.OnVideoSavedCallback() {

                            @Override
                            public void onVideoSaved(@NonNull File file) {
                                if (mOnCameraWrapListener != null) {
                                    mOnCameraWrapListener.onTaken(Uri.fromFile(file));
                                }
                            }

                            @Override
                            public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
                                //Toast.makeText()
                                if (mOnCameraWrapListener != null) {
                                    mOnCameraWrapListener.onTaken(null);
                                }
                            }
                        });
            }
        } else if (R.id.switch_camera == id) {
            mVideoView.setFlash(ImageCapture.FLASH_MODE_OFF);
            mVideoView.toggleCamera();
        } else if (R.id.cancel == id) {
            if (mOnCameraWrapListener != null) {
                mOnCameraWrapListener.onCancel();
            }
        }
    }

    private void startTimeCounter() {
        mCounterTv.setVisibility(View.VISIBLE);
        mTakeVideoBtn.setImageResource(R.drawable.ic_recording);
        mStartTime = 0;

        mCounterTv.postDelayed(mTimeRunnable, 1000);
    }

    private void stopTimeCounter() {
        mCounterTv.setVisibility(View.GONE);
        mTakeVideoBtn.setImageResource(R.drawable.ic_record);
    }

    private Runnable mTimeRunnable = new Runnable() {
        @Override
        public void run() {
            mCounterTv.setText(formatDate(++mStartTime));
            if (mVideoView.isRecording()) {
                if (mStartTime > mMaxVideoDuration && mMaxVideoDuration > 0) {
                    mTakeVideoBtn.performClick();
                } else {
                    mCounterTv.postDelayed(mTimeRunnable, 1000);
                }
            }
        }
    };

    /**
     * 格式化时间
     *
     * @return
     */
    public String formatDate(int timeCount) {
        mStringBuilder.delete(0, mStringBuilder.length());
        int minute = timeCount / 60;
        int second = timeCount % 60;

        if (minute < 10) {
            mStringBuilder.append("0");
        }
        mStringBuilder.append(minute);

        mStringBuilder.append(":");

        if (second < 10) {
            mStringBuilder.append(0);
        }
        mStringBuilder.append(second);

        return mStringBuilder.toString();
    }

    @Override
    public void onPause() {
        super.onPause();
        mVideoView.stopRecording();
    }
}
