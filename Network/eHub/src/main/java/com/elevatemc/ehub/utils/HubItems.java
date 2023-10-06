package com.elevatemc.ehub.utils;

import com.elevatemc.elib.util.ItemUtils;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

@UtilityClass
public final class HubItems {
    public static final ItemStack SELECT_SERVER = new ItemStack(Material.WATCH);
    public static final ItemStack MUSIC_ENABLED = new ItemStack(Material.GREEN_RECORD);
    public static final ItemStack MUSIC_DISABLED = new ItemStack(Material.RECORD_3);

    public static final ItemStack COSMETICS = new ItemStack(Material.NETHER_STAR);
    public static final ItemStack ENDER_PEARLS = new ItemStack(Material.ENDER_PEARL);

    static {
        ItemUtils.setDisplayName(SELECT_SERVER,  ChatColor.DARK_AQUA + "Games " + ChatColor.GRAY + "(Right Click)" );
        ItemUtils.setLore(SELECT_SERVER, Collections.singletonList(ChatColor.GRAY + "Use this item to select the game you wish to play"));
        ItemUtils.setDisplayName(MUSIC_ENABLED,  ChatColor.DARK_AQUA + "Toggle Music: " + ChatColor.GREEN + "Enabled " + ChatColor.GRAY + "(Right Click)" );
        ItemUtils.setDisplayName(MUSIC_DISABLED,  ChatColor.DARK_AQUA + "Toggle Music: " + ChatColor.RED + "Disabled " + ChatColor.GRAY + "(Right Click)" );
        ItemUtils.setLore(MUSIC_ENABLED, Collections.singletonList(ChatColor.GRAY + "Use this item to toggle the music"));
        ItemUtils.setLore(MUSIC_DISABLED, Collections.singletonList(ChatColor.GRAY + "Use this item to toggle the music"));
        ItemUtils.setDisplayName(COSMETICS, ChatColor.DARK_AQUA + "Cosmetics " + ChatColor.GRAY + "(Right Click)");
        ItemUtils.setDisplayName(ENDER_PEARLS, ChatColor.DARK_AQUA + "Ender Pearl " + ChatColor.GRAY + "(Right Click)");
        ItemUtils.setLore(ENDER_PEARLS, Collections.singletonList(ChatColor.GRAY + "Use this item to fly away with enderpearls"));
        ENDER_PEARLS.setAmount(64);
    }
}