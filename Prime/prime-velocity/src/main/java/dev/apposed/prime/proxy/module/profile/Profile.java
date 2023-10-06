package dev.apposed.prime.proxy.module.profile;

import com.google.gson.annotations.SerializedName;
import com.velocitypowered.api.proxy.Player;
import dev.apposed.prime.proxy.PrimeProxy;
import dev.apposed.prime.proxy.module.profile.grant.Grant;
import dev.apposed.prime.proxy.module.profile.punishment.Punishment;
import dev.apposed.prime.proxy.module.profile.punishment.type.PunishmentType;
import dev.apposed.prime.proxy.module.rank.Rank;
import dev.apposed.prime.proxy.module.rank.meta.RankMeta;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class Profile {

    @SerializedName("_id")
    private UUID uuid;

    private String username;

    private Set<Grant> grants;
    private Set<String> permissions;
    private Set<Punishment> punishments;

    private boolean online;

    public Profile(Player player) {
        this.uuid = player.getUniqueId();
        this.username = player.getUsername();

        this.grants = new HashSet<>();
        this.permissions = new HashSet<>();
    }

    public Profile(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;

        this.grants = new HashSet<>();
        this.permissions = new HashSet<>();
    }

    public List<Grant> getActiveGrants() {
        return this.grants.stream().filter(Grant::isActive).collect(Collectors.toList());
    }

    public Grant getHighestActiveGrant() {
        return this.getActiveGrants().stream().max(Comparator.comparingInt(a -> a.getRank().getWeight())).get();
    }

    public Grant highestGrantOnScope(String scope) {
        return this.getActiveGrants().stream().filter(grant -> !grant.getRank().hasMeta(RankMeta.HIDDEN, true)).filter(grant -> grant.getScopes().contains("Global") || grant.getScopes().contains(scope)).max(Comparator.comparingInt(a -> a.getRank().getWeight())).get();
    }

    public boolean hasPermission(String permission) {
        final List<String> permissions = new ArrayList<>();

        permissions.addAll(this.permissions);
        this.getActiveGrants().stream().map(Grant::getRank).forEach(rank -> permissions.addAll(rank.getFullPermissions()));

        if (permissions.contains("*")) return true;

        return permissions.contains(permission);
    }

    public boolean hasRank(Rank rank) {
        return this.grants.stream().anyMatch(grant -> grant.getRank().getName().equalsIgnoreCase(rank.getName()));
    }

    public boolean hasMeta(RankMeta meta) {
        return this.getActiveGrants().stream().anyMatch(grant -> grant.getRank().hasMeta(meta, true));
    }

    public String getColoredName() {
        return this.getHighestActiveGrant().getRank() + this.getUsername();
    }


    public String getColoredName(String scope) {
        return this.highestGrantOnScope(scope).getRank().getColor() + this.getUsername();
    }

    public boolean isStaff() {
        return this.getActiveGrants().stream().anyMatch(grant -> grant.getRank().hasMeta(RankMeta.STAFF, true));
    }

    public boolean hasServerPerm() {
        return this.getActiveGrants().stream().anyMatch(grant -> grant.getRank().hasMeta(RankMeta.SERVER, true));
    }

    public Player getPlayer() {
        return PrimeProxy.getInstance().getServer().getPlayer(uuid).orElse(null);
    }

    public List<Punishment> getActivePunishments() {
        return this.punishments.stream().filter(Punishment::isActive).collect(Collectors.toList());
    }

    public List<Punishment> getActivePunishments(PunishmentType type) {
        return this.getActivePunishments().stream().filter(punishment -> punishment.getType() == type).collect(Collectors.toList());
    }

    public Optional<Punishment> getActivePunishment(PunishmentType type) {
        return this.getActivePunishments(type).stream().max(Comparator.comparingLong(Punishment::getDuration));
    }

    public boolean hasActivePunishment(PunishmentType type) {
        return this.getActivePunishments().stream().filter(punishment -> punishment.getType() == type).count() > 0;
    }
}