package com.example.newbody;

//연락처 가져오기
public class FriendInviteContact {
    private String name;
    private String phoneNumber;

    public FriendInviteContact(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
