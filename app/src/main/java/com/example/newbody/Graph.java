package com.example.newbody;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Graph extends AppCompatActivity implements OnDateSetListener {

    private Button exerciseGraph, exerciseRange, graphView, prev, selectCalendar;
    private Chart chartView, chartView2;
    private String select_ex, select_range;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private LineChart chart, chart2;
    private TextView dateSelect;
    private View dateView;
    SimpleDateFormat yy, mm;
    Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        Intent intent = new Intent(this, VoiceRecognitionService.class);
        startService(intent);

        chart = findViewById(R.id.chart);
        chart2 = findViewById(R.id.chart2);
        exerciseGraph = findViewById(R.id.exerciseGraph);
        exerciseRange = findViewById(R.id.graphRange);
        selectCalendar = findViewById(R.id.selectDate);
        dateSelect = findViewById(R.id.calendarText);
        dateView = findViewById(R.id.calendarSelect);
        graphView = findViewById(R.id.graph_info_button);
        chartView = findViewById(R.id.chart);
        prev = findViewById(R.id.prevButtonGraph);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        date = new Date();
        yy = new SimpleDateFormat("yyyy");
        mm = new SimpleDateFormat("MM");

        exerciseRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRangeDialog();
            }
        });

        exerciseGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {showExerciseDialog();}
        });

        graphView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(exerciseRange.getText().equals("범위")){
                    Toast.makeText(Graph.this, "범위를 선택해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(exerciseGraph.getText().equals("운동")){
                    Toast.makeText(Graph.this, "운동을 선택해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(exerciseRange.getText().equals("월별 통계")){
                    int yearNum = Integer.parseInt(yy.format(date));
                    int mmNum = Integer.parseInt(mm.format(date));
                    dateView.setVisibility(View.VISIBLE);
                    chart.setVisibility(View.GONE);
                    chart2.setVisibility(View.VISIBLE);
                    dateSelect.setText(yearNum + "년 " + mmNum + "월 ");
                    loadMonthlyData(yearNum, mmNum);
                }else if(exerciseRange.getText().equals("최근 7일")){
                    chart.setVisibility(View.VISIBLE);
                    chart2.setVisibility(View.GONE);
                    dateView.setVisibility(View.INVISIBLE);
                    loadLast7DaysSquatData();
                }
            }
        });

        selectCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Menu.class);
                intent.putExtra("SELECTED_FRAGMENT_INDEX", 3);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onDateSet(int year, int month) {
        dateSelect.setText(year + "년 " + (month + 1) + "월");
    }

    private void showRangeDialog() {
        final String[] exOptions = {"월별 통계", "최근 7일"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("범위 선택");
        builder.setItems(exOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                select_range = exOptions[which];
                exerciseRange.setText(select_range);
            }
        });
        builder.show();
    }

    private void showExerciseDialog() {
        final String[] exOptions = {"스쿼트", "푸쉬업", "덤벨 숄더 프레스", "사이드 레터럴 레이즈", "레그 레이즈"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("운동 선택");
        builder.setItems(exOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                select_ex = exOptions[which];
                exerciseGraph.setText(select_ex);
            }
        });
        builder.show();
    }

    private void loadLast7DaysSquatData() {
        final List<String> last7Days = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("MMdd");
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < 7; i++) {
            last7Days.add(dateFormat.format(cal.getTime()));
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }

        Collections.reverse(last7Days);

        String collectionName = null;
        String exercise = null;

        if (exerciseGraph.getText().equals("스쿼트")) {
            collectionName = "dailySquatRecords";
            exercise = "squatCount";
        } else if (exerciseGraph.getText().equals("푸쉬업")) {
            collectionName = "dailyPushupRecords";
            exercise = "pushupCount";
        } else if (exerciseGraph.getText().equals("덤벨 숄더 프레스")) {
            collectionName = "dailyDumbbellRecords";
            exercise = "dumbbellCount";
        } else if (exerciseGraph.getText().equals("사이드 레터럴 레이즈")) {
            collectionName = "dailySideRecords";
            exercise = "sideCount";
        } else if (exerciseGraph.getText().equals("레그 레이즈")) {
            collectionName = "dailyLegRecords";
            exercise = "legCount";
        }

        DocumentReference userRecordRef = db.collection(collectionName).document(user.getUid());

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < last7Days.size(); i++) {
            final int index = i;
            final String date = last7Days.get(i);

            String finalExercise = exercise;
            userRecordRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Long exerciseCount = document.getLong("2023"+date + finalExercise);
                        if (exerciseCount == null) {
                            exerciseCount = 0L;
                        }
                        entries.add(new Entry(index, exerciseCount));
                        if (index == last7Days.size() - 1) {
                            drawGraph(entries, last7Days);
                        }
                    } else {
                        Log.d("Firestore", "Failed to get document", task.getException());
                    }
                }
            });
        }
    }

    private void loadMonthlyData(int year, int month) {
        String collectionName = null;
        String exercise = null;

        if (exerciseGraph.getText().equals("스쿼트")) {
            collectionName = "dailySquatRecords";
            exercise = "squatCount";
        } else if (exerciseGraph.getText().equals("푸쉬업")) {
            collectionName = "dailyPushupRecords";
            exercise = "pushupCount";
        } else if (exerciseGraph.getText().equals("덤벨 숄더 프레스")) {
            collectionName = "dailyDumbbellRecords";
            exercise = "dumbbellCount";
        } else if (exerciseGraph.getText().equals("사이드 레터럴 레이즈")) {
            collectionName = "dailySideRecords";
            exercise = "sideCount";
        } else if (exerciseGraph.getText().equals("레그 레이즈")) {
            collectionName = "dailyLegRecords";
            exercise = "legCount";
        }

        DocumentReference userRecordRef = db.collection(collectionName).document(user.getUid());

        // 해당 연도와 달에 해당하는 날짜 범위를 설정합니다.
        int lastDay = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
        List<Entry> entries = new ArrayList<>();
        for (int day = 1; day <= lastDay; day++) {
            final int index = day - 1;
            final String date = String.format("%04d%02d%02d", year, month, day);

            String finalExercise = exercise;
            userRecordRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Long exerciseCount = document.getLong(date + finalExercise);
                        if (exerciseCount == null) {
                            exerciseCount = 0L;
                        }
                        entries.add(new Entry(index, exerciseCount));
                        if (index == lastDay - 1) {
                            drawMonthGraph(entries, year, month, lastDay);
                        }
                    } else {
                        Log.d("Firestore", "Failed to get document", task.getException());
                    }
                }
            });
        }
    }

    private void drawGraph(List<Entry> entries, final List<String> last7Days) {
        LineDataSet dataSet = new LineDataSet(entries, exerciseGraph.getText() + " 그래프");

        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setLineWidth(2f);
        dataSet.setColor(Color.parseColor("#9DCEFF"));
        dataSet.setCircleColor(Color.parseColor("#9DCEFF"));

        LineData lineData = new LineData(dataSet);

        chart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value >= 0 && value < last7Days.size()) {
                    return last7Days.get((int) value);
                }
                return "";
            }
        });

        chart.getAxisRight().setEnabled(false);

        chart.setData(lineData);
        chart.invalidate();
    }

    private void drawMonthGraph(List<Entry> entries, int year, int month, int lastDay) {
        LineDataSet dataSet = new LineDataSet(entries, exerciseGraph.getText() + " 그래프");

        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setLineWidth(2f);
        dataSet.setColor(Color.parseColor("#9DCEFF"));
        dataSet.setCircleColor(Color.parseColor("#9DCEFF"));

        LineData lineData = new LineData(dataSet);

        chart2.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value >= 0 && value < lastDay) {
                    return String.format("%02d", (int)value + 1);
                }
                return "";
            }
        });

        chart2.getAxisRight().setEnabled(false);

        chart2.setData(lineData);
        chart2.invalidate();

        // 한 화면에 7개의 데이터 포인트만 보이도록 설정
        chart2.setVisibleXRangeMaximum(7);
        // 최초로 마지막 7개의 데이터 포인트를 보여줌
        chart2.moveViewToX(entries.size() - 7);
    }



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
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String str = results.get(0);
            if(str.equals("스쿼트") || str.equals("푸쉬업") || str.equals("푸시업") || str.equals("덤벨 숄더 프레스") || str.equals("덤벨") || str.equals("덤벨숄더프레스") ||
                    str.equals("사이드 레터럴 레이즈") || str.equals("사레레") || str.equals("사이드레터럴레이즈") || str.equals("레그 레이즈") ||
                    str.equals("레그레이즈") || str.equals("준비 운동") || str.equals("준비운동")){
                if(str.equals("스쿼트")){
                    select_ex = "스쿼트";
                    exerciseGraph.setText(select_ex);
                }else if(str.equals("푸쉬업") || str.equals("푸시업")){
                    select_ex = "푸쉬업";
                    exerciseGraph.setText(select_ex);
                }else if(str.equals("덤벨 숄더 프레스") || str.equals("덤벨") || str.equals("덤벨숄더프레스")){
                    select_ex = "덤벨 숄더 프레스";
                    exerciseGraph.setText(select_ex);
                }else if(str.equals("사이드 레터럴 레이즈") || str.equals("사레레") || str.equals("사이드레터럴레이즈")){
                    select_ex = "사이드 레터럴 레이즈";
                    exerciseGraph.setText(select_ex);
                }else if(str.equals("레그 레이즈") || str.equals("레그레이즈")){
                    select_ex = "레그 레이즈";
                    exerciseGraph.setText(select_ex);
                }
            } else if(str.equals("이전")){
                Intent intent = new Intent(getApplicationContext(), Menu.class);
                intent.putExtra("SELECTED_FRAGMENT_INDEX", 3);
                startActivity(intent);
                finish();
            } else if(str.equals("조회") || str.equals("그래프 조회") || str.equals("그래프")){
                if(exerciseGraph.getText().equals("운동")){
                    Toast.makeText(Graph.this, "운동을 선택해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                chart.setVisibility(View.VISIBLE);
                loadLast7DaysSquatData();
            }
        }
    }

    private void restartVoiceRecognitionService() {
        Intent intent = new Intent(this, VoiceRecognitionService.class);
        startService(intent);
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
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 10000);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 3000);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 브로드캐스트 리시버 등록
        registerReceiver(receiver, new IntentFilter("com.example.newbody.RESULT_ACTION"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 브로드캐스트 리시버 등록 해제
        unregisterReceiver(receiver);
    }

    public void showDatePickerDialog(View v) {
        YearMonthPickerDialog dialog = new YearMonthPickerDialog();
        dialog.setListener(new OnDateSetListener() {
            @Override
            public void onDateSet(int year, int month) {
                // 여기에 로직 추가
                dateSelect.setText(year + "년 " + month + "월");
                loadMonthlyData(year, month);
            }
        });
        dialog.show(getSupportFragmentManager(), "YearMonthPickerDialog");

    }


    public static class YearMonthPickerDialog extends DialogFragment {
        private static final int MAX_YEAR = 2099;
        private static final int MIN_YEAR = 2000;

        private OnDateSetListener listener;

        public void setListener(OnDateSetListener listener) {
            this.listener = listener;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();

            View dialog = inflater.inflate(R.layout.year_month_picker, null);
            final NumberPicker monthPicker = dialog.findViewById(R.id.monthPicker);
            final NumberPicker yearPicker = dialog.findViewById(R.id.yearPicker);

            monthPicker.setMinValue(1);
            monthPicker.setMaxValue(12);
            monthPicker.setValue(1);

            int year = Calendar.getInstance().get(Calendar.YEAR);
            yearPicker.setMinValue(MIN_YEAR);
            yearPicker.setMaxValue(MAX_YEAR);
            yearPicker.setValue(year);

            builder.setView(dialog)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (listener != null) {
                                listener.onDateSet(yearPicker.getValue(), monthPicker.getValue());
                            }
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            YearMonthPickerDialog.this.getDialog().cancel();
                        }
                    });
            return builder.create();
        }
    }


}