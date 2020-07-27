package com.camerax.lib;

import android.animation.Animator;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.ImageProxy;
import androidx.fragment.app.Fragment;

import com.camerax.lib.core.CameraOption;
import com.camerax.lib.core.CameraView;
import com.camerax.lib.core.ExAspectRatio;
import com.camerax.lib.core.IFlashLight;
import com.camerax.lib.core.OnCameraFaceListener;
import com.camerax.lib.core.OnCameraListener;
import com.camerax.lib.core.OnFocusListener;
import com.camerax.lib.core.OnImgAnalysisListener;

/**
 * Copyright (C) 2017
 * 版权所有
 * <p>
 * 功能描述：相机预览Fragment，支持拍照，扫描图片分析
 * <p>
 * 作者：yijiebuyi
 * 创建时间：2020-07-21
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */

public class CameraFragment extends Fragment implements View.OnClickListener, OnFocusListener,
        OnCameraListener, OnImgAnalysisListener, OnCameraFaceListener {
    private final static String TAG = "CameraFragment";
    /**
     * 是否显示底部控制器
     */
    public final static String KEY_SHOW_BOTTOM_CONTROLLER = "key_show_bottom_controller";
    public final static String KEY_CAMERA_OPTION = "key_camera_option";

    private CameraView mCameraView;

    /**
     * view handle
     */
    private View mBottomView;
    private ImageView mTakePhotoBtn;
    private ImageView mCancelBtn;
    private ImageView mSwitchCameraBtn;
    private FocusImageView mFocusImageView;

    private ImageView mCameraLightBtn;
    private View mCameraLightLayout;

    private TextView mCloseLightTv;
    private TextView mOpenLightTv;
    private TextView mAutoLightTv;
    private TextView mFillLightTv;

    /**
     * 是否隐藏底部控制器
     */
    private boolean mHideBottomController;

    private OnCameraListener mOnCameraWrapListener;
    private OnImgAnalysisListener mOnImgAnalysisWrapListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_cemara, null);
        mCameraView = view.findViewById(R.id.preview_view);
        mCameraView.setOnFocusListener(this);
        mCameraView.setOnCameraListener(this);
        mCameraView.setOnImgAnalysisListener(this);
        mCameraView.setOnCameraFaceListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CameraOption option = initOption();
        mCameraView.initCamera(option, this);

        initView(view);
    }

    private void initView(View view) {
        mBottomView = view.findViewById(R.id.bottom_container);

        if (mHideBottomController) {
            mBottomView.setVisibility(View.GONE);
        } else {
            mBottomView.setVisibility(View.VISIBLE);
            mTakePhotoBtn = view.findViewById(R.id.take_photo);
            mCancelBtn = view.findViewById(R.id.cancel);
            mSwitchCameraBtn = view.findViewById(R.id.switch_camera);
            mFocusImageView = view.findViewById(R.id.focus_view);

            mTakePhotoBtn.setOnClickListener(this);
            mCancelBtn.setOnClickListener(this);
            mSwitchCameraBtn.setOnClickListener(this);
        }

        //camera flash light
        mCameraLightBtn = view.findViewById(R.id.camera_light_btn);
        mCameraLightLayout = view.findViewById(R.id.camera_light);

        mCameraLightBtn.setOnClickListener(this);
        view.findViewById(R.id.light_state).setOnClickListener(this);

        mAutoLightTv = view.findViewById(R.id.auto_light);
        mOpenLightTv = view.findViewById(R.id.open_light);
        mCloseLightTv = view.findViewById(R.id.close_light);
        mFillLightTv = view.findViewById(R.id.fill_light);

        mAutoLightTv.setOnClickListener(this);
        mOpenLightTv.setOnClickListener(this);
        mCloseLightTv.setOnClickListener(this);
        mFillLightTv.setOnClickListener(this);

        mFillLightTv.setVisibility(mCameraView.getCameraParam().faceFront ? View.GONE : View.VISIBLE);
    }

    private CameraOption initOption() {
        Bundle data = getArguments();

        boolean hideBottomCtrl = data != null && !data.getBoolean(KEY_SHOW_BOTTOM_CONTROLLER);
        CameraOption option = null;
        Object obj = null;
        if (data != null && (obj = data.getSerializable(KEY_CAMERA_OPTION)) != null) {
            option = (CameraOption) obj;
        } else {
            option = new CameraOption.Builder(ExAspectRatio.RATIO_16_9)
                    .build();
        }

        mHideBottomController = option.isAnalysisImg() || hideBottomCtrl;

        return option;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void setOnCameraListener(OnCameraListener listener) {
        mOnCameraWrapListener = listener;
    }

    public void setOnImgAnalysisListener(OnImgAnalysisListener listener) {
        mOnImgAnalysisWrapListener = listener;
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.take_photo) {
            mCameraView.take();
        } else if (vid == R.id.switch_camera) {
            mCameraView.switchFace();
        } else if (vid == R.id.cancel) {
            mCameraView.cancel();
        } else if (vid == R.id.camera_light_btn) {
            showLightLayout();
        } else if (vid == R.id.light_state) {
            hideLightLayout();
        } else if (vid == R.id.close_light) {
            mCameraView.closeFlashLight();
            hideLightLayout();
        } else if (vid == R.id.auto_light) {
            mCameraView.autoFlashLight();
            hideLightLayout();
        } else if (vid == R.id.open_light) {
            mCameraView.openFlashLight();
            hideLightLayout();
        } else if (vid == R.id.fill_light) {
            mCameraView.fillLight();
            hideLightLayout();
        }
    }

    private void showLightLayout() {
        mCameraLightLayout.animate().alpha(1).setDuration(200).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mCameraLightBtn.setAlpha(0.f);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mCameraLightLayout.setVisibility(View.VISIBLE);
                mCameraLightBtn.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();

        mAutoLightTv.setSelected(false);
        mOpenLightTv.setSelected(false);
        mCloseLightTv.setSelected(false);
        mFillLightTv.setSelected(false);
        switch (mCameraView.getCameraParam().lightState) {
            case IFlashLight.CLOSE:
                mCloseLightTv.setSelected(true);
                break;
            case IFlashLight.OPEN:
                mOpenLightTv.setSelected(true);
                break;
            case IFlashLight.AUTO:
                mAutoLightTv.setSelected(true);
                break;
            case IFlashLight.FILL:
                mFillLightTv.setSelected(true);
                break;
        }
    }

    private void hideLightLayout() {
        mCameraLightLayout.animate().alpha(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mCameraLightLayout.setVisibility(View.GONE);
                mCameraLightBtn.setAlpha(1.f);
                mCameraLightBtn.setVisibility(View.VISIBLE);


                switch (mCameraView.getCameraParam().lightState) {
                    case IFlashLight.CLOSE:
                        mCameraLightBtn.setImageResource(R.drawable.ic_camera_close_light);
                        break;
                    case IFlashLight.OPEN:
                        mCameraLightBtn.setImageResource(R.drawable.ic_camera_open_light);
                        break;
                    case IFlashLight.AUTO:
                        mCameraLightBtn.setImageResource(R.drawable.ic_camera_auto_light);
                        break;
                    case IFlashLight.FILL:
                        break;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).setDuration(200).start();
    }

    /**
     * 隐藏底部布局
     */
    public void hideBottomView(boolean anim) {
        if (mBottomView != null) {
            if (anim) {
                mBottomView.animate().alpha(0).setDuration(100).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mBottomView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            } else {
                mBottomView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onStartFocus(float x, float y) {
        mFocusImageView.startFocus(x, y);
    }

    @Override
    public void onEndFocus(boolean succ) {
        if (succ) {
            mFocusImageView.onFocusFailed();
        } else {
            mFocusImageView.onFocusFailed();
        }
    }

    @Override
    public void onTaken(Uri uri) {
        if (mOnCameraWrapListener != null) {
            mOnCameraWrapListener.onTaken(uri);
        }
    }

    @Override
    public void onCancel() {
        if (mOnCameraWrapListener != null) {
            mOnCameraWrapListener.onCancel();
        }
    }

    @Override
    public void onImageAnalysis(@NonNull ImageProxy image, long elapseTime) {
        if (mOnImgAnalysisWrapListener != null) {
            mOnImgAnalysisWrapListener.onImageAnalysis(image, elapseTime);
        }
    }

    @Override
    public void onSwitchCamera(boolean front) {
        mFillLightTv.setVisibility(front ? View.GONE : View.VISIBLE);
    }
}
