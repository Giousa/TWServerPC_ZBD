package com.zmm.twserverpc_zbd.client.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Description:
 * Author:zhangmengmeng
 * Date:2017/8/3
 * Time:上午10:06
 */

public class ReportModel implements Serializable{

    private String id;


    //1：机顶盒  2：多机版PC
    private int provenance;

    private int year;
    private int month;

    private String device_id;
    private String device_name;
    private String doctor_id;
    private String user_id;

    private int totalTime;
    private int activeTime;
    private int passiveTime;
    private float activeMileage;
    private float passiveMileage;
    private float timeAngle;
    private float mileageAngle;
    private int spasmTimes;
    private int spasmLevel;
    private int minSpeed;
    private int maxSpeed;
    private int avgSpeed;
    private int minResistance;
    private int maxResistance;
    private int avgResistance;
    private int avgHeartRate;
    private int maxHeartRate;
    private int isPrescription;//1:处方  0：不是
    private String totalCal;
    private String suggestion;
    private String speedRate;
    private String heartRate;

    private String beginTime;
    private String endTime;
    private String createTime;
    private String updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getProvenance() {
        return provenance;
    }

    public void setProvenance(int provenance) {
        this.provenance = provenance;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(String doctor_id) {
        this.doctor_id = doctor_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public int getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(int activeTime) {
        this.activeTime = activeTime;
    }

    public int getPassiveTime() {
        return passiveTime;
    }

    public void setPassiveTime(int passiveTime) {
        this.passiveTime = passiveTime;
    }

    public float getActiveMileage() {
        return activeMileage;
    }

    public void setActiveMileage(float activeMileage) {
        this.activeMileage = activeMileage;
    }

    public float getPassiveMileage() {
        return passiveMileage;
    }

    public void setPassiveMileage(float passiveMileage) {
        this.passiveMileage = passiveMileage;
    }

    public float getTimeAngle() {
        return timeAngle;
    }

    public void setTimeAngle(float timeAngle) {
        this.timeAngle = timeAngle;
    }

    public float getMileageAngle() {
        return mileageAngle;
    }

    public void setMileageAngle(float mileageAngle) {
        this.mileageAngle = mileageAngle;
    }

    public int getSpasmTimes() {
        return spasmTimes;
    }

    public void setSpasmTimes(int spasmTimes) {
        this.spasmTimes = spasmTimes;
    }

    public int getSpasmLevel() {
        return spasmLevel;
    }

    public void setSpasmLevel(int spasmLevel) {
        this.spasmLevel = spasmLevel;
    }

    public int getMinSpeed() {
        return minSpeed;
    }

    public void setMinSpeed(int minSpeed) {
        this.minSpeed = minSpeed;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public int getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(int avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public int getMinResistance() {
        return minResistance;
    }

    public void setMinResistance(int minResistance) {
        this.minResistance = minResistance;
    }

    public int getMaxResistance() {
        return maxResistance;
    }

    public void setMaxResistance(int maxResistance) {
        this.maxResistance = maxResistance;
    }

    public int getAvgResistance() {
        return avgResistance;
    }

    public void setAvgResistance(int avgResistance) {
        this.avgResistance = avgResistance;
    }

    public int getAvgHeartRate() {
        return avgHeartRate;
    }

    public void setAvgHeartRate(int avgHeartRate) {
        this.avgHeartRate = avgHeartRate;
    }

    public int getMaxHeartRate() {
        return maxHeartRate;
    }

    public void setMaxHeartRate(int maxHeartRate) {
        this.maxHeartRate = maxHeartRate;
    }

    public int getIsPrescription() {
        return isPrescription;
    }

    public void setIsPrescription(int isPrescription) {
        this.isPrescription = isPrescription;
    }

    public String getTotalCal() {
        return totalCal;
    }

    public void setTotalCal(String totalCal) {
        this.totalCal = totalCal;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getSpeedRate() {
        return speedRate;
    }

    public void setSpeedRate(String speedRate) {
        this.speedRate = speedRate;
    }

    public String getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(String heartRate) {
        this.heartRate = heartRate;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
