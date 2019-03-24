package com.example.firebaseauth;

import java.io.Serializable;

public class Room implements Serializable {

    private String roomName, length, width, imageUrl, roomDesc;

    public Room() {

    }

    Room(String roomName, String length, String width, String imageUrl, String roomDesc) {
        this.roomName = roomName;
        this.length = length;
        this.width = width;
        this.imageUrl = imageUrl;
        this.roomDesc = roomDesc;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setRoomDesc(String roomDesc) {
        this.roomDesc = roomDesc;
    }

    String getRoomName() {
        return roomName;
    }

    String getLength() {
        return length;
    }

    String getWidth() {
        return width;
    }

    String getImageUrl() {
        return imageUrl;
    }

    String getRoomDesc() {
        return roomDesc;
    }
}
