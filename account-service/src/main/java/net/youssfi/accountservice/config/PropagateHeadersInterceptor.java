package net.youssfi.accountservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Enumeration;

@Component
public class PropagateHeadersInterceptor implements RequestInterceptor {

    public void apply(RequestTemplate template) {

        if (RequestContextHolder.getRequestAttributes() == null) {
            return;
        }

        jakarta.servlet.http.HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();

        Enumeration<String> e = request.getHeaderNames();
        while (e.hasMoreElements())
        {
            String headerName = e.nextElement().toString();
            if (headerName.toLowerCase().startsWith("x-") || headerName.toLowerCase().startsWith("authorization"))
            {
                String values = request.getHeader(headerName);
                template.header(headerName, values);
            }
        }
    }
}