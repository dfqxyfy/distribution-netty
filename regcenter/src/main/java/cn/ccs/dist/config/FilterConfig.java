package cn.ccs.dist.config;

import cn.ccs.dist.config.filter.BasicFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;

/**
 * Filter配置
 *
 * @author chaichuanshi
 * @date 2017-12-21 21:56
 */
@Configuration
public class FilterConfig {

    /**
     * 日志过滤器
     * @return
     */
    @Bean
    public FilterRegistrationBean shiroFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new BasicFilter());
        registration.addUrlPatterns("/*");
        registration.setName("basicFilter");
        registration.setOrder(Integer.MAX_VALUE-1);
        return registration;
    }


}