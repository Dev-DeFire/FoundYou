package com.nishantnitinmishra.foundyou;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.media.FaceDetector;

import androidx.annotation.NonNull;

import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MLModel {
    public Interpreter tenserflowLite(Activity activity,@NonNull String modelFile){

        //Load model
        try {
            return new Interpreter(loadModelFile(activity,modelFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public MappedByteBuffer loadModelFile(Activity activity, String MODEL_FILE) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_FILE);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
}
