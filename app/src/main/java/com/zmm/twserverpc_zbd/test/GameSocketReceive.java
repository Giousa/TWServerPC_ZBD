package com.zmm.twserverpc_zbd.test;

import com.alibaba.fastjson.JSON;
import com.zmm.twserverpc_zbd.socket.SocketMsg;
import com.zmm.twserverpc_zbd.socket.SocketRelation;
import com.zmm.twserverpc_zbd.socket.SocketStatus;
import com.zmm.twserverpc_zbd.utils.ThreadUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Description:
 * Author:zhangmengmeng
 * Date:2019/1/18
 * Email:65489469@qq.com
 */
public class GameSocketReceive {

    private static int index = 0;

    //游戏-PC  socket通信
    private static DatagramSocket mDatagramSocket;

    public static void main(String[] args) throws IOException {

        // 创建接收端Socket对象
        DatagramSocket ds = new DatagramSocket(12008);

        // 创建发送端Socket对象
        mDatagramSocket = new DatagramSocket();

        sendDataToPC();

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

    private static void sendDataToPC() {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                index++;

                //每隔10s，反馈消息
                if(index%290 == 0){
                    sendSocketDataToGame("gameover");//游戏正常结束
                }else if(index%11 == 0){
                    sendSocketDataToGame("exit");//游戏退出
                }else if(index%300 == 0){
                    sendSocketDataToGame("start");//游戏开始
                }else if(index == 10){
                    sendSocketDataToGame("start");//游戏开始
                }
            }
        }, 0, 1000);

    }


    /**
     * 发送数据到PC
     * @param msg
     */
    private static void sendSocketDataToGame(String msg){

        System.out.println("---------开始发送数据到PC---------");

        try {
            byte[] bys = msg.getBytes();

            DatagramPacket dp = new DatagramPacket(bys, bys.length, InetAddress.getByName("127.0.0.1"), 12009);

            // 发送数据
            mDatagramSocket.send(dp);

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
