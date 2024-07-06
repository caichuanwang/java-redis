package com.github.caichuanwang.SimpleRateLimiter;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

public class SimpleRateLimiter  {
    private Jedis client;
    public SimpleRateLimiter(Jedis client) {
        this.client = client;
    }

    public boolean isActionAllowed(String userID, String actionKey, int period, int maxCount){
        String key = String.format("hist:%s:%s", userID, actionKey);
        long nowTs = System.currentTimeMillis();
        Pipeline pipe = client.pipelined();
        pipe.zadd(key, nowTs, nowTs+"");
        pipe.multi();
        pipe.zremrangeByScore(key, 0, nowTs - period * 1000L);
        Response<Long> zcount = pipe.zcard(key);
        pipe.expire(key, period + 1 );
        pipe.exec();
        pipe.close();
        return zcount.get() <= maxCount;
    }



    public static void main(String[] args) throws InterruptedException {
        Jedis jedis = new Jedis();
        SimpleRateLimiter limiter = new SimpleRateLimiter(jedis);
        for (int i = 0; i < 10; i++) {
            Thread.sleep(500);
            System.out.println(limiter.isActionAllowed("yamucloud","update",5,5));
        }
        Thread.sleep(8000);
        for (int i = 0; i < 10; i++) {
            Thread.sleep(500);
            System.out.println(limiter.isActionAllowed("yamucloud","update",5,5));
        }
    }
}

