package com.jeremyliao.tensorflow_lite_android;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.jeremyliao.tflite_sdk.AbstractImageClassifier;
import com.jeremyliao.tflite_sdk.ClassifyInfo;
import com.jeremyliao.tflite_sdk.MnistImageClassifier;
import com.jeremyliao.tflite_sdk.QuantizedImageClassifier;
import com.jeremyliao.tflite_sdk.AbstractTFLiteProcessor;
import com.jeremyliao.tflite_sdk.TestTFLiteProcessor;

import java.io.IOException;
import java.nio.ByteBuffer;

public class TFLiteDemo extends Activity {

    private AbstractImageClassifier classifier1;
    private AbstractImageClassifier classifier2;
    private AbstractTFLiteProcessor processor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tflite_demo);
        try {
            classifier1 = new QuantizedImageClassifier(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            classifier2 = new MnistImageClassifier(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            processor = new TestTFLiteProcessor(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        classifier1.close();
        classifier2.close();
        processor.close();
    }

    public void classify1(View v) {
        Bitmap bitmap = getBitmap(this, R.drawable.mailbox);
        Bitmap bitmap1 = newScaledBitmap(bitmap, 224, 224);
        classifier1.classifyBitmap(bitmap1);
        bitmap1.recycle();
        bitmap.recycle();
    }

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

    public void testProcess(View v) {
        processor.run(new AbstractTFLiteProcessor.InputDataFactory() {
            @Override
            public void input(ByteBuffer buffer) {
                buffer.putFloat(6.0f);
            }
        });
    }

    private static Bitmap getBitmap(Context context, int vectorDrawableId) {
        Bitmap bitmap = null;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            Drawable vectorDrawable = context.getDrawable(vectorDrawableId);
            bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                    vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.draw(canvas);
        } else {
            bitmap = BitmapFactory.decodeResource(context.getResources(), vectorDrawableId);
        }
        return bitmap;
    }

    private static Bitmap newScaledBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float sx = (float) width / w;
        float sy = (float) height / h;
        matrix.postScale(sx, sy);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }
}
