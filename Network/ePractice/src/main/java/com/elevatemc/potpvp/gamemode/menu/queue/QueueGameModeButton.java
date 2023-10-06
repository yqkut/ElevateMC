package com.elevatemc.potpvp.gamemode.menu.queue;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.gamemode.GameModes;
import com.elevatemc.potpvp.gamemode.menu.extra.SelectNoDebuffOrDebuff;
import com.elevatemc.potpvp.match.MatchHandler;
import com.elevatemc.potpvp.queue.QueueHandler;
import com.google.common.base.Preconditions;
import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.util.Callback;
import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Arrays;
import java.util.List;

public final class QueueGameModeButton extends Button {

    private final GameMode gameMode;
    private final Callback<GameMode> callback;
    private final boolean competitive;

    public QueueGameModeButton(GameMode gameMode, boolean competitive, Callback<GameMode> callback) {
        this.gameMode = Preconditions.checkNotNull(gameMode, "gameMode");
        this.competitive = Preconditions.checkNotNull(competitive, "competitive");
        this.callback = Preconditions.checkNotNull(callback, "callback");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.DARK_AQUA + gameMode.getName();
    }

    @Override
    public List<String> getDescription(Player player) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        QueueHandler queueHandler = PotPvPSI.getInstance().getQueueHandler();
        int inFightsRanked = matchHandler.countPlayersPlayingMatches(m -> m.getGameMode() == gameMode && m.isRanked());
        int inQueueRanked = queueHandler.countPlayersQueued(gameMode, true);

        int inFightsUnranked = matchHandler.countPlayersPlayingMatches(m -> m.getGameMode() == gameMode && !m.isRanked());
        int inQueueUnranked = queueHandler.countPlayersQueued(gameMode, false);

        return ImmutableList.of(
                ChatColor.DARK_AQUA + "┃ " + ChatColor.WHITE + "In Fights: " + ChatColor.DARK_AQUA + (competitive ? inFightsRanked : inFightsUnranked),
                ChatColor.DARK_AQUA + "┃ " + ChatColor.WHITE + "In Queue: " + ChatColor.DARK_AQUA + (competitive ? inQueueRanked : inQueueUnranked),
                "",
                ChatColor.AQUA + "⇨ " + ChatColor.WHITE + "Click this to join the " + gameMode.getName() + " queue");

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
                    callback.callback(gameMode);
                }
            }).openMenu(player);
        } else {
            callback.callback(gameMode);
        }
    }
}