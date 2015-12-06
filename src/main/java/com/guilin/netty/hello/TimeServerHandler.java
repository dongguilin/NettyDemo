package com.guilin.netty.hello;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;

/**
 * Created by hadoop on 2015/12/6.
 * Netty时间服务器服务端IO事件处理类
 * IO事件处理类，作用类似于Reactor模式中的Handler类，主要用于处理网络I/O事件，例如记录日志，对消息进行编解码等
 */
public class TimeServerHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);//将缓冲区中的可读字节数组复制到byte数组中
        String body = new String(req, "UTF-8");
        System.out.println("The time server receive order:" + body);
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.write(resp);//异步发送应答消息给客户端
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //将消息发送队列中的消息写入到SocketChannel中发送给对方
        /*从性能角度考虑，为了防止频繁地唤醒Selector进行消息发送，Netty的write方法并有直接将消息写入SocketChannel中，
        调用write方法只是把待发送的消息放到发送缓冲数组中，再通过调用flush方法，将发送缓冲区中的消息全部写到SocketChannel中
        */
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

}
