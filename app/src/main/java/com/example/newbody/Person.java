package com.example.newbody;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Person extends Fragment {
    private View view;

    FirebaseAuth auth;
    Button button, infoButton, goalButton, progressButton, friendListButton, friendButton, payButton;
    FirebaseUser user;

    FirebaseFirestore db;
    TextView name, height, weight, age;

    SimpleDateFormat yy, md;
    Date date;
    View fixButton, premiumCheckLayout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_person, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        button = view.findViewById(R.id.logout);
        name = view.findViewById(R.id.user_name);
        height = view.findViewById(R.id.height_data);
        weight = view.findViewById(R.id.weight_data);
        age = view.findViewById(R.id.age_data);
        infoButton = view.findViewById(R.id.info_button);
        goalButton = view.findViewById(R.id.goal_button);
        progressButton = view.findViewById(R.id.progress_button);
        friendListButton = view.findViewById(R.id.friend_list_button);
        friendButton = view.findViewById(R.id.friend_button);
        payButton = view.findViewById(R.id.pay_button);
        premiumCheckLayout = view.findViewById(R.id.premium_check_layout);

        user = auth.getCurrentUser();
        date = new Date();
        yy = new SimpleDateFormat("yyyy");
        md = new SimpleDateFormat("MMdd");

        fixButton = view.findViewById(R.id.fix);

        premiumCheck();

        fixButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MemberChangeActivity.class);
                startActivity(intent);
            }
        });

        db.collection("users").document(user.getUid())
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

        if(user == null){
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }else{
            db.collection("users").document(user.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    int yearNum = Integer.parseInt(yy.format(date));
                                    int mdNum = Integer.parseInt(md.format(date));
                                    String birth = document.getString("birth");
                                    int year = Integer.parseInt(birth.substring(0, 4));
                                    int md = Integer.parseInt(birth.substring(4, 8));

                                    if(md < mdNum){
                                        age.setText(Integer.toString(yearNum-year));
                                    }else{
                                        age.setText(Integer.toString(yearNum-year-1));
                                    }

                                    name.setText(document.getString("name"));
                                    weight.setText(document.getString("weight"));
                                    height.setText(document.getString("height"));
                                } else {
                                    age.setText("0");
                                    name.setText("0");
                                    weight.setText("0");
                                    height.setText("0");
                                }
                            } else {
                                // handle the failure case
                            }
                        }
                    });
        }

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MemberInfo.class);
                startActivity(intent);
            }
        });

        goalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Target.class);
                startActivity(intent);
            }
        });
        progressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Graph.class);
                startActivity(intent);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        friendListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FriendListPlus.class);
                startActivity(intent);
            }
        });

        friendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FriendList.class);
                startActivity(intent);
            }
        });

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PayActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void loadImageIntoImageView(String imageUrl) {
        ImageView userImageView = view.findViewById(R.id.profile_pic); // Replace with your ImageView's ID

        Glide.with(this)
                .load(imageUrl)
                .circleCrop()
                .into(userImageView);
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
                                        premiumCheckLayout.setVisibility(View.GONE);
                                    }
                                }
                            } else {
                            }
                        }
                    });
        }
    }
}
