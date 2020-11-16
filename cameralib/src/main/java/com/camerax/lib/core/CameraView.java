package com.camerax.lib.core;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.ViewGroup;
import android.widget.Toast;

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
import androidx.camera.core.VideoCapture;
import androidx.camera.core.impl.VideoCaptureConfig;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.camerax.lib.CameraUtil;
import com.camerax.lib.R;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

/**
 * Copyright (C) 2017
 * 版权所有
 * <p>
 * 功能描述：
 * <p>
 * 作者：yijiebuyi
 * 创建时间：2020/7/27
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */

public class CameraView extends CameraPreview implements ICamera, IFlashLight,
        CameraPreview.CameraGestureListener, CameraXConfig.Provider {
    private final static String TAG = "CameraView";

    /**
     * 当前相机预览参数
     */
    private CameraParam mCameraParam;

    private ListenableFuture<ProcessCameraProvider> mCameraProviderFuture;
    private CameraSelector mCameraSelector;
    private Camera mCamera;

    private ImageCapture mImageCapture;

    private VideoCapture mVideoCapture;
    private VideoCaptureConfig mVideoCaptureConfig;

    private ImageAnalysis mImageAnalysis;
    private CameraInfo mCameraInfo;
    private CameraControl mCameraControl;
    private Preview mPreview;

    private LifecycleOwner mLifecycleOwner;
    private Executor mExecutor;
    private ProcessCameraProvider mCameraProvider;

    /**
     * 是否需要图片分析
     */
    private boolean mIsImgAnalysis;
    /**
     * 分析开始时间
     */
    private long mAnalysisStartTime;
    /**
     * 拍照保存的图片路径
     */
    private String mOutFilePath;

    private int SCREEN_WIDTH = 0;
    private int SCREEN_HEIGHT = 0;
    private Executor mIoExecutor;

    private Context mContext;

    public CameraView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public CameraView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CameraView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public CameraView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    /**
     * 初始化
     *
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        mCameraParam = new CameraParam();
        mCameraParam.asRatio = ExAspectRatio.RATIO_16_9;
        mCameraParam.faceFront = false;
        mCameraParam.scale = 1.0f;

        DisplayMetrics dm = getResources().getDisplayMetrics();
        SCREEN_WIDTH = dm.widthPixels;
        SCREEN_HEIGHT = dm.heightPixels;

        setCameraGestureListener(this);
    }

    private OnCameraListener mOnCameraListener;
    private OnImgAnalysisListener mOnImgAnalysisListener;
    private OnFocusListener mOnFocusListener;
    private OnCameraFaceListener mOnCameraFaceListener;
    private OnPreviewLayoutListener mOnPreviewLayoutListener;
    private OnCameraBindListener mOnCameraBindListener;

    public void setOnCameraListener(OnCameraListener listener) {
        mOnCameraListener = listener;
    }

    public void setOnImgAnalysisListener(OnImgAnalysisListener listener) {
        mOnImgAnalysisListener = listener;
    }

    public void setOnFocusListener(OnFocusListener listener) {
        mOnFocusListener = listener;
    }

    public void setOnCameraFaceListener(OnCameraFaceListener listener) {
        mOnCameraFaceListener = listener;
    }

    public void setOnPreviewLayoutListener(OnPreviewLayoutListener listener) {
        mOnPreviewLayoutListener = listener;
    }

    public void setOnCameraBindListener(OnCameraBindListener listener) {
        mOnCameraBindListener = listener;
    }

    /**
     * 初始化相机
     */
    public void initCamera(CameraOption option, LifecycleOwner lifecycleOwner) {
        mLifecycleOwner = lifecycleOwner;
        setOption(option);
        setPreviewAspect(mCameraParam.asRatio);

        reset();
    }

    /**
     * 设置预览比例以及布局大小
     * 子类可自己计算比例，以及布局位置
     * @param asRatio
     */
    protected void setPreviewAspect(@ExAspectRatio.ExRatio int asRatio) {
        //是否是竖屏
        boolean isPortrait = mContext.getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT;

        int bottomPanelHeight = CameraUtil.dip2px(mContext, 90);
        int bottomPanelOffset = SCREEN_HEIGHT - bottomPanelHeight;

        int width = SCREEN_WIDTH;
        int height = SCREEN_HEIGHT;
        int topMargin = 0;
        int leftMargin = 0;

        if (isPortrait) {
            switch (asRatio) {
                case ExAspectRatio.RATIO_16_9:
                    height = (int) (width * 16 / 9.0F);
                    break;
                case ExAspectRatio.RATIO_4_3:
                    height = (int) (width * 4 / 3.0F);
                    break;
                case ExAspectRatio.RATIO_1_1:
                    height = width = SCREEN_WIDTH;
                    break;
            }

            topMargin = (SCREEN_HEIGHT - height) / 2;
            if (topMargin + height > bottomPanelOffset) {
                topMargin = bottomPanelOffset - height;
            }
        } else {
            switch (asRatio) {
                case ExAspectRatio.RATIO_16_9:
                    width = (int) (height * 16 / 9.0F);
                    break;
                case ExAspectRatio.RATIO_4_3:
                    width = (int) (height * 4 / 3.0F);
                    break;
                case ExAspectRatio.RATIO_1_1:
                    height = width = SCREEN_HEIGHT;
                    break;
            }

            leftMargin = (SCREEN_WIDTH - width) / 2;
        }

        if (mOnPreviewLayoutListener != null) {
            mOnPreviewLayoutListener.onLayoutSizeChange(width, height, leftMargin, topMargin);
        }

        setLayoutParams(width, height, leftMargin, topMargin);
    }

    /**
     * 子类可以重新改方法重新布局，也可以设置 OnPreviewLayoutListener
     * @param width
     * @param height
     * @param leftMargin
     * @param topMargin
     */
    protected void setLayoutParams(int width, int height, int leftMargin, int topMargin) {
        ViewGroup.LayoutParams params =  getLayoutParams();
        if (params == null) {
            return;
        }

        params.width = width;
        params.height = height;

        if (params instanceof ConstraintLayout.LayoutParams) {
            ConstraintLayout.LayoutParams conParams = (ConstraintLayout.LayoutParams)params;
            conParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
            conParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            conParams.topMargin = topMargin;
            conParams.leftMargin = leftMargin;
        }

        setLayoutParams(params);
    }

    /**
     * 设置参数
     *
     * @param option
     */
    private void setOption(CameraOption option) {
        if (option == null) {
            return;
        }

        mCameraParam.faceFront = option.isFaceFront();
        mCameraParam.asRatio = option.getRatio();
        mIsImgAnalysis = option.isAnalysisImg();
        mOutFilePath = option.getOutPath();

        //如果是分析图片，默认后置
        if (mIsImgAnalysis) {
            mCameraParam.faceFront = false;
        }
    }

    /**
     * 初始化配置信息
     */
    private void initCameraConfig() {
        initImageAnalysis();
        initImageCapture();
        initPreview();
        initCameraSelector();
    }

    /**
     * 图像分析
     */
    private void initImageAnalysis() {
        //设置分辨率，某些机型无法识别
        mImageAnalysis = new ImageAnalysis.Builder()
                // 分辨率
                //.setTargetResolution(CameraUtil.computeSize(mCameraParam.asRatio, SCREEN_WIDTH))
                // 非阻塞模式
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        if (mIsImgAnalysis) {
            startImgAnalysis();
        }
    }


    /**
     * 构建图像捕获用例
     */
    private void initImageCapture() {
        // 构建图像捕获用例
        // 暂时cameraX outImage 不支持1：1
        int ratio = mCameraParam.asRatio;
        boolean squareImg = false;
        if (ratio == ExAspectRatio.RATIO_1_1) {
            squareImg = true;
            //ratio = ExAspectRatio.RATIO_16_9;
        }

        if (squareImg) {
            mImageCapture = new ImageCapture.Builder()
                    .setFlashMode(ImageCapture.FLASH_MODE_OFF)
                    .setTargetResolution(new Size(2000, 2000))
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                    .build();
        } else {
            mImageCapture = new ImageCapture.Builder()
                    .setFlashMode(ImageCapture.FLASH_MODE_OFF)
                    .setTargetAspectRatio(ratio)
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                    .build();
        }

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

    private void setVideoCapture () {
         VideoCaptureConfig.Builder builder = new VideoCaptureConfig.Builder();

    }

    /**
     * 构建图像预览
     */
    private void initPreview() {
        int ratio = mCameraParam.asRatio;
        if (ratio == ExAspectRatio.RATIO_1_1) {
            ratio = ExAspectRatio.RATIO_4_3;
        }

        mPreview = new Preview.Builder()
                .setTargetAspectRatio(ratio)
                .build();
    }

    /**
     * 选择摄像头
     */
    private void initCameraSelector() {
        mCameraSelector = new CameraSelector.Builder()
                .requireLensFacing(mCameraParam.faceFront ?
                        CameraSelector.LENS_FACING_FRONT : CameraSelector.LENS_FACING_BACK)
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
        mPreview.setSurfaceProvider(createSurfaceProvider());
        mCamera = cameraProvider.bindToLifecycle(mLifecycleOwner, mCameraSelector,
                mImageCapture, mImageAnalysis, mPreview);

        mCameraInfo = mCamera.getCameraInfo();
        mCameraControl = mCamera.getCameraControl();

        if (mOnCameraBindListener != null) {
            mOnCameraBindListener.onCameraBind();
        }
    }

    @Override
    public void takePhoto() {
        final File file = !TextUtils.isEmpty(mOutFilePath) ? new File(mOutFilePath) : CameraUtil.getOutFile(mContext);
        savePhotoToFile(file);
    }

    @Override
    public void takeVideo() {
        final File file = !TextUtils.isEmpty(mOutFilePath) ? new File(mOutFilePath) : CameraUtil.getVideoOutFile(mContext);
        saveVideoToFile(file);
    }

    /**
     * 直接将图片保存到文件，由系统默认处理
     * @param file
     */
    private void savePhotoToFile(final File file) {
        ImageCapture.Metadata metadata = new ImageCapture.Metadata();
        metadata.setReversedHorizontal(mCameraParam.faceFront);
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
                        Toast.makeText(mContext, R.string.take_photo_fail, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 对返回的内存中的图片自定义处理（处理）
     * @param file
     */
    private void saveToFileFromMemory(final File file) {
        mImageCapture.takePicture(mExecutor, new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(final @NonNull ImageProxy image) {
                //super.onCaptureSuccess(image);
                //TODO Save Image

                //if (mIoExecutor == null) {
                //    mIoExecutor = Executors.newSingleThreadExecutor();
                //}
                //mIoExecutor.execute(saver);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                super.onError(exception);
            }
        });
    }

    private void saveVideoToFile(File file) {
        //TODO
    }

    @Override
    public void focus(float x, float y, float rawX, float rawY) {
        if (!isAttachedToWindow()) {
            return;
        }

        if (mOnFocusListener != null) {
            mOnFocusListener.onStartFocus(x , y, rawX, rawY);
        }

        MeteringPointFactory factory = createMeteringPointFactory(mCameraSelector);
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
                    if (mOnFocusListener != null) {
                        mOnFocusListener.onEndFocus(result.isFocusSuccessful());
                    }
                } catch (Exception e) {
                    if (mOnFocusListener != null) {
                        mOnFocusListener.onEndFocus(false);
                    }
                }
            }
        }, mExecutor);
    }

    @Override
    public void switchFace() {
        if (mCameraParam.faceFront) {
            mCameraParam.faceFront = false;
        } else {
            mCameraParam.faceFront = true;
        }

        if (mCameraParam.lightState == IFlashLight.FILL) {
            mCameraParam.lightState = IFlashLight.AUTO;
        }

        if (mOnCameraFaceListener != null) {
            mOnCameraFaceListener.onSwitchCamera(mCameraParam.faceFront);
        }
        reset();
    }

    @Override
    public void switchAspect(@ExAspectRatio.ExRatio int ratio) {
        mCameraParam.asRatio = ratio;
        setPreviewAspect(ratio);
        reset();
    }

    /**
     * 相机缩放
     */
    public void scale(float scale) {
        if (mCameraControl != null) {
            mCameraControl.setZoomRatio(scale);
        }
    }

    @Override
    public void reset() {
        mExecutor = ContextCompat.getMainExecutor(getContext());
        mCameraProviderFuture = ProcessCameraProvider.getInstance(getContext());

        initCameraConfig();

        mCameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    mCameraProvider = mCameraProviderFuture.get();
                    mCameraProvider.unbindAll();
                    bindPreview(mCameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                }
            }
        }, mExecutor);
    }

    @Override
    public void cancel() {
        if (mOnCameraListener != null) {
            mOnCameraListener.onCancel();
        }
    }

    @Override
    public CameraParam getCameraParam() {
        return mCameraParam;
    }

    @Override
    public void closeFlashLight() {
        mImageCapture.setFlashMode(ImageCapture.FLASH_MODE_OFF);
        mCameraControl.enableTorch(false);
        mCameraParam.lightState = IFlashLight.CLOSE;
    }

    @Override
    public void openFlashLight() {
        mImageCapture.setFlashMode(ImageCapture.FLASH_MODE_ON);
        mCameraParam.lightState = IFlashLight.OPEN;
    }

    @Override
    public void autoFlashLight() {
        mImageCapture.setFlashMode(ImageCapture.FLASH_MODE_AUTO);
        mCameraParam.lightState = IFlashLight.AUTO;
    }

    @Override
    public void fillLight() {
        mCameraControl.enableTorch(true);
        mCameraParam.lightState = IFlashLight.FILL;
    }

    @Override
    public void onClick(float x, float y, float rawX, float rawY) {
        focus(x, y, rawX, rawY);
    }

    @Override
    public void onZoom(float scale) {
        scale(scale);
    }

    @NonNull
    @Override
    public CameraXConfig getCameraXConfig() {
        return Camera2Config.defaultConfig();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        release();
    }

    public void release() {
        if (mCameraProvider != null) {
            mCameraProvider.unbindAll();
        }
    }
}
