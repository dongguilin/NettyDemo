package com.guilin.netty.linedecoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * Created by hadoop on 2015/12/6.
 * 支持TCP粘包拆包的TimeServer
 * LineBasedFrameDecoder+StringDecoder组合就是按行切分的文本解码器
 */
public class TimeServer {

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
            }
        }
        new TimeServer().bind(port);
    }

    public void bind(int port) throws Exception {
        //接收客户端连接用
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //处理网络读写事件
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            //配置服务器启动类
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                            ch.pipeline().addLast(new StringDecoder());//设置字符串解码器 自动将报文转为字符串
                            ch.pipeline().addLast(new TimeServerHandler());//处理网络IO 处理器
                        }
                    });
            //绑定端口 等待绑定成功
            ChannelFuture future = bootstrap.bind(port).sync();
            //等待服务器退出
            future.channel().closeFuture().sync();
        } finally {
            //释放线程资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

}
