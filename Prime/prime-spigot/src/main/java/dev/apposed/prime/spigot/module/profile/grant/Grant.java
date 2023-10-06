package dev.apposed.prime.spigot.module.profile.grant;

import com.google.gson.annotations.SerializedName;
import dev.apposed.prime.spigot.module.rank.Rank;
import dev.apposed.prime.spigot.util.ItemBuilder;
import dev.apposed.prime.spigot.util.time.DurationUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Data @AllArgsConstructor
public class Grant {

    @SerializedName("_id")
    private UUID id;

    private final Rank rank;

    private final UUID addedBy;
    private final long addedAt;
    private final String addedReason;
    private final long duration;
    private final List<String> scopes;

    private UUID removedBy;
    private long removedAt;
    private String removedReason;

    private boolean removed;
    private UUID player;

    public Grant(Rank rank, UUID addedBy, long addedAt, String addedReason, long duration, List<String> scopes) {
        this.id = UUID.randomUUID();
        this.rank = rank;

        this.addedBy = addedBy;
        this.addedAt = addedAt;
        this.addedReason = addedReason;
        this.duration = duration;
        this.scopes = scopes;
    }

    public Grant(UUID id, Rank rank, UUID addedBy, long addedAt, String addedReason, long duration, List<String> scopes, UUID removedBy, long removedAt, String removedReason, boolean removed) {
        this.id = id;
        this.rank = rank;
        this.addedBy = addedBy;
        this.addedAt = addedAt;
        this.addedReason = addedReason;
        this.duration = duration;
        this.scopes = scopes;
        this.removedBy = removedBy;
        this.removedAt = removedAt;
        this.removedReason = removedReason;
        this.removed = removed;
    }

    // not using this idk why. ill change the old code to use method later on
    public void removeGrant(UUID removedBy, long removedAt, String removedReason) {
        this.removedBy = removedBy;
        this.removedAt = removedAt;
        this.removedReason = removedReason;

        this.removed = true;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Grant grant = (Grant) o;
        return addedAt == grant.addedAt && duration == grant.duration && removedAt == grant.removedAt && removed == grant.removed && Objects.equals(id, grant.id) && Objects.equals(rank, grant.rank) && Objects.equals(addedBy, grant.addedBy) && Objects.equals(addedReason, grant.addedReason) && Objects.equals(scopes, grant.scopes) && Objects.equals(removedBy, grant.removedBy) && Objects.equals(removedReason, grant.removedReason) && Objects.equals(player, grant.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rank, addedBy, addedAt, addedReason, duration, scopes, removedBy, removedAt, removedReason, removed, player);
    }
}
