package com.rogers.api.config.context;

import com.rogers.api.constants.ApiConstants;
import com.rogers.api.logger.LoggerProcessingContext;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class RogersAppContext {
    
    public static final String[] requestHeaderNames = {
            ApiConstants.HEADER_DEALER_CODE,
            ApiConstants.HEADER_CHAIN,
            ApiConstants.HEADER_REGION,
            ApiConstants.HEADER_STORE,
            ApiConstants.HEADER_APPLICATION_ID,
            ApiConstants.HEADER_USER_ID,
            ApiConstants.HEADER_SESSION_ID,
            ApiConstants.HEADER_TRANSACTION_ID,
            ApiConstants.HEADER_REQUEST_ID,
            ApiConstants.HEADER_WEB_USER_ID,
            ApiConstants.HEADER_LANGUAGE,
            ApiConstants.HEADER_IP_ADDRESS,
            ApiConstants.HEADER_ENV
    };
    private String currentURI;
    private LoggerProcessingContext processingContext = null;

    private final Map<String,String> requestHeader = new HashMap<>();
    private final Map<String,String> responseHeader = new HashMap<>();
    private final Map<String, String> commerceHeader = new ConcurrentHashMap<>();
    private final PerformanceVO performance = new PerformanceVO();

    public void addRequestHeader(String name, String value) {
        this.requestHeader.put(name, value);
    }


    public void start(String name){
        this.performance.start(name);
    }
    public void end(String name){
        this.performance.end(name);
    }

}
