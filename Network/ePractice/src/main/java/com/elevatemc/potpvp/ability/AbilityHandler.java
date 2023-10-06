package com.elevatemc.potpvp.ability;

import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;

import com.elevatemc.elib.fake.impl.player.FakePlayerPacketHandler;
import com.elevatemc.elib.util.ClassUtils;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.ability.listener.AbilityListener;
import com.elevatemc.potpvp.ability.packet.InvisibilityPacketAdapter;
import com.elevatemc.potpvp.util.Cooldown;
import com.elevatemc.spigot.eSpigot;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class AbilityHandler {

    @Getter
    private final Map<Ability, Integer> usedItems = new HashMap<>();
    @Getter
    private final Map<String, Ability> abilities = new HashMap<>();
    @Getter
    private final Table<UUID, String, Long> lastUsedItem = HashBasedTable.create();

    @Getter
    public static final Table<UUID, Ability, Cooldown> cooldown = HashBasedTable.create();

    private File file;
    private FileConfiguration data;

    public AbilityHandler(PotPvPSI instance) {
        for (Class<?> clazz : ClassUtils.getClassesInPackage(PotPvPSI.getInstance(), "com.elevatemc.potpvp.ability.type")) {

            if (!Ability.class.isAssignableFrom(clazz)) {
                continue;
            }

            try {
                final Ability ability = (Ability) clazz.newInstance();

                this.abilities.put(ability.getName(), ability);
            } catch (InstantiationException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }

        instance.getServer().getPluginManager().registerEvents(new AbilityListener(), instance);

        instance.getServer().getScheduler().runTaskLater(instance, this::loadStatistics, 20);

        eSpigot.getInstance().addPacketHandler(new InvisibilityPacketAdapter());
    }

    public void loadStatistics() {
        this.file = new File(PotPvPSI.getInstance().getDataFolder(), "ability-stats.yml");
        this.data = YamlConfiguration.loadConfiguration(this.file);

        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if (this.data.get("abilities") == null) {
            return;
        }

        for (String key : data.getConfigurationSection("abilities").getKeys(false)) {
            final Ability ability = this.fromName(key);

            if (ability == null) {
                continue;
            }

            this.usedItems.put(ability, data.getInt("abilities." + key));
        }
    }

    public void saveStatistics() {
        Map<String, Object> configValues = this.data.getValues(false);
        for (Map.Entry<String, Object> entry : configValues.entrySet())
            this.data.set(entry.getKey(), null);

        for (Map.Entry<Ability, Integer> abilityIntegerEntry : this.usedItems.entrySet()) {
            this.data.set("abilities." + abilityIntegerEntry.getKey().getName(), +abilityIntegerEntry.getValue());
        }

        try {
            this.data.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Ability fromName(String name) {
        return this.abilities.values().stream().filter(ability -> ability.getName().equalsIgnoreCase(name)).findAny().orElse(null);
    }

    @Command(
            names = {"ability give"},
            permission = "foxtrot.command.ability"
    )
    public static void execute(CommandSender sender,
                               @Parameter(name = "ability") Ability ability,
                               @Parameter(name = "amount", defaultValue = "1") int amount,
                               @Parameter(name = "player", defaultValue = "self") Player target) {
        final ItemStack itemStack = ability.hassanStack.clone();
        itemStack.setAmount(amount);
        target.getInventory().addItem(itemStack);

    }

    @Command(names = {"ability reset"}, permission = "foxtrot.command.ability.reset")
    public static void reset(Player player, @Parameter(name = "ability") Ability ability) {
        if (!getCooldown().contains(player.getUniqueId(), ability)) {
            player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You don't have a cooldown for " + ability.getDisplayName() + ChatColor.RED + ".");
            return;
        }

        getCooldown().remove(player.getUniqueId(), ability);

        player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Removed cooldown for the " + ability.getDisplayName() + ChatColor.RED + ".");
    }

    public void applyCooldown(Ability ability, Player player) {
        this.usedItems.putIfAbsent(ability, 0);
        this.usedItems.replace(ability, this.usedItems.get(ability) + 1);

        if (ability.getCooldown() <= 0) {
            return;
        }

        cooldown.put(player.getUniqueId(), ability, new Cooldown(ability.getCooldown()));
    }

    public void applyCooldown(Ability ability, Player player, long time) {
        if (ability.getCooldown() <= 0) {
            return;
        }

        cooldown.put(player.getUniqueId(), ability, new Cooldown(ability.getCooldown()));
    }

    public boolean hasCooldown(Ability ability, Player player) {
        if (!cooldown.contains(player.getUniqueId(), ability)) {
            return false;
        }

        return !cooldown.get(player.getUniqueId(), ability).hasExpired();
    }

    public long getRemaining(Ability ability, Player player) {
        if (!cooldown.contains(player.getUniqueId(), ability)) {
            return 0L;
        }

        return cooldown.get(player.getUniqueId(), ability).getRemaining();
    }

    public void resetCooldowns(Player player) {
        cooldown.rowMap().forEach((uuid, abilityMap) -> {
            if (player.getUniqueId().equals(uuid)) {
                abilityMap.keySet().forEach(ability -> {
                    cooldown.remove(uuid, ability);
                });
            }
        });
    }
}
