package com.zmm.twserverpc_zbd.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zmm.twserverpc_zbd.client.model.ActiveModel;
import com.zmm.twserverpc_zbd.client.model.JigouIdsModel;
import com.zmm.twserverpc_zbd.client.model.JigouModel;
import com.zmm.twserverpc_zbd.client.model.MessageModel;
import com.zmm.twserverpc_zbd.client.model.PassiveModel;
import com.zmm.twserverpc_zbd.client.model.PauseModel;
import com.zmm.twserverpc_zbd.client.model.RelationModel;
import com.zmm.twserverpc_zbd.client.model.ReportModel;
import com.zmm.twserverpc_zbd.utils.DateUtil;
import com.zmm.twserverpc_zbd.utils.MyOkHttpUtils;
import com.zmm.twserverpc_zbd.utils.ThreadUtils;
import com.zzwloves.netty.websocket.CloseStatus;
import com.zzwloves.netty.websocket.TextMessage;
import com.zzwloves.netty.websocket.WebSocketMessage;
import com.zzwloves.netty.websocket.WebSocketSession;
import com.zzwloves.netty.websocket.client.WebSocketClient;
import com.zzwloves.netty.websocket.handler.WebSocketHandler;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
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
    private static boolean isExit = false;
    private static long mStartTime;
    private static long mEndTime;

    //总里程
    private static Map<String,Float> mTotalMilMap = new HashMap<>();
    //总卡路里
    private static Map<String,Float> mTotalCalMap = new HashMap<>();


    //数据汇总
    private static Map<String,List<String>> mAllMaps = new HashMap<>();
    private static WebSocketClient mSocketClient;
    private static MySocketHandler mMySocketHandler;

    //websocket
    private static WebSocketSession mWebSocketSession;

    //存储连接数组
    private static Map<String,ChannelHandlerContext> mDeviceMaps = new HashMap<>();

    //推送  待运行设备关联信息
    private static List<RelationModel.DataBean> mDataBeanList;

    //PC-游戏  socket通信
    private static DatagramSocket mDatagramSocket;


    public static void main(String[] args) throws Exception{

        mStartTime = System.currentTimeMillis();

        String canonicalPath3 = new File("..").getCanonicalPath();
        //机构名称和服务名称
        File file = new File(canonicalPath3+"/jigou.json");
        String content= FileUtils.readFileToString(file,"UTF-8");
        JigouModel jigouModel = JSONObject.parseObject(content, JigouModel.class);

        //机构id和服务id
        File file2 = new File(canonicalPath3+"/id.json");
        String contentId= FileUtils.readFileToString(file2,"UTF-8");
        JigouIdsModel jigouIdsModel = JSONObject.parseObject(contentId, JigouIdsModel.class);

        final String url = "ws://172.28.6.73:8080/websocket?type=device&deviceType=activePassiveServer&orgId="
                +jigouIdsModel.getOrgId()+"&pcServerId="+jigouIdsModel.getPcServerId()+"&pcServerName="+jigouModel.getPcServerName();

        System.out.println("长连接url = "+url);

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(4);

        //TODO  正式服 删除
        isStart = true;

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

                                    if(isStart){
                                        System.out.println("客户端消息:"+msg);
                                        mDataList.add(msg);
                                    }
                                }

                                @Override
                                public void onDeviceUnconnect(String deviceId) {
                                    System.out.println("Server::onDeviceUnconnect 断开id = "+deviceId);

                                    mDeviceMaps.remove(deviceId);

                                    sendDeviceOffline(deviceId);
                                }

                                @Override
                                public void onDeviceconnect(String deviceId,ChannelHandlerContext ctx) {

                                    System.out.println("Server::onDeviceconnect 在线id = "+deviceId);

                                    mDeviceMaps.put(deviceId,ctx);

                                    sendDeviceSingleOnline(deviceId);

                                }

                                @Override
                                public void onDeviceconnect(Map<String, ChannelHandlerContext> map) {
//                                    System.out.println("Server::onDeviceconnect 在线id = "+map);
//
//                                    mDeviceMaps = map;
//
//                                    //TODO 在发送在线前，判断是否游戏中
//                                    sendDeviceListOnline();

                                }
                            });

                            ch.pipeline().addLast(serverHandler);
                        }
                    });

            Channel ch = bootstrap.bind(8844).sync().channel();

            // 创建发送端Socket对象
            mDatagramSocket = new DatagramSocket();
            System.out.println("mDatagramSocket = "+mDatagramSocket);

            //计时发送数据
            sendData();

            //websocket长连接
            ThreadUtils.runOnBackgroundThread(new Runnable() {
                @Override
                public void run() {
                    mMySocketHandler = new MySocketHandler();

                    mSocketClient = new WebSocketClient(url,null,mMySocketHandler);

                    reconnectWebsocket();
                }
            });

            ThreadUtils.runOnBackgroundThread(new Runnable() {
                @Override
                public void run() {

                    receiveGameData();
                }
            });

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

        //mRunningDevices  对推送数据进行解析判断，只有满足id时才通过

//        if(mDataBeanList != null && mDataBeanList.size() > 0){
//            for (int i = 0; i < mDataBeanList.size(); i++) {
//                String id = mDataBeanList.get(i).getDeviceId();
//                if(data.contains("id")){
//                    //执行
//                }
//            }
//        }


        if (data.contains("{") && data.contains("}") && data.contains("passiveMileage")) {
            passiveModel(data);

        } else if (data.contains("{") && data.contains("}") && data.contains("activeMileage")) {
            activeModel(data);

        } else if (data.contains("activeDuration")) {
            gameOver(data.substring(12,24));

        } else if (data.contains("{") && data.contains("}") && data.contains("beginTime")) {
            gamePause(data);
        }
    }
    
    
    private static void passiveModel(String msg) {

        PassiveModel passiveModel = JSON.parseObject(msg, PassiveModel.class);

        String deviceId = passiveModel.getLoginId();
        int speed = Integer.parseInt(passiveModel.getCurSpeed());

        //里程
        int totalTime = (int) ((System.currentTimeMillis() - mStartTime)/1000);
        System.out.println("总时间： totalTime = "+totalTime);

        Float milValue = mTotalMilMap.get(deviceId);
        if(milValue == null){
            milValue = 0.0f;
        }
        float mil = speed * 0.1f * 1000 / 3600;
        milValue += mil;
        mTotalMilMap.put(deviceId,milValue);

        //卡路里
        float cal = speed * 0.2f/300;


        Float calValue = mTotalCalMap.get(deviceId);
        if(calValue == null){
            calValue = 0.0f;
        }
        calValue += cal;
        mTotalCalMap.put(deviceId,calValue);


        byte[] head = {100, 101};
        byte[] typeBytes = intToByte(1);
        byte[] speedBytes = intToByte(speed);
        byte[] resistanceBytes = intToByte(Integer.parseInt(passiveModel.getCurResistance()));
        byte[] offsetBytes = intToByte(0);
        byte[] spasmTimesBytes = intToByte(Integer.parseInt(passiveModel.getSpasmTimes()));
        byte[] spasmLevelBytes = intToByte(Integer.parseInt(passiveModel.getSpasmLevel()));
        byte[] milBytes = intToByte((int)mil);
        byte[] calBytes = intToByte((int) cal);


        byte[] bytes = byteMergerAll(head, deviceId.getBytes(), typeBytes, speedBytes, resistanceBytes, offsetBytes, spasmTimesBytes, spasmLevelBytes,milBytes,calBytes);

        sendSocketBytesToGame(bytes);


        //存储
        List<String> strings = mAllMaps.get(passiveModel.getLoginId());
        if(strings == null){
            strings = new ArrayList<>();
        }

        strings.add(msg);
        mAllMaps.put(passiveModel.getLoginId(),strings);
    }

 

	private static void activeModel(String msg) {
        //发送服务器
//            sendYunData(activeModel.getS_id(),0,Integer.parseInt(activeModel.getCurSpeed()),
//            		Integer.parseInt(activeModel.getCurResistance()),Integer.parseInt(activeModel.getSpasmTimes()),
//            		spasmLevel,Integer.parseInt(activeModel.getOffset()),0,1);


        //发送数据到游戏 v2.0
        ActiveModel activeModel = JSON.parseObject(msg, ActiveModel.class);
        String deviceId = activeModel.getLoginId();
        int speed = Integer.parseInt(activeModel.getCurSpeed());

        //里程
        int totalTime = (int) ((System.currentTimeMillis() - mStartTime)/1000);
        System.out.println("总时间： totalTime = "+totalTime);

        Float milValue = mTotalMilMap.get(deviceId);
        if(milValue == null){
            milValue = 0.0f;
        }
        float mil = speed * 0.1f * 1000 / 3600;
        milValue += mil;
        mTotalMilMap.put(deviceId,milValue);

        //卡路里
        int resistance = Integer.parseInt(activeModel.getCurResistance());
        if(resistance == 0){
            resistance = 1;
        }
        float resParam = resistance * 1.00f / 3.0f;
        float cal = speed * 0.2f * resParam/60;

        Float calValue = mTotalCalMap.get(deviceId);
        if(calValue == null){
            calValue = 0.0f;
        }
        calValue += cal;
        mTotalCalMap.put(deviceId,calValue);


        byte[] head = {100, 101};
        byte[] typeBytes = intToByte(0);
        byte[] speedBytes = intToByte(speed);
        byte[] resistanceBytes = intToByte(Integer.parseInt(activeModel.getCurResistance()));
        byte[] offsetBytes = intToByte(Integer.parseInt(activeModel.getOffset()));
        byte[] spasmTimesBytes = intToByte(Integer.parseInt(activeModel.getSpasmTimes()));
        byte[] spasmLevelBytes = intToByte(0);
        byte[] milBytes = intToByte((int)mil);
        byte[] calBytes = intToByte((int) cal);


        byte[] bytes = byteMergerAll(head, deviceId.getBytes(), typeBytes, speedBytes, resistanceBytes, offsetBytes, spasmTimesBytes, spasmLevelBytes,milBytes,calBytes);

        sendSocketBytesToGame(bytes);



        //存储
        List<String> strings = mAllMaps.get(activeModel.getLoginId());
        if(strings == null){
            strings = new ArrayList<>();
        }

        strings.add(msg);
        mAllMaps.put(activeModel.getLoginId(),strings);
    }



    private static void gamePause(String data) {

        PauseModel pauseModel = JSON.parseObject(data, PauseModel.class);
//        SocketStatus socketStatus = new SocketStatus();
//        socketStatus.setId(pauseModel.getLoginId());
//        socketStatus.setStatus(0);
//
//        sendSocketDataToGame(JSON.toJSONString(socketStatus));
        byte[] head = {105, 106};
        byte[] statusBytes = intToByte(0);

        byte[] bytes = byteMergerAll(head, pauseModel.getLoginId().getBytes(), statusBytes);

        System.out.println("服务端 暂停 原始数据 = " + Arrays.toString(bytes));
        System.out.println("服务端 暂停 设备id = " + pauseModel.getLoginId());

        sendSocketBytesToGame(bytes);
    }

    private static void gameOver(String id) {

//        SocketStatus socketStatus = new SocketStatus();
//        socketStatus.setId(id);
//        socketStatus.setStatus(1);
//
//        sendSocketDataToGame(JSON.toJSONString(socketStatus));

        byte[] head = {110, 111};
        byte[] statusBytes = intToByte(0);

        byte[] bytes = byteMergerAll(head, id.getBytes(), statusBytes);

        System.out.println("服务端 停止 原始数据 = " + Arrays.toString(bytes));
        System.out.println("服务端 停止 设备id = " + id);


        sendSocketBytesToGame(bytes);


        //TODO 模拟结束，后期需修订
//        ThreadUtils.runOnBackgroundThread(new Runnable() {
//            @Override
//            public void run() {
//                reportBuild();
//            }
//        });
    }


    /**
     * 发送数据到游戏   关联数据、主动数据、被动数据、暂停数据、停止数据
     * @param msg
     */
    private static void sendSocketDataToGame(String msg){

        System.out.println("---------开始发送数据到游戏---------");

        try {
            byte[] bys = msg.getBytes();

            DatagramPacket dp = new DatagramPacket(bys, bys.length, InetAddress.getByName("127.0.0.1"), 12008);

            // 发送数据
            mDatagramSocket.send(dp);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 发送数据到游戏   关联数据、主动数据、被动数据、暂停数据、停止数据
     *
     * @param bytes
     */
    private static void sendSocketBytesToGame(byte[] bytes) {

        System.out.println("---------服务端 ===》 发送数据 ==》 客户端---------");

        try {

            DatagramPacket dp = new DatagramPacket(bytes, bytes.length, InetAddress.getByName("127.0.0.1"), 12008);

            // 发送数据
            mDatagramSocket.send(dp);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 获取游戏返回数据
     */
    private static void receiveGameData() {

        try {
            byte[] receive = new byte[1024];

            while(true){
                Socket sock = new Socket("127.0.0.1", 12009);

                sock.getOutputStream().write("aa".getBytes());

                String data;
                sock.getInputStream().read(receive);
                data = new String(receive);
                System.out.println("服务端接收客户端发来的游戏状态: " + data);


                switch (data){
                    case "gameover":
                        isStart = false;
                        System.out.println("服务端：游戏结束");
//                        reportBuild();

                        break;

                    case "exit":
                        System.out.println("服务端：游戏退出");

                        break;

                    case "start":
                        System.out.println("服务端：游戏开始");

                        mStartTime = System.currentTimeMillis();
                        isStart = true;
                        break;

                    case "ready":
                        System.out.println("服务端：游戏准备");

                        mStartTime = System.currentTimeMillis();
                        isStart = true;
                        break;
                }

                //通知客户端，不需要继续发送了
                sock.getOutputStream().write("ping".getBytes());

                sock.close();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
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

        //游戏结束，将推送数据置空
        mDataBeanList = null;

        mEndTime = System.currentTimeMillis();


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
                reportModel.setProvenance(2);
                reportModel.setType(1);


                MyOkHttpUtils.sendReport(reportModel);

            }
        }

    }

    /**
     * 长连接
     */
    public static class MySocketHandler implements WebSocketHandler {


        @Override
        public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
            System.out.println("websocket ：： 连接成功");
            mWebSocketSession = webSocketSession;

            sendDeviceListOnline();

        }

        @Override
        public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
            System.out.println("接收数据：：websocket ：： 消息处理");

            TextMessage textMessage = (TextMessage) webSocketMessage;
            String payload = textMessage.getPayload();

            RelationModel relationModel = JSONObject.parseObject(payload, RelationModel.class);
            System.out.println("接收数据：：relationModel = "+relationModel);

            //每次推送，之前存储数据置空
            mDataBeanList = null;
            mDataBeanList = relationModel.getData();

            //TODO 发送关联数据
            sendRelationData();


        }

        @Override
        public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
            System.out.println("websocket ：： 连接异常");
        }

        @Override
        public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
            System.out.println("websocket ：： 连接失败");
            mWebSocketSession = null;
            reconnectWebsocket();

        }
    }

    /**
     * 发送关联信息数据
     */
    private static void sendRelationData() {

        byte[] head = {97, 98};
        byte[] codeBytes = intToByte(getCode());


        for (int i = 0; i < mDataBeanList.size(); i++) {
            RelationModel.DataBean dataBean = mDataBeanList.get(i);
            String id = dataBean.getDeviceId();
            String patientName = dataBean.getPatientName();
            int length = patientName.getBytes().length;
            byte[] bytes = byteMergerAll(head, codeBytes, id.getBytes(),intToByte(length),patientName.getBytes());

            System.out.println("服务端 关联数据 "+Arrays.toString(bytes));
            sendSocketBytesToGame(bytes);

        }




    }

    /**
     * 上传单个在线设备
     */
    private static void sendDeviceSingleOnline(String deviceId){

        if(mWebSocketSession != null){

            System.out.println("当前单个在线设备："+deviceId);

            MessageModel messageModel = new MessageModel();
            messageModel.setId(UUID.randomUUID().toString());
            messageModel.setCode("2001");
            messageModel.setCreateTime(System.currentTimeMillis());

            List<String> devicesList = new ArrayList<>();
            devicesList.add(deviceId);

            messageModel.setData(devicesList);

            String textMsg = JSON.toJSONString(messageModel);

            System.out.println("发送：：单个在线设备列表 =  "+textMsg);

            TextMessage textMessage = new TextMessage(textMsg);
            try {
                mWebSocketSession.sendMessage(textMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    /**
     * 上传在线设备列表
     */
    private static void sendDeviceListOnline(){

        if(mWebSocketSession != null && mDeviceMaps != null && mDeviceMaps.size() > 0){

            System.out.println("当前在线设备列表："+mDeviceMaps.toString());

            MessageModel messageModel = new MessageModel();
            messageModel.setId(UUID.randomUUID().toString());
            messageModel.setCode("2001");
            messageModel.setCreateTime(System.currentTimeMillis());

            List<String> devicesList = new ArrayList<>();

            for(Map.Entry<String,ChannelHandlerContext> entry : mDeviceMaps.entrySet()){

                //这个情况，是已经推送的情况下。推送前，必须是在线的，说明已经推送过了。
                if(mDataBeanList != null && mDataBeanList.size() > 0){
                    boolean contains = mDataBeanList.contains(entry.getKey());
                    if(!contains){
                        devicesList.add(entry.getKey());
                    }
                }else {
                    devicesList.add(entry.getKey());
                }

            }

            messageModel.setData(devicesList);

            String textMsg = JSON.toJSONString(messageModel);

            System.out.println("发送：：多个在线设备列表 =  "+textMsg);

            TextMessage textMessage = new TextMessage(textMsg);
            try {
                mWebSocketSession.sendMessage(textMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 上传离线设备
     * @param deviceId
     */
    private static void sendDeviceOffline(String deviceId){

        if(mWebSocketSession != null && deviceId != null){

            MessageModel messageModel = new MessageModel();
            messageModel.setId(UUID.randomUUID().toString());
            messageModel.setCode("2003");
            messageModel.setCreateTime(System.currentTimeMillis());

            List<String> devicesList = new ArrayList<>();
            devicesList.add(deviceId);
            messageModel.setData(devicesList);

            String textMsg = JSON.toJSONString(messageModel);

            System.out.println("发送：：离线设备 =  "+textMsg);

            TextMessage textMessage = new TextMessage(textMsg);
            try {
                mWebSocketSession.sendMessage(textMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 上传运行设备列表   游戏start的时候，发送
     */
    private static void sendDeviceListRunning(){

        if(mWebSocketSession != null && mDataBeanList != null && mDataBeanList.size() > 0){

            MessageModel messageModel = new MessageModel();
            messageModel.setId(UUID.randomUUID().toString());
            messageModel.setCode("2002");
            messageModel.setCreateTime(System.currentTimeMillis());

            List<String> devicesList = new ArrayList<>();

            for (int i = 0; i < mDataBeanList.size(); i++) {
                devicesList.add(mDataBeanList.get(i).getDeviceId());
            }
            messageModel.setData(devicesList);

            String textMsg = JSON.toJSONString(messageModel);

            System.out.println("发送：：运行设备列表 =  "+textMsg);

            TextMessage textMessage = new TextMessage(textMsg);
            try {
                mWebSocketSession.sendMessage(textMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 合并字节数组
     * @param values
     * @return
     */
    private static byte[] byteMergerAll(byte[]... values) {
        int length_byte = 0;
        for (int i = 0; i < values.length; i++) {
            length_byte += values[i].length;
        }
        byte[] all_byte = new byte[length_byte];
        int countLength = 0;
        for (int i = 0; i < values.length; i++) {
            byte[] b = values[i];
            System.arraycopy(b, 0, all_byte, countLength, b.length);
            countLength += b.length;
        }
        return all_byte;
    }

    /**
     * 获取随机6位数字
     * @return
     */
    public static int getCode() {

        return (int)((Math.random()*9+1)*100000);
    }


    /**
     * 重连websocket
     */
    private static void reconnectWebsocket(){

        while (true){
            try {
                Thread.sleep(5000);

                mSocketClient.start();

            } catch (Exception e) {
                System.out.println("长连接异常");
                e.printStackTrace();

            }
        }
    }


    /**
     * 发送云端时时数据
     * @param s_id
     * @param flag
     * @param curSpeed
     * @param curResistance
     * @param spasmTimes
     * @param spasticity
     * @param offset
     * @param curDirection
     * @param smartMode
     */
    private static void sendYunData(final String s_id, final int flag, final int curSpeed, final int curResistance, final int spasmTimes, final int spasticity,
                                    final int offset, final int curDirection, final int smartMode) {

        MyOkHttpUtils.sendYunData(s_id, flag, curSpeed, curResistance, spasmTimes, spasticity, offset, curDirection, smartMode);



    }

}
