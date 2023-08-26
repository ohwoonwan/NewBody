package com.example.newbody;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kakao.sdk.common.util.Utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.util.List;

public class RecyclerViewAdapterInvite extends RecyclerView.Adapter<RecyclerViewAdapterInvite.ViewHolder> {

    private List<FriendInviteContact> contactList;
    private Context context;

    public RecyclerViewAdapterInvite(Context context, List<FriendInviteContact> contactList) {
        this.context = context;
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friendinviteitem, parent, false);
        return new ViewHolder(view);
    }

    //친구초대 창에 나오는 정보
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FriendInviteContact contact = contactList.get(position);
        holder.nameTextView.setText(contact.getName());
        holder.phoneNumberTextView.setText(contact.getPhoneNumber());
        String nativeAppKey = context.getString(R.string.kakao_share_app_key);
        // 초대 버튼 클릭 리스너 설정
        holder.ivtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("RecyclerViewAdapter", "친구 초대 버튼 클릭");
                // 카카오톡 앱 링크 생성
                String packageName = "com.kakao.talk";
                String dynamicLinkUrl = "https://newbody.page.link/XktS";
                String appName = "NewBody"; // 앱 이름
                String kakaoLink = "kakaotalk://send?text=안녕하세요! 친구 초대 메시지입니다.&url=" + dynamicLinkUrl;
                Log.d("RecyclerViewAdapter", "카카오톡 링크 URI: " + kakaoLink); // 추가한 라인
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(kakaoLink));
                intent.setPackage(packageName);

                // 카카오톡 앱이 설치되어 있는지 확인
                PackageManager packageManager = context.getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                boolean isKakaoInstalled = activities.size() > 0;

                if (isKakaoInstalled) {
                    // 카카오톡 앱이 설치되어 있을 때 카카오톡 앱 실행
                    context.startActivity(intent);
                    Toast.makeText(context, "카카오톡으로 정상적으로 초대 링크를 보냈습니다!", Toast.LENGTH_SHORT).show();
                } else {
                    // 카카오톡 앱이 설치되어 있지 않을 때 다른 동작 수행 또는 알림 표시
                    // 예: "카카오톡 앱을 설치하세요." 메시지 표시
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView phoneNumberTextView;

        public Button ivtBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name);
            phoneNumberTextView = itemView.findViewById(R.id.phoneNumber);
            ivtBtn = itemView.findViewById(R.id.ivtBtn);
        }
    }
}
