package cn.ccs.dist.netty;

import cn.ccs.dist.component.SpringUtils;
import cn.ccs.dist.reg.entity.RegInfoProperties;
import cn.ccs.dist.reg.redis.RegUserService;
import cn.ccs.dist.reg.zookeeper.RegClient;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;

import java.util.concurrent.ConcurrentHashMap;

public class WebSocketHandler extends SimpleChannelInboundHandler<Object> {

    WebSocketServerHandshaker handshaker;

    private static final ConcurrentHashMap<String,ChannelHandlerContext> CTXMAP = new ConcurrentHashMap<>();



    private  static  final AttributeKey<String> USER_KEY=AttributeKey.newInstance("USER_KEY");;
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive    ");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        System.out.println(msg.getClass());
        if(msg instanceof FullHttpRequest){
            final FullHttpRequest fhr = (FullHttpRequest) msg;
            System.out.println(fhr.uri());
            handleHttpRequest(channelHandlerContext,fhr);
        }else if (msg instanceof WebSocketFrame) {
            handleWebSocketRequest(channelHandlerContext,(WebSocketFrame)msg);
        }else {
            System.out.println("未知类型");
            System.out.println(msg.getClass());
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerAdded!!!");
        final RegClient regClient = SpringUtils.getApplicationContext().getBean(RegClient.class);
        regClient.addReg();
        final RegUserService regUserService = SpringUtils.getApplicationContext().getBean(RegUserService.class);
        regUserService.regServer(ctx.channel().attr(USER_KEY).get());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerRemoved!!!");
        final RegClient regClient = SpringUtils.getApplicationContext().getBean(RegClient.class);
        CTXMAP.remove(ctx.channel().attr(USER_KEY).get());
        regClient.delReg();
        final RegUserService regUserService = SpringUtils.getApplicationContext().getBean(RegUserService.class);
        regUserService.unRegServer(ctx.channel().attr(USER_KEY).get());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        CTXMAP.remove(ctx.channel().attr(USER_KEY).get());
        final RegClient regClient = SpringUtils.getApplicationContext().getBean(RegClient.class);
        regClient.addReg();
        final RegUserService regUserService = SpringUtils.getApplicationContext().getBean(RegUserService.class);
        regUserService.unRegServer(ctx.channel().attr(USER_KEY).get());
    }


    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        if (HttpMethod.GET == request.method()) {
            QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
            String userIdStr = decoder.parameters() != null && decoder.parameters().containsKey("userId") ? decoder.parameters().get("userId").get(0) : null;

            if (userIdStr == null) {
                return;
            }
            CTXMAP.put(userIdStr,ctx);
            ctx.channel().attr(USER_KEY).set(userIdStr);
            if (request.decoderResult().isSuccess() && "websocket".equals(request.headers().get("Upgrade"))) {
                WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory("wss://" + request.headers().get("Host") + "/websocket", null, false);
                handshaker = factory.newHandshaker(request);//通过创建请求生成一个握手对象
                if (handshaker != null) {
                    handshaker.handshake(ctx.channel(), request);
                }
            }
        }
    }

    private RegInfoProperties bean;


    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
        // BAD_REQUEST(400) 客户端请求错误返回的应答消息
        if (res.status().code() != 200) {
            // 将返回的状态码放入缓存中，Unpooled没有使用缓存池
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            HttpUtil.setContentLength(res, res.content().readableBytes());
        }
        // 发送应答消息
        ChannelFuture cf = ctx.channel().writeAndFlush(res);
        // 非法连接直接关闭连接
        if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
            cf.addListener(ChannelFutureListener.CLOSE);
        }
    }


    private void handleWebSocketRequest(ChannelHandlerContext ctx, WebSocketFrame req) throws Exception {
        System.out.println("handler");
        if (req instanceof CloseWebSocketFrame) {
            // 关闭websocket连接
            handshaker.close(ctx.channel(), (CloseWebSocketFrame)req.retain());
            return;
        }
        if (req instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(req.content().retain()));
            return;
        }
        if (!(req instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException("当前只支持文本消息，不支持二进制消息");
        }
        if (ctx == null || this.handshaker == null || ctx.isRemoved()) {
            throw new Exception("尚未握手成功，无法向客户端发送WebSocket消息");
        }
        final String text = ((TextWebSocketFrame) req).text();
        System.out.println(text);
        final TextWebSocketFrame o = new TextWebSocketFrame(text);
        ctx.channel().write(o);
        ctx.flush();
    }

    public static void send(String userId,String msg){
        CTXMAP.get(userId).writeAndFlush(msg);
    }

}
