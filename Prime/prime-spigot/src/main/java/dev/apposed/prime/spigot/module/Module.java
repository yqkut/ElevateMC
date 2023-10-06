package dev.apposed.prime.spigot.module;

import dev.apposed.prime.spigot.Prime;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter @Setter
public abstract class Module {

    private String name = this.getClass().getSimpleName();
    private String version = "1.0.0";

    private Prime plugin = JavaPlugin.getPlugin(Prime.class);

    private ModuleHandler moduleHandler = plugin.getModuleHandler();

    public void onEnable() {
        System.out.println("Module " + name + " v" + version + " has been enabled.");
    }

    public void onDisable() {
        System.out.println("Module " + name + " v" + version + " has been disabled.");
    }
}