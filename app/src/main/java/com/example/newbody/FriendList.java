package com.example.newbody;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newbody.FriendData;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FriendList extends AppCompatActivity {

    TextView textView;
    private FirebaseFirestore firestore;
    String searchOption = "name";

    private Button searchBtn;
    private EditText searchEditText;

    private RecyclerViewAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        searchBtn = findViewById(R.id.searchBtn);
        searchEditText = findViewById(R.id.searchWord);
        textView = findViewById(R.id.textView);

        // 파이어스토어 인스턴스 초기화
        firestore = FirebaseFirestore.getInstance();

        recyclerViewAdapter = new RecyclerViewAdapter();
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerViewAdapter);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchOption = "name";
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchWord = searchEditText.getText().toString();
                recyclerViewAdapter.performSearch(searchWord, searchOption);
            }
        });
    }
}
