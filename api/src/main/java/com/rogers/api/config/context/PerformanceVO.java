package com.rogers.api.config.context;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
public class PerformanceVO {
    private Map<String,PerformanceItem> data = new LinkedHashMap<>();

    public void start(String name){
        data.put(name,new PerformanceItem(name));
    }

    public void end(String name){
        PerformanceItem item = data.get(name);
        if (item != null){
            item.end();
        }
    }
}
