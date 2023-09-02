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
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
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
import java.util.Locale;

public class ManagerMoney extends AppCompatActivity implements OnDateSetListener{

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

        exerciseRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRangeDialog();
            }
        });

        graphView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(exerciseRange.getText().equals("범위")){
                    Toast.makeText(ManagerMoney.this, "범위를 선택해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(exerciseRange.getText().equals("월별 통계")){
                    int yearNum = Integer.parseInt(yy.format(date));
                    int mmNum = Integer.parseInt(mm.format(date));
                    dateView.setVisibility(View.VISIBLE);
                    chart.setVisibility(View.GONE);
                    chart2.setVisibility(View.VISIBLE);
                    dateSelect.setText(yearNum + "년 " + mmNum + "월 ");
                    fetchMonthlyData(yearNum, mmNum);
                }else if(exerciseRange.getText().equals("최근 7일")){
                    chart.setVisibility(View.VISIBLE);
                    chart2.setVisibility(View.GONE);
                    dateView.setVisibility(View.INVISIBLE);
                    fetchLast7DaysData();
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

    // 새로운 클래스
    public class SalesData {
        public String date;
        public int sales;

        public SalesData(String date, int sales) {
            this.date = date;
            this.sales = sales;
        }
    }

    public void fetchLast7DaysData() {
        final String collectionName = "payments";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        final ArrayList<String> last7Days = new ArrayList<>();
        final ArrayList<SalesData> salesDataList = new ArrayList<>();

        // Generate the last 7 days in yyyyMMdd format
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < 7; i++) {
            last7Days.add(sdf.format(cal.getTime()));
            cal.add(Calendar.DATE, -1);
        }

        for (final String date : last7Days) {
            DocumentReference paymentRecordRef = db.collection(collectionName).document(date);
            paymentRecordRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        int amount = document.exists() ? document.getLong("amount").intValue() : 0;

                        salesDataList.add(new SalesData(date, amount));

                        if (salesDataList.size() == 7) {
                            // 날짜로 정렬
                            Collections.sort(salesDataList, (a, b) -> a.date.compareTo(b.date));

                            // 날짜와 매출 데이터를 분리
                            ArrayList<String> sortedDates = new ArrayList<>();
                            ArrayList<Integer> sortedSales = new ArrayList<>();
                            for (SalesData data : salesDataList) {
                                sortedDates.add(data.date);
                                sortedSales.add(data.sales);
                            }
                            plotGraph(sortedSales, sortedDates);
                        }
                    }
                }
            });
        }
    }

    private void plotGraph(ArrayList<Integer> salesData, ArrayList<String> last7Days) {
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < salesData.size(); i++) {
            entries.add(new Entry(i, salesData.get(i)));
        }

        LineDataSet dataSet = new LineDataSet(entries, " 그래프");

        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setLineWidth(2f);
        dataSet.setColor(Color.parseColor("#9DCEFF"));
        dataSet.setCircleColor(Color.parseColor("#9DCEFF"));

        LineData lineData = new LineData(dataSet);

        chart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value >= 0 && value < last7Days.size()) {
                    return last7Days.get((int) value).substring(4, 8);
                }
                return "";
            }
        });

        chart.getAxisRight().setEnabled(false);

        chart.setData(lineData);
        chart.invalidate();
    }

    // 월별 매출 데이터를 가져오는 메서드
    public void fetchMonthlyData(int year, int month) {
        final String collectionName = "payments";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        final ArrayList<SalesData> salesDataList = new ArrayList<>();
        final ArrayList<String> daysInMonth = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1); // Calendar.MONTH is zero-based
        cal.set(Calendar.DAY_OF_MONTH, 1);

        int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 1; i <= maxDay; i++) {
            cal.set(Calendar.DAY_OF_MONTH, i);
            daysInMonth.add(sdf.format(cal.getTime()));
        }

        for (final String date : daysInMonth) {
            DocumentReference paymentRecordRef = db.collection(collectionName).document(date);
            paymentRecordRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        int amount = document.exists() ? document.getLong("amount").intValue() : 0;
                        salesDataList.add(new SalesData(date, amount));

                        if (salesDataList.size() == maxDay) {
                            Collections.sort(salesDataList, (a, b) -> a.date.compareTo(b.date));
                            ArrayList<String> sortedDates = new ArrayList<>();
                            ArrayList<Integer> sortedSales = new ArrayList<>();
                            for (SalesData data : salesDataList) {
                                sortedDates.add(data.date);
                                sortedSales.add(data.sales);
                            }
                            plotMonthGraph(sortedSales, sortedDates);
                        }
                    }
                }
            });
        }
    }

    private void plotMonthGraph(ArrayList<Integer> salesData, ArrayList<String> last7Days) {
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < salesData.size(); i++) {
            entries.add(new Entry(i, salesData.get(i)));
        }

        LineDataSet dataSet = new LineDataSet(entries, " 그래프");

        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setLineWidth(2f);
        dataSet.setColor(Color.parseColor("#9DCEFF"));
        dataSet.setCircleColor(Color.parseColor("#9DCEFF"));

        LineData lineData = new LineData(dataSet);

        chart2.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value >= 0 && value < last7Days.size()) {
                    return last7Days.get((int) value).substring(4, 8);
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
        chart2.moveViewToX(0);
    }

    public void showDatePickerDialog(View v) {
        Graph.YearMonthPickerDialog dialog = new Graph.YearMonthPickerDialog();
        dialog.setListener(new OnDateSetListener() {
            @Override
            public void onDateSet(int year, int month) {
                // 여기에 로직 추가
                dateSelect.setText(year + "년 " + month + "월");
                fetchMonthlyData(year, month);
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