package com.camerax.lib.core;

import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;
import androidx.camera.core.AspectRatio;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Copyright (C) 2017
 * 版权所有
 * <p>
 * 功能描述：
 * <p>
 * 作者：yijiebuyi
 * 创建时间：2020/7/22
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */

public class ExAspectRatio {
    public static final int RATIO_UNKNOWN = -2;
    /** 1:1 standard aspect ratio. */
    public static final int RATIO_1_1 = -1;
    /** 4:3 standard aspect ratio. */
    public static final int RATIO_4_3 = AspectRatio.RATIO_4_3;
    /** 16:9 standard aspect ratio. */
    public static final int RATIO_16_9 = AspectRatio.RATIO_16_9;

    private ExAspectRatio() {}

    /**
     * @hide
     */
    @IntDef({RATIO_UNKNOWN, RATIO_1_1, RATIO_4_3, RATIO_16_9})
    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    public @interface ExRatio {
    }
}
