package com.zmm.twserverpc_zbd.socket;

/**
 * Description:
 * Author:zhangmengmeng
 * Date:2019/1/18
 * Email:65489469@qq.com
 */
public class SocketStatus {

    private String id;

    //0:暂停  1：停止
    private int status;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SocketStatus{" +
                "id='" + id + '\'' +
                ", status=" + status +
                '}';
    }
}
