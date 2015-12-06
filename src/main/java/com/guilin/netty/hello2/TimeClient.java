package com.guilin.netty.hello2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * Created by hadoop on 2015/12/6.
 * 支持TCP粘包和拆包的TimeClient
 * LineBasedFrameDecoder+StringDecoder组合就是按行切分的文本解码器
 */
public class TimeClient {

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
            }
        }
        new TimeClient().connect("127.0.0.1", port);
    }

    public void connect(String host, int port) throws Exception {
        //网络事件处理线程组
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            //配置客户端的启动类
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)//设置封包，使用一次大数据的写操作，而不是多次小数据的写操作
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new TimeClientHandler());
                        }
                    });
            //连接服务器，同步等待成功
            ChannelFuture future = bootstrap.connect(host, port).sync();

            //同步等待客户端通道关闭
            future.channel().closeFuture().sync();
        } finally {
            //释放线程组资源
            group.shutdownGracefully();
        }

    }

}
