package cn.ccs.dist.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.marshalling.MarshallingDecoder;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.io.UnsupportedEncodingException;

/**
 * 客户端发送请求
 * @author zhb
 *
 */
public class ClientNetty {
    
    // 要请求的服务器的ip地址
    private String ip;
    // 服务器的端口
    private int port;
    
    public ClientNetty(String ip, int port){
        this.ip = ip;
        this.port = port;
    }
    
    // 请求端主题
    private void action() throws InterruptedException, UnsupportedEncodingException {
        
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        
        Bootstrap bs = new Bootstrap();
        
        bs.group(bossGroup)
          .channel(NioSocketChannel.class)
          .option(ChannelOption.SO_KEEPALIVE, true)
          .handler(new ChannelInitializer<SocketChannel>() {
              @Override
              protected void initChannel(SocketChannel socketChannel) throws Exception {              
//                     marshalling 序列化对象的解码
//                  socketChannel.pipeline().addLast(MarshallingDecoder);
////                     marshalling 序列化对象的编码
//                  socketChannel.pipeline().addLast(MarshallingCodefactory.buildEncoder());
//
                    // 处理来自服务端的响应信息

                  socketChannel.pipeline().addLast(new HttpServerCodec());
                  socketChannel.pipeline().addLast(new ChunkedWriteHandler());
                  socketChannel.pipeline().addLast(new HttpObjectAggregator(8192));
                  socketChannel.pipeline().addLast(new WebSocketServerProtocolHandler("/hello"));
                  socketChannel.pipeline().addLast(new ClientHandler());
              }
         });



        // 客户端开启
        ChannelFuture cf = bs.connect(ip, port).sync();
        
        String reqStr = "我是客户端请求1$_";
        
        // 发送客户端的请求
        cf.channel().writeAndFlush(reqStr.getBytes());
        cf.channel().flush();
        System.out.println("over");
        //cf.channel().closeFuture().sync();
        cf.channel().close();
    }
            
    public static void main(String[] args) throws UnsupportedEncodingException, InterruptedException {
        new ClientNetty("127.0.0.1",8899).action();
    }
        
}