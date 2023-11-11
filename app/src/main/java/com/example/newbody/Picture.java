package com.example.newbody;

import android.app.Activity;
import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.OutputStream;
import java.util.ArrayList;

public class Picture extends Fragment {
    private static final int PERMISSIONS_REQUEST_CAMERA = 2;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private View view;
    private View camera_button;
    private Bitmap currentImageBitmap;
    private Button save;
    private Uri imageUri;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_picture, container, false);

        camera_button = view.findViewById(R.id.camera_button);
        save = view.findViewById(R.id.saveButton);

        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestCameraPermission();
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentImageBitmap != null) {
                    saveToGallery(currentImageBitmap);
                }
                Toast.makeText(getActivity(), "사진이 저장되었습니다", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    public void takePicture() {
        requestCameraPermission();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_CAMERA);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 카메라 권한이 승인됐을 때 실행할 코드를 여기에 작성합니다.
            } else {
                // 사용자가 권한 요청을 거부했을 때 실행할 코드를 여기에 작성합니다.
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            save.setVisibility(View.VISIBLE);
            Bundle extras = data.getExtras();
            currentImageBitmap = (Bitmap) extras.get("data");

            ImageView imageView = (ImageView) getView().findViewById(R.id.pic_image);
            imageView.setImageBitmap(currentImageBitmap);
        }
    }

    private void saveToGallery(Bitmap bitmap) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        Uri uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream outputStream = getActivity().getContentResolver().openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            OutputStream outputStream = getActivity().getContentResolver().openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();

            // Firebase Storage에 이미지 업로드
            uploadImageToFirebase(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images/" + System.currentTimeMillis() + ".jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // 이미지가 성공적으로 업로드되었을 때
                    // 이미지의 다운로드 URL을 가져와서 저장
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Firebase Realtime Database에 URL 저장
                        saveImageUrlToDatabase(uri.toString());
                    });
                })
                .addOnFailureListener(e -> {
                    // 업로드 실패 시 처리
                    e.printStackTrace();
                });
    }

    private void saveImageUrlToDatabase(String imageUrl) {
        // 이미지 URL을 Firebase Realtime Database에 저장
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("images");
        String key = databaseRef.push().getKey();
        databaseRef.child(key).setValue(imageUrl);
    }
}
