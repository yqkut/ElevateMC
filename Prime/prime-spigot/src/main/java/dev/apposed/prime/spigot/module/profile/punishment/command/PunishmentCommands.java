package dev.apposed.prime.spigot.module.profile.punishment.command;

import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.flag.Flag;
import com.elevatemc.elib.command.param.Parameter;
import dev.apposed.prime.packet.PunishmentPacket;
import dev.apposed.prime.spigot.Prime;
import dev.apposed.prime.spigot.PrimeConstants;
import dev.apposed.prime.spigot.module.ModuleHandler;
import dev.apposed.prime.spigot.module.database.redis.JedisModule;
import dev.apposed.prime.spigot.module.profile.Profile;
import dev.apposed.prime.spigot.module.profile.ProfileHandler;
import dev.apposed.prime.spigot.module.profile.punishment.Punishment;
import dev.apposed.prime.spigot.module.profile.punishment.menu.BaseHistoryMenu;
import dev.apposed.prime.spigot.module.profile.punishment.menu.UnresolvedPunishmentMenu;
import dev.apposed.prime.spigot.module.profile.punishment.type.PunishmentType;
import dev.apposed.prime.spigot.module.profile.target.ProfileTarget;
import dev.apposed.prime.spigot.module.server.ServerHandler;
import dev.apposed.prime.spigot.util.Color;
import dev.apposed.prime.spigot.util.time.DurationUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class PunishmentCommands {

    private static final Prime plugin = Prime.getInstance();
    private static final ModuleHandler moduleHandler = plugin.getModuleHandler();
    
    private static final ProfileHandler profileHandler = moduleHandler.getModule(ProfileHandler.class);
    private static final JedisModule jedisModule = moduleHandler.getModule(JedisModule.class);
    private static final ServerHandler serverHandler = moduleHandler.getModule(ServerHandler.class);

    private static final String 
            networkName = plugin.getConfig().getString("network.name"), 
            appealLink = plugin.getConfig().getString("network.appeal");

    @Command(names = {"history", "hist", "phistory", "phist", "c", "checkhistory", "ch"}, permission = "prime.command.history")
    public static void executeHistory(Player player, @Parameter(name = "player") ProfileTarget profileTarget) {
        profileTarget.resolve(profile -> {
            if (profile == null) {
                profileTarget.sendError(player);
                return;
            }

            Prime.getInstance().getServer().getScheduler().runTask(Prime.getInstance(), () -> {
                new BaseHistoryMenu(profile, new ArrayList<>(profile.getPunishments())).openMenu(player);
            });
        });
    }
    
    @Command(names = {"tempban", "tb", "tban"}, permission = "prime.command.tempban")
    public static void executeTempBan(CommandSender sender, @Flag(value = {"p", "a"}) boolean announce, @Parameter(name = "player") ProfileTarget profileTarget, @Parameter(name = "duration") String durationString, @Parameter(name = "reason", wildcard = true) String reason) {
        profileTarget.resolve(profile -> {
            if (profile == null) {
                profileTarget.sendError(sender);
                return;
            }

            long duration = DurationUtils.fromString(durationString);

            AtomicReference<UUID> addedBy = new AtomicReference<>(PrimeConstants.CONSOLE_UUID);
            if(sender instanceof Player) {
                Player player = (Player) sender;
                addedBy.set(player.getUniqueId());
            }

            Punishment punishment = new Punishment(
                    PunishmentType.BAN,
                    addedBy.get(),
                    System.currentTimeMillis(),
                    reason.replace("-p", ""),
                    duration
            );

            if(profile.getLastHashedIp() != null)
                punishment.setIp(profile.getLastHashedIp());

            profile.getPunishments().add(punishment);
            profileHandler.sendSync(profile);

            Prime.getInstance().getServer().getScheduler().runTask(Prime.getInstance(), () -> {
                Player player = Bukkit.getPlayer(profile.getUuid());
                if(player != null && player.isOnline()) {
                    if(punishment.getRemaining() == Long.MAX_VALUE) {
                        player.kickPlayer(
                                Color.translate("&cYour account has been permanently suspended from the " + networkName + ".\n\n&cAppeal on " + appealLink + ".")
                        );
                    } else {
                        player.kickPlayer(
                                Color.translate("&cYour account has been suspended from the " + networkName + " for " + punishment.formatDuration() + ".\n\n&cAppeal on " + appealLink + ".")
                        );
                    }
                }
            });

            // need to send ban message
            jedisModule.sendPacket(new PunishmentPacket(
                    profile.getUuid(),
                    addedBy.get(),
                    serverHandler.getCurrentName(),
                    punishment,
                    announce,
                    false
            ));
        });
    }

    @Command(names = {"unban"}, permission = "prime.command.unban", description = "")
    public static void executeUnban(CommandSender sender, @Flag(value = {"p", "a"}) boolean announce, @Parameter(name = "player") ProfileTarget profileTarget, @Parameter(name = "reason", wildcard = true) String reason, @Flag(value = {"c"}) boolean console) {
        profileTarget.resolve(profile -> {
            if (profile == null) {
                profileTarget.sendError(sender);
                return;
            }

            Optional<Punishment> punishmentOptional = profile.getActivePunishment(PunishmentType.BAN);

            if(punishmentOptional.isPresent()) {
                Punishment punishment = punishmentOptional.get();

                AtomicReference<UUID> removedBy = new AtomicReference<>(PrimeConstants.CONSOLE_UUID);
                if(sender instanceof Player) {
                    Player player = (Player) sender;
                    removedBy.set(player.getUniqueId());
                }

                punishment.removePunishment(
                        removedBy.get(),
                        System.currentTimeMillis(),
                        reason
                );

                profileHandler.sendSync(profile);

                if(!console) {
                    jedisModule.sendPacket(new PunishmentPacket(
                            profile.getUuid(),
                            removedBy.get(),
                            serverHandler.getCurrentName(),
                            punishment,
                            announce,
                            true
                    ));
                }
            } else {
                sender.sendMessage(Color.translate("&cThat player is not banned."));
            }
        });
    }

    @Command(names = {"unmute"}, permission = "prime.command.unmute", description = "")
    public static void executeUnmute(CommandSender sender, @Flag(value = {"p", "a"}) boolean announce, @Parameter(name = "player") ProfileTarget profileTarget, @Parameter(name = "reason", wildcard = true) String reason) {
        profileTarget.resolve(profile -> {
            if (profile == null) {
                profileTarget.sendError(sender);
                return;
            }

            Optional<Punishment> punishmentOptional = profile.getActivePunishment(PunishmentType.MUTE);
            if(punishmentOptional.isPresent()) {
                Punishment punishment = punishmentOptional.get();

                AtomicReference<UUID> removedBy = new AtomicReference<>(PrimeConstants.CONSOLE_UUID);
                if(sender instanceof Player) {
                    Player player = (Player) sender;
                    removedBy.set(player.getUniqueId());
                }

                punishment.removePunishment(
                        removedBy.get(),
                        System.currentTimeMillis(),
                        reason
                );

                profileHandler.sendSync(profile);

                jedisModule.sendPacket(new PunishmentPacket(
                        profile.getUuid(),
                        removedBy.get(),
                        serverHandler.getCurrentName(),
                        punishment,
                        announce,
                        true
                ));
            } else {
                sender.sendMessage(Color.translate("&cThat player is not muted."));
            }
        });
    }

    @Command(names = {"unresolvedpunishments", "unresolvedp", "upunishments"}, permission = "prime.command.unresolvedpunishments", description = "")
    public static void executeUnresolved(Player player) {
        final Optional<Profile> profileOptional = profileHandler.getProfile(player.getUniqueId());
        if(!profileOptional.isPresent()) return;
        final Profile profile = profileOptional.get();

        new UnresolvedPunishmentMenu(profile).openMenu(player);
    }

    @Command(names = {"unghostmute"}, permission = "prime.command.unghostmute", description = "")
    public static void executeUnGhostmute(CommandSender sender, @Flag(value = {"p", "a"}) boolean announce, @Parameter(name = "player") ProfileTarget profileTarget, @Parameter(name = "reason", wildcard = true) String reason) {
        profileTarget.resolve(profile -> {
            if (profile == null) {
                profileTarget.sendError(sender);
                return;
            }

            Optional<Punishment> punishmentOptional = profile.getActivePunishment(PunishmentType.GHOSTMUTE);
            if(punishmentOptional.isPresent()) {
                Punishment punishment = punishmentOptional.get();

                AtomicReference<UUID> removedBy = new AtomicReference<>(PrimeConstants.CONSOLE_UUID);
                if(sender instanceof Player) {
                    Player player = (Player) sender;
                    removedBy.set(player.getUniqueId());
                }

                punishment.removePunishment(
                        removedBy.get(),
                        System.currentTimeMillis(),
                        reason
                );

                profileHandler.sendSync(profile);

                jedisModule.sendPacket(new PunishmentPacket(
                        profile.getUuid(),
                        removedBy.get(),
                        serverHandler.getCurrentName(),
                        punishment,
                        announce,
                        true
                ));
            } else {
                sender.sendMessage(Color.translate("&cThat player is not ghost muted."));
            }
        });
    }

    @Command(names = {"unblacklist"}, permission = "prime.command.unblacklist", description = "")
    public static void executeUnblacklist(CommandSender sender, @Flag(value = {"p", "a"}) boolean announce, @Parameter(name = "player") ProfileTarget profileTarget, @Parameter(name = "reason", wildcard = true) String reason) {
        profileTarget.resolve(profile -> {
            if (profile == null) {
                profileTarget.sendError(sender);
                return;
            }

            Optional<Punishment> punishmentOptional = profile.getActivePunishment(PunishmentType.BLACKLIST);
            if(punishmentOptional.isPresent()) {
                Punishment punishment = punishmentOptional.get();

                AtomicReference<UUID> removedBy = new AtomicReference<>(PrimeConstants.CONSOLE_UUID);
                if(sender instanceof Player) {
                    Player player = (Player) sender;
                    removedBy.set(player.getUniqueId());
                }

                punishment.removePunishment(
                        removedBy.get(),
                        System.currentTimeMillis(),
                        reason
                );

                profileHandler.sendSync(profile);

                jedisModule.sendPacket(new PunishmentPacket(
                        profile.getUuid(),
                        removedBy.get(),
                        serverHandler.getCurrentName(),
                        punishment,
                        announce,
                        true
                ));
            } else {
                sender.sendMessage(Color.translate("&cThat player is not blacklisted."));
            }
        });
    }

    @Command(names = {"warn"}, permission = "prime.command.warn", description = "")
    public static void executeWarn(CommandSender sender, @Flag(value = {"p", "a"}) boolean announce, @Parameter(name = "player") ProfileTarget profileTarget, @Parameter(name = "reason", wildcard = true) String reason) {
        profileTarget.resolve(profile -> {
            if (profile == null) {
                profileTarget.sendError(sender);
                return;
            }

            AtomicReference<UUID> addedBy = new AtomicReference<>(PrimeConstants.CONSOLE_UUID);
            if(sender instanceof Player) {
                Player player = (Player) sender;
                addedBy.set(player.getUniqueId());
            }

            Punishment punishment = new Punishment(
                    PunishmentType.WARN,
                    addedBy.get(),
                    System.currentTimeMillis(),
                    reason.replace("-p", ""),
                    Long.MAX_VALUE
            );

            if(profile.getLastHashedIp() != null)
                punishment.setIp(profile.getLastHashedIp());

            profile.getPunishments().add(punishment);

            // need to send ban message
            jedisModule.sendPacket(new PunishmentPacket(
                    profile.getUuid(),
                    addedBy.get(),
                    serverHandler.getCurrentName(),
                    punishment,
                    announce,
                    false
            ));
            profileHandler.sendSync(profile);

            Player player = Bukkit.getPlayer(profile.getUuid());
            if(player != null && player.isOnline()) {
                player.sendMessage(Color.translate("&cYou have been warned for &f" + reason + "&c."));
            }
        });
    }

    @Command(names = {"blacklist", "bl"}, permission = "prime.command.blacklist", description = "")
    public static void executeBlacklist(CommandSender sender, @Flag(value = {"p", "a"}) boolean announce, @Parameter(name = "player") ProfileTarget profileTarget, @Parameter(name = "reason", wildcard = true) String reason) {
        profileTarget.resolve(profile -> {
            if(profile == null) {
                profileTarget.sendError(sender);
                return;
            }

            AtomicReference<UUID> addedBy = new AtomicReference<>(PrimeConstants.CONSOLE_UUID);
            if(sender instanceof Player) {
                Player player = (Player) sender;
                addedBy.set(player.getUniqueId());
            }

            Punishment punishment = new Punishment(
                    PunishmentType.BLACKLIST,
                    addedBy.get(),
                    System.currentTimeMillis(),
                    reason.replace("-p", ""),
                    Long.MAX_VALUE
            );

            if(profile.getLastHashedIp() != null)
                punishment.setIp(profile.getLastHashedIp());

            profile.getPunishments().add(punishment);

            Player player = Bukkit.getPlayer(profile.getUuid());
            if(player != null && player.isOnline()) {
                player.kickPlayer(
                        Color.translate("&cYour account has been blacklisted from the " + networkName + ".\n\n&cThis type of punishment is not appealable.")
                );
            }

            // need to send ban message

            jedisModule.sendPacket(new PunishmentPacket(
                    profile.getUuid(),
                    addedBy.get(),
                    serverHandler.getCurrentName(),
                    punishment,
                    announce,
                    false
            ));
            profileHandler.sendSync(profile);
        });
    }

    @Command(names = {"ban", "b"}, permission = "prime.command.ban", description = "")
    public static void executeBan(CommandSender sender, @Flag(value = {"p", "a"}) boolean announce, @Parameter(name = "player") ProfileTarget profileTarget, @Parameter(name = "reason", wildcard = true) String extra) {
        profileTarget.resolve(profile -> {
            if (profile == null) {
                profileTarget.sendError(sender);
                return;
            }

            final String[] args = extra.split(" ");
            long length = Long.MAX_VALUE;
            if(args.length > 1) {
                final String posLength = args[0];
                final long tempLength = DurationUtils.fromString(posLength);
                if(tempLength != -1) length = tempLength;
            }

            final StringBuilder reasonBuilder = new StringBuilder();
            for(int i=(length == Long.MAX_VALUE ? 0 : 1); i<args.length; i++) {
                reasonBuilder.append(args[i]).append(" ");
            }
            final String reason = reasonBuilder.toString();

            AtomicReference<UUID> addedBy = new AtomicReference<>(PrimeConstants.CONSOLE_UUID);
            if(sender instanceof Player) {
                Player player = (Player) sender;
                addedBy.set(player.getUniqueId());
            }

            Punishment punishment = new Punishment(
                    PunishmentType.BAN,
                    addedBy.get(),
                    System.currentTimeMillis(),
                    reason,
                    length
            );

            if(profile.getLastHashedIp() != null)
                punishment.setIp(profile.getLastHashedIp());

            profile.getPunishments().add(punishment);

            Prime.getInstance().getServer().getScheduler().runTask(Prime.getInstance(), () -> {
                Player player = Bukkit.getPlayer(profile.getUuid());
                if(player != null && player.isOnline()) {
                    if(punishment.getRemaining() == Long.MAX_VALUE) {
                        player.kickPlayer(
                                Color.translate("&cYour account has been permanently suspended from the " + networkName + ".\n\n&cAppeal on " + appealLink + ".")
                        );
                    } else {
                        player.kickPlayer(
                                Color.translate("&cYour account has been suspended from the " + networkName + " for " + punishment.formatDuration() + ".\n\n&cAppeal on " + appealLink + ".")
                        );
                    }
                }
            });

            // need to send ban message
            jedisModule.sendPacket(new PunishmentPacket(
                    profile.getUuid(),
                    addedBy.get(),
                    serverHandler.getCurrentName(),
                    punishment,
                    announce,
                    false
            ));
            profileHandler.sendSync(profile);
        });
    }

    @Command(names = {"mute", "m"}, permission = "prime.command.mute", description = "")
    public static void executeMute(CommandSender sender, @Flag(value = {"p", "a"}) boolean announce, @Parameter(name = "player") ProfileTarget profileTarget, @Parameter(name = "reason", wildcard = true) String extra) {
        profileTarget.resolve(profile -> {
            if (profile == null) {
                profileTarget.sendError(sender);
                return;
            }

            final String[] args = extra.split(" ");
            long length = Long.MAX_VALUE;
            if(args.length > 1) {
                final String posLength = args[0];
                final long tempLength = DurationUtils.fromString(posLength);
                if(tempLength != -1) length = tempLength;
            }

            final StringBuilder reasonBuilder = new StringBuilder();
            for(int i=(length == Long.MAX_VALUE ? 0 : 1); i<args.length; i++) {
                reasonBuilder.append(args[i]).append(" ");
            }
            final String reason = reasonBuilder.toString();

            AtomicReference<UUID> addedBy = new AtomicReference<>(PrimeConstants.CONSOLE_UUID);
            if(sender instanceof Player) {
                Player player = (Player) sender;
                addedBy.set(player.getUniqueId());
            }

            Punishment punishment = new Punishment(
                    PunishmentType.MUTE,
                    addedBy.get(),
                    System.currentTimeMillis(),
                    reason,
                    length
            );

            if(profile.getLastHashedIp() != null)
                punishment.setIp(profile.getLastHashedIp());

            profile.getPunishments().add(punishment);
            profileHandler.sendSync(profile);

            // need to send ban message
            jedisModule.sendPacket(new PunishmentPacket(
                    profile.getUuid(),
                    addedBy.get(),
                    serverHandler.getCurrentName(),
                    punishment,
                    announce,
                    false
            ));

            Player player = Bukkit.getPlayer(profile.getUuid());
            if(player != null && player.isOnline()) {
                player.sendMessage(Color.translate("&cYou have been muted for &f" + reason + "&c."));
            }
        });
    }

    @Command(names = {"tempmute", "tmute", "tm"}, permission = "prime.command.tempmute", description = "")
    public static void executeTempMute(CommandSender sender, @Flag(value = {"p", "a"}) boolean announce, @Parameter(name = "player") ProfileTarget profileTarget, @Parameter(name = "duration") String durationString, @Parameter(name = "reason", wildcard = true) String reason) {
        profileTarget.resolve(profile -> {
            if (profile == null) {
                profileTarget.sendError(sender);
                return;
            }

            long duration = DurationUtils.fromString(durationString);

            AtomicReference<UUID> addedBy = new AtomicReference<>(PrimeConstants.CONSOLE_UUID);
            if(sender instanceof Player) {
                Player player = (Player) sender;
                addedBy.set(player.getUniqueId());
            }

            Punishment punishment = new Punishment(
                    PunishmentType.MUTE,
                    addedBy.get(),
                    System.currentTimeMillis(),
                    reason.replace("-p", ""),
                    duration
            );

            if(profile.getLastHashedIp() != null)
                punishment.setIp(profile.getLastHashedIp());

            profile.getPunishments().add(punishment);
            profileHandler.sendSync(profile);

            // need to send ban message
            jedisModule.sendPacket(new PunishmentPacket(
                    profile.getUuid(),
                    addedBy.get(),
                    serverHandler.getCurrentName(),
                    punishment,
                    announce,
                    false
            ));

            Player player = Bukkit.getPlayer(profile.getUuid());
            if(player != null && player.isOnline()) {
                player.sendMessage(Color.translate("&cYou have been muted for &f" + reason + "&c."));
            }
        });
    }

    @Command(names = {"ghostmute"}, permission = "prime.command.ghostmute", description = "")
    public static void executeGhostMute(CommandSender sender, @Flag(value = {"p", "a"}) boolean announce, @Parameter(name = "player") ProfileTarget profileTarget, @Parameter(name = "duration") String durationString, @Parameter(name = "reason", wildcard = true) String reason) {
        profileTarget.resolve(profile -> {
            if (profile == null) {
                profileTarget.sendError(sender);
                return;
            }

            long duration = DurationUtils.fromString(durationString);

            AtomicReference<UUID> addedBy = new AtomicReference<>(PrimeConstants.CONSOLE_UUID);
            if(sender instanceof Player) {
                Player player = (Player) sender;
                addedBy.set(player.getUniqueId());
            }

            Punishment punishment = new Punishment(
                    PunishmentType.GHOSTMUTE,
                    addedBy.get(),
                    System.currentTimeMillis(),
                    reason,
                    duration
            );

            if(profile.getLastHashedIp() != null)
                punishment.setIp(profile.getLastHashedIp());

            profile.getPunishments().add(punishment);
            profileHandler.sendSync(profile);

            // need to send ban message
            jedisModule.sendPacket(new PunishmentPacket(
                    profile.getUuid(),
                    addedBy.get(),
                    serverHandler.getCurrentName(),
                    punishment,
                    announce,
                    false
            ));
        });
    }

    @Command(names = {"staffhistory"}, async = true, permission = "prime.command.staffhistory")
    public static void executeStaffHistory(Player player, @Parameter(name = "player") ProfileTarget profileTarget) {
        profileTarget.resolve(profile -> {
            if (profile == null) {
                profileTarget.sendError(player);
                return;
            }

            player.sendMessage(Color.translate("&aSearching the database for all punishments made by " + profile.getUuid() + ". This may take a few moments."));
            final List<Punishment> punishments = profileHandler.getPunishmentsByStaff(profile.getUuid());
            if(punishments == null) {
                player.sendMessage(Color.translate("&cFailed to fetch punishments."));
                return;
            }

            Prime.getInstance().getServer().getScheduler().runTask(Prime.getInstance(), () -> new BaseHistoryMenu(profile, punishments)
                    .showWhoReceived()
                    .openMenu(player));
        });
    }

    @Command(names = {"staffrollback", "staffpunishmentrollback", "punishmentrollback"}, async = true, permission = "prime.command.staffrollback", description = "")
    public static void executeStaffRollback(CommandSender sender, @Parameter(name = "player") ProfileTarget profileTarget, @Parameter(name = "duration") String durationString, @Parameter(name = "rollback reason", wildcard = true) String reason) {
        profileTarget.resolve(profile -> {
            if (profile == null) {
                profileTarget.sendError(sender);
                return;
            }

            long duration = DurationUtils.fromString(durationString);

            AtomicReference<UUID> removedBy = new AtomicReference<>(PrimeConstants.CONSOLE_UUID);
            if(sender instanceof Player) {
                Player player = (Player) sender;
                removedBy.set(player.getUniqueId());
            }

            final AtomicInteger i = new AtomicInteger(0);
            profileHandler.getPunishments(profile.getUuid()).stream().filter(punishment -> !punishment.isRemoved()).filter(Punishment::isActive).filter(grant -> (System.currentTimeMillis() - grant.getAddedAt()) <= duration).forEach(punishment -> {
                punishment.removePunishment(
                        removedBy.get(),
                        System.currentTimeMillis(),
                        reason);

                i.getAndIncrement();
            });

            sender.sendMessage(Color.translate("&aSuccessfully rolled back " + i.get() + " punishments."));
        });
    }

    @Command(names = {"kick"}, permission = "prime.command.kick")
    public static void kick(CommandSender sender, @Parameter(name = "player") Player player, @Parameter(name = "reason", defaultValue = "", wildcard = true) String reason) {
        Prime.getInstance().getServer().getScheduler().runTask(Prime.getInstance(), () -> {
            player.kickPlayer(reason);
            sender.sendMessage(Color.translate("&6Kicked &f" + player.getName() + "&6."));
        });
    }

    @Command(names = {"removeoverlaps", "removeidentityoverlaps"}, permission = "prime.command.identityoverlaps")
    public static void removeOverlaps(CommandSender sender, @Parameter(name = "remove player") ProfileTarget removeTarget, @Parameter(name = "compare player") ProfileTarget compareTarget) {
        removeTarget.resolve(removeProfile -> {
            if(removeProfile == null) {
                removeTarget.sendError(sender);
                return;
            }

            compareTarget.resolve(compareProfile -> {
                if(compareProfile == null) {
                    compareTarget.sendError(sender);
                    return;
                }

                compareProfile.getIdentities().forEach(identity -> {
                    if(removeProfile.hasIdentity(identity.getIp())) {
                        removeProfile.getIdentities().remove(identity);
                        sender.sendMessage(Color.translate("&cRemoved &e" + identity.getIp() + " &cfrom &r" + removeProfile.getColoredName()));
                    }
                });

                profileHandler.save(removeProfile);
            });
        });
    }
}
