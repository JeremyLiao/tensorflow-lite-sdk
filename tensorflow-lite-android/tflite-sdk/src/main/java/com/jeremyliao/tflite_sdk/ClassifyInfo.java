package com.jeremyliao.tflite_sdk;

/**
 * Created by liaohailiang on 2018/8/3.
 */
public class ClassifyInfo {

    private String label;
    private long index;
    private float probability;

    public ClassifyInfo() {
    }

    public ClassifyInfo(String label, long index, float probability) {
        this.label = label;
        this.index = index;
        this.probability = probability;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public float getProbability() {
        return probability;
    }

    public void setProbability(float probability) {
        this.probability = probability;
    }

    @Override
    public String toString() {
        return "label: " + label + " | index: " + index + " | probability: " + probability;
    }
}
