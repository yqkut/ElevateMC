package dev.apposed.prime.packet;

import com.elevatemc.elib.util.UUIDUtils;
import dev.apposed.prime.spigot.Prime;
import dev.apposed.prime.spigot.PrimeConstants;
import dev.apposed.prime.spigot.module.database.redis.packet.Packet;
import dev.apposed.prime.spigot.module.profile.ProfileHandler;
import dev.apposed.prime.spigot.module.profile.punishment.Punishment;
import dev.apposed.prime.spigot.module.webhook.DiscordWebhook;
import dev.apposed.prime.spigot.util.Color;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

@AllArgsConstructor
public class PunishmentPacket extends Packet {

    private final String PUNISHMENT_WEBHOOK = "https://discord.com/api/webhooks/993223760213717102/uEbyMMb5VQCCQi2ewigSYKNrJj4IHQ27NaPX9WQwVT_Z98sHaCttsKRiTVMPi4kyikc1";

    private UUID player, executor;
    private String actingServer;
    private Punishment punishment;
    private boolean publicPunishment;
    private boolean undo;

    @Override
    public void onReceive() {
        final Prime plugin = JavaPlugin.getPlugin(Prime.class);
        final ProfileHandler profileHandler = plugin.getModuleHandler().getModule(ProfileHandler.class);
        String networkName = plugin.getConfig().getString("network.name");
        String appealLink = plugin.getConfig().getString("network.appeal");


        String message = Color.translate("&r%player% &ahas been&e%silent% &a" + (this.undo ? "un" : "") + punishment.getType().getDisplay() + " &aby &r%executor%&a.")
                .replace("%player%", Color.translate(player.equals(PrimeConstants.CONSOLE_UUID) ? "&4&lConsole" : profileHandler.isCached(player) ? profileHandler.getProfile(player).get().getColoredName() : UUIDUtils.name(player)))
                .replace("%silent%", Color.translate((publicPunishment ? "" : " &esilently")))
                .replace("%executor%", Color.translate(executor.equals(PrimeConstants.CONSOLE_UUID) ? "&4&lConsole" : profileHandler.isCached(player) ? profileHandler.getProfile(executor).get().getColoredName() : UUIDUtils.name(executor)));

        TextComponent component = new TextComponent(message);
        if(!publicPunishment) {
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder("§eReason: §c" + (undo ? punishment.getRemovedReason() : punishment.getAddedReason()) + "\n§eServer: §c" + actingServer + (undo ? "" : "\n§eDuration: §c" + punishment.formatDuration())).create()));
        }

        if(!publicPunishment) {
            profileHandler.getStaffProfiles().forEach(staffProfile -> {
                Player player = Bukkit.getPlayer(staffProfile.getUuid());
                if (player != null && player.isOnline()) {
                    player.spigot().sendMessage(component);
                }
            });
        } else {
            Bukkit.broadcastMessage(message);
        }

        Player player = Bukkit.getPlayer(this.player);
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
    }

    @Override
    public void onSend() {
        final Prime plugin = JavaPlugin.getPlugin(Prime.class);
        final DiscordWebhook webhook = new DiscordWebhook(PUNISHMENT_WEBHOOK);
        webhook.addEmbed(
                new DiscordWebhook.EmbedObject()
                        .setTitle(UUIDUtils.name(player) + " has been " + (this.undo ? "un" : "") + punishment.getType().getDisplay())
                        .addField("Executor", (executor.equals(PrimeConstants.CONSOLE_UUID) ? "Console" : UUIDUtils.name(executor)), false)
                        .addField("Reason", (this.undo ? punishment.getRemovedReason() : punishment.getAddedReason()), false)
                        .addField("Duration", (this.undo ? "N/A" : punishment.formatDuration()), false)
                        .setColor(java.awt.Color.cyan)
                        .setFooter("Prime Punishments", null)
        );
        new Thread(() -> {
            try {
                webhook.execute();
            }catch(Exception ex) {
                ex.printStackTrace();
            }
        }, "punishment-log-" + executor).start();
    }
}
