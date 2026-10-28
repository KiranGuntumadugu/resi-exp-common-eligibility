package com.rogers.api.config.context;

import lombok.Getter;
import lombok.Setter;

public class PerformanceItem {

    @Getter @Setter private String name;
    @Getter private final long start;
    private long end;
    private boolean finished;

    public PerformanceItem(String name){
        this.name = name;
        this.start = System.currentTimeMillis();
        this.finished = false;
    }

    public void end(){
        this.finished = true;
        this.end = System.currentTimeMillis();
    }

    public long getPerformance() {
        if (!finished)
            return -1L; // If it still hasn't ended, we can't calculate performance. Return -1 to show it still hasn't ended
        return (this.end - this.start);
    }

}
