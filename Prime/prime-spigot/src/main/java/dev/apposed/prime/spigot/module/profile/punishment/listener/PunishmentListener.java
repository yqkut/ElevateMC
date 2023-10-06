package dev.apposed.prime.spigot.module.profile.punishment.listener;

import dev.apposed.prime.spigot.module.listener.ListenerModule;
import dev.apposed.prime.spigot.module.profile.Profile;
import dev.apposed.prime.spigot.module.profile.ProfileHandler;
import dev.apposed.prime.spigot.module.profile.punishment.Punishment;
import dev.apposed.prime.spigot.module.profile.punishment.type.PunishmentType;
import dev.apposed.prime.spigot.module.rank.meta.RankMeta;
import dev.apposed.prime.spigot.module.server.ServerHandler;
import dev.apposed.prime.spigot.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Optional;

public class PunishmentListener extends ListenerModule {

    private final ProfileHandler profileHandler;
    private final ServerHandler serverHandler;

    private final String networkName, appealLink;

    public PunishmentListener() {
        this.profileHandler = getModuleHandler().getModule(ProfileHandler.class);
        this.serverHandler = getModuleHandler().getModule(ServerHandler.class);
        this.networkName = getPlugin().getConfig().getString("network.name");
        this.appealLink = getPlugin().getConfig().getString("network.appeal");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        final Player player = event.getPlayer();
        profileHandler.load(player.getUniqueId()).thenAccept(profile -> {
            if(profile.hasActivePunishment(PunishmentType.BLACKLIST)) {
                Optional<Punishment> punishmentOptional = profile.getActivePunishment(PunishmentType.BLACKLIST);
                if(punishmentOptional.isPresent()) {
                    player.kickPlayer(
                            Color.translate("&cYour account has been blacklisted from the " + networkName + ".\n\n&cThis type of punishment is not appealable.")
                    );
                    return;
                }
            }

            if (serverHandler.getCurrentScope().getId().equalsIgnoreCase("hubs")) return;

            if(profile.hasActivePunishment(PunishmentType.BAN)) {
                Optional<Punishment> punishmentOptional = profile.getActivePunishment(PunishmentType.BAN);
                if(punishmentOptional.isPresent()) {
                    Punishment punishment = punishmentOptional.get();
                    if(punishment.getRemaining() == Long.MAX_VALUE) {
                        player.kickPlayer(
                                Color.translate("&cYour account has been permanently suspended from the " + networkName + ".\n\n&cAppeal on " + appealLink + ".")
                        );
                    } else {
                        player.kickPlayer(
                                Color.translate("&cYour account has been suspended from the " + networkName + " for " + punishment.formatDuration() + ".\n\n&cAppeal on " + appealLink + ".")
                        );
                    }
                    return;
                }
            }

            // identity check - if they have a rank that allows them to bypass ip bans this will be skipped
            if(!profile.hasMeta(RankMeta.IP_BYPASS)) {
                profile.getIdentities().forEach(identity -> {
                    Optional<Profile> punishedIdentityOptional = this.profileHandler.identityIsPunished(identity);
                    if (punishedIdentityOptional.isPresent()) {
                        Profile punishedProfile = punishedIdentityOptional.get();

                        if (punishedProfile.hasActivePunishment(PunishmentType.BLACKLIST)) {
                            Optional<Punishment> punishmentOptional = punishedProfile.getActivePunishment(PunishmentType.BLACKLIST);
                            if (punishmentOptional.isPresent()) {
                                player.kickPlayer(
                                        Color.translate("&cYour account has been blacklisted from the " + networkName + ".\n\n&cThis type of punishment is not appealable.")
                                );
                                return;
                            }
                        }

                        if (punishedProfile.hasActivePunishment(PunishmentType.BAN)) {
                            Optional<Punishment> punishmentOptional = punishedProfile.getActivePunishment(PunishmentType.BAN);
                            if (punishmentOptional.isPresent()) {
                                Punishment punishment = punishmentOptional.get();
                                if (punishment.getRemaining() == Long.MAX_VALUE) {
                                    player.kickPlayer(
                                            Color.translate("&cYour account has been permanently suspended from the " + networkName + "\n&cThis punishment is in relation to &r" + punishedProfile.getColoredName() + "\n&cAppeal on " + appealLink + ".")
                                    );
                                } else {
                                    player.kickPlayer(
                                            Color.translate("&cYour account has been suspended from the " + networkName + " for " + punishment.formatDuration() + "\n&cThis punishment is in relation to &r" + punishedProfile.getColoredName() + "\n&cAppeal on " + appealLink + ".")
                                    );
                                }
                                return;
                            }
                        }
                    }
                });
            }
        }).exceptionally(throwable -> {
            player.kickPlayer(Color.translate("&cYour profile was not loaded properly."));
            return null;
        });
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        final Optional<Profile> profileOptional = profileHandler.getProfile(player.getUniqueId());
        if(!profileOptional.isPresent()) {
            player.sendMessage(Color.translate("&cYour profile is not loaded properly. Try reconnecting."));
            event.setCancelled(true);
            return;
        }

        final Profile profile = profileOptional.get();

        if(profile.hasActivePunishment(PunishmentType.MUTE)) {
            Optional<Punishment> punishmentOptional = profile.getActivePunishment(PunishmentType.MUTE);
            if(punishmentOptional.isPresent()) {
                Punishment punishment = punishmentOptional.get();
                if(punishment.getRemaining() == Long.MAX_VALUE) {
                    player.sendMessage(Color.translate("&cYou are muted forever."));
                } else {
                    player.sendMessage(Color.translate("&cYou are muted for " + punishment.formatDuration()));
                }

                event.setCancelled(true);
            }
            return;
        }

        if(profile.hasActivePunishment(PunishmentType.GHOSTMUTE)) {
            event.setCancelled(true);
            player.sendMessage(event.getFormat());
        }
    }
}
