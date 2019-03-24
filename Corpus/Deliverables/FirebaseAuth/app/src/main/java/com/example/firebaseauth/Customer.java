package com.example.firebaseauth;

import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

public class Customer implements Serializable {

    private String name , email, phoneNo, creatorId, customerId;
    private Address addr;
    private @ServerTimestamp Date timestamp;

    public Customer() {

    }

    Customer(String name, String email, String phoneNo, Address addr, String creatorId, String customerId) {
        this.name = name;
        this.email = email;
        this.phoneNo = phoneNo;
        this.addr = addr;
        this.creatorId = creatorId;
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    String getEmail() {
        return email;
    }

    String getPhoneNo() {
        return phoneNo;
    }

    Address getAddress() {
        return addr;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    String getCreatorId() {
        return creatorId;
    }

    String getCustomerId() {
        return customerId;
    }

    public void setName(String name) {
        this.name = name;
    }

    void setEmail(String email) {
        this.email = email;
    }

    void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    void setAddress(Address addr) { this.addr = addr;
    }
}
