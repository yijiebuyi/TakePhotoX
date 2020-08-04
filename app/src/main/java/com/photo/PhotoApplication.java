package com.photo;

import android.app.Application;
import android.os.StrictMode;
import android.widget.Toast;

import com.squareup.leakcanary.LeakCanary;

/**
 * Copyright (C) 2017
 * 版权所有
 * <p>
 * 功能描述：
 * <p>
 * 作者：yijiebuyi
 * 创建时间：2020/8/4
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */

public class PhotoApplication extends Application {

    @Override public void onCreate() {
        super.onCreate();
        setupLeakCanary();
    }

    protected void setupLeakCanary() {
        if (BuildConfig.DEBUG) {
            if (LeakCanary.isInAnalyzerProcess(this)) {
                return;
            }
            Toast.makeText(this, "photo application", 1).show();
            LeakCanary.install(this);
            LeakCanary.enableDisplayLeakActivity(this);
        }
    }
}
