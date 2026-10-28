 package com.rogers.api.controller;

 import com.rogers.api.logger.CommonLogger;

public abstract class BaseController {
    protected CommonLogger logger = CommonLogger.getLogger(this.getClass());
}
