package com.elevatemc.potpvp.ability.packet;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.ability.type.Invisibility;
import com.elevatemc.spigot.handler.PacketHandler;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author xanderume@gmail.com
 */
public class InvisibilityPacketAdapter implements PacketHandler {

    @Override
    public boolean handleSentPacketCancellable(PlayerConnection connection, Packet<?> packet) {
        if (packet instanceof PacketPlayOutEntityEquipment) {
            PacketPlayOutEntityEquipment packetPlayOutHeldItemSlot = (PacketPlayOutEntityEquipment) packet;
            final int id = packetPlayOutHeldItemSlot.getId();
            final ItemStack itemStack = CraftItemStack.asBukkitCopy(packetPlayOutHeldItemSlot.getItem());

            if (itemStack == null || itemStack.getType() == Material.AIR) {
                return true;
            }

            if (!(packetPlayOutHeldItemSlot.getItem().getItem() instanceof ItemArmor)) {
                return true;
            }

            Player player = PotPvPSI.getInstance().getServer().getOnlinePlayers().stream().filter(it -> it.getEntityId() == id).findFirst().orElse(null);
            if (player != null) {
                if (player.getActivePotionEffects().stream().anyMatch(effect -> effect.getType().equals(Invisibility.EFFECT.getType()) && effect.getAmplifier() == Invisibility.EFFECT.getAmplifier())) {
                    return false;
                }
            }
        }
        return true;
    }

//    @Override
//    public void onPacketSending(PacketEvent event) {
//
//        final Integer id = event.getPacket().getIntegers().read(0);
//        final ItemStack itemStack = event.getPacket().getItemModifier().read(0);
//
//        if (itemStack == null || itemStack.getType() == Material.AIR) {
//            return;
//        }
//
//        if (!(CraftItemStack.asNMSCopy(itemStack).getItem() instanceof ItemArmor)) {
//            return;
//        }
//
//        PotPvPSI.getInstance().getServer().getOnlinePlayers().stream().filter(it -> it.getEntityId() == id).findFirst().ifPresent(it -> {
//
//            if (it.getActivePotionEffects().stream().noneMatch(effect -> effect.getType().equals(Invisibility.EFFECT.getType()) && effect.getAmplifier() == Invisibility.EFFECT.getAmplifier())) {
//                return;
//            }
//
//            event.setCancelled(true);
//        });
//
//    }

}
