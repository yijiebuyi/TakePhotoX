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

import com.devbrackets.android.exomedia.listener.OnErrorListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;

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

public class VideoPlayFragment extends Fragment implements View.OnClickListener, OnPreparedListener {
    public final static String KEY_VIDEO_URI = "key_video_uri";

    private Context mContext;
    private Activity mActivity;

    private VideoView mVideoView;
    private ImageView mCancelBtn;
    private ImageView mConfirmSelectBtn;

    private Uri mVideoUri;
    private OnMediaListener mOnMediaListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        mActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_videox_paly, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mVideoView = view.findViewById(R.id.video_view);
        mVideoView.setOnPreparedListener(this);

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
            mVideoUri = data.getParcelable(KEY_VIDEO_URI);
            mVideoView.setVideoURI(mVideoUri);
            mVideoView.setOnErrorListener(new OnErrorListener() {
                @Override
                public boolean onError(Exception e) {
                    return false;
                }
            });
        } else {
            Toast.makeText(mContext, "img load failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.confirm_select) {
            if (mOnMediaListener != null) {
                mOnMediaListener.onPhotoSelect(mVideoUri);
            }
        } else if (v.getId() == R.id.cancel) {
            if (mOnMediaListener != null) {
                mOnMediaListener.onCancel();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mVideoView != null) {
            mVideoView.start();
        }
    }

    @Override
    public void onPrepared() {
        mVideoView.start();
        if (mOnMediaListener != null) {
            mOnMediaListener.onMediaLoad(true);
        }
    }

    public void setOnMediaListener(OnMediaListener listener) {
        mOnMediaListener = listener;
    }
}
