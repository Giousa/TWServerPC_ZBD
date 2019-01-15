package com.zmm.twserverpc_zbd.server;

import com.alibaba.fastjson.JSON;
import com.zmm.twserverpc_zbd.client.model.ActiveModel;
import com.zmm.twserverpc_zbd.client.model.PassiveModel;
import com.zmm.twserverpc_zbd.client.model.ReportModel;
import com.zmm.twserverpc_zbd.utils.DateUtil;
import com.zmm.twserverpc_zbd.utils.MyOkHttpUtils;
import com.zmm.twserverpc_zbd.utils.ThreadUtils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * Description:
 * Author:zhangmengmeng
 * Date:2017/8/3
 * Time:下午5:02
 */

public class Server {

    private static List<String> mDataList = new ArrayList<>();
    private static long sPreTime;
    private static int mPassiveMil;
    private static int mActiveMil;
    private static byte mSpasmLevel = 1;
    private static boolean isPause = false;
    private static long mPreTime;


    //计算汇总数据参数
    private static boolean isStart = false;
    private static boolean isOver = false;
    private static long mStartTime;
    private static long mEndTime;
    //数据汇总
    private static Map<String,List<String>> mAllMaps = new HashMap<>();


    public static void main(String[] args) {

        mStartTime = System.currentTimeMillis();

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(4);

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new HttpResponseEncoder());
                            ch.pipeline().addLast(new HttpRequestDecoder());

                            ServerHandler serverHandler = new ServerHandler();
                            serverHandler.setServerReadListener(new ServerHandler.ServerReadListener() {
                                @Override
                                public void onServerReadListener(String msg) {
                                	System.out.println("客户端消息:"+msg);
                                    isOver = false;
                                    mDataList.add(msg);
                                }
                            });
                            ch.pipeline().addLast(serverHandler);
                        }
                    });

            Channel ch = bootstrap.bind(8844).sync().channel();
            sendData();

            System.out.println("服务器开启:");
            ch.closeFuture().sync();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    private static void sendData() {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                mPreTime = System.currentTimeMillis();
                if(mDataList.size() > 0){
                    parseJson(mDataList.get(0));
                    mDataList.remove(0);
                    if (mDataList.size() > 50) {
    					mDataList.clear();
    				}
                }
                
            }
        }, 100, 100);

    }


    private static void parseJson(String data) {

        if (data.contains("maxSpeed") || data.contains("maxResistance")) {
            mEndTime = System.currentTimeMillis();
            gameOver();
        } else if (data.contains("passiveMileage")) {
            passiveModel(data);
            if(!isPause){
                sendUDPDataUnPause();
            }
        } else if (data.contains("activeMileage")) {
            activeModel(data);
            if(!isPause){
                sendUDPDataUnPause();
            }
        } else if (data.contains("beginTime")) {
            isPause = true;
            gamePause();
        }
    }
    
    
    private static int spasmLevel = 1;
    
    private static void passiveModel(String msg) {
        if (msg.contains("{") && msg.contains("spasmLevel") && msg.contains("passiveMileage")  &&  msg.contains("curResistance") && msg.contains("curSpeed") && msg.contains("}")) {
            PassiveModel passiveModel = JSON.parseObject(msg, PassiveModel.class);
            byte flag = 1;
            byte speed = (byte) Integer.parseInt(passiveModel.getCurSpeed());
            byte resistance = (byte) Integer.parseInt(passiveModel.getCurResistance());
            byte spasm = (byte) Integer.parseInt(passiveModel.getSpasmTimes());
            byte level = (byte) Integer.parseInt(passiveModel.getSpasmLevel());
            spasmLevel = Integer.parseInt(passiveModel.getSpasmLevel());
            int cal = (int) (Double.parseDouble(passiveModel.getCalories()) * 1000);
            int mil = (int) (Double.parseDouble(passiveModel.getPassiveMileage()) * 1000);
            byte[] id = strToByteArray(passiveModel.getLoginId());
            if(speed < 0){
                speed = (byte) (speed+128);
            }
            mPassiveMil = mil;
            mSpasmLevel = level;
            byte offset = 0;
            int v = 5000;

            isPause = false;

            //发送游戏
            sendUDPData(flag, speed, resistance, spasm, offset, cal, mil, v,id);

            //发送服务器
//            sendYunData(passiveModel.getS_id(),1,Integer.parseInt(passiveModel.getCurSpeed()),
//            		Integer.parseInt(passiveModel.getCurResistance()),Integer.parseInt(passiveModel.getSpasmTimes()),
//            		spasmLevel,0,0,1);


            //存储
            List<String> strings = mAllMaps.get(passiveModel.getLoginId());
            if(strings == null){
                strings = new ArrayList<>();
            }

            strings.add(msg);
            mAllMaps.put(passiveModel.getLoginId(),strings);
        }
    }

 

	private static void activeModel(String msg) {
        if (msg.contains("{") && msg.contains("offset") && msg.contains("activeMileage") && msg.contains("curSpeed") && msg.contains("}")) {
            ActiveModel activeModel = JSON.parseObject(msg, ActiveModel.class);
            byte flag = 0;
            byte speed = (byte) Integer.parseInt(activeModel.getCurSpeed());
            byte resistance = (byte) (Integer.parseInt(activeModel.getCurResistance())+1);
            byte spasm = (byte) Integer.parseInt(activeModel.getSpasmTimes());
            int cal = (int) (Double.parseDouble(activeModel.getCalories()) * 1000);
            int mil = (int) (Double.parseDouble(activeModel.getActiveMileage()) * 1000);
            byte[] id = strToByteArray(activeModel.getLoginId());
            if(speed < 0){
                speed = (byte) (speed+128);
            }
            mActiveMil = mil;
            byte offset = Byte.parseByte(activeModel.getOffset());
            int v = (15 - offset) * 10000 / 30;

            isPause = false;

            //发送游戏
            sendUDPData(flag, speed, resistance, spasm, offset, cal, mil, v,id);

            //发送服务器
//            sendYunData(activeModel.getS_id(),0,Integer.parseInt(activeModel.getCurSpeed()),
//            		Integer.parseInt(activeModel.getCurResistance()),Integer.parseInt(activeModel.getSpasmTimes()),
//            		spasmLevel,Integer.parseInt(activeModel.getOffset()),0,1);

            //存储
            List<String> strings = mAllMaps.get(activeModel.getLoginId());
            if(strings == null){
                strings = new ArrayList<>();
            }

            strings.add(msg);
            mAllMaps.put(activeModel.getLoginId(),strings);
        }
    }
	
   private static void sendYunData(final String s_id, final int flag, final int curSpeed, final int curResistance, final int spasmTimes, final int spasticity,
                                   final int offset, final int curDirection, final int smartMode) {
	   	
//	   ThreadUtils.runOnBackgroundThread(new Runnable() {
//			@Override
//			public void run() {
//
//            }
//	    });

       MyOkHttpUtils.sendYunData(s_id, flag, curSpeed, curResistance, spasmTimes, spasticity, offset, curDirection, smartMode);



   }

    private static void gamePause() {
        sendUDPDataPause();
    }

    private static void gameOver() {
        sendUDPDataEnd();
        ThreadUtils.runOnBackgroundThread(new Runnable() {
            @Override
            public void run() {
                reportBuild();
            }
        });
    }

    /**
     * 发送主动或被动数据
     * @param flag
     * @param speed
     * @param resistance
     * @param spasm
     * @param offset
     * @param cal
     * @param mil
     * @param left
     * @param id
     */
    private static void sendUDPData(byte flag, byte speed, byte resistance, byte spasm, byte offset,
                             int cal, int mil, int left,byte[] id) {

        DatagramSocket ds;
        DatagramPacket dp = null;
        byte[] bys;
        try {
            ds = new DatagramSocket();
            byte[] calBytes = intToByte(cal);
            byte[] milBytes = intToByte(mPassiveMil + mActiveMil);
            byte[] leftBytes = intToByte(left);
            byte[] time = intToByte(0);
            bys = new byte[]{100, 101, flag, speed, resistance, spasm, mSpasmLevel, offset,
                    calBytes[0], calBytes[1], calBytes[2], calBytes[3],
                    milBytes[0], milBytes[1], milBytes[2], milBytes[3],
                    leftBytes[0], leftBytes[1], leftBytes[2], leftBytes[3],
                    time[0], time[1], time[2], time[3],id[0],id[1],id[2],id[3],
                    id[4],id[5],id[6],id[7],id[8],id[9],id[10],id[11]};
            dp = new DatagramPacket(bys, bys.length, InetAddress.getByName("127.0.0.1"), 12346);
            ds.send(dp);
            ds.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂停
     */
    private static void sendUDPDataPause() {
        DatagramSocket ds;
        DatagramPacket dp = null;
        byte[] bys;
        try {

            ds = new DatagramSocket();
            bys = new byte[]{105, 106, 0, 0, 0,
                    0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0,
                    0, 0, 0, 0};
            dp = new DatagramPacket(bys, bys.length, InetAddress.getByName("127.0.0.1"), 12346);
            ds.send(dp);
            ds.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消暂停
     */
    private static void sendUDPDataUnPause() {
//        DatagramSocket ds;
//        DatagramPacket dp = null;
//        byte[] bys;
//        try {
//
//            ds = new DatagramSocket();
//            bys = new byte[]{102, 103, 0, 0, 0,
//                    0, 0, 0, 0, 0,
//                    0, 0, 0, 0, 0,
//                    0, 0, 0, 0, 0,
//                    0, 0, 0, 0};
////            System.out.println("Server发送UnPause数据：" + Arrays.toString(bys));
//            dp = new DatagramPacket(bys, bys.length, InetAddress.getByName("127.0.0.1"), 12346);
//            ds.send(dp);
//            ds.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 结束
     */
    private static void sendUDPDataEnd() {
        DatagramSocket ds;
        DatagramPacket dp = null;
        byte[] bys;
        try {
            ds = new DatagramSocket();
            bys = new byte[]{110, 111, 0, 0, 0,
                    0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0,
                    0, 0, 0, 0};
            dp = new DatagramPacket(bys, bys.length, InetAddress.getByName("127.0.0.1"), 12346);
            ds.send(dp);
            ds.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * int到字节数组的转换.
     */
    public static byte[] intToByte(int number) {
        int temp = number;
        byte[] b = new byte[4];
        for (int i = 0; i < b.length; i++) {
            b[i] = new Integer(temp & 0xff).byteValue();// 将最低位保存在最低位
            temp = temp >> 8;// 向右移8位
        }
        return b;
    }


    public static byte[] strToByteArray(String str) {
        if (str == null) {
            return null;
        }
        byte[] byteArray = str.getBytes();
        return byteArray;
    }


    /**
     * 生成报告
     */
    private static void reportBuild() {

        if(mAllMaps != null && mAllMaps.size() > 0){
            for (Map.Entry<String, List<String>> entry : mAllMaps.entrySet()) {
                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

                String deviceId = entry.getKey();
                List<String> stringList = entry.getValue();

                if(stringList == null || stringList.size() == 0){
                    continue;
                }

                float cal = 0;

                float mileage = 0;
                float passiveMil = 0;
                float activeMil = 0;

                float timeAngle = 0;
                float mileageAngle = 0;

                int totalTime = 0;
                int activeTime = 0;
                int passiveTime = 0;

                int totalSpeed = 0;
                int minSpeed = 0;
                int maxSpeed = 0;
                int avgSpeed = 0;

                int totalResistance = 0;
                int minResistance = 0;
                int maxResistance = 0;
                int avgResistance = 0;

                int spasmLevel = 0;

                int spasmCount = 0;

                ArrayList<Integer> speedRate = new ArrayList<>();

                for (String msg:stringList) {

                    if(msg.contains("passiveMileage")){
                        PassiveModel passiveModel = JSON.parseObject(msg, PassiveModel.class);
                        int speedValue = Integer.parseInt(passiveModel.getCurSpeed());
                        //累加速度
                        totalSpeed += speedValue;
                        speedRate.add(speedValue);
                        //被动里程
                        passiveMil += speedValue * 5.00f / 36;
                        //被动时间
                        passiveTime++;
                        //被动阻力 0

                        //最高速度
                        if(maxSpeed > speedValue){
                            maxSpeed = speedValue;
                        }

                        //最低速度

                        if(minSpeed < speedValue){
                            minSpeed = speedValue;
                        }

                        //痉挛次数
                        spasmCount = Integer.parseInt(passiveModel.getSpasmTimes());
                        //痉挛等级
                        spasmLevel = Integer.parseInt(passiveModel.getSpasmLevel());


                    }else {

                        ActiveModel activeModel = JSON.parseObject(msg, ActiveModel.class);
                        int speedValue = Integer.parseInt(activeModel.getCurSpeed());
                        totalSpeed += speedValue;
                        speedRate.add(speedValue);
                        activeMil += speedValue * 5.00f / 36;
                        activeTime++;
                        int resistance = (Integer.parseInt(activeModel.getCurResistance()) + 1);
                        totalResistance += resistance;

                        //最高速度
                        if(maxSpeed > speedValue){
                            maxSpeed = speedValue;
                        }

                        //最低速度

                        if(minSpeed < speedValue){
                            minSpeed = speedValue;
                        }
                        //最高阻力
                        if(maxResistance > resistance){
                            maxResistance = resistance;
                        }

                        //最低阻力
                        if(minResistance < resistance){
                            maxResistance = resistance;
                        }

                        //痉挛次数
                        spasmCount = Integer.parseInt(activeModel.getSpasmTimes());

                    }


                }



                //总时间
                totalTime = passiveTime + activeTime;

                //总里程
                mileage = passiveMil + activeMil;

                //时间角度
                if(passiveTime == 0 && activeTime != 0){
                    timeAngle = 360f;
                }else if(passiveTime != 0 && activeTime == 0){
                    timeAngle = 0f;
                }else if(passiveTime != 0 && activeTime != 0){
                    timeAngle = activeTime * 1.00f * 360 /totalTime;
                }

                //里程角度
                if(passiveMil == 0 && activeMil != 0){
                    mileageAngle = 360f;
                }else if(passiveMil != 0 && activeMil == 0){
                    mileageAngle = 0f;
                }else if(passiveMil != 0 && activeMil != 0){
                    mileageAngle = activeMil * 1.00f * 360 /mileage;
                }

                //卡路里
                cal = 60.00f * totalSpeed / 10800;

                //平均速度
                avgSpeed = totalSpeed / totalTime;
                //平均阻力
                avgResistance = totalResistance / totalTime;

                ReportModel reportModel = new ReportModel();

                //设备id
                reportModel.setDevice_id(deviceId);
                //用户id
                reportModel.setUser_id(UUID.randomUUID().toString());

                int year = 0;
                int month = 0;

                try {
                    System.out.println("mStartTime = "+mStartTime);
                    year = Integer.parseInt(DateUtil.longToString(mStartTime,"yyyy"));
                    month = Integer.parseInt(DateUtil.longToString(mStartTime,"MM"));

                    reportModel.setBeginTime(DateUtil.longToString(mStartTime,"yyyy-MM-dd HH:mm:ss"));
                    reportModel.setEndTime(DateUtil.longToString(mEndTime,"yyyy-MM-dd HH:mm:ss"));

                } catch (ParseException e) {
                    System.out.println("转换异常");
                    e.printStackTrace();
                }

                reportModel.setYear(year);
                reportModel.setMonth(month);
                reportModel.setTotalTime(totalTime);
                reportModel.setActiveTime(activeTime);
                reportModel.setPassiveTime(passiveTime);
                reportModel.setActiveMileage(activeMil);
                reportModel.setPassiveMileage(passiveMil);
                reportModel.setTimeAngle(timeAngle);
                reportModel.setMileageAngle(mileageAngle);
                reportModel.setSpasmTimes(spasmCount);
                reportModel.setSpasmLevel(spasmLevel);
                reportModel.setTotalCal(cal+"");
                reportModel.setMinSpeed(minSpeed);
                reportModel.setAvgSpeed(avgSpeed);
                reportModel.setMaxSpeed(maxSpeed);
                reportModel.setMinResistance(minResistance);
                reportModel.setAvgResistance(avgResistance);
                reportModel.setMaxResistance(maxResistance);
                reportModel.setSpeedRate(JSON.toJSONString(speedRate));


                MyOkHttpUtils.sendReport(reportModel);

            }
        }


        isOver = true;

    }
}
