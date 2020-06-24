package com.himanshu.onlineshopping.Model;

public class Seller {
    private String Sid, Name, Phone, Email, Password, ShopAddress, Address;

    public Seller() {
    }

    public Seller(String sid, String name, String phone, String email, String password, String shopAddress, String address) {
        Sid = sid;
        Name = name;
        Phone = phone;
        Email = email;
        Password = password;
        ShopAddress = shopAddress;
        Address = address;
    }

    public String getSid() {
        return Sid;
    }

    public void setSid(String sid) {
        Sid = sid;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getShopAddress() {
        return ShopAddress;
    }

    public void setShopAddress(String shopAddress) {
        ShopAddress = shopAddress;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}