package com.camerax.lib;

import android.graphics.Point;
import android.graphics.Rect;

import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;
import androidx.camera.core.AspectRatio;

import com.camerax.lib.core.ExAspectRatio;

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
    private @FrameMode.Mode int mFrameMode;
    private Point mFrameOffset;
    private float mFrameRatio;
    private int mFrameColor;
    private int mCornerColor;

    private int mFrameWidth;
    private int mFrameHeight;

    private ScannerFrameOption(Builder builder) {
        mFrameMode = builder.frameMode;
        mFrameOffset = builder.frameOffset;
        mFrameRatio = builder.frameRatio;
        mFrameColor = builder.frameColor;
        mCornerColor = builder.cornerColor;
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

    public int getFrameColor() {
        return mFrameColor;
    }

    public int getCornerColor() {
        return mCornerColor;
    }


    public int getFrameWidth() {
        return mFrameWidth;
    }

    public int getFrameHeight() {
        return mFrameHeight;
    }

    public static final class Builder {
        private @FrameMode.Mode int frameMode;
        private Point frameOffset;
        private float frameRatio;
        private int frameColor;
        private int cornerColor;
        private int frameWidth;
        private int frameHeight;

        public Builder(@FrameMode.Mode int mode) {
            frameMode = mode;
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

        public Builder frameColor(int color) {
            frameColor = color;
            return this;
        }

        public Builder cornerColor(int color) {
            cornerColor = color;
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

        private FrameMode() {}

        /**
         * @hide
         */
        @IntDef({MODE_FRAME_NO, MODE_FRAME_SAME_RATIO, MODE_FRAME_SQUARE, MODE_FRAME_FREE})
        @Retention(RetentionPolicy.SOURCE)
        public @interface Mode {
        }
    }
}
