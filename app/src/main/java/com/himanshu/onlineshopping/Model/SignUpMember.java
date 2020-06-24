package com.himanshu.onlineshopping.Model;

public class SignUpMember {

    String Name, PhoneNo, Password, Image, Address, PhoneOrder;

    public SignUpMember() {
    }

    public SignUpMember(String name, String phoneNo, String password, String image, String address, String phoneOrder) {
        Name = name;
        PhoneNo = phoneNo;
        Password = password;
        Image = image;
        Address = address;
        PhoneOrder = phoneOrder;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhoneNo() {
        return PhoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        PhoneNo = phoneNo;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getPhoneOrder() {
        return PhoneOrder;
    }

    public void setPhoneOrder(String phoneOrder) {
        PhoneOrder = phoneOrder;
    }
}
