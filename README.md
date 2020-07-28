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
  implementation 'com.github.yijiebuyi:TakePhotoX:v1.0.4'
}

```

#### CameraView基本用法：
```java

private CameraView mCameraView;
、、、、、、

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

```java
