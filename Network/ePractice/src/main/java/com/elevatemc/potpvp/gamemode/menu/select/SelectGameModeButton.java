package com.elevatemc.potpvp.gamemode.menu.select;

import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.gamemode.GameModes;
import com.elevatemc.potpvp.gamemode.menu.extra.SelectNoDebuffOrDebuff;
import com.google.common.base.Preconditions;
import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.util.Callback;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Collections;
import java.util.List;

final class SelectGameModeButton extends Button {

    private final GameMode gameMode;
    private final Callback<GameMode> callback;

    SelectGameModeButton(GameMode gameMode, Callback<GameMode> callback) {
        this.gameMode = Preconditions.checkNotNull(gameMode, "gameMode");
        this.callback = Preconditions.checkNotNull(callback, "callback");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.DARK_AQUA + gameMode.getName();
    }

    @Override
    public List<String> getDescription(Player player) {
        return Collections.singletonList(ChatColor.GRAY + gameMode.getDescription());
    }

    @Override
    public Material getMaterial(Player player) {
        return gameMode.getIcon().getItemType();
    }

    @Override
    public byte getDamageValue(Player player) {
        return gameMode.getIcon().getData();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        if (gameMode.equals(GameModes.TEAMFIGHT)) {
            new SelectNoDebuffOrDebuff(isDebuff -> {
                if (isDebuff) {
                    callback.callback(gameMode);
                } else {
                    callback.callback(GameModes.TEAMFIGHT_DEBUFF);
                }
            }).openMenu(player);
        } else {
            callback.callback(gameMode);
        }
    }

}