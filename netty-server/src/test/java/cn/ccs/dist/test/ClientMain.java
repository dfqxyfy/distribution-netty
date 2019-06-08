package cn.ccs.dist.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;

public class ClientMain {
    public static void main(String[] args) throws Exception{
        EventLoopGroup group=new NioEventLoopGroup();
        Bootstrap boot=new Bootstrap();
        boot.option(ChannelOption.SO_KEEPALIVE,true)
                .option(ChannelOption.TCP_NODELAY,true)
                .option(ChannelOption.SO_BACKLOG,1024*1024*10)
                .group(group)
                .handler(new LoggingHandler(LogLevel.INFO))
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline p = socketChannel.pipeline();
                        p.addLast(new ChannelHandler[]{new HttpClientCodec(),
                                new HttpObjectAggregator(1024*1024*10)});
                        p.addLast("hookedHandler", new WebSocketClientHandler());
                    }
                });
        URI websocketURI = new URI("ws://localhost:8899/ws?userId=111");
        HttpHeaders httpHeaders = new DefaultHttpHeaders();
        //进行握手
        WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(websocketURI, WebSocketVersion.V13, (String)null, true,httpHeaders);
        System.out.println("connect");
        final Channel channel=boot.connect(websocketURI.getHost(),websocketURI.getPort()).sync().channel();
        WebSocketClientHandler handler = (WebSocketClientHandler)channel.pipeline().get("hookedHandler");
        handler.setHandshaker(handshaker);
        handshaker.handshake(channel);
        //阻塞等待是否握手成功
        handler.handshakeFuture().sync();

        Thread text=new Thread(new Runnable() {
            public void run() {
                int i=2;
                while (i>0){
                    i--;
                    System.out.println("text send");
                    TextWebSocketFrame frame = new TextWebSocketFrame("我是文本");
                    channel.writeAndFlush(frame).addListener(new ChannelFutureListener() {
                        public void operationComplete(ChannelFuture channelFuture) throws Exception {
                            if(channelFuture.isSuccess()){
                                System.out.println("text send success");
                            }else{
                                System.out.println("text send failed  "+channelFuture.cause().getMessage());
                            }
                        }
                    });
                }

                }
        });
        System.out.println("start sending");
        text.start();
    }
}
