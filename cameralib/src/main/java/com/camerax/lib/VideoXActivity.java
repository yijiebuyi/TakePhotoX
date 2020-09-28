package com.camerax.lib;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.camerax.lib.core.CameraOption;
import com.camerax.lib.core.ExAspectRatio;
import com.camerax.lib.core.OnCameraListener;
import com.qw.soul.permission.SoulPermission;
import com.qw.soul.permission.bean.Permission;
import com.qw.soul.permission.bean.Permissions;
import com.qw.soul.permission.callbcak.CheckRequestPermissionsListener;

/**
 * Copyright (C) 2017
 * 版权所有
 * <p>
 * 功能描述：
 * <p>
 * 作者：yijiebuyi
 * 创建时间：2020/8/21
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */

public class VideoXActivity extends AppCompatActivity {
    private final static String VIDEO_FRAGMENT = "video_fragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camerax);
        requestPermission();
    }

    public void enterVideoFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        final VideoFragment cfg = new VideoFragment();

        CameraOption option = new CameraOption.Builder(ExAspectRatio.RATIO_16_9)
                //.outPath(Environment.getExternalStorageDirectory() + "/AAAA.JPEG")
                .faceFront(false)
                .build();

        Bundle data = new Bundle();
        data.putSerializable(CameraConstant.KEY_CAMERA_OPTION, option);
        data.putInt(CameraConstant.KEY_MAX_VIDEO_DURATION,
                getIntent().getIntExtra(CameraConstant.KEY_MAX_VIDEO_DURATION, 0));
        cfg.setArguments(data);
        cfg.setOnCameraListener(new OnCameraListener() {
            @Override
            public void onTaken(Uri uri) {
                enterVideoPlayFragment(uri);
            }

            @Override
            public void onCancel() {
                finish();
            }
        });

        ft.replace(R.id.container, cfg, VIDEO_FRAGMENT);
        ft.commit();
    }


    public void enterVideoPlayFragment(Uri fileUri) {
        FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();

        final VideoPlayFragment fg = new VideoPlayFragment();
        Bundle data = new Bundle();
        data.putParcelable(VideoPlayFragment.KEY_VIDEO_URI, fileUri);
        fg.setArguments(data);
        fg.setOnMediaListener(new OnMediaListener() {
            @Override
            public void onMediaLoad(boolean succ) {

            }

            @Override
            public void onPhotoSelect(Uri uri) {
                finishWithData(uri);
            }

            @Override
            public void onCancel() {
                enterVideoFragment();
            }
        });

        ft.replace(R.id.container, fg);
        ft.commit();
    }

    private void requestPermission() {
        SoulPermission.getInstance().checkAndRequestPermissions(
                Permissions.build(Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE),
                //if you want do noting or no need all the callbacks you may use SimplePermissionsAdapter instead
                new CheckRequestPermissionsListener() {
                    @Override
                    public void onAllPermissionOk(Permission[] allPermissions) {
                        //Toast.makeText(CameraXActivity.this, "授权成功", Toast.LENGTH_SHORT).show();
                        enterVideoFragment();
                    }

                    @Override
                    public void onPermissionDenied(Permission[] refusedPermissions) {
                        Toast.makeText(VideoXActivity.this, R.string.permission_grant_fail, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void finishWithData(Uri uri) {
        Intent intent = new Intent();
        intent.setData(uri);
        setResult(RESULT_OK, intent);
        finish();
    }
}
