package com.example.newbody;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class MemberChangeActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageReference storageRef;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private EditText nameEditText, genderEditText, birthEditText, weightEditText, heightEditText;

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_change);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        nameEditText = findViewById(R.id.name_fix);
        genderEditText = findViewById(R.id.gender_fix);
        birthEditText = findViewById(R.id.birth_fix);
        weightEditText = findViewById(R.id.weight_fix);
        heightEditText = findViewById(R.id.height_fix);

        View updateButton = findViewById(R.id.fix_button);
        View imageButton = findViewById(R.id.pic_button);

        storageRef = FirebaseStorage.getInstance().getReference("profile_pics");

        loadImageFromFirestore();

        if(user == null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
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
                                    birthEditText.setText(document.getString("birth"));
                                    genderEditText.setText(document.getString("gender"));
                                    nameEditText.setText(document.getString("name"));
                                    weightEditText.setText(document.getString("weight"));
                                    heightEditText.setText(document.getString("height"));
                                }
                            } else {
                            }
                        }
                    });
        }

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUri != null) {
                    uploadFile();
                }

                String name = nameEditText.getText().toString();
                String gender = genderEditText.getText().toString();
                String birth = birthEditText.getText().toString();
                String weight = weightEditText.getText().toString();
                String height = heightEditText.getText().toString();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(MemberChangeActivity.this, "이름을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(gender)) {
                    Toast.makeText(MemberChangeActivity.this, "성별을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(birth)) {
                    Toast.makeText(MemberChangeActivity.this, "생일을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(weight)) {
                    Toast.makeText(MemberChangeActivity.this, "몸무게를 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(height)) {
                    Toast.makeText(MemberChangeActivity.this, "키를 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> userData = new HashMap<>();
                if (user != null) {
                    userData.put("name", name);
                    userData.put("gender", gender);
                    userData.put("birth", birth);
                    userData.put("weight", weight);
                    userData.put("height", height);
                }

                db.collection("users").document(user.getUid())
                        .update(userData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MemberChangeActivity.this, "Data updated successfully.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), Menu.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MemberChangeActivity.this, "Failed to update data.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            ImageView selectedImageView = findViewById(R.id.profile_pic);
            Glide.with(this).load(imageUri).into(selectedImageView);
        }
    }

    private void uploadFile() {
        final StorageReference oldFileReference = storageRef.child(mAuth.getCurrentUser().getUid() + ".jpg");
        oldFileReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // After deleting the old image, upload the new one
                final StorageReference fileReference = storageRef.child(mAuth.getCurrentUser().getUid() + ".jpg");
                fileReference.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String downloadUrl = uri.toString();
                                        updateImageUrlToFirestore(downloadUrl);
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // If the old image doesn't exist, just upload the new one
                final StorageReference fileReference = storageRef.child(mAuth.getCurrentUser().getUid() + ".jpg");
                fileReference.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String downloadUrl = uri.toString();
                                        updateImageUrlToFirestore(downloadUrl);
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
            }
        });
    }

    private void updateImageUrlToFirestore(String url) {
        Map<String, Object> data = new HashMap<>();
        data.put("imageUrl", url);

        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MemberChangeActivity.this, "Image updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MemberChangeActivity.this, "Failed to update image", Toast.LENGTH_SHORT).show();
                    }
                });
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
        ImageView userImageView = findViewById(R.id.profile_pic);

        Glide.with(this)
                .load(imageUrl)
                .circleCrop()
                .into(userImageView);
    }

}