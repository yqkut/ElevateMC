package dev.apposed.prime.spigot;

import com.elevatemc.elib.eLib;
import dev.apposed.prime.spigot.module.ModuleHandler;
import dev.apposed.prime.spigot.module.database.mongo.MongoModule;
import dev.apposed.prime.spigot.module.database.redis.JedisModule;
import dev.apposed.prime.spigot.module.profile.ProfileHandler;
import dev.apposed.prime.spigot.module.profile.target.ProfileTarget;
import dev.apposed.prime.spigot.module.profile.target.ProfileTypeAdapter;
import dev.apposed.prime.spigot.module.profile.listener.ProfileListener;
import dev.apposed.prime.spigot.module.profile.punishment.listener.PunishmentListener;
import dev.apposed.prime.spigot.module.profile.skin.SkinHandler;
import dev.apposed.prime.spigot.module.rank.Rank;
import dev.apposed.prime.spigot.module.rank.RankHandler;
import dev.apposed.prime.spigot.module.rank.adapter.RankTypeAdapter;
import dev.apposed.prime.spigot.module.server.ServerHandler;
import dev.apposed.prime.spigot.module.server.filter.ChatFilterHandler;
import dev.apposed.prime.spigot.module.server.filter.listener.ChatFilterListener;
import dev.apposed.prime.spigot.module.tag.Tag;
import dev.apposed.prime.spigot.module.tag.TagHandler;
import dev.apposed.prime.spigot.module.tag.adapter.TagTypeAdapter;
import dev.apposed.prime.spigot.module.webhook.listener.StaffGriefListener;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

@Getter
public class Prime extends JavaPlugin {

    private ModuleHandler moduleHandler;
    private static Prime instance;

    @Override
    public void onEnable() {
        instance = this;

        loadHandlers();
        loadHelpers();
        loadModules();
    }

    @Override
    public void onDisable() {
        this.moduleHandler.disableModules();

        instance = null;
    }

    private void loadHandlers() {
        this.moduleHandler = new ModuleHandler();

        Arrays.asList(
                new MongoModule(),
                new JedisModule(),
                new RankHandler(),
                new ProfileHandler(),
                new ServerHandler(),
                new ChatFilterHandler(),
                new TagHandler(),
                new SkinHandler()
        ).forEach(module -> this.moduleHandler.registerModule(module));
    }
    
    private void loadHelpers() {
        saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);

        eLib.getInstance().getCommandHandler().registerAll(this);

        eLib.getInstance().getCommandHandler().registerParameterType(ProfileTarget.class, new ProfileTypeAdapter());
        eLib.getInstance().getCommandHandler().registerParameterType(Rank.class, new RankTypeAdapter(this.moduleHandler.getModule(RankHandler.class)));
        eLib.getInstance().getCommandHandler().registerParameterType(Tag.class, new TagTypeAdapter(this.moduleHandler.getModule(TagHandler.class)));
    }

    private void loadModules() {
        Arrays.asList(
                /*
                Listeners, Commands, Modules, etc.
                The module handler should be able to handle them all
                */
                new ProfileListener(),
                new PunishmentListener(),
                new StaffGriefListener(),
                new ChatFilterListener()
        ).forEach(module -> this.moduleHandler.registerModule(module));
    }

    public static Prime getInstance() {
        return instance;
    }
}
