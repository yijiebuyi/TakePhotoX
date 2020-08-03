package com.camerax.lib.analysis;

import android.graphics.Point;

import androidx.annotation.IntDef;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

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

public class ScannerFrameOption implements Serializable {
    private final static float DEFAULT_FRAME_RATIO = 0.6f;
    private final static int DEFAULT_FRAME_BORDER_COLOR = 0xFFDDDDDD;
    private final static int DEFAULT_FRAME_CORNER_COLOR = 0xFF0F94ED;

    private @FrameMode.Mode
    int mFrameMode;
    private Point mFrameOffset;
    private float mFrameRatio;
    private int mFrameBorderColor;
    private int mFrameCornerColor;

    private int mFrameWidth;
    private int mFrameHeight;

    private ScannerFrameOption(Builder builder) {
        mFrameMode = builder.frameMode;
        mFrameOffset = builder.frameOffset;
        mFrameRatio = builder.frameRatio;
        mFrameBorderColor = builder.frameBorderColor;
        mFrameCornerColor = builder.frameCornerColor;
    }

    public int getFrameMode() {
        return mFrameMode;
    }

    public Point getFrameOffset() {
        return mFrameOffset;
    }

    public float getFrameRatio() {
        return mFrameRatio;
    }

    public int getFrameBorderColor() {
        return mFrameBorderColor;
    }

    public int getFrameCornerColor() {
        return mFrameCornerColor;
    }


    public int getFrameWidth() {
        return mFrameWidth;
    }

    public int getFrameHeight() {
        return mFrameHeight;
    }

    public static final class Builder {
        @FrameMode.Mode
        private int frameMode = FrameMode.MODE_FRAME_SQUARE;
        private Point frameOffset;
        private float frameRatio;
        private int frameBorderColor;
        private int frameCornerColor;
        private int frameWidth;
        private int frameHeight;

        //默认配置
        public Builder() {
            frameMode = FrameMode.MODE_FRAME_SQUARE;
            frameRatio = DEFAULT_FRAME_RATIO;
            frameBorderColor = DEFAULT_FRAME_BORDER_COLOR;
            frameCornerColor = DEFAULT_FRAME_CORNER_COLOR;
        }

        public Builder frameMode(int mode) {
            frameMode = mode;
            return this;
        }

        public Builder frameOffset(Point offset) {
            frameOffset = offset;
            return this;
        }

        public Builder frameRatio(float ratio) {
            frameRatio = ratio;
            return this;
        }

        public Builder frameBorderColor(int color) {
            frameBorderColor = color;
            return this;
        }

        public Builder frameCornerColor(int color) {
            frameCornerColor = color;
            return this;
        }

        public Builder frameWidth(int width) {
            frameWidth = width;
            return this;
        }

        public Builder frameHeight(int height) {
            frameHeight = height;
            return this;
        }

        public ScannerFrameOption build() {
            return new ScannerFrameOption(this);
        }
    }

    public static class FrameMode {
        /**
         * 无边框
         */
        public static final int MODE_FRAME_NO = 0;
        /**
         * 和view同样的比例（比例取值：0~1）
         */
        public static final int MODE_FRAME_SAME_RATIO = 1;
        /**
         * 方形
         */
        public static final int MODE_FRAME_SQUARE = 2;
        /**
         * 自由比例
         */
        public static final int MODE_FRAME_FREE = 3;

        private FrameMode() {
        }

        /**
         * @hide
         */
        @IntDef({MODE_FRAME_NO, MODE_FRAME_SAME_RATIO, MODE_FRAME_SQUARE, MODE_FRAME_FREE})
        @Retention(RetentionPolicy.SOURCE)
        public @interface Mode {
        }
    }
}
