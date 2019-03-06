package com.zmm.twserverpc_zbd.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Description:
 * Author:zhangmengmeng
 * Date:2019/2/21
 * Email:65489469@qq.com
 */
public class Client2 {


    private static int index = 0;

    //游戏-PC  socket通信
    private static DatagramSocket mDatagramSocket;


    public static void main(String[] args) throws IOException {

        System.out.println("客户端开启：：：：：：：：：");

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

            byte[] bytes = dp.getData();

            parseBytes(bytes);

        }
    }

    private static void sendDataToPC() {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                index++;

                //每隔10s，反馈消息
//                if(index%290 == 0){
//                    sendSocketDataToGame("gameover");//游戏正常结束
//                }else if(index%11 == 0){
//                    sendSocketDataToGame("exit");//游戏退出
//                }else if(index%300 == 0){
//                    sendSocketDataToGame("start");//游戏开始
//                }else if(index == 10){
//                    sendSocketDataToGame("start");//游戏开始
//                }

                if(index%29 == 0){
                    sendSocketDataToGame("gameover");//游戏正常结束
                }else if(index%11 == 0){
                    sendSocketDataToGame("exit");//游戏退出
                }else if(index%30 == 0){
                    sendSocketDataToGame("start");//游戏开始
                }else if(index%15 == 0){
                    sendSocketDataToGame("ready");//游戏准备
                }
            }
        }, 0, 1000);

    }


    /**
     * 发送数据到PC
     * @param msg
     */
    private static void sendSocketDataToGame(String msg){

//        System.out.println("---------客户端 ===》 发送数据 ==》 服务端---------");


        try {
            byte[] bys = msg.getBytes();

            DatagramPacket dp = new DatagramPacket(bys, bys.length, InetAddress.getByName("127.0.0.1"), 12009);

            // 发送数据
            mDatagramSocket.send(dp);

        }catch (Exception e){
            e.printStackTrace();
        }

    }



    /**
     * 根据字节解析数据
     * @param bytes
     */
    private static void parseBytes(byte[] bytes) {

        System.out.println("bytes = "+Arrays.toString(bytes));

        if(bytes[0] == 100 & bytes[1] == 101){

            receiveNormalData(bytes);
        }else if(bytes[0] == 105 & bytes[1] == 106){

            receivePauseData(bytes);
        }else if(bytes[0] == 110 & bytes[1] == 111){

            receiveStopData(bytes);
        }else if(bytes[0] == 97 & bytes[1] == 98){

            receiveRelationData(bytes);

        }

    }

    /**
     * 获取关联数据
     *  [97, 98, 120, -74, 1, 0, 67, 56, 57, 51, 52, 54, 54, 67, 57, 57, 54, 56, 14, 0, 0, 0, -26, -75, -117, -24, -81, -107, -26, -120, -112, -27, -111, -104, 95, 56]
     * @param bytes
     */
    private static void receiveRelationData(byte[] bytes){

        byte[] codeBytes = subBytes(bytes, 2,4);
        byte[] deviceIdBytes = subBytes(bytes,6,12);
        byte[] lengthBytes = subBytes(bytes,18,4);
        byte[] nameBytes = subBytes(bytes,22,byteToInt(lengthBytes));

        int code = byteToInt(codeBytes);
        String deviceId = byteArrayToStr(deviceIdBytes);
        String name = byteArrayToStr(nameBytes);

        String gbk = null;
        try {
            gbk = new String(name.getBytes("utf-8"), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        System.out.println("客户端接收  code = "+code+",设备id = "+deviceId+",患者名称 = "+gbk);
    }

    private static void receiveStopData(byte[] bytes) {
        byte[] deviceIdBytes = subBytes(bytes,2,12);
        String deviceId = byteArrayToStr(deviceIdBytes);
        System.out.println("客户端接收  停止设备id： "+deviceId);

    }

    private static void receivePauseData(byte[] bytes) {
        byte[] deviceIdBytes = subBytes(bytes,2,12);
        String deviceId = byteArrayToStr(deviceIdBytes);
        System.out.println("客户端接收  暂停设备id： "+deviceId);

    }

    /**
     *  deviceId : 设备id号
     *  type: 0 主动   1 被动
     *  speed : 转速
     *  resistance : 阻力
     *  offset:偏移值   0:中间  -1~-15  左边   1~15右边
     *  spasmTimes : 痉挛次数 主动时无效
     *  spasmLevel : 痉挛等级 主动时无效
     *
     * @param bytes
     */
    private static void receiveNormalData(byte[] bytes) {

        byte[] deviceIdBytes = subBytes(bytes,2,12);
        byte[] typeBytes = subBytes(bytes, 14,4);
        byte[] speedBytes = subBytes(bytes,18,4);
        byte[] resistanceBytes = subBytes(bytes,22,4);
        byte[] offsetBytes = subBytes(bytes,26,4);
        byte[] spasmTimesBytes = subBytes(bytes,30,4);
        byte[] spasmLevelBytes = subBytes(bytes,34,4);
        byte[] milBytes = subBytes(bytes,38,4);
        byte[] calBytes = subBytes(bytes,42,4);

        String deviceId = byteArrayToStr(deviceIdBytes);
        int type = byteToInt(typeBytes);
        int speed = byteToInt(speedBytes);
        int resistance = byteToInt(resistanceBytes);
        int offset = byteToInt(offsetBytes);
        int spasmTimes = byteToInt(spasmTimesBytes);
        int spasmLevel = byteToInt(spasmLevelBytes);
        int mil = byteToInt(milBytes);
        int cal = byteToInt(calBytes);


        if(type == 0){
            //主动  此时spasmTimes 和 spasmLevel 无效

        }else {
            //被动  此时 offset 恒等于0

        }



        System.out.println("客户端接收  设备id = "+deviceId+",type = "+type+",speed = "+speed+",resistance = "+resistance+",offset = "+offset+",spasmTimes = "+spasmTimes+",spasmLevel = "+spasmLevel+",mil = "+mil+",cal = "+cal);

    }


    /**
     * 字节数组到int的转换.
     */
    public static int byteToInt(byte[] b) {
        int s = 0;
        int s0 = b[0] & 0xff;// 最低位
        int s1 = b[1] & 0xff;
        int s2 = b[2] & 0xff;
        int s3 = b[3] & 0xff;
        s3 <<= 24;
        s2 <<= 16;
        s1 <<= 8;
        s = s0 | s1 | s2 | s3;
        return s;
    }

    public static String byteArrayToStr(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        String str = new String(byteArray);
        return str;
    }

    /**
     * 从一个byte[]数组中截取一部分
     * @param src
     * @param begin
     * @param count
     * @return
     */
    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        for (int i=begin;i<begin+count; i++) bs[i-begin] = src[i];
        return bs;
    }
}
