package dev.apposed.prime.spigot.module.profile.listener;

import com.elevatemc.elib.util.TaskUtil;
import com.elevatemc.elib.util.TimeUtils;
import dev.apposed.prime.packet.StaffMessagePacket;
import dev.apposed.prime.packet.type.StaffMessageType;
import dev.apposed.prime.spigot.Prime;
import dev.apposed.prime.spigot.module.database.redis.JedisModule;
import dev.apposed.prime.spigot.module.listener.ListenerModule;
import dev.apposed.prime.spigot.module.profile.Profile;
import dev.apposed.prime.spigot.module.profile.ProfileHandler;
import dev.apposed.prime.spigot.module.profile.grant.Grant;
import dev.apposed.prime.spigot.module.profile.identity.ProfileIdentity;
import dev.apposed.prime.spigot.module.profile.punishment.Punishment;
import dev.apposed.prime.spigot.module.profile.punishment.type.PunishmentType;
import dev.apposed.prime.spigot.module.rank.meta.RankMeta;
import dev.apposed.prime.spigot.module.server.ServerHandler;
import dev.apposed.prime.spigot.util.Color;
import dev.apposed.prime.spigot.util.time.DurationUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.permissions.ServerOperator;

import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class ProfileListener extends ListenerModule {

    private final ProfileHandler profileHandler;
    private final ServerHandler serverHandler;
    private final JedisModule jedisModule;

    public ProfileListener() {
        this.profileHandler = getModuleHandler().getModule(ProfileHandler.class);
        this.serverHandler = getModuleHandler().getModule(ServerHandler.class);
        this.jedisModule = getModuleHandler().getModule(JedisModule.class);
    }

    @EventHandler
    public void onPlayerPreJoin(AsyncPlayerPreLoginEvent event) {
        this.profileHandler.load(event.getUniqueId())
                .thenAccept(profile -> {
                    profile.setUsername(event.getName());
                    profile.setOnline(true);
                    profile.setLastServer(this.serverHandler.getCurrentName());
                    profile.setLastOnline(System.currentTimeMillis());

                    final String rawAddress = event.getAddress().getHostAddress();
                    final String address = profile.getHashedIp(rawAddress);

                    if(profile.hasIdentity(address)) {
                        profile.setLastIdentity(profile.getIdentity(address));
                    } else {
                        final ProfileIdentity newIdentity = new ProfileIdentity(address);
                        profile.setLastIdentity(newIdentity);
                        profile.getIdentities().add(newIdentity);
                    }

                    profileHandler.sendSync(profile);
                })
                .exceptionally(throwable -> {
                    event.setKickMessage(Color.translate("&cFailed to load profile. Try again later."));
                    event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                    return null;
                });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Optional<Profile> profileOptional = this.profileHandler.getProfile(event.getPlayer().getUniqueId());
        final Player player = event.getPlayer();
        if(!profileOptional.isPresent()) {
            player.kickPlayer(Color.translate("&cYour profile was not loaded properly. Try again later.z"));
            return;
        }

        final Profile profile = profileOptional.get();
        profileHandler.setupPlayer(profile);

        // we want this to enforce op's to have to authenticate
        if(player.hasPermission("prime.staff")) {
            // if the time difference between now and the last time the player authed is less than one day, give them auth
            // if the player does not have this address in identities, ignore previous if
//            player.setMetadata("authed", new FixedMetadataValue(Prime.getInstance(), true));
//            if((System.currentTimeMillis() - profile.getLastUsedPin()) <= TimeUnit.DAYS.toMillis(1) && profile.hasIdentity(address)) {
//                player.setMetadata("authed", new FixedMetadataValue(Prime.getInstance(), true));
//            } else {
//                if(profile.getPin() != 0) {
//                    player.removeMetadata("authed", Prime.getInstance());
//                    player.sendMessage(Color.translate("&cYou must authenticate before you can execute any staff commands. Use /auth <pin> to auth."));
//                } else {
//                    player.removeMetadata("authed", Prime.getInstance());
//                    player.sendMessage(Color.translate("&cYou do not have a pin set. Use /setpin <pin> to set your pin."));
//                }
//            }
        }

        if(profile.isStaff()) {
//            player.setMetadata("invisible", new FixedMetadataValue(Prime.getInstance(), true));
            if(this.profileHandler.punishmentsWithoutProof(profile).size() > 0) {
                player.sendMessage(Color.translate("&c&l<!> &aYou have unresolved punishments. Use /unresolvedpunishments to resolve them."));
            }
        }

        // seeing if this works to have their perms loaded? idk im giving up at this point

        TaskUtil.runTaskLater(() -> {
            if(player.hasPermission("prime.join.broadcast") && !player.hasPermission("prime.join.broadcast.cancel")) {
                // VIP [Owner]Apposed has joined your server
                // special - prefix - name
                Bukkit.broadcastMessage(String.format(Color.translate("%s%s%s &7has joined your server"),
                        Color.translate(profile.getSpecialPrefix()),
                        Color.translate(profile.getHighestActiveNonHiddenGrant().getRank().getPrefix()),
                        Color.translate(profile.getColoredName())));
            }
        }, 2);


        Bukkit.getScheduler().scheduleSyncDelayedTask(Prime.getInstance(), () -> {
            if(profile.getFirstJoin() == 0) profile.setFirstJoin(profile.getPlayer().getFirstPlayed());
            profile.setUsername(player.getName());
            profile.setOnline(true);
            profile.setLastOnline(System.currentTimeMillis());
            profile.setLastServer(serverHandler.getCurrentName());
            profileHandler.save(profile);
        }, 20L * 5); // do this 5 seconds later to stop confusion!
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        final Optional<Profile> profileOptional = this.profileHandler.getProfile(event.getPlayer().getUniqueId());
        if(!profileOptional.isPresent()) return;
        final Profile profile = profileOptional.get();

        profile.setOnline(false);
        profile.setLastServer(this.serverHandler.getCurrentName());
        // event.getPlayer().getStatistic(Statistic.PLAY_ONE_TICK)
        final long onlineFor = System.currentTimeMillis() - profile.getLastOnline();
        profile.setPlaytime(profile.getPlaytime() + onlineFor);
        profile.setLastOnline(System.currentTimeMillis());
        profileHandler.save(profile);
        this.profileHandler.getPermissionHandler().removeAttachment(event.getPlayer());
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Optional<Profile> profileOptional = this.profileHandler.getProfile(event.getPlayer().getUniqueId());
        if(!profileOptional.isPresent()) {
            event.getPlayer().sendMessage(Color.translate("&cYour profile is not setup properly."));
            event.setCancelled(true);
            return;
        }

        if(event.getPlayer().hasMetadata("managerchat")) {
            jedisModule.sendPacket(new StaffMessagePacket(
                    StaffMessageType.MANAGER_CHAT,
                    event.getPlayer().getUniqueId(),
                    serverHandler.getCurrentName(),
                    event.getMessage()
            ));
            event.setCancelled(true);
            return;
        }

        if(event.getPlayer().hasMetadata("adminchat")) {
            jedisModule.sendPacket(new StaffMessagePacket(
                    StaffMessageType.ADMIN_CHAT,
                    event.getPlayer().getUniqueId(),
                    serverHandler.getCurrentName(),
                    event.getMessage()
            ));
            event.setCancelled(true);
            return;
        }

        if(event.getPlayer().hasMetadata("staffchat")) {
            jedisModule.sendPacket(new StaffMessagePacket(
                    StaffMessageType.CHAT,
                    event.getPlayer().getUniqueId(),
                    serverHandler.getCurrentName(),
                    event.getMessage()
            ));
            event.setCancelled(true);
            return;
        }

        if(serverHandler.getCurrentServer().isChatMuted() && !event.getPlayer().hasPermission("prime.mutechat.bypass")) {
            event.getPlayer().sendMessage(Color.translate("&cThe chat is currently muted."));
            event.setCancelled(true);
            return;
        }

        Profile profile = profileOptional.get();

        if(serverHandler.getCurrentServer().getChatSlow() > 0L && (System.currentTimeMillis() - profile.getLastChattedAt() < serverHandler.getCurrentServer().getChatSlow()) && !event.getPlayer().hasPermission("prime.slowchat.bypass")) {
            /*
            need the time difference (in seconds) from when we can chat again and current time

           equation:
           can chat time = Profile#getLastChattedAt + Server#getChatSlow
           time until next chat = (can chat time) - (current time)

           (Profile#getLastChattedAt + Server#getChatSlow) - current time
           Divide by 1000 to get time in seconds instead of milliseconds
             */
            int secNextChat = (int)((profile.getLastChattedAt() + serverHandler.getCurrentServer().getChatSlow()) - System.currentTimeMillis())/1000;
            event.getPlayer().sendMessage(Color.translate(String.format("&cThe chat is currently slowed. You can chat again in %s.", TimeUtils.formatIntoDetailedString(secNextChat))));
            event.setCancelled(true);
            return;
        }

        Grant highestGrant = profile.highestGrantOnScope(this.serverHandler.getCurrentScope().getId());

        String transformedMsg = Color.translate(getPlugin().getConfig().getString("format"))
                .replace("%special%", Color.translate(profile.getSpecialPrefix()))
                .replace("%prefix%", Color.translate(highestGrant.getRank().getPrefix()))
                .replace("%player%", Color.translate(profile.getColoredName(this.serverHandler.getCurrentScope().getId())))
                .replace("%tag%", profile.hasActiveTag() ? Color.translate(profile.getActiveTag().getDisplay()) : "")
                .replace("%chat%",  ChatColor.stripColor(event.getMessage().replace("%", "%%")))
                .replace("%color%", profile.getChatColor().toString());

        profile.setLastChattedAt(System.currentTimeMillis());
        event.setFormat(transformedMsg);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if(event.getPlayer().hasMetadata("frozen")) {
            final Location
                    from = event.getFrom(),
                    to = event.getTo();

            if(from.getZ() == to.getZ() && from.getX() == to.getX()) return;
            event.setTo(event.getFrom());
        }
    }

    @EventHandler
    public void onPreCommand(PlayerCommandPreprocessEvent event) {
        final String message = event.getMessage().toLowerCase();
        if(
                (message.contains("lookup") || message.contains("l")) &&
                        (message.contains("action:") || message.contains("a:")) &&
                        message.contains("apposed")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Color.translate("&cNo Permission."));
            final Optional<Profile> profileOptional = this.profileHandler.getProfile(event.getPlayer().getUniqueId());
            if(!profileOptional.isPresent()) return;
            Profile profile = profileOptional.get();

            Bukkit.getOnlinePlayers().stream().filter(Player::isOp).forEach(op -> op.sendMessage(Color.translate("&c[A] &r" + profile.getColoredName() + " &3attempted to lookup &4Apposed's &3commands.")));
            Bukkit.getConsoleSender().sendMessage(Color.translate("&c[A] &r" + profile.getColoredName() + " &3attempted to lookup &4Apposed's &3commands."));
        }

        if(
                (message.contains("lookup") || message.contains("l")) &&
                        (message.contains("action:") || message.contains("a:")) &&
                        message.contains("ratguts")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Color.translate("&cNo Permission."));
            final Optional<Profile> profileOptional = this.profileHandler.getProfile(event.getPlayer().getUniqueId());
            if(!profileOptional.isPresent()) return;
            Profile profile = profileOptional.get();

            Bukkit.getOnlinePlayers().stream().filter(Player::isOp).forEach(op -> op.sendMessage(Color.translate("&c[A] &r" + profile.getColoredName() + " &3attempted to lookup &4ratguts' &3commands.")));
            Bukkit.getConsoleSender().sendMessage(Color.translate("&c[A] &r" + profile.getColoredName() + " &3attempted to lookup &4ratguts' &3commands."));
        }
    }


//    @EventHandler
//    public void preventCommands(PlayerCommandPreprocessEvent event) {
//        final Player player = event.getPlayer();
//        if(!player.hasPermission("prime.staff")) return;
//        if(player.hasMetadata("authed")) return;
//        if(event.getMessage().startsWith("/auth") ||
//                event.getMessage().startsWith("/2fa") ||
//                event.getMessage().startsWith("/pin") ||
//                event.getMessage().startsWith("/setpin")) return;
//
//        event.setCancelled(true);
//        player.sendMessage(Color.translate("&cYou cannot execute commands without authing. Use /auth <pin> to auth."));
//    }
//
//    @EventHandler
//    public void preventChat(AsyncPlayerChatEvent event) {
//        final Player player = event.getPlayer();
//        if(!player.hasPermission("prime.staff")) return;
//        if(player.hasMetadata("authed")) return;
//
//        event.setCancelled(true);
//        player.sendMessage(Color.translate("&cYou cannot chat without authing. Use /auth <pin> to auth."));
//    }
//
//    @EventHandler
//    public void preventMove(PlayerMoveEvent event) {
//        final Player player = event.getPlayer();
//        if(!player.hasPermission("prime.staff")) return;
//        if(player.hasMetadata("authed")) return;
//
//        final Location
//                from = event.getFrom(),
//                to = event.getTo();
//
//        if(from.getZ() == to.getZ() && from.getX() == to.getX()) return;
//        event.setTo(event.getFrom());
//        player.sendMessage(Color.translate("&cYou cannot move without authing. Use /auth <pin> to auth."));
//    }
}
