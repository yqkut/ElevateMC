package com.elevatemc.potpvp.kit.menu.kits;

import com.elevatemc.potpvp.gamemode.kit.GameModeKit;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.kit.Kit;
import com.elevatemc.potpvp.kit.KitHandler;
import com.elevatemc.potpvp.kit.menu.editkit.EditKitMenu;
import com.elevatemc.elib.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

final class KitEditButton extends Button {

    private final Optional<Kit> kitOpt;
    private final GameModeKit gameModeKit;
    private final int slot;

    KitEditButton(Optional<Kit> kitOpt, GameModeKit gameModeKit, int slot) {
        this.kitOpt = Preconditions.checkNotNull(kitOpt, "kitOpt");
        this.gameModeKit = Preconditions.checkNotNull(gameModeKit, "gameMode");
        this.slot = slot;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.DARK_AQUA + (kitOpt.isPresent() ? kitOpt.get().getName() : "Free slot");
    }

    @Override
    public List<String> getDescription(Player player) {
        return kitOpt.isPresent() ?
                ImmutableList.of(
                        ChatColor.DARK_AQUA + "┃ " + ChatColor.WHITE + "Edit this kit " + ChatColor.GRAY + "(Click)",
                        ChatColor.DARK_AQUA + "┃ " + ChatColor.WHITE + "Delete this kit " + ChatColor.GRAY + "(Drop)"
                ) :
                ImmutableList.of(
                        ChatColor.DARK_AQUA + "┃ " + ChatColor.WHITE + "Click this to create a new kit"
                );
    }

    @Override
    public Material getMaterial(Player player) {
        return kitOpt.isPresent() ? Material.ENCHANTED_BOOK : Material.INK_SACK;
    }

    @Override
    public byte getDamageValue(Player player) {
        return kitOpt.isPresent() ? 0 : DyeColor.GRAY.getDyeData();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        if (clickType.equals(ClickType.DROP) && kitOpt.isPresent()) {
            KitHandler kitHandler = PotPvPSI.getInstance().getKitHandler();
            kitHandler.removeKit(player, gameModeKit, this.slot);

            new KitsMenu(gameModeKit).openMenu(player);
        } else {
            Kit resolvedKit = kitOpt.orElseGet(() -> {
                KitHandler kitHandler = PotPvPSI.getInstance().getKitHandler();
                return kitHandler.saveDefaultKit(player, gameModeKit, this.slot);
            });

            new EditKitMenu(resolvedKit).openMenu(player);
        }
    }

}