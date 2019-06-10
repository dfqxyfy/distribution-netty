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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RegInfoProperties)) return false;

        RegInfoProperties that = (RegInfoProperties) o;

        if (getHttpServer() != null ? !getHttpServer().equals(that.getHttpServer()) : that.getHttpServer() != null)
            return false;
        if (getHttpPort() != null ? !getHttpPort().equals(that.getHttpPort()) : that.getHttpPort() != null)
            return false;
        if (getNettyServer() != null ? !getNettyServer().equals(that.getNettyServer()) : that.getNettyServer() != null)
            return false;
        return getNettyPort() != null ? getNettyPort().equals(that.getNettyPort()) : that.getNettyPort() == null;
    }

    @Override
    public int hashCode() {
        int result = getHttpServer() != null ? getHttpServer().hashCode() : 0;
        result = 31 * result + (getHttpPort() != null ? getHttpPort().hashCode() : 0);
        result = 31 * result + (getNettyServer() != null ? getNettyServer().hashCode() : 0);
        result = 31 * result + (getNettyPort() != null ? getNettyPort().hashCode() : 0);
        return result;
    }
}