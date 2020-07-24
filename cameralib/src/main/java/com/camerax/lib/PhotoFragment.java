package com.camerax.lib;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

/**
 * Copyright (C) 2017
 * 版权所有
 * <p>
 * 功能描述：图片浏览
 * <p>
 * 作者：yijiebuyi
 * 创建时间：2020/7/22
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */

public class PhotoFragment extends Fragment implements View.OnClickListener{
    public final static String KEY_PHOTO_URI = "key_photo_uri";

    private Context mContext;
    private Activity mActivity;

    private ImageView mPhotoView;
    private ImageView mCancelBtn;
    private ImageView mConfirmSelectBtn;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        mActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_photo, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPhotoView = view.findViewById(R.id.photo);

        mCancelBtn = view.findViewById(R.id.cancel);
        mConfirmSelectBtn = view.findViewById(R.id.confirm_select);

        mCancelBtn.setOnClickListener(this);
        mConfirmSelectBtn.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle data = getArguments();
        if (data != null) {
            Uri uri = data.getParcelable(KEY_PHOTO_URI);
            Glide.with(mContext).load(uri).into(mPhotoView);
        } else {
            Toast.makeText(mContext, "img load failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.confirm_select) {
            mActivity.finish();
        } else if (v.getId() == R.id.cancel) {
            ((CameraXActivity)mActivity).enterCameraFragment();
        }
    }
}
