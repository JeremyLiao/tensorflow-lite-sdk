package com.jeremyliao.tflite_sdk;

import android.content.Context;
import android.content.res.AssetFileDescriptor;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by liaohailiang on 2018/8/4.
 */
public abstract class AbstractTFLiteProcessor {

    protected ByteBuffer inputData = null;
    protected Interpreter tflite;
    private Context context;

    public AbstractTFLiteProcessor(Context context) throws IOException {
        this.context = context;
        tflite = new Interpreter(loadModelFile());
        inputData = ByteBuffer.allocateDirect(
                getInputNum() * getInputByteNumPerChannel());
        inputData.order(ByteOrder.nativeOrder());
    }

    public void close() {
        tflite.close();
        tflite = null;
    }

    public Object run(InputDataFactory factory) {
        if (factory == null) {
            return null;
        }
        inputData.rewind();
        factory.input(inputData);
        tflite.run(inputData, getOutputBuffer());
        return getOutputBuffer();
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

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(getModelPath());
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    protected abstract int getInputNum();

    protected abstract int getInputByteNumPerChannel();

    protected abstract Object getOutputBuffer();

    protected abstract String getModelPath();

    public interface InputDataFactory {
        void input(ByteBuffer buffer);
    }
}
