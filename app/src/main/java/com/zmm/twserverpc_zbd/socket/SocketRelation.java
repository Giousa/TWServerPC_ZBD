package com.zmm.twserverpc_zbd.socket;

/**
 * Description:
 * Author:zhangmengmeng
 * Date:2019/1/18
 * Email:65489469@qq.com
 */
public class SocketRelation {

    //设备id
    private String id;

    //患者姓名
    private String patientName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    @Override
    public String toString() {
        return "SocketRelation{" +
                "id='" + id + '\'' +
                ", patientName='" + patientName + '\'' +
                '}';
    }
}
