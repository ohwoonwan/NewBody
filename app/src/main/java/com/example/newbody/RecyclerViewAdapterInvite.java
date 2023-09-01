package com.example.newbody;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
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

import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
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

    // 친구초대 창에 나오는 정보
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FriendInviteContact contact = contactList.get(position);
        holder.nameTextView.setText(contact.getName());
        holder.phoneNumberTextView.setText(contact.getPhoneNumber());

        // 초대 버튼 클릭 리스너 설정
        holder.ivtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("RecyclerViewAdapter", "친구 초대 버튼 클릭");
                // 친구 초대 창으로 이동하는 Intent 생성
                Intent inviteIntent = new Intent(context, FriendInvite.class);
                context.startActivity(inviteIntent);
                // 카카오톡 앱 링크 생성
                String packageName = "com.kakao.talk";
                String dynamicLinkUrl = "https://newbody.page.link/mKfG";
                String appName = "NewBody"; // 앱 이름

                // Firebase Dynamic Links를 통해 동적 링크 생성하기 위한 파라미터 설정
                DynamicLink.Builder dynamicLinkBuilder = FirebaseDynamicLinks.getInstance()
                        .createDynamicLink()
                        .setDomainUriPrefix("https://newbody.page.link") // 파이어베이스에서 설정한 도메인 URL
                        .setLink(Uri.parse("https://newbody.page.link/mKfG")) // 딥 링크 URL
                        .setAndroidParameters(new DynamicLink.AndroidParameters.Builder("com.example.newbody") // Android 앱의 패키지명
                                .setMinimumVersion(1) // 필요에 따라 버전 설정
                                .build())
                        .setSocialMetaTagParameters(
                                new DynamicLink.SocialMetaTagParameters.Builder()
                                        .setTitle("NewBody")
                                        .setDescription("친구 초대 메시지입니다.")
                                        .build());

                Uri dynamicLinkUri = dynamicLinkBuilder.buildDynamicLink().getUri(); // 동적 링크 URI 가져오기

                Intent intent = new Intent(Intent.ACTION_VIEW, dynamicLinkUri);

                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                    Toast.makeText(context, "정상적으로 초대 링크를 보냈습니다!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(context, "초대할 수 있는 앱이 설치되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
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
