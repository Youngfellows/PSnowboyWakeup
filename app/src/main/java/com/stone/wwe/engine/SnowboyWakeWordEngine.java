package com.stone.wwe.engine;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ai.kitt.snowboy.SnowboyDetect;

/**
 * Use Snowboy library
 */
public class SnowboyWakeWordEngine extends WakeWordEngine implements Runnable {
    private static final String TAG = SnowboyWakeWordEngine.class.getName();

    private SnowboyDetect mSnowboyDetect;
    private AudioRecord mAudioRecord;
    private byte[] mAudioBuffer;
    private boolean isDetecting = false;

    public SnowboyWakeWordEngine(String resource, String model) {
        super();
        mSnowboyDetect = new SnowboyDetect(resource, model);
        mSnowboyDetect.SetSensitivity("0.6"); // Sensitivity for each hotword
        mSnowboyDetect.SetAudioGain(1.0f); // Audio gain for detection
        mSnowboyDetect.ApplyFrontend(true);
        Log.i(TAG, "NumHotwords = " + mSnowboyDetect.NumHotwords() + ", BitsPerSample = " + mSnowboyDetect.BitsPerSample() + ", NumChannels = " + mSnowboyDetect.NumChannels() + ", SampleRate = " + mSnowboyDetect.SampleRate());

        initAudioRecord();
    }

    @Override
    public void startDetection() {
        new Thread(this).start();
    }

    @Override
    public void stopDetection() {
        isDetecting = false;
    }

    private void initAudioRecord() {
        int buffersize = (int) (16000 * 2 * 0.1);
        Log.d(TAG, "initAudioRecord: buffersize = " + buffersize);
        mAudioBuffer = new byte[buffersize];
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION, 16000,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, mAudioBuffer.length);

        mAudioRecord.getState();
    }

    /**
     * load shared library 'libsnowboy-detect-android.so'
     */
    static {
        System.loadLibrary("snowboy-detect-android");
    }

    @Override
    public void run() {
        isDetecting = true;
        mAudioRecord.startRecording();

        while (isDetecting) {
            mAudioRecord.read(mAudioBuffer, 0, mAudioBuffer.length);
            short[] data = new short[mAudioBuffer.length / 2];
            ByteBuffer.wrap(mAudioBuffer).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(data);

            int detectResult = mSnowboyDetect.RunDetection(data, data.length);

            if (detectResult == -2) {
                this.onNoSpeech();
                Log.d(TAG, "SnowboyWakeWordEngine run: " + detectResult);
            } else if (detectResult == -1) {
                this.onDetectError();
                Log.d(TAG, "SnowboyWakeWordEngine run: " + detectResult);
            } else if (detectResult == 0) {
                this.onSpeeching();
                Log.d(TAG, "SnowboyWakeWordEngine run: " + detectResult);
            } else if (detectResult > 0) {
                Log.w(TAG, "SnowboyWakeWordEngine run: " + detectResult + ",Hotwords detected");
                this.onKeyWordDetect(detectResult);
            }
        }
        mAudioRecord.stop();
    }
}
