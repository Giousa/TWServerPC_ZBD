package com.zmm.twserverpc_zbd.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpContent;
import io.netty.util.CharsetUtil;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private ServerReadListener mServerReadListener;

    public interface ServerReadListener{
        void onServerReadListener(String msg);
    }

    public void setServerReadListener(ServerReadListener serverReadListener) {
        mServerReadListener = serverReadListener;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {

        if (msg instanceof HttpContent) {

            HttpContent content = (HttpContent) msg;
            ByteBuf buf = content.content();
            String data = buf.toString(CharsetUtil.UTF_8).substring(12);

            if(mServerReadListener != null){
                mServerReadListener.onServerReadListener(data);
            }
            buf.release();

        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println(cause.getMessage());
        ctx.close();
    }

}