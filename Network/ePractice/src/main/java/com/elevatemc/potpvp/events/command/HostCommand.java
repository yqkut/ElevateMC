package com.elevatemc.potpvp.events.command;

import com.elevatemc.elib.command.Command;
import com.elevatemc.potpvp.events.game.Game;
import com.elevatemc.potpvp.events.game.GameQueue;
import com.elevatemc.potpvp.events.game.GameState;
import com.elevatemc.potpvp.events.menu.HostMenu;
import com.elevatemc.potpvp.util.Color;
import org.bukkit.entity.Player;

public class HostCommand {

    @Command(names = { "host"}, permission = "potpvp.host")
    public static void host(Player sender) {
        new HostMenu().openMenu(sender);
    }

    @Command(names = {"forcestart"}, permission = "op")
    public static void forcestart(Player player) {
        final Game game = GameQueue.INSTANCE.getCurrentGame(player);
        if(game == null) {
            player.sendMessage(Color.translate("&cYou are not in an event."));
            return;
        }

        if(game.getState() != GameState.STARTING) {
            player.sendMessage(Color.translate("&cThis event cannot be force started"));
            return;
        }

        game.setStartingAt(System.currentTimeMillis());
        player.sendMessage(Color.translate("&aSuccessfully updated the events start time to now."));
    }
}
