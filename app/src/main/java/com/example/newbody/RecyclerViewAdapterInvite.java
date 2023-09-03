package com.example.newbody;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapterInvite extends RecyclerView.Adapter<RecyclerViewAdapterInvite.ViewHolder> {

    private List<FriendInviteContact> originalUsers;
    private List<FriendInviteContact> filteredUsers;
    private Context context;
    private boolean nameFound = true;
    public RecyclerViewAdapterInvite(Context context, List<FriendInviteContact> contactList) {
        this.context = context;
        this.originalUsers = new ArrayList<>(contactList); // 원본 목록 초기화
        this.filteredUsers = new ArrayList<>(contactList); // 필터링된 목록 초기화
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
        FriendInviteContact contact = filteredUsers.get(position);
        holder.nameTextView.setText(contact.getName());
        holder.phoneNumberTextView.setText(contact.getPhoneNumber());

        // 초대 버튼 클릭 리스너 설정
        holder.ivtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("RecyclerViewAdapter", "친구 초대 버튼 클릭");
                // 친구 초대 창으로 이동하는 Intent 생성
                sendInvitation(contact);
            }
        });
    }
    // 검색 기능을 수행하는 메서드
    public void performSearch(String searchWord, String option) {
        filteredUsers.clear();
        nameFound = false;

        if (searchWord == null || option == null || searchWord.isEmpty() || option.isEmpty()) {
            // 검색어가 없을 때 전체 데이터를 불러옵니다.
            filteredUsers.addAll(originalUsers);
        } else {
            for (FriendInviteContact contact : originalUsers) {
                String name = contact.getName();

                if (name != null) {
                    // 검색 옵션에 따라 데이터를 필터링하여 추가합니다.
                    if (option.equals("name") && name.contains(searchWord)) {
                        filteredUsers.add(contact);
                        nameFound = true;
                    }
                }
            }
        }
        notifyDataSetChanged(); // 데이터 변경을 어댑터에 알려 화면을 갱신합니다.
    }


    // 메시지 보내기 기능
    private void sendInvitation(FriendInviteContact contact) {
        String phoneNumber = contact.getPhoneNumber();
        String message = "새로운 삶을 살고싶으신가요? 지금부터 시작하세요! 초보자도 쉽게 따라할수 있는 홈트레이닝 NewBody: https://newbody.page.link/mKfG";
        // SMS를 보내기 위한 Intent 생성
        Uri uri = Uri.parse("smsto:" + phoneNumber);
        Intent sendIntent = new Intent(Intent.ACTION_SENDTO, uri);
        sendIntent.putExtra("sms_body", message);

        if (sendIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(sendIntent);
            Toast.makeText(context, "정상적으로 초대 메시지를 보냈습니다!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "메시지를 보낼 앱이 설치되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return filteredUsers.size();
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
