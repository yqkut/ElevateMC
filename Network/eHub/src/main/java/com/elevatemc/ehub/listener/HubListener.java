package com.elevatemc.ehub.listener;

import com.elevatemc.ehub.eHub;
import com.elevatemc.ehub.menu.cosmetics.CosmeticsMenu;
import com.elevatemc.ehub.menu.selector.ServerSelector;
import com.elevatemc.ehub.utils.HubItems;
import com.elevatemc.elib.util.Pair;
import dev.apposed.prime.spigot.module.profile.Profile;
import dev.apposed.prime.spigot.module.profile.ProfileHandler;
import dev.apposed.prime.spigot.module.profile.identity.ProfileIdentity;
import dev.apposed.prime.spigot.module.profile.punishment.Punishment;
import dev.apposed.prime.spigot.module.profile.punishment.type.PunishmentType;
import dev.apposed.prime.spigot.module.rank.meta.RankMeta;
import dev.apposed.prime.spigot.module.server.scoreboard.PrimeScoreboardStyle;
import dev.apposed.prime.spigot.util.Color;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Optional;

public class HubListener implements Listener {

    private static final ProfileHandler profileHandler = eHub.getInstance().getPrime().getModuleHandler().getModule(ProfileHandler.class);
    private static final String networkName = eHub.getInstance().getPrime().getConfig().getString("network.name");
    private static final String appealLink = eHub.getInstance().getPrime().getConfig().getString("network.appeal");

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        e.setJoinMessage(null);
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            player.hidePlayer(otherPlayer);
            otherPlayer.hidePlayer(player);
        }

        player.spigot().setCollidesWithEntities(false);
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(player.getWorld().getSpawnLocation());

        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        inventory.setArmorContents(null);
        if (player.hasMetadata("MUSIC_DISABLED")) {
            inventory.setItem(7, HubItems.MUSIC_DISABLED);
        } else {
            inventory.setItem(7, HubItems.MUSIC_ENABLED);
        }

        inventory.setItem(4, HubItems.COSMETICS);

        inventory.setItem(8, HubItems.ENDER_PEARLS);
        inventory.setHeldItemSlot(0);

        player.setWalkSpeed(0.3F);
        player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
        player.sendMessage(StringUtils.repeat(" \n", 100));

        profileHandler.load(player.getUniqueId()).thenAccept(profile -> {
            if (profile.hasActivePunishment(PunishmentType.BAN)) {
                player.setMetadata("banned", new FixedMetadataValue(eHub.getInstance(), true));
                Optional<Punishment> punishmentOptional = profile.getActivePunishment(PunishmentType.BAN);
                if (punishmentOptional.isPresent()) {
                    Punishment punishment = punishmentOptional.get();
                    if (punishment.getRemaining() == Long.MAX_VALUE) {
                        player.sendMessage(
                                ChatColor.translateAlternateColorCodes('&', "&cYour account has been permanently suspended from the " + networkName + " Network.\n\n&cAppeal on " + appealLink + ".")
                        );
                    } else {
                        player.sendMessage(
                                ChatColor.translateAlternateColorCodes('&', "&cYour account has been suspended from the " + networkName + " Network for " + punishment.formatDuration() + ".\n\n&cAppeal on " + appealLink + ".")
                        );
                    }
                    return;
                }
            }

            if (profile.hasActivePunishment(PunishmentType.BLACKLIST)) {
                player.setMetadata("banned", new FixedMetadataValue(eHub.getInstance(), true));
                Optional<Punishment> punishmentOptional = profile.getActivePunishment(PunishmentType.BAN);
                if (punishmentOptional.isPresent()) {
                    player.sendMessage(
                            ChatColor.translateAlternateColorCodes('&', "&cYour account has been permanently blacklisted from the " + networkName + " Network" + "\n&cAppeal on " + appealLink + ".")
                    );
                    player.setMetadata("related", new FixedMetadataValue(eHub.getInstance(), "related"));
                    return;
                }
            }

            // identity check - if they have a rank that allows them to bypass ip bans this will be skipped
            if (!profile.hasMeta(RankMeta.IP_BYPASS)) {
                for (ProfileIdentity identity : profile.getIdentities()) {
                    Optional<Profile> punishedIdentityOptional = profileHandler.identityIsPunished(identity);
                    if (punishedIdentityOptional.isPresent()) {
                        Profile punishedProfile = punishedIdentityOptional.get();

                        if (punishedProfile.hasActivePunishment(PunishmentType.BAN)) {
                            player.setMetadata("banned", new FixedMetadataValue(eHub.getInstance(), true));
                            Optional<Punishment> punishmentOptional = punishedProfile.getActivePunishment(PunishmentType.BAN);
                            if (punishmentOptional.isPresent()) {
                                Punishment punishment = punishmentOptional.get();
                                if (punishment.getRemaining() == Long.MAX_VALUE) {
                                    player.sendMessage(
                                            ChatColor.translateAlternateColorCodes('&', "&cYour account has been permanently suspended from the " + networkName + " Network\n&cThis punishment is in relation to &r" + punishedProfile.getColoredName() + "\n&cAppeal on " + appealLink + ".")
                                    );
                                } else {
                                    player.sendMessage(
                                            ChatColor.translateAlternateColorCodes('&', "&cYour account has been suspended from the " + networkName + " Network for " + punishment.formatDuration() + "\n&cThis punishment is in relation to &r" + punishedProfile.getColoredName() + "\n&cAppeal on " + appealLink + ".")
                                    );
                                }
                                player.setMetadata("related", new FixedMetadataValue(eHub.getInstance(), "related"));
                                return;
                            }
                        }

                        if (punishedProfile.hasActivePunishment(PunishmentType.BLACKLIST)) {
                            Optional<Punishment> punishmentOptional = punishedProfile.getActivePunishment(PunishmentType.BLACKLIST);
                            if (punishmentOptional.isPresent()) {
                                player.sendMessage(
                                        ChatColor.translateAlternateColorCodes('&', "&cYour account has been permanently blacklisted from the " + networkName + " Network\n&cThis punishment is in relation to &r" + punishedProfile.getColoredName() + "\n&cAppeal on " + appealLink + ".")
                                );
                                player.setMetadata("related", new FixedMetadataValue(eHub.getInstance(), "related"));
                                return;
                            }
                        }

                        if (!punishedProfile.hasActivePunishment(PunishmentType.BAN) && !punishedProfile.hasActivePunishment(PunishmentType.BLACKLIST)) {
                            player.removeMetadata("related", eHub.getInstance());
                        }
                    }
                }
            }

            if(!profile.hasActivePunishment(PunishmentType.BLACKLIST) && !profile.hasActivePunishment(PunishmentType.BAN) && !player.hasMetadata("related")) {
                player.removeMetadata("banned", eHub.getInstance());
            }
        });

        if(!player.hasMetadata("banned")) inventory.setItem(0, HubItems.SELECT_SERVER);

        final Pair<ChatColor, ChatColor> style = PrimeScoreboardStyle.getStyle(player);
        player.sendMessage("");
        player.sendMessage("" + style.getKey() + "Welcome to the " + style.getKey() + ChatColor.BOLD + "Elevate Network" + style.getValue() + "!");
        player.sendMessage("");
        player.sendMessage("  " + ChatColor.GRAY + "►" + style.getKey() + " Website: " + style.getValue() + "https://elevatemc.com");
        player.sendMessage("  " + ChatColor.GRAY + "►" + style.getKey() + " Store: " + style.getValue() + "https://store.elevatemc.com");
        player.sendMessage("  " + ChatColor.GRAY + "►" + style.getKey() + " Twitter: " + style.getValue() + "https://twitter.com/elevatemcnet");
        player.sendMessage("  " + ChatColor.GRAY + "►" + style.getKey() + " Discord: " + style.getValue() + "https://elevatemc.com/discord");
        player.sendMessage("  " + ChatColor.GRAY + "►" + style.getKey() + " Telegram: " + style.getValue() + "https://t.me/ElevateMC");
        player.sendMessage("  " + ChatColor.GRAY + "►" + style.getKey() + " NameMC: " + style.getValue() + "https://namemc.com/elevatemc.com");
        player.sendMessage("");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        e.setQuitMessage(null);
        Player player = e.getPlayer();
        eHub.getInstance().getQueueHandler().getPositions().remove(e.getPlayer().getUniqueId());
        if (player.getVehicle() != null) {
            player.getVehicle().leaveVehicle();
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (!e.hasItem() || !e.getAction().name().contains("RIGHT_")) {
            return;
        }

        ItemStack item = e.getItem();

        if(e.getPlayer().hasMetadata("banned")) return;

        if (item.isSimilar(HubItems.SELECT_SERVER)) {
            new ServerSelector().openMenu(e.getPlayer());
            return;
        }

        if (item.isSimilar(HubItems.COSMETICS)) {
            new CosmeticsMenu().openMenu(e.getPlayer());
            return;
        }

        if (item.isSimilar(HubItems.ENDER_PEARLS)) {
            return;
        }

        Player player = e.getPlayer();
        Inventory inventory = player.getInventory();

        if (item.isSimilar(HubItems.MUSIC_DISABLED)) {
            player.removeMetadata("MUSIC_DISABLED", eHub.getInstance());
            eHub.getInstance().getRadioSongPlayer().addPlayer(player);
            inventory.setItem(4, HubItems.MUSIC_ENABLED);
            return;
        }

        if (item.isSimilar(HubItems.MUSIC_ENABLED)) {
            player.setMetadata("MUSIC_DISABLED", new FixedMetadataValue(eHub.getInstance(), true));
            eHub.getInstance().getRadioSongPlayer().removePlayer(player);
            inventory.setItem(4, HubItems.MUSIC_DISABLED);
            return;
        }

        if (!e.getPlayer().hasMetadata("build")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if(!event.getPlayer().hasMetadata("banned")) return;
        event.setCancelled(true);
        event.getPlayer().sendMessage(Color.translate("&cYou are not allowed to type in chat!"));
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if(!event.getPlayer().hasMetadata("banned")) return;
        if(!event.getMessage().equalsIgnoreCase("/register") && !event.getMessage().equalsIgnoreCase("/resetpassword")) {
            event.getPlayer().sendMessage(Color.translate("&cThe only command you are allowed to run is /register."));
            event.setCancelled(true);
        }
    }
}
