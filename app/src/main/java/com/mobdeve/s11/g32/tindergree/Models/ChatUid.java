package com.mobdeve.s11.g32.tindergree.Models;

public class ChatUid {
    private String member1;
    private String member2;
    private String chatUID;

    public ChatUid(String member1, String member2, String chatUID) {
        this.member1 = member1;
        this.member2 = member2;
        this.chatUID = chatUID;
    }

    public ChatUid() {

    }

    public String getMember1() {
        return member1;
    }

    public void setMember1(String member1) {
        this.member1 = member1;
    }

    public String getMember2() {
        return member2;
    }

    public void setMember2(String member2) {
        this.member2 = member2;
    }

    public String getChatUID() {
        return chatUID;
    }

    public void setChatUID(String chatUID) {
        this.chatUID = chatUID;
    }
}
