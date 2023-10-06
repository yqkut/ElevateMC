package com.elevatemc.ehub;

import cc.fyre.universe.Universe;
import cc.fyre.universe.server.Server;
import com.elevatemc.ehub.database.RedisManager;
import com.elevatemc.ehub.listener.*;
import com.elevatemc.ehub.profile.manager.ProfileManager;
import com.elevatemc.ehub.queue.QueueHandler;
import com.elevatemc.ehub.scoreboard.eHubScoreboardConfiguration;
import com.elevatemc.ehub.tab.eHubTabProvider;
import com.elevatemc.ehub.type.armor.task.ArmorTask;
import com.elevatemc.ehub.type.particle.task.ParticleTask;
import com.elevatemc.ehub.utils.PlayerCountTask;
import com.elevatemc.elib.eLib;
import com.elevatemc.elib.tab.data.TabList;
import com.google.common.collect.ImmutableList;
import com.mysql.jdbc.StringUtils;
import com.xxmicloxx.NoteBlockAPI.model.Playlist;
import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import dev.apposed.prime.spigot.Prime;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class eHub extends JavaPlugin {
    @Getter private static eHub instance;
    @Getter private Prime prime;
    @Getter private QueueHandler queueHandler;

    @Getter
    private RedisManager redisManager;

    @Getter
    private ParticleTask particleTask;

    @Getter
    private ProfileManager profileManager;
    @Getter
    private ArmorTask armorTask;

    private final List<String> serversToPing = ImmutableList.of("ALL", "Practice");

    @Getter RadioSongPlayer radioSongPlayer = null;

    @Override
    public void onEnable() {
        instance = this;

        prime = Prime.getPlugin(Prime.class);
        queueHandler = new QueueHandler();

        getServer().getPluginManager().registerEvents(new HubListener(), this);
        getServer().getPluginManager().registerEvents(new PreventionListener(), this);
        getServer().getPluginManager().registerEvents(new FunListener(), this);

        setupMusicPlayer();
        this.loadManagers();
        this.loadHandlers();
        this.loadTasks();

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        getServer().getScheduler().runTaskTimer(this, new PlayerCountTask(serversToPing), 0, 20L);

        eLib.getInstance().getCommandHandler().registerAll(this);
        eLib.getInstance().getScoreboardHandler().setConfiguration(eHubScoreboardConfiguration.create());
        eLib.getInstance().getTabHandler().setTabList(new TabList(this, new eHubTabProvider()));
    }

    public String getGlobalPlayerCount() {
        int players = 0;
        for (Server server : Universe.getInstance().getUniverseHandler().getServers()) {
            players += server.getOnlinePlayers().get();
        }
        return String.valueOf(players);
    }

    public String getServerPlayerCount(String server) {
        return String.valueOf(Universe.getInstance().getUniverseHandler().serverFromName(server).getOnlinePlayers().get());
    }

    public String getMaxPlayerCount(String server) {
        return String.valueOf(Universe.getInstance().getUniverseHandler().serverFromName("Elevate-Practice").getMaximumPlayers().get());
    }

    public void setupMusicPlayer() {
        if (!getDataFolder().exists()) getDataFolder().mkdir();
        File songsDirectory = new File(getDataFolder(), "songs");
        if (!songsDirectory.exists()) songsDirectory.mkdir();

        if (songsDirectory.isDirectory()) {
            ArrayList<Song> songs = new ArrayList<>();
            for (File file : songsDirectory.listFiles()) {
                if (file.isDirectory()) continue;
                Song song = NBSDecoder.parse(file);
                if (song == null) continue;

                if (StringUtils.isEmptyOrWhitespaceOnly(song.getTitle())) {
                    file.delete();
                    Bukkit.getLogger().warning("File " + file.getName() + " has no title, deleted!");
                    continue;
                }

                if (song.getTitle().length() > 12) {
                    song = new Song(song.getSpeed(), song.getLayerHashMap(), song.getSongHeight(), song.getLength(), song.getTitle().substring(0, 12) + "…", song.getAuthor(), song.getOriginalAuthor(), song.getDescription(), song.getPath(), song.getFirstCustomInstrumentIndex(), song.getCustomInstruments(), song.isStereo());
                }

                songs.add(song);
            }
            Bukkit.getLogger().info("Loaded " + songs.size() + " songs.");
            radioSongPlayer = new RadioSongPlayer(new Playlist(songs.toArray(new Song[0])));
            radioSongPlayer.setRepeatMode(RepeatMode.ALL);
            radioSongPlayer.setPlaying(true);
            radioSongPlayer.setRandom(true);
        }

        getServer().getPluginManager().registerEvents(new MusicListener(), this);
    }

    private void loadManagers() {
        redisManager = new RedisManager();
        profileManager = new ProfileManager();
    }

    private void loadTasks() {
        armorTask = new ArmorTask();
        particleTask = new ParticleTask();
    }

    private void loadHandlers() {
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    @Override
    public void onDisable() {
        armorTask.cancel();
        particleTask.cancel();

        getProfileManager().getProfiles().clear();
    }
}
