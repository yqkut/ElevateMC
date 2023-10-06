package dev.apposed.prime.packet;

import dev.apposed.prime.spigot.Prime;
import dev.apposed.prime.spigot.module.database.redis.packet.Packet;
import dev.apposed.prime.spigot.module.profile.Profile;
import dev.apposed.prime.spigot.module.profile.ProfileHandler;
import dev.apposed.prime.packet.type.StaffMessageType;
import dev.apposed.prime.spigot.util.Color;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor @RequiredArgsConstructor @Data
public class StaffMessagePacket extends Packet {

    private final StaffMessageType type;
    private final UUID uuid;
    private final String prevServer, server;

    private String message;
    private UUID player;

    @Override
    public void onReceive() {
        final Prime plugin = JavaPlugin.getPlugin(Prime.class);
        final ProfileHandler profileHandler = plugin.getModuleHandler().getModule(ProfileHandler.class);
        profileHandler.load(uuid).thenAccept(profile -> {
            switch(type) {
                case JOIN: {
                    String message = Color.translate(plugin.getConfig().getString("staff.join"))
                            .replace("%player%", profile.getColoredName())
                            .replace("%prev_server%", this.prevServer);

                    profileHandler.getStaffProfiles().forEach(staffProfile -> {
                        Player player = Bukkit.getPlayer(staffProfile.getUuid());
                        if(player != null && player.isOnline() && !player.hasMetadata("togglestaff")) {
                            player.sendMessage(message);
                        }
                    });
                    break;
                }
                case LEAVE: {
                    String message = Color.translate(plugin.getConfig().getString("staff.leave"))
                            .replace("%player%", profile.getColoredName())
                            .replace("%prev_server%", this.prevServer);

                    profileHandler.getStaffProfiles().forEach(staffProfile -> {
                        Player player = Bukkit.getPlayer(staffProfile.getUuid());
                        if(player != null && player.isOnline() && !player.hasMetadata("togglestaff")) {
                            player.sendMessage(message);
                        }
                    });
                    break;
                }
                case SWITCH: {
                    String message = Color.translate(plugin.getConfig().getString("staff.switch"))
                            .replace("%player%", profile.getColoredName())
                            .replace("%prev_server%", this.prevServer)
                            .replace("%server%", this.server);

                    profileHandler.getStaffProfiles().forEach(staffProfile -> {
                        Player player = Bukkit.getPlayer(staffProfile.getUuid());
                        if(player != null && player.isOnline() && !player.hasMetadata("togglestaff")) {
                            player.sendMessage(message);
                        }
                    });
                    break;
                }
                case CHAT: {
                    String message = Color.translate(plugin.getConfig().getString("staff.chat"))
                            .replace("%player%", profile.getColoredName())
                            .replace("%prefix%", profile.getHighestActiveNonHiddenGrant().getRank().getPrefix())
                            .replace("%server%", this.prevServer)
                            .replace("%message%", this.server);

                    profileHandler.getStaffProfiles().forEach(staffProfile -> {
                        Player player = Bukkit.getPlayer(staffProfile.getUuid());
                        if(player != null && player.isOnline() && !player.hasMetadata("togglestaff")) {
                            player.sendMessage(message);
                        }
                    });
                    break;
                }
                case ADMIN_CHAT: {
                    String message = Color.translate(plugin.getConfig().getString("staff.admin"))
                            .replace("%player%", profile.getColoredName())
                            .replace("%prefix%", profile.getHighestActiveNonHiddenGrant().getRank().getPrefix())
                            .replace("%server%", this.prevServer)
                            .replace("%message%", this.server);

                    profileHandler.getStaffProfiles().forEach(staffProfile -> {
                        Player player = Bukkit.getPlayer(staffProfile.getUuid());
                        if(player != null && player.isOnline() && !player.hasMetadata("togglestaff") && player.hasPermission("prime.command.adminchat")) {
                            player.sendMessage(message);
                        }
                    });
                    break;
                }
                case MANAGER_CHAT: {
                    String message = Color.translate(plugin.getConfig().getString("staff.manager"))
                            .replace("%player%", profile.getColoredName())
                            .replace("%prefix%", profile.getHighestActiveNonHiddenGrant().getRank().getPrefix())
                            .replace("%server%", this.prevServer)
                            .replace("%message%", this.server);

                    profileHandler.getStaffProfiles().forEach(staffProfile -> {
                        Player player = Bukkit.getPlayer(staffProfile.getUuid());
                        if(player != null && player.isOnline() && !player.hasMetadata("togglestaff") && player.hasPermission("prime.command.managerchat")) {
                            player.sendMessage(message);
                        }
                    });
                    break;
                }
                case HELPOP: {
                    final String message = Color.translate(String.format("&9[Request]&b[%s&b] &r%s &7has requested assistance: &f%s",
                            getServer(),
                            profile.getColoredName(),
                            getMessage()));

                    profileHandler.getStaffProfiles().forEach(staffProfile -> {
                        Player player = Bukkit.getPlayer(staffProfile.getUuid());
                        if(player != null && player.isOnline() && !player.hasMetadata("togglestaff")) {
                            player.sendMessage(message);
                        }
                    });
                    break;
                }
                case REPORT: {
                    profileHandler.load(player).thenAccept(target -> {
                        final String message = Color.translate(String.format("&9[Report]&b[%s&b] &r%s &7has reported &r%s&7: &f%s",
                                getServer(),
                                profile.getColoredName(),
                                target.getColoredName(),
                                getMessage()));

                        profileHandler.getStaffProfiles().forEach(staffProfile -> {
                            Player player = Bukkit.getPlayer(staffProfile.getUuid());
                            if(player != null && player.isOnline() && !player.hasMetadata("togglestaff")) {
                                player.sendMessage(message);
                            }
                        });
                    }).exceptionally(throwable -> {
                        Bukkit.getConsoleSender().sendMessage(Color.translate("&cStaffMessagePacket, failed to fetch target profile with uuid " + player.toString()));
                        return null;
                    });
                    break;
                }
                case FREEZE: {
                    profileHandler.load(player).thenAccept(target -> {
                        final String message = Color.translate(String.format("&b[S] &7(%s&7) &r%s &7has frozen &r%s&7.",
                                getServer(),
                                profile.getColoredName(),
                                target.getColoredName()));

                        profileHandler.getStaffProfiles().forEach(staffProfile -> {
                            Player player = Bukkit.getPlayer(staffProfile.getUuid());
                            if(player != null && player.isOnline() && !player.hasMetadata("togglestaff")) {
                                player.sendMessage(message);
                            }
                        });
                    }).exceptionally(throwable -> {
                        Bukkit.getConsoleSender().sendMessage(Color.translate("&cStaffMessagePacket, failed to fetch target profile with uuid " + player.toString()));
                        return null;
                    });
                    break;
                }
                case UNFREEZE: {
                    profileHandler.load(player).thenAccept(target -> {
                        final String message = Color.translate(String.format("&b[S] &7(%s&7) &r%s &7has unfrozen &r%s&7.",
                                getServer(),
                                profile.getColoredName(),
                                target.getColoredName()));

                        profileHandler.getStaffProfiles().forEach(staffProfile -> {
                            Player player = Bukkit.getPlayer(staffProfile.getUuid());
                            if(player != null && player.isOnline() && !player.hasMetadata("togglestaff")) {
                                player.sendMessage(message);
                            }
                        });
                    }).exceptionally(throwable -> {
                        Bukkit.getConsoleSender().sendMessage(Color.translate("&cStaffMessagePacket, failed to fetch target profile with uuid " + player.toString()));
                        return null;
                    });

                    break;
                }
            }
        }).exceptionally(throwable -> {
            Bukkit.getConsoleSender().sendMessage(Color.translate("&cStaffMessagePacket, failed to load profile with uuid " + uuid.toString()));
            return null;
        });
    }

    @Override
    public void onSend() {

    }
}
