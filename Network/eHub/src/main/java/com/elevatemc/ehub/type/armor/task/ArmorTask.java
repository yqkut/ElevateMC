package com.elevatemc.ehub.type.armor.task;

import com.elevatemc.ehub.eHub;
import com.elevatemc.ehub.profile.Profile;
import com.elevatemc.ehub.type.armor.ArmorType;
import com.elevatemc.ehub.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

public class ArmorTask extends BukkitRunnable {

    public ArmorTask() {
        runTaskTimerAsynchronously(eHub.getInstance(), 40L, 1L);
    }

    @Override
    public void run() {

        for (ArmorType type : ArmorType.values()) {
            if (type.getRunnable() == null && type.getArmorType() == null) {

                for (ItemStack item : type.getItems()) {
                    if (item == null) {
                        continue;
                    }

                    ItemBuilder.copyOf(item).setColor(type.getColor());
                }

                continue;
            }

            Color color = null;

            if (type.getRunnable() != null) {
                type.getRunnable().run();
                color = Color.fromRGB(type.getR(), type.getG(), type.getB());

            } else if (type.getArmorType() != null) {

                ArmorType newType = ArmorType.valueOf(type.getArmorType());
                color = Color.fromRGB(newType.getR(), newType.getG(), newType.getB());
            }

            if (color == null) {
                continue;
            }

            for (ItemStack item : type.getItems()) {
                if (item == null) {
                    continue;
                }

                ItemBuilder.copyOf(item).setColor(color);
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            Profile profile = eHub.getInstance().getProfileManager().getByUuid(player.getUniqueId());
            ArmorType type = profile.getArmorType();

            if (type == null) {
                continue;
            }

            PlayerInventory inventory = player.getInventory();

            for (ItemStack item : type.getItems()) {
                ItemBuilder builder = ItemBuilder.copyOf(item);

                if (profile.isEnchanted()) {
                    builder.addEnchantment(Enchantment.DURABILITY);
                } else {
                    builder.clearEnchantments();
                }
            }

            inventory.setHelmet(profile.isAstronaut() ?
                    new ItemBuilder(Material.STAINED_GLASS).setDurability(type.getAstronaut()).get() : profile.getArmors()[3] ? type.getItems()[3] : null);

            inventory.setChestplate(profile.getArmors()[2] ? type.getItems()[2] : null);
            inventory.setLeggings(profile.getArmors()[1] ? type.getItems()[1] : null);
            inventory.setBoots(profile.getArmors()[0] ? type.getItems()[0] : null);

            player.updateInventory();
        }
    }
}
