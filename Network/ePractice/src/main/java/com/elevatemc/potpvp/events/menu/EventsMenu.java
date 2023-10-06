package com.elevatemc.potpvp.events.menu;

import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.Menu;
import com.elevatemc.potpvp.events.game.Game;
import com.elevatemc.potpvp.events.game.GameQueue;
import com.elevatemc.potpvp.events.game.GameState;
import com.elevatemc.potpvp.util.Color;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EventsMenu extends Menu {

    public EventsMenu() {
        setAutoUpdate(true);
    }

    @Override
    public String getTitle(Player player) {
        return Color.translate("&3Join an event");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = Maps.newHashMap();

        for (Game game : GameQueue.INSTANCE.getCurrentGames()) {
            buttons.put(buttons.size(), new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.AQUA + game.getEvent().getName() + " Event";
                }

                @Override
                public List<String> getDescription(Player player) {

                    List<String> lines = new ArrayList<>();

                    for (String line : Arrays.asList(
                            "&7&m-------------------------",
                            "&bPlayers&7: &f" + game.getPlayers().size() + (game.getMaxPlayers() == -1 ? "" : "&7/" + game.getMaxPlayers()),
                            "&bState&7: &f" + StringUtils.capitalize(game.getState().name().toLowerCase()),
                            "&bHosted By&7: &f" + game.getHost().getDisplayName(),
                            " ",
                            (game.getState() == GameState.STARTING ? "&aClick here to join." : "&7Click here to spectate."),
                            "&7&m-------------------------")) {
                        lines.add(ChatColor.translateAlternateColorCodes('&', line));
                    }

                    return lines;
                }

                @Override
                public Material getMaterial(Player player) {
                    return game.getEvent().getIcon().getType();
                }

                @Override
                public byte getDamageValue(Player player) {
                    return (byte) game.getEvent().getIcon().getDurability();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    player.closeInventory();
                    if (game.getState() == GameState.STARTING) {
                        if (game.getMaxPlayers() > 0 && game.getPlayers().size() >= game.getMaxPlayers()) {
                            player.sendMessage(ChatColor.RED + "This event is currently full! Sorry!");
                            return;
                        }
                        game.add(player);
                    } else {
                        game.addSpectator(player);
                    }
                }

            });
        }

        if (buttons.isEmpty()) {
            player.closeInventory();
        }

        return buttons;
    }
}
