package dev.apposed.prime.spigot.util;

import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ItemBuilder {

    private ItemStack itemStack;
    private ItemMeta itemMeta;
    private List<String> lore = new ArrayList<>();

    public ItemBuilder(Material material, short data) {
        itemStack = new ItemStack(material, 1, data);
        itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();

        if (itemMeta.hasLore()) {
            lore = itemMeta.getLore();
        }
    }

    public ItemBuilder(Material material) {
        itemStack = new ItemStack(material);
        itemMeta = itemStack.getItemMeta();
    }

    public static void registerGlow() {
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Glow glow = new Glow(70);
            Enchantment.registerEnchantment(glow);
        } catch (IllegalArgumentException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ItemBuilder setLeatherColor(int red, int green, int blue) {
        final LeatherArmorMeta meta = (LeatherArmorMeta) this.itemMeta;

        meta.setColor(Color.fromRGB(red, green, blue));

        return this;
    }

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        itemMeta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder name(String name) {
        itemMeta.setDisplayName(dev.apposed.prime.spigot.util.Color.translate(name));
        return this;
    }

    public ItemBuilder lore(String lore) {
        this.lore.add(dev.apposed.prime.spigot.util.Color.translate(lore));

        return this;
    }

    public ItemBuilder lore(String... lores) {
        for (String s : lores) {
            lore(s);
        }
        return this;
    }

    public ItemBuilder amount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder dur(int dur) {
        itemStack.setDurability((short) dur);

        return this;
    }

    public short dur() {
        return this.itemStack.getDurability();
    }

    public ItemStack build() {
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public ItemBuilder lore(List<String> lore) {
        List<String> toReturn = new ArrayList<>();
        for (String str : lore) {
            toReturn.add(dev.apposed.prime.spigot.util.Color.translate(str));
        }
        this.lore = toReturn;
        return this;
    }

    public ItemBuilder glow() {
        itemMeta.addEnchant(new Glow(70), 1, true);
        return this;
    }

    public ItemBuilder removeGlow() {
        itemMeta.removeEnchant(new Glow(70));
        return this;
    }


    public static class Glow extends Enchantment {

        public Glow(int id) {
            super(id);
        }

        @Override
        public boolean canEnchantItem(ItemStack arg0) {
            return false;
        }

        @Override
        public boolean conflictsWith(Enchantment arg0) {
            return false;
        }

        @Override
        public EnchantmentTarget getItemTarget() {
            return null;
        }

        @Override
        public int getMaxLevel() {
            return 0;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public int getStartLevel() {
            return 0;
        }

    }

}
