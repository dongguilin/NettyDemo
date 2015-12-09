package com.guilin.netty.msgpack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

/**
 * Created by hadoop on 2015/12/6.
 * MessagePack编码器
 */
public class MsgpackEncoder extends MessageToByteEncoder<Object> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        //将Object类型的POJO对象编码为byte数组，然后写入到ByteBuf中
        MessagePack msgPack = new MessagePack();
        byte[] raw = msgPack.write(msg);
        out.writeBytes(raw);
    }
}
