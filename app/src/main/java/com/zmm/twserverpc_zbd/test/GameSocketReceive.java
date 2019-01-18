package com.zmm.twserverpc_zbd.test;

import com.alibaba.fastjson.JSON;
import com.zmm.twserverpc_zbd.socket.SocketMsg;
import com.zmm.twserverpc_zbd.socket.SocketRelation;
import com.zmm.twserverpc_zbd.socket.SocketStatus;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Description:
 * Author:zhangmengmeng
 * Date:2019/1/18
 * Email:65489469@qq.com
 */
public class GameSocketReceive {

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

    private static void paseData(String data) {
        if (data.contains("type")) {
            //主动或者被动数据
            passiveOrActive(data);

        } else if (data.contains("status")) {
            //暂停或者停止
            gameStatus(data);

        } else if (data.contains("patientName")) {
            //关联数据
            relation(data);

        }
    }

    private static void passiveOrActive(String data) {
        SocketMsg socketMsg = JSON.parseObject(data, SocketMsg.class);
        if(socketMsg.getType() == 0){
            System.out.println("SocketReceive：：主动模式");
        }else {
            System.out.println("SocketReceive：：被动模式");
        }

        System.out.println("SocketReceive：："+socketMsg.toString());

    }

    private static void relation(String data) {
        SocketRelation socketRelation = JSON.parseObject(data, SocketRelation.class);
        System.out.println("SocketReceive：："+socketRelation.toString());

    }

    private static void gameStatus(String data) {
        SocketStatus socketStatus = JSON.parseObject(data, SocketStatus.class);
        if(socketStatus.getStatus() == 0){
            System.out.println("SocketReceive：：暂停");
        }else {
            System.out.println("SocketReceive：：停止");
        }

        System.out.println("SocketReceive：："+socketStatus.toString());

    }

}
