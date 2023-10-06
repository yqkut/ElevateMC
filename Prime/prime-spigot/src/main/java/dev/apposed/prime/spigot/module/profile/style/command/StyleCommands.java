package dev.apposed.prime.spigot.module.profile.style.command;

import com.elevatemc.elib.command.Command;
import dev.apposed.prime.spigot.module.profile.style.menu.StyleMenu;
import org.bukkit.entity.Player;

public class StyleCommands {

    @Command(names = {"style"}, permission = "prime.command.style")
    public static void open(Player player) {
        new StyleMenu().openMenu(player);
    }
}
