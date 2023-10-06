package com.elevatemc.elib.combatlogger;

import com.elevatemc.elib.eLib;
import com.elevatemc.elib.combatlogger.adapter.CombatLoggerAdapter;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CombatLogger {

    public static final String COMBAT_LOGGER_METADATA = "eLib-CombatLogger";

    @Getter private final String playerName;
    @Getter private final UUID playerUuid;
    @Getter private final ItemStack[] armor;
    @Getter private final ItemStack[] inventory;
    @Getter private double health = 20.0D;
    @Getter private final Set<PotionEffect> effects = new HashSet();
    @Getter private  long despawnTime;
    @Getter private EntityType entityType;
    @Getter private String nameFormat;
    @Getter private CombatLoggerAdapter eventAdapter;
    @Getter private LivingEntity spawnedEntity;

    public CombatLogger(Player player,long time,TimeUnit unit) {
        this.entityType = EntityType.VILLAGER;
        this.nameFormat = ChatColor.YELLOW + "%s";
        this.playerName = player.getName();
        this.playerUuid = player.getUniqueId();
        this.armor = player.getInventory().getArmorContents();
        this.inventory = player.getInventory().getContents();
        this.despawnTime = unit.toSeconds(time);
    }

    public CombatLogger setDespawnTime(long time, TimeUnit unit) {
        this.despawnTime = unit.toSeconds(time);
        return this;
    }

    public CombatLogger setEntityType(EntityType entityType) {
        if (!entityType.isAlive() && !entityType.isSpawnable()) {
            throw new IllegalArgumentException("EntityType must be living and spawnable!");
        } else {
            this.entityType = entityType;
            return this;
        }
    }

    public CombatLogger setHealth(double health) {
        this.health = health;
        return this;
    }

    public CombatLogger setNameFormat(String nameFormat) {
        this.nameFormat = nameFormat;
        return this;
    }

    public CombatLogger setPotionEffects(Collection<PotionEffect> effects) {
        this.effects.addAll(effects);
        return this;
    }

    public CombatLogger setAdapter(CombatLoggerAdapter adapter) {
        this.eventAdapter = adapter;
        return this;
    }

    public LivingEntity spawn(Location location) {

        final LivingEntity entity = (LivingEntity)location.getWorld().spawnEntity(location, this.entityType);

        entity.setMetadata(COMBAT_LOGGER_METADATA, new FixedMetadataValue(eLib.getInstance(), "001100010010011110100001"));

        eLib.getInstance().getCombatLoggerHandler().getCombatLoggerMap().put(entity.getUniqueId(), this);
        eLib.getInstance().getCombatLoggerHandler().getCombatLoggerMap().put(this.playerUuid, this);

        entity.setCustomName(String.format(this.nameFormat, this.playerName));
        entity.setCustomNameVisible(true);
        entity.setCanPickupItems(false);
        entity.addPotionEffects(this.effects);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2147483647, 100), true);
        entity.setMaxHealth(this.health + 2.0D);
        entity.setHealth(this.health);

        new BukkitRunnable() {

            @Override
            public void run() {

                if (!entity.isDead() && entity.isValid()) {
                    entity.remove();
                    eLib.getInstance().getCombatLoggerHandler().getCombatLoggerMap().remove(entity.getUniqueId());
                    eLib.getInstance().getCombatLoggerHandler().getCombatLoggerMap().remove(this);
                }

            }
        }.runTaskLater(eLib.getInstance(), this.despawnTime * 20L);

        this.spawnedEntity = entity;

        return entity;
    }


}
