package net.laffeymyth.proxychat.delay;

import java.util.concurrent.TimeUnit;

public interface DelayService {
    void putDelay(String playerName, String name, long time, TimeUnit timeUnit);
    long getDelay(String playerName, String name, TimeUnit timeUnit);
    boolean hasDelay(String playerName, String name);
}
