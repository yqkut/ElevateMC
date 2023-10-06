package dev.apposed.prime.proxy.module.database.redis;

import com.google.gson.Gson;
import dev.apposed.prime.proxy.module.Module;
import dev.apposed.prime.proxy.module.database.redis.packet.Packet;
import dev.apposed.prime.proxy.util.json.JsonHelper;
import lombok.Getter;
import ninja.leaping.configurate.ConfigurationNode;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Getter
public class JedisModule extends Module {

    private String host;
    private String password;
    private int port;
    private boolean auth;

    private JedisPool jedisPool;

    private String channel;

    private Gson gson;
    private ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Override
    public void onEnable() {
        ConfigurationNode redisConfig = getPlugin().getConfig().getNode("redis");
        this.host = redisConfig.getNode("host").getString();
        this.port = redisConfig.getNode("port").getInt();
        this.channel = redisConfig.getNode("channel").getString();
        this.auth = redisConfig.getNode("auth").getBoolean();
        this.password = redisConfig.getNode("password").getString();

        this.gson = JsonHelper.GSON;
        connect();
    }

    /**
     * Attempts to make a connection to the
     * redis database with the specified credentials and
     * starts a thread for receiving messages
     */
    public void connect() {
        this.jedisPool = new JedisPool(host, port);
        if(this.auth) {
            this.jedisPool.getResource().auth(this.password);
        }

        executorService.execute(() -> this.runCommand(redis -> {
            if(this.auth) {
                redis.auth(this.password);
            }

            redis.subscribe(new JedisPubSub() {

                @Override
                public void onMessage(String channel, String message) {
                    try {
                        // Create the packet
                        String[] strings = message.split("/split/");
                        Object jsonObject = gson.fromJson(strings[1], Class.forName(strings[0]));
                        if(jsonObject == null) return;

                        Packet packet = (Packet) jsonObject;

                        packet.onReceive();

                    } catch (Exception ex) {
                        // do nothing
                    }
                }
            }, channel);
        }));
    }

    /**
     * sends a packet through redis
     *
     * @Parameter packet the packet to get sent
     */

    public void sendPacket(Packet packet) {
        packet.onSend();

        executorService.execute(() -> {
            runCommand(redis -> {
                if(this.auth) {
                    redis.auth(this.password);
                }

                redis.publish(channel, packet.getClass().getName() + "/split/" + gson.toJson(packet));
            });
        });
    }

    /**
     * sends a packet through redis
     *
     * @Parameter consumer the callback to be executed
     */
    public void runCommand(Consumer<Jedis> consumer) {
        Jedis jedis = jedisPool.getResource();
        if (jedis != null) {
            if(this.auth) {
                jedis.auth(this.password);
            }
            consumer.accept(jedis);
            jedisPool.returnResource(jedis);
        }
    }
}
