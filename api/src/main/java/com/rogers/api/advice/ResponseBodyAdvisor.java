package com.rogers.api.advice;

import com.rogers.api.config.context.RogersAppContext;
import com.rogers.api.config.context.RogersAppContextKeeper;
import com.rogers.api.logger.CommonLogger;
import com.rogers.response.Response;
import com.rogers.response.SuccessResponse;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
@Order(1)
public class ResponseBodyAdvisor implements ResponseBodyAdvice<Object> {

    private final CommonLogger logger = CommonLogger.getLogger(this.getClass());

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {

        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        // Previously this returned false when attrs != null which prevented the advisor from running
        // Apply the advisor only when we have an active servlet request (typical HTTP flow)
        return attrs != null;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        // preserve null (e.g., 204 No Content)
        if (body == null) {
            return null;
        }

        if (body instanceof Response) {
            return body;
        }

        if (body instanceof byte[] || body instanceof Resource) {
            return body;
        }

        if (body instanceof ResponseEntity<?> re) {
            Object inner = re.getBody();
            if (inner instanceof Response) {
                return re;
            }
            Response<Object> wrapped = SuccessResponse.of(inner);
            HttpHeaders headers = re.getHeaders();
            return new ResponseEntity<>(wrapped, headers, re.getStatusCode());
        }

        RogersAppContext appContext = RogersAppContextKeeper.getContext();

        if (logger.isDebugEnabled()){
            logger.debug("performance-report:\n" + appContext.getPerformance());
        }
        // Normal case: wrap and return
        return SuccessResponse.of(body);
    }
}
