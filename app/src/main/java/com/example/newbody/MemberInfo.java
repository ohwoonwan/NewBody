package com.example.newbody;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MemberInfo extends AppCompatActivity {

    Button prev;
    TextView name, gender, birth, weight, height;
    ImageView profile;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Uri imageUri;
    private StorageReference storageRef;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_info);

        initViews();

        storageRef = FirebaseStorage.getInstance().getReference("profile_pics");
        loadImageFromFirestore();

        if(user == null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }else {
            db.collection("users").document(user.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String date = document.getString("birth");
                                    int year = Integer.parseInt(date.substring(0, 4));
                                    int mm = Integer.parseInt(date.substring(4, 6));
                                    int dd = Integer.parseInt(date.substring(6, 8));

                                    birth.setText(year + "년 " + mm + "월 " + dd + "일");
                                    gender.setText(document.getString("gender"));
                                    name.setText(document.getString("name") + "님");
                                    weight.setText(document.getString("weight")+"kg");
                                    height.setText(document.getString("height")+"cm");
                                }
                            } else {
                            }
                        }
                    });
        }

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

    public void initViews(){
        prev = findViewById(R.id.prevButtonInfo);
        name = findViewById(R.id.name_info);
        gender = findViewById(R.id.gender_info);
        birth = findViewById(R.id.birth_info);
        weight = findViewById(R.id.weight_info);
        height = findViewById(R.id.height_info);
        profile = findViewById(R.id.profile_pic_info);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    private void loadImageFromFirestore() {
        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String imageUrl = documentSnapshot.getString("imageUrl");
                            if (!TextUtils.isEmpty(imageUrl)) {
                                loadImageIntoImageView(imageUrl);
                            } else {
                            }
                        } else {
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    private void loadImageIntoImageView(String imageUrl) {

        Glide.with(this)
                .load(imageUrl)
                .circleCrop()
                .into(profile);
    }
}