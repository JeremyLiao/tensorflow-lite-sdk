package com.jeremyliao.tflite_sdk;

import android.content.Context;

import java.io.IOException;

/**
 * Created by liaohailiang on 2018/8/3.
 */
public class MnistImageClassifier extends AbstractImageClassifier {

    private static final float IMAGE_STD = 256.0f;

    private float[][] labelProbArray = null;

    public MnistImageClassifier(Context context) throws IOException {
        super(context);
        labelProbArray = new float[1][getNumLabels()];
    }

    @Override
    protected void addPixelValue(int pixelValue) {
        float r = (((pixelValue >> 16) & 0xFF));
        float g = (((pixelValue >> 8) & 0xFF));
        float b = ((pixelValue & 0xFF));
        float gray = r * 0.299f + g * 0.587f + b * 0.114f;
        imgData.putFloat(gray);
    }

    @Override
    protected float getProbability(int labelIndex) {
        return labelProbArray[0][labelIndex];
    }

    @Override
    protected void setProbability(int labelIndex, Number value) {
        labelProbArray[0][labelIndex] = value.floatValue();
    }

    @Override
    protected float getNormalizedProbability(int labelIndex) {
        return getProbability(labelIndex);
    }

    @Override
    protected Object getOutputBuffer() {
        return labelProbArray;
    }

    @Override
    protected String getModelPath() {
        return "mnist_model.tflite";
    }

    @Override
    protected String getLabelPath() {
        return "labels_mnist.txt";
    }

    @Override
    protected int getImageSizeX() {
        return 28;
    }

    @Override
    protected int getImageSizeY() {
        return 28;
    }

    @Override
    protected int getNumBytesPerChannel() {
        // a 32bit float value requires 4 bytes
        return 4;
    }

    @Override
    protected int getNumChannelsPerPixel() {
        return 1;
    }
}
