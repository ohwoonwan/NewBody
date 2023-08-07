package com.example.newbody;

import android.annotation.SuppressLint;
import android.os.Bundle;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Calendar extends Fragment {
    private View view;
    private String readDay = null;
    private String str = null;
    private CalendarView calendarView;
    private Button cha_Btn, del_Btn, save_Btn;
    private TextView diaryTextView, textView2, textView3;
    private EditText contextEditText;

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

        // Firebase 데이터베이스 참조를 초기화합니다.
        databaseReference = FirebaseDatabase.getInstance().getReference();

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
                diaryTextView.setText(String.format("%d / %d / %d", year, month + 1, dayOfMonth));
                contextEditText.setText("");
                checkDay(year, month, dayOfMonth);
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
                contextEditText.setVisibility(View.INVISIBLE);
                textView2.setVisibility(View.VISIBLE);
            }
        });
        return view;
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

                    // 저장 버튼을 보여주고 수정, 삭제 버튼을 숨깁니다.
                    save_Btn.setVisibility(View.INVISIBLE);
                    cha_Btn.setVisibility(View.VISIBLE);
                    del_Btn.setVisibility(View.VISIBLE);
                } else {
                    // Diary entry does not exist for the selected date
                    textView2.setVisibility(View.INVISIBLE);

                    diaryTextView.setVisibility(View.VISIBLE);
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