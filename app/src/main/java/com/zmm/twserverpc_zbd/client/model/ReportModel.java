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

    private long startTime;
    private int totalTime;
    private double totalMileage;
    private int activeTime;
    private int passiveTime;
    private double activeMileage;
    private double passiveMileage;
    private float timeAngle;
    private float mileageAngle;
    private byte spasmTimes;
    private byte spasmLevel;
    private double totalCal;
    private byte minSpeed;
    private byte maxSpeed;
    private byte avgSpeed;
    private byte minResistance;
    private byte maxResistance;
    private byte avgResistance;
    private String suggestion;
    private ArrayList<Integer> resistanceLeft;
    private ArrayList<Integer> resistanceRight;
    private ArrayList<Integer> powerLeft;
    private ArrayList<Integer> powerRight;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public double getTotalMileage() {
        return totalMileage;
    }

    public void setTotalMileage(double totalMileage) {
        this.totalMileage = totalMileage;
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

    public double getActiveMileage() {
        return activeMileage;
    }

    public void setActiveMileage(double activeMileage) {
        this.activeMileage = activeMileage;
    }

    public double getPassiveMileage() {
        return passiveMileage;
    }

    public void setPassiveMileage(double passiveMileage) {
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

    public byte getSpasmTimes() {
        return spasmTimes;
    }

    public void setSpasmTimes(byte spasmTimes) {
        this.spasmTimes = spasmTimes;
    }

    public byte getSpasmLevel() {
        return spasmLevel;
    }

    public void setSpasmLevel(byte spasmLevel) {
        this.spasmLevel = spasmLevel;
    }

    public double getTotalCal() {
        return totalCal;
    }

    public void setTotalCal(double totalCal) {
        this.totalCal = totalCal;
    }

    public byte getMinSpeed() {
        return minSpeed;
    }

    public void setMinSpeed(byte minSpeed) {
        this.minSpeed = minSpeed;
    }

    public byte getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(byte maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public byte getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(byte avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public byte getMinResistance() {
        return minResistance;
    }

    public void setMinResistance(byte minResistance) {
        this.minResistance = minResistance;
    }

    public byte getMaxResistance() {
        return maxResistance;
    }

    public void setMaxResistance(byte maxResistance) {
        this.maxResistance = maxResistance;
    }

    public byte getAvgResistance() {
        return avgResistance;
    }

    public void setAvgResistance(byte avgResistance) {
        this.avgResistance = avgResistance;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public ArrayList<Integer> getResistanceLeft() {
        return resistanceLeft;
    }

    public void setResistanceLeft(ArrayList<Integer> resistanceLeft) {
        this.resistanceLeft = resistanceLeft;
    }

    public ArrayList<Integer> getResistanceRight() {
        return resistanceRight;
    }

    public void setResistanceRight(ArrayList<Integer> resistanceRight) {
        this.resistanceRight = resistanceRight;
    }

    public ArrayList<Integer> getPowerLeft() {
        return powerLeft;
    }

    public void setPowerLeft(ArrayList<Integer> powerLeft) {
        this.powerLeft = powerLeft;
    }

    public ArrayList<Integer> getPowerRight() {
        return powerRight;
    }

    public void setPowerRight(ArrayList<Integer> powerRight) {
        this.powerRight = powerRight;
    }
}
