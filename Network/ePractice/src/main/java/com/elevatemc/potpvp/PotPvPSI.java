package com.elevatemc.potpvp;

import com.elevatemc.elib.command.param.defaults.GameModeParameterType;
import com.elevatemc.elib.eLib;
import com.elevatemc.elib.tab.data.TabList;
import com.elevatemc.potpvp.ability.Ability;
import com.elevatemc.potpvp.ability.AbilityHandler;
import com.elevatemc.potpvp.ability.parameter.AbilityParameter;
import com.elevatemc.potpvp.cosmetic.Cosmetic;
import com.elevatemc.potpvp.cosmetic.CosmeticHandler;
import com.elevatemc.potpvp.cosmetic.command.type.CosmeticParameterType;
import com.elevatemc.potpvp.deathmessage.DeathMessageHandler;
import com.elevatemc.potpvp.events.EventHandler;
import com.elevatemc.potpvp.events.EventListeners;
import com.elevatemc.potpvp.events.EventTask;
import com.elevatemc.potpvp.events.event.GameEvent;
import com.elevatemc.potpvp.events.game.Game;
import com.elevatemc.potpvp.events.game.GameListeners;
import com.elevatemc.potpvp.events.game.GameQueue;
import com.elevatemc.potpvp.gamemode.*;
import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.gamemode.kit.GameModeKit;
import com.elevatemc.potpvp.gamemode.kit.GameModeKitJsonAdapter;
import com.elevatemc.potpvp.gamemode.kit.GameModeKitParameterType;
import com.elevatemc.potpvp.hologram.HologramHandler;
import com.elevatemc.potpvp.hctranked.HCTRankedHandler;
import com.elevatemc.potpvp.tab.PotPvPLayoutProvider;
import com.elevatemc.potpvp.util.ClickTracker;
import com.lunarclient.bukkitapi.cooldown.LCCooldown;
import com.lunarclient.bukkitapi.cooldown.LunarClientAPICooldown;
import com.lunarclient.bukkitapi.nethandler.client.obj.ServerRule;
import com.lunarclient.bukkitapi.serverrule.LunarClientAPIServerRule;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import dev.apposed.prime.spigot.Prime;
import lombok.Getter;
import com.elevatemc.potpvp.arena.ArenaHandler;
import com.elevatemc.potpvp.arena.ArenaSchematic;
import com.elevatemc.potpvp.arena.ArenaSchematicParameterType;
import com.elevatemc.potpvp.duel.DuelHandler;
import com.elevatemc.potpvp.elo.EloHandler;
import com.elevatemc.potpvp.follow.FollowHandler;
import com.elevatemc.potpvp.kit.KitHandler;
import com.elevatemc.potpvp.listener.*;
import com.elevatemc.potpvp.lobby.LobbyHandler;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchHandler;
import com.elevatemc.potpvp.nametag.PotPvPNametagProvider;
import com.elevatemc.potpvp.party.PartyHandler;
import com.elevatemc.potpvp.postmatchinv.PostMatchInvHandler;
import com.elevatemc.potpvp.pvpclasses.PvPClassHandler;
import com.elevatemc.potpvp.queue.QueueHandler;
import com.elevatemc.potpvp.match.rematch.RematchHandler;
import com.elevatemc.potpvp.scoreboard.PotPvPScoreboardConfiguration;
import com.elevatemc.potpvp.setting.SettingHandler;
import com.elevatemc.potpvp.statistics.StatisticsHandler;
import com.elevatemc.potpvp.tournament.TournamentHandler;
import com.elevatemc.potpvp.util.VoidChunkGenerator;
import com.elevatemc.elib.serialization.*;
import org.bukkit.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public final class PotPvPSI extends JavaPlugin {

    private static PotPvPSI instance;
    public static final Random RANDOM = new Random();
    @Getter private static final Gson gson = new GsonBuilder()
        .registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectAdapter())
        .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
        .registerTypeHierarchyAdapter(Location.class, new LocationAdapter())
        .registerTypeHierarchyAdapter(Vector.class, new VectorAdapter())
        .registerTypeAdapter(BlockVector.class, new BlockVectorAdapter())
        .registerTypeHierarchyAdapter(GameModeKit.class, new GameModeKitJsonAdapter()) // custom GameMode serializer
        .registerTypeAdapter(ChunkSnapshot.class, new ChunkSnapshotAdapter())
        .serializeNulls()
        .create();

    @Getter private static final Gson PLAIN_GSON = (new GsonBuilder())
            .registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectAdapter())
            .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
            .registerTypeHierarchyAdapter(Location.class, new LocationAdapter())
            .registerTypeHierarchyAdapter(Vector.class, new VectorAdapter())
            .registerTypeAdapter(BlockVector.class, new BlockVectorAdapter())
            .serializeNulls().create();

    @Getter private MongoDatabase mongoDatabase;

    @Getter private SettingHandler settingHandler;
    @Getter private DuelHandler duelHandler;
    @Getter private KitHandler kitHandler;
    @Getter private LobbyHandler lobbyHandler;
    private ArenaHandler arenaHandler;
    @Getter private MatchHandler matchHandler;
    @Getter private PartyHandler partyHandler;
    @Getter private QueueHandler queueHandler;
    @Getter private RematchHandler rematchHandler;
    @Getter private PostMatchInvHandler postMatchInvHandler;

    @Getter private ClickTracker clickTracker;
    @Getter private DeathMessageHandler deathMessageHandler;
    @Getter private FollowHandler followHandler;
    @Getter private EloHandler eloHandler;
    @Getter private TournamentHandler tournamentHandler;
    @Getter private PvPClassHandler pvpClassHandler;
    @Getter private Prime prime;
    @Getter private CosmeticHandler cosmeticHandler;
    @Getter private AbilityHandler abilityHandler;
    @Getter private HologramHandler hologramHandler;
    @Getter private HCTRankedHandler HCTRankedHandler;
    @Getter private EventHandler eventHandler;

    @Getter private AtomicInteger onlineCount = new AtomicInteger();
    @Getter private AtomicInteger fightsCount = new AtomicInteger();

    @Override
    public void onEnable() {
        //SpigotConfig.onlyCustomTab = true; // because we'll definitely forget
        //this.dominantColor = ChatColor.DARK_PURPLE;
        instance = this;
        prime = Prime.getPlugin(Prime.class);

        saveDefaultConfig();

        setupMongo();

        // We do not need to load the spawn world since that is the default world.
        Bukkit.getServer().createWorld(new WorldCreator("arenas").generateStructures(false).generator(new VoidChunkGenerator()).type(WorldType.FLAT));

        for (World world : Bukkit.getWorlds()) {
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("doMobSpawning", "false");
            world.setGameRuleValue("doFireTick", "false");
            world.setWeatherDuration(0);
            world.setTime(6_000L);
        }

        LunarClientAPIServerRule.setRule(ServerRule.LEGACY_COMBAT, true);
        LunarClientAPIServerRule.setRule(ServerRule.SERVER_HANDLES_WAYPOINTS, true);
        LunarClientAPICooldown.registerCooldown(new LCCooldown("Enderpearl", 16, Material.ENDER_PEARL));


        settingHandler = new SettingHandler();
        duelHandler = new DuelHandler();
        queueHandler = new QueueHandler();
        GameModes.loadGameModes();
        kitHandler = new KitHandler();
        lobbyHandler = new LobbyHandler();
        arenaHandler = new ArenaHandler();
        matchHandler = new MatchHandler();
        partyHandler = new PartyHandler();
        rematchHandler = new RematchHandler();
        postMatchInvHandler = new PostMatchInvHandler();
        followHandler = new FollowHandler();
        eloHandler = new EloHandler();
        pvpClassHandler = new PvPClassHandler();
        tournamentHandler = new TournamentHandler();
        abilityHandler = new AbilityHandler(this);
        HCTRankedHandler = new HCTRankedHandler();
        hologramHandler = new HologramHandler();
        deathMessageHandler = new DeathMessageHandler();
        clickTracker = new ClickTracker(this);
        // custom shits with better code | who wants to be using static references. #disgusting even tho its easier
        cosmeticHandler = new CosmeticHandler();

        eventHandler = new EventHandler(this);



        new EventTask().runTaskTimerAsynchronously(this, 1L, 1L);

        for(GameEvent event : EventHandler.EVENTS) {
            for(Listener listener : event.getListeners()) {
                getServer().getPluginManager().registerEvents(listener, this);
            }
        }

        getServer().getPluginManager().registerEvents(new BasicPreventionListener(), this);
        getServer().getPluginManager().registerEvents(new BowHealthListener(), this);
        getServer().getPluginManager().registerEvents(new NightModeListener(), this);
        getServer().getPluginManager().registerEvents(new PearlCooldownListener(), this);
        getServer().getPluginManager().registerEvents(new RankedMatchQualificationListener(), this);
        getServer().getPluginManager().registerEvents(new TabCompleteListener(), this);
        getServer().getPluginManager().registerEvents(new StatisticsHandler(), this);
        getServer().getPluginManager().registerEvents(new FancyInventoryListener(), this);
        getServer().getPluginManager().registerEvents(new EventListeners(), this);
        getServer().getPluginManager().registerEvents(new GameListeners(), this);

        eLib.getInstance().getCommandHandler().registerAll(this);
        eLib.getInstance().getCommandHandler().registerParameterType(GameMode.class, new GameModeParameterType());
        eLib.getInstance().getCommandHandler().registerParameterType(GameModeKit.class, new GameModeKitParameterType());
        eLib.getInstance().getCommandHandler().registerParameterType(ArenaSchematic.class, new ArenaSchematicParameterType());
        eLib.getInstance().getCommandHandler().registerParameterType(Ability.class, new AbilityParameter());
        eLib.getInstance().getCommandHandler().registerParameterType(Cosmetic.class, new CosmeticParameterType());
        System.out.println("Loading tab layout...");
        eLib.getInstance().getTabHandler().setTabList(new TabList(this, new PotPvPLayoutProvider()));
        System.out.println("Done finished loading!");
        eLib.getInstance().getNameTagHandler().registerProvider(new PotPvPNametagProvider());
        eLib.getInstance().getScoreboardHandler().setConfiguration(PotPvPScoreboardConfiguration.create());
        hologramHandler.registerHolograms();

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            int online = (int) Bukkit.getOnlinePlayers()
                    .stream()
                    .filter(invisible -> !invisible.hasMetadata("modmode"))
                    .count();
            getOnlineCount().set(online);
            getFightsCount().set(matchHandler.countPlayersPlayingInProgressMatches());
        }, 3 * 20L, 20L);
    }

    @Override
    public void onDisable() {
        for (Match match : this.matchHandler.getHostedMatches()) {
            if (match.getGameMode().getBuildingAllowed()) match.getArena().restore();
        }

        GameQueue.INSTANCE.getCurrentGames().forEach(Game::end);

        try {
            arenaHandler.saveSchematics();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String playerName : PvPClassHandler.getEquippedKits().keySet()) {
            PvPClassHandler.getEquippedKits().get(playerName).remove(getServer().getPlayerExact(playerName));
        }

        instance = null;
    }

    private void setupMongo() {
        MongoClientURI uri = new MongoClientURI(getConfig().getString("mongo.uri"));
        MongoClient client = new MongoClient(uri);
        mongoDatabase = client.getDatabase(getConfig().getString("mongo.database"));
    }

    // This is here because chunk snapshots are (still) being deserialized, and serialized sometimes.
    private static class ChunkSnapshotAdapter extends TypeAdapter<ChunkSnapshot> {

        @Override
        public ChunkSnapshot read(JsonReader arg0) throws IOException {
            return null;
        }

        @Override
        public void write(JsonWriter arg0, ChunkSnapshot arg1) throws IOException {
            
        }
        
    }

    /**
     * It is important that the default world generator gets set to "Practice". This setting can be found under "worlds > MAINWORLD > generator" in bukkit.yml
     */
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new VoidChunkGenerator();
    }

    public ArenaHandler getArenaHandler() {
        return arenaHandler;
    }

    public static PotPvPSI getInstance() {
        return instance;
    }
}