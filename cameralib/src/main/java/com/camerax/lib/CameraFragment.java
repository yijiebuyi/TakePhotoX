package com.camerax.lib;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.camera2.Camera2Config;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraXConfig;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.FocusMeteringResult;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.MeteringPointFactory;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

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

public class CameraFragment extends Fragment implements CameraXConfig.Provider,
        CameraPreview.CameraGestureListener, View.OnClickListener {
    private final static String TAG = "CameraFragment";
    /**
     * 是否显示底部控制器
     */
    public final static String KEY_SHOW_BOTTOM_CONTROLLER = "key_show_bottom_controller";
    /**
     * 相机前置还是后置
     */
    public final static String KEY_CAMERA_FACE = "key_camera_face";
    /**
     * 是否需要图片分析
     */
    public final static String KEY_IMG_ANALYSIS = "key_img_analysis";

    /**
     * 图片拍照后保存的路径
     */
    public final static String KEY_OUT_PATH = "key_out_path";

    private int SCREEN_WIDTH = 0;
    private int SCREEN_HEIGHT = 0;
    private Context mContext;
    private Activity mActivity;

    private CameraPreview mPreviewView;

    /**
     * view handle
     */
    private View mBottomView;
    private ImageView mTakePhotoBtn;
    private ImageView mCancelBtn;
    private ImageView mSwitchCameraBtn;
    private FocusImageView mFocusImageView;

    private ListenableFuture<ProcessCameraProvider> mCameraProviderFuture;
    private CameraSelector mCameraSelector;
    private Camera mCamera;

    private ImageCapture mImageCapture;
    private ImageAnalysis mImageAnalysis;
    private CameraInfo mCameraInfo;
    private CameraControl mCameraControl;
    private Preview mPreview;
    private int mAspectRatioInt = ExAspectRatio.RATIO_16_9;
    private int mCameraSelectorInt = CameraSelector.LENS_FACING_FRONT;

    private Executor mExecutor;

    private OnCameraListener mOnCameraListener;
    private OnImgAnalysisListener mOnImgAnalysisListener;

    /**
     * 是否隐藏底部控制器
     */
    private boolean mHideBottomController;

    /**
     * 是否需要图片分析
     */
    private boolean mImgAnalysis;
    /**
     * 分析开始时间
     */
    private long mAnalysisStartTime;
    /**
     * 拍照保存的图片路径
     */
    private String mOutFilePath;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        mActivity = getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        SCREEN_WIDTH = dm.widthPixels;
        SCREEN_HEIGHT = dm.heightPixels;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_cemara, null);
        mPreviewView = view.findViewById(R.id.preview_view);
        mPreviewView.setCameraGestureListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBottomView = view.findViewById(R.id.bottom_container);

        initData();

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

        initCamera();
    }

    private void initData() {
        Bundle data = getArguments();
        mCameraSelectorInt = data != null ?
                data.getInt(KEY_CAMERA_FACE, CameraSelector.LENS_FACING_BACK) : CameraSelector.LENS_FACING_BACK;
        mImgAnalysis = data != null && data.getBoolean(KEY_IMG_ANALYSIS);
        if (mImgAnalysis) {
            mCameraSelectorInt = CameraSelector.LENS_FACING_BACK;
        }

        mOutFilePath = data == null ? null : data.getString(KEY_OUT_PATH);
        mHideBottomController = mImgAnalysis || (data != null && !data.getBoolean(KEY_SHOW_BOTTOM_CONTROLLER));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void setOnCameraListener(OnCameraListener listener) {
        mOnCameraListener = listener;
    }

    public void setOnImgAnalysisListener(OnImgAnalysisListener listener) {
        mOnImgAnalysisListener = listener;
    }

    /**
     * 初始化相机
     */
    private void initCamera() {
        mExecutor = ContextCompat.getMainExecutor(getContext());
        mCameraProviderFuture = ProcessCameraProvider.getInstance(getContext());

        initUseCases();

        mCameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = mCameraProviderFuture.get();
                    cameraProvider.unbindAll();
                    bindPreview(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                }
            }
        }, mExecutor);
    }

    /**
     * 初始化配置信息
     */
    private void initUseCases() {
        initImageAnalysis();
        initImageCapture();
        initPreview();
        initCameraSelector();
    }

    /**
     * 图像分析
     */
    private void initImageAnalysis() {
        mImageAnalysis = new ImageAnalysis.Builder()
                // 分辨率
                .setTargetResolution(CameraUtil.computeSize(mAspectRatioInt, SCREEN_WIDTH))
                // 非阻塞模式
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        if (mImgAnalysis) {
            startImgAnalysis();
        }
    }

    /**
     * 构建图像捕获用例
     */
    private void initImageCapture() {
        // 构建图像捕获用例
        mImageCapture = new ImageCapture.Builder()
                .setFlashMode(ImageCapture.FLASH_MODE_AUTO)
                .setTargetAspectRatio(mAspectRatioInt)
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build();

        // 旋转监听
        OrientationEventListener orientationEventListener = new OrientationEventListener(mContext) {
            @Override
            public void onOrientationChanged(int orientation) {
                int rotation;
                // Monitors orientation values to determine the target rotation value
                if (orientation >= 45 && orientation < 135) {
                    rotation = Surface.ROTATION_270;
                } else if (orientation >= 135 && orientation < 225) {
                    rotation = Surface.ROTATION_180;
                } else if (orientation >= 225 && orientation < 315) {
                    rotation = Surface.ROTATION_90;
                } else {
                    rotation = Surface.ROTATION_0;
                }

                mImageCapture.setTargetRotation(rotation);
            }
        };

        orientationEventListener.enable();
    }

    /**
     * 构建图像预览
     */
    private void initPreview() {
        mPreview = new Preview.Builder()
                .setTargetAspectRatio(mAspectRatioInt)
                .build();
    }

    /**
     * 选择摄像头
     */
    private void initCameraSelector() {
        mCameraSelector = new CameraSelector.Builder()
                .requireLensFacing(mCameraSelectorInt)
                .build();
    }

    /**
     * 分析图片
     */
    public void startImgAnalysis() {
        mAnalysisStartTime = System.currentTimeMillis();
        mImageAnalysis.setAnalyzer(mExecutor, new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy image) {
                if (mOnImgAnalysisListener != null) {
                    mOnImgAnalysisListener.onImageAnalysis(image, System.currentTimeMillis() - mAnalysisStartTime);
                }
            }
        });
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        mPreview.setSurfaceProvider(mPreviewView.createSurfaceProvider());
        mCamera = cameraProvider.bindToLifecycle(this, mCameraSelector,
                mImageCapture, mImageAnalysis, mPreview);

        mCameraInfo = mCamera.getCameraInfo();
        mCameraControl = mCamera.getCameraControl();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @NonNull
    @Override
    public CameraXConfig getCameraXConfig() {
        return Camera2Config.defaultConfig();
    }

    /**
     * 开始对焦
     * @param x
     * @param y
     */
    private void startFocus(float x, float y) {
        mFocusImageView.startFocus(x, y);
        MeteringPointFactory factory = mPreviewView.createMeteringPointFactory(mCameraSelector);
        //MeteringPointFactory factory = new SurfaceOrientedMeteringPointFactory(1.0f, 1.0f);
        MeteringPoint point = factory.createPoint(x, y);
        FocusMeteringAction action = new FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
                // auto calling cancelFocusAndMetering in 3 seconds
                .setAutoCancelDuration(3, TimeUnit.SECONDS)
                .build();
        final ListenableFuture future = mCameraControl.startFocusAndMetering(action);
        future.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    FocusMeteringResult result = (FocusMeteringResult) future.get();
                    if (result.isFocusSuccessful()) {
                        mFocusImageView.onFocusSuccess();
                    } else {
                        mFocusImageView.onFocusFailed();
                    }
                } catch (Exception e) {
                    mFocusImageView.onFocusFailed();
                }
            }
        }, mExecutor);
    }

    @Override
    public void onClick(float x, float y) {
        startFocus(x, y);
    }

    @Override
    public void onZoom(float scale) {
        mCameraControl.setZoomRatio(scale);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.take_photo) {
            takePhoto();
        } else if (v.getId() == R.id.switch_camera) {
            switchCamera();
        } else if (v.getId() == R.id.cancel) {
            if (mOnCameraListener != null) {
                mOnCameraListener.onCancel();
            }
        }
    }

    /**
     * 切换摄像头
     */
    public void switchCamera() {
        if (mCameraSelectorInt == CameraSelector.LENS_FACING_BACK) {
            mCameraSelectorInt = CameraSelector.LENS_FACING_FRONT;
        } else {
            mCameraSelectorInt = CameraSelector.LENS_FACING_BACK;
        }
        initCamera();
    }

    /**
     * 保存图片
     */
    public void takePhoto() {
        final File file = !TextUtils.isEmpty(mOutFilePath) ? new File(mOutFilePath) : CameraUtil.getOutFile(mContext);
        ImageCapture.Metadata metadata = new ImageCapture.Metadata();
        metadata.setReversedHorizontal(mCameraSelectorInt == CameraSelector.LENS_FACING_FRONT);
        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(file).setMetadata(metadata).build();
        mImageCapture.takePicture(outputFileOptions, mExecutor,
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Uri result = outputFileResults.getSavedUri();
                        if (result == null) {
                            result = Uri.fromFile(file);
                        }

                        if (mOnCameraListener != null) {
                            mOnCameraListener.onTaken(result);
                        }
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e(TAG, exception.getMessage());
                        //Toast.makeText(mContext, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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
}
