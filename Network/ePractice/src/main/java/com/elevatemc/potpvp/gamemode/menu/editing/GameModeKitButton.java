package com.elevatemc.potpvp.gamemode.menu.editing;

import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.util.Callback;
import com.elevatemc.potpvp.gamemode.kit.GameModeKit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Collections;
import java.util.List;

public class GameModeKitButton extends Button {
    private final GameModeKit kit;
    private final Callback<GameModeKit> callback;

    public GameModeKitButton(GameModeKit kit, Callback<GameModeKit> callback) {
        this.kit = kit;
        this.callback = callback;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.AQUA + kit.getDisplayName();
    }

    @Override
    public List<String> getDescription(Player paramPlayer) {
        return Collections.singletonList(ChatColor.GRAY + "Click here to edit this kit");
    }

    @Override
    public Material getMaterial(Player player) {
        return kit.getIcon().getItemType();
    }

    @Override
    public byte getDamageValue(Player player) {
        return kit.getIcon().getData();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        callback.callback(kit);
    }
}