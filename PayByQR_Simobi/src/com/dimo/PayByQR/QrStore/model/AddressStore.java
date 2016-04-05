package com.dimo.PayByQR.QrStore.model;

/**
 * Created by san on 1/14/16.
 */
public class AddressStore {
    public String id;
    public String name;
    public String address;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
