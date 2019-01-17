package com.zmm.twserverpc_zbd.server;

import com.alibaba.fastjson.JSON;
import com.zmm.twserverpc_zbd.client.model.ActiveModel;
import com.zmm.twserverpc_zbd.client.model.PassiveModel;

import java.util.HashMap;
import java.util.Map;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpContent;
import io.netty.util.CharsetUtil;

@ChannelHandler.Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private ServerReadListener mServerReadListener;

    private Map<String,ChannelHandlerContext> mDeviceMaps = new HashMap<>();

    public interface ServerReadListener{
        void onServerReadListener(String msg);

//        void onDeviceConnectListener(String deviceId,ChannelHandlerContext ctx);

        void onDeviceUnconnect(String deviceId);

        void onDeviceconnect( Map<String,ChannelHandlerContext> map);
    }

    public void setServerReadListener(ServerReadListener serverReadListener) {
        mServerReadListener = serverReadListener;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        if (msg instanceof HttpContent) {

            HttpContent content = (HttpContent) msg;
            ByteBuf buf = content.content();
            String data = buf.toString(CharsetUtil.UTF_8).substring(12);

            if(mServerReadListener != null && data.contains("{") && data.contains("}")){

                mServerReadListener.onServerReadListener(data);

                String deviceId = null;

                if (data.contains("passiveMileage")){
                    PassiveModel passiveModel = JSON.parseObject(data, PassiveModel.class);
                    deviceId = passiveModel.getLoginId();


                }else if (data.contains("activeMileage")){
                    ActiveModel activeModel = JSON.parseObject(data, ActiveModel.class);
                    deviceId = activeModel.getLoginId();

                }

                ChannelHandlerContext channelHandlerContext = mDeviceMaps.get(deviceId);

                if(channelHandlerContext == null || !ctx.equals(channelHandlerContext)){

                    System.out.println("netty连接成功11111111：：ctx = "+ctx);

                    mDeviceMaps.put(deviceId,ctx);

                    mServerReadListener.onDeviceconnect(mDeviceMaps);

                }


            }
            buf.release();

        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("netty连接成功222222222：：ctx = "+ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        System.out.println("netty连接断开：：ctx = "+ctx);

        if(mDeviceMaps != null && mDeviceMaps.size() > 0){

            for(Map.Entry<String,ChannelHandlerContext> entry : mDeviceMaps.entrySet()){
                String key = entry.getKey();
                ChannelHandlerContext value = entry.getValue();

                mDeviceMaps.remove(key);

                if(value.equals(ctx)){

                    if(mServerReadListener != null){
                        mServerReadListener.onDeviceUnconnect(key);
                    }

                }
            }

        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
//        System.out.println("channelReadComplete：：ctx = "+ctx);

        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println(cause.getMessage());
        System.out.println("exceptionCaught：：ctx = "+ctx);

        ctx.close();
    }

}