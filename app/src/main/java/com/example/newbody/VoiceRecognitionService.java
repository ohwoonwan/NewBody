package com.example.newbody;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.Locale;

public class VoiceRecognitionService extends Service {
    protected SpeechRecognizer mSpeechRecognizer;
    protected Intent mRecognizerIntent;

    @Override
    public void onCreate() {
        super.onCreate();

        // Check for permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // Permission handling is more complex in a service, you might want to stop the service if you don't have permissions
            stopSelf();
            return;
        }

        startVoiceRecognition();
    }

    private void startVoiceRecognition() {
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, Locale.getDefault());
        mRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        mRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        mRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {}

            @Override
            public void onBeginningOfSpeech() {}

            @Override
            public void onRmsChanged(float rmsdB) {}

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {}

            @Override
            public void onError(int error) {
                switch (error) {
                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    case SpeechRecognizer.ERROR_NETWORK:
                    case SpeechRecognizer.ERROR_AUDIO:
                    case SpeechRecognizer.ERROR_SERVER:
                    case SpeechRecognizer.ERROR_CLIENT:
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    case SpeechRecognizer.ERROR_NO_MATCH:
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        mSpeechRecognizer.startListening(mRecognizerIntent);
                        break;
                    default:
                        mSpeechRecognizer.cancel();
                        mSpeechRecognizer.startListening(mRecognizerIntent);
                        break;
                }
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && matches.size() > 0) {
                    if (matches.contains("바디야") || matches.contains("뉴바디")) {
                        sendResult(1);
                        Log.d("bodyTest", "body");
                    }
                }
                new android.os.Handler().postDelayed(() -> mSpeechRecognizer.startListening(mRecognizerIntent), 2000);
            }


            @Override
            public void onPartialResults(Bundle partialResults) {}

            @Override
            public void onEvent(int eventType, Bundle params) {}
        });
        mSpeechRecognizer.startListening(mRecognizerIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // For this use-case, we don't need binding.
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.destroy();
        }
    }

    private void sendResult(int resultCode) {
        Intent intent = new Intent("com.example.newbody.RESULT_ACTION");
        intent.putExtra("resultCode", resultCode);
        sendBroadcast(intent);
    }

}

