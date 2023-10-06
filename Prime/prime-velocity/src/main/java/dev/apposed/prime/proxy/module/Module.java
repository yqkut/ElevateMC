package dev.apposed.prime.proxy.module;

import dev.apposed.prime.proxy.PrimeProxy;
import lombok.Getter;
import lombok.Setter;
import ninja.leaping.configurate.ConfigurationNode;

@Getter @Setter
public abstract class Module {

    private String name = this.getClass().getSimpleName();
    private String version = "1.0.0";

    private PrimeProxy plugin = PrimeProxy.getInstance();

    private ConfigurationNode config = plugin.getConfig();
    private ModuleHandler moduleHandler = plugin.getModuleHandler();

    public void onEnable() {
        System.out.println("Module " + name + " v" + version + " has been enabled.");
    }

    public void onDisable() {
        System.out.println("Module " + name + " v" + version + " has been disabled.");
    }
}