package com.example.newbody;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

import java.util.ArrayList;
import java.util.Locale;

public class Home extends Fragment {
    private View view;
    private View posture_button, video_button, record_button, ranking_button;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;
    TextView name, bmi, bmiResult;

    protected SpeechRecognizer mSpeechRecognizer;
    protected Intent mRecognizerIntent;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int resultCode = intent.getIntExtra("resultCode", Activity.RESULT_CANCELED);
            if (resultCode == 1) {
                VoiceTask voiceTask = new VoiceTask();
                voiceTask.execute();
            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_home, container, false);

        Intent intentV = new Intent(getActivity(), VoiceRecognitionService.class);
        getActivity().startService(intentV);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        posture_button = view.findViewById(R.id.posture_button);
        video_button = view.findViewById(R.id.video_button);
        record_button = view.findViewById(R.id.record_button);
        ranking_button = view.findViewById(R.id.ranking_button);

        name = view.findViewById(R.id.name_info);
        bmi = view.findViewById(R.id.bmi_num);
        bmiResult = view.findViewById(R.id.bmi_result);

        if(user == null){
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }else{
            db.collection("users").document(user.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    int weight = Integer.parseInt(document.getString("weight"));
                                    int height = Integer.parseInt(document.getString("height"));

                                    double bmiNum = (double)weight/(((double)height/100)*((double)height/100));
                                    bmi.setText(String.format("%.2f", bmiNum));
                                    if(bmiNum < 18.5){
                                        bmiResult.setText("당신의 BMI수치는 저체중에 해당합니다. ");
                                    }else if(bmiNum >= 18.5 && bmiNum < 25){
                                        bmiResult.setText("당신의 BMI수치는 정상입니다. ");
                                    }else if(bmiNum >= 25 && bmiNum < 30){
                                        bmiResult.setText("당신의 BMI수치는 과체중에 해당합니다. ");
                                    }else if(bmiNum >= 30){
                                        bmiResult.setText("당신의 BMI수치는 비만에 해당합니다. ");
                                    }
                                    name.setText(document.getString("name"));
                                } else {
                                }
                            } else {
                                // handle the failure case
                            }
                        }
                    });
        }

        posture_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Posture.class);
                startActivity(intent);
            }
        });
        video_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Video.class);
                startActivity(intent);
            }
        });
        record_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Record.class);
                startActivity(intent);
            }
        });
        ranking_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Ranking.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 권한 체크 및 요청
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 100);
        } else {
            startVoiceRecognition();
        }
    }

    private void startVoiceRecognition() {
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireActivity());
        mRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, Locale.getDefault());
        mRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, requireActivity().getPackageName());
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
                    if (matches.contains("시리야")) {
                        VoiceTask voiceTask = new VoiceTask();
                        voiceTask.execute();
                        return;
                    }
                }
                // Restart listening after processing results
                mSpeechRecognizer.startListening(mRecognizerIntent);
            }

            @Override
            public void onPartialResults(Bundle partialResults) {}

            @Override
            public void onEvent(int eventType, Bundle params) {}
        });

        mSpeechRecognizer.startListening(mRecognizerIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startVoiceRecognition();
        } else {
            Toast.makeText(requireContext(), "음성 인식에 필요한 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 1) {
            VoiceTask voiceTask = new VoiceTask();
            voiceTask.execute();
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String str = results.get(0);
            if(str.equals("자세교정") || str.equals("자세 교정")){
                Intent intent = new Intent(getActivity(), Posture.class);
                startActivity(intent);
            }else if(str.equals("홈트레이닝") || str.equals("홈 트레이닝")){
                Intent intent = new Intent(getActivity(), Video.class);
                startActivity(intent);
            }else if(str.equals("기록측정") || str.equals("기록 측정")){
                Intent intent = new Intent(getActivity(), Record.class);
                startActivity(intent);
            }else if(str.equals("랭킹") || str.equals("순위")){
                Intent intent = new Intent(getActivity(), Ranking.class);
                startActivity(intent);
            }
        }
    }

    public class VoiceTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            getVoice();
        }
    }

    private void getVoice() {
        Intent intent = new Intent();
        intent.setAction(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        String language = "ko-KR";
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        startActivityForResult(intent, 2);
    }

    @Override
    public void onResume() {
        super.onResume();
        // 브로드캐스트 리시버 등록
        requireActivity().registerReceiver(receiver, new IntentFilter("com.example.newbody.RESULT_ACTION"));
    }

    @Override
    public void onPause() {
        super.onPause();
        // 브로드캐스트 리시버 등록 해제
        requireActivity().unregisterReceiver(receiver);
    }
}
