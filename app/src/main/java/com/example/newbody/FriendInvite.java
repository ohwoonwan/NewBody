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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
    private Button searchBtn;

    String searchOption = "name";
    private RecyclerView recyclerView;
    TextView textView;
    private EditText searchEditText;
    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerViewAdapterInvite adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_invite);

        prev = findViewById(R.id.prevButtonFriendInvite2);
        recyclerView = findViewById(R.id.recyclerView2);
        searchBtn = findViewById(R.id.searchBtn2);
        textView = findViewById(R.id.textView2);
        searchEditText = findViewById(R.id.searchWord2);
        recyclerViewAdapter = new RecyclerViewAdapter();
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
                String searchOption = "name";
                adapter.performSearch(searchWord, searchOption); // 수정된 부분
                if (adapter.getItemCount() == 0) {
                    Toast.makeText(FriendInvite.this, "존재하지 않는 이름입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
