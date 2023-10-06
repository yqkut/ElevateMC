package com.elevatemc.elib;

import com.elevatemc.elib.autoreboot.AutoRebootHandler;
import com.elevatemc.elib.border.BorderHandler;
import com.elevatemc.elib.bossbar.BossBarHandler;
import com.elevatemc.elib.combatlogger.CombatLoggerHandler;
import com.elevatemc.elib.command.CommandHandler;
import com.elevatemc.elib.event.HalfHourEvent;
import com.elevatemc.elib.event.HourEvent;
import com.elevatemc.elib.fake.FakeEntityHandler;
import com.elevatemc.elib.hologram.HologramHandler;
import com.elevatemc.elib.nametag.NameTagHandler;
import com.elevatemc.elib.npc.NPCManager;
import com.elevatemc.elib.npc.entry.NPCEntry;
import com.elevatemc.elib.npc.entry.NPCEntryAdapter;
import com.elevatemc.elib.pidgin.PidginHandler;
import com.elevatemc.elib.redis.RedisCommand;
import com.elevatemc.elib.scoreboard.ScoreboardHandler;
import com.elevatemc.elib.serialization.*;
import com.elevatemc.elib.skin.MojangSkinHandler;
import com.elevatemc.elib.skinfix.SkinFixCommand;
import com.elevatemc.elib.skinfix.SkinFixHandler;
import com.elevatemc.elib.tab.TabListManager;
import com.elevatemc.elib.util.InventoryAdapter;
import com.elevatemc.elib.util.ItemUtils;
import com.elevatemc.elib.util.json.ClassAdapter;
import com.elevatemc.elib.util.json.ColorAdapter;
import com.elevatemc.elib.util.json.GsonProvider;
import com.elevatemc.elib.uuid.UUIDCache;
import com.elevatemc.elib.visibility.VisibilityHandler;
import com.elevatemc.spigot.eSpigot;
import org.bukkit.Bukkit;
import com.elevatemc.elib.util.json.map.AllowNullMapTypeAdapterFactory;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import com.google.gson.*;
import lombok.Getter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.awt.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class eLib extends JavaPlugin {

    @Getter
    private static eLib instance;

    @Getter
    private JedisPool localJedisPool;
    @Getter
    private JedisPool backboneJedisPool;
    @Getter
    private long backboneLastError;
    @Getter
    private long redisLastError;

    @Getter
    private CommandHandler commandHandler;
    @Getter
    private HologramHandler hologramHandler;

    @Getter
    private TabListManager tabHandler;
    @Getter
    private NameTagHandler nameTagHandler;
    @Getter
    private ScoreboardHandler scoreboardHandler;
    @Getter
    private AutoRebootHandler autoRebootHandler;
    @Getter
    private CombatLoggerHandler combatLoggerHandler;

    @Getter
    private BorderHandler borderHandler;
    @Getter
    private BossBarHandler bossBarHandler;
    @Getter
    private VisibilityHandler visibilityHandler;

    @Getter
    private PidginHandler pidginHandler;
    @Getter
    private UUIDCache uuidCache;
    @Getter private MojangSkinHandler mojangSkinHandler;
    @Getter private FakeEntityHandler fakeEntityHandler;
    @Getter private NPCManager npcManager;

    public static final Gson GSON = new com.google.gson.GsonBuilder()
            .registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectAdapter())
            .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
            .registerTypeHierarchyAdapter(Location.class, new LocationAdapter())
            .registerTypeHierarchyAdapter(Vector.class, new VectorAdapter())
            .registerTypeAdapter(BlockVector.class, new BlockVectorAdapter())
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    public static final Gson PLAIN_GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectAdapter())
            .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
            .registerTypeHierarchyAdapter(Location.class, new LocationAdapter())
            .registerTypeHierarchyAdapter(Vector.class, new VectorAdapter())
            .registerTypeAdapter(BlockVector.class, new BlockVectorAdapter())
            .serializeNulls()
            .create();

    @Override
    public void onEnable() {

        instance = this;
        registerSerializers();
        this.saveDefaultConfig();

        try {
            getLogger().info("Connecting to local redis");
            this.localJedisPool = new JedisPool(
                    new JedisPoolConfig(),
                    this.getConfig().getString("Redis.Host"),
                    this.getConfig().getInt("Redis.Port", 6379),
                    20000,
                    this.getConfig().getString("Redis.Pass", null),
                    this.getConfig().getInt("Redis.DbId", 5)
            );
            getLogger().info("Connected to local redis");

        } catch (Exception ex) {
            this.localJedisPool = null;

            System.out.println("*********************************************");
            System.out.println("               REDIS");
            System.out.println("-> FAILED TO CONNECT TO LOCAL POOL");
            System.out.println("-> INSTANCE: " + this.getConfig().getString("Redis.Host"));
            System.out.println("*********************************************");
        }
        getLogger().info("Connecting to backbone redis");

        try {
            this.backboneJedisPool = new JedisPool(
                    new JedisPoolConfig(),
                    this.getConfig().getString("Backbone.Host"),
                    this.getConfig().getInt("Backbone.Port", 6379),
                    20000,
                    this.getConfig().getString("Backbone.Pass", null),
                    this.getConfig().getInt("Backbone.DbId", 0)
            );
            getLogger().info("Connected to local redis");

        } catch (Exception ex) {
            this.backboneJedisPool = null;

            System.out.println("*********************************************");
            System.out.println("               REDIS");
            System.out.println("-> FAILED TO CONNECT TO BACKBONE POOL");
            System.out.println("-> INSTANCE: " + this.getConfig().getString("Redis.Host"));
            System.out.println("*********************************************");
        }

        this.mojangSkinHandler = new MojangSkinHandler(this);
        this.commandHandler = new CommandHandler();
        this.hologramHandler = new HologramHandler(this);

        this.tabHandler = new TabListManager();
        this.nameTagHandler = new NameTagHandler();
        this.scoreboardHandler = new ScoreboardHandler();

        this.fakeEntityHandler = new FakeEntityHandler(this);
        this.npcManager = new NPCManager();
        this.autoRebootHandler = new AutoRebootHandler();
        this.combatLoggerHandler = new CombatLoggerHandler();

        this.borderHandler = new BorderHandler();
        this.bossBarHandler = new BossBarHandler();
        this.visibilityHandler = new VisibilityHandler();

        this.pidginHandler = new PidginHandler("pidgin", this.backboneJedisPool);

        this.uuidCache = new UUIDCache();

        ItemUtils.load();

        eSpigot.getInstance().addPacketHandler(new InventoryAdapter());
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        this.setupHourEvents();
        if (!Bukkit.getOnlineMode()) {
            SkinFixHandler skinFix = new SkinFixHandler();
            commandHandler.registerClass(SkinFixCommand.class);

            getServer().getPluginManager().registerEvents(skinFix, this);

            eSpigot.getInstance().addPacketHandler(skinFix);
        }
    }

    public void registerSerializers() {


        GsonProvider.registerTypeAdapter(Class.class, new ClassAdapter());
        GsonProvider.registerTypeHierarchyAdapter(NPCEntry.class, new NPCEntryAdapter());
        GsonProvider.registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter());
        GsonProvider.registerTypeHierarchyAdapter(Location.class, new LocationAdapter());
        GsonProvider.registerTypeHierarchyAdapter(Color.class, new ColorAdapter());

        GsonProvider.registerTypeAdapter(EnumMap.class, (InstanceCreator<Object>) type -> {
            Type[] types = (((ParameterizedType) type).getActualTypeArguments());
            return new EnumMap((Class<?>) types[0]);
        });

        GsonProvider.registerTypeAdapterFactory(new AllowNullMapTypeAdapterFactory(GsonProvider.createConstructor(), true));
    }
    public void onDisable() {
        NPCManager.getInstance().saveAllNPCs();
        this.mojangSkinHandler.saveUUIDSkinCache();
    }

    public <T> T runRedisCommand(RedisCommand<T> redisCommand) {

        Jedis jedis = this.localJedisPool.getResource();

        T result = null;

        try {
            result = redisCommand.execute(jedis);
        } catch (Exception e) {
            e.printStackTrace();

            this.redisLastError = System.currentTimeMillis();

            if (jedis != null) {
                jedis.close();
                jedis = null;
            }
        } finally {

            if (jedis != null) {
                jedis.close();
            }

        }

        return result;
    }

    public <T> T runBackboneRedisCommand(RedisCommand<T> redisCommand) {

        Jedis jedis = this.backboneJedisPool.getResource();

        T result = null;

        try {
            result = redisCommand.execute(jedis);
        } catch (Exception e) {
            e.printStackTrace();
            this.backboneLastError = System.currentTimeMillis();

            if (jedis != null) {
                jedis.close();
                jedis = null;
            }
        } finally {

            if (jedis != null) {
                jedis.close();
            }

        }

        return result;
    }

    private void setupHourEvents() {

        final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor((
                new ThreadFactoryBuilder()).setNameFormat("eLib - Hour Event Thread").setDaemon(true).build());

        final int minOfHour = Calendar.getInstance().get(12);
        final int minToHour = 60 - minOfHour;
        final int minToHalfHour = minToHour >= 30 ? minToHour : 30 - minOfHour;

        executor.scheduleAtFixedRate(() -> eLib.getInstance().getServer().getScheduler().runTask(this, ()
                -> eLib.getInstance().getServer().getPluginManager().callEvent(
                        new HourEvent(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)))),  minToHour, 60L, TimeUnit.MINUTES);

        executor.scheduleAtFixedRate(() -> eLib.getInstance().getServer().getScheduler().runTask(this, ()
                -> eLib.getInstance().getServer().getPluginManager().callEvent(
                        new HalfHourEvent(Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE)))),  minToHalfHour, 30L, TimeUnit.MINUTES);
    }

}
