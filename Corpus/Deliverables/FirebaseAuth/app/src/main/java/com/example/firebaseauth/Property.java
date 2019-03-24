package com.example.firebaseauth;

import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

public class Property implements Serializable {

    private String customerName, bathrooms, bedrooms, receptions, propertyType, creatorId, customerId, propertyId, mainPhotoUrl, price;
    private boolean dg, forSale, forLet, garden, parking, gasHeat;
    private Address addr;
    private @ServerTimestamp Date timestamp;

    public Property() {

    }

    Property(String customerName, String bathrooms, String bedrooms, String receptions, String propertyType, Address addr, String creatorId, String customerId, String propertyId, String mainPhotoUrl) {
        this.customerName = customerName;
        this.bathrooms = bathrooms;
        this.bedrooms = bedrooms;
        this.receptions = receptions;
        this.propertyType = propertyType;
        this.dg = dg;
        this.forSale = forSale;
        this.forLet = forLet;
        this.garden = garden;
        this.parking = parking;
        this.gasHeat = gasHeat;
        this.price = price;
        this.addr = addr;
        this.creatorId = creatorId;
        this.customerId = customerId;
        this.propertyId = propertyId;
        this.mainPhotoUrl = mainPhotoUrl;
    }

    String getCustomer() {
        return customerName;
    }

    String getBathrooms() {
        return bathrooms;
    }

    String getBedrooms() {
        return bedrooms;
    }

    String getReceptions() {
        return receptions;
    }

    String getPropertyType() {
        return propertyType;
    }

    String getCreatorId() {
        return creatorId;
    }

    String getCustomerId() {
        return customerId;
    }

    String getPropertyId() {
        return propertyId;
    }

    String getMainPhotoUrl() {
        return mainPhotoUrl;
    }

    public String getPrice() {
        return price;
    }

    public boolean isDg() {
        return dg;
    }

    public boolean isForSale() {
        return forSale;
    }

    public boolean isForLet() {
        return forLet;
    }

    public boolean isGarden() {
        return garden;
    }

    public boolean isParking() {
        return parking;
    }

    public boolean isGasHeat() {
        return gasHeat;
    }

    public Address getAddress() {
        return addr;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setCustomer(String customer) {
        this.customerName = customer;
    }

    public void setBathrooms(String bathrooms) {
        this.bathrooms = bathrooms;
    }

    public void setBedrooms(String bedrooms) {
        this.bedrooms = bedrooms;
    }

    public void setReceptions(String receptions) {
        this.receptions = receptions;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public void setMainPhotoUrl(String mainPhotoUrl) {
        this.mainPhotoUrl = mainPhotoUrl;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setDg(boolean dg) {
        this.dg = dg;
    }

    public void setForSale(boolean forSale) {
        this.forSale = forSale;
    }

    public void setForLet(boolean forLet) {
        this.forLet = forLet;
    }

    public void setGarden(boolean garden) {
        this.garden = garden;
    }

    public void setParking(boolean parking) {
        this.parking = parking;
    }

    public void setGasHeat(boolean gasHeat) {
        this.gasHeat = gasHeat;
    }

    public void setAddress(Address addr) {
        this.addr = addr;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
