package com.example.newbody;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.newbody.record.RecordSquatMain;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Home extends Fragment {
    private View view, yoga, yoga_lock;
    private View posture_button, video_button, record_button, ranking_button, yoga_button, yoga_lock_button;
    private PieChart pieChart;
    private Button notice;
    private CustomDialogNotice customDialog;
    private ImageView premium;
    private AdView mAdView;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;
    TextView name, bmiResult;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_home, container, false);

        yoga = view.findViewById(R.id.yoga_layout);
        yoga_lock = view.findViewById(R.id.yoga_lock_layout);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        posture_button = view.findViewById(R.id.posture_button);
        video_button = view.findViewById(R.id.video_button);
        record_button = view.findViewById(R.id.record_button);
        ranking_button = view.findViewById(R.id.ranking_button);
        yoga_button = view.findViewById(R.id.yoga_button);
        yoga_lock_button = view.findViewById(R.id.yoga_lock_button);
        pieChart = view.findViewById(R.id.pieChart);
        notice = view.findViewById(R.id.noticeDialog);
        premium = view.findViewById(R.id.premium_badge);

        name = view.findViewById(R.id.name_info);
        bmiResult = view.findViewById(R.id.bmi_result);
        mAdView = view.findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        premiumCheck();

        if(user == null){
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }else{
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
                                    pieChart.highlightValue(0, 0);
                                } else {
                                }
                            } else {
                                // handle the failure case
                            }
                        }
                    });
        }

        notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog = new CustomDialogNotice(getContext(),
                        "본 앱은 음성 인식을 지원합니다. \n\n앱 내에서 '바디'를 불러보세요 ! \n\n'바디야' 또는 '뉴바디'라고 말하면 \n\n바디가 대답해주고 \n\n" +
                                "명령을 실행해줍니다. " + "\n\n\n" + "예시) 자세 교정, 기록 측정 등");
                customDialog.show();
            }
        });

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

        yoga_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), YogaPosture.class);
                startActivity(intent);
            }
        });

        yoga_lock_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "프리미엄 회원 전용 메뉴입니다", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    public void premiumCheck(){
        if(user == null){
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }else{
            db.collection("users").document(user.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@androidx.annotation.NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String grade = document.getString("grade");
                                    if (grade == null) grade = "일반";

                                    if(grade.equals("프리미엄")){
                                        premium.setVisibility(View.VISIBLE);
                                        yoga.setVisibility(View.VISIBLE);
                                        yoga_lock.setVisibility(View.GONE);
                                    }
                                }
                            } else {
                            }
                        }
                    });
        }
    }

}
