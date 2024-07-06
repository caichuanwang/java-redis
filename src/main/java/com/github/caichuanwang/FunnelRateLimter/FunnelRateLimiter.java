package com.github.caichuanwang.FunnelRateLimter;

import java.util.HashMap;
import java.util.Map;

public class FunnelRateLimiter {
    private int capacity; //漏斗容量  单位 个
    private float leakingRate; //漏斗流水速率  个/毫秒
    private int leftQuota; //漏斗剩余空间  单位 个
    private long leakingTs; // 上一次漏水时间 单位毫秒

    public FunnelRateLimiter(int capacity, float leakingRate) {
        this.capacity = capacity;
        this.leakingRate = leakingRate;
        this.leftQuota = capacity;
        this.leakingTs =System.currentTimeMillis();
    }

    void makeSpace(){
        long nowTs  = System.currentTimeMillis();
        long deltaTs = nowTs - leakingTs;
        int delta = (int) (deltaTs * leakingRate);
        if (delta < 0){
            this.leakingTs = nowTs;
            this.leftQuota = capacity;
            return;
        }
        if (delta < 1){
         return ;
        }
        this.leftQuota = this.leftQuota + delta;
        this.leakingTs = System.currentTimeMillis();
        if (this.leftQuota > this.capacity){
            this.leftQuota = this.capacity;
        }
    }

    boolean watering(int quota){
        makeSpace();
        if (this.leftQuota >= quota){
            this.leftQuota -= quota;
            return true;
        }
        return false;
    }

    private Map<String,FunnelRateLimiter> funnels = new HashMap<String,FunnelRateLimiter>();

    public boolean isAllowed(String userID,String actionKey,
                             int capacity, float leakingRate){
        String key  = String.format("%s:%s",userID,actionKey);
        FunnelRateLimiter funnel = funnels.get(key);
        if (funnels == null){
            funnel = new FunnelRateLimiter(capacity, leakingRate);
            funnels.put(key,funnel);
        }

    return     funnel.watering(1);
    }

}
