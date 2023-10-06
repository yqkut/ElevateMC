package dev.apposed.prime.proxy.module.profile.listener;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.permission.PermissionsSetupEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.apposed.prime.proxy.PrimeProxy;
import dev.apposed.prime.proxy.module.profile.permission.DefaultPermissionProvider;
import dev.apposed.prime.proxy.module.profile.punishment.type.PunishmentType;
import dev.apposed.prime.proxy.module.velocity.VelocityHandler;
import dev.apposed.prime.proxy.module.database.redis.JedisModule;
import dev.apposed.prime.packet.StaffMessagePacket;
import dev.apposed.prime.packet.type.StaffMessageType;
import dev.apposed.prime.proxy.module.profile.Profile;
import dev.apposed.prime.proxy.module.profile.ProfileHandler;
import dev.apposed.prime.proxy.module.rank.meta.RankMeta;
import dev.apposed.prime.proxy.util.Color;
import dev.apposed.prime.proxy.util.request.SimpleRequest;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.json.simple.JSONObject;

import java.util.Optional;

public class ProfileListener {

    private final PrimeProxy plugin;

    private final ProfileHandler profileHandler;
    private final VelocityHandler velocityHandler;
    private final JedisModule jedisModule;

    public ProfileListener(PrimeProxy plugin) {
        this.plugin = plugin;

        this.profileHandler = plugin.getModuleHandler().getModule(ProfileHandler.class);
        this.velocityHandler = plugin.getModuleHandler().getModule(VelocityHandler.class);
        this.jedisModule = plugin.getModuleHandler().getModule(JedisModule.class);
    }

    @Subscribe
    public void onLogin(LoginEvent event) {
        final Player player = event.getPlayer();
        if(plugin.isInMaintenance() && !plugin.getWhitelistedUsernames().contains(player.getUsername().toLowerCase())) {
            event.setResult(ResultedEvent.ComponentResult.denied(Color.translate("&cElevate is currently in maintenance.")));
            return;
        }

        this.profileHandler.load(player.getUniqueId()).thenAccept(profile -> {
            if(profile.hasActivePunishment(PunishmentType.BAN) || profile.hasActivePunishment(PunishmentType.BLACKLIST)) {
                // redirect them to the ban server!
                final Optional<RegisteredServer> serverOptional = plugin.getServer().getServer("Purgatory");
                if(!serverOptional.isPresent()) {
                    event.setResult(ResultedEvent.ComponentResult.denied(Color.translate("&cCould not redirect you to the Purgatory Hub. Appeal on elevatemc.com.")));
                    return;
                }

                final RegisteredServer server = serverOptional.get();
                player.createConnectionRequest(server);
            }
        });
    }

    @Subscribe
    public void onLeave(DisconnectEvent event) {
        Player player = event.getPlayer();
        profileHandler.load(player.getUniqueId()).thenAccept(profile -> {
            if(profile.isStaff() && player.getCurrentServer().isPresent()) {
                velocityHandler.setPrevServer(player, null);
                this.jedisModule.sendPacket(new StaffMessagePacket(StaffMessageType.LEAVE, player.getUniqueId(), player.getCurrentServer().get().getServerInfo().getName(), ""));
            }
        });
    }

    @Subscribe
    public void onJoin(ServerConnectedEvent event) {
        Player player = event.getPlayer();
        this.profileHandler.load(player.getUniqueId()).thenAccept(profile -> {
            if(profile.isStaff()) {
                if(this.velocityHandler.getPreviousServer(player) == null) {
                    this.velocityHandler.setPrevServer(player, event.getServer().getServerInfo().getName());
                    this.jedisModule.sendPacket(new StaffMessagePacket(StaffMessageType.JOIN, player.getUniqueId(), event.getServer().getServerInfo().getName(), ""));
                } else {
                    if(this.velocityHandler.getPreviousServer(player) != null) {
                        String prev = this.velocityHandler.getPreviousServer(player);
                        this.jedisModule.sendPacket(new StaffMessagePacket(StaffMessageType.SWITCH, player.getUniqueId(), prev, event.getServer().getServerInfo().getName()));
                    }
                }

                this.velocityHandler.setPrevServer(player, event.getServer().getServerInfo().getName());
            }
        });
    }

    @Subscribe
    public void onJoinCheckVpn(ServerConnectedEvent event) {
        Player player = event.getPlayer();
        this.profileHandler.load(player.getUniqueId()).thenAccept(profile -> {
            if (!player.getCurrentServer().isPresent() && !profile.hasMeta(RankMeta.VPN_BYPASS)) {
                String ip = player.getRemoteAddress().getAddress().getHostAddress();

                SimpleRequest.getRequest("http://ip-api.com/json/" + ip + "?fields=status,proxy,hosting").thenAccept(response -> {
                    if(response.get("status") == null  || response.get("proxy") == null  || response.get("hosting") == null) {
                        player.disconnect(Component.text("There was an issue whilst checking your provider for a VPN.").color(NamedTextColor.RED));
                        return;
                    }

                    if((Boolean) response.get("proxy") || (Boolean) response.get("hosting")) {
                        player.disconnect(Component.text("There was an issue whilst checking your provider for a VPN.").color(NamedTextColor.RED));
                        return;
                    }
                });
            }
        });
    }

    @Subscribe
    public void setupPermissions(PermissionsSetupEvent event) {
        event.setProvider(DefaultPermissionProvider.INSTANCE);
    }

}
