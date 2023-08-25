package com.example.newbody;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newbody.FriendInviteContact;
import com.example.newbody.R;
import com.example.newbody.RecyclerViewAdapterInvite;

import java.util.ArrayList;
import java.util.List;

public class FriendInvite extends AppCompatActivity {
    private Button prev;
    private RecyclerView recyclerView;
    private RecyclerViewAdapterInvite adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_invite);
        prev = findViewById(R.id.prevButtonFriendInvite);
        recyclerView = findViewById(R.id.recyclerView);

        // 연락처 데이터를 읽어와서 contactList에 저장
        List<FriendInviteContact> contactList = getContactList();

        // RecyclerViewAdapterInvite 어댑터를 초기화하고 리사이클러뷰에 설정
        adapter = new RecyclerViewAdapterInvite(this, contactList);
        recyclerView.setAdapter(adapter);

        // 리사이클러뷰에 LinearLayoutManager 설정 (세로 방향)
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // (옵션) 리사이클러뷰에 구분선 추가
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

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

    private List<FriendInviteContact> getContactList() {
        List<FriendInviteContact> contactList = new ArrayList<>();

        // 연락처 데이터를 읽어오는 코드
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                FriendInviteContact contact = new FriendInviteContact(name, phoneNumber);
                contactList.add(contact);
            }
            cursor.close();
        }

        return contactList;
    }
}
