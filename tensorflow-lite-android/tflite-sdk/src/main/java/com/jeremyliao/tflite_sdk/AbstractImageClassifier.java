package com.jeremyliao.tflite_sdk;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by liaohailiang on 2018/8/3.
 */
public abstract class AbstractImageClassifier {

    private static final String TAG = "AbstractImageClassifier";
    /**
     * Dimensions of inputs.
     */
    private static final int DIM_BATCH_SIZE = 1;
    private static final int FILTER_STAGES = 3;
    private static final float FILTER_FACTOR = 0.4f;

    protected ByteBuffer imgData = null;
    private int[] intValues;
    private List<String> labelList;
    protected Interpreter tflite;
    private float[][] filterLabelProbArray = null;
    private Context context;

    public AbstractImageClassifier(Context context) throws IOException {
        this.context = context;
        intValues = new int[getImageSizeX() * getImageSizeY()];
        tflite = new Interpreter(loadModelFile());
        labelList = loadLabelList();
        imgData = ByteBuffer.allocateDirect(
                DIM_BATCH_SIZE
                        * getImageSizeX()
                        * getImageSizeY()
                        * getNumChannelsPerPixel()
                        * getNumBytesPerChannel());
        imgData.order(ByteOrder.nativeOrder());
        filterLabelProbArray = new float[FILTER_STAGES][getNumLabels()];
    }

    public ClassifyInfo[] classifyBitmap(Bitmap bitmap) {
        convertBitmapToByteBuffer(bitmap);
        // Here's where the magic happens!!!
        long startTime = SystemClock.uptimeMillis();
        runInference();
        long endTime = SystemClock.uptimeMillis();
        Log.d(TAG, "Timecost to run model inference: " + Long.toString(endTime - startTime));
        ClassifyInfo[] result = getResult();
        //暂时取消滤波
//        applyFilter();
        return result;
    }

    /**
     * Closes tflite to release resources.
     */
    public void close() {
        tflite.close();
        tflite = null;
    }

    public void setUseNNAPI(boolean useNNAPI) {
        if (tflite != null) {
            tflite.setUseNNAPI(useNNAPI);
        }
    }

    public void setNumThreads(int numThreads) {
        if (tflite != null) {
            tflite.setNumThreads(numThreads);
        }
    }

    private void convertBitmapToByteBuffer(Bitmap bitmap) {
        if (imgData == null) {
            return;
        }
        imgData.rewind();
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        // Convert the image to floating point.
        int pixel = 0;
        long startTime = SystemClock.uptimeMillis();
        for (int i = 0; i < getImageSizeX(); ++i) {
            for (int j = 0; j < getImageSizeY(); ++j) {
                final int val = intValues[pixel++];
                addPixelValue(val);
            }
        }
        long endTime = SystemClock.uptimeMillis();
        Log.d(TAG, "Timecost to put values into ByteBuffer: " + Long.toString(endTime - startTime));
    }

    private void runInference() {
        tflite.run(imgData, getOutputBuffer());
    }

    /**
     * Reads label list from Assets.
     */
    private List<String> loadLabelList() throws IOException {
        List<String> labelList = new ArrayList<String>();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(context.getAssets().open(getLabelPath())));
        String line;
        while ((line = reader.readLine()) != null) {
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }

    /**
     * Memory-map the model file in Assets.
     */
    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(getModelPath());
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    protected int getNumLabels() {
        return labelList.size();
    }

    private ClassifyInfo[] getResult() {
        int numLabels = getNumLabels();
        ClassifyInfo[] result = new ClassifyInfo[numLabels];
        for (int i = 0; i < numLabels; i++) {
            result[i] = new ClassifyInfo(labelList.get(i), i, getNormalizedProbability(i));
        }
        Arrays.sort(result, new Comparator<ClassifyInfo>() {
            @Override
            public int compare(ClassifyInfo t1, ClassifyInfo t2) {
                if (t1.getProbability() > t2.getProbability()) {
                    return -1;
                } else if (t1.getProbability() < t2.getProbability()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        return result;
    }

    private void applyFilter() {
        int numLabels = getNumLabels();
        // Low pass filter `labelProbArray` into the first stage of the filter.
        for (int j = 0; j < numLabels; ++j) {
            filterLabelProbArray[0][j] +=
                    FILTER_FACTOR * (getProbability(j) - filterLabelProbArray[0][j]);
        }
        // Low pass filter each stage into the next.
        for (int i = 1; i < FILTER_STAGES; ++i) {
            for (int j = 0; j < numLabels; ++j) {
                filterLabelProbArray[i][j] +=
                        FILTER_FACTOR * (filterLabelProbArray[i - 1][j] - filterLabelProbArray[i][j]);
            }
        }
        // Copy the last stage filter output back to `labelProbArray`.
        for (int j = 0; j < numLabels; ++j) {
            setProbability(j, filterLabelProbArray[FILTER_STAGES - 1][j]);
        }
    }

    protected abstract String getModelPath();

    protected abstract String getLabelPath();

    protected abstract int getImageSizeX();

    protected abstract int getImageSizeY();

    protected abstract int getNumBytesPerChannel();

    protected abstract int getNumChannelsPerPixel();

    protected abstract void addPixelValue(int pixelValue);

    protected abstract float getProbability(int labelIndex);

    protected abstract void setProbability(int labelIndex, Number value);

    protected abstract float getNormalizedProbability(int labelIndex);

    protected abstract Object getOutputBuffer();
}
