package com.camerax.lib;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
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
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

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

    private OnMediaListener mOnMediaListener;
    private Uri mPhotoUri;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        mActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_photox, null);
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
            mPhotoUri = data.getParcelable(KEY_PHOTO_URI);
            Glide.with(mContext)
                    .load(mPhotoUri)
                    .apply(new RequestOptions()
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE))
                    .addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    if (mOnMediaListener != null) {
                        mOnMediaListener.onMediaLoad(false);
                    }
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    if (mOnMediaListener != null) {
                        mOnMediaListener.onMediaLoad(true);
                    }
                    return false;
                }
            }).into(mPhotoView);
        } else {
            Toast.makeText(mContext, "img load failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.confirm_select) {
            if (mOnMediaListener != null) {
                mOnMediaListener.onPhotoSelect(mPhotoUri);
            }
        } else if (v.getId() == R.id.cancel) {
            if (mOnMediaListener != null) {
                mOnMediaListener.onCancel();
            }
        }
    }

    public void setOnMediaListener(OnMediaListener listener) {
        mOnMediaListener = listener;
    }
}
