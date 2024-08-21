package net.laffeymyth.proxychat.factory;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RedissonFactory {
    public static RedissonClient create() {
        Config config = new Config();
        config.useSingleServer()
                .setPassword("smartCookieLikeMe2008")
                .setAddress("redis://127.0.0.1:6379");

        return Redisson.create(config);
    }
}
