package com.example.sharepulse;

public class DonationCamp {
    private String name;
    private String description;
    private String link;
    private String xCoordinate;
    private String yCoordinate;
    private String startDate;
    private String endDate;
    private String address;
    private String startTime;
    private String endTime;
    private String campId;

    public DonationCamp() {
        // Empty constructor for Firebase
    }

    public DonationCamp(String name, String description, String link, String xCoordinate, String yCoordinate,
                        String startDate, String endDate, String address, String startTime, String endTime,
                        String campId) {
        this.name = name;
        this.description = description;
        this.link = link;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.address = address;
        this.startTime = startTime;
        this.endTime = endTime;
        this.campId = campId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getXCoordinate() {
        return xCoordinate;
    }

    public void setXCoordinate(String xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public String getYCoordinate() {
        return yCoordinate;
    }

    public void setYCoordinate(String yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCampId() {
        return campId;
    }

    public void setCampId(String campId) {
        this.campId = campId;
    }
}
