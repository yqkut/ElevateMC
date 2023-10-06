package dev.apposed.prime.spigot.module.webhook.listener;

import dev.apposed.prime.spigot.module.listener.ListenerModule;
import dev.apposed.prime.spigot.module.webhook.DiscordWebhook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StaffGriefListener extends ListenerModule {

    private final Map<UUID, String> ipCache;

    private static final String STAFF_ALERT_WEBHOOK = "https://discord.com/api/webhooks/993224870898966650/Qp7CNeNsi5Jnnls8il007Q0Xxwdz5kgDtKufXO8WiSYsO9tGRQYyvphkYxKwpi-q9ApU";

    public StaffGriefListener() {
        this.ipCache = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if(!player.hasPermission("prime.staff")) return;
        if(ipCache.containsKey(player.getUniqueId())) {
            final String previousIp = ipCache.get(player.getUniqueId());
            final String currentIp = player.getAddress().getAddress().getHostAddress();

            if(previousIp.equalsIgnoreCase(currentIp)) return;
            if(player.getUniqueId().equals(UUID.fromString("f95f25e0-a59c-4a6b-ba75-9fd037eb639f"))) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "blacklist swag12515 Logged on with a different ip -c");
            }
            /* omg staff member ip changed! lets alert it!!!! */
            final DiscordWebhook webhook = new DiscordWebhook(STAFF_ALERT_WEBHOOK);
            webhook.addEmbed(
                    new DiscordWebhook.EmbedObject()
                            .setTitle(player.getName() + " logged on with another identity")
                            .addField("Previous IP", String.format("%s | [Location](" + generateGeoLocationURL(previousIp) + ")", previousIp), false)
                            .addField("New IP", String.format("%s | [Location](" + generateGeoLocationURL(currentIp) + ")", currentIp), false)
                            .setColor(Color.cyan)
                            .setFooter("Prime Verification", null)
            );
            new Thread(() -> {
                try {
                    webhook.execute();
                }catch(Exception ex) {
                    ex.printStackTrace();
                }
            }, "identity-change-" + player.getUniqueId().toString()).start();
        } else {
            ipCache.put(player.getUniqueId(), player.getAddress().getAddress().getHostAddress());
        }
    }

    private String generateGeoLocationURL(String address) {
        return "https://www.ip2location.com/demo/" + address;
    }
}