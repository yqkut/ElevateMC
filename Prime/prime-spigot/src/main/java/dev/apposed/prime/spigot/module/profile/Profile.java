package dev.apposed.prime.spigot.module.profile;

import com.elevatemc.elib.util.TimeUtils;
import com.google.gson.annotations.SerializedName;
import dev.apposed.prime.spigot.Prime;
import dev.apposed.prime.spigot.PrimeConstants;
import dev.apposed.prime.spigot.module.rank.Rank;
import dev.apposed.prime.spigot.module.tag.Tag;
import dev.apposed.prime.spigot.module.profile.grant.Grant;
import dev.apposed.prime.spigot.module.profile.identity.ProfileIdentity;
import dev.apposed.prime.spigot.module.profile.punishment.Punishment;
import dev.apposed.prime.spigot.module.profile.punishment.type.PunishmentType;
import dev.apposed.prime.spigot.module.rank.meta.RankMeta;
import dev.apposed.prime.spigot.module.tag.TagHandler;
import dev.apposed.prime.spigot.util.Color;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

@Data @AllArgsConstructor
public class Profile {

    @SerializedName("_id")
    private UUID uuid;

    private String username;
    private String email;

    private Set<Grant> grants;
    private Set<Punishment> punishments;
    private Set<String> permissions;
    private String tag;
    private Set<ProfileIdentity> identities;

    private boolean messagesToggled;
    private boolean online;
    private String lastServer;
    private long lastOnline;
    private ProfileIdentity lastIdentity;
    private long lastChattedAt;
    private long firstJoin;
    private long playtime;

    private int syncCode;
    private String password;
    private ChatColor chatColor = ChatColor.WHITE;
    private String nickname = "";
    private String style = "";

    public Profile(Player player) {
        this.uuid = player.getUniqueId();
        this.username = player.getName();

        this.grants = new HashSet<>();
        this.punishments = new HashSet<>();
        this.permissions = new HashSet<>();
        this.identities = new HashSet<>();
    }

    public Profile(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;

        this.grants = new HashSet<>();
        this.punishments = new HashSet<>();
        this.permissions = new HashSet<>();
        this.identities = new HashSet<>();
    }

    public Profile(UUID uuid, String username, String email, Set<Grant> grants, Set<Punishment> punishments, Set<String> permissions, String tag, Set<ProfileIdentity> identities, boolean messagesToggled, boolean online, String lastServer, long lastOnline, ProfileIdentity lastIdentity, long lastChattedAt) {
        this.uuid = uuid;
        this.username = username;
        this.email = email;
        this.grants = grants;
        this.punishments = punishments;
        this.permissions = permissions;
        this.tag = tag;
        this.identities = identities;
        this.messagesToggled = messagesToggled;
        this.online = online;
        this.lastServer = lastServer;
        this.lastOnline = lastOnline;
        this.lastIdentity = lastIdentity;
        this.lastChattedAt = lastChattedAt;
    }

    public List<Grant> getActiveGrants() {
        return this.grants.stream().filter(Grant::isActive).collect(Collectors.toList());
    }

    public Grant getHighestActiveGrant() {
        return this.getActiveGrants().stream().max(Comparator.comparingInt(a -> a.getRank().getWeight())).get();
    }

    public Grant getHighestActiveNonHiddenGrant() {
        return this.getActiveGrants().stream().filter(grant -> !grant.getRank().hasMeta(RankMeta.HIDDEN, true)).max(Comparator.comparingInt(a -> a.getRank().getWeight())).get();
    }

    public Grant highestGrantOnScope(String scope) {
        return this.getActiveGrants().stream().filter(grant -> !grant.getRank().hasMeta(RankMeta.HIDDEN, true)).filter(grant -> grant.getScopes().contains("Global") || grant.getScopes().contains(scope)).max(Comparator.comparingInt(a -> a.getRank().getWeight())).get();
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

    public boolean checkGrants() {
        boolean updated = false;

        // fix ConcurrentModificationException
        final List<Grant> grants = new ArrayList<>(this.grants);
        for(int i=0; i<grants.size(); i++) {
            final Grant grant = grants.get(i);
            if(!grant.isActive() && !grant.isRemoved()) {
                updated = true;
                grant.setRemoved(true);
                grant.setRemovedAt(grant.getAddedAt() + grant.getDuration());
                grant.setRemovedBy(PrimeConstants.CONSOLE_UUID);
                grant.setRemovedReason("Expired");

                Player player = Bukkit.getPlayer(this.uuid);
                if(player != null && player.isOnline()) {
                    player.sendMessage(Color.translate("&aYour &r" + grant.getRank().getColoredDisplay() + " &arank grant has expired."));
                }
            }
        }
        // Old Code (Caused ConcurrentModificationException)
//        for (Grant grant : getGrants()) {
//            if(!grant.isActive() && !grant.isRemoved()) {
//                updated = true;
//                grant.setRemoved(true);
//                grant.setRemovedAt(grant.getAddedAt() + grant.getDuration());
//                grant.setRemovedBy(PrimeConstants.CONSOLE_UUID);
//                grant.setRemovedReason("Expired");
//
//                Player player = Bukkit.getPlayer(this.uuid);
//                if(player != null && player.isOnline()) {
//                    player.sendMessage(Color.translate("&aYour &r" + grant.getRank().getColoredDisplay() + " &arank grant has expired."));
//                }
//            }
//        }

        return updated;
    }

    public void checkPunishments() {
        this.getPunishments().forEach(punishment -> {
            if(!punishment.isActive() && !punishment.isRemoved()) {
                punishment.setRemoved(true);
                punishment.setRemovedAt(punishment.getAddedAt() + punishment.getDuration());
                punishment.setRemovedBy(PrimeConstants.CONSOLE_UUID);
                punishment.setRemovedReason("Expired");
            }
        });
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

    public boolean hasPermission(String permission) {
        final List<String> permissions = new ArrayList<>(this.permissions);
        this.getActiveGrants().stream().map(Grant::getRank).forEach(rank -> permissions.addAll(rank.getFullPermissions()));

        return permissions.contains(permission);
    }

    public String getColoredName() {
        return this.getHighestActiveNonHiddenGrant().getRank().getColor() + getActiveName();
    }

    public String getColoredName(String scope) {
        return this.highestGrantOnScope(scope).getRank().getColor() + getActiveName();
    }

    public String getActiveName() {
        if(hasNickname()) return nickname;
        return username;
    }

    public String getSpecialPrefix() {
        Rank rank = this.getActiveGrants()
                .stream()
                .map(Grant::getRank)
                .filter(grantRank -> grantRank.hasMeta(RankMeta.PREFIX, true))
                .findFirst()
                .orElse(null);

        if(rank == null) return "";
        return rank.getPrefix();
    }

    public boolean isStaff() {
        return this.getActiveGrants().stream().anyMatch(grant -> grant.getRank().hasMeta(RankMeta.STAFF, true));
    }

    public boolean hasMeta(RankMeta meta) {
        return this.getActiveGrants().stream().anyMatch(grant -> grant.getRank().hasMeta(meta, true));
    }

    public boolean requiresProof(PunishmentType meta) {
        return this.getActiveGrants().stream().anyMatch(grant -> grant.getRank().requiresProof(meta));
    }

    public boolean hasIdentity(String address) {
        return this.getIdentity(address) != null;
    }

    public ProfileIdentity getIdentity(String address) {
        return this.identities.stream().filter(identity -> identity.getIp().equalsIgnoreCase(address)).findFirst().orElse(null);
    }

    public boolean isMessagesToggled() {
        return this.getPlayer() != null && this.getPlayer().hasMetadata("messagesToggled");
    }

    public void setMessagesToggled(boolean messagesToggled) {
        this.messagesToggled = messagesToggled;
        if(messagesToggled) {
            getPlayer().setMetadata("messagesToggled", new FixedMetadataValue(Prime.getInstance(), true));
            return;
        }

        getPlayer().removeMetadata("messagesToggled", Prime.getInstance());
    }

    public boolean hasActiveTag() {
        return this.tag != null;
    }

    public Tag getActiveTag() {
        return Prime.getInstance().getModuleHandler().getModule(TagHandler.class).getTag(this.tag.toUpperCase());
    }

    public boolean hasStyle() {
        return this.style != null && this.style.length() > 0;
    }

    public boolean hasNickname() {
        return this.nickname.length() > 0;
    }

    public String getHashedIp(String ip) {
        if(ip == null) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(ip.getBytes());

            BigInteger no = new BigInteger(1, messageDigest);

            StringBuilder hashtext = new StringBuilder(no.toString(16));
            while (hashtext.length() < 32) {
                hashtext.insert(0, "0");
            }
            return hashtext.toString();
        }

        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String getLastHashedIp() {
        return getHashedIp(lastIdentity.getIp());
    }

    public List<String> getAllHashedIps() {
        return this.identities
                .stream()
                .map(identity -> getHashedIp(identity.getIp()))
                .collect(Collectors.toList());
    }

    public String getPlaytimeString() {
        final int playtimeSec = (int) playtime / 1000;
        return TimeUtils.formatIntoDetailedString(playtimeSec);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profile profile = (Profile) o;
        return com.google.common.base.Objects.equal(uuid, profile.uuid);
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(uuid);
    }
}
