package com.rogers.api.aspect;

import com.rogers.api.config.context.RogersAppContextKeeper;
import com.rogers.api.logger.CommControllerAspectHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class LoggerAspect {
    private final CommControllerAspectHandler commControllerAspectHandler = new CommControllerAspectHandler();

    @Pointcut("within(com.rogers.api.controller..*)")
    public void pointcutController() {
    }

    @Around("pointcutController()")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        return commControllerAspectHandler.proceed(joinPoint, RogersAppContextKeeper.getContext().getRequestHeader());
    }
}
