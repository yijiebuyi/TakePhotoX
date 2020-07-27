package com.camerax.lib.core;

import java.io.Serializable;

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

public class CameraOption implements Serializable {
    private int mRatio;
    private boolean mFaceFront;
    private String mOutPath;
    private boolean mAnalysisImg;

    private CameraOption(Builder builder) {
        mRatio = builder.ratio;
        mFaceFront = builder.faceFront;
        mOutPath = builder.outPath;
        mAnalysisImg = builder.analysisImg;
    }

    public int getRatio() {
        return mRatio;
    }

    public boolean isFaceFront() {
        return mFaceFront;
    }

    public String getOutPath() {
        return mOutPath;
    }

    public boolean isAnalysisImg() {
        return mAnalysisImg;
    }

    public static final class Builder {
        private int ratio;
        private boolean faceFront;
        private String outPath;
        private boolean analysisImg;

        public Builder(@ExAspectRatio.ExRatio int ratio) {
            this.ratio = ratio;
        }

        public Builder ratio(int ratio) {
            this.ratio = ratio;
            return this;
        }

        public Builder faceFront(boolean faceFront) {
            this.faceFront = faceFront;
            return this;
        }

        public Builder outPath(String outPath) {
            this.outPath = outPath;
            return this;
        }

        public Builder analysisImg(boolean analysisImg) {
            this.analysisImg = analysisImg;
            return this;
        }

        public CameraOption build() {
            return new CameraOption(this);
        }
    }
}
