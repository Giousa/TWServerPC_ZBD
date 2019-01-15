package com.zmm.twserverpc_zbd.client.model;

import java.io.Serializable;

public class ParamModel implements Serializable{

	private String s_id;
	
	private int flag;
	private int curSpeed;
	private int curResistance;
	private int spasmTimes;
	private int spasticity;
	private int curDirection;
	private int offset;
	private int smartMode;
	private String curTime;
	
	public String getS_id() {
		return s_id;
	}
	public void setS_id(String s_id) {
		this.s_id = s_id;
	}

	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public int getCurSpeed() {
		return curSpeed;
	}
	public void setCurSpeed(int curSpeed) {
		this.curSpeed = curSpeed;
	}
	public int getCurResistance() {
		return curResistance;
	}
	public void setCurResistance(int curResistance) {
		this.curResistance = curResistance;
	}
	public int getSpasmTimes() {
		return spasmTimes;
	}
	public void setSpasmTimes(int spasmTimes) {
		this.spasmTimes = spasmTimes;
	}
	public int getSpasticity() {
		return spasticity;
	}
	public void setSpasticity(int spasticity) {
		this.spasticity = spasticity;
	}
	public int getCurDirection() {
		return curDirection;
	}
	public void setCurDirection(int curDirection) {
		this.curDirection = curDirection;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public int getSmartMode() {
		return smartMode;
	}
	public void setSmartMode(int smartMode) {
		this.smartMode = smartMode;
	}
	public String getCurTime() {
		return curTime;
	}
	public void setCurTime(String curTime) {
		this.curTime = curTime;
	}
	
	
}
