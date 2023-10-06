package dev.apposed.prime.proxy;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.apposed.prime.proxy.module.ModuleHandler;
import dev.apposed.prime.proxy.module.command.MaintenanceCommand;
import dev.apposed.prime.proxy.module.velocity.VelocityHandler;
import dev.apposed.prime.proxy.module.command.ServerCommand;
import dev.apposed.prime.proxy.module.database.mongo.MongoModule;
import dev.apposed.prime.proxy.module.database.redis.JedisModule;
import dev.apposed.prime.proxy.module.profile.ProfileHandler;
import dev.apposed.prime.proxy.module.profile.listener.ProfileListener;
import dev.apposed.prime.proxy.module.rank.RankHandler;
import dev.apposed.prime.proxy.module.server.ServerHandler;
import dev.apposed.prime.proxy.module.velocity.listener.VelocityListener;
import lombok.Getter;
import lombok.SneakyThrows;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.slf4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Getter
@Plugin(id = "primeproxy", name = "Prime", version = "1.0-BETA", description = "All in one Essentials core.", authors = {"Apposed"})
public class PrimeProxy {

    @Getter private static PrimeProxy instance;

    private ModuleHandler moduleHandler;
    private YAMLConfigurationLoader configurationLoader, maintenanceLoader;
    private ConfigurationNode config, maintenance;

    private final ProxyServer server;
    private final Logger logger;
    private Path dataDirectory;

    @Inject
    public PrimeProxy(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        instance = this;
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onInitialize(ProxyInitializeEvent e) {
        loadHelpers();
        loadHandlers();
        registerCommands();
    }

    @SneakyThrows
    @Subscribe
    public void onShutdown(ProxyShutdownEvent e) {
        configurationLoader.save(config);
        maintenanceLoader.save(maintenance);
    }

    private void loadHandlers() {
        this.moduleHandler = new ModuleHandler();

        Arrays.asList(
                new MongoModule(),
                new JedisModule(),
                new RankHandler(),
                new ProfileHandler(),
                new ServerHandler(),
                new VelocityHandler()
        ).forEach(module -> this.moduleHandler.registerModule(module));
    }

    @SneakyThrows
    private void loadHelpers() {
        if (!dataDirectory.toFile().exists())
            dataDirectory.toFile().mkdir();

        File configFile = dataDirectory.resolve("config.yml").toFile();
        if (!configFile.exists()) {
            configFile.createNewFile();
            try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.yml")) {
                Files.copy(in, configFile.toPath());
            } catch (IOException e) {
                throw new RuntimeException("Unable to create configuration file", e);
            }
        }

        File maintenanceFile = dataDirectory.resolve("maintenance.yml").toFile();
        if(!maintenanceFile.exists()) {
            maintenanceFile.createNewFile();
            try (InputStream in = getClass().getClassLoader().getResourceAsStream("maintenance.yml")) {
                Files.copy(in, maintenanceFile.toPath());
            } catch (IOException e) {
                throw new RuntimeException("Unable to create maintenance file", e);
            }
        }

        configurationLoader = YAMLConfigurationLoader.builder().setFile(configFile).build();
        maintenanceLoader = YAMLConfigurationLoader.builder().setFile(maintenanceFile).build();

        try {
            config = configurationLoader.load();
            maintenance = maintenanceLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerCommands() {
        server.getEventManager().register(this, new ProfileListener(this));
        server.getEventManager().register(this, new VelocityListener());
        server.getCommandManager().register(server.getCommandManager().metaBuilder("server").build(), new ServerCommand(this));
        server.getCommandManager().register(server.getCommandManager().metaBuilder("maintenance").build(), new MaintenanceCommand(this));
    }

    public boolean isInMaintenance() {
        return maintenance.getNode("enabled").getBoolean();
    }

    @SneakyThrows
    public List<String> getWhitelistedUsernames() {
        return maintenance.getNode("whitelist").getList(TypeToken.of(String.class));
    }
}
