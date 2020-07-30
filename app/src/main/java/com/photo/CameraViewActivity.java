package com.photo;

import android.Manifest;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.camerax.lib.CameraXActivity;
import com.camerax.lib.QrCodeFragment;
import com.camerax.lib.core.CameraView;
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
 * 创建时间：2020/7/30
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */

public class CameraViewActivity extends AppCompatActivity {
    CameraView mCaraView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermission();
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
                        mCaraView = new CameraView(CameraViewActivity.this);
                        mCaraView.initCamera(null, CameraViewActivity.this);

                        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        addContentView(mCaraView, params);

                        /*mCaraView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(CameraViewActivity.this, "succ", 1).show();
                                mCaraView.take();
                            }
                        });*/
                        mCaraView.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                    Toast.makeText(CameraViewActivity.this, "succ", 1).show();
                                    mCaraView.take();
                                }
                                return true;
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(Permission[] refusedPermissions) {
                        Toast.makeText(CameraViewActivity.this, com.camerax.lib.R.string.permission_grant_fail, Toast.LENGTH_SHORT).show();
                    }
                });

    }

}
