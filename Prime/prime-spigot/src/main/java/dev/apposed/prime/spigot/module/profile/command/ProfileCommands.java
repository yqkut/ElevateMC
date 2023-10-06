package dev.apposed.prime.spigot.module.profile.command;

import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.flag.Flag;
import com.elevatemc.elib.command.param.Parameter;
import com.elevatemc.elib.util.TimeUtils;
import com.elevatemc.elib.util.UUIDUtils;
import com.google.common.collect.Maps;
import dev.apposed.prime.packet.StaffMessagePacket;
import dev.apposed.prime.packet.type.StaffMessageType;
import dev.apposed.prime.spigot.Prime;
import dev.apposed.prime.spigot.PrimeConstants;
import dev.apposed.prime.spigot.module.ModuleHandler;
import dev.apposed.prime.spigot.module.database.redis.JedisModule;
import dev.apposed.prime.spigot.module.profile.Profile;
import dev.apposed.prime.spigot.module.profile.ProfileHandler;
import dev.apposed.prime.spigot.module.profile.grant.Grant;
import dev.apposed.prime.spigot.module.profile.menu.ChatColorMenu;
import dev.apposed.prime.spigot.module.profile.punishment.type.PunishmentType;
import dev.apposed.prime.spigot.module.profile.target.ProfileTarget;
import dev.apposed.prime.spigot.module.rank.Rank;
import dev.apposed.prime.spigot.module.rank.RankHandler;
import dev.apposed.prime.spigot.module.rank.meta.RankMeta;
import dev.apposed.prime.spigot.module.server.ServerHandler;
import dev.apposed.prime.spigot.module.server.filter.ChatFilter;
import dev.apposed.prime.spigot.module.server.filter.ChatFilterHandler;
import dev.apposed.prime.spigot.module.webhook.DiscordWebhook;
import dev.apposed.prime.spigot.util.Color;
import dev.apposed.prime.spigot.util.time.DurationUtils;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ProfileCommands {

    private static final Prime plugin = Prime.getInstance();
    private static final ModuleHandler moduleHandler = plugin.getModuleHandler();

    private static final ProfileHandler profileHandler = moduleHandler.getModule(ProfileHandler.class);
    private static final RankHandler rankHandler = moduleHandler.getModule(RankHandler.class);
    private static final JedisModule jedisModule = moduleHandler.getModule(JedisModule.class);
    private static final ServerHandler serverHandler = moduleHandler.getModule(ServerHandler.class);
    private static final ChatFilterHandler filterHandler = moduleHandler.getModule(ChatFilterHandler.class);

    private static final Map<UUID, Long> lastRequest = Maps.newHashMap();
    private static final Map<UUID, UUID> lastConversation = Maps.newHashMap();

    private static final String GRANT_WEBHOOK = "https://discord.com/api/webhooks/993224084060115004/Rpd7FcojkkgLlbWJQg938NkwlarCA0Kuw_9uNfxyVEMMwBzlbaxkNnA7gdPkHKcHm6P3";
    private static final DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);

    @Command(names = {"sc", "staffchat"}, description = "Use the staff chat", permission = "prime.command.staffchat")
    public static void executeStaffChat(Player player, @Parameter(name = "message", defaultValue = "|toggle|", wildcard = true) String message) {
        if(message.equalsIgnoreCase("|toggle|")) {
            // toggle
            if(player.hasMetadata("staffchat")) {
                player.removeMetadata("staffchat", plugin);
            } else {
                player.setMetadata("staffchat", new FixedMetadataValue(plugin, true));
            }

            player.sendMessage(Color.translate("&9[SC] " + (player.hasMetadata("staffchat") ? "&aEnabled" : "&cDisabled") + "&7."));
            return;
        }

        jedisModule.sendPacket(new StaffMessagePacket(
                StaffMessageType.CHAT,
                player.getUniqueId(),
                serverHandler.getCurrentName(),
                message
        ));
    }

    @Command(names = {"ac", "adminchat"}, description = "Use the admin chat", permission = "prime.command.adminchat")
    public static void adminChat(Player player, @Parameter(name = "message", defaultValue = "|toggle|", wildcard = true) String message) {
        if(message.equalsIgnoreCase("|toggle|")) {
            // toggle
            if(player.hasMetadata("adminchat")) {
                player.removeMetadata("adminchat", plugin);
            } else {
                player.setMetadata("adminchat", new FixedMetadataValue(plugin, true));
            }

            player.sendMessage(Color.translate("&c[AC] " + (player.hasMetadata("adminchat") ? "&aEnabled" : "&cDisabled") + "&7."));
            return;
        }

        jedisModule.sendPacket(new StaffMessagePacket(
                StaffMessageType.ADMIN_CHAT,
                player.getUniqueId(),
                serverHandler.getCurrentName(),
                message
        ));
    }

    @Command(names = {"mc", "managerchat"}, description = "Use the manager chat", permission = "prime.command.managerchat")
    public static void managerChat(Player player, @Parameter(name = "message", defaultValue = "|toggle|", wildcard = true) String message) {
        if(message.equalsIgnoreCase("|toggle|")) {
            // toggle
            if(player.hasMetadata("managerchat")) {
                player.removeMetadata("managerchat", plugin);
            } else {
                player.setMetadata("managerchat", new FixedMetadataValue(plugin, true));
            }

            player.sendMessage(Color.translate("&5[MC] " + (player.hasMetadata("managerchat") ? "&aEnabled" : "&cDisabled") + "&7."));
            return;
        }

        jedisModule.sendPacket(new StaffMessagePacket(
                StaffMessageType.MANAGER_CHAT,
                player.getUniqueId(),
                serverHandler.getCurrentName(),
                message
        ));
    }

    @Command(names = {"helpop", "request"}, permission = "prime.command.helpop")
    public static void helpop(Player player, @Parameter(name = "message", wildcard = true) String message) {
        final long lastRequestTime = lastRequest.getOrDefault(player.getUniqueId(), 0L);
        if((System.currentTimeMillis() - lastRequestTime) < TimeUnit.MINUTES.toMillis(2)) {
            player.sendMessage(Color.translate("&cYou may only send a request once every two minutes."));
            return;
        }

        final StaffMessagePacket requestPacket = new StaffMessagePacket(
                StaffMessageType.HELPOP,
                player.getUniqueId(),
                "",
                serverHandler.getCurrentName()
        );

        requestPacket.setMessage(message);

        jedisModule.sendPacket(requestPacket);

        lastRequest.put(player.getUniqueId(), System.currentTimeMillis());
        player.sendMessage(Color.translate("&aYour request has been received."));
    }
    
    @Command(names = {"report"}, permission = "prime.command.report")
    public static void report(Player player, @Parameter(name = "player") Player targetPlayer, @Parameter(name = "message", wildcard = true) String message) {
        final long lastRequestTime = lastRequest.getOrDefault(player.getUniqueId(), 0L);
        if((System.currentTimeMillis() - lastRequestTime) < TimeUnit.MINUTES.toMillis(2)) {
            player.sendMessage(Color.translate("&cYou may only send a request once every two minutes."));
            return;
        }

        final StaffMessagePacket requestPacket = new StaffMessagePacket(
                StaffMessageType.REPORT,
                player.getUniqueId(),
                "",
                serverHandler.getCurrentName()
        );

        final Profile target = profileHandler.getProfile(targetPlayer.getUniqueId()).orElse(null);
        if(target == null) {
            targetPlayer.sendMessage(Color.translate("&cFailed to fetch target player's profile."));
            return;
        }

        requestPacket.setMessage(message);
        requestPacket.setPlayer(target.getUuid());

        jedisModule.sendPacket(requestPacket);

        lastRequest.put(player.getUniqueId(), System.currentTimeMillis());
        player.sendMessage(Color.translate("&aYour request has been received."));
    }
    
    @Command(names = {"list"}, permission = "prime.command.list")
    public static void executeList(CommandSender sender) {
        final List<UUID> vanishedStaff = Bukkit.getOnlinePlayers().stream().filter(player -> player.hasMetadata("modmode")).map(Player::getUniqueId).collect(Collectors.toList());
        if(sender.hasPermission("prime.staff")) {
            final String onlinePlayers = "(" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers() + ")";
            final String playerList = profileHandler.getProfiles().stream().filter(profile -> profile.getPlayer() != null).sorted((a, b) -> Integer.compare(b.getHighestActiveNonHiddenGrant().getRank().getWeight(), a.getHighestActiveNonHiddenGrant().getRank().getWeight())).map(profile -> {
                if(vanishedStaff.contains(profile.getUuid())) {
                    return ChatColor.WHITE + "*" + profile.getColoredName();
                }
                return profile.getColoredName();
            }).collect(Collectors.joining(Color.translate("&f, ")));
            sender.sendMessage(new String[] {
                    rankHandler.getCache().stream().filter(rank -> !rank.hasMeta(RankMeta.HIDDEN, true)).sorted((a, b) -> Integer.compare(b.getWeight(), a.getWeight())).map(Rank::getColoredDisplay).collect(Collectors.joining(Color.translate("&f, "))),
                    onlinePlayers + " [" + playerList + ChatColor.WHITE + "]"
            });
        } else {
            final String onlinePlayers = "(" + (Bukkit.getOnlinePlayers().size() - vanishedStaff.size()) + "/" + Bukkit.getMaxPlayers() + ")";            final String playerList = profileHandler.getProfiles().stream().filter(profile -> profile.getPlayer() != null).sorted((a, b) -> Integer.compare(b.getHighestActiveNonHiddenGrant().getRank().getWeight(), a.getHighestActiveNonHiddenGrant().getRank().getWeight())).map(profile -> {
                if(vanishedStaff.contains(profile.getUuid())) {
                    return null;
                }
                return profile.getColoredName();
            }).filter(Objects::nonNull).collect(Collectors.joining(Color.translate("&f, ")));
            sender.sendMessage(new String[] {
                    rankHandler.getCache().stream().filter(rank -> !rank.hasMeta(RankMeta.HIDDEN, true)).sorted((a, b) -> Integer.compare(b.getWeight(), a.getWeight())).map(Rank::getColoredDisplay).collect(Collectors.joining(Color.translate("&f, "))),
                    onlinePlayers + " [" + playerList + ChatColor.WHITE + "]"
            });
        }
    }
    
    @Command(names = {"tpm", "togglemessages", "toggleprivatemessages"}, permission = "prime.command.tpm")
    public static void executeTpm(Player player) {
        final Optional<Profile> profileOptional = profileHandler.getProfile(player.getUniqueId());
        if(!profileOptional.isPresent()) {
            player.sendMessage(Color.translate("&cCould not load your profile."));
            return;
        }

        profileOptional.get().setMessagesToggled(!profileOptional.get().isMessagesToggled());
        player.sendMessage(Color.translate("&eYou have toggled " + (profileOptional.get().isMessagesToggled() ? "&coff" : "&aon") + " &eprivate messages."));
    }
    
    @Command(names = {"seen"}, permission = "prime.command.seen")
    public static void executeSeen(CommandSender sender, @Parameter(name = "player") ProfileTarget profileTarget) {
        profileTarget.resolve(profile -> {
            if (profile == null) {
                profileTarget.sendError(sender);
                return;
            }

            if(profile.getLastServer() == null || profile.getLastOnline() == 0L) {
                sender.sendMessage(Color.translate("&c" + profile.getUsername() + " has never joined the server."));
                return;
            }

            final Player player = profile.getPlayer();
            if(!profile.isOnline() || (player != null && player.hasMetadata("modmode")) && !player.hasPermission("prime.staff")) {
                sender.sendMessage(Color.translate(profile.getColoredName() + " &ewas last seen " + DurationUtils.formatAgo(System.currentTimeMillis() - profile.getLastOnline()) + " &eon &7" + profile.getLastServer()));
                return;
            }

            sender.sendMessage(Color.translate(profile.getColoredName() + " &eis currently &aonline &eon &7" + profile.getLastServer()));
        });
    }
    
    @Command(names = {"message", "msg"}, permission = "prime.command.message")
    public static void executeMessage(Player player, @Parameter(name = "player") Player targetPlayer, @Parameter(name = "message", wildcard = true) String message) {
        final Optional<Profile> profileOptional = profileHandler.getProfile(player.getUniqueId());
        if(!profileOptional.isPresent()) {
            player.sendMessage(Color.translate("&cCould not load your profile."));
            return;
        }

        final Profile profile = profileOptional.get();
        if(profile.hasActivePunishment(PunishmentType.MUTE)) {
            player.sendMessage(Color.translate("&cYou cannot message players while muted."));
            return;
        }

        if(profile.isMessagesToggled()) {
            player.sendMessage(Color.translate("&cYou have messages toggled."));
            return;
        }

        final Profile target = profileHandler.getProfile(targetPlayer.getUniqueId()).orElse(null);
        if(target == null) {
            targetPlayer.sendMessage(Color.translate("&cFailed to fetch target player's profile."));
            return;
        }

        if(target.isMessagesToggled() && !player.hasPermission("prime.message.bypass")) {
            player.sendMessage(Color.translate("&c" + target.getUsername() + " has messages toggled."));
            return;
        }

        player.sendMessage(Color.translate("&7(To " + target.getColoredName() + "&7) ") + message);

        final ChatFilter filter = filterHandler.filterMessage(message);
        if(filter != null) {
            final TextComponent component = new TextComponent(Color.translate("&c&l[Filtered] "));
            component.addExtra(Color.translate("&7(" + profile.getColoredName() + " &e-> " + target.getColoredName() + "&7) &f" + message));
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder("§eThis message was hidden from " + target.getUsername() + ".\n§cFilter: " + filter.getDescription()).create()));

            Bukkit.getOnlinePlayers().stream().filter(staff -> staff.hasPermission("prime.staff")).forEach(staff -> {
                staff.spigot().sendMessage(component);
            });
            return;
        }

        target.getPlayer().sendMessage(Color.translate("&7(From " + profile.getColoredName() + "&7) ") + message);
        lastConversation.put(player.getUniqueId(), target.getUuid());
        lastConversation.put(target.getUuid(), player.getUniqueId());
    }
    
    @Command(names = {"reply", "r"}, permission = "prime.command.message")
    public static void executeReply(Player player, @Parameter(name = "message", wildcard = true) String message) {
        final Optional<Profile> profileOptional = profileHandler.getProfile(player.getUniqueId());
        if(!profileOptional.isPresent()) {
            player.sendMessage(Color.translate("&cCould not load your profile."));
            return;
        }

        final Profile profile = profileOptional.get();
        if(profile.hasActivePunishment(PunishmentType.MUTE)) {
            player.sendMessage(Color.translate("&cYou cannot message players while muted."));
            return;
        }

        if(profile.isMessagesToggled()) {
            player.sendMessage(Color.translate("&cYou have messages toggled."));
            return;
        }

        if(!lastConversation.containsKey(player.getUniqueId())) {
            player.sendMessage(Color.translate("&cYou have not messaged anybody recently."));
            return;
        }

        final Optional<Profile> targetOptional = profileHandler.getProfile(lastConversation.get(player.getUniqueId()));
        if(!targetOptional.isPresent()) {
            player.sendMessage(Color.translate("&cCould not load the target's profile."));
            return;
        }

        final Profile target = targetOptional.get();

        if(target.isMessagesToggled() && !player.hasPermission("prime.message.bypass")) {
            player.sendMessage(Color.translate("&c" + target.getUsername() + " has messages toggled."));
            return;
        }

        final Player targetPlayer = target.getPlayer();

        if (targetPlayer == null) {
            lastConversation.remove(player.getUniqueId());
            player.sendMessage(Color.translate("&cThe player is no longer online."));
            return;
        }

        player.sendMessage(Color.translate("&7(To " + target.getColoredName() + "&7) ") + message);

        final ChatFilter filter = filterHandler.filterMessage(message);
        if(filter != null) {
            final TextComponent component = new TextComponent(Color.translate("&c&l[Filtered] "));
            component.addExtra(Color.translate("&7(" + profile.getColoredName() + " &e-> " + target.getColoredName() + "&7) &f" + message));
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder("§eThis message was hidden from " + target.getUsername() + ".\n§cFilter: " + filter.getDescription()).create()));

            Bukkit.getOnlinePlayers().stream().filter(staff -> staff.hasPermission("prime.staff")).forEach(staff -> {
                staff.spigot().sendMessage(component);
            });
            return;
        }
        targetPlayer.sendMessage(Color.translate("&7(From " + profile.getColoredName() + "&7) ") + message);
        lastConversation.put(player.getUniqueId(), target.getUuid());
        lastConversation.put(target.getUuid(), player.getUniqueId());
    }

    @Command(names = {"permission add"}, permission = "prime.command.permission.add")
    public static void permissionAdd(CommandSender sender, @Parameter(name = "player")ProfileTarget profileTarget, @Parameter(name = "permission", wildcard = true) String permission) {
        profileTarget.resolve(profile -> {
            if(profile == null) {
                profileTarget.sendError(sender);
                return;
            }

            if(profile.getPermissions().contains(permission)) {
                sender.sendMessage(Color.translate(profile.getColoredName() + " &calready has &e" + permission + " &cassigned to them."));
                return;
            }

            UUID addedBy;

            if(sender instanceof Player) {
                Player player = (Player) sender;
                addedBy = player.getUniqueId();
            } else {
                addedBy = PrimeConstants.CONSOLE_UUID;
            }

            profile.getPermissions().add(permission);
            profileHandler.sendSync(profile);

            final DiscordWebhook webhook = new DiscordWebhook(GRANT_WEBHOOK);
            webhook.addEmbed(
                    new DiscordWebhook.EmbedObject()
                            .setTitle(profile.getUsername() + " has been assigned " + permission)
                            .addField("Added By", (addedBy.equals(PrimeConstants.CONSOLE_UUID) ? "Console" : UUIDUtils.name(addedBy)), false)
                            .addField("Current Server", serverHandler.getCurrentName(), false)
                            .setColor(java.awt.Color.cyan)
                            .setFooter("Prime Grants", null)
            );
            new Thread(() -> {
                try {
                    webhook.execute();
                }catch(Exception ex) {
                    ex.printStackTrace();
                }
            }).start();

            sender.sendMessage(Color.translate("&aAdded &e" + permission + " &ato " + profile.getColoredName() + "'s &aprofile."));
        });
    }

    @Command(names = {"permission remove"}, permission = "prime.command.permission.remove")
    public static void permissionRemove(CommandSender sender, @Parameter(name = "player") ProfileTarget profileTarget, @Parameter(name = "permission", wildcard = true) String permission) {
        profileTarget.resolve(profile -> {
            if(profile == null) {
                profileTarget.sendError(sender);
                return;
            }

            if(!profile.getPermissions().contains(permission)) {
                sender.sendMessage(Color.translate(profile.getColoredName() + " &cdoes not have &e" + permission + " &cassigned to them."));
                return;
            }

            UUID addedBy;

            if(sender instanceof Player) {
                Player player = (Player) sender;
                addedBy = player.getUniqueId();
            } else {
                addedBy = PrimeConstants.CONSOLE_UUID;
            }

            profile.getPermissions().remove(permission);
            profileHandler.sendSync(profile);

            final DiscordWebhook webhook = new DiscordWebhook(GRANT_WEBHOOK);
            webhook.addEmbed(
                    new DiscordWebhook.EmbedObject()
                            .setTitle(profile.getUsername() + " has been unassigned " + permission)
                            .addField("Added By", (addedBy.equals(PrimeConstants.CONSOLE_UUID) ? "Console" : UUIDUtils.name(addedBy)), false)
                            .addField("Current Server", serverHandler.getCurrentName(), false)
                            .setColor(java.awt.Color.cyan)
                            .setFooter("Prime Grants", null)
            );
            new Thread(() -> {
                try {
                    webhook.execute();
                }catch(Exception ex) {
                    ex.printStackTrace();
                }
            }).start();

            sender.sendMessage(Color.translate("&aRemoved &e" + permission + " &afrom " + profile.getColoredName() + "'s &aprofile."));
        });
    }

    @Command(names = {"permission list"}, permission = "prime.command.permission.list")
    public static void permissionList(CommandSender sender, @Parameter(name = "player") ProfileTarget profileTarget) {
        profileTarget.resolve(profile -> {
            if(profile == null) {
                profileTarget.sendError(sender);
                return;
            }

            sender.sendMessage(Color.translate(profile.getColoredName() + "'s &aIndividual Permissions:"));
            sender.sendMessage(profile.getPermissions().stream()
                    .map(permission -> Color.translate("&f- " + permission))
                    .collect(Collectors.joining("\n"))
            );
        });
    }

    @Command(names = {"gamemode c", "gamemode creative", "creative", "gamemode 1", "gmc"}, permission = "prime.command.gamemode")
    public static void executeGamemodeCreative(Player sender, @Parameter(name = "player", defaultValue = "self") Player player) {
        player.setGameMode(GameMode.CREATIVE);
        player.sendMessage(Color.translate("&6Gamemode: &fCREATIVE"));
        if(!sender.getUniqueId().equals(player.getUniqueId())) {
            sender.sendMessage(Color.translate("&6" + player.getName() + "'s Gamemode: &fCREATIVE"));
        }
    }

    @Command(names = {"gamemode s", "gamemode survival", "survival", "gamemode 0", "gms"}, permission = "prime.command.gamemode")
    public static void executeGamemodeSurvival(Player sender, @Parameter(name = "player", defaultValue = "self") Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        player.sendMessage(Color.translate("&6Gamemode: &fSURVIVAL"));
        if(!sender.getUniqueId().equals(player.getUniqueId())) {
            sender.sendMessage(Color.translate("&6" + player.getName() + "'s Gamemode: &fSURVIVAL"));
        }
    }

    @Command(names = {"fly"}, permission = "prime.command.fly")
    public static void executeFly(Player sender, @Parameter(name = "player", defaultValue = "self") Player player) {
        player.setAllowFlight(!player.getAllowFlight());
        player.sendMessage(Color.translate("&6Fly: " + (player.getAllowFlight() ? "&aEnabled" : "&cDisabled")));
        if(!sender.getUniqueId().equals(player.getUniqueId())) {
            sender.sendMessage(Color.translate("&6" + player.getName() + "'s Fly:" + (player.getAllowFlight() ? "&aEnabled" : "&cDisabled")));
        }
    }

    @Command(names = {"ss", "freeze"}, permission = "prime.command.freeze")
    public static void freeze(Player player, @Parameter(name = "player") Player target) {
        if(target.hasMetadata("frozen")) {
            target.removeMetadata("frozen", plugin);
            target.sendMessage(Color.translate("&aYou have been unfrozen."));
            jedisModule.sendPacket(new StaffMessagePacket(
                    StaffMessageType.UNFREEZE,
                    player.getUniqueId(),
                    "",
                    serverHandler.getCurrentName(),
                    "",
                    target.getUniqueId()
            ));
        } else {
            target.setMetadata("frozen", new FixedMetadataValue(plugin, true));
            target.sendMessage(Color.translate("&cYou have been frozen by a staff member."));
            jedisModule.sendPacket(new StaffMessagePacket(
                    StaffMessageType.FREEZE,
                    player.getUniqueId(),
                    "",
                    serverHandler.getCurrentName(),
                    "",
                    target.getUniqueId()
            ));
        }
    }

    @Command(names = {"invsee"}, permission = "prime.command.invsee")
    public static void invsee(Player player, @Parameter(name = "player") Player target) {
        final Profile profile = profileHandler.getProfile(target.getUniqueId()).orElse(null);
        if(profile == null) return;
        player.sendMessage(Color.translate("&eOpening " + profile.getColoredName() + "'s &einventory."));
        player.openInventory(target.getInventory());
    }

    @Command(names = {"tp", "teleport"}, permission = "prime.command.teleport")
    public static void teleport(Player player, @Parameter(name = "player") Player target, @Parameter(name = "player", defaultValue = "self") Player other) {
        if(!other.getUniqueId().equals(player.getUniqueId())) {
            // teleport target to other
            target.teleport(other);
            final Optional<Profile> targetProfOpt = profileHandler.getProfile(target.getUniqueId()),
                    otherProfOpt = profileHandler.getProfile(other.getUniqueId());
            if(!targetProfOpt.isPresent() || !otherProfOpt.isPresent()) return;
            final Profile targetProfile = targetProfOpt.get(),
                    otherProfile = otherProfOpt.get();

            player.sendMessage(Color.translate(String.format("&eTeleporting %s &eto %s&e.", targetProfile.getColoredName(), otherProfile.getColoredName())));
            return;
        }

        final Profile profile = profileHandler.getProfile(target.getUniqueId()).orElse(null);
        if(profile == null) return;
        player.sendMessage(Color.translate("&eTeleporting to " + profile.getColoredName() + "&e."));
        player.teleport(target);
    }

    @Command(names = {"tppos"}, permission = "prime.command.tppos")
    public static void tppos(Player player, @Parameter(name = "x") double x, @Parameter(name = "y") double y, @Parameter(name = "z") double z) {
        player.teleport(new Location(player.getWorld(), x, y, z));
        player.sendMessage(Color.translate("&6Teleporting..."));
    }

    @Command(names = {"tphere", "s"}, permission = "prime.command.teleport.here")
    public static void teleportHere(Player player, @Parameter(name = "player") Player target) {
        final Profile profile = profileHandler.getProfile(target.getUniqueId()).orElse(null);
        if(profile == null) return;
        player.sendMessage(Color.translate("&eTeleporting " + profile.getColoredName() + " &eto yourself."));
        target.teleport(player);
    }

    // TODO: Add flags (-a | no armor, -i | no inventory)
    @Command(names = {"clear", "ci", "clearinventory"}, permission = "prime.command.clear")
    public static void clear(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.sendMessage(Color.translate("&eCleared your inventory."));
    }

    @Command(names = {"stafftimeline", "timeline"}, permission = "prime.command.timeline")
    public static void timeline(Player self, @Parameter(name = "player", defaultValue = "self") Player player) {
        if(!self.isOp() && !(self.getUniqueId().equals(player.getUniqueId()))) {
            self.sendMessage(Color.translate("&cLol what are you trying to do buddy"));
            return;
        }
        final Optional<Profile> profileOptional = profileHandler.getProfile(player.getUniqueId());
        if(!profileOptional.isPresent()) return;
        final Profile profile = profileOptional.get();

        final List<Grant> staffGrants = profile.getGrants()
                                .stream()
                                .filter(grant -> grant.getRank().hasMeta(RankMeta.STAFF, true))
                                .sorted((a, b) -> b.getRank().getWeight() - a.getRank().getWeight())
                                .collect(Collectors.toList());

        if(staffGrants.size() != 0) {
            final Grant firstGrant = staffGrants.get(staffGrants.size()-1);
            final long firstStaffRank = firstGrant.getAddedAt();
            final long difference = System.currentTimeMillis() - firstStaffRank;
            final int differenceSec = (int) (difference/1000);

            self.sendMessage(Color.SPACER_LONG);
            staffGrants.forEach(grant -> {
                Profile added = profileHandler.getProfile(grant.getAddedBy()).orElse(null);
                String addedBy = added == null ? "Console" : added.getUsername();
                if(firstGrant.equals(grant)) {
                    self.sendMessage(Color.translate("&6" + profile.getUsername() + " &ejoined the staff team as " + grant.getRank().getColoredDisplay() + " &eon &d" + format.format(grant.getAddedAt()) + "&e."));
                } else {
                    self.sendMessage(Color.translate("&6" + profile.getUsername() + " &ewas &apromoted &eto &r" + grant.getRank().getColoredDisplay() + " &eon &d" + format.format(grant.getAddedAt()) + "&e by &6" + addedBy + "&e."));
                }
            });
            self.sendMessage(" ");

            final int differenceWeeks = (int) Math.ceil(differenceSec / 60 / 60 / 24 / 7);

            Grant lastGrant = null;
            final StringBuilder builder = new StringBuilder();
            for(int i=0; i<differenceWeeks; i++) {
                String color = "&7";
                Grant closest = getHighestStaffGrant(staffGrants, firstStaffRank+((i+1) * TimeUnit.DAYS.toMillis(7)));
                if(closest != null) {
                    if(lastGrant == null || lastGrant.getRank() != closest.getRank()) {
                        color = closest.getRank().getColor();
                    }
                }
                lastGrant = closest;

                builder.append(Color.translate(color + "█"));
            }

            self.sendMessage(Color.translate("&3Timeline:"));
            self.sendMessage(builder.toString());
            self.sendMessage(Color.translate("&6" + profile.getUsername() + " &ehas been helping the staff team for &d" + TimeUtils.formatIntoDetailedString(differenceSec) + "&e."));

        }
        self.sendMessage(Color.SPACER_LONG);
    }

    private static Grant getHighestStaffGrant(List<Grant> grants, long time) {
        Grant closest = null;
        long closetDiff = Long.MAX_VALUE;

        for(Grant grant : grants) {
            long diff = Math.abs(time - grant.getAddedAt());
            if(diff < closetDiff) {
                closest = grant;
                closetDiff = diff;
            }
        }

        return closest;
    }

    @Command(names = {"playtime", "pt"}, permission = "prime.command.playtime")
    public static void playtime(CommandSender sender, @Parameter(name = "player") ProfileTarget target) {
        target.resolve(profile -> {
            if(profile == null) {
                target.sendError(sender);
                return;
            }

            sender.sendMessage(Color.translate(profile.getColoredName() + " &ehas been playing for &d" + profile.getPlaytimeString() + "&e."));
        });
    }

    @Command(names = {"togglestaff", "togglestaffchat"}, permission = "prime.command.togglestaff")
    public static void togglestaff(Player player) {
        if(player.hasMetadata("togglestaff")) {
            player.removeMetadata("togglestaff", plugin);
            player.sendMessage(Color.translate("&aYou are now viewing staff related messages"));
            return;
        }

        player.setMetadata("togglestaff", new FixedMetadataValue(plugin, true));
        player.sendMessage(Color.translate("&cYou are no longer viewing staff related messages"));
    }

    @Command(names = {"sudo"}, permission = "op")
    public static void sudo(CommandSender sender, @Flag(value = {"f"}) boolean force, @Parameter(name = "player") Player player, @Parameter(name = "command", wildcard = true) String command) {
        sender.sendMessage(Color.translate("&6Forcing &f" + player.getName() + " &6to run &f" + command));
        boolean wasOp = player.isOp();
        if(force && !player.isOp()) player.setOp(true);
        player.performCommand(command);
        if(force && !wasOp) player.setOp(false);
    }

    @Command(names = {"masssay"}, permission = "op")
    public static void masssay(Player sender, @Parameter(name = "message", wildcard = true) String message) {
        Bukkit.getOnlinePlayers().stream().filter(Player::isOp).forEach(op -> op.sendMessage(Color.translate("&c&l" + sender.getName() + " HAS USED MASS SAY")));
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.chat(message);
        });
    }

    @Command(names = {"link", "sync", "register"}, permission = "prime.command.register")
    public static void sync(Player player) {
        final Optional<Profile> profileOptional = profileHandler.getProfile(player.getUniqueId());
        if(!profileOptional.isPresent()) return;
        final Profile profile = profileOptional.get();

        if(profile.getPassword() != null) {
            player.sendMessage(Color.translate("&cYou are already registered. If you forgot your password, use /resetpassword"));
            return;
        }

        player.sendMessage(Color.translate("&aGenerating sync code..."));
        int code = profile.getSyncCode();
        if(code == 0) {
            code = ThreadLocalRandom.current().nextInt(100000, 999999);
            profile.setSyncCode(code);
            profileHandler.save(profile);
        }
        player.sendMessage(Color.translate("&cTo finish registering your account, follow the final steps on https://elevatemc.com/register?code=" + code));
    }

    @Command(names = {"resetpassword", "forgotpassword"}, permission = "prime.command.resetpassword")
    public static void resetPassword(Player player) {
        final Optional<Profile> profileOptional = profileHandler.getProfile(player.getUniqueId());
        if(!profileOptional.isPresent()) return;
        final Profile profile = profileOptional.get();

        if(profile.getPassword() == null) {
            player.sendMessage(Color.translate("&cYou have not registered an account on the website. To do so, use /register."));
            return;
        }

        profile.setSyncCode(0);
        profile.setPassword(null);
        player.sendMessage(Color.translate("&aSuccessfully reset your password. To create your new password use /register."));
        profileHandler.save(profile);
    }

    @Command(names = {"chatcolor"}, permission = "prime.command.chatcolor")
    public static void chatcolor(Player player) {
        new ChatColorMenu().openMenu(player);
    }

    @Command(names = {"nick", "nickname", "disguise"}, permission = "prime.command.nick")
    public static void nick(Player player, @Parameter(name = "name", defaultValue = "off") String name) {
        final Optional<Profile> profileOptional = profileHandler.getProfile(player.getUniqueId());
        if(!profileOptional.isPresent()) return;
        final Profile profile = profileOptional.get();

        if(name.equalsIgnoreCase("off")) {
            profile.setNickname("");
            player.sendMessage(Color.translate("&aSuccessfully reset your nickname."));
        } else {
            profile.setNickname(name);
            player.sendMessage(Color.translate("&aSuccessfully set your nickname to " + profile.getNickname() + "."));
        }

        profileHandler.sendSync(profile);
    }

    @Command(names = {"realname", "whois"}, permission = "prime.command.realname")
    public static void realname(CommandSender sender, @Parameter(name = "nickname") String nickname) {
        final Profile profile = profileHandler.getProfileFromNickname(nickname);
        if(profile == null) {
            sender.sendMessage(Color.translate("&cThere are no players online with that nickname."));
            return;
        }

        sender.sendMessage(Color.translate(profile.getColoredName() + "'s &areal name is &e" + profile.getUsername()));
    }

    @Command(names = {"say", "bc", "broadcast"}, permission = "prime.command.say")
    public static void say(CommandSender sender, @Parameter(name = "message", wildcard = true) String message) {
        String name = "";
        if(sender instanceof Player) {
            final Player player = (Player) sender;
            final Optional<Profile> profileOptional = profileHandler.getProfile(player.getUniqueId());
            if(!profileOptional.isPresent()) return;
            final Profile profile = profileOptional.get();

            name = profile.getColoredName();
        }else {
            name = Color.translate("&4&lServer");
        }

        Bukkit.broadcastMessage(Color.translate("&d[" + name + "&d] ") + message);
    }

//    @Command(names = {"setpin"}, permission = "prime.command.setpin")
//    public static void setpin(CommandSender sender, @Parameter(name = "pin") int pin, @Parameter(name = "player", defaultValue = "self") ProfileTarget profileTarget) {
//        profileTarget.resolve(profile -> {
//            if(profile == null) {
//                profileTarget.sendError(sender);
//                return;
//            }
//
//            if(sender instanceof ConsoleCommandSender) {
//                // setting pin of other player
//                profile.setPin(pin);
//                if(profile.getPlayer() != null) profile.getPlayer().sendMessage(Color.translate("&aYour pin has been updated."));
//                sender.sendMessage(Color.translate("&aSuccessfully updated pin."));
//                profileHandler.sendSync(profile);
//                return;
//            }
//
//            if(!((Player) sender).getUniqueId().equals(profile.getUuid())) {
//                sender.sendMessage(Color.translate("&cYou cannot set another player's pin."));
//                return;
//            }
//
//            if(profile.getPin() != 0) {
//                sender.sendMessage(Color.translate("&cYour pin has already been set. If you forgot your pin, please contact the management team."));
//                return;
//            }
//
//            profile.setPin(pin);
//            sender.sendMessage(Color.translate("&aSuccessfully set your pin."));
//            profileHandler.save(profile);
//        });
//    }
//
//    @Command(names = {"resetpin"}, permission = "op")
//    public static void resetpin(CommandSender sender, @Parameter(name = "player") ProfileTarget profileTarget) {
//        profileTarget.resolve(profile -> {
//            if(profile == null) {
//                profileTarget.sendError(sender);
//                return;
//            }
//
//            if(!(sender instanceof ConsoleCommandSender)) {
//                sender.sendMessage(Color.translate("&cThis command must be executed through console."));
//                return;
//            }
//
//            profile.setPin(0);
//            sender.sendMessage(Color.translate("&aSuccessfully reset pin."));
//            profileHandler.save(profile);
//        });
//    }
//
//    @Command(names = {"forceauth"}, permission = "op")
//    public static void forceauth(CommandSender sender, @Parameter(name = "player") Player player) {
//        if(!(sender instanceof ConsoleCommandSender)) {
//            sender.sendMessage(Color.translate("&cThis command must be executed through console."));
//            return;
//        }
//
//        player.setMetadata("authed", new FixedMetadataValue(Prime.getInstance(), true));
//        sender.sendMessage(Color.translate("&aForcefully authenticated the provided player."));
//        player.sendMessage(Color.translate("&aYou have been forcefully authenticated."));
//    }
//
//    @Command(names = {"auth", "2fa", "pin"}, permission = "prime.staff")
//    public static void pin(Player player, @Parameter(name = "pin") int pin) {
//        final Optional<Profile> profileOptional = profileHandler.getProfile(player.getUniqueId());
//        if(!profileOptional.isPresent()) {
//            player.sendMessage(Color.translate("&cYour profile is not loaded."));
//            return;
//        }
//
//        if(player.hasMetadata("authed")) {
//            player.sendMessage(Color.translate("&cYou are already authenticated."));
//            return;
//        }
//
//        final Profile profile = profileOptional.get();
//
//        if(profile.getPin() == 0) {
//            player.sendMessage(Color.translate("&cYou have not yet set a pin. Use /setpin <pin> to set your pin."));
//            return;
//        }
//
//        if(pin != profile.getPin()) {
//            player.sendMessage(Color.translate("&cInvalid pin. Try again."));
//            if(player.hasMetadata("pin_failure")) {
//                // player already failed once before
//                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("ban %s Potentially Compromised Account (Failed authentication twice in a row)", player.getName()));
//                return;
//            }
//            player.setMetadata("pin_failure", new FixedMetadataValue(Prime.getInstance(), true));
//            return;
//        }
//
//        player.setMetadata("authed", new FixedMetadataValue(Prime.getInstance(), true));
//        player.removeMetadata("pin_failure", Prime.getInstance());
//        profile.setLastUsedPin(System.currentTimeMillis());
//        profileHandler.sendSync(profile);
//
//        player.sendMessage(Color.translate("&aYou have successfully authenticated."));
//    }
}
