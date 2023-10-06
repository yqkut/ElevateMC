package com.elevatemc.ehub.utils;

import dev.apposed.prime.spigot.util.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;


//Thank you qLib
public class ItemBuilder {
    private final ItemStack itemStack;

    public static ItemBuilder copyOf(ItemBuilder builder) {
        return new ItemBuilder(builder.get());
    }

    public static ItemBuilder copyOf(ItemStack item) {
        return new ItemBuilder(item);
    }

    public ItemBuilder(Material material) {
        itemStack = new ItemStack(material);
    }

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemBuilder setAmount(int amount) {
        itemStack.setAmount(Math.min(amount, 64));
        return this;
    }

    public ItemBuilder setName(String name) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(Color.translate(name));
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addLoreLine(String name) {
        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = meta.getLore();

        if (lore == null) {
            lore = new ArrayList<>();
        }

        lore.add(Color.translate(name));
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        List<String> toSet = new ArrayList<>();
        ItemMeta meta = itemStack.getItemMeta();

        lore.forEach((string) -> toSet.add(Color.translate(string)));

        meta.setLore(toSet);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setDurability(int durability) {
        itemStack.setDurability((short)durability);
        return this;
    }

    public ItemBuilder setData(int data) {
        itemStack.setData(new MaterialData(itemStack.getType(), (byte)data));
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        itemStack.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment) {
        itemStack.addUnsafeEnchantment(enchantment, 1);
        return this;
    }

    public ItemBuilder setType(Material material) {
        itemStack.setType(material);
        return this;
    }

    public ItemBuilder clearLore() {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setLore(new ArrayList<>());

        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder clearEnchantments() {
        itemStack.getEnchantments().keySet().forEach(itemStack::removeEnchantment);
        return this;
    }

    public ItemBuilder setColor(org.bukkit.Color color) {
        if (itemStack.getType() != Material.LEATHER_BOOTS
                && itemStack.getType() != Material.LEATHER_CHESTPLATE
                && itemStack.getType() != Material.LEATHER_HELMET
                && itemStack.getType() != Material.LEATHER_LEGGINGS) {

            throw new IllegalArgumentException("color() only applicable for leather armor.");
        } else {
            LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
            meta.setColor(color);

            itemStack.setItemMeta(meta);
            return this;
        }
    }

    public ItemBuilder setOwner(String owner) {
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        meta.setOwner(owner);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemStack get() {
        return itemStack;
    }

    public static void rename(ItemStack stack, String name) {
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(Color.translate(name));
        stack.setItemMeta(meta);
    }

    public static ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Color.translate(name));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItem(Material material, String name, int amount) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Color.translate(name));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItem(Material material, String name, int amount, int damage) {
        ItemStack item = new ItemStack(material, amount, (short) damage);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Color.translate(name));
        item.setItemMeta(meta);
        return item;
    }
}
