package dev.apposed.prime.proxy.module.profile.listener;

import dev.apposed.prime.proxy.PrimeProxy;
import dev.apposed.prime.proxy.module.bungee.BungeeHandler;
import dev.apposed.prime.proxy.module.database.redis.JedisModule;
import dev.apposed.prime.packet.StaffMessagePacket;
import dev.apposed.prime.packet.type.StaffMessageType;
import dev.apposed.prime.proxy.module.profile.Profile;
import dev.apposed.prime.proxy.module.profile.ProfileHandler;
import dev.apposed.prime.proxy.module.rank.meta.RankMeta;
import dev.apposed.prime.proxy.util.request.SimpleRequest;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.json.simple.JSONObject;

// TODO: Do profile online/offline with last server and last online in this class instead of on the spigot instances? Not sure
public class ProfileListener implements Listener {

    private final PrimeProxy plugin;

    private final ProfileHandler profileHandler;
    private final BungeeHandler bungeeHandler;
    private final JedisModule jedisModule;

    public ProfileListener(PrimeProxy plugin) {
        this.plugin = plugin;

        this.profileHandler = plugin.getModuleHandler().getModule(ProfileHandler.class);
        this.bungeeHandler = plugin.getModuleHandler().getModule(BungeeHandler.class);
        this.jedisModule = plugin.getModuleHandler().getModule(JedisModule.class);
    }

    @EventHandler
    public void onLeave(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        profileHandler.load(player.getUniqueId()).thenAccept(profile -> {
            if(profile.isStaff() && player.getServer() != null) {
                bungeeHandler.setPrevServer(player, null);
                this.jedisModule.sendPacket(new StaffMessagePacket(StaffMessageType.LEAVE, player.getUniqueId(), player.getServer().getInfo().getName(), ""));
            }
        });
    }

    @EventHandler
    public void onJoin(ServerConnectedEvent event) {
        ProxiedPlayer player = event.getPlayer();
        profileHandler.load(player.getUniqueId()).thenAccept(profile -> {
            this.profileHandler.setupPlayer(profile);
            if(profile.isStaff()) {
                if(this.bungeeHandler.getPreviousServer(player) == null) {
                    this.bungeeHandler.setPrevServer(player, event.getServer().getInfo().getName());
                    this.jedisModule.sendPacket(new StaffMessagePacket(StaffMessageType.JOIN, player.getUniqueId(), event.getServer().getInfo().getName(), ""));
                } else {
                    if(this.bungeeHandler.getPreviousServer(player) != null) {
                        String prev = this.bungeeHandler.getPreviousServer(player);
                        this.jedisModule.sendPacket(new StaffMessagePacket(StaffMessageType.SWITCH, player.getUniqueId(), prev, event.getServer().getInfo().getName()));
                    }
                }

                this.bungeeHandler.setPrevServer(player, event.getServer().getInfo().getName());
            }
        });
    }

    @EventHandler
    public void onJoinCheckVpn(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        profileHandler.load(player.getUniqueId()).thenAccept(profile -> {
            if(player.getServer() == null && !profile.hasMeta(RankMeta.VPN_BYPASS)) {
                String ip = player.getAddress().getAddress().getHostAddress();
                SimpleRequest.getRequest("http://ip-api.com/json/" + ip + "?fields=status,proxy,hosting").thenAccept(response -> {
                    if(response.get("status") == null  || response.get("proxy") == null  || response.get("hosting") == null) {
                        TextComponent component = new TextComponent("There was an issue whilst checking your provider for a VPN.");
                        component.setColor(ChatColor.RED);

                        player.disconnect(component);
                        event.setCancelled(true);
                        return;
                    }

                    if((Boolean) response.get("proxy") || (Boolean) response.get("hosting")) {
                        TextComponent component = new TextComponent("You may not join the network whilst using a VPN.");
                        component.setColor(ChatColor.RED);

                        player.disconnect(component);
                        event.setCancelled(true);
                        return;
                    }
                });
            }
        });
    }

}
