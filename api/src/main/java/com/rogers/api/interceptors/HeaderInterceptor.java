package com.rogers.api.interceptors;

import com.rogers.api.config.context.RogersAppContext;
import com.rogers.api.config.context.RogersAppContextKeeper;
import com.rogers.api.constants.ApiConstants;
import com.rogers.api.exception.CommerceException;
import com.rogers.api.logger.CommonLogger;
import com.rogers.api.logger.LoggerContextKeeper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class HeaderInterceptor implements HandlerInterceptor,BusinessApiValidation {
    private final CommonLogger logger = CommonLogger.getLogger(this.getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if(!isBusinessApiCall(request)) return true;
        RogersAppContext appContext = RogersAppContextKeeper.getContext();
        appContext.setCurrentURI(request.getRequestURI());
        Set<String> receivedHeaders = new HashSet<>(Collections.list(request.getHeaderNames()));
        Set<String> expectedHeaders = new HashSet<>(Arrays.asList(RogersAppContext.requestHeaderNames));
        logger.info("HeaderInterceptor :: preHandle : receivedHeaders {} expectedHeaders {}", receivedHeaders, expectedHeaders);
        Set<String> receivedAndExpectedHeaders = new HashSet<>(expectedHeaders);
        receivedAndExpectedHeaders.retainAll(receivedHeaders);
        if (receivedAndExpectedHeaders.isEmpty()) {
            throw new CommerceException(HttpStatus.BAD_REQUEST, ApiConstants.ERROR_CODE_50000, "No required HTTP headers were found");
        }
        Set<String> missingHeaders = new HashSet<>(expectedHeaders);
        missingHeaders.removeAll(receivedAndExpectedHeaders);
        logger.info("HeaderInterceptor :: preHandle : missingHeaders {}", missingHeaders);
        if (!missingHeaders.isEmpty()) {
            throw new CommerceException(HttpStatus.BAD_REQUEST, ApiConstants.ERROR_CODE_50000, "Missing required HTTP headers " + missingHeaders);
        }
        receivedHeaders.forEach(headerKey -> {
            String headerValue = request.getHeader(headerKey);
            if (headerValue != null) {
                appContext.addRequestHeader(headerKey, headerValue);
            }
        });
        LoggerContextKeeper.getContext().setProcessingContext(appContext.getProcessingContext());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        RogersAppContextKeeper.remove();
    }
}
