package com.example.sharepulse;

public class ReadWriteUserDetails {
    public String fName,lName,mobileNo,gender,nic,bDate, imageUrl;

    //empty constructor for database reading
    public ReadWriteUserDetails(){};

    public ReadWriteUserDetails(String fName, String lName, String mobileNo, String gender, String nic, String bDate){

        this.fName = fName;
        this.lName = lName;
        this.mobileNo = mobileNo;
        this.gender = gender;
        this.nic = nic;
        this.bDate = bDate;
    }

    public ReadWriteUserDetails(String fName, String lName, String mobileNo, String gender, String nic, String bDate, String imageUrl) {
        this.fName = fName;
        this.lName = lName;
        this.mobileNo = mobileNo;
        this.gender = gender;
        this.nic = nic;
        this.bDate = bDate;
        this.imageUrl = imageUrl;
    }
}
