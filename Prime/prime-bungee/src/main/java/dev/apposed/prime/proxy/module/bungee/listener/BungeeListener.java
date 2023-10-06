package dev.apposed.prime.proxy.module.bungee.listener;

import dev.apposed.prime.proxy.PrimeProxy;
import dev.apposed.prime.proxy.module.database.redis.JedisModule;
import dev.apposed.prime.proxy.util.time.DurationUtils;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.TimeUnit;

public class BungeeListener implements Listener {

    private final JedisModule jedisModule;
    private volatile String lineOne = "Database Error", lineTwo = "Failed to fetch MOTD";
    private volatile long countdown = 0;

    {
        this.jedisModule = PrimeProxy.getInstance().getModuleHandler().getModule(JedisModule.class);

        PrimeProxy.getInstance().getProxy().getScheduler().schedule(PrimeProxy.getInstance(), () -> this.jedisModule.runCommand(jedis -> {
                this.lineOne = jedis.hget("Prime:MOTD", "1");
                this.lineTwo = jedis.hget("Prime:MOTD", "2");
                this.countdown = Long.parseLong(jedis.hget("Prime:MOTD", "countdown"));
        }), 0, 10, TimeUnit.SECONDS);
    }

    @EventHandler
    public void onProxyPing(ProxyPingEvent event) {
        ServerPing response = event.getResponse();

        response.setDescription((this.lineOne != null ? this.lineOne.replace("%countdown%", DurationUtils.formatIntoDetailedString(
                (int)((countdown - System.currentTimeMillis()) / 1000)
        )) : "") + "\n" + (this.lineTwo != null ? this.lineTwo.replace("%countdown%", DurationUtils.formatIntoDetailedString(
                (int)((countdown - System.currentTimeMillis()) / 1000)
        )) : ""));
        event.setResponse(response);
//        event.setResponse(new LunarServerPing(event.getResponse()));
    }
}
