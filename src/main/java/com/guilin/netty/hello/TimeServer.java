package com.guilin.netty.hello;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by hadoop on 2015/12/6.
 * Netty时间服务器服务端
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
        //配置服务端的NIO线程组
        //NioEventLoopGroup是个线程组，它包含了一组NIO线程，专门用于网络事件的处理，实际上它们就是Reactor线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();//用于服务端接受客户端的连接
        EventLoopGroup workerGroup = new NioEventLoopGroup();//用于进行SocketChannel的网络读写

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();//Netty用于启动NIO服务端的辅助启动类
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)//配置NioServerSocketChannel的TCP参数
                    .childHandler(new TimeServerHandler());//绑定I/O事件处理类

            //绑定端口，同步等待成功
            ChannelFuture future = bootstrap.bind(port).sync();

            //等待服务端监听端口关闭
            future.channel().closeFuture().sync();
        } finally {
            //优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
