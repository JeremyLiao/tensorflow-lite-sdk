package com.jeremyliao.tflite_sdk;

import android.content.Context;

import java.io.IOException;

/**
 * Created by liaohailiang on 2018/8/4.
 */
public class TestTFLiteProcessor extends AbstractTFLiteProcessor {

    private float[] output = null;

    public TestTFLiteProcessor(Context context) throws IOException {
        super(context);
        output = new float[1];
    }

    @Override
    protected int getInputNum() {
        return 1;
    }

    @Override
    protected int getInputByteNumPerChannel() {
        return 4;
    }

    @Override
    protected String getModelPath() {
        return "test_model.tflite";
    }

    @Override
    protected Object getOutputBuffer() {
        return output;
    }
}
