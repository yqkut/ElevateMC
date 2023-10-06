package com.elevatemc.potpvp.match.listener;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.deathmessage.DeathMessageHandler;
import com.elevatemc.potpvp.deathmessage.objects.Damage;
import com.elevatemc.potpvp.deathmessage.util.UnknownDamage;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchHandler;
import com.elevatemc.potpvp.nametag.PotPvPNametagProvider;
import com.elevatemc.potpvp.setting.SettingHandler;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityWeather;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;

public final class MatchDeathMessageListener implements Listener {

    private static final String NO_KILLER_MESSAGE = ChatColor.translateAlternateColorCodes('&', "%s&7 died.");
    private static final String KILLED_BY_OTHER_MESSAGE = ChatColor.translateAlternateColorCodes('&', "%s&7 killed %s&7.");

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player killed = event.getEntity();
        Player killer = killed.getKiller();

        SettingHandler settingHandler = PotPvPSI.getInstance().getSettingHandler();
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying(killed);

        if (match == null) {
            return;
        }

        PacketPlayOutSpawnEntityWeather lightningPacket = createLightningPacket(killed.getLocation());

        float thunderSoundPitch = 0.8F + PotPvPSI.RANDOM.nextFloat() * 0.2F;
        float explodeSoundPitch = 0.5F + PotPvPSI.RANDOM.nextFloat() * 0.2F;

        List<Damage> record = PotPvPSI.getInstance().getDeathMessageHandler().getDamage(killed);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            UUID onlinePlayerUuid = onlinePlayer.getUniqueId();

            // if this player has no relation to the match skip
            if (match.getTeam(onlinePlayerUuid) == null && !match.isSpectator(onlinePlayerUuid)) {
                continue;
            }

            // if the killer died before the player did we just pretend they weren't
            // involved (their name would show up as a spectator, which would be confusing
            // for players)
            if (killer == null || match.isSpectator(killer.getUniqueId())) {
                UnknownDamage dmg = new UnknownDamage(killed.getName(), 1.0D);
                onlinePlayer.sendMessage(dmg.getDeathMessage(onlinePlayer));
            } else {
                if (record != null && record.size() > 0) {
                    onlinePlayer.sendMessage(record.get(record.size() - 1).getDeathMessage(onlinePlayer));
                } else {
                    UnknownDamage dmg = new UnknownDamage(killed.getName(), 1.0D);
                    onlinePlayer.sendMessage(dmg.getDeathMessage(onlinePlayer));
                }
            }

            onlinePlayer.playSound(killed.getLocation(), Sound.AMBIENCE_THUNDER, 10000F, thunderSoundPitch);
            onlinePlayer.playSound(killed.getLocation(), Sound.EXPLODE, 2.0F, explodeSoundPitch);

            sendLightningPacket(onlinePlayer, lightningPacket);
        }

        PotPvPSI.getInstance().getDeathMessageHandler().clearDamage(killed);
    }

    private PacketPlayOutSpawnEntityWeather createLightningPacket(Location location) {
        PacketPlayOutSpawnEntityWeather packet = new PacketPlayOutSpawnEntityWeather();
        packet.setA(128); // entity id of 128
        packet.setB(1); // type of lightning (1)
        packet.setC((int) (location.getX() * 32.0D)); // x
        packet.setD((int) (location.getY() * 32.0D)); // y
        packet.setE((int) (location.getZ() * 32.0D)); // z

        return packet;
    }

    private void sendLightningPacket(Player target, PacketPlayOutSpawnEntityWeather packet) {
        PlayerConnection playerConnection = ((CraftPlayer) target).getHandle().playerConnection;
        playerConnection.sendPacket(packet);
    }

}