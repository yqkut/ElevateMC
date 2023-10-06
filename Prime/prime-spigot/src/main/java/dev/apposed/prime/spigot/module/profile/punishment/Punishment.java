package dev.apposed.prime.spigot.module.profile.punishment;

import dev.apposed.prime.spigot.module.profile.punishment.evidence.PunishmentEvidence;
import dev.apposed.prime.spigot.module.profile.punishment.type.PunishmentType;
import dev.apposed.prime.spigot.util.ItemBuilder;
import dev.apposed.prime.spigot.util.time.DurationUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Data @AllArgsConstructor
public class Punishment {

    private final PunishmentType type;

    private final UUID addedBy;
    private final long addedAt;
    private final String addedReason;
    private final long duration;

    private Set<PunishmentEvidence> evidence;

    private UUID removedBy;
    private long removedAt;
    private String removedReason;

    private String ip;
    private boolean removed;
    private UUID player;

    public Punishment(PunishmentType type, UUID addedBy, long addedAt, String addedReason, long duration) {
        this.type = type;

        this.addedBy = addedBy;
        this.addedAt = addedAt;
        this.addedReason = addedReason;
        this.duration = duration;

        this.evidence = new HashSet<>();
    }

    public Punishment(PunishmentType type, UUID addedBy, long addedAt, String addedReason, long duration, Set<PunishmentEvidence> evidence, UUID removedBy, long removedAt, String removedReason, boolean removed) {
        this.type = type;
        this.addedBy = addedBy;
        this.addedAt = addedAt;
        this.addedReason = addedReason;
        this.duration = duration;
        this.evidence = evidence;
        this.removedBy = removedBy;
        this.removedAt = removedAt;
        this.removedReason = removedReason;
        this.removed = removed;
    }

    public PunishmentEvidence addEvidence(String link, UUID addedBy, long addedAt) {
        PunishmentEvidence evidence = new PunishmentEvidence(link, addedBy, addedAt);
        this.evidence.add(evidence);
        return evidence;
    }

    public void removePunishment(UUID removedBy, long removedAt, String removedReason) {
        this.removed = true;

        this.removedBy = removedBy;
        this.removedAt = removedAt;
        this.removedReason = removedReason;
    }

    public boolean isActive() {
        if(!removed) {
            if(this.duration == Long.MAX_VALUE) return true;
            return System.currentTimeMillis() <= (this.addedAt + this.duration);
        }

        return false;
    }

    public long getRemaining() {
        if(removed) return 0L;
        if(duration == Long.MAX_VALUE) return Long.MAX_VALUE;
        if(!isActive()) return 0L;

        return (addedAt + duration) - System.currentTimeMillis();
    }

    public String formatDuration() {
        if(this.duration == Long.MAX_VALUE) return "Permanent";
        return DurationUtils.toString(this.addedAt + this.duration);
    }

    public ItemStack getItemStack() {
        ItemBuilder builder = new ItemBuilder(Material.WOOL);
        if(removed && !isActive()) {
            builder.dur(14);
        } else {
            builder.dur(13);
        }

        return builder.build();
    }

    public boolean hasIp() {
        return ip != null && ip.length() > 0;
    }
}
