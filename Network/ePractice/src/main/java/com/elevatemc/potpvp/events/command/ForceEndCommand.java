package com.elevatemc.potpvp.events.command;

import com.elevatemc.elib.command.Command;
import com.elevatemc.potpvp.events.game.Game;
import com.elevatemc.potpvp.events.game.GameQueue;
import org.bukkit.entity.Player;

public class ForceEndCommand {

    @Command(names = { "event forceend", "forceend"}, permission = "op")
    public static void host(Player sender) {
        Game game = GameQueue.INSTANCE.getCurrentGame(sender);

        if (game == null) {
            sender.sendMessage("You're not in a game");
            return;
        }

        game.end();
        game.sendMessage("&cThe game has been forcefully ended.");
    }

}
