package com.example.firebaseauth;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Address implements Serializable {

    private String street, town, postCode, houseNo;

    Address() {

    }

    public Address(String houseNo, String street, String town, String postCode) {
        this.houseNo = houseNo;
        this.street = street;
        this.town = town;
        this.postCode= postCode;
    }

    void setStreet(String street) {
        this.street = street;
    }

    void setTown(String town) {
        this.town = town;
    }

    void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }

    String getStreet() {
        return street;
    }

    String getTown() {
        return town;
    }

    String getPostCode() {
        return postCode;
    }

    String getHouseNo() {
        return houseNo;
    }
}