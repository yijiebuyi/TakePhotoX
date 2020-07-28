# TakePhotoX
基于camerax拍照

### 功能
 - 依赖androidx
 - 支持前后摄像头切换
 - 支持4:3 16:9 1:1 图片拍摄
 - 支持二维码扫描识别
 - 支持灯光控制

## 使用

```gradle

dependencies {
  implementation 'com.github.yijiebuyi:TakePhotoX:v1.0.5'
}

```

#### 基本用法：
- 使用CameraView，自己实现相机ui
```java

private CameraView mCameraView;

//设置拍照回调
mCameraView.setOnCameraListener(new OnCameraListener() {
            @Override
            public void onTaken(Uri uri) {
                
            }

            @Override
            public void onCancel() {

            }
        });
        
//设置对焦回调
mCameraView.setOnFocusListener(new OnFocusListener() {
            @Override
            public void onStartFocus(float x, float y, float rawX, float rawY) {
                
            }

            @Override
            public void onEndFocus(boolean succ) {

            }
        });
        
//设置图片分析回调
mCameraView.setOnImgAnalysisListener(l);
//设置前后摄像头切换回调
mCameraView.setOnCameraFaceListener(l);

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
           cfg.hidePanel(false);
           enterPhotoFragment(uri);
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


- 后续添加系统默的拍照


