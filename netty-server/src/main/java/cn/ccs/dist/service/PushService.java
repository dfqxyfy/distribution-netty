package cn.ccs.dist.service;

import cn.ccs.dist.netty.WebSocketHandler;
import cn.ccs.dist.reg.entity.RegInfoProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestTemplate;

public class PushService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RegInfoProperties regInfoProperties;

    public String send(String userId, String msg) {
        RegInfoProperties info = (RegInfoProperties) redisTemplate.opsForValue().get(userId);

        if (info.equals(regInfoProperties)) {
            WebSocketHandler.send(userId, msg);
            return "success";
        }
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(conHttp(info) + "api/send?userId=" + userId + "&msg=" + msg, String.class);
    }


    private String conHttp(RegInfoProperties info) {
        String basServer = "http://" + info.getHttpServer() + ":" + info.getHttpPort() + "/";
        return basServer;
    }
}
