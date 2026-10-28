package com.rogers.api.config.context;

public class RogersAppContextKeeper {
    private static final ThreadLocal<RogersAppContext> threadLocal = new ThreadLocal<RogersAppContext>() {
        @Override
        protected RogersAppContext initialValue(){
            return new RogersAppContext();
        }
    };

    public static RogersAppContext getContext(){
        return threadLocal.get();
    }

    public static void remove(){
        threadLocal.remove();
    }
}
