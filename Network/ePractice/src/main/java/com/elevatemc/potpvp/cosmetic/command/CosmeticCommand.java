package com.elevatemc.potpvp.cosmetic.command;

import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.cosmetic.Cosmetic;
import com.elevatemc.potpvp.cosmetic.CosmeticHandler;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class CosmeticCommand {

    private static final CosmeticHandler cosmeticHandler = PotPvPSI.getInstance().getCosmeticHandler();

    @Command(names = {"cosmetic list"}, permission = "op", description = "")
    public static void list(Player player) {
        player.sendMessage(cosmeticHandler.getCosmetics().stream().map(Cosmetic::getName).collect(Collectors.joining(", ")));
    }

    @Command(names = {"cosmetic equip"}, permission = "op", description = "")
    public static void equip(Player player, @Parameter(name = "cosmetic") Cosmetic cosmetic) {
        cosmeticHandler.activateCosmetic(player, cosmetic);
    }

    @Command(names = {"cosmetic unequip-all"}, permission = "op", description = "")
    public static void unequipAll(Player player) {
        cosmeticHandler.deactivateCosmetics(player);
    }

    @Command(names = {"cosmetic unequip"}, permission = "op", description = "")
    public static void unequip(Player player, @Parameter(name = "cosmetic") Cosmetic cosmetic) {
        cosmeticHandler.deactivateCosmetic(player, cosmetic);
    }
}
