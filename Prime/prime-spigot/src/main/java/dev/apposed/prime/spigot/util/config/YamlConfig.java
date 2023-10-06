package dev.apposed.prime.spigot.util.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class YamlConfig extends YamlConfiguration {

    private JavaPlugin plugin;
    private String fileName;
    private File file;

    public YamlConfig(JavaPlugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;

        this.file = new File(plugin.getDataFolder(), fileName);

        createFile();
    }

    private void createFile() {
        try{
            if(!file.exists()) {
                if (plugin.getResource(fileName) != null) {
                    plugin.saveResource(fileName, false);
                } else {
                    this.save(file);
                }

                this.load(file);
                this.save(file);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void save() {
        try{
            this.save(file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void reload() {
        try{
            this.load(file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}