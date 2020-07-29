package com.photo;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.camerax.lib.OnPhotoListener;
import com.camerax.lib.PhotoFragment;
import com.camerax.lib.QrCodeFragment;
import com.camerax.lib.QrCodeParser;
import com.camerax.lib.ScannerFrameOption;

/**
 * Copyright (C) 2017
 * 版权所有
 * <p>
 * 功能描述：
 * <p>
 * 作者：yijiebuyi
 * 创建时间：2020/7/29
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */

public class QrCodeActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(com.camerax.lib.R.layout.activity_camerax);

        FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();

        final QrCodeFragment fg = new QrCodeFragment();
        ft.replace(com.camerax.lib.R.id.container, fg);
        ft.commit();
    }

}
