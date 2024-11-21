package mud.api.controller.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLDecoder;

@Component
public class RequestFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(RequestFilter.class);

    @Override
    public void init(FilterConfig filterConfig) {
        logger.info("RequestFilter Initialized");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;

        if (!httpRequest.getRequestURI().startsWith("/v1/message")) {
            if (httpRequest.getQueryString() == null) {
                logger.info(String.format("RequestUri: %s", httpRequest.getRequestURI(), "UTf-8"));
            } else {
                logger.info(String.format("RequestUri: %s?%s", httpRequest.getRequestURI(), URLDecoder.decode(httpRequest.getQueryString(), "UTf-8")));
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        logger.info("RequestFilter Destroyed");
    }
}
