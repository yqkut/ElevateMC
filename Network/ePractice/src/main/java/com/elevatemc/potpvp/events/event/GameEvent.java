package com.elevatemc.potpvp.events.event;

import com.elevatemc.potpvp.events.game.Game;
import com.elevatemc.potpvp.events.parameter.GameParameter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface GameEvent {

    String getName();
    String getPermission();
    String getDescription();
    ItemStack getIcon();
    boolean canStart(Game game);
    GameEventLogic getLogic(Game game);
    String getNameTag(Game game, Player player, Player viewer);
    List<String> getScoreboardScores(Player player, Game game);
    List<Listener> getListeners();
    List<GameParameter> getParameters();
    List<ItemStack> getLobbyItems();
    default int getMaxInstances() {
        return 1;
    }

}
