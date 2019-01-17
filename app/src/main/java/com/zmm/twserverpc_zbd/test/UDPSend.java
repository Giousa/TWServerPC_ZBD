package com.zmm.twserverpc_zbd.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Description:
 * Author:zhangmengmeng
 * Date:2019/1/17
 * Email:65489469@qq.com
 */
public class UDPSend {

    private static int speed = 5;

    public static void main(String[] args) throws IOException {

        // 创建发送端Socket对象
        DatagramSocket ds = new DatagramSocket();

        while (true){

            try {
                // 创建数据并打包
                String msg = "{\"loginId\":\"TW2018-TEST-ID\",\"s_id\":\"中文数据\",\"s_name\":\"TW2018\",\"curSpeed\":\""+speed+"\",\"curResistance\":\"3\",\"curDirection\":\"1\",\"calories\":\".000\",\"passiveMileage\":\".000\",\"spasmLevel\":\"6\",\"spasmTimes\":\"0\"}";

                byte[] bys = msg.getBytes();

                DatagramPacket dp = new DatagramPacket(bys, bys.length, InetAddress.getByName("127.0.0.1"), 12008);

                // 发送数据
                ds.send(dp);

                speed++;

                if(speed >= 60){
                    speed = 5;
                }

                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        // 释放资源
//        ds.close();
    }
}
