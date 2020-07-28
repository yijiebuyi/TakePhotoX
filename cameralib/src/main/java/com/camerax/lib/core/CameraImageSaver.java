package com.camerax.lib.core;

import android.graphics.ImageFormat;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.ImageProxy;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

/**
 * Copyright (C) 2017
 * 版权所有
 * <p>
 * 功能描述：
 * <p>
 * 作者：yijiebuyi
 * 创建时间：2020/7/28
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */

public class CameraImageSaver implements Runnable {
    private static final int NO_ERROR = 0;
    /** Failed to write to or close the file */
    private static final int FILE_IO_FAILED = 1;
    /** Failure when attempting to encode image */
    private static final int ENCODE_FAILED = 2;
    /** Failure when attempting to crop image */
    private static final int CROP_FAILED = 3;
    private static final int UNKNOWN = 4;

    private static final String TAG = "ImageSaver";

    private static final String TEMP_FILE_PREFIX = "CameraX";
    private static final String TEMP_FILE_SUFFIX = ".tmp";
    private static final int COPY_BUFFER_SIZE = 1024;
    private static final int PENDING = 1;
    private static final int NOT_PENDING = 0;

    // The image that was captured
    private final ImageProxy mImage;
    // The orientation of the image
    private final int mOrientation;
    // The target location to save the image to.
    // The executor to call back on
    private final Executor mExecutor;
    private OnImageSavedListener mListener;

    private final File mOutFile;
    private boolean mHoriMirror;

    public CameraImageSaver(
            ImageProxy image,
            File outFile,
            int orientation,
            boolean horiMirror,
            Executor executor,
            OnImageSavedListener listener) {
        mImage = image;
        mOutFile = outFile;
        mHoriMirror = horiMirror;
        mOrientation = orientation;
        mListener = listener;
        mExecutor = executor;
    }

    @Override
    public void run() {
// Finally, we save the file to disk
        int saveError = NO_ERROR;
        String errorMessage = null;
        Exception exception = null;

        File file;
        Uri outputUri = null;
        try {
            // Create a temp file if the save location is not a file. This is necessary because
            // ExifInterface only supports File.
            file = isSaveToFile() ? mOutFile :
                    File.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX);
        } catch (IOException e) {
            postError(FILE_IO_FAILED, "Failed to create temp file", e);
            return;
        }

        try (ImageProxy imageToClose = mImage;
             FileOutputStream output = new FileOutputStream(file)) {
            byte[] bytes = ImageUtil.imageToJpegByteArray(mImage);
            output.write(bytes);

            Exif exif = Exif.createFromFile(file);
            exif.attachTimestamp();

            // Use exif for orientation (contains rotation only) from the original image if JPEG,
            // because imageToJpegByteArray removes EXIF in certain conditions. See b/124280392
            if (mImage.getFormat() == ImageFormat.JPEG) {
                ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
                // Rewind to make sure it is at the beginning of the buffer
                buffer.rewind();

                byte[] data = new byte[buffer.capacity()];
                buffer.get(data);
                InputStream inputStream = new ByteArrayInputStream(data);
                Exif originalExif = Exif.createFromInputStream(inputStream);

                exif.setOrientation(originalExif.getOrientation());
            } else {
                exif.rotate(mOrientation);
            }

            if (mHoriMirror) {
                exif.flipHorizontally();
            }
            exif.save();
        } catch (IOException | IllegalArgumentException e) {
            saveError = FILE_IO_FAILED;
            errorMessage = "Failed to write or close the file";
            exception = e;
        } catch (ImageUtil.CodecFailedException e) {
            switch (e.getFailureType()) {
                case ENCODE_FAILED:
                    saveError = ENCODE_FAILED;
                    errorMessage = "Failed to encode mImage";
                    break;
                case DECODE_FAILED:
                    saveError = CROP_FAILED;
                    errorMessage = "Failed to crop mImage";
                    break;
                case UNKNOWN:
                default:
                    saveError = UNKNOWN;
                    errorMessage = "Failed to transcode mImage";
                    break;
            }
            exception = e;
        } finally {
            if (!isSaveToFile()) {
                // Cleanup temp file if created.
                file.delete();
            }
        }

        if (saveError != NO_ERROR) {
            postError(saveError, errorMessage, exception);
        } else {
            postSuccess(outputUri);
        }
    }

    private boolean isSaveToFile() {
        //return mOutputFileOptions.getFile() != null;
        return true;
    }

    private void postSuccess(final @Nullable Uri outputUri) {
        try {
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    mListener.onImageSaved(outputUri);
                }
            });
        } catch (RejectedExecutionException e) {
            Log.e(TAG, "Application executor rejected executing OnImageSavedCallback.onImageSaved "
                    + "callback. Skipping.");
        }
    }

    private void postError(final int saveError, final String message,
                           @Nullable final Throwable cause) {
        try {
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    mListener.onError(saveError, message, cause);
                }
            });
        } catch (RejectedExecutionException e) {
            Log.e(TAG, "Application executor rejected executing OnImageSavedCallback.onError "
                    + "callback. Skipping.");
        }
    }

    public interface OnImageSavedListener{

        void onImageSaved(@NonNull Uri outputFileResults);

        void onError(int saveError, String message, @Nullable Throwable cause);
    }
}
