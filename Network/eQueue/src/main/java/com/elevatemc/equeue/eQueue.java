package com.elevatemc.equeue;

import com.elevatemc.equeue.command.eQueueCommand;
import com.elevatemc.equeue.listener.GeneralListener;
import com.elevatemc.equeue.queue.QueueHandler;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import dev.apposed.prime.proxy.PrimeProxy;
import dev.apposed.prime.proxy.module.profile.ProfileHandler;
import lombok.Getter;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Plugin(
        id = "equeue",
        name = "eQueue",
        description = "A simple single instance queue plugin.",
        version = "RELEASE",
        url = "https://elevatemc.com",
        authors = {"ElevateMC Development Team"},
        dependencies = {
                @Dependency(id = "primeproxy")
        }
)
public class eQueue {
    @Getter private static eQueue instance;
    @Getter private QueueHandler queueHandler;
    @Getter private ConfigurationNode config;

    private Path dataDirectory;

    @Getter private ProfileHandler profileHandler;

    @Getter private final ProxyServer server;
    @Getter private final Logger logger;

    @Getter MinecraftChannelIdentifier channel = MinecraftChannelIdentifier.create("equeue", "main");

    @Inject
    public eQueue(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        instance = this;
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;

        // profileHandler = prime.getModuleHandler().getModule(ProfileHandler.class);
    }

    @Subscribe
    public void onInitialize(ProxyInitializeEvent e) {
        loadConfig();

        profileHandler = PrimeProxy.getInstance().getModuleHandler().getModule(ProfileHandler.class);
        queueHandler = new QueueHandler();
        getQueueHandler().loadQueues();

        server.getChannelRegistrar().register(channel);
        server.getEventManager().register(this, new GeneralListener());
        server.getCommandManager().register(server.getCommandManager().metaBuilder("equeue").build(), new eQueueCommand());
    }

    public void reloadConfigAndQueues() {
        this.loadConfig();
        getQueueHandler().loadQueues();
    }

    private void loadConfig() {
        if (!dataDirectory.toFile().exists())
            dataDirectory.toFile().mkdir();

        File file = dataDirectory.resolve("config.yml").toFile();


        if (!file.exists()) {
            try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        YAMLConfigurationLoader configurationLoader = YAMLConfigurationLoader.builder().setFile(file).build();
        try {
            config = configurationLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
