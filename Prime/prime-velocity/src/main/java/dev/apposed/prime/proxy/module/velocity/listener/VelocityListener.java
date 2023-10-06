package dev.apposed.prime.proxy.module.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import dev.apposed.prime.proxy.PrimeProxy;
import dev.apposed.prime.proxy.module.database.redis.JedisModule;
import dev.apposed.prime.proxy.util.Color;
import dev.apposed.prime.proxy.util.time.DurationUtils;
import net.kyori.adventure.text.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class VelocityListener {

    private final JedisModule jedisModule;
    private volatile String lineOne = "Database Error", lineTwo = "Failed to fetch MOTD";
    private volatile long countdown = 0;

    {
        this.jedisModule = PrimeProxy.getInstance().getModuleHandler().getModule(JedisModule.class);

        PrimeProxy.getInstance().getServer().getScheduler().buildTask(PrimeProxy.getInstance(), () -> this.jedisModule.runCommand(jedis -> {
                this.lineOne = jedis.hget("Prime:MOTD", "1");
                this.lineTwo = jedis.hget("Prime:MOTD", "2");
                this.countdown = Long.parseLong(jedis.hget("Prime:MOTD", "countdown"));
        })).repeat(10, TimeUnit.SECONDS).schedule();
    }

    @Subscribe
    public void onProxyPing(ProxyPingEvent event) {
        ServerPing.Builder builder = event.getPing().asBuilder()
                .description(Component.text((this.lineOne != null ? this.lineOne.replace("%countdown%", DurationUtils.formatIntoDetailedString(
                        (int)((countdown - System.currentTimeMillis()) / 1000)
                )) : "") + "\n" + (this.lineTwo != null ? this.lineTwo.replace("%countdown%", DurationUtils.formatIntoDetailedString(
                        (int)((countdown - System.currentTimeMillis()) / 1000)
                )) : "")));
        if(PrimeProxy.getInstance().isInMaintenance()) {
            builder.version(new ServerPing.Version(1, Color.translateNormal("&4Maintenance")));
            final ServerPing.SamplePlayer[] samplePlayers = new ServerPing.SamplePlayer[1];
            samplePlayers[0] = new ServerPing.SamplePlayer(Color.translateNormal("&cElevate is currently in maintenance"), MAINTENANCE_UUID);
            builder.samplePlayers(samplePlayers);
        }

        event.setPing(builder.build());
    }

    private static final UUID MAINTENANCE_UUID = new UUID(0, 0);
}
