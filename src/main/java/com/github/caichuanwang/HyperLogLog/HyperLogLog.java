package com.github.caichuanwang.HyperLogLog;

import redis.clients.jedis.Jedis;

public class HyperLogLog {
    public void addHyperLogLog() {
      Jedis client =  new Jedis();
      for (int i = 0; i < 1000; i++) {
          client.pfadd("codehole","user" + i);
          long total = client.pfcount("codehole");
          if (total != i+1){
              System.out.printf("%d %d \n",total,i+1);
          }
      }
      client.zrangeByScore("key",0,System.currentTimeMillis(),0,1);
      client.close();
    }
}


