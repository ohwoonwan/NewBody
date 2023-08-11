package com.example.newbody;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class Calendar extends Fragment {
    private View view, mic_button;
    private String readDay = null;
    private String str = null;
    private CalendarView calendarView;
    private Button cha_Btn, del_Btn, save_Btn;
    private TextView diaryTextView, textView2, textView3;
    private EditText contextEditText;

    private FirebaseAuth auth;

    private FirebaseUser user;
    private DatabaseReference databaseReference;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_calendar, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        diaryTextView = view.findViewById(R.id.diaryTextView);
        save_Btn = view.findViewById(R.id.save_Btn);
        del_Btn = view.findViewById(R.id.del_Btn);
        cha_Btn = view.findViewById(R.id.cha_Btn);
        textView2 = view.findViewById(R.id.textView2);
        contextEditText = view.findViewById(R.id.contextEditText);
        mic_button = view.findViewById(R.id.mic_button);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        // Firebase 데이터베이스 참조를 초기화합니다.
        databaseReference = FirebaseDatabase.getInstance().getReference("Calender").child(user.getUid());

        // Firebase에 접근하기위한 객체 초기화
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                diaryTextView.setVisibility(View.VISIBLE);
                save_Btn.setVisibility(View.VISIBLE);
                contextEditText.setVisibility(View.VISIBLE);
                textView2.setVisibility(View.INVISIBLE);
                cha_Btn.setVisibility(View.INVISIBLE);
                del_Btn.setVisibility(View.INVISIBLE);
                mic_button.setVisibility(View.VISIBLE);
                diaryTextView.setText(String.format("%d / %d / %d", year, month + 1, dayOfMonth));
                contextEditText.setText("");
                checkDay(year, month, dayOfMonth);
            }
        });

        mic_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VoiceTask voiceTask = new VoiceTask();
                voiceTask.execute();
            }
        });

        save_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDiary(readDay);
                str = contextEditText.getText().toString();
                textView2.setText(str);
                save_Btn.setVisibility(View.INVISIBLE);
                cha_Btn.setVisibility(View.VISIBLE);
                del_Btn.setVisibility(View.VISIBLE);
                mic_button.setVisibility(View.INVISIBLE);
                contextEditText.setVisibility(View.INVISIBLE);
                textView2.setVisibility(View.VISIBLE);
            }
        });
        return view;
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
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 10000);  // 10초
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 5000);  // 5초
        startActivityForResult(intent, 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String str = results.get(0);
            contextEditText.setText(str);
        }
    }
    public void checkDay(int cYear, int cMonth, int cDay) {
        readDay = "" + cYear + "-" + (cMonth + 1) + "" + "-" + cDay;
        DatabaseReference dateRef = databaseReference.child(readDay);

        dateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Diary entry exists for the selected date
                    String content = snapshot.getValue(String.class);
                    textView2.setText(content);

                    // 데이터가 있으므로 textView2를 보여주고 contextEditText를 숨깁니다.
                    textView2.setVisibility(View.VISIBLE);
                    contextEditText.setVisibility(View.INVISIBLE);
                    mic_button.setVisibility(View.INVISIBLE);

                    // 저장 버튼을 보여주고 수정, 삭제 버튼을 숨깁니다.
                    save_Btn.setVisibility(View.INVISIBLE);
                    cha_Btn.setVisibility(View.VISIBLE);
                    del_Btn.setVisibility(View.VISIBLE);
                } else {
                    // Diary entry does not exist for the selected date
                    textView2.setVisibility(View.INVISIBLE);

                    diaryTextView.setVisibility(View.VISIBLE);
                    mic_button.setVisibility(View.VISIBLE);
                    save_Btn.setVisibility(View.VISIBLE);
                    cha_Btn.setVisibility(View.INVISIBLE);
                    del_Btn.setVisibility(View.INVISIBLE);
                    contextEditText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Error occurred while fetching diary entry
                error.toException().printStackTrace();
            }
        });



        cha_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contextEditText.setVisibility(View.VISIBLE);
                textView2.setVisibility(View.INVISIBLE);
                contextEditText.setText(str);

                save_Btn.setVisibility(View.VISIBLE);
                cha_Btn.setVisibility(View.INVISIBLE);
                del_Btn.setVisibility(View.INVISIBLE);
                textView2.setText(contextEditText.getText());
            }
        });

        del_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView2.setVisibility(View.INVISIBLE);
                contextEditText.setText("");
                contextEditText.setVisibility(View.VISIBLE);
                save_Btn.setVisibility(View.VISIBLE);
                cha_Btn.setVisibility(View.INVISIBLE);
                del_Btn.setVisibility(View.INVISIBLE);

                // Remove the diary entry from Firebase Realtime Database
                dateRef.removeValue()
                        .addOnSuccessListener(aVoid -> {
                            // Diary entry removed successfully
                        })
                        .addOnFailureListener(e -> {
                            // Error occurred while removing diary entry
                            e.printStackTrace();
                        });
            }
        });
    }

    @SuppressLint("WrongConstant")
    public void saveDiary(String readDay) {
        // Get the content from the EditText
        String content = contextEditText.getText().toString();

        DatabaseReference dateRef = databaseReference.child(readDay);

        // Save the diary entry to Firebase Realtime Database
        dateRef.setValue(content)
                .addOnSuccessListener(aVoid -> {
                    // Diary entry saved successfully
                })
                .addOnFailureListener(e -> {
                    // Error occurred while saving diary entry
                    e.printStackTrace();
                });
    }

}

