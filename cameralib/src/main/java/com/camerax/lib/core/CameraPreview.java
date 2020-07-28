package com.camerax.lib.core;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.view.PreviewView;

/**
 * Copyright (C) 2017
 * 版权所有
 * <p>
 * 功能描述：相机预览view
 * <p>
 * 作者：yijiebuyi
 * 创建时间：2020/7/22
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */

public class CameraPreview extends PreviewView {
    private final float MAX_SCALE = 4.0f;
    private final float MIN_SCALE = 1.0f;

    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;

    private CameraGestureListener mCameraGestureListener;
    private float mCurrentScale = 1.0f;

    public CameraPreview(@NonNull Context context) {
        super(context);

        init(context);
    }

    public CameraPreview(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public CameraPreview(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    public CameraPreview(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context);
    }

    private void init(Context context) {
        mCurrentScale =  1.0f;
        mGestureDetector = new GestureDetector(context, new GestureListener());
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);
        mGestureDetector.onTouchEvent(event);

        return true;
    }

    public void setCameraGestureListener(CameraGestureListener listener) {
        mCameraGestureListener = listener;
    }

    public interface CameraGestureListener {
        void onClick(float x, float y, float rawX, float rawY);

        void onZoom(float scale);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scale = detector.getScaleFactor();
            if (mCameraGestureListener != null) {
                float s = scale * mCurrentScale;
                if (s < MIN_SCALE) {
                    s = MIN_SCALE;
                } else if (s > MAX_SCALE){
                    s = MAX_SCALE;
                }
                mCameraGestureListener.onZoom(s);
            }
            return super.onScale(detector);
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            float scale = detector.getScaleFactor();
            return super.onScaleBegin(detector);
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            float scale = detector.getScaleFactor();
            mCurrentScale *= scale;

            if (mCurrentScale < MIN_SCALE) {
                mCurrentScale = MIN_SCALE;
            } else if (mCurrentScale > MAX_SCALE) {
                mCurrentScale = MAX_SCALE;
            }
            super.onScaleEnd(detector);
        }
    }


    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return super.onDown(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return super.onDoubleTapEvent(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (mCameraGestureListener != null) {
                mCameraGestureListener.onClick(e.getX(), e.getY(), e.getRawX(), e.getRawY());
            }
            return super.onSingleTapConfirmed(e);
        }
    }


}
