package com.example.sharepulse;

public class BloodDonationRequest {
    private String fName;
    private String lName;
    private String user;
    private String userId;
    private String mobileNo;
    private String gender;
    private String nic;
    private String bDate;
    private String centerCode;
    private boolean is16WeekChecked;
    private boolean isNoPregnantChecked;
    private boolean isNoTransfusionChecked;
    private boolean isNoMedicineChecked;
    private boolean isSurgeryChecked;
    private boolean isAthsmaChecked;
    private boolean isCancerChecked;
    private boolean isHivChecked;
    private String currentDateAndTime;

    public BloodDonationRequest() {
        // Default constructor required for Firebase
    }

    public BloodDonationRequest(String fName, String lName, String user, String userId, String mobileNo, String gender, String nic, String bDate, String centerCode,
                                boolean is16WeekChecked, boolean isNoPregnantChecked, boolean isNoTransfusionChecked, boolean isNoMedicineChecked,
                                boolean isSurgeryChecked, boolean isAthsmaChecked, boolean isCancerChecked, boolean isHivChecked,
                                String currentDateAndTime) {
        this.fName = fName;
        this.lName = lName;
        this.user = user;
        this.userId = userId;
        this.mobileNo = mobileNo;
        this.gender = gender;
        this.nic = nic;
        this.bDate = bDate;
        this.centerCode = centerCode;
        this.is16WeekChecked = is16WeekChecked;
        this.isNoPregnantChecked = isNoPregnantChecked;
        this.isNoTransfusionChecked = isNoTransfusionChecked;
        this.isNoMedicineChecked = isNoMedicineChecked;
        this.isSurgeryChecked = isSurgeryChecked;
        this.isAthsmaChecked = isAthsmaChecked;
        this.isCancerChecked = isCancerChecked;
        this.isHivChecked = isHivChecked;
        this.currentDateAndTime = currentDateAndTime;
    }

    // Getters and setters

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getbDate() {
        return bDate;
    }

    public void setbDate(String bDate) {
        this.bDate = bDate;
    }

    public String getCenterCode() {
        return centerCode;
    }

    public void setCenterCode(String centerCode) {
        this.centerCode = centerCode;
    }

    public boolean isIs16WeekChecked() {
        return is16WeekChecked;
    }

    public void setIs16WeekChecked(boolean is16WeekChecked) {
        this.is16WeekChecked = is16WeekChecked;
    }

    public boolean isNoPregnantChecked() {
        return isNoPregnantChecked;
    }

    public void setNoPregnantChecked(boolean isNoPregnantChecked) {
        this.isNoPregnantChecked = isNoPregnantChecked;
    }

    public boolean isNoTransfusionChecked() {
        return isNoTransfusionChecked;
    }

    public void setNoTransfusionChecked(boolean isNoTransfusionChecked) {
        this.isNoTransfusionChecked = isNoTransfusionChecked;
    }

    public boolean isNoMedicineChecked() {
        return isNoMedicineChecked;
    }

    public void setNoMedicineChecked(boolean isNoMedicineChecked) {
        this.isNoMedicineChecked = isNoMedicineChecked;
    }

    public boolean isSurgeryChecked() {
        return isSurgeryChecked;
    }

    public void setSurgeryChecked(boolean isSurgeryChecked) {
        this.isSurgeryChecked = isSurgeryChecked;
    }

    public boolean isAthsmaChecked() {
        return isAthsmaChecked;
    }

    public void setAthsmaChecked(boolean isAthsmaChecked) {
        this.isAthsmaChecked = isAthsmaChecked;
    }

    public boolean isCancerChecked() {
        return isCancerChecked;
    }

    public void setCancerChecked(boolean isCancerChecked) {
        this.isCancerChecked = isCancerChecked;
    }

    public boolean isHivChecked() {
        return isHivChecked;
    }

    public void setHivChecked(boolean isHivChecked) {
        this.isHivChecked = isHivChecked;
    }

    public String getCurrentDateAndTime() {
        return currentDateAndTime;
    }

    public void setCurrentDateAndTime(String currentDateAndTime) {
        this.currentDateAndTime = currentDateAndTime;
    }
}

