# tensorflow-lite-sdk

## 目标

- **基于Google Tensorflow-Lite，对官方Tensorflow-Lite Demo加以提炼，以更加通用的形式提供一个Tensorflow-Lite Android SDK**
- **从训练基于TensorFlow的深度学习模型，到把模型运行在Android TensorFlow Lite框架上，本项目提供一个完整的流程和方法**

## Android运行深度神经网络模型，只需要三步
1. **利用TensorFlow训练一个深度神经网络模型**（这部分不属于本项目内容，自己去学习吧：））
2. **生成Android平台可用的模型文件tflite** 
避免踩坑，可参见我的博文：[TensorFlow Lite研究](https://jeremyliao.github.io/ai/2018/08/08/TensorFlow-Lite%E7%A0%94%E7%A9%B6.html)
3. **利用本项目的SDK**：[tflite-sdk](https://github.com/JeremyLiao/tensorflow-lite-sdk/tree/master/tensorflow-lite-android/tflite-sdk)，继承并实现AbstractImageClassifier或AbstractTFLiteProcessor，就可以在Android平台上利用深度神经网络带来的强大能力了

## Module
- SDK Module：[tflite-sdk](https://github.com/JeremyLiao/tensorflow-lite-sdk/tree/master/tensorflow-lite-android/tflite-sdk)
- Demo Module：[app](https://github.com/JeremyLiao/tensorflow-lite-sdk/tree/master/tensorflow-lite-android/app)

## Overview
### 图片分类器

用于利用TensorFlow处理图片分类问题

[AbstractImageClassifier](https://github.com/JeremyLiao/tensorflow-lite-sdk/blob/master/tensorflow-lite-android/tflite-sdk/src/main/java/com/jeremyliao/tflite_sdk/AbstractImageClassifier.java) 图片分类器应该继承的基类

[QuantizedImageClassifier](https://github.com/JeremyLiao/tensorflow-lite-sdk/blob/master/tensorflow-lite-android/tflite-sdk/src/main/java/com/jeremyliao/tflite_sdk/QuantizedImageClassifier.java) 基于官方Demo中 tflite模型文件的分类器

[MnistImageClassifier](https://github.com/JeremyLiao/tensorflow-lite-sdk/blob/master/tensorflow-lite-android/tflite-sdk/src/main/java/com/jeremyliao/tflite_sdk/MnistImageClassifier.java) 自己训练了一个识别mnist数据集模型的分类器

#### 示例

```java
    public void classify2(View v) {
        Bitmap bitmap = getBitmap(this, R.drawable.six1);
        Bitmap bitmap1 = newScaledBitmap(bitmap, 28, 28);
        ClassifyInfo[] classifyInfos = classifier2.classifyBitmap(bitmap1);
        if (classifyInfos != null && classifyInfos.length > 0) {
            Toast.makeText(this, classifyInfos[0].toString(), Toast.LENGTH_LONG).show();
        }
        bitmap1.recycle();
        bitmap.recycle();
    }
```


### 更加基础的处理器

用于利用TensorFlow处理任意类型的输入

[AbstractTFLiteProcessor](https://github.com/JeremyLiao/tensorflow-lite-sdk/blob/master/tensorflow-lite-android/tflite-sdk/src/main/java/com/jeremyliao/tflite_sdk/AbstractTFLiteProcessor.java) 处理器基类

[TestTFLiteProcessor](https://github.com/JeremyLiao/tensorflow-lite-sdk/blob/master/tensorflow-lite-android/tflite-sdk/src/main/java/com/jeremyliao/tflite_sdk/TestTFLiteProcessor.java) 一个实现的例子

#### 示例

```
    public void testProcess(View v) {
        processor.run(new AbstractTFLiteProcessor.InputDataFactory() {
            @Override
            public void input(ByteBuffer buffer) {
                buffer.putFloat(6.0f);
            }
        });
    }
```
