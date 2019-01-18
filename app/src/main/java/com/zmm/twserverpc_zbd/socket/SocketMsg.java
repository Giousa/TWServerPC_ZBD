package com.zmm.twserverpc_zbd.socket;

/**
 * Description: Socket  PC-游戏 数据通信
 * Author:zhangmengmeng
 * Date:2019/1/18
 * Email:65489469@qq.com
 */
public class SocketMsg {


    private String id;

    //类型 0：主动 1：被动
    private int type;

    //转速
    private int speed;

    //阻力
    private int resistance;

    //偏移 0:中间  -1~-15  左边   1~15右边
    private int offset;

    //痉挛次数
    private int spasmTimes;

    //痉挛等级  主动模式下，等级为null
    private Integer spasmLevel;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getResistance() {
        return resistance;
    }

    public void setResistance(int resistance) {
        this.resistance = resistance;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getSpasmTimes() {
        return spasmTimes;
    }

    public void setSpasmTimes(int spasmTimes) {
        this.spasmTimes = spasmTimes;
    }

    public Integer getSpasmLevel() {
        return spasmLevel;
    }

    public void setSpasmLevel(Integer spasmLevel) {
        this.spasmLevel = spasmLevel;
    }

    @Override
    public String toString() {
        return "SocketMsg{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", speed=" + speed +
                ", resistance=" + resistance +
                ", offset=" + offset +
                ", spasmTimes=" + spasmTimes +
                ", spasmLevel=" + spasmLevel +
                '}';
    }
}
