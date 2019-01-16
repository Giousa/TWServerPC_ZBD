package com.zmm.twserverpc_zbd.client.model;

import java.util.List;

/**
 * Description:
 * Author:zhangmengmeng
 * Date:2019/1/16
 * Email:65489469@qq.com
 */
public class RelationModel {


    /**
     * id : popoppopop
     * code : 1001
     * data : [{"deviceId":"aaaaaa","patientName":"刘备"},{"deviceId":"bbbbbb","patientName":"曹操"},{"deviceId":"cccccc","patientName":"孙权"},{"deviceId":"dddddd","patientName":"袁绍"}]
     * createTime : 1522226878636
     */

    private String id;
    private String code;
    private long createTime;
    private List<DataBean> data;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * deviceId : aaaaaa
         * patientName : 刘备
         */

        private String deviceId;
        private String patientName;

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getPatientName() {
            return patientName;
        }

        public void setPatientName(String patientName) {
            this.patientName = patientName;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "deviceId='" + deviceId + '\'' +
                    ", patientName='" + patientName + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "RelationModel{" +
                "id='" + id + '\'' +
                ", code='" + code + '\'' +
                ", createTime=" + createTime +
                ", data=" + data +
                '}';
    }
}
