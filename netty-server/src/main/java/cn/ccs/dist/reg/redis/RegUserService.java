package cn.ccs.dist.reg.redis;

import cn.ccs.dist.reg.entity.RegInfoProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RegUserService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RegInfoProperties regInfoProperties;

    public void regServer(String userId){
        redisTemplate.opsForValue().set(userId,regInfoProperties);
    }

    public void unRegServer(String userId){
        redisTemplate.delete(userId);
    }
}
