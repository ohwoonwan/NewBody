package com.example.newbody;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.List;

public class Graph extends AppCompatActivity {

    private Button exerciseGraph, graphView, prev;
    private Chart chartView;
    private String select_ex;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        chart = findViewById(R.id.chart);
        exerciseGraph = findViewById(R.id.exerciseGraph);
        graphView = findViewById(R.id.graph_info_button);
        chartView = findViewById(R.id.chart);
        prev = findViewById(R.id.prevButtonGraph);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        exerciseGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {showExerciseDialog();}
        });

        graphView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(exerciseGraph.getText().equals("운동")){
                    Toast.makeText(Graph.this, "운동을 선택해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                chart.setVisibility(View.VISIBLE);
                loadLast7DaysSquatData();
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

    private void drawGraph(List<Entry> entries, final List<String> last7Days) {
        LineDataSet dataSet = new LineDataSet(entries, exerciseGraph.getText() + "그래프");
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


//        chart.getAxisLeft().setDrawGridLines(false);
//        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisRight().setEnabled(false);

        chart.setData(lineData);
        chart.invalidate();
    }
}