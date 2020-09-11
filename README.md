# TakePhotoX
基于camerax拍照

# Demo下载
https://www.pgyer.com/takephotox
安装密码：123456

### 功能
 - 支持前后摄像头切换
 - 支持4:3 16:9 1:1 图片拍摄
 - 支持二维码扫描识别
 - 支持灯光控制

## 使用
 - 1.在project的build.gradle添加如下代码
```gradle
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```

 - 2.依赖androidx
```gradle

dependencies {
  implementation 'com.github.yijiebuyi:TakePhotoX:v1.1.2'
}

```

### 拍照基本用法：
- 使用CameraView，自己实现相机ui
```java
//CameraView对外提供的功能，详细见ICamera，IFlashLight

//CameraView使用
private CameraView mCameraView;
//================常用的功能如下=================
//拍照
mCameraView.take();
//对焦
mCameraView.focus(float x, float y, float rawX, float rawY);
//切换前置后置
mCameraView.switchFace();
//相机切换预览比例和拍照比例
mCameraView.switchAspect(@ExAspectRatio.ExRatio int ratio);
    
//================设置回调======================
//设置拍照回调
mCameraView.setOnCameraListener(OnCameraListener l);
//设置对焦回调
mCameraView.setOnFocusListener(OnFocusListener l);    
//设置图片分析回调
mCameraView.setOnImgAnalysisListener(OnImgAnalysisListener l);
//设置前后摄像头切换回调
mCameraView.setOnCameraFaceListener(OnCameraFaceListener l);
//设置相机预览view的布局和尺寸变化回调
mCameraView.setOnPreviewLayoutListener(OnPreviewLayoutListener l);
```



- 也可以使用CameraFragment，使用默认的提供的ui效果
```java
   FragmentManager fm = getSupportFragmentManager();
   FragmentTransaction ft = fm.beginTransaction();
   final CameraFragment cfg = new CameraFragment();

   CameraOption option = new CameraOption.Builder(ExAspectRatio.RATIO_16_9)
           .faceFront(false)
           .build();

   Bundle data = new Bundle();
   data.putSerializable(CameraFragment.KEY_CAMERA_OPTION, option);
   cfg.setArguments(data);
   cfg.setOnCameraListener(new OnCameraListener() {
       @Override
       public void onTaken(Uri uri) {
           //返回拍照图片uri
       }

       @Override
       public void onCancel() {
           finish();
       }
   });
```

- 直接使用CameraXActivity
```java
startActivityForResult(new Intent(MainActivity.this, CameraXActivity.class), 1000);

@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            //返回拍照的图片地址
            Uri uri = data.getData();
        }
    }
```

### 二维码扫描基本用法：
注：需要依赖zxing的包
- 二维码扫描 直接使用QrCodeFragment
- 二维码扫描 使用QRCodeView
```java
    mQRCodeView = new QRCodeView(mContext);
    mQRCodeView.setOnImgAnalysisListener(this);
    mQRCodeView.setScannerFrameOption(new ScannerFrameOption.Builder()
            .frameMode(ScannerFrameOption.FrameMode.MODE_FRAME_SQUARE)
            .frameRatio(0.6f)
            .build());

   //add mQRCodeView
```

