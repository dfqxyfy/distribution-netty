package cn.ccs.dist.config.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * XSS过滤
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-04-01 10:20
 */
public class BasicFilter implements Filter {

	private Logger logger = LoggerFactory.getLogger(BasicFilter.class);

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
		HttpServletRequest r = (HttpServletRequest) request;
		String path = r.getQueryString();
		if (path == null) {
			Map<String, String> map = new HashMap<>();
			Enumeration headerNames = ((HttpServletRequest) request).getHeaderNames();
			while (headerNames.hasMoreElements()) {//循环遍历Header中的参数，把遍历出来的参数放入Map中
				String key = (String) headerNames.nextElement();
				String value = ((HttpServletRequest) request).getHeader(key);
				map.put(key, value);
			}
			logger.info(map.toString());
		}

		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}

}