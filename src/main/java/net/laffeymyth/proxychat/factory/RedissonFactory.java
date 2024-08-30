package net.laffeymyth.proxychat.factory;

import net.laffeymyth.localization.commons.service.PlainLocalizationService;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RedissonFactory {
    private static final PlainLocalizationService LANG = PlainLocalizationService.lang();

    public static RedissonClient create() {
        Config config = new Config();
        config.useSingleServer()
                .setPassword(LANG.getMessage("config_redis_password", "ru"))
                .setAddress(LANG.getMessage("config_redid_address", "ru"));

        return Redisson.create(config);
    }
}
