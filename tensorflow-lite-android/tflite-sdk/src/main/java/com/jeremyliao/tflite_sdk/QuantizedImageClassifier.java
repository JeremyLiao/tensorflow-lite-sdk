package com.jeremyliao.tflite_sdk;

import android.content.Context;

import java.io.IOException;

/**
 * Created by liaohailiang on 2018/8/3.
 */
public class QuantizedImageClassifier extends AbstractImageClassifier {

    private byte[][] labelProbArray = null;

    public QuantizedImageClassifier(Context context) throws IOException {
        super(context);
        labelProbArray = new byte[1][getNumLabels()];
    }

    @Override
    protected void addPixelValue(int pixelValue) {
        imgData.put((byte) ((pixelValue >> 16) & 0xFF));
        imgData.put((byte) ((pixelValue >> 8) & 0xFF));
        imgData.put((byte) (pixelValue & 0xFF));
    }

    @Override
    protected float getProbability(int labelIndex) {
        return labelProbArray[0][labelIndex];
    }

    @Override
    protected void setProbability(int labelIndex, Number value) {
        labelProbArray[0][labelIndex] = value.byteValue();
    }

    @Override
    protected float getNormalizedProbability(int labelIndex) {
        return (labelProbArray[0][labelIndex] & 0xff) / 255.0f;
    }

    @Override
    protected Object getOutputBuffer() {
        return labelProbArray;
    }

    @Override
    protected String getModelPath() {
        return "mobilenet_quant_v1_224.tflite";
    }

    @Override
    protected String getLabelPath() {
        return "labels_mobilenet_quant_v1_224.txt";
    }

    @Override
    protected int getImageSizeX() {
        return 224;
    }

    @Override
    protected int getImageSizeY() {
        return 224;
    }

    @Override
    protected int getNumBytesPerChannel() {
        return 1;
    }

    @Override
    protected int getNumChannelsPerPixel() {
        return 3;
    }
}
