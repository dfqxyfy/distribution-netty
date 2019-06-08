package cn.ccs.dist.component;

import cn.ccs.dist.netty.WebSocketServer;
import cn.ccs.dist.reg.entity.RegInfoProperties;
import cn.ccs.dist.reg.zookeeper.RegClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class InitZkNetty {

    @Autowired
    private RegInfoProperties regInfo;

    @Autowired
    private RegClient regClient;

    public InitZkNetty(RegInfoProperties regInfo){
        this.regInfo=regInfo;
    }

    @PostConstruct
    public void initS() throws Exception{
        WebSocketServer socketServer = new WebSocketServer();
        new Thread(){
            @Override
            public void run(){
                try {
                    socketServer.initServer(regInfo.getNettyPort());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

        regClient.init();
    }

}
