package com.example.newbody.record;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newbody.CustomDialog;
import com.example.newbody.PoseMatcher;
import com.example.newbody.R;
import com.example.newbody.TargetPose;
import com.example.newbody.TargetShape;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.PoseLandmark;
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.Map;

import android.os.CountDownTimer;

public class RecordSquatMain extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth mAuth;

    private boolean squatStartDetected = false;
    private boolean squatEndDetected = false;
    private long time;
    private int score = 0;
    private TargetPose targetSquatStartSign;
    private TargetPose targetSquatEndSign;
    private CountDownTimer timer;

    private CustomDialog customDialog;

    PreviewView previewView;
    PoseDetector detector;
    ImageView guidelineView;
    ImageCapture imageCapture;

    TextView count, timeEx, countEx;

    Canvas guidelineCanvas;
    Bitmap guidelineBmp, tempBitmap;
    Paint guidePointPaint, guidePaint, transPaint;

    private final int UPDATE_TIME = 40;
    private boolean isFrameBeingTested = false, canvasAlreadyClear = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_squat_main);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        time = intent.getLongExtra("time", 0);

        initTargetPoses();
        initViews();

        startCountdown(5000);
    }

    private void startCountdown(long duration) {
        new CountDownTimer(duration, 1000) {

            public void onTick(long millisUntilFinished) {
                count.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                count.setText("시작!");
                count.setVisibility(View.INVISIBLE);
                startTimer();
                countEx.setText("개수 : " + score);
                checkPermissions();
            }

        }.start();
    }

    private void startTimer() {

        timer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // 남은 시간을 분과 초로 변환하여 표시
                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;
                String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
                timeEx.setText("남은 시간 : " + timeLeftFormatted);
            }

            @Override
            public void onFinish() {
                FirebaseUser user = mAuth.getCurrentUser();
                Map<String, Object> userData = new HashMap<>();
                if (user != null) {
                    final String collectionName;  // 'final' 키워드 추가

                    if (time == 60000) {
                        collectionName = "countSquat1Minute";
                    } else if (time == 120000) {
                        collectionName = "countSquat2Minute";
                    } else if (time == 180000) {
                        collectionName = "countSquat3Minute";
                    } else {
                        collectionName = "";  // 기본값 설정
                    }

                    DocumentReference userRecordRef = db.collection(collectionName).document(user.getUid());
                    userRecordRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Long squatCountLong;
                                    if (time == 60000) {
                                        squatCountLong = document.getLong("countSquat1Minute");
                                    } else if (time == 120000) {
                                        squatCountLong = document.getLong("countSquat2Minute");
                                    } else {
                                        squatCountLong = document.getLong("countSquat3Minute");
                                    }
                                    int existingSquatCount = 0; // 초기 값을 0으로 설정

                                    if (squatCountLong != null) {
                                        existingSquatCount = squatCountLong.intValue();
                                    }

                                    if (existingSquatCount <= score) {
                                        userData.put(collectionName, score);
                                        db.collection(collectionName).document(user.getUid())
                                                .set(userData)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid){
                                                        // 성공적으로 업데이트했을 때의 로직
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@org.checkerframework.checker.nullness.qual.NonNull Exception e) {
                                                        // 업데이트 실패했을 때의 로직
                                                    }
                                                });
                                    } else {
                                        Log.d("Firestore", "User's score is not higher than the existing record.");
                                    }
                                } else {
                                    // 만약 문서가 없다면, 바로 점수를 저장합니다.
                                    userData.put(collectionName, score);
                                    userRecordRef.set(userData)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("Firestore", "Data successfully written!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@org.checkerframework.checker.nullness.qual.NonNull Exception e) {
                                                    Log.w("Firestore", "Error writing document", e);
                                                }
                                            });
                                }
                            } else {
                                Log.d("Firestore", "Failed to get document", task.getException());
                            }
                        }
                    });
                }

                customDialog = new CustomDialog(RecordSquatMain.this
                        ,"시간 : " + (time/60000) + "분 \n기록 : " + score + "개");
                customDialog.show();
            }
        }.start();
    }

    private void loadGuidelines(Bitmap bmp, Pose pose){
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                guidelineBmp = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);
                guidelineCanvas = new Canvas(guidelineBmp);

                if(transPaint == null || guidePaint == null){
                    transPaint = new Paint();
                    transPaint.setColor(Color.TRANSPARENT);
                    transPaint.setStyle(Paint.Style.FILL_AND_STROKE);

                    guidePointPaint = new Paint();
                    guidePointPaint.setColor(Color.RED);
                    guidePointPaint.setStrokeWidth(10f);
                    guidePointPaint.setStrokeCap(Paint.Cap.BUTT);
                    guidePointPaint.setStyle(Paint.Style.FILL_AND_STROKE);

                    guidePaint = new Paint();
                    guidePaint.setColor(Color.WHITE);
                    guidePaint.setStrokeWidth(3f);
                    guidePaint.setStrokeCap(Paint.Cap.BUTT);
                    guidePaint.setStyle(Paint.Style.STROKE);
                }

                // setting everything as transparent
                guidelineCanvas.drawColor(Color.TRANSPARENT);
//                guidelineCanvas.drawRect(0, 0, guidelineBmp.getWidth(), guidelineBmp.getHeight(), transPaint);

                // drawing just a rect
                if(pose != null){
                    for(PoseLandmark landmark : pose.getAllPoseLandmarks()){
                        guidelineCanvas.drawCircle(landmark.getPosition().x, landmark.getPosition().y, 6f, guidePointPaint);
                    }

                    // drawing lines
                    // TORSO
                    guidelineCanvas.drawLine(pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER).getPosition().x, pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER).getPosition().y, pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER).getPosition().x, pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER).getPosition().y, guidePaint);
                    guidelineCanvas.drawLine(pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER).getPosition().x, pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER).getPosition().y, pose.getPoseLandmark(PoseLandmark.LEFT_HIP).getPosition().x, pose.getPoseLandmark(PoseLandmark.LEFT_HIP).getPosition().y, guidePaint);
                    guidelineCanvas.drawLine(pose.getPoseLandmark(PoseLandmark.RIGHT_HIP).getPosition().x, pose.getPoseLandmark(PoseLandmark.RIGHT_HIP).getPosition().y, pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER).getPosition().x, pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER).getPosition().y, guidePaint);
                    guidelineCanvas.drawLine(pose.getPoseLandmark(PoseLandmark.LEFT_HIP).getPosition().x, pose.getPoseLandmark(PoseLandmark.LEFT_HIP).getPosition().y, pose.getPoseLandmark(PoseLandmark.RIGHT_HIP).getPosition().x, pose.getPoseLandmark(PoseLandmark.RIGHT_HIP).getPosition().y, guidePaint);

                    //limbs
                    guidelineCanvas.drawLine(pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER).getPosition().x, pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER).getPosition().y, pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW).getPosition().x, pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW).getPosition().y, guidePaint);
                    guidelineCanvas.drawLine(pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW).getPosition().x, pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW).getPosition().y, pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER).getPosition().x, pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER).getPosition().y, guidePaint);
                    guidelineCanvas.drawLine(pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW).getPosition().x, pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW).getPosition().y, pose.getPoseLandmark(PoseLandmark.LEFT_WRIST).getPosition().x, pose.getPoseLandmark(PoseLandmark.LEFT_WRIST).getPosition().y, guidePaint);
                    guidelineCanvas.drawLine(pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW).getPosition().x, pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW).getPosition().y, pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST).getPosition().x, pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST).getPosition().y, guidePaint);
                    //
                    guidelineCanvas.drawLine(pose.getPoseLandmark(PoseLandmark.LEFT_HIP).getPosition().x, pose.getPoseLandmark(PoseLandmark.LEFT_HIP).getPosition().y, pose.getPoseLandmark(PoseLandmark.LEFT_KNEE).getPosition().x, pose.getPoseLandmark(PoseLandmark.LEFT_KNEE).getPosition().y, guidePaint);
                    guidelineCanvas.drawLine(pose.getPoseLandmark(PoseLandmark.RIGHT_HIP).getPosition().x, pose.getPoseLandmark(PoseLandmark.RIGHT_HIP).getPosition().y, pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE).getPosition().x, pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE).getPosition().y, guidePaint);
                    guidelineCanvas.drawLine(pose.getPoseLandmark(PoseLandmark.LEFT_KNEE).getPosition().x, pose.getPoseLandmark(PoseLandmark.LEFT_KNEE).getPosition().y, pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE).getPosition().x, pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE).getPosition().y, guidePaint);
                    guidelineCanvas.drawLine(pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE).getPosition().x, pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE).getPosition().y, pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE).getPosition().x, pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE).getPosition().y, guidePaint);

                    //MOUTH
                    guidelineCanvas.drawLine(pose.getPoseLandmark(PoseLandmark.LEFT_MOUTH).getPosition().x, pose.getPoseLandmark(PoseLandmark.LEFT_MOUTH).getPosition().y, pose.getPoseLandmark(PoseLandmark.RIGHT_MOUTH).getPosition().x, pose.getPoseLandmark(PoseLandmark.RIGHT_MOUTH).getPosition().y, guidePaint);
                    guidelineCanvas.drawLine(pose.getPoseLandmark(PoseLandmark.LEFT_EAR).getPosition().x, pose.getPoseLandmark(PoseLandmark.LEFT_EAR).getPosition().y, pose.getPoseLandmark(PoseLandmark.LEFT_EYE).getPosition().x, pose.getPoseLandmark(PoseLandmark.LEFT_EYE).getPosition().y, guidePaint);
                    guidelineCanvas.drawLine(pose.getPoseLandmark(PoseLandmark.RIGHT_EAR).getPosition().x, pose.getPoseLandmark(PoseLandmark.RIGHT_EAR).getPosition().y, pose.getPoseLandmark(PoseLandmark.RIGHT_EYE).getPosition().x, pose.getPoseLandmark(PoseLandmark.RIGHT_EYE).getPosition().y, guidePaint);
                    guidelineCanvas.drawLine(pose.getPoseLandmark(PoseLandmark.LEFT_EYE).getPosition().x, pose.getPoseLandmark(PoseLandmark.LEFT_EYE).getPosition().y, pose.getPoseLandmark(PoseLandmark.NOSE).getPosition().x, pose.getPoseLandmark(PoseLandmark.NOSE).getPosition().y, guidePaint);
                    guidelineCanvas.drawLine(pose.getPoseLandmark(PoseLandmark.RIGHT_EYE).getPosition().x, pose.getPoseLandmark(PoseLandmark.RIGHT_EYE).getPosition().y, pose.getPoseLandmark(PoseLandmark.NOSE).getPosition().x, pose.getPoseLandmark(PoseLandmark.NOSE).getPosition().y, guidePaint);
                    canvasAlreadyClear = false;
                }else{
                    canvasAlreadyClear = true;
                }

                guidelineView.invalidate();
                guidelineView.setImageBitmap(guidelineBmp);
                Log.d("debugg", "New Guidelines Drawn");
            }
        });

    }

    private void initViews(){
        previewView = findViewById(R.id.viewFinder);
        guidelineView = findViewById(R.id.canvas);
        count = findViewById(R.id.count);
        timeEx = findViewById(R.id.timeEx);
        countEx = findViewById(R.id.countEx);
    }

    private void runTest(){
        if(detector == null){
            AccuratePoseDetectorOptions options = new AccuratePoseDetectorOptions.Builder().setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE).build();
            detector = PoseDetection.getClient(options);
        }

        tempBitmap = previewView.getBitmap();
        if(previewView.getBitmap() == null){
            return;
        }

        isFrameBeingTested = true;
        detector.process(InputImage.fromBitmap(tempBitmap, 0)).addOnCompleteListener(new OnCompleteListener<Pose>() {

            @Override
            public void onComplete(@NonNull Task<Pose> task) {
                if(task.isSuccessful()){
                    Pose pose = task.getResult();
                    handlePoseDetection(pose); // 포즈 감지 후 적절한 동작 처리
                    List<PoseLandmark> landmarks = pose.getAllPoseLandmarks();
                    Log.d("debugg", "Landmarks found : " + landmarks.size());
                    if(landmarks.size() == 0){
                        isFrameBeingTested = false;
                        if(!canvasAlreadyClear)
                            loadGuidelines(tempBitmap, null);
                        return;
                    }

                    loadGuidelines(tempBitmap, pose);
                    isFrameBeingTested = false;
                }else{
                    Log.e("debugg", "Error in test", task.getException());
                    loadGuidelines(tempBitmap, null);
                    isFrameBeingTested = false;
                }
            }
        });
    }

    private void initTargetPoses() {
        targetSquatStartSign = new TargetPose(
                Arrays.asList(
                        new TargetShape(PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_HIP, PoseLandmark.RIGHT_KNEE, 60.0),
                        new TargetShape(PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_HIP, PoseLandmark.LEFT_KNEE, 60.0),
                        new TargetShape(PoseLandmark.LEFT_HIP, PoseLandmark.LEFT_KNEE, PoseLandmark.LEFT_ANKLE, 70.0),
                        new TargetShape(PoseLandmark.RIGHT_HIP, PoseLandmark.RIGHT_KNEE, PoseLandmark.RIGHT_ANKLE, 70.0)
                )
        );

        targetSquatEndSign = new TargetPose(
                Arrays.asList(
                        new TargetShape(PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_HIP, PoseLandmark.LEFT_ANKLE, 180.0),
                        new TargetShape(PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_HIP, PoseLandmark.RIGHT_ANKLE, 180.0)
                )
        );
    }

    private boolean isPoseMatching(Pose pose, TargetPose targetPose) {
        PoseMatcher matcher = new PoseMatcher(); // PoseMatcher 객체 생성. 이전에 제공된 코드에서 제공된 것처럼 생성해야 합니다.
        return matcher.match(pose, targetPose);
    }

    private void handlePoseDetection(Pose pose) {
        boolean isSquatStart = isPoseMatching(pose, targetSquatStartSign);
        boolean isSquatEnd = isPoseMatching(pose, targetSquatEndSign);

        if (squatStartDetected && isSquatEnd) {
            score++;
            countEx.setText("개수 : " + score);
            squatStartDetected = false; // 다음 연속 감지를 위해 초기화
            squatEndDetected = false;
        } else if (isSquatStart) {
            squatStartDetected = true;
        }
    }

    private void startAnalysis(){
        Handler handler = new Handler(getMainLooper());

        handler.post(new Runnable() {
            @Override
            public void run() {
                if(!isFrameBeingTested){
                    runTest();
                }
                handler.postDelayed(this, UPDATE_TIME);
            }
        });
    }

    private void startInit(){
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider provider = cameraProviderFuture.get();

                    Preview preview = new Preview.Builder().build();
                    preview.setSurfaceProvider(previewView.createSurfaceProvider());
                    imageCapture = new ImageCapture.Builder().build();

                    provider.unbindAll();
                    provider.bindToLifecycle(RecordSquatMain.this, CameraSelector.DEFAULT_FRONT_CAMERA, preview);
                    Toast.makeText(getApplicationContext(), "Camera started", Toast.LENGTH_SHORT).show();

                    startAnalysis();
                } catch (Exception e) {
                    Log.e("debugg", "Error Getting camera Provider", e);
                    Toast.makeText(getApplicationContext(), "Errror Loading Camera Provider, Restart App", Toast.LENGTH_SHORT).show();
                }
            }
        },ActivityCompat.getMainExecutor(RecordSquatMain.this));
    }

    private void checkPermissions(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getApplicationContext(), "Camera Permission Request", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 455);
        }else{
            Toast.makeText(getApplicationContext(), "Camera Permission Granted", Toast.LENGTH_SHORT).show();
            startInit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 455) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Permission not granted !", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Camera Permission Granted", Toast.LENGTH_SHORT).show();
                startInit();
            }
        }
    }

}