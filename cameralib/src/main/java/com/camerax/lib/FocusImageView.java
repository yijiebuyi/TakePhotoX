package com.camerax.lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * Copyright (C) 2017
 * 版权所有
 * <p>
 * 功能描述：对焦图片
 * <p>
 * 作者：yijiebuyi
 * 创建时间：2020/7/24
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */

public class FocusImageView extends AppCompatImageView {
    private static final int NO_ID = -1;
    private int mFocusImg = NO_ID;
    private int mFocusSucceedImg = NO_ID;
    private int mFocusFailedImg = NO_ID;
    private Animation mAnimation;
    private Handler mHandler;


    public FocusImageView(Context context) {
        super(context);
        init(context, null);
    }

    public FocusImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FocusImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.focusview_show);
        mHandler = new Handler();

        setVisibility(View.GONE);

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FocusImageView);
            mFocusImg = typedArray.getResourceId(R.styleable.FocusImageView_focusing_id, NO_ID);
            mFocusSucceedImg = typedArray.getResourceId(R.styleable.FocusImageView_focus_succ_id, NO_ID);
            mFocusFailedImg = typedArray.getResourceId(R.styleable.FocusImageView_focus_fail_id, NO_ID);
            typedArray.recycle();
        }
    }


    /**
     * 显示对焦图案
     */
    public void startFocus(float x, float y, float rawX, float rawY) {
        if (mFocusImg == NO_ID) {
            throw new RuntimeException("focus image is null");
        }
        //根据触摸的坐标设置聚焦图案的位置
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) getLayoutParams();

        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        params.topMargin = (int) rawY - getMeasuredHeight() / 2;
        params.leftMargin = (int) rawX - getMeasuredWidth() / 2;

        setLayoutParams(params);
        //设置控件可见，并开始动画
        setVisibility(View.VISIBLE);
        setImageResource(mFocusImg);
        startAnimation(mAnimation);
    }

    /**
     * 聚焦成功回调
     */
    public void onFocusSuccess() {
        setImageResource(mFocusSucceedImg);
        //移除在startFocus中设置的callback，1秒后隐藏该控件
        mHandler.removeCallbacks(null, null);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setVisibility(View.GONE);
            }
        }, 1000);

    }

    /**
     * 聚焦失败回调
     */
    public void onFocusFailed() {
        setImageResource(mFocusFailedImg);
        //移除在startFocus中设置的callback，1秒后隐藏该控件
        mHandler.removeCallbacks(null, null);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setVisibility(View.GONE);
            }
        }, 1000);
    }

    /**
     * 设置开始聚焦时的图片
     *
     * @param focus
     */
    public void setFocusImg(int focus) {
        mFocusImg = focus;
    }

    /**
     * 设置聚焦成功显示的图片
     *
     * @param focusSucceed
     */
    public void setFocusSucceedImg(int focusSucceed) {
        mFocusSucceedImg = focusSucceed;
    }
}
