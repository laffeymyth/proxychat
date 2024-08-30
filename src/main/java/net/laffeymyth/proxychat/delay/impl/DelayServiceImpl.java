package net.laffeymyth.proxychat.delay.impl;

import net.laffeymyth.proxychat.delay.DelayService;
import org.redisson.api.RMapCache;

import java.util.concurrent.TimeUnit;

public class DelayServiceImpl implements DelayService {
    private final RMapCache<String, Byte> delaySet;

    public DelayServiceImpl(RMapCache<String, Byte> delaySet) {
        this.delaySet = delaySet;
    }

    @Override
    public void putDelay(String playerName, String name, long time, TimeUnit timeUnit) {
        if (time > 0) {
            delaySet.put(name + "_" + playerName.toLowerCase(), (byte) 0, time, timeUnit);
        }
    }

    @Override
    public long getDelay(String playerName, String name, TimeUnit timeUnit) {
        return timeUnit.convert(delaySet.remainTimeToLive(name + "_" + playerName.toLowerCase()), timeUnit) / 1000;
    }

    @Override
    public boolean hasDelay(String playerName, String name) {
        return delaySet.containsKey(name + "_" + playerName.toLowerCase());
    }
}
