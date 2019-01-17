package com.zmm.twserverpc_zbd.test;

import com.alibaba.fastjson.JSON;
import com.zmm.twserverpc_zbd.client.model.ActiveModel;
import com.zmm.twserverpc_zbd.client.model.PassiveModel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Description:
 * Author:zhangmengmeng
 * Date:2019/1/17
 * Email:65489469@qq.com
 */
public class UDPReceive {

    public static void main(String[] args) throws IOException {

        // 创建接收端Socket对象
        DatagramSocket ds = new DatagramSocket(12008);

        // 创建数据包
        while (true) {

            byte[] bys = new byte[1024];
            DatagramPacket dp = new DatagramPacket(bys, bys.length);

            // //接收数据
            ds.receive(dp);

            // 解析数据
            String data = new String(dp.getData(), 0, dp.getLength());

            paseData(data);

        }
    }

    /**
     * 解析数据
     * @param data
     */
    private static void paseData(String data) {
        if (data.contains("maxSpeed") || data.contains("maxResistance")) {
            //主被动停止
            gameOver();

        } else if (data.contains("passiveMileage")) {
            //被动数据
            passiveModel(data);

        } else if (data.contains("activeMileage")) {
            //主动数据
            activeModel(data);

        } else if (data.contains("beginTime")) {
            //暂停
            pause(data);
        }
    }

    private static void passiveModel(String data) {
        PassiveModel passiveModel = JSON.parseObject(data, PassiveModel.class);

        System.out.println("被动数据："+passiveModel.toString());

    }

    private static void activeModel(String data) {
        ActiveModel activeModel = JSON.parseObject(data, ActiveModel.class);

    }

    private static void gameOver() {

    }

    private static void pause(String data) {

    }
}
