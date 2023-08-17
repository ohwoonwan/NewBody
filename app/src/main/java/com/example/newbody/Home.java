package com.example.newbody;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Home extends Fragment {
    private View view;
    private View posture_button, video_button, record_button, ranking_button;
    private PieChart pieChart;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;
    TextView name, bmiResult;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_home, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        posture_button = view.findViewById(R.id.posture_button);
        video_button = view.findViewById(R.id.video_button);
        record_button = view.findViewById(R.id.record_button);
        ranking_button = view.findViewById(R.id.ranking_button);
        pieChart = view.findViewById(R.id.pieChart);

        name = view.findViewById(R.id.name_info);
        bmiResult = view.findViewById(R.id.bmi_result);

        if(user == null){
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(getActivity(), LoadingActivity.class);
            startActivity(intent);

            db.collection("users").document(user.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    int weight = Integer.parseInt(document.getString("weight"));
                                    int height = Integer.parseInt(document.getString("height"));

                                    double bmiNum = (double)weight/(((double)height/100)*((double)height/100));
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

                                    String bmi = String.valueOf(bmiNum);

                                    List<PieEntry> entries = new ArrayList<>();
                                    entries.add(new PieEntry((float) bmiNum, ""));
                                    entries.add(new PieEntry(100-(float)bmiNum, ""));

//                                    Log.d("BmiChart", "BMI: " + bmi);

                                    PieDataSet dataSet = new PieDataSet(entries, "");
                                    dataSet.setColors(Color.parseColor("#C58BF2"), Color.WHITE);
//                                    dataSet.setDrawValues(false);
                                    dataSet.setValueTextColor(Color.WHITE);
                                    dataSet.setValueTextSize(14f);

                                    PieData pieData = new PieData(dataSet);
                                    pieData.setValueFormatter(new PercentFormatter(pieChart));
                                    pieChart.setData(pieData);
                                    pieChart.getDescription().setEnabled(false);
                                    pieChart.setDrawHoleEnabled(false);
                                    pieChart.getLegend().setEnabled(false);
                                    pieChart.invalidate();
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

}
