package com.example.lazyworkout.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserList {
    private Map<String, Object> userList = new HashMap<>();
    private Date modifiedDate;
    private UserMessage sentUser;
    private String newestMessage;
    private String sentUid;
    private String receiveUid;
    private List<String> listUser = new ArrayList<>();

    public UserList() { }

    public UserList(String newestMessage, UserMessage sentUser, String receiveUser) {
        this.newestMessage = newestMessage;
        this.sentUser = sentUser;
        this.modifiedDate = Calendar.getInstance().getTime();
        this.receiveUid = receiveUser;
        userList.put("modifiedDate", modifiedDate);
        userList.put("sentUser", sentUser);
        userList.put("newestMessage", newestMessage);
        this.sentUid = sentUser.getUid();
        userList.put("sentUid", this.sentUid);
        userList.put("receiveUid", this.receiveUid);
        listUser.add(sentUser.getUid());
        listUser.add(receiveUser);
        userList.put("listUser", listUser);
    }

    public UserList(Date modifiedDate, String newestMessage, UserMessage sentUser, List<String> listUser) {
        this.modifiedDate = modifiedDate;
        this.newestMessage = newestMessage;
        this.sentUser = sentUser;
        this.listUser = listUser;
        this.sentUid = listUser.get(0);
        this.receiveUid = listUser.get(1);
    }

    public String getNewestMessage() {return this.newestMessage;}
    public String getSentUid() {return this.sentUid;}
    public String getReceiveUid() {return this.receiveUid;}
    public Date getModifiedDate() {return this.modifiedDate;}
    public UserMessage getSentUser() {return this.sentUser;}
    public Map<String, Object> getUserList() {return this.userList;}

    public void setNewestMessage(String newestMessage) {this.newestMessage = newestMessage;}
    public void setModifiedDate(Date modifiedDate) {this.modifiedDate = modifiedDate;}
    public void setSentUser(UserMessage sentUser) {this.sentUser = sentUser;}
}
