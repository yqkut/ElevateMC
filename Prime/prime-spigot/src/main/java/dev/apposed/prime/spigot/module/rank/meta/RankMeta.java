package dev.apposed.prime.spigot.module.rank.meta;

import lombok.Getter;
import org.bukkit.Material;

@Getter
public enum RankMeta {
    DEFAULT("Default Rank", Material.WORKBENCH),
    STAFF("Staff Rank", Material.BOOK),
    SERVER("Bungee /server", Material.WOOD_DOOR ),
    DONATOR("Donator Rank", Material.EMERALD),
    PREFIX("Additional Prefix", Material.PAPER),
    VPN_BYPASS("VPN Bypass", Material.FEATHER),
    IP_BYPASS("IP Bypass", Material.IRON_BARDING),
    HIDDEN("Hidden Rank", Material.IRON_DOOR);

    private String display;
    private Material material;

    RankMeta(String display, Material material) {
        this.display = display;
        this.material = material;
    }

}
