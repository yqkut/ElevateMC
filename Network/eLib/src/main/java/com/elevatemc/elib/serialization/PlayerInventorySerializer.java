package com.elevatemc.elib.serialization;

import com.elevatemc.elib.eLib;
import lombok.AllArgsConstructor;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class PlayerInventorySerializer {

    public static String serialize(Player player) {
        return eLib.PLAIN_GSON.toJson(new PlayerInventoryWrapper(player));
    }
    public static String serialize(PlayerInventoryWrapper wrapper) {
        return eLib.PLAIN_GSON.toJson(wrapper);
    }

    public static PlayerInventoryWrapper deserialize(String json) {
        return eLib.PLAIN_GSON.fromJson(json, PlayerInventoryWrapper.class);
    }

    public static BasicDBObject getInsertableObject(Player player) {
        return (BasicDBObject)JSON.parse(serialize(player));
    }

    @AllArgsConstructor
    public static class PlayerInventoryWrapper {

        @Getter private final PotionEffect[] effects;
        @Getter private final ItemStack[] contents;
        @Getter private final ItemStack[] armor;
        @Getter private final int health;
        @Getter private final int hunger;

        public PlayerInventoryWrapper(Player player) {
            this.contents = player.getInventory().getContents();

            int i;
            ItemStack stack;

            for(i = 0; i < this.contents.length; ++i) {

                stack = this.contents[i];

                if (stack == null) {
                    this.contents[i] = new ItemStack(Material.AIR, 0, (short)0);
                }

            }

            this.armor = player.getInventory().getArmorContents();

            for(i = 0; i < this.armor.length; ++i) {

                stack = this.armor[i];

                if (stack == null) {
                    this.armor[i] = new ItemStack(Material.AIR, 0, (short)0);
                }

            }

            this.effects = player.getActivePotionEffects().toArray(new PotionEffect[player.getActivePotionEffects().size()]);
            this.health = (int)player.getHealth();
            this.hunger = player.getFoodLevel();
        }

        public void apply(Player player) {

            player.getInventory().setContents(this.contents);
            player.getInventory().setArmorContents(this.armor);
            player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));

            for (int i = 0; i < this.effects.length; i++) {

                final PotionEffect potionEffect = this.effects[i];

                player.addPotionEffect(potionEffect);
            }
        }
    }
}
