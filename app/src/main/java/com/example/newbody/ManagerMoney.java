package com.example.newbody;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

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

public class ManagerMoney extends AppCompatActivity {
    // implements OnDateSetListener

    private Button exerciseRange, graphView, prev, selectCalendar;
    private String select_range;
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
        setContentView(R.layout.activity_manager_money);

        initView();

//        exerciseRange.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showRangeDialog();
//            }
//        });
//
//        graphView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(exerciseRange.getText().equals("범위")){
//                    Toast.makeText(ManagerMoney.this, "범위를 선택해주세요", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if(exerciseRange.getText().equals("월별 통계")){
//                    int yearNum = Integer.parseInt(yy.format(date));
//                    int mmNum = Integer.parseInt(mm.format(date));
//                    dateView.setVisibility(View.VISIBLE);
//                    chart.setVisibility(View.GONE);
//                    chart2.setVisibility(View.VISIBLE);
//                    dateSelect.setText(yearNum + "년 " + mmNum + "월 ");
//                    loadMonthlyData(yearNum, mmNum);
//                }else if(exerciseRange.getText().equals("최근 7일")){
//                    chart.setVisibility(View.VISIBLE);
//                    chart2.setVisibility(View.GONE);
//                    dateView.setVisibility(View.INVISIBLE);
//                    loadLast7DaysSquatData();
//                }
//            }
//        });
//
//        selectCalendar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showDatePickerDialog(view);
//            }
//        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ManagerMenu.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public void initView(){
        prev = findViewById(R.id.prevButtonMoneyManage);
        exerciseRange = findViewById(R.id.graphRange2);
        graphView = findViewById(R.id.graph_money_button);
        selectCalendar = findViewById(R.id.selectDate1);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        chart = findViewById(R.id.chart11);
        chart2 = findViewById(R.id.chart22);
        dateSelect = findViewById(R.id.calendarText1);
        dateView = findViewById(R.id.calendarSelect1);
        date = new Date();
        yy = new SimpleDateFormat("yyyy");
        mm = new SimpleDateFormat("MM");
    }

//    @Override
//    public void onDateSet(int year, int month) {
//        dateSelect.setText(year + "년 " + (month + 1) + "월");
//    }
//
//    private void showRangeDialog() {
//        final String[] exOptions = {"월별 통계", "최근 7일"};
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("범위 선택");
//        builder.setItems(exOptions, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                select_range = exOptions[which];
//                exerciseRange.setText(select_range);
//            }
//        });
//        builder.show();
//    }
//
//    private void loadLast7DaysSquatData() {
//        final List<String> last7Days = new ArrayList<>();
//        DateFormat dateFormat = new SimpleDateFormat("MMdd");
//        java.util.Calendar cal = java.util.Calendar.getInstance();
//        for (int i = 0; i < 7; i++) {
//            last7Days.add(dateFormat.format(cal.getTime()));
//            cal.add(java.util.Calendar.DAY_OF_MONTH, -1);
//        }
//
//        Collections.reverse(last7Days);
//        List<Entry> entries = new ArrayList<>();
//
//        for (int i = 0; i < 7; i++) {
//            final String date = last7Days.get(i);
//            DocumentReference docRef = db.collection("payments").document("2023" + date);
//            final int finalI = i;
//            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    if (task.isSuccessful()) {
//                        DocumentSnapshot document = task.getResult();
//                        if (document.exists()) {
//                            long amount = document.getLong("amount");
//                            entries.add(new Entry(finalI, amount));
//                        } else {
//                            entries.add(new Entry(finalI, 0));
//                        }
//                        if (finalI == last7Days.size() - 1) {
//                            drawGraph(entries, last7Days);
//                        }
//                    }
//                }
//            });
//        }
//
//    }
//
//    private void loadMonthlyData(int year, int month) {
//
//        // 해당 연도와 달에 해당하는 날짜 범위를 설정합니다.
//        int lastDay = java.util.Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
//        List<Entry> entries = new ArrayList<>();
//        for (int day = 1; day <= lastDay; day++) {
//            final int index = day - 1;
//            final String date = String.format("%04d%02d%02d", year, month, day);
//            DocumentReference docRef = db.collection("payments").document(date);
//            final int finalI = day;
//            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    if (task.isSuccessful()) {
//                        DocumentSnapshot document = task.getResult();
//                        if (document.exists()) {
//                            long amount = document.getLong("amount");
//                            entries.add(new Entry(finalI, amount));
//                        } else {
//                            entries.add(new Entry(finalI, 0));
//                        }
//                        if (finalI == lastDay - 1) {
//                            drawMonthGraph(entries, year, month, lastDay);
//                        }
//                    }
//                }
//            });
//        }
//    }
//
//    private void drawGraph(List<Entry> entries, final List<String> last7Days) {
//        LineDataSet dataSet = new LineDataSet(entries, exerciseRange.getText() + " 그래프");
//
//        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
//        dataSet.setLineWidth(2f);
//        dataSet.setColor(Color.parseColor("#9DCEFF"));
//        dataSet.setCircleColor(Color.parseColor("#9DCEFF"));
//
//        LineData lineData = new LineData(dataSet);
//
//        chart.getXAxis().setValueFormatter(new ValueFormatter() {
//            @Override
//            public String getFormattedValue(float value) {
//                if (value >= 0 && value < last7Days.size()) {
//                    return last7Days.get((int) value);
//                }
//                return "";
//            }
//        });
//
//        chart.getAxisRight().setEnabled(false);
//
//        chart.setData(lineData);
//        chart.invalidate();
//    }
//
//    private void drawMonthGraph(List<Entry> entries, int year, int month, int lastDay) {
//        LineDataSet dataSet = new LineDataSet(entries, exerciseRange.getText() + " 그래프");
//
//        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
//        dataSet.setLineWidth(2f);
//        dataSet.setColor(Color.parseColor("#9DCEFF"));
//        dataSet.setCircleColor(Color.parseColor("#9DCEFF"));
//
//        LineData lineData = new LineData(dataSet);
//
//        chart2.getXAxis().setValueFormatter(new ValueFormatter() {
//            @Override
//            public String getFormattedValue(float value) {
//                if (value >= 0 && value < lastDay) {
//                    return String.format("%02d", (int)value + 1);
//                }
//                return "";
//            }
//        });
//
//        chart2.getAxisRight().setEnabled(false);
//
//        chart2.setData(lineData);
//        chart2.invalidate();
//
//        // 한 화면에 7개의 데이터 포인트만 보이도록 설정
//        chart2.setVisibleXRangeMaximum(7);
//        // 최초로 마지막 7개의 데이터 포인트를 보여줌
//        chart2.moveViewToX(entries.size() - 7);
//    }
//
//    public void showDatePickerDialog(View v) {
//        Graph.YearMonthPickerDialog dialog = new Graph.YearMonthPickerDialog();
//        dialog.setListener(new OnDateSetListener() {
//            @Override
//            public void onDateSet(int year, int month) {
//                // 여기에 로직 추가
//                dateSelect.setText(year + "년 " + month + "월");
//                loadMonthlyData(year, month);
//            }
//        });
//        dialog.show(getSupportFragmentManager(), "YearMonthPickerDialog");
//
//    }
//
//
//    public static class YearMonthPickerDialog extends DialogFragment {
//        private static final int MAX_YEAR = 2099;
//        private static final int MIN_YEAR = 2000;
//
//        private OnDateSetListener listener;
//
//        public void setListener(OnDateSetListener listener) {
//            this.listener = listener;
//        }
//
//        @NonNull
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//            LayoutInflater inflater = getActivity().getLayoutInflater();
//
//            View dialog = inflater.inflate(R.layout.year_month_picker, null);
//            final NumberPicker monthPicker = dialog.findViewById(R.id.monthPicker);
//            final NumberPicker yearPicker = dialog.findViewById(R.id.yearPicker);
//
//            monthPicker.setMinValue(1);
//            monthPicker.setMaxValue(12);
//            monthPicker.setValue(1);
//
//            int year = Calendar.getInstance().get(Calendar.YEAR);
//            yearPicker.setMinValue(MIN_YEAR);
//            yearPicker.setMaxValue(MAX_YEAR);
//            yearPicker.setValue(year);
//
//            builder.setView(dialog)
//                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            if (listener != null) {
//                                listener.onDateSet(yearPicker.getValue(), monthPicker.getValue());
//                            }
//                        }
//                    })
//                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            YearMonthPickerDialog.this.getDialog().cancel();
//                        }
//                    });
//            return builder.create();
//        }
//    }
}