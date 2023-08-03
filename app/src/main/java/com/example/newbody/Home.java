package com.example.newbody;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Home extends Fragment {
    private View view;
    private View posture_button, video_button, record_button, ranking_button;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_home, container, false);

        posture_button = view.findViewById(R.id.posture_button);
        video_button = view.findViewById(R.id.video_button);
        record_button = view.findViewById(R.id.record_button);
        ranking_button = view.findViewById(R.id.ranking_button);

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
