package com.zmm.twserverpc_zbd.client.model;

import java.util.List;

/**
 * Description:
 * Author:zhangmengmeng
 * Date:2019/1/16
 * Email:65489469@qq.com
 */
public class MessageModel {


    /**
     * id : xxxxxxxxxxx
     * code : 2001
     * data : ["12132","ssss","fsfdf"]
     * createTime : 1522226878636
     */

    private String id;
    private String code;
    private long createTime;
    private List<String> data;

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

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
