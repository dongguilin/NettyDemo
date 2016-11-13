package com.guilin.netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;


/**
 * Created by hadoop on 2016/1/2.
 */
public class WebSocketServer {

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        new WebSocketServer().run(port);
    }

    public void run(int port) throws Exception {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //将请求和应答消息编码或者解码为HTTP消息
                            pipeline.addLast("http-codec", new HttpServerCodec());
                            //聚合器，把多个消息转换为一个单一的FullHttpRequest或是FullHttpResponse
                            pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
                            //块写入处理器，支持异步发送大的码流(例如大的文件传输)，但不占用过多内存，防止发生Java内存溢出错误
                            pipeline.addLast("http-chunked", new ChunkedWriteHandler());
                            //自定义服务端处理器
                            pipeline.addLast("handler", new WebSocketServerHandler());
                        }
                    });

            Channel channel = bootstrap.bind(port).sync().channel();
            System.out.println("Web socket server started at port:" + port + '.');
            System.out.println("Open your browser and navigate to http://localhost:" + port + '/');
            channel.closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}
