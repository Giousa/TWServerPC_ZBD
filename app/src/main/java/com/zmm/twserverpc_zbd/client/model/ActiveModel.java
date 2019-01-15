package com.zmm.twserverpc_zbd.client.model;

/**
 * Description:
 * Author:zhangmengmeng
 * Date:2017/8/14
 * Time:上午10:21
 */

public class ActiveModel {

    private String loginId;
    private String s_id;
    private String s_name;
    private String curSpeed;
    private String curResistance;
    private String curDirection;
    private String calories;
    private String activeMileage;
    private String spasmTimes;
    private String offset;

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getS_id() {
        return s_id;
    }

    public void setS_id(String s_id) {
        this.s_id = s_id;
    }

    public String getS_name() {
        return s_name;
    }

    public void setS_name(String s_name) {
        this.s_name = s_name;
    }

    public String getCurSpeed() {
        return curSpeed;
    }

    public void setCurSpeed(String curSpeed) {
        this.curSpeed = curSpeed;
    }

    public String getCurResistance() {
        return curResistance;
    }

    public void setCurResistance(String curResistance) {
        this.curResistance = curResistance;
    }

    public String getCurDirection() {
        return curDirection;
    }

    public void setCurDirection(String curDirection) {
        this.curDirection = curDirection;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getActiveMileage() {
        return activeMileage;
    }

    public void setActiveMileage(String activeMileage) {
        this.activeMileage = activeMileage;
    }

    public String getSpasmTimes() {
        return spasmTimes;
    }

    public void setSpasmTimes(String spasmTimes) {
        this.spasmTimes = spasmTimes;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

}
