package cn.ccs.dist.reg.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Data
@Component
@ConfigurationProperties(prefix = "dist.info")
public class RegInfoProperties implements Serializable{
    private String httpServer;
    private Integer httpPort;
    private String nettyServer;
    private Integer nettyPort;
}