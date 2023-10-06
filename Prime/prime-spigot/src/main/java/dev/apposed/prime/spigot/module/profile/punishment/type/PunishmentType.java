package dev.apposed.prime.spigot.module.profile.punishment.type;

import dev.apposed.prime.spigot.util.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
public enum PunishmentType {
    WARN("warned", "Warns", new ItemBuilder(Material.WOOL).dur(5).build(), 17, "prime.history.warn"),
    MUTE("muted", "Mutes", new ItemBuilder(Material.WOOL).dur(4).build(), 15, "prime.history.mute"),
    GHOSTMUTE("ghost muted", "Ghost Mutes", new ItemBuilder(Material.WOOL).dur(1).build(), 13, "prime.history.ghostmute"),
    BAN("banned", "Bans", new ItemBuilder(Material.WOOL).dur(14).build(), 11, "prime.history.ban"),
    BLACKLIST("blacklisted", "Blacklists", new ItemStack(Material.BEDROCK), 9, "prime.history.blacklist");

    String display, menu;
    ItemStack menuStack;
    int slot;
    String permission;

    PunishmentType(String display, String menu, ItemStack menuStack, int slot, String permission) {
        this.display = display;
        this.menu = menu;
        this.menuStack = menuStack;
        this.slot = slot;
        this.permission = permission;
    }
}
