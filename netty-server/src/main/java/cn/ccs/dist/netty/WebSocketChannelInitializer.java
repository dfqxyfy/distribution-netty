package cn.ccs.dist.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
        protected void initChannel(SocketChannel ch) throws Exception {

            ch.pipeline().addLast("http-codec", new HttpServerCodec())
                    .addLast("aggregator", new HttpObjectAggregator(65535))
                    .addLast("http-chunked", new ChunkedWriteHandler())
                    .addLast("handler", new WebSocketHandler());
        }


    }
